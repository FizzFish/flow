@file:Suppress("UnstableApiUsage")

package cn.sast.framework.plugin

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.config.SaConfig
import cn.sast.common.*
import cn.sast.framework.AnalyzeTaskRunner
import cn.sast.framework.plugin.CheckersConfig.CheckerFilterByName
import cn.sast.framework.plugin.PluginDefinitions.Definition
import com.feysh.corax.config.api.IConfigPluginExtension
import kotlinx.collections.immutable.toImmutableSet
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

/**
 * 加载并解析 *SA-Config 插件* 与 *SA-Configuration YAML* 的统一入口。
 *
 * @param configDirs  存放 `sa-configuration.yml`（或其衍生文件）的目录集合
 * @param pluginsDirs PF4J 插件根目录集合
 */
class ConfigPluginLoader(
    private val configDirs: List<IResource>,
    private val pluginsDirs: List<IResDirectory>,
) {

    /** 按需延迟初始化的插件管理器 */
    private val pluginManager: PluginManager by lazy(::loadPlugin)

    /** 聚合所有插件自定义序列化器的模块 */
    private val serializersModule: SerializersModule by lazy {
        PluginDefinitions.serializersModule(pluginManager)
    }

    /* ---------- 对外 API ---------- */

    /**
     * 根据 *插件 ID*（可选）与 *配置名称*（可选）选出唯一的 [IConfigPluginExtension]，生成 [SaConfig]。
     *
     * - 若 `pluginId == null` 则在 *所有已加载插件* 里搜索；
     * - 若 `name == null` 但命中多个扩展，则会抛出异常提示你手动指定。
     */
    fun loadFromName(pluginId: String? = null, name: String? = null): SaConfig {
        val extensions = getConfigExtensions(pluginId)
        require(extensions.isNotEmpty()) {
            "No IConfigPluginExtension found in pluginsDir(s)=$pluginsDirs"
        }

        val chosen = when {
            name == null && extensions.size == 1 -> extensions.first()
            name == null ->
                error(
                    "Multiple configs detected, please specify one of: ${
                        extensions.joinToString { it.name }
                    }"
                )

            else -> {
                val matched = extensions.filter { it.name == name }
                require(matched.size == 1) {
                    "Config name “$name” not found or duplicated in pluginsDir(s)=$pluginsDirs"
                }
                matched.first()
            }
        }

        logger.info { "Using config extension: ${chosen.name}" }

        return SaConfig(
            checkers = chosen.units.toImmutableSet(),
            sootConfig = chosen.sootConfig,
            builtinAnalysisConfig = null,
            preAnalysisConfig = null,
            enableCheckTypes = null
        )
    }

    /**
     * 读取 *YAML* 并合并插件默认定义，返回最终 [SaConfig]。
     * 若 YAML 文件需要 *规范化* 或 *已补全默认值*，则在同目录写出 `*.normalize.yml`。
     */
    fun searchCheckerUnits(
        ymlConfig: IResFile,
        checkerFilter: CheckerFilterByName? = null,
    ): SaConfig {
        val config = SAConfiguration.deserialize(serializersModule, ymlConfig)

        val needNormalize = config.sort()
        val defs = PluginDefinitions.load(pluginManager)
        val hasChange = config.supplementAndMerge(defs, ymlConfig.toString())

        if ((needNormalize || hasChange) && ymlConfig.parent != null) {
            val normalized = ymlConfig.parent.resolve(
                ymlConfig.nameWithoutExtension + ".normalize.yml"
            ).toFile()
            config.sort()
            config.serialize(serializersModule, normalized)
            logger.info { "Serialized normalized SA-Configuration: $normalized" }
        }

        return config.filter(defs, checkerFilter)
    }

    /** 生成一份空模板并写出至 [tempFile]。 */
    fun makeTemplateYml(tempFile: IResFile) {
        val defs = PluginDefinitions.load(pluginManager)
        SAConfiguration()
            .apply {
                supplementAndMerge(defs, null)
                sort()
                serialize(serializersModule, tempFile)
            }
        logger.info { "Serialized template SA-Configuration: $tempFile" }
    }

    /* ---------- 私有实现 ---------- */

    private fun getConfigExtensions(
        pluginId: String? = null,
    ): List<IConfigPluginExtension> = when (pluginId) {
        null -> pluginManager.getExtensions(IConfigPluginExtension::class.java)
        else -> pluginManager.getExtensions(IConfigPluginExtension::class.java, pluginId)
    }.also {
        logger.info { "Found ${it.size} IConfigPluginExtension(s)" }
    }

    /** 执行 PF4J 插件加载与启动。 */
    private fun loadPlugin(): PluginManager {
        logger.info { "Plugin directories: $pluginsDirs" }

        val roots = pluginsDirs.map { it.path }
        return PluginManager(roots).apply {
            loadPlugins()
            startPlugins()
            require(plugins.isNotEmpty()) { "No config plugins found under $pluginsDirs" }
        }
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }

    /* ---------- 嵌套 PluginManager ---------- */

    /**
     * 自定义 PF4J `PluginManager`，在启动插件时：
     * 1. 将商业插件计数写入 [AnalyzerEnv];
     * 2. 根据策略选择不同的 `ClassLoader` 实现；
     * 3. 提供 `pluginToReflections` 以供插件元数据扫描。
     */
    class PluginManager(
        importPaths: List<Path>,
        private val classLoadStrategy: ClassLoadingStrategy = ClassLoadingStrategy.ADP,
    ) : DefaultPluginManager(importPaths) {

        /** plugin -> [Reflections] 映射，延迟创建 */
        val pluginToReflections: Map<PluginWrapper, Reflections> by lazy {
            plugins.values.associateWith { wrapper ->
                val cl = wrapper.pluginClassLoader as URLClassLoader
                Reflections(
                    ConfigurationBuilder()
                        .addUrls(*cl.urLs)
                        .addClassLoaders(cl)
                )
            }
        }

        override fun createPluginDescriptorFinder(): PluginDescriptorFinder =
            ManifestPluginDescriptorFinder()

        override fun createPluginRepository(): PluginRepository =
            CompoundPluginRepository()
                .add(DefaultPluginRepository(pluginsRoots)) { isNotDevelopment }
                // 额外策略按需添加
                .also { /* hook if needed */ }

        override fun createPluginLoader(): PluginLoader =
            CompoundPluginLoader()
                .add(
                    object : DefaultPluginLoader(this) {
                        override fun createPluginClassLoader(
                            pluginPath: Path,
                            pluginDescriptor: PluginDescriptor,
                        ): PluginClassLoader = PluginClassLoader(
                            this@PluginManager,
                            pluginDescriptor,
                            javaClass.classLoader,
                            classLoadStrategy,
                        )
                    }
                ) { isNotDevelopment }

        /** 非开发环境（PF4J 内置实现）。 */
        private val isNotDevelopment: Boolean
            get() = DevelopmentMode.isDisabled

        override fun startPlugins() {
            // 1) 计商业插件
            plugins.values.forEach { wrapper ->
                try {
                    val verifyCls = Base64.getDecoder().decode("Y29tLmRpZm9jZC5WZXJpZnlKTkk=")
                        .decodeToString()
                    AnalyzeTaskRunner.setV3r14yJn1Class(
                        wrapper.pluginClassLoader.loadClass(verifyCls)
                    )
                } catch (_: Exception) { /** 忽略验证失败 **/ }

                if (PluginDefinitions.checkCommercial(wrapper.pluginId)) {
                    AnalyzerEnv.bvs1n3ss.incrementAndGet()
                }
            }
            // 2) 正常启动
            super.startPlugins()
        }
    }
}
