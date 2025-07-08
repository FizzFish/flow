package cn.sast.api.report

/**
 * 代码范围：`[startLine:startCol, endLine:endCol]`
 */
data class Range(
    val startLine: Int,
    val startCol: Int,
    val endLine: Int,
    val endCol: Int
) : Comparable<Range>, IReportHashAble {

    override fun compareTo(other: Range): Int =
        startLine.compareTo(other.startLine)
            .takeIf { it != 0 } ?: startCol.compareTo(other.startCol)
            .takeIf { it != 0 } ?: endLine.compareTo(other.endLine)
            .takeIf { it != 0 } ?: endCol.compareTo(other.endCol)

    override fun reportHash(c: IReportHashCalculator): String =
        "$startLine,$startCol,$endLine,$endCol"

    override fun toString(): String = "[$startLine:$startCol,$endLine:$endCol]"
}
