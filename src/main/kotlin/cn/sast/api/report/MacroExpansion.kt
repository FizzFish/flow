package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/MacroExpansion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
data class MacroExpansion(
    val message: String,
    val classname: IBugResInfo,
    val line: Int,
    val column: Int,
    val range: Range? = null
) : Comparable<MacroExpansion> {
    override fun compareTo(other: MacroExpansion): Int {
        var result = message.compareTo(other.message)
        if (result != 0) return result
        
        result = classname.compareTo(other.classname)
        if (result != 0) return result
        
        result = Intrinsics.compare(line, other.line)
        if (result != 0) return result
        
        result = Intrinsics.compare(column, other.column)
        if (result != 0) return result
        
        return ComparatorUtilsKt.compareToNullable(range, other.range)
    }

    operator fun component1(): String = message

    operator fun component2(): IBugResInfo = classname

    operator fun component3(): Int = line

    operator fun component4(): Int = column

    operator fun component5(): Range? = range

    fun copy(
        message: String = this.message,
        classname: IBugResInfo = this.classname,
        line: Int = this.line,
        column: Int = this.column,
        range: Range? = this.range
    ): MacroExpansion {
        return MacroExpansion(message, classname, line, column, range)
    }

    override fun toString(): String {
        return "MacroExpansion(message=$message, classname=$classname, line=$line, column=$column, range=$range)"
    }

    override fun hashCode(): Int {
        return (((message.hashCode() * 31 + classname.hashCode()) * 31 + line.hashCode()) * 31 + column.hashCode()) * 31 +
            (range?.hashCode() ?: 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MacroExpansion) return false

        return message == other.message &&
            classname == other.classname &&
            line == other.line &&
            column == other.column &&
            range == other.range
    }
}