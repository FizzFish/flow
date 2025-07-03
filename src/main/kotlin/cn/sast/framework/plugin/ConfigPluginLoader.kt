package cn.sast.framework.plugin

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.SaConfig
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.framework.AnalyzeTaskRunner
import com.feysh.corax.config.api.IConfigPluginExtension
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.Base64
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.function.BooleanSupplier
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import org.pf4j.ClassLoadingStrategy
import org.pf4j.CompoundPluginLoader
import org.pf4j.CompoundPluginRepository
import org.pf4j.DefaultPluginLoader
import org.pf4j.DefaultPluginManager
import org.pf4j.DefaultPluginRepository
import org.pf4j.ManifestPluginDescriptorFinder
import org.pf4j.PluginClassLoader
import org.pf4j.PluginDescriptor
import org.pf4j.PluginDescriptorFinder
import org.pf4j.PluginLoader
import org.pf4j.PluginRepository
import org.pf4j.PluginWrapper
import org.reflections.Configuration
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

@SourceDebugExtension(["SMAP\nConfigPluginLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,197:1\n1#2:198\n774#3:199\n865#3,2:200\n1557#3:202\n1628#3,3:203\n*S KotlinDebug\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader\n*L\n133#1:199\n133#1:200,2\n180#1:202\n180#1:203,3\n*E\n"])
class ConfigPluginLoader(configDirs: List<IResource>, pluginsDirs: List<IResDirectory>) {
    val configDirs: List<IResource> = configDirs
    private val pluginsDirs: List<IResDirectory> = pluginsDirs

    val pluginManager: PluginManager by lazy { loadPlugin() }
    private val serializersModule: SerializersModule by lazy { PluginDefinitions.Companion.getSerializersModule(pluginManager) }

    private fun getConfigExtensions(pluginId: String?): List<IConfigPluginExtension> {
        val extensions = if (pluginId != null) {
            pluginManager.getExtensions(IConfigPluginExtension::class.java, pluginId)
        } else {
            pluginManager.getExtensions(IConfigPluginExtension::class.java)
        }

        logger.info { "Found ${extensions.size} extensions for extension point '${IConfigPluginExtension::class.java.name}'" }
        return extensions
    }

