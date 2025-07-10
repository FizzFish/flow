package cn.sast.framework.plugin

import cn.sast.api.config.*
import cn.sast.api.report.CheckType2StringKind
import cn.sast.common.GLB
import cn.sast.common.IResFile
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModule
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.OpenOption
import java.util.*

/**
 * Large configuration aggregate controlling every piece of the analysis pipeline.
 * This rewrite keeps public surface & serialisation layout while *greatly* simplifying internals.
 */
@Serializable
data class SAConfiguration(
    val builtinAnalysisConfig: BuiltinAnalysisConfig = BuiltinAnalysisConfig(),
    val preAnalysisConfig: PreAnalysisConfig = PreAnalysisConfig(),
    val configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> = LinkedHashMap(),
    val checkers: LinkedHashSet<CheckersConfig> = LinkedHashSet(),
) {
    // ---------------------------------------------------------------------
    //  Runtime helpers (non‑serialised)
    // ---------------------------------------------------------------------
    @Transient private val related = IdentityHashMap<PluginDefinitions<*, *>, IConfig>()
    @Transient private val disabled = IdentityHashMap<PluginDefinitions<*, *>, IConfig>()

    // ---------------------------------------------------------------------
    //  Public API – high‑level helpers (much simplified)
    // ---------------------------------------------------------------------

    /** Sort configs + checkers into deterministic order; returns *true* iff order changed. */
    fun sort(): Boolean {
        val oldHash = linkedHashCode()
        configurations.entries.toMutableList()
            .sortedBy { it.key }
            .forEach { (k, v) -> configurations[k] = v.sorted().toLinkedHashSet() }
        checkers.sortByName()
        return oldHash != linkedHashCode()
    }

    /** Build a [SaConfig] filtered by [checkerFilter]. */
//    fun filter(defs: PluginDefinitions, checkerFilter: CheckerFilterByName?): SaConfig {
//        // ⚠️  FULL logic omitted – this keeps the method usable for pipeline wiring but
//        //     does not reproduce the complex entitlement checks from original source.
//        val mergedSootHandler: ISootInitializeHandler = ISootInitializeHandler { _, _ -> }
//        return SaConfig(
//            builtinAnalysisConfig,
//            preAnalysisConfig,
//            emptySet(),
//            mergedSootHandler,
//            emptySet()
//        )
//    }
    fun filter(
        defs: PluginDefinitions,
        checkerFilter: CheckerFilterByName?
    ): SaConfig {
        // 1️⃣ 计算启用清单
        val enableDefinitions: EnablesConfig = getCheckers(defs, checkerFilter)

        // 2️⃣ 打印调试信息（可按需删减）
//        logger.info { "enabled checker units   : ${enableDefinitions.preAnalysisUnits + enableDefinitions.aiAnalysisUnits}" }
//        logger.info { "enabled soot configs    : ${enableDefinitions.sootConfig}" }
//        logger.info { "enabled check types     : ${enableDefinitions.checkTypes}" }
//        logger.info { "builtinAnalysisConfig   : $builtinAnalysisConfig" }

        // 3️⃣ 合并/拼接 Soot 配置
        val sootConfigMerge: ISootInitializeHandler =
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

        // 4️⃣ 将所有已启用的 CheckType 注入全局 GLB（保持原逻辑）
        GLB += defs
            .getCheckTypeDefinition(CheckType::class.java)
            .map { it.singleton }

        // 5️⃣ 构造并返回最终配置
        return SaConfig(
            builtinAnalysisConfig,
            preAnalysisConfig,
            (enableDefinitions.preAnalysisUnits + enableDefinitions.aiAnalysisUnits)
                .toPersistentSet()
                .toMutableSet(),          // <- MutableSet<CheckerUnit>
            sootConfigMerge,
            enableDefinitions.checkTypes.toSet()
        )
    }


    /** YAML serialise into [out]. */
    fun serialize(module: SerializersModule, out: IResFile) {
        val yaml = Yaml(module, yamlConfiguration)
        Files.newOutputStream(out.path, *arrayOf<OpenOption>()).use { os ->
            yaml.encodeToStream(serializer(), this, os)
        }
    }

    // ---------------------------------------------------------------------
    //  Small helpers / extensions
    // ---------------------------------------------------------------------
    private fun linkedHashCode(): Int = listOf(
        configurations.values.map { it.toList() },
        checkers.map { it.checkTypes.toList() }
    ).hashCode()

    private fun <T> Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> = LinkedHashSet(this.toList())

    private fun LinkedHashSet<CheckersConfig>.sortByName() {
        val sorted = this.sortedBy { it.name }
        clear(); addAll(sorted)
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        fun serializer() = SAConfiguration.serializer()
    }
}
data class EnablesConfig(
    val aiAnalysisUnits: MutableList<AIAnalysisUnit> = ArrayList(),
    val preAnalysisUnits: MutableList<PreAnalysisUnit> = ArrayList(),
    val sootConfig: MutableList<ISootInitializeHandler> = ArrayList(),
    val checkTypes: MutableList<CheckType> = ArrayList(),
    val def2config: MutableMap<CheckerUnit, IConfig> = IdentityHashMap()
)

internal fun LinkedHashSet<ConfigSerializable>.sorted(): List<ConfigSerializable> =
    this.sorted()

fun CheckType.toIdentifier(): String =
    CheckType2StringKind.active.convert(this)
