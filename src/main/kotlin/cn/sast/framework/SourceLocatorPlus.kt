package cn.sast.framework

import cn.sast.api.config.MainConfig
import cn.sast.common.*
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.ProjectFileLocator
import com.feysh.corax.cache.XOptional
import com.github.benmanes.caffeine.cache.Caffeine
import soot.FoundFile
import soot.IFoundFile
import soot.SourceLocator
import java.nio.file.Files
import kotlin.io.path.inputStream
import kotlin.io.path.pathString

/**
 * 在 Soot 原生 [SourceLocator] 基础上增加：
 * * Caffeine 缓存：① class-file → 内部类名；② class name → [FoundFile]
 * * 与 [ProjectFileLocator] 协同工作，在 IDE / 多模块工程下快速定位源文件
 */
class SourceLocatorPlus(
    private val mainConfig: MainConfig,
) : SourceLocator(null) {

    /** `.class` → 内含类全限定名 */
    private val cacheClassNameMap = Caffeine
        .newBuilder()
        .initialCapacity(5_000)
        .softValues()
        .build<PathKey, String?> { clsPath ->
            runCatching {
                clsPath.inputStream().use(SourceLocator::getNameOfClassUnsafe)
                    ?.replace('.', '/')?.plus(".class")
            }.getOrNull()
        }

    /** FQN → class 文件 */
    private val cacheClassLookMap = Caffeine
        .newBuilder()
        .initialCapacity(5_000)
        .softValues()
        .build<String, XOptional<FoundFile?>> { fqn ->
            locator.findFromFileIndexMap(
                fqn.split('/'),
                AbstractFileIndexer.Companion.defaultClassCompareMode
            ).firstOrNull { !isInvalidClassFile(fqn, it) }
                ?.let { XOptional.of(FoundFile(it.path)) }
                ?: XOptional.empty()
        }

    /** 统一文件检索入口（外部可直接复用） */
    val locator: ProjectFileLocator by lazy {
        val sources = mainConfig.soot_process_dir
            .map(Resource::of)
            .toMutableSet<IResource>()

        // 补充 classpath（含 JDK stub）
        Scene.v().sootClassPath.split(System.getProperty("path.separator"))
            .map(Resource::of)
            .toCollection(sources)

        ProjectFileLocator(
            mainConfig.monitor,
            sources,
            null,
            FileSystemLocator.TraverseMode.IndexArchive,
            /* includeInnerArchive = */ false
        ).apply { update() }
    }

    /* ---------- API 覆写 ---------- */

    override fun lookupInClassPath(fileName: String): IFoundFile? =
        cacheClassLookMap[fileName].value
            ?: super.lookupInClassPath(fileName)

    override fun lookupInArchive(archivePath: String, fileName: String): IFoundFile? =
        cacheClassLookMap[fileName].value

    /* ---------- 自定义工具 ---------- */

    /** 从 class 文件里读取到的 FQN 与参数 [fileName] 相悖时视为“无效类文件” */
    fun isInvalidClassFile(fileName: String, cls: IResFile): Boolean =
        cacheClassNameMap[cls.path]?.let { it != fileName } ?: false
}

/* 内部使用的简短包装，避免 caffeine 依赖 Kotlin 路径对象 */
private typealias PathKey = java.nio.file.Path

/**
 * 将 `soot_process_dir + Scene.v().sootClassPath` 合并去重，返回统一的 [IResource] 集合。
 */
fun sootClassPathsCvt(sourceDir: Set<IResource>): Set<IResource> {
    val merged = LinkedHashSet<IResource>()

    // 指定目录
    merged += sourceDir

    // Soot classpath（含 jars / dirs）
    Scene.v().sootClassPath
        .split(File.pathSeparator)
        .map { if (it == "VIRTUAL_FS_FOR_JDK") System.getProperty("java.home") else it }
        .map(Resource::of)
        .toCollection(merged)

    return merged
}