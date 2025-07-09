package cn.sast.framework.report

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.FileResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.util.IMonitor
import cn.sast.common.*
import cn.sast.common.FileSystemLocator.TraverseMode
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.io.IOException
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists

/**
 * 根据逻辑路径查真实文件；内部用 [FileSystemCacheLocator] 做索引缓存。
 */
class ProjectFileLocator(
    private val monitor: IMonitor? = null,
    override var sourceDir: Set<IResource>,
    private val fileWrapperOutputDir: IResDirectory?,
    private var traverseMode: TraverseMode,
    private val enableInfo: Boolean = true,
) : IProjectFileLocator {

    /* ----------------------------- cache ------------------------------ */

    private val cache: AsyncCache<Pair<IBugResInfo, IWrapperFileGenerator>, IResFile?> =
        Caffeine.newBuilder()
            .maximumSize(8_000)
            .softValues()
            .buildAsync { (info, wrapper), _ ->
                when (info) {
                    is ClassResInfo -> get(info, wrapper)
                    is FileResInfo  -> get(info,  wrapper)
                    else            -> null
                }
            }

    /* -------------------------- index 扫描 ---------------------------- */

    /** 最新索引的异步任务 */
    private var updateJob: Deferred<FileIndexer>? = null

    /** 创建 / 更新索引 */
    override fun update() {
        if (updateJob != null) error("update() should be called once per locator lifetime")
        updateJob = GlobalScope.async(Dispatchers.Default) {
            FileSystemCacheLocator
                .getIndexer(sourceDir, traverseMode)
                .build()
        }.also { it.start() }
    }

    /** suspend 取索引 */
    private suspend fun indexer(): FileIndexer =
        updateJob?.await() ?: error("call update() first")

    /** 阻塞取索引 */
    private fun indexerBlocking(): FileIndexer =
        runBlocking { indexer() }

    /* ---------------------------- 查询接口 --------------------------- */

    override fun findFromFileIndexMap(
        parentSubPath: List<String>,
        mode: CompareMode,
    ): Sequence<IResFile> =
        indexerBlocking().findFromFileIndexMap(parentSubPath, mode)

    override suspend fun getByFileExtension(ext: String): Sequence<IResFile> =
        indexer().getPathsByExtension(ext).asSequence()

    override suspend fun getByFileName(filename: String): Sequence<IResFile> =
        indexer().findFromFileIndexMap(filename.split('/'))

    override suspend fun getAllFiles(): Sequence<IResFile> =
        indexer().run {
            sequence {
                extensionToPathMap.values.forEach { yieldAll(it) }
            }
        }

    /* -------------------- 单文件定位（带 wrapper） -------------------- */

    override fun get(
        resInfo: IBugResInfo,
        fileWrapperIfNotExists: IWrapperFileGenerator,
    ): IResFile? =
        cache.get(Tuples.pair(resInfo, fileWrapperIfNotExists)).get()

    /* ------------------------- 统计信息 ------------------------------ */

    fun totalFiles(): Long =
        indexerBlocking().count

    fun totalJavaSrcFiles(): Long =
        ResourceKt.javaExtensions.sumOf { ext ->
            indexerBlocking().getPathsByExtension(ext).size
        }.toLong()

    /* -------------------- 内部获取（无 cache） ------------------------ */

    private fun get(
        resInfo: ClassResInfo,
        wrapper: IWrapperFileGenerator,
    ): IResFile? =
        indexerBlocking()
            .findAnyFile(resInfo.sourceFile, AbstractFileIndexer.defaultClassCompareMode)
            ?: makeWrapperFile(resInfo, wrapper)

    private fun get(
        resInfo: FileResInfo,
        wrapper: IWrapperFileGenerator,
    ): IResFile? =
        when {
            resInfo.sourcePath.exists() -> resInfo.sourcePath
            else                        -> makeWrapperFile(resInfo, wrapper)
        }

    private fun makeWrapperFile(
        resInfo: IBugResInfo,
        wrapper: IWrapperFileGenerator,
    ): IResFile? =
        fileWrapperOutputDir?.let { wrapper.makeWrapperFile(it, resInfo) }

    /* ----------------------- static helpers -------------------------- */

    companion object {
        private val logger = KotlinLogging.logger {}

        /**
         * Best-effort 查找 `src.zip` 或 `lib/src.zip`，验证内部是否含 `java/lang/Object.java`。
         */
        fun findJdkSources(jdkHome: IResFile): List<IResFile> {
            val candidates = listOf(
                jdkHome.resolve("lib").resolve("src.zip"),
                jdkHome.parent.resolve("src.zip"),
            )
            val result = mutableListOf<IResFile>()

            for (srcZip in candidates) runCatching {
                if (!Files.isReadable(srcZip.path)) return@runCatching
                val uri = URI.create("jar:${srcZip.path.toUri()}")
                FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fs ->
                    val root = fs.rootDirectories.first()
                    val sep  = fs.separator
                    fun has(file: String) = Files.exists(root.resolve(file.replace("/", sep)))
                    if (has("java/lang/Object.java") || has("java.base/java/lang/Object.java"))
                        result += srcZip.toFile()
                }
            }.onFailure { e ->
                logger.debug(e) { "findJdkSources(): $e" }
            }
            return result
        }
    }
}
