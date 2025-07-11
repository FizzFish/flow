package cn.sast.api.config

/* ────────────────────────────────────────────────────────────────────────────
 * Imports（保留原依赖，若无此库请在 build.gradle(.kts) 添加）
 * ─────────────────────────────────────────────────────────────────────────── */
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.util.IMonitor
import cn.sast.common.*
import cn.sast.common.FileSystemLocator.TraverseMode
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.rules.ProcessRule
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import soot.Scene
import java.io.File
import java.nio.charset.Charset
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.math.max
import kotlin.reflect.jvm.internal.impl.utils.CollectionsKt


/* ────────────────────────────────────────────────────────────────────────────
 * 1. Kotlin 数据类/配置类
 * ─────────────────────────────────────────────────────────────────────────── */
class MainConfig(
    /* —— CLI / 全局 —— */
    val sourceEncoding:         Charset                  = Charsets.UTF_8,
    var monitor:                IMonitor?                = null,
    var saConfig:               SaConfig?                = null,
    var output_dir:              IResDirectory            = Resource.dirOf("out/test-out"),
    val dumpSootScene: Boolean = false,
    val use_wrapper: Boolean = true,
    val hideNoSource: Boolean = false,
    var traverseMode:           TraverseMode             = TraverseMode.RecursivelyIndexArchive,
    var processDir:             PersistentSet<IResource> = persistentSetOf(),
    var classpath:              PersistentSet<String>    = persistentSetOf(),
    var sourcePath:             PersistentSet<IResource> = persistentSetOf(),
    var projectRoot:            PersistentSet<IResource> = persistentSetOf(),
    val autoAppClasses: PersistentSet<IResource> = persistentSetOf(),
    val autoAppTraverseMode: TraverseMode = TraverseMode.RecursivelyIndexArchive,
    val autoAppSrcInZipScheme: Boolean = true,
    var skipClass:      Boolean = true,
    var incrementAnalyze:      Boolean = false,
    var enableLineNumbers:      Boolean = true,
    var enableOriginalNames:    Boolean = true,
    var allowPhantomRefs:       Boolean = true,
    val output_format: Int = 14,
    val throw_analysis: Int = 3,
    val process_multiple_dex: Boolean = true,
    val appClasses: Set<String> = emptySet(),
    val src_precedence: SrcPrecedence = SrcPrecedence.prec_apk_class_jimple,
    val ecj_options: List<String> = emptyList(),
    val sunBootClassPath: String? = System.getProperty("sun.boot.class.path"),
    val javaExtDirs: String? = System.getProperty("java.ext.dirs"),
    val hashAbspathInPlist: Boolean = false,
    val deCompileIfNotExists: Boolean = true,
    val enableCodeMetrics: Boolean = true,
    val prepend_classpath: Boolean = false,
    val whole_program: Boolean = true,
    val no_bodies_for_excluded: Boolean = true,
    val allow_phantom_refs: Boolean = true,
    val enableReflection: Boolean = true,
    val staticFieldTrackingMode: StaticFieldTrackingMode = StaticFieldTrackingMode.ContextFlowSensitive,
    val callGraphAlgorithm: String = "insens",
    val callGraphAlgorithmBuiltIn: String = "cha",
    val memoryThreshold: Double = 0.9,
    var androidPlatformDir:     String?  = null,
    var appOnly: Boolean,
    var isAndroidScene: Boolean
) {

    /* ─────────────── 延迟派生字段 ─────────────── */
    val useDefaultJavaClassPath: Boolean = false
    val sqliteReportDb: IResFile
        get() = output_dir
            .resolve("sqlite")
            .resolve("sqlite_report_coraxjava.db")
            .toFile()

    /** 并行线程数：默认为 `CPU-1`，最少 1 */
    var parallelsNum: Int = max(OS.maxThreadNum - 1, 1)
        get() = if (field <= 0) OS.maxThreadNum else field
        set(value) { if (value > 0) field = value }

    /** 自动判断 `androidPlatformDir` 是否指向单独 jar */
    val forceAndroidJar: Boolean? by lazy {
        androidPlatformDir?.let { File(it).run {
            require(exists()) { "androidPlatformDir not exist: $it" }
            isFile
        } }
    }

    /* ======================== 功能方法 ======================== */

    /** 根据当前配置拼出最终 Soot class-path */
    fun sootClasspath(): Set<String> {
        val cp = buildSet {
            if (processDir.isNotEmpty()) addAll(
                processDir.flatMap { globPaths(it.toString()) ?: emptyList() }
                    .map { it.expandRes(output_dir).absolutePath }
            )
            if (classpath.isNotEmpty()) addAll(classpath)
            if (useDefaultJavaClassPath) {
                Scene.defaultJavaClassPath().takeIf { it.isNotEmpty() }
                    ?.split(File.pathSeparatorChar)
                    ?.let { addAll(it) }
            }
        }
        return skipInvalidClassPaths(cp)
    }

    fun getAndroidJarClasspath(targetAPKFile: String): String? {

        val androidJar = if (forceAndroidJar == true) {
            androidPlatformDir
        } else {
            Scene.v().getAndroidJarPath(androidPlatformDir, targetAPKFile)
        }

        return androidJar
    }

    /** 类路径过滤：排除损坏 zip / jar */
    private fun skipInvalidClassPaths(paths: Collection<String>): Set<String> = buildSet {
        for (p in paths) {
            if (isSkipClassSource(p)) continue
            val res = Resource.of(p)
            if (res.isFile && res.zipLike) {
                runCatching { Resource.getEntriesUnsafe(res.path) }
                    .onSuccess { add(p) }
                    .onFailure { e -> logger.error { "Skip invalid archive $p: ${e.message}" } }
            } else {
                add(p)
            }
        }
    }

    fun isEnable(type: CheckType): Boolean {
        val saConfig = this.saConfig
        return saConfig != null && !saConfig.isEnable(type)
    }
    /** 判断 classPath 是否在过滤规则中 */
    fun isSkipClassSource(path: String): Boolean =
        runCatching { isSkipClassSource(Path.of(path)) }.getOrDefault(false)

    fun isSkipClassSource(path: Path): Boolean =
        scanFilter.getActionOfClassPath("Process", path, null) == ProcessRule.ScanAction.Skip

    /* —— 相对路径转换工具 —— */

    fun tryGetRelativePath(p: IResource): RelativePath {
        return tryGetRelativePathFromAbsolutePath(p.absolute.normalize.toString())
    }

    fun tryGetRelativePath(p: Path): RelativePath {
        return tryGetRelativePath(Resource.of(p))
    }

    fun tryGetRelativePathFromAbsolutePath(abs: String): RelativePath =
        allResourcePathNormalized
            .firstOrNull { abs.startsWith(it) }
            ?.let { src -> RelativePath(src, buildRelative(src, abs)) }
            ?: RelativePath("", abs.replace("\\", "/"))

    private fun buildRelative(src: String, path: String): String =
        path.substring(src.length)
            .replace("\\", "/")
            .replace("//", "/")
            .let { if (it.startsWith('/')) it else "/$it" }

    /* ======================== 初始化 / 派生 ======================== */

    /** 根路径列表，用于相对路径推导（由外部注入） */
    var rootPathsForConvertRelativePath: List<IResource> = emptyList()
        set(value) {
            field = value
            allResourcePathNormalized = value
                .filter { it.isDirectory || it.zipLike }
                .map { it.absolute.normalize.toString() }
                .distinct()
        }

    private var allResourcePathNormalized: List<String> = emptyList()
    var scanFilter: ScanFilter = ScanFilter(this)

    init {
        setSourcePath(sourcePath)
    }

    private fun setSourcePath(value: PersistentSet<IResource>) {
        sourcePath = value
        sourcePathZFS = value.mapNotNull { res ->
            if (!res.zipLike) null else runCatching {
                Resource.getZipFileSystem(res.path)
            }.getOrNull()
        }.toPersistentSet()
    }

    var sourcePathZFS: PersistentSet<FileSystem> = persistentSetOf()
        private set

    /* ======================== 日志 / 校验 ======================== */

    fun validate() {
        require(classpath.none { it.isEmpty() }) { "classpath 有空字符串" }
        require(processDir.none { it.toString().isEmpty() }) { "processDir 有空字符串" }
        require(sourcePath.none { it.toString().isEmpty() }) { "sourcePath 有空字符串" }
    }

    fun getSoot_process_dir(): Set<String> {
        val ret: MutableSet<String> = HashSet()
        for(res in this.processDir + this.autoAppClasses) {
            ret.add(res.expandRes(this.output_dir).absolutePath)
        }
        return ret
    }


    /* ======================== 嵌套数据类 ======================== */

    data class RelativePath(override val prefix: String, override val relativePath: String) : IRelativePath {
        val identifier: String get() = "${short(prefix)}$relativePath"
        val absoluteNormalizePath: String get() = "$prefix$relativePath"

        companion object {
            private fun short(prefix: String): String =
                prefix.substringAfterLast('/', prefix)
        }
    }

    /* ======================== 静态成员 ======================== */

    companion object {
        const val CHECKER_INFO_CSV = "checker_info.csv"
        const val RULE_SORT_YAML   = "rule_sort.yaml"
        var excludeFiles: Set<String> = setOf("classes-header.jar", "classes-turbine.jar")

        /** 优先语言顺序 */
        var preferredLanguages: List<Language> = listOf(Language.ZH, Language.EN)
            set(value) { field = if (value.isEmpty()) listOf(Language.ZH, Language.EN) else value }

        /* ——— 日志 & YAML 工具由项目注入 ——— */
        val logger: KLogger = TODO("Inject mu.KotlinLogging logger")
        val yaml:   Yaml    = TODO("Inject kaml.Yaml instance")

        /** 给 Java 调用的辅助封装 */
        @JvmStatic fun checkerInfoDir(
            configDirs: List<IResource>,
            stopOnError: Boolean = true
        ): IResDirectory? = when (configDirs.size) {
            1 -> {
                val dir = configDirs.single().path.normalize()
                if (!Files.exists(dir)) {
                    if (stopOnError) error("$dir 不存在") else null
                } else {
                    val checkerInfoDir = dir.takeIf {
                        Files.exists(it.resolve(CHECKER_INFO_CSV))
                    } ?: dir.resolve("../../Canary/analysis-config").normalize()
                    if (Files.exists(checkerInfoDir.resolve(CHECKER_INFO_CSV)))
                        Resource.dirOf(checkerInfoDir) else {
                        if (stopOnError) error("在 $dir 未找到 $CHECKER_INFO_CSV")
                        else null
                    }
                }
            }
            else -> if (stopOnError) error("只能存在一个插件目录: $configDirs") else null
        }
    }
}

/* ────────────────────────────────────────────────────────────────────────────
 * 2. 扩展函数 (原 MainConfigKt.kt)
 * ─────────────────────────────────────────────────────────────────────────── */
/* ────────────────────────────────────────────────────────────────────────────
 * 1. 全局 YAML / 序列化配置
 * ------------------------------------------------------------------------- */

/** 若需要多态注册，可在此 `SerializersModule { }` 区块里补充 */
val serializersModule: SerializersModule = SerializersModule { }

val yamlConfiguration = YamlConfiguration(
    encodeDefaults = true,
    polymorphismStyle = PolymorphismStyle.Tag,
    sequenceStyle   = SequenceStyle.Block
)

/** 统一的 YAML 实例；供全项目复用 */
val yamlFormat: Yaml = Yaml(
    serializersModule = serializersModule,
    configuration     = yamlConfiguration
)

/* ────────────────────────────────────────────────────────────────────────────
 * 2. MainConfig 扩展工具
 * ------------------------------------------------------------------------- */

/** 增量分析：简单声明依赖 */
fun MainConfig.simpleIAnalysisDepends(): IAnalysisDepends =
    (incrementAnalyze as? IncrementalAnalyzeByChangeFiles)
        ?.simpleDeclAnalysisDependsGraph
        ?: PhantomAnalysisDepends

