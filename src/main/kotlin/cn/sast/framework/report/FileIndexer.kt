package cn.sast.framework.report

import cn.sast.common.IResFile
import java.util.concurrent.ConcurrentHashMap

/**
 * 线程安全的文件索引：
 * * `fileNameToPathMap`  – 同名文件集合
 * * `extensionToPathMap` – 同后缀文件集合
 */
class FileIndexer(
    private val fileNameToPathMap: Map<String, Set<IResFile>>,
    private val extensionToPathMap: Map<String, Set<IResFile>>
) : AbstractFileIndexer<IResFile>() {

    /** 总文件数（懒计算） */
    val count: Long by lazy { fileNameToPathMap.values.sumOf { it.size }.toLong() }

    // ----------------------------------------------------------------- //
    // AbstractFileIndexer 实现
    // ----------------------------------------------------------------- //

    override fun getNames(path: IResFile, mode: CompareMode): List<String> =
        path.path.map { it.toString() }

    override fun getPathsByName(name: String): Collection<IResFile> =
        fileNameToPathMap[name] ?: emptyList()

    /** 根据扩展名检索 */
    fun getPathsByExtension(extension: String): Collection<IResFile> =
        extensionToPathMap[extension] ?: emptyList()
}
