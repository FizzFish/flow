package cn.sast.framework.plugin

import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.util.Kotlin_extKt
import cn.sast.common.GLB
import cn.sast.common.IResFile
import cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig
import cn.sast.framework.plugin.PluginDefinitions.Definition
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.AIAnalysisUnit
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ISootInitializeHandler
import com.feysh.corax.config.api.PreAnalysisUnit
import com.feysh.corax.config.api.SAOptions
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.IdentityHashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.NoSuchElementException
import java.util.Comparator
import kotlin.comparisons.compareValues
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import soot.Main
import soot.Scene
import soot.jimple.toolkits.callgraph.CallGraph
import soot.options.Options

@Serializable
@SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,557:1\n126#2:558\n153#2,3:559\n126#2:645\n153#2,3:646\n1557#3:562\n1628#3,3:563\n1261#3,4:566\n1628#3,3:570\n230#3,2:573\n1628#3,3:575\n774#3:578\n865#3,2:579\n2632#3,3:581\n774#3:584\n865#3,2:585\n1611#3,9:587\n1863#3:596\n1864#3:598\n1620#3:599\n1611#3,9:600\n1863#3:609\n1864#3:611\n1620#3:612\n1611#3,9:613\n1863#3:622\n1864#3:624\n1620#3:625\n1611#3,9:626\n1863#3:635\n1864#3:637\n1620#3:638\n1279#3,2:639\n1293#3,4:641\n1557#3:649\n1628#3,3:650\n1#4:597\n1#4:610\n1#4:623\n1#4:636\n1#4:660\n487#5,7:653\n*S KotlinDebug\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration\n*L\n195#1:558\n195#1:559,3\n289#1:645\n289#1:646,3\n196#1:562\n196#1:563,3\n203#1:566,4\n204#1:570,3\n245#1:573,2\n262#1:575,3\n263#1:578\n263#1:579,2\n266#1:581,3\n270#1:584\n270#1:585,2\n274#1:587,9\n274#1:596\n274#1:598\n274#1:599\n275#1:600,9\n275#1:609\n275#1:611\n275#1:612\n276#1:613,9\n276#1:622\n276#1:624\n276#1:625\n277#1:626,9\n277#1:635\n277#1:637\n277#1:638\n288#1:639,2\n288#1:641,4\n336#1:649\n336#1:650,3\n274#1:597\n275#1:610\n276#1:623\n277#1:636\n418#1:653,7\n*E\n"])
data class SAConfiguration(
    val builtinAnalysisConfig: BuiltinAnalysisConfig = BuiltinAnalysisConfig(null, null, 0, 0, 15, null),
    val preAnalysisConfig: PreAnalysisConfig = PreAnalysisConfig(0, 0, 0, null, 0, 31, null),
    val configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> = LinkedHashMap(),
    val checkers: LinkedHashSet<CheckersConfig> = LinkedHashSet()
) {
    @Transient
    private val relatedMap: IdentityHashMap<Definition<*>, IConfig> = IdentityHashMap()

    @Transient
    private val disabled: IdentityHashMap<Definition<*>, IConfig> = IdentityHashMap()

    private object ConfigPairComparator : Comparator<Pair<*, *>> {
        override fun compare(a: Pair<*, *>, b: Pair<*, *>): Int {
            return compareValues(a.first as Comparable<Any>, b.first as Comparable<Any>)
        }
    }

    private object CheckersConfigComparator : Comparator<CheckersConfig> {
        override fun compare(a: CheckersConfig, b: CheckersConfig): Int {
            return compareValues(a, b)
        }
    }

    private fun linkedHashCode(): Int {
        val hash = ArrayList<Any>()
        hash.add(configurations.values.map { it.toList() }.hashCode())
        hash.add(checkers.map { it.checkTypes.toList() }.hashCode())
        return hash.hashCode()
    }

    fun sort(): Boolean {
        val old = linkedHashCode()
        configurations = configurations.toList()
            .sortedWith(ConfigPairComparator)
            .associate { it.first to it.second.sortedWith(CheckersConfigComparator).toLinkedHashSet() }
            .toLinkedHashMap()
        checkers = checkers.sortedWith(CheckersConfigComparator).map { it.sort() }.toLinkedHashSet()
        return linkedHashCode() != old
    }

    private fun <T> Definition<*>.getInstance(clz: Class<T>): T? {
        return if (clz.isInstance(getSingleton())) getSingleton() as T else null
    }

    private fun Definition<*>.relateConfig(): IConfig {
        return relatedMap[this] ?: throw IllegalStateException("$this not relate to a config")
    }

    private fun <T> Definition<*>.get(clz: Class<T>): T? {
        val config = relateConfig()
        if (config is IOptional && !config.enable) {
            disabled[this] = config
            return null
        }

        if (this is PluginDefinitions.CheckTypeDefinition) {
            val checkerMatches = CheckersConfig(singleton.checker)
            val matchingChecker = checkers.firstOrNull { it.name == checkerMatches.name }
                ?: throw NoSuchElementException("Collection contains no element matching the predicate.")

            if (!matchingChecker.enable) {
                return null
            }
        }

        return getInstance(clz)
    }

    fun getCheckers(defs: PluginDefinitions, checkerFilter: CheckerFilterByName?): EnablesConfig {
        val res = EnablesConfig()
        val checkerUnits = defs.getCheckerUnitDefinition(CheckerUnit::class.java)
        val sootInitializeHandlers = defs.getISootInitializeHandlerDefinition(ISootInitializeHandler::class.java)
        val checkTypes = defs.getCheckTypeDefinition(CheckType::class.java)
        
        val enabledNames = checkerFilter?.enables
        if (checkerFilter != null) {
            val def2checkerUnit = checkerFilter.renameMap.keys
            val definedCheckTypes = checkTypes.map { it.defaultConfig.checkType }.toSet()
            
            val notExistsNames = enabledNames.filter { 
                !definedCheckTypes.contains(it) && !def2checkerUnit.contains(it) 
            }
            
            if (notExistsNames.isNotEmpty()) {
                logger.warn { "\nThese check types named $notExistsNames cannot be found in analysis-config\n" }
            }
            
            if (enabledNames.none { definedCheckTypes.contains(it) }) {
                throw IllegalStateException("No checker type are enabled")
            }
            
            val disabledCheckers = definedCheckTypes.filter { !enabledNames.contains(it) }
            logger.debug { "\nThese check types $disabledCheckers are not enabled\n" }
        }

        res.preAnalysisUnits.addAll(checkerUnits.mapNotNull { get(it, PreAnalysisUnit::class.java) })
        res.aiAnalysisUnits.addAll(checkerUnits.mapNotNull { get(it, AIAnalysisUnit::class.java) })
        res.sootConfig.addAll(sootInitializeHandlers.mapNotNull { get(it, ISootInitializeHandler::class.java) })
        
        res.checkTypes.addAll(checkTypes.mapNotNull { ct ->
            if (enabledNames != null) {
                if (enabledNames.contains(ct.defaultConfig.checkType)) {
                    getInstance(ct, CheckType::class.java)
                } else null
            } else {
                get(ct, CheckType::class.java)
            }
        })

        val checkerUnitMap = checkerUnits.associateWith { get(it, CheckerUnit::class.java) }
            .let { Kotlin_extKt.nonNullValue(it) }
        
        res.def2config.putAll(checkerFilter?.renameMap?.mapValues { 
            relateConfig(it.key as PluginDefinitions.CheckerUnitDefinition)
        } ?: emptyMap())

        return res
    }

    fun filter(defs: PluginDefinitions, checkerFilter: CheckerFilterByName?): SaConfig {
        val enableDefinitions = getCheckers(defs, checkerFilter)
        
        logger.info { "Num of effective PreAnalysisUnit is ${enableDefinitions.preAnalysisUnits.size}/${defs.getPreAnalysisUnit(PreAnalysisUnit::class.java).size}" }
        logger.info { "Num of effective AIAnalysisUnit is ${enableDefinitions.aiAnalysisUnits.size}/${defs.getAIAnalysisUnit(AIAnalysisUnit::class.java).size}" }
        logger.info { "Num of effective ISootInitializeHandler is ${enableDefinitions.sootConfig.size}/${defs.getISootInitializeHandlerDefinition(ISootInitializeHandler::class.java).size}" }
        logger.info { "Num of effective CheckType is ${enableDefinitions.checkTypes.size}/${defs.getCheckTypeDefinition(CheckType::class.java).size}" }

        val sootConfigMerge = if (enableDefinitions.sootConfig.size == 1) {
            enableDefinitions.sootConfig.first()
        } else {
            object : ISootInitializeHandler {
                override fun configure(options: Options) {
                    enableDefinitions.sootConfig.forEach { it.configure(options) }
                }

                override fun configure(scene: Scene) {
                    enableDefinitions.sootConfig.forEach { it.configure(scene) }
                }

                override fun configureAfterSceneInit(scene: Scene, options: Options) {
                    enableDefinitions.sootConfig.forEach { it.configureAfterSceneInit(scene, options) }
                }

                override fun configure(main: Main) {
                    enableDefinitions.sootConfig.forEach { it.configure(main) }
                }

                override fun onBeforeCallGraphConstruction(scene: Scene, options: Options) {
                    enableDefinitions.sootConfig.forEach { it.onBeforeCallGraphConstruction(scene, options) }
                }

                override fun onAfterCallGraphConstruction(cg: CallGraph, scene: Scene, options: Options) {
                    enableDefinitions.sootConfig.forEach { it.onAfterCallGraphConstruction(cg, scene, options) }
                }

                override fun toString(): String {
                    return "SootConfigMerge-${enableDefinitions.sootConfig}"
                }
            }
        }

        GLB.INSTANCE += defs.getCheckTypeDefinition(CheckType::class.java).map { it.singleton }

        return SaConfig(
            builtinAnalysisConfig,
            preAnalysisConfig,
            (enableDefinitions.preAnalysisUnits + enableDefinitions.aiAnalysisUnits).toMutableSet(),
            sootConfigMerge,
            enableDefinitions.checkTypes.toSet()
        )
    }

    fun serialize(serializersModule: SerializersModule, out: IResFile) {
        val yml = Yaml(serializersModule, MainConfigKt.getYamlConfiguration())
        try {
            Files.newOutputStream(out.path).use { stream ->
                yml.encodeToStream(serializer(), this, stream)
            }
        } catch (e: Exception) {
            Files.delete(out.path)
            throw e
        }
    }

    fun supplementAndMerge(defs: PluginDefinitions, ymlPath: String?): Boolean {
        val hashOld = hashCode()
        val defaultConfig = Companion.getDefaultConfig(defs)
        val head = lazy { logger.warn { "/--------------------- config information view ---------------------" } }

        Compare(this, defs, head).apply {
            existsHandler = { self, thatConfig ->
                val definitions = getAndRelate(defs, self, thatConfig)
                if (self is IFieldOptions) {
                    self.options?.let { options ->
                        definitions.keys.forEach { it.options = options }
                    }
                }
            }
            multipleHandler = { self, multipleThat ->
                throw IllegalStateException("Please remove duplicate definitions: $multipleThat from your plugin directory")
            }
            notExistsHandler = { type, miss ->
                head.value
                logger.warn { "There is a configuration ${miss.name} of type $type defined, but there is no corresponding definition in the plugin directory." }
            }
        }.compare(defaultConfig)

        Compare(defaultConfig, ymlPath, head, this, defs).apply {
            multipleHandler = { self, multipleThat ->
                throw IllegalStateException("Please remove duplicate configurations: $multipleThat from $ymlPath")
            }
            notExistsHandler = { type, miss ->
                if (ymlPath != null) {
                    head.value
                    logger.warn { "There is an definition \"$miss\" of type $type, but it is not configured in configuration: $ymlPath." }
                }
                configurations.getOrPut(type) { LinkedHashSet() }.add(miss)
                getAndRelate(defs, miss, miss)
            }
            notExistsHandler = { checker, miss ->
                if (ymlPath != null) {
                    head.value
                    logger.warn { "There is an definition \"$miss\", but it is not configured in configuration: $ymlPath." }
                }
                checker.checkTypes.add(miss)
                if (!checkers.contains(checker)) {
                    checkers.add(checker)
                }
                getAndRelate(defs, miss, miss)
            }
        }.compare(this)

        if (head.isInitialized()) {
            logger.warn { "\\--------------------- config information view ---------------------" }
        }

        return hashCode() != hashOld
    }

    private fun getAndRelate(defs: PluginDefinitions, self: IConfig, thatConfig: IConfig): MutableMap<Definition<*>, IConfig> {
        val definitions = defs.defaultConfigs.filterValues { it === thatConfig }
        if (definitions.isEmpty()) {
            throw IllegalStateException("internal error. empty definition. config: $self and $thatConfig")
        }
        if (definitions.size != 1) {
            throw IllegalStateException("internal error. multiple definitions: $definitions. config: $self and $thatConfig")
        }
        relatedMap[definitions.keys.first()] = self
        return definitions
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")

        val UnitTypeName: String
            get() = this::class.simpleName!!

        fun getDefaultConfig(defs: PluginDefinitions): SAConfiguration {
            val configurations = LinkedHashMap<String, LinkedHashSet<ConfigSerializable>>()
            val checkers = LinkedHashSet<CheckersConfig>()

            for ((def, config) in defs.defaultConfigs) {
                require(config == def.defaultConfig) { "Check failed." }

                when (def) {
                    is PluginDefinitions.CheckTypeDefinition -> {
                        val checker = CheckersConfig(def.singleton.checker)
                        val existing = checkers.filter { it.name == checker.name }
                        
                        when {
                            existing.isEmpty() -> {
                                checkers.add(checker)
                                checker.checkTypes.add(def.defaultConfig)
                            }
                            existing.size == 1 -> {
                                existing.first().checkTypes.add(def.defaultConfig)
                            }
                            else -> throw IllegalStateException(
                                "Please fix duplicate IChecker::name of checker definitions: ${def.singleton.checker} and $existing"
                            )
                        }
                    }
                    is PluginDefinitions.CheckerUnitDefinition -> {
                        configurations.getOrPut(getUnitTypeName(def.type)) { LinkedHashSet() }
                            .add(def.defaultConfig)
                    }
                    is PluginDefinitions.ISootInitializeHandlerDefinition -> {
                        configurations.getOrPut(getUnitTypeName(def.type)) { LinkedHashSet() }
                            .add(def.defaultConfig)
                    }
                    else -> throw NoWhenBranchMatchedException()
                }
            }

            return SAConfiguration(
                configurations = configurations,
                checkers = checkers
            )
        }

        fun deserialize(serializersModule: SerializersModule, input: IResFile): SAConfiguration {
            val yml = Yaml(serializersModule, MainConfigKt.getYamlConfiguration())
            return Files.newInputStream(input.path).use { stream ->
                yml.decodeFromStream(serializer(), stream)
            }
        }

        fun serializer(): KSerializer<SAConfiguration> {
            return SAConfiguration.serializer()
        }
    }

    private abstract class Compare(val self: SAConfiguration) {
        protected var existsHandler: (IConfig, IConfig) -> Unit = { _, _ -> }
        protected var multipleHandler: (IConfig, Collection<IConfig>) -> Unit = { _, _ -> }
        protected var notExistsHandler: (String, ConfigSerializable) -> Unit = { _, _ -> }
        protected var notExistsHandler2: (CheckersConfig, CheckTypeConfig) -> Unit = { _, _ -> }

        fun compare(that: SAConfiguration) {
            for ((type, configs) in self.configurations) {
                for (config in configs) {
                    val matched = that.configurations[type]?.filter { it.name == config.name } ?: emptyList()
                    when {
                        matched.size > 1 -> multipleHandler(config, matched)
                        matched.isNotEmpty() -> existsHandler(config, matched.first())
                        else -> notExistsHandler(type, config)
                    }
                }
            }

            for (checker in self.checkers) {
                val matchedCheckers = that.checkers.filter { it.name == checker.name }
                for (checkType in checker.checkTypes) {
                    val matchedTypes = matchedCheckers.flatMap { it.checkTypes }
                        .filter { it.checkType == checkType.checkType }
                    when {
                        matchedTypes.size > 1 -> multipleHandler(checkType, matchedTypes)
                        matchedTypes.isNotEmpty() -> existsHandler(checkType, matchedTypes.first())
                        else -> notExistsHandler2(checker, checkType)
                    }
                }
            }
        }
    }

    data class EnablesConfig(
        val aiAnalysisUnits: MutableList<AIAnalysisUnit> = ArrayList(),
        val preAnalysisUnits: MutableList<PreAnalysisUnit> = ArrayList(),
        val sootConfig: MutableList<ISootInitializeHandler> = ArrayList(),
        var checkTypes: MutableList<CheckType> = ArrayList(),
        var def2config: MutableMap<CheckerUnit, IConfig> = IdentityHashMap()
    ) {
        operator fun plusAssign(b: EnablesConfig) {
            aiAnalysisUnits.addAll(b.aiAnalysisUnits)
            preAnalysisUnits.addAll(b.preAnalysisUnits)
            sootConfig.addAll(b.sootConfig)
        }
    }
}