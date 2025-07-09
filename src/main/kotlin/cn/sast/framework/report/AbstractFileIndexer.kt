package cn.sast.framework.report

import kotlin.math.min

/**
 * 抽象文件索引器。
 * `PathType` 可以是 `java.nio.file.Path`、`IResFile` 等任何
 * 能够唯一定位文件/资源的类型。
 */
abstract class AbstractFileIndexer<PathType>(private val errorMsgShow: Boolean = false) {

    /** 文件名比较模式 */
    enum class CompareMode(val isClass: Boolean) {
        /** 完整路径（目录层级 + 文件名） */
        Path(false),

        /** Java/Kotlin class 形式（包名 + 类名，不关心目录布局） */
        Class(true),

        /**
         * 仅比对名字 + 内部类，忽略包名。
         *   例：`org/example/Demo.kt` 与 `Demo` 视作「相同」
         */
        ClassNoPackageLayout(true)
    }

    // --------------------------------------------------------------------- //
    // 必须由子类实现的抽象方法
    // --------------------------------------------------------------------- //

    /** 将 [path] 映射成可比对的「名字分片」 */
    abstract fun getNames(path: PathType, mode: CompareMode = CompareMode.Path): List<String>

    /** 通过单个名字（通常是文件/类名最后一段）反查可能的路径 */
    abstract fun getPathsByName(name: String): Collection<PathType>

    // --------------------------------------------------------------------- //
    // 公共查询 API
    // --------------------------------------------------------------------- //

    /** 通过名字分片序列查找匹配路径 */
    fun findFromFileIndexMap(
        toFindNames: List<String>,
        mode: CompareMode = CompareMode.Path
    ): Sequence<PathType> {
        if (toFindNames.isEmpty()) return emptySequence()

        val target = toFindNames.normalizePathParts(mode)
        val last   = target.last()

        return getPathsByName(last).asSequence().filter { candidate ->
            val parts = getNames(candidate, mode).normalizePathParts(mode)
            when (mode) {
                CompareMode.ClassNoPackageLayout -> true
                else                             -> parts.endsWith(target)
            }
        }
    }

    /** 通过单个路径对象反查同名/同类文件 */
    fun findFromFileIndexMap(
        sample: PathType,
        mode: CompareMode = CompareMode.Path
    ): Sequence<PathType> = findFromFileIndexMap(getNames(sample, mode), mode)

    /** 返回 *所有* 命中路径 */
    fun findFiles(
        fileNames: Collection<String>,
        mode: CompareMode = CompareMode.Path
    ): List<PathType> =
        fileNames.flatMap { name ->
            require("\\" !in name) { "invalid source file name: $name" }
            findFromFileIndexMap(name.split('/'), mode).toList()
        }

    /** 找到第一条命中路径即返回；未命中返回 `null` */
    fun findAnyFile(
        fileNames: Collection<String>,
        mode: CompareMode = CompareMode.Path
    ): PathType? =
        fileNames.firstNotNullOfOrNull { name ->
            require("\\" !in name) { "invalid source file name: $name in $fileNames" }
            findFromFileIndexMap(name.split('/'), mode).firstOrNull()
        }

    // --------------------------------------------------------------------- //
    // 私有辅助
    // --------------------------------------------------------------------- //

    private fun List<String>.hasDot(): Boolean =
        size > 1 && dropLast(1).any { '.' in it }

    /** 把 `com/example/My$Inner.class` ⇒ `[com, example, My, Inner, class]`（按模式拆分） */
    private fun List<String>.normalizePathParts(
        mode: CompareMode = CompareMode.Path
    ): List<String> =
        if (mode.isClass && hasDot())
            flatMapIndexed { index, part ->
                if (index != lastIndex && '.' in part) part.split('.') else listOf(part)
            }
        else this

    /** `a.endsWith(b)`（列表级别） */
    private fun List<String>.endsWith(suffix: List<String>): Boolean {
        if (size < suffix.size) return false
        val offset = size - suffix.size
        return indices.drop(offset).all { this[it] == suffix[it - offset] }
    }

    companion object {
        /** 项目默认的类名比较模式 */
        var defaultClassCompareMode: CompareMode = CompareMode.Path
    }
}