/** 增量分析：跨过程依赖 */
fun MainConfig.interProceduralAnalysisDepends(): IAnalysisDepends =
    (incrementAnalyze as? IncrementalAnalyzeByChangeFiles)
        ?.interProceduralAnalysisDependsGraph
        ?: PhantomAnalysisDepends

/**
 * 判断归档资源是否应跳过（已列入 sourcePathZFS 时不过滤）
 */
fun MainConfig.skipResourceInArchive(res: IResource): Boolean =
    !res.isJarScheme || runCatching {
        !sourcePathZFS.contains(Resource.getZipFileSystem(res.schemePath))
    }.getOrDefault(true)

/* ────────────────────────────────────────────────────────────────────────────
 * 3. 检查器配置目录探测
 * ------------------------------------------------------------------------- */

private const val CHECKER_INFO_CSV = "checker_info.csv"

/**
 * 根据插件目录列表解析出真正含有 `checker_info.csv` 的目录。
 *
 * @param stopOnError 为 `false` 时，遇到异常场景返回 `null` 而非抛错
 */
fun checkerInfoDir(
    configDirs: List<IResource>,
    stopOnError: Boolean = true
): IResDirectory? {
    if (configDirs.size != 1) {
        if (stopOnError) error("Only one plugin folder could exist: $configDirs") else return null
    }

    val analysisConfigDir = configDirs.single().path.normalize()
    if (!Files.exists(analysisConfigDir)) {
        if (stopOnError) error("$analysisConfigDir does not exist") else return null
    }

    val linkOpts = emptyArray<LinkOption>()
    fun Path.hasCheckerInfo() =
        Files.exists(this, *linkOpts) && Files.exists(resolve(CHECKER_INFO_CSV), *linkOpts)

    // ① 首选插件自身目录
    val primary: Path? = analysisConfigDir.takeIf { it.hasCheckerInfo() }

    // ② 退回到 Canary/analysis-config
    val fallback = analysisConfigDir
        .resolve("../../Canary/analysis-config")
        .normalize()
        .takeIf { it.hasCheckerInfo() }

    val resolved = primary ?: fallback
    return when {
        resolved != null     -> Resource.dirOf(resolved)
        stopOnError          -> error("$CHECKER_INFO_CSV not found under $analysisConfigDir")
        else                 -> null
    }
}