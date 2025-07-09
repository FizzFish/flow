package cn.sast.framework.report

import cn.sast.common.IResFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

/**
 * 按需增量构建 [FileIndexer]。
 */
class FileIndexerBuilder {

    private val fileNameToPathMap =
        ConcurrentHashMap<String, MutableSet<IResFile>>()

    private val extensionToPathMap =
        ConcurrentHashMap<String, MutableSet<IResFile>>()

    /** 新增一个文件到索引 */
    fun addIndexMap(resFile: IResFile) {
        if (!resFile.isFile) return

        fileNameToPathMap
            .getOrPut(resFile.name) { ConcurrentSkipListSet() }
            .add(resFile)

        extensionToPathMap
            .getOrPut(resFile.extension) { ConcurrentSkipListSet() }
            .add(resFile)
    }

    /** 将其他索引合并进来 */
    fun union(indexer: FileIndexer) {
        indexerPaths(indexer.fileNameToPathMap, fileNameToPathMap)
        indexerPaths(indexer.extensionToPathMap, extensionToPathMap)
    }

    /** 构建最终索引（所有集合变为只读 `Set`，节省内存） */
    fun build(): FileIndexer =
        FileIndexer(
            fileNameToPathMap.mapValues { it.value.toSet() },
            extensionToPathMap.mapValues { it.value.toSet() }
        )

    // ----------------------------------------------------------------- //
    // 内部工具
    // ----------------------------------------------------------------- //

    private fun indexerPaths(
        from: Map<String, Set<IResFile>>,
        to: MutableMap<String, MutableSet<IResFile>>
    ) = from.forEach { (k, v) ->
        to.getOrPut(k) { ConcurrentSkipListSet() }.addAll(v)
    }
}
