package cn.sast.api.report

import cn.sast.api.util.compareToNullable
import com.feysh.corax.config.api.Language

/**
 * 宏展开诊断信息。
 */
data class MacroExpansion(
    val message: String,
    val classname: IBugResInfo,
    val line: Int,
    val column: Int,
    val range: Range? = null
) : Comparable<MacroExpansion> {

    override fun compareTo(other: MacroExpansion): Int =
        message.compareTo(other.message)
            .takeIf { it != 0 } ?: classname.compareTo(other.classname)
            .takeIf { it != 0 } ?: line.compareTo(other.line)
            .takeIf { it != 0 } ?: column.compareTo(other.column)
            .takeIf { it != 0 } ?: compareToNullable(range, other.range) ?: 0
}