    fun loadFromName(pluginId: String?, name: String?): SaConfig {
        if (name == null) {
            logger.info { "Automatically search for the SA-Config under path '$pluginsDirs', with the requirement that there can only exist one config." }
        }

        val configExtensions = getConfigExtensions(pluginId)
        if (configExtensions.isEmpty()) {
            throw IllegalStateException("not found IConfigPluginExtension in: $pluginsDirs")
        }

        val ps = pluginsDirs.joinToString(File.pathSeparator)
        val extension = if (name == null) {
            if (configExtensions.size != 1) {
                throw IllegalStateException(
                    "you need choose which one of names: [ \n\t${
                        configExtensions.joinToString(",\n\t") { ext -> 
                            "${ext.getName()}@$ps" 
                        } 
                    } ]"
                )
            }
            configExtensions.first()
        } else {
            val choose = configExtensions.filter { it.getName() == name }
            when {
                choose.isEmpty() -> throw IllegalStateException("your choose: $name not found in plugins dir: $ps")
                choose.size != 1 -> throw IllegalStateException("dup choose: $name in plugins dir: $ps. choose: $choose")
                else -> choose.first()
            }
        }

        logger.info { "use config method for entry: ${extension.getName()} in ${Resource.INSTANCE.locateAllClass(extension::class.java)}" }
        return SaConfig(
            units = ExtensionsKt.toImmutableSet(extension.getUnits()),
            sootConfig = extension.getSootConfig(),
            options = 3
        )
    }

    fun searchCheckerUnits(ymlConfig: IResFile, checkerFilter: CheckerFilterByName?): SaConfig {
        val configFromYml = SAConfiguration.deserialize(serializersModule, ymlConfig)
        val needNormalize = configFromYml.sort()
        val defs = PluginDefinitions.load(pluginManager)
        val hasChange = configFromYml.supplementAndMerge(defs, ymlConfig.toString())

        ymlConfig.parent?.let { parent ->
            if (needNormalize || hasChange) {
                val normalizedConfig = parent.resolve(
                    "${ymlConfig.name.substringBeforeLast(ymlConfig.extension).dropLast(1)}.normalize.yml"
                ).toFile()
                configFromYml.sort()
                configFromYml.serialize(serializersModule, normalizedConfig)
                logger.info { "Serialized a normalized SA-Configuration yml file: $normalizedConfig" }
            }
        }

        return configFromYml.filter(defs, checkerFilter)
    }

    fun makeTemplateYml(tempFile: IResFile) {
        val defs = PluginDefinitions.load(pluginManager)
        val emptyYaml = SAConfiguration(options = 15)
        emptyYaml.supplementAndMerge(defs, null)
        emptyYaml.sort()
        emptyYaml.serialize(serializersModule, tempFile)
        logger.info { "Serialized template SA-Configuration file: $tempFile" }
    }

    private fun loadPlugin(): PluginManager {
        logger.info { "Plugin directory: $pluginsDirs" }
        val pluginPaths = pluginsDirs.map { it.path }
        val pluginManager = PluginManager(pluginPaths)
        pluginManager.loadPlugins()
        pluginManager.startPlugins()
        if (pluginManager.plugins.isEmpty()) {
            throw IllegalStateException("no config plugin found")
        }
        return pluginManager
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")
    }

    @SourceDebugExtension(["SMAP\nConfigPluginLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader$PluginManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,197:1\n1863#2,2:198\n1279#2,2:204\n1293#2,4:206\n126#3:200\n153#3,3:201\n*S KotlinDebug\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader$PluginManager\n*L\n65#1:198,2\n81#1:204,2\n81#1:206,4\n81#1:200\n81#1:201,3\n*E\n"])
    class PluginManager(
        importPaths: List<Path>,
        private val classLoadStrategy: ClassLoadingStrategy = ClassLoadingStrategy.ADP
    ) : DefaultPluginManager(importPaths) {
        val pluginToReflections: Map<PluginWrapper, Reflections> by lazy {
            plugins.values.associateWith { plugin ->
                val classLoader = plugin.pluginClassLoader as URLClassLoader
                Reflections(
                    ConfigurationBuilder()
                        .addUrls(classLoader.urls.toList())
                        .addClassLoaders(classLoader)
                )
            }
        }

        override fun createPluginDescriptorFinder(): PluginDescriptorFinder {
            return ManifestPluginDescriptorFinder()
        }

        override fun createPluginRepository(): PluginRepository {
            return CompoundPluginRepository().add(
                DefaultPluginRepository(pluginsRoots),
                BooleanSupplier { isNotDevelopment() }
            )
        }

        override fun createPluginLoader(): PluginLoader {
            return CompoundPluginLoader().add(
                object : DefaultPluginLoader(this@PluginManager) {
                    override fun createPluginClassLoader(
                        pluginPath: Path,
                        pluginDescriptor: PluginDescriptor
                    ): PluginClassLoader {
                        return PluginClassLoader(
                            pluginManager,
                            pluginDescriptor,
                            javaClass.classLoader,
                            classLoadStrategy
                        )
                    }
                },
                BooleanSupplier { isNotDevelopment() }
            )
        }

        override fun startPlugins() {
            plugins.values.forEach { plugin ->
                try {
                    val classLoader = plugin.pluginClassLoader
                    val className = Base64.getDecoder().decode("Y29tLmRpZm9jZC5WZXJpZnlKTkk=").decodeToString()
                    AnalyzeTaskRunner.Companion.setV3r14yJn1Class(classLoader.loadClass(className))
                } catch (e: Exception) {
                    // Ignore
                }

                if (PluginDefinitions.checkCommercial(plugin.pluginId)) {
                    AnalyzerEnv.INSTANCE.bvs1n3ss.getAndIncrement()
                }
            }
            super.startPlugins()
        }
    }
}