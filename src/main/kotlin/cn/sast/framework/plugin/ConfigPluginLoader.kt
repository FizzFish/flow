package cn.sast.framework.plugin

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.SaConfig
import cn.sast.common.*
import cn.sast.framework.AnalyzeTaskRunner
import com.feysh.corax.config.api.IConfigPluginExtension
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import mu.KotlinLogging
import org.pf4j.*
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.*
import java.util.function.BooleanSupplier
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.text.Charsets.UTF_8

/**
 * Load *Corax* configuration‑plugins (PF4J jars) and expose helper APIs to resolve
 * [SaConfig] instances.
 *
 * The logic is a 1‑to‑1 rewrite from the original Java bytecode with purely
 * syntactic/idiomatic Kotlin fixes; behaviour is preserved.
 */
class ConfigPluginLoader(
    /** Directories that contain built‑in YAML configs shipped with the framework. */
    val configDirs: List<IResource>,
    /** Directories where PF4J will look for *config‑plugins* (jars). */
    private val pluginsDirs: List<IResDirectory>,
) {

    // ---------------------------------------------------------------------
    //  Fields & delegates
    // ---------------------------------------------------------------------

    private val pluginManager: PluginManager by lazy(NONE) { loadPlugin() }
    private val serializersModule: SerializersModule by lazy(NONE) {
        PluginDefinitions.getSerializersModule(pluginManager)
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }

    // ---------------------------------------------------------------------
    //  Nested PF4J plugin‑manager with custom loader strategy
    // ---------------------------------------------------------------------

    /**
     * Custom PF4J manager that uses [classLoadStrategy] so we can attach agents
     * (JDK 17+ requires *ADP* or similar). Also provides a lazily computed map
     * from loaded plugin to its own [Reflections] index (used by some analyses).
     */
    class PluginManager(
        importPaths: List<Path>,
        private val classLoadStrategy: ClassLoadingStrategy = ClassLoadingStrategy.ADP,
    ) : DefaultPluginManager(importPaths) {

        private val pluginToReflections: Map<PluginWrapper, Reflections> by lazy(NONE) {
            plugins.associateWith { wrapper ->
                val cl = wrapper.pluginClassLoader as URLClassLoader
                Reflections(ConfigurationBuilder()
                    .addUrls(cl.urLs.toList())
                    .addClassLoaders(listOf(cl)))
            }
        }

        // -----------------------------------------------------------------
        //  PF4J factory overrides
        // -----------------------------------------------------------------

        override fun createPluginDescriptorFinder(): PluginDescriptorFinder =
            ManifestPluginDescriptorFinder()

        override fun createPluginRepository(): PluginRepository? =
            CompoundPluginRepository()
                .add(DefaultPluginRepository(pluginsRoots), BooleanSupplier { isNotDevelopment })

        override fun createPluginLoader(): PluginLoader? =
            CompoundPluginLoader()
                .add(object : DefaultPluginLoader(this) {
                    override fun createPluginClassLoader(
                        pluginPath: Path,
                        pluginDescriptor: PluginDescriptor,
                    ): PluginClassLoader = PluginClassLoader(
                        this@PluginManager,
                        pluginDescriptor,
                        javaClass.classLoader,
                        classLoadStrategy,
                    )
                }, BooleanSupplier { isNotDevelopment })

        // -----------------------------------------------------------------
        //  Life‑cycle hooks
        // -----------------------------------------------------------------

        @Throws(ClassNotFoundException::class)
        override fun startPlugins() {
            // Commercial guard + secret class loading shim
            for (wrapper in plugins) {
                try {
                    val clazzName = String(Base64.getDecoder().decode("Y29tLmRpZm9jZC5WZXJpZnlKTkk="), UTF_8)
                    val clz = wrapper.pluginClassLoader.loadClass(clazzName)
                    AnalyzeTaskRunner.setV3r14yJn1Class(clz)
                } catch (_: Exception) {
                    // ignore
                }

                if (PluginDefinitions.checkCommercial(wrapper.pluginId)) {
                    AnalyzerEnv.bvs1n3ss.getAndAdd(1)
                }
            }
            super.startPlugins()
        }

        // Public accessor
        fun pluginToReflections(): Map<PluginWrapper, Reflections> = pluginToReflections
    }

    // ---------------------------------------------------------------------
    //  Public helpers
    // ---------------------------------------------------------------------

    fun getPluginManager(): PluginManager = pluginManager

    /** Return all [IConfigPluginExtension]s, optionally limited to one `pluginId`. */
    private fun getConfigExtensions(pluginId: String?): List<IConfigPluginExtension> =
        if (pluginId != null)
            pluginManager.getExtensions(IConfigPluginExtension::class.java, pluginId)
        else
            pluginManager.getExtensions(IConfigPluginExtension::class.java)
                .also {
                    logger.info { "Found ${it.size} extensions for extension point '${IConfigPluginExtension::class.java.name}'" }
                }

    /**
     * Locate a single [SaConfig] either by **pluginId**/**name** or by auto‑discovery.
    * Mirrors the exact guard logic of the Java original.
    */
    fun loadFromName(pluginId: String?, name: String?): SaConfig {
        if (name == null) {
            logger.info {
                "Automatically search for the SA-Config under path '$pluginsDirs', with the requirement that there can only exist one config."
            }
        }

        val configExtensions = getConfigExtensions(pluginId)
        require(configExtensions.isNotEmpty()) { "not found IConfigPluginExtension in: $pluginsDirs" }

        val searchPath = pluginsDirs.joinToString(File.pathSeparator)

        val chosen: IConfigPluginExtension = if (name != null) {
            val matches = configExtensions.filter { it.name == name }
            when {
                matches.isEmpty() -> error("your choose: $name not found in plugins dir: $searchPath")
                matches.size > 1 -> error("dup choose: $name in plugins dir: $searchPath. choose: $matches")
                else -> matches.first()
            }
        } else {
            if (configExtensions.size != 1) {
                val display = configExtensions.joinToString(",\n\t") { "${it.name}@$searchPath" }
                error("you need choose which one of names: [ \n\t$display ]")
            }
            configExtensions.first()
        }

        logger.info {
            "use config method for entry: ${chosen.name} in " +
                    Resource.locateAllClass(chosen.javaClass)
        }

        return SaConfig(
            null, // builtinAnalysisConfig
            null, // preAnalysisConfig
            ExtensionsKt.toImmutableSet(chosen.units),
            chosen.sootConfig,
            null, // extra
        )
    }

    /**
     * Parse, normalise, and post‑process an SA‑Configuration YAML (checker‑list).
     */
    fun searchCheckerUnits(ymlConfig: IResFile, checkerFilter: CheckerFilterByName?): SaConfig {
        val configFromYml = SAConfiguration.deserialize(serializersModule, ymlConfig)
        val needNormalize = configFromYml.sort()
        val defs = PluginDefinitions.load(pluginManager)
        val hasChange = configFromYml.supplementAndMerge(defs, ymlConfig.toString())

        val dir: IResource? = ymlConfig.parent
        if (dir != null && (needNormalize || hasChange)) {
            val nameSansExt = ymlConfig.name.removeSuffix(".${ymlConfig.extension}").dropLast(1)
            val normalizedFile = dir.resolve("$nameSansExt.normalize.yml").toFile()
            configFromYml.sort()
            configFromYml.serialize(serializersModule, normalizedFile)
            logger.info { "Serialized a normalized SA-Configuration yml file: $normalizedFile" }
        }

        return configFromYml.filter(defs, checkerFilter)
    }

    /** Generate an *empty template* SA‑Configuration YAML. */
    fun makeTemplateYml(tempFile: IResFile) {
        val defs = PluginDefinitions.load(pluginManager)
        val emptyYaml = SAConfiguration()
        emptyYaml.supplementAndMerge(defs, null)
        emptyYaml.sort()
        emptyYaml.serialize(serializersModule, tempFile)
        logger.info { "Serialized template SA-Configuration file: $tempFile" }
    }

    // ---------------------------------------------------------------------
    //  Private helpers
    // ---------------------------------------------------------------------

    /** Discover and start PF4J plugins. */
    @Throws(ClassNotFoundException::class)
    private fun loadPlugin(): PluginManager {
        logger.info { "Plugin directory: $pluginsDirs" }
        val pm = PluginManager(pluginsDirs.map { it.path })
        pm.loadPlugins()
        pm.startPlugins()
        check(pm.plugins.isNotEmpty()) { "no config plugin found" }
        return pm
    }
}
