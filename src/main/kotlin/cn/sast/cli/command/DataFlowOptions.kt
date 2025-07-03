package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlin.math.max

/**
 * CLI：数据流分析相关选项
 */
class DataFlowOptions : OptionGroup(
    name = "Data Flow Options",
    helpTags = null,
) {
    /** 是否启用污点分析 */
    val enableDataFlow by option(
        "--enable-data-flow",
        help = "Enable taint/data-flow analysis"
    ).flag(default = false)

    /** 是否统计覆盖率（静态） */
    val enableCoverage by option(
        "--enable-coverage",
        help = "Enable coverage statistics"
    ).flag(default = false)

    /**
     * “分支缩放因子”——可用 `--factor 3` 设置；
     * 为方便校验，这里将空字符串视为 null。
     */
    val factor1: Int? by option(
        "--factor",
        metavar = "N",
        help = "Tuning factor for heuristics"
    ).convert { it.toIntOrNull() ?: fail("must be an integer") }

    /** 每个被调方法超时（ms） */
    val dataFlowInterProceduralCalleeTimeOut: Int? by option(
        "--callee-timeout",
        metavar = "MS"
    ).convert { it.toInt() }
}

/* ---------- OptionWithValues.ext ---------- */

private fun <InT : Any, ValueT : Any> OptionWithValues<InT, ValueT>.fail(msg: String): Nothing =
    error("Invalid value for option ${names.joinToString()} : $msg")
