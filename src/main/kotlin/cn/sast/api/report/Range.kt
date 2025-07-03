package cn.sast.api.report

import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/Range\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
data class Range(
    val start_line: Int,
    val start_col: Int,
    val end_line: Int,
    val end_col: Int
) : Comparable<Range>, IReportHashAble {
    override fun compareTo(other: Range): Int {
        var result = Intrinsics.compare(start_line, other.start_line)
        if (result != 0) return result
        
        result = Intrinsics.compare(start_col, other.start_col)
        if (result != 0) return result
        
        result = Intrinsics.compare(end_line, other.end_line)
        if (result != 0) return result
        
        return Intrinsics.compare(end_col, other.end_col)
    }

    override fun reportHash(c: IReportHashCalculator): String {
        return "${this.start_line},${this.start_col},${this.end_line},${this.end_col}"
    }

    override fun toString(): String {
        return "[${start_line}:${start_col},${end_line}:${end_col}]"
    }

    operator fun component1(): Int = start_line
    operator fun component2(): Int = start_col
    operator fun component3(): Int = end_line
    operator fun component4(): Int = end_col

    fun copy(
        start_line: Int = this.start_line,
        start_col: Int = this.start_col,
        end_line: Int = this.end_line,
        end_col: Int = this.end_col
    ): Range {
        return Range(start_line, start_col, end_line, end_col)
    }

    override fun hashCode(): Int {
        return ((start_line.hashCode() * 31 + start_col.hashCode()) * 31 + end_line.hashCode()) * 31 + end_col.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Range) return false
        
        return start_line == other.start_line &&
            start_col == other.start_col &&
            end_line == other.end_line &&
            end_col == other.end_col
    }
}