package cn.sast.framework.report

import cn.sast.common.FileSystemLocator
import cn.sast.common.FileSystemLocator.TraverseMode
import cn.sast.common.IResFile
import cn.sast.common.IResource
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * 访问文件系统时的「索引缓存」：
 * (根目录 + 遍历模式) → [FileIndexer]
 */
object FileSystemCacheLocator {

    /** 非阻塞缓存，失效时间 10 min，可并发构建 */
    private val cache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(256)
        .build<Pair<IResource, TraverseMode>, Deferred<FileIndexer>> { (root, mode) ->
            GlobalScope.async(Dispatchers.Default) {
                buildIndexer(root, mode)
            }
        }

    /** *内部* 真正扫描文件系统并构建索引 */
    private suspend fun buildIndexer(
        root: IResource,
        traverseMode: TraverseMode
    ): FileIndexer = coroutineScope {
        val builder = FileIndexerBuilder()
        val locator = object : FileSystemLocator(builder) {
            override fun visitFile(file: IResFile) = builder.addIndexMap(file)
        }
        locator.process(root.path, traverseMode)
        builder.build()
    }

    /** 获取（或异步创建）索引 */
    suspend fun getIndexer(
        roots: Set<IResource>,
        traverseMode: TraverseMode
    ): FileIndexerBuilder = coroutineScope {
        val builder = FileIndexerBuilder()
        roots.map { cache.get(it to traverseMode) }
            .awaitAll()
            .forEach(builder::union)
        builder
    }

    /** 清理全部缓存 */
    fun clear() = cache.invalidateAll()
}
