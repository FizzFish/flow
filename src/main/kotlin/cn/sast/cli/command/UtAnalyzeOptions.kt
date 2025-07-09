package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*

/**
 * UT 静态分析相关选项
 *
 * `--enable-ut-analyze`   开关
 * `--ut-max-depth N`      自定义深度，正整数
 */
class UtAnalyzeOptions : OptionGroup(
    name = "UtAnalyze Options"
) {
    val enableUtAnalyze: Boolean by option(
        "--enable-ut-analyze",
        help = "Enable UT static analyze"
    ).flag(default = false)

    val utMaxDepth: Int? by option(
        "--ut-max-depth",
        metavar = "N",
        help = "Maximum analysis depth"
    ).convert("N") { it.toIntOrNull() ?: fail("must be an integer >0") }
}
