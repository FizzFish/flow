package cn.sast.api.report

import cn.sast.common.IResFile
import cn.sast.common.Resource
import kotlin.io.path.absolute
import kotlin.io.path.normalize

/**
 * 以文件形式定位的资源信息。
 */
class FileResInfo(private val sourcePath: IResFile) : IBugResInfo() {

    /** 绝对规范化路径（懒加载） */
    private val abs by lazy { sourcePath.absolute().normalize() }

    override val reportFileName: String get() = abs.name
    override val path: String get() = abs.toString().replace(':', '-')

    override fun reportHash(c: IReportHashCalculator): String = c.fromAbsPath(abs)

    /* ---------- Equality / Compare ---------- */

    override fun equals(other: Any?): Boolean =
        other is FileResInfo && abs == other.abs

    override fun hashCode(): Int = abs.hashCode()

    override fun compareTo(other: IBugResInfo): Int =
        when (other) {
            is FileResInfo -> abs.compareTo(other.abs)
            else           -> this::class.simpleName!!.compareTo(other::class.simpleName!!)
        }

    override fun toString(): String = "FileResInfo(file=$abs)"
}
