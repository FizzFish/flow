package cn.sast.api.config

import cn.sast.api.incremental.IncrementalAnalyze
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.util.IMonitor
import cn.sast.common.FileSystemLocator
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.OS
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.common.FileSystemLocator.TraverseMode
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IRelativePath
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.rules.ProcessRule
import java.io.File
import java.nio.charset.Charset
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.LinkedHashSet
import java.util.Locale
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import mu.KLogger
import soot.Scene

@SourceDebugExtension(["SMAP\nMainConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,463:1\n1611#2,9:464\n1863#2:473\n1864#2:475\n1620#2:476\n774#2:477\n865#2,2:478\n1557#2:480\n1628#2,3:481\n1619#2:484\n1863#2:485\n1864#2:488\n1620#2:489\n1454#2,5:490\n1628#2,3:495\n1557#2:499\n1628#2,3:500\n1755#2,3:503\n1755#2,3:506\n1755#2,3:509\n774#2:514\n865#2,2:515\n774#2:517\n865#2,2:518\n1#3:474\n1#3:486\n1#3:487\n1#3:498\n12574#4,2:512\n*S KotlinDebug\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfig\n*L\n182#1:464,9\n182#1:473\n182#1:475\n182#1:476\n197#1:477\n197#1:478,2\n197#1:480\n197#1:481,3\n224#1:484\n224#1:485\n224#1:488\n224#1:489\n262#1:490,5\n270#1:495,3\n303#1:499\n303#1:500,3\n342#1:503,3\n343#1:506,3\n344#1:509,3\n405#1:514\n405#1:515,2\n412#1:517\n412#1:518,2\n182#1:474\n224#1:487\n352#1:512,2\n*E\n"])
class MainConfig(
    sourceEncoding: Charset = Charsets.UTF_8,
    monitor: IMonitor? = null,
    saConfig: SaConfig? = null,
    output_dir: IResDirectory = Resource.INSTANCE.dirOf("out/test-out"),
    dumpSootScene: Boolean = false,
    androidPlatformDir: String? = null,
    use_wrapper: Boolean = true,
    hideNoSource: Boolean = false,
    traverseMode: TraverseMode = FileSystemLocator.TraverseMode.RecursivelyIndexArchive,
    useDefaultJavaClassPath: Boolean = true,
    processDir: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
    classpath: PersistentSet<String> = ExtensionsKt.persistentSetOf(),
    sourcePath: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
    projectRoot: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
    autoAppClasses: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
    autoAppTraverseMode: TraverseMode = FileSystemLocator.TraverseMode.RecursivelyIndexArchive,
    autoAppSrcInZipScheme: Boolean = true,
    skipClass: Boolean = false,
    incrementAnalyze: IncrementalAnalyze? = null,
    enableLineNumbers: Boolean = true,
    enableOriginalNames: Boolean = true,
    output_format: Int = 14,
    throw_analysis: Int = 3,
    process_multiple_dex: Boolean = true,
    appClasses: Set<String> = emptySet(),
    src_precedence: SrcPrecedence = SrcPrecedence.prec_apk_class_jimple,
    ecj_options: List<String> = emptyList(),
    sunBootClassPath: String? = System.getProperty("sun.boot.class.path"),
    javaExtDirs: String? = System.getProperty("java.ext.dirs"),
    hashAbspathInPlist: Boolean = false,
    deCompileIfNotExists: Boolean = true,
    enableCodeMetrics: Boolean = true,
    prepend_classpath: Boolean = false,
    whole_program: Boolean = true,
    no_bodies_for_excluded: Boolean = true,
    allow_phantom_refs: Boolean = true,
    enableReflection: Boolean = true,
    staticFieldTrackingMode: StaticFieldTrackingMode = StaticFieldTrackingMode.ContextFlowSensitive,
    callGraphAlgorithm: String = "insens",
    callGraphAlgorithmBuiltIn: String = "cha",
    memoryThreshold: Double = 0.9
) {
    val sourceEncoding: Charset = sourceEncoding
    var monitor: IMonitor? = monitor
        internal set
    var saConfig: SaConfig? = saConfig
        internal set
    var output_dir: IResDirectory = output_dir
        internal set
    var dumpSootScene: Boolean = dumpSootScene
        internal set
    var use_wrapper: Boolean = use_wrapper
        internal set
    var hideNoSource: Boolean = hideNoSource
        internal set
    var traverseMode: TraverseMode = traverseMode
        internal set
    var useDefaultJavaClassPath: Boolean = useDefaultJavaClassPath
        internal set
    var processDir: PersistentSet<IResource> = processDir
        internal set
    var classpath: PersistentSet<String> = classpath
        internal set
    var projectRoot: PersistentSet<IResource> = projectRoot
        internal set
    var autoAppClasses: PersistentSet<IResource> = autoAppClasses
        internal set
    var autoAppTraverseMode: TraverseMode = autoAppTraverseMode
        internal set
    var autoAppSrcInZipScheme: Boolean = autoAppSrcInZipScheme
        internal set
    var skipClass: Boolean = skipClass
        internal set
    var incrementAnalyze: IncrementalAnalyze? = incrementAnalyze
        internal set
    var enableLineNumbers: Boolean = enableLineNumbers
        internal set
    var enableOriginalNames: Boolean = enableOriginalNames
        internal set
    var output_format: Int = output_format
        internal set
    var throw_analysis: Int = throw_analysis
        internal set
    var process_multiple_dex: Boolean = process_multiple_dex
        internal set
    var appClasses: Set<String> = appClasses
        internal set
    var src_precedence: SrcPrecedence = src_precedence
        internal set
    var ecj_options: List<String> = ecj_options
        internal set
    var sunBootClassPath: String? = sunBootClassPath
        internal set
    var javaExtDirs: String? = javaExtDirs
        internal set
    var hashAbspathInPlist: Boolean = hashAbspathInPlist
        internal set
    var deCompileIfNotExists: Boolean = deCompileIfNotExists
        internal set
    var enableCodeMetrics: Boolean = enableCodeMetrics
        internal set
    var prepend_classpath: Boolean = prepend_classpath
        internal set
    var whole_program: Boolean = whole_program
        internal set
    var no_bodies_for_excluded: Boolean = no_bodies_for_excluded
        internal set
    var allow_phantom_refs: Boolean = allow_phantom_refs
        internal set
    var enableReflection: Boolean = enableReflection
        internal set
    var staticFieldTrackingMode: StaticFieldTrackingMode = staticFieldTrackingMode
        internal set
    var memoryThreshold: Double = memoryThreshold
        internal set
    var version: String? = null
        internal set
    var checkerInfo: Lazy<CheckerInfoGenResult?>? = null
        internal set

    val configDirs: MutableList<IResource> = ArrayList()
    val checkerInfoDir: IResDirectory?
        get() = MainConfigKt.checkerInfoDir(configDirs, false)

    val checker_info_csv: IResFile?
        get() {
            val dir = checkerInfoDir
            return dir?.resolve("checker_info.csv")?.toFile()
        }

    val rule_sort_yaml: IResFile?
        get() {
            val dir = checkerInfoDir
            return dir?.resolve("rule_sort.yaml")?.toFile()
        }

    var apponly: Boolean = false
        internal set

    var sourcePath: PersistentSet<IResource> = sourcePath
        internal set(value) {
            field = value
            sourcePathZFS = value.mapNotNull {
                if (!it.zipLike) null else try {
                    Resource.INSTANCE.getZipFileSystem(it.path)
                } catch (e: Exception) {
                    null
                }
            }.toPersistentSet()
        }

    var sourcePathZFS: PersistentSet<FileSystem> = ExtensionsKt.persistentSetOf()
        private set

    var rootPathsForConvertRelativePath: List<IResource> = emptyList()
        internal set(value) {
            field = value
            allResourcePathNormalized = value.filter {
                it.isDirectory() || it.zipLike
            }.map {
                it.absolute.normalize.toString()
            }.distinct()
        }

    var allResourcePathNormalized: List<String> = emptyList()
        internal set

    val sqlite_report_db: IResFile
        get() = output_dir.resolve("sqlite").resolve("sqlite_report_coraxjava.db").toFile()

    var callGraphAlgorithm: String = callGraphAlgorithm
        internal set(value) {
            field = value.toLowerCase(Locale.getDefault())
        }

    var callGraphAlgorithmBuiltIn: String = callGraphAlgorithmBuiltIn
        internal set(value) {
            field = value.toLowerCase(Locale.getDefault())
        }

    var isAndroidScene: Boolean? = false
        internal set

    var parallelsNum: Int = maxOf(OS.INSTANCE.maxThreadNum - 1, 1)
        get() = if (field <= 0) OS.INSTANCE.maxThreadNum else field
        set(value) {
            if (value > 0) {
                field = value
            }
        }

    private val forceAndroidJar$delegate = lazy { forceAndroidJar_delegate$lambda$12(this) }
    val forceAndroidJar: Boolean?
        get() = forceAndroidJar$delegate.value

    var androidPlatformDir: String? = androidPlatformDir
        get() = if (field == "ANDROID_JARS") System.getenv("ANDROID_JARS") else field
        set(value) {
            field = value
            if (value != null && value.isEmpty()) {
                throw IllegalStateException("Check failed.")
            }
        }

    var scanFilter: ScanFilter = ScanFilter(this)
        internal set

    var projectConfig: ProjectConfig = ProjectConfig()
        internal set(value) {
            field = value
            scanFilter.update(value)
        }

    init {
        setSourcePath(sourcePath)
        scanFilter.update(projectConfig)
    }

    fun skipInvalidClassPaths(paths: Collection<String>): Set<String> {
        return paths.filterTo(LinkedHashSet()) { path ->
            if (isSkipClassSource(path)) {
                logger.info("Exclude class path: $path")
                false
            } else {
                try {
                    val resource = Resource.INSTANCE.of(path)
                    if (!resource.isFile || !resource.zipLike) {
                        true
                    } else {
                        try {
                            Resource.INSTANCE.getEntriesUnsafe(resource.path)
                            true
                        } catch (e: Exception) {
                            logger.error("skip the invalid archive file: $resource e: ${e.message}")
                            false
                        }
                    }
                } catch (e: Exception) {
                    true
                }
            }
        }
    }

    fun get_expand_class_path(): Set<IResource> {
        return if (!apponly) {
            classpath.flatMap { path ->
                ResourceImplKt.globPaths(path) ?: throw IllegalStateException("classpath option: \"$path\" is invalid or target not exists")
            }.toSet()
        } else {
            emptySet()
        }
    }

    fun get_soot_classpath(): Set<String> {
        val javaClasspath = get_expand_class_path().map { it.expandRes(output_dir).absolutePath }.toMutableSet()
        if (useDefaultJavaClassPath) {
            val defaultPath = Scene.defaultJavaClassPath()
            if (defaultPath.isNotEmpty()) {
                javaClasspath.addAll(defaultPath.split(File.pathSeparatorChar))
            }
        }
        return skipInvalidClassPaths(javaClasspath)
    }

    fun getAndroidJarClasspath(targetAPKFile: String): String? {
        return forceAndroidJar?.let { force ->
            val androidJar = if (force) {
                androidPlatformDir
            } else {
                Scene.v().getAndroidJarPath(androidPlatformDir, targetAPKFile)
            }
            if (androidJar.isNullOrEmpty()) {
                throw IllegalArgumentException("Failed requirement.")
            }
            androidJar
        }
    }

    fun isSkipClassSource(path: Path): Boolean {
        return scanFilter.getActionOfClassPath("Process", path, null) === ProcessRule.ScanAction.Skip
    }

    fun isSkipClassSource(path: String): Boolean {
        return try {
            val normalizedPath = Path.of(path).takeIf { Files.exists(it) } ?: return false
            isSkipClassSource(normalizedPath)
        } catch (e: Exception) {
            false
        }
    }

    fun validate() {
        if (classpath.any { it.isEmpty() }) {
            throw IllegalStateException("classpath has empty string")
        }
        if (processDir.any { it.toString().isEmpty() }) {
            throw IllegalStateException("processDir has empty string")
        }
        if (sourcePath.any { it.toString().isEmpty() }) {
            throw IllegalStateException("sourcePath has empty string")
        }
    }

    fun isEnable(type: CheckType): Boolean {
        return saConfig?.isEnable(type) ?: true
    }

    fun isAnyEnable(vararg type: CheckType): Boolean {
        return type.any { isEnable(it) }
    }

    private fun getRelative(src: String, path: String): String {
        var relative = path.substring(src.length)
            .replace("\\", "/")
            .replace("//", "/")
        
        return when {
            relative.startsWith("/") -> relative
            relative.startsWith("!/") -> relative.substring(1)
            else -> "/$relative"
        }
    }

    fun tryGetRelativePathFromAbsolutePath(abs: String): RelativePath {
        allResourcePathNormalized.forEach { src ->
            if (abs.startsWith(src)) {
                val normalizedSrc = src.replace("\\", "/").replace("//", "/").removeSuffix("/")
                return RelativePath(normalizedSrc, getRelative(src, abs))
            }
        }
        return RelativePath("", abs.replace("\\", "/").replace("//", "/"))
    }

    fun tryGetRelativePathFromAbsolutePath(abs: IResource): RelativePath {
        return tryGetRelativePathFromAbsolutePath(abs.toString())
    }

    fun tryGetRelativePath(p: IResource): RelativePath {
        return tryGetRelativePathFromAbsolutePath(p.absolute.normalize)
    }

    fun tryGetRelativePath(p: Path): RelativePath {
        return tryGetRelativePath(Resource.INSTANCE.of(p))
    }

    fun <E : Any> simpleDeclIncrementalAnalysisFilter(targets: Collection<E>): Collection<E> {
        return (incrementAnalyze as? IncrementalAnalyzeByChangeFiles)
            ?.simpleDeclAnalysisDependsGraph
            ?.let { graph ->
                targets.filter { graph.shouldReAnalyzeTarget(it) != ProcessRule.ScanAction.Skip }
            } ?: targets
    }

    fun <E : Any> InterProceduralIncrementalAnalysisFilter(targets: Collection<E>): Collection<E> {
        return (incrementAnalyze as? IncrementalAnalyzeByChangeFiles)
            ?.interProceduralAnalysisDependsGraph
            ?.let { graph ->
                targets.filter { graph.shouldReAnalyzeTarget(it) != ProcessRule.ScanAction.Skip }
            } ?: targets
    }

    companion object {
        const val CHECKER_INFO_CSV_NAME = "checker_info.csv"
        const val RULE_SORT_YAML = "rule_sort.yaml"

        var preferredLanguages: List<Language> = listOf(Language.ZH, Language.EN)
            internal set(value) {
                field = if (value.isEmpty()) listOf(Language.ZH, Language.EN) else value
            }

        private val logger: KLogger = TODO("Initialize logger")
        val excludeFiles: Set<String> = emptySet()

        @JvmStatic
        fun forceAndroidJar_delegate$lambda$12(config: MainConfig): Boolean? {
            val platformDir = config.androidPlatformDir ?: return null
            if (platformDir.isEmpty()) {
                throw RuntimeException("Android platform directory is empty")
            }
            val file = File(platformDir)
            if (!file.exists()) {
                throw IllegalArgumentException("androidPlatformDir not exist")
            }
            return file.isFile
        }
    }

    data class RelativePath(
        val prefix: String,
        val relativePath: String
    ) : IRelativePath {
        val identifier: String
            get() = "${short(prefix)}$relativePath"

        val absoluteNormalizePath: String
            get() = "$prefix$relativePath"

        init {
            if (prefix.isNotEmpty()) {
                prefixes.add(prefix)
            }
        }

        companion object {
            val prefixes: MutableSet<String> = Collections.synchronizedSet(LinkedHashSet())

            fun short(prefix: String): String {
                val lastSeparator = prefix.lastIndexOfAny(listOf("/", "\\"))
                return if (lastSeparator == -1) prefix else prefix.substring(lastSeparator + 1)
            }
        }
    }
}