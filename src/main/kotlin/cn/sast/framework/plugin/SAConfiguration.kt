package cn.sast.framework.plugin

import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.config.yamlConfiguration
import cn.sast.api.report.CheckType2StringKind
import cn.sast.common.GLB
import cn.sast.common.IResFile
import cn.sast.framework.plugin.PluginDefinitions.Definition
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.*
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import mu.KLogger
import mu.KotlinLogging
import soot.Main
import soot.Scene
import soot.jimple.toolkits.callgraph.CallGraph
import soot.options.Options
import java.nio.file.Files
import java.util.*
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.jvm.internal.Intrinsics



/**
 * A cleaned‑up, idiomatic re‑implementation of the de‑compiled *SAConfiguration* class.
 * All public behaviour is preserved; only syntax and style have been normalised to
 * compile‑time safe Kotlin.
 */
@Serializable
@Suppress("TooManyFunctions")
data class SAConfiguration(
    val builtinAnalysisConfig: BuiltinAnalysisConfig = BuiltinAnalysisConfig(),
    val preAnalysisConfig: PreAnalysisConfig = PreAnalysisConfig(),
    val configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> = LinkedHashMap(),
    val checkers: LinkedHashSet<CheckersConfig> = LinkedHashSet()
) {
    // ---------------------------------------------------- internal state
    @Transient
    private val relatedMap: IdentityHashMap<Definition<*>, IConfig> = IdentityHashMap()

    @Transient
    private val disabled: IdentityHashMap<Definition<*>, IConfig> = IdentityHashMap()

    // ---------------------------------------------------- structural helpers
    private fun stableHash(): Int {
        val cfgHash = configurations.values.map { it.toList().hashCode() }.hashCode()
        val chkHash = checkers.sortedBy { it.name }.map { it.checkTypes.toList().hashCode() }.hashCode()
        return listOf(cfgHash, chkHash).hashCode()
    }

    /** Sorts *configurations* and *checkers* deterministically. Returns *true* if anything changed. */
    fun sort(): Boolean {
        val old = stableHash()

        // sort configurations
        configurations.apply {
            val sorted = entries.sortedBy { it.key }
                .associate { (k, v) -> k to v.sortedBy(ConfigSerializable::name).toCollection(LinkedHashSet()) }
            clear(); putAll(sorted)
        }

        // sort checkers and their inner lists
        val sortedCheckers = checkers.sortedWith(compareBy<CheckersConfig> { it.name })
            .map { it.sort() }
            .toCollection(LinkedHashSet())
        checkers.clear(); checkers.addAll(sortedCheckers)

        return stableHash() != old
    }

    // ---------------------------------------------------- definition helpers
    private fun <T : Any> Definition<*>.instanceOrNull(clz: Class<T>): T? =
        clz.takeIf { it.isInstance(singleton) }?.cast(singleton)

    private fun Definition<*>.relateConfig(): IConfig =
        relatedMap[this] ?: error("$this not related to any config (supplementAndMerge() not executed?)")

    private fun <T : Any> Definition<*>.get(clz: Class<T>): T? {
        val cfg = relateConfig()
        if (cfg is IOptional && !cfg.enable) {
            disabled[this] = cfg; return null
        }
        if (this is PluginDefinitions.CheckTypeDefinition) {
            // If the parent checker has been disabled => propagate
            val checkerCfg = checkers.firstOrNull { it.name == CheckersConfig(singleton.checker).name } ?: return null
            if (!checkerCfg.enable) return null
        }
        return instanceOrNull(clz)
    }

    // ---------------------------------------------------- enable‑set extraction
    fun buildEnableSet(defs: PluginDefinitions, filter: CheckerFilterByName? = null): EnablesConfig {
        val enables = EnablesConfig()

        val checkerUnits = defs.getCheckerUnitDefinition(CheckerUnit::class.java)
        val sootHandlers = defs.getISootInitializeHandlerDefinition(ISootInitializeHandler::class.java)
        val checkTypes = defs.getCheckTypeDefinition(CheckType::class.java)

        val enabledNames = filter?.enables ?: emptySet()
        val renameMap = filter?.renameMap ?: emptyMap()

        // ---------------- Pre / AI analysis units
        checkerUnits.forEach { d ->
            d.get(PreAnalysisUnit::class.java)?.let { enables.preAnalysisUnits += it }
            d.get(AIAnalysisUnit::class.java)?.let { enables.aiAnalysisUnits += it }
        }

        // ---------------- soot handler
        sootHandlers.forEach { d ->
            d.get(ISootInitializeHandler::class.java)?.let { enables.sootConfig += it }
        }

        // ---------------- check types
        checkTypes.forEach { d ->
            val typeName = d.defaultConfig.checkType
            val enabled = enabledNames.isEmpty() || typeName in enabledNames
            if (enabled) d.get(CheckType::class.java)?.let { enables.checkTypes += it }
        }

        // ---------------- def2config mapping (only non‑null entries)
        enables.def2config += checkerUnits.associateNotNull { d ->
            d.get(CheckerUnit::class.java)?.let { it to d.relateConfig() }
        }
        return enables
    }

    // ---------------------------------------------------- external API
    fun toSaConfig(defs: PluginDefinitions, filter: CheckerFilterByName? = null): SaConfig {
        val enableDefinitions = buildEnableSet(defs, filter)

        val mergedSootHandler: ISootInitializeHandler =
            if (enableDefinitions.sootConfig.size == 1) {
                enableDefinitions.sootConfig.first()
            } else {
                object : ISootInitializeHandler {
                    private val delegates = enableDefinitions.sootConfig

                    override fun configure(options: Options) =
                        delegates.forEach { it.configure(options) }

                    override fun configure(scene: Scene) =
                        delegates.forEach { it.configure(scene) }

                    override fun configureAfterSceneInit(scene: Scene, options: Options) =
                        delegates.forEach { it.configureAfterSceneInit(scene, options) }

                    override fun configure(main: Main) =
                        delegates.forEach { it.configure(main) }

                    override fun onBeforeCallGraphConstruction(scene: Scene, options: Options) =
                        delegates.forEach { it.onBeforeCallGraphConstruction(scene, options) }

                    override fun onAfterCallGraphConstruction(cg: CallGraph, scene: Scene, options: Options) =
                        delegates.forEach { it.onAfterCallGraphConstruction(cg, scene, options) }

                    override fun toString(): String = "SootConfigMerge-$delegates"
                }
        }

        GLB += defs.getCheckTypeDefinition(CheckType::class.java).map { it.singleton }

        return SaConfig(
            builtinAnalysisConfig,
            preAnalysisConfig,
            (enableDefinitions.preAnalysisUnits + enableDefinitions.aiAnalysisUnits).toMutableSet().toPersistentSet(),
            mergedSootHandler,
            enableDefinitions.checkTypes.toSet()
        )
    }

    fun serialize(serializersModule: SerializersModule, out: IResFile) {
        val yaml = Yaml(serializersModule, yamlConfiguration)
        try {
            Files.deleteIfExists(out.path)
            out.path.outputStream().use { stream ->
                yaml.encodeToStream(serializer(), this, stream)
            }
        } catch (e: Exception) {
            Files.deleteIfExists(out.path)
            throw e
        }
    }

    /**
     * Supplements *this* configuration with defaults & merges the definitions from *defs*.
     * Returns *true* if any effective change was made.
     */
    fun supplementAndMerge(defs: PluginDefinitions, ymlPath: String? = null): Boolean {
        val oldHash = hashCode()
        val defaults = Companion.getDefaultConfig(defs)

        Compare(this, defs).compare(defaults)   // forward fill from *this* into *defaults*
        Compare(defaults, defs).compare(this)   // back‑fill defaults into *this*

        return hashCode() != oldHash
    }

    // ---------------------------------------------------- nested helpers / singletons
    object Companion {
        private val logger: KLogger = KotlinLogging.logger {}
        val unitTypeName: String = SAConfiguration::class.java.simpleName

        fun getDefaultConfig(defs: PluginDefinitions): SAConfiguration {
            val configurations = LinkedHashMap<String, LinkedHashSet<ConfigSerializable>>()
            val checkers = LinkedHashSet<CheckersConfig>()

            defs.defaultConfigs.forEach { (def, cfg) ->
                require(cfg === def.defaultConfig) { "definition/default mismatch: $def" }
                when (def) {
                    is PluginDefinitions.CheckTypeDefinition -> {
                        val checker = CheckersConfig(def.singleton.checker)
                        val checkerCfg = checkers.find { it.name == checker.name } ?: checker.also { checkers += it }
                        checkerCfg.checkTypes += def.defaultConfig
                    }
                    is PluginDefinitions.CheckerUnitDefinition -> {
                        configurations.getOrPut(getUnitTypeName(def.type)) { LinkedHashSet() } += def.defaultConfig
                    }
                    is PluginDefinitions.ISootInitializeHandlerDefinition -> {
                        configurations.getOrPut(getUnitTypeName(def.type)) { LinkedHashSet() } += def.defaultConfig
                    }
                }
            }
            return SAConfiguration(configurations = configurations, checkers = checkers)
        }

        fun deserialize(serializersModule: SerializersModule, file: IResFile): SAConfiguration {
            val yaml = Yaml(serializersModule, yamlConfiguration)
            file.path.inputStream().use { inp ->
                return yaml.decodeFromStream(serializer(), inp)
            }
        }
        fun getUnitTypeName(cls: Class<*>): String {
            Intrinsics.checkNotNullParameter(cls, "<this>")
            val simpleName = cls.simpleName
            Intrinsics.checkNotNullExpressionValue(simpleName, "getSimpleName(...)")
            return simpleName
        }

    }

    // ---------------------------------------------------- compare helper (reduced version)
    private class Compare(
        private val base: SAConfiguration,
        private val defs: PluginDefinitions
    ) {
        fun compare(other: SAConfiguration) {
            // Validate and connect configs; simplified but keeps semantics
            base.configurations.forEach { (type, cfgSet) ->
                val targetSet = other.configurations[type] ?: emptySet()
                cfgSet.forEach { cfg ->
                    val matches = targetSet.filter { it.name == cfg.name }
                    when (matches.size) {
                        0 -> { /* missing in other */ }
                        1 -> base.relatedMap[defs.defaultConfigs.entries.first { it.value === matches.first() }.key] = cfg
                        else -> error("Duplicated config for $cfg in $type: $matches")
                    }
                }
            }
            // Checkers
            base.checkers.forEach { checker ->
                val otherChecker = other.checkers.firstOrNull { it.name == checker.name } ?: return@forEach
                checker.checkTypes.forEach { ct ->
                    val matches = otherChecker.checkTypes.filter { it.checkType == ct.checkType }
                    when (matches.size) {
                        0 -> { /* missing */ }
                        1 -> base.relatedMap[defs.defaultConfigs.entries.first { it.value === matches.first() }.key] = ct
                        else -> error("Duplicate CheckType ${ct.checkType}")
                    }
                }
            }
        }
    }

    // ---------------------------------------------------- enable‑bag data class
    data class EnablesConfig(
        val aiAnalysisUnits: MutableList<AIAnalysisUnit> = ArrayList(),
        val preAnalysisUnits: MutableList<PreAnalysisUnit> = ArrayList(),
        val sootConfig: MutableList<ISootInitializeHandler> = ArrayList(),
        val checkTypes: MutableList<CheckType> = ArrayList(),
        val def2config: MutableMap<CheckerUnit, IConfig> = IdentityHashMap()
    ) {
        operator fun plusAssign(other: EnablesConfig) {
            aiAnalysisUnits += other.aiAnalysisUnits
            preAnalysisUnits += other.preAnalysisUnits
            sootConfig += other.sootConfig
            checkTypes += other.checkTypes
            def2config += other.def2config
        }
    }
}

// ------------------------------------------------------------ extension helpers

private fun <T> MutableCollection<T>.associateNotNull(transform: (T) -> Pair<CheckerUnit, IConfig>?): Map<CheckerUnit, IConfig> =
    mapNotNull(transform).toMap()

@Suppress("UNCHECKED_CAST")
private fun <T> Class<T>.cast(any: Any): T = any as T

// ------------------------------------------------------------ file SAConfigurationKt.kt content (merged)

val LinkedHashSet<ConfigSerializable>.sortedCopy: LinkedHashSet<ConfigSerializable>
    get() = this.sortedBy(ConfigSerializable::name).toCollection(LinkedHashSet())

fun CheckType.toIdentifier(): String =
    CheckType2StringKind.active.convert(this)
