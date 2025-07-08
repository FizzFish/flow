package cn.sast.api.report

import cn.sast.api.util.compareToMap
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import com.feysh.corax.cache.analysis.SootInfoCache
import cn.sast.api.util.compareToNullable

/**
 * 单个“路径事件”——信息、类、位置（可选栈深）。
 */
data class BugPathEvent(
    val message: Map<Language, String>,
    val classname: IBugResInfo,
    val region: Region,
    val stackDepth: Int? = null
) : Comparable<BugPathEvent>, IReportHashAble {

    override fun compareTo(other: BugPathEvent): Int =
        compareToMap(message, other.message)
            .takeIf { it != 0 } ?: classname.compareTo(other.classname)
            .takeIf { it != 0 } ?: region.compareTo(other.region)

    override fun reportHash(c: IReportHashCalculator): String =
        "${classname.reportHash(c)}:$region"

    fun reportHashWithMessage(c: IReportHashCalculator): String =
        "${reportHash(c)} ${message.values.sorted()}"
}


/**
 * 承载构造 [BugPathEvent] 时所需的外部环境。
 */
data class BugPathEventEnvironment(val sootInfoCache: SootInfoCache?)

/**
 * “<类, 位置>” 可比较包装，用于有序集合。
 */
data class BugPathPosition(
    val classname: IBugResInfo,
    val region: Region?
) : Comparable<BugPathPosition> {

    override fun compareTo(other: BugPathPosition): Int =
        classname.compareTo(other.classname)
            .takeIf { it != 0 } ?: compareToNullable(region, other.region)
}