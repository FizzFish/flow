package cn.sast.framework.plugin

import cn.sast.api.AnalyzerEnv
import cn.sast.api.util.getSootTypeName
import cn.sast.common.Resource
import com.feysh.corax.config.api.*
import com.feysh.corax.config.builtin.checkers.DeadCodeChecker
import com.feysh.corax.config.builtin.checkers.DeadStoreChecker
import com.feysh.corax.config.builtin.checkers.DefineUnusedChecker
import com.feysh.corax.config.builtin.soot.DefaultSootConfiguration
import com.feysh.corax.config.builtin.soot.EmptySootConfiguration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.*
import mu.KLogger
import mu.KotlinLogging
import org.pf4j.PluginWrapper
import kotlin.reflect.KClass

/**
 * Kotlin reconstruction of the original `PluginDefinitions` (de‑compiled Java).
 *
 * NOTE  ‑  The file was originally authored in Kotlin; most semantics therefore map one‑to‑one.
 * This rewrite stays as close as possible to the original logic while leveraging Kotlin idioms
 * (e.g. immutable collections, `buildList`, and `buildString`).
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class PluginDefinitions {

    /** All discovered definitions (built‑in + plugins). */
    private val definitions: MutableSet<Definition<*>> = LinkedHashSet()

    /** Mapping   identifier → definition. */
    private val identifiers: MutableMap<String, Definition<*>> = LinkedHashMap()

    /** Mapping   definition  → identifier (reverse lookup). */
    private val identifiersReverse: MutableMap<Definition<*>, String> = LinkedHashMap()

    init {
        // --- Built‑in registrations -----------------------------------------------------------
        register("builtin", ISootInitializeHandler::class.java, DefaultSootConfiguration::class.java)
        register("builtin", ISootInitializeHandler::class.java, EmptySootConfiguration::class.java)

        register("builtin", CheckType::class.java, DefineUnusedChecker.UrfUnreadField)
        register("builtin", CheckType::class.java, DefineUnusedChecker.UnusedMethod)
        register("builtin", CheckType::class.java, DeadCodeChecker.DeadCode)
        register("builtin", CheckType::class.java, DeadCodeChecker.UnreachableBranch)
        register("builtin", CheckType::class.java, DeadStoreChecker.DeadLocalStore)
    }

    // -----------------------------------------------------------------------------------------
    //  Sub‑type‑specific getters (used by the configuration UI)
    // -----------------------------------------------------------------------------------------

    @JvmName("getISootInitializeHandlerDefinition")
    fun getISootInitializeHandlerDefinition(@Suppress("UNUSED_PARAMETER") clz: Class<ISootInitializeHandler>): LinkedHashSet<ISootInitializeHandlerDefinition> =
        definitions.filterIsInstance<ISootInitializeHandlerDefinition>().toCollection(LinkedHashSet())

    @JvmName("getCheckTypeDefinition")
    fun getCheckTypeDefinition(@Suppress("UNUSED_PARAMETER") clz: Class<CheckType>): LinkedHashSet<CheckTypeDefinition> =
        definitions.filterIsInstance<CheckTypeDefinition>().toCollection(LinkedHashSet())

    @JvmName("getCheckerUnitDefinition")
    fun getCheckerUnitDefinition(@Suppress("UNUSED_PARAMETER") clz: Class<CheckerUnit>): LinkedHashSet<CheckerUnitDefinition> =
        definitions.filterIsInstance<CheckerUnitDefinition>().toCollection(LinkedHashSet())

    @JvmName("getPreAnalysisUnit")
    fun getPreAnalysisUnit(@Suppress("UNUSED_PARAMETER") clz: Class<PreAnalysisUnit>): LinkedHashSet<CheckerUnitDefinition> =
        getCheckerUnitDefinition(CheckerUnit::class.java)
            .filterTo(LinkedHashSet()) { it.type == clz }

    @JvmName("getAIAnalysisUnit")
    fun getAIAnalysisUnit(@Suppress("UNUSED_PARAMETER") clz: Class<AIAnalysisUnit>): LinkedHashSet<CheckerUnitDefinition> =
        getCheckerUnitDefinition(CheckerUnit::class.java)
            .filterTo(LinkedHashSet()) { it.type == clz }

    /** Convenience – returns the *default* config for every definition. */
    fun getDefaultConfigs(): Map<Definition<*>, IConfig> = buildMap {
        for (def in definitions) put(def, def.defaultConfig)
    }

    // -----------------------------------------------------------------------------------------
    //  Registration helpers (built‑ins & plugins)
    // -----------------------------------------------------------------------------------------

    private fun register(prefix: String, type: Class<*>, implClass: Class<*>) {
        register(prefix, type, Companion.singleton(implClass))
    }

    private fun register(prefix: String, type: Class<*>, singleton: Any?) {
        if (singleton == null) return

        val def = Definition.invoke(prefix, type, singleton)
        val identifier = buildString {
            append(prefix).append(":")
            append(getSootTypeName(type))
            append(getSootTypeName(singleton.javaClass))
        }

        identifiers[identifier]?.let { exists ->
            val msg = "When adding ${getSootTypeName(singleton.javaClass)}: " +
                    "${Resource.locateClass(singleton.javaClass)}, there is already a " +
                    "${exists.singleton.javaClass} ${Resource.locateClass(exists.singleton.javaClass)} with the same name."
            error(msg)
        }

        if (!type.isInstance(singleton)) {
            logger.error { "${singleton.javaClass}: $singleton is not instance of type: $type" }
            return
        }

        identifiers[identifier] = def
        identifiersReverse[def] = identifier
        definitions += def
    }

    /** Register an implementation contributed by a PF4J plugin. */
    fun register(plugin: PluginWrapper, type: Class<*>, definition: Class<*>) {
        register(plugin.pluginId, type, definition)
    }

    // -----------------------------------------------------------------------------------------
    //  Nested sealed hierarchy that represents *every* pluggable concept.
    // -----------------------------------------------------------------------------------------

    sealed class Definition<T : Any>(
        val name: String,
        val type: Class<*>,
        val singleton: T,
        optionFields: List<java.lang.reflect.Field> = Companion.optionFieldsOf(singleton)
    ) {
        /** All declared `SAOptions` fields (cached). */
        private val optionFields: List<java.lang.reflect.Field> = optionFields.also { fs ->
            fs.forEach { it.isAccessible = true }
        }

        /** Map  field‑name → Field (for reflective writes). */
        val fieldName2Field: Map<String, java.lang.reflect.Field> = optionFields.associateBy { it.name }

        /** Helper – discover the *actual* `options` field (commonly named `option` or `options`). */
        val option: SAOptions?
            get() = fieldName2Field["options"]?.get(singleton) as? SAOptions
                ?: fieldName2Field["option"]?.get(singleton) as? SAOptions

        // ---------------------------------------------------------------------
        //  Abstract API expected from concrete sub‑types
        // ---------------------------------------------------------------------
        abstract val defaultConfig: IConfig

        // ---------------------------------------------------------------------
        //  Utility for bulk option injection via reflection
        // ---------------------------------------------------------------------
        @Throws(IllegalAccessException::class)
        fun setOptions(options: Map<String, SAOptions>) {
            for ((fieldName, opt) in options) {
                fieldName2Field[fieldName]?.set(singleton, opt)
                    ?: logger.error { "error fieldName2Field: $fieldName2Field" }
            }
        }

        // ---------------------------------------------------------------------
        //  Static helpers
        // ---------------------------------------------------------------------
        companion object {
            private val logger: KLogger = KotlinLogging.logger {}
            private val definitionBaseTypes: Set<Class<*>> = setOf(
                PreAnalysisUnit::class.java,
                AIAnalysisUnit::class.java,
                ISootInitializeHandler::class.java,
                CheckType::class.java
            )

            /** Conveniences – instantiate the correct concrete wrapper. */
            fun invoke(prefix: String, type: Class<*>, singleton: Any): Definition<*> = when (type) {
                PreAnalysisUnit::class.java, AIAnalysisUnit::class.java -> {
                    val name = "$prefix:${UtilsKt.getSootTypeName(singleton.javaClass)}"
                    @Suppress("UNCHECKED_CAST")
                    CheckerUnitDefinition(name, type, singleton as CheckerUnit)
                }
                ISootInitializeHandler::class.java -> {
                    val name = "$prefix:${UtilsKt.getSootTypeName(singleton.javaClass)}"
                    ISootInitializeHandlerDefinition(name, type, singleton as ISootInitializeHandler)
                }
                CheckType::class.java -> {
                    val name = "$prefix:${UtilsKt.getSootTypeName(singleton.javaClass)}"
                    CheckTypeDefinition(name, type, singleton as CheckType)
                }
                else -> error("Unsupported definition base‑type: $type")
            }

            /** Extract every `SAOptions` field from a singleton. */
            fun optionFieldsOf(singleton: Any): List<java.lang.reflect.Field> =
                singleton.javaClass.declaredFields.filter { SAOptions::class.java.isAssignableFrom(it.type) }
        }
    }

    // -----------------------------------------------------------------------------------------
    //  Concrete wrappers (type‑safe access to sub‑type‑specific config)
    // -----------------------------------------------------------------------------------------

    class CheckerUnitDefinition(
        name: String,
        type: Class<*>,
        singleton: CheckerUnit,
    ) : Definition<CheckerUnit>(name, type, singleton) {
        override val defaultConfig: CheckerUnitOptionalConfig = CheckerUnitOptionalConfig(name, singleton.enableDefault, option)
    }

    class ISootInitializeHandlerDefinition(
        name: String,
        type: Class<*>,
        singleton: ISootInitializeHandler,
    ) : Definition<ISootInitializeHandler>(name, type, singleton) {
        override val defaultConfig: SootOptionsConfig = SootOptionsConfig(name, singleton === DefaultSootConfiguration.INSTANCE, option)
    }

    class CheckTypeDefinition(
        name: String,
        type: Class<*>,
        singleton: CheckType,
    ) : Definition<CheckType>(name, type, singleton) {
        override val defaultConfig: CheckersConfig.CheckTypeConfig = CheckersConfig.CheckTypeConfig(singleton)
    }

    // -----------------------------------------------------------------------------------------
    //  Companion utilities (reflection, PF4J discovery, Serialization)
    // -----------------------------------------------------------------------------------------

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
        private val commercialKeywords: Set<String> = setOf("commercial", "business")

        /** True if *any* commercial keyword appears in the name (case‑insensitively). */
        fun checkCommercial(name: String): Boolean = commercialKeywords.any { name.contains(it, ignoreCase = true) }

        /** Resolve the Kotlin `object` instance (if any) – returns `null` for regular classes. */
        fun <T : Any> singleton(clz: Class<T>): T? = try {
            // Track license usage statistics.
            if (checkCommercial(clz.name)) AnalyzerEnv.Bvs1n3ss.getAndAdd(1)
            clz.getField("INSTANCE").get(null) as? T
        } catch (e: NoSuchFieldException) {
            logger.debug(e) { "$clz is not a singleton" }; null
        } catch (e: SecurityException) {
            logger.debug(e) { "Failed to get INSTANCE field of $clz" }; null
        }

        /** Build the kotlinx‑serialization module with *all* plugin‑provided polymorphic types. */
        fun serializersModule(pluginManager: ConfigPluginLoader.PluginManager): SerializersModule =
            SerializersModule {
                // --- CheckerUnitOptionalConfig ------------------------------------------------
                polymorphicDefault(Object::class) { CheckerUnitOptionalConfig.serializer() }

                // -- SAOptions hierarchy -------------------------------------------------------
                polymorphic(SAOptions::class) {
                    subclass(DefaultSootConfiguration.CustomOptions::class, DefaultSootConfiguration.CustomOptions.serializer())
                    addAllSubTypesOf(pluginManager, SAOptions::class)
                }

                // -- ITaintType hierarchy ------------------------------------------------------
                polymorphic(ITaintType::class) { addAllSubTypesOf(pluginManager, ITaintType::class) }

                // -- CheckType hierarchy -------------------------------------------------------
                polymorphic(CheckType::class) { addAllSubTypesOf(pluginManager, CheckType::class) }
            }

        // ---------------------------------------------------------------------
        //  Helpers
        // ---------------------------------------------------------------------
        private inline fun <reified T : Any> PolymorphicModuleBuilder<in T>.addAllSubTypesOf(
            pluginManager: ConfigPluginLoader.PluginManager,
            baseKClass: KClass<T>
        ) {
            val javaBase = baseKClass.java
            val subs = buildSet {
                for ((_, reflections) in pluginManager.pluginToReflections) {
                    addAll(reflections.getSubTypesOf(javaBase))
                }
            }
            for (sub in subs) {
                if (!ReflectionUtilKt.isAbstract(sub)) {
                    val ser = SerializersKt.serializerOrNull(sub) as? KSerializer<Any>
                    if (ser != null) {
                        subclass(sub.kotlin as KClass<out T>, ser)
                    } else {
                        logger.error { "The class $sub does not have the @Serializable annotation." }
                    }
                }
            }
        }
    }
}
