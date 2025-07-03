package cn.sast.framework.report

import cn.sast.common.IResFile
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import java.nio.file.Path
import java.util.Map.Entry
import kotlin.Lazy
import kotlin.collections.Map
import kotlin.collections.Set
import kotlin.collections.emptyList
import kotlin.collections.toList
import kotlin.lazy

public class FileIndexer(
    fileNameToPathMap: Map<String, Set<IResFile>>,
    extensionToPathMap: Map<String, Set<IResFile>>
) : AbstractFileIndexer<IResFile> {
    internal val fileNameToPathMap: Map<String, Set<IResFile>>
    internal val extensionToPathMap: Map<String, Set<IResFile>>

    public val count: Long by lazy {
        var c = 0L
        for ((_, value) in fileNameToPathMap) {
            c += value.size
        }
        c
    }

    init {
        this.fileNameToPathMap = fileNameToPathMap
        this.extensionToPathMap = extensionToPathMap
    }

    public open fun getNames(path: IResFile, mode: CompareMode): List<String> {
        val p: Path = path.getPath()
        val names = mutableListOf<String>()
        for (i in 0 until p.nameCount) {
            names.add(p.getName(i).toString())
        }
        return names.toList()
    }

    public override fun getPathsByName(name: String): Collection<IResFile> {
        return fileNameToPathMap[name] ?: emptyList()
    }

    public fun getPathsByExtension(extension: String): Collection<IResFile> {
        return extensionToPathMap[extension] ?: emptyList()
    }
}