package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*

/**
 * 数据流分析相关 CLI 选项
 *
 * - 与先前行为保持一致
 * - 若需新增选项，请按 `option()` 链式 DSL 自行扩展
 */
class DataFlowOptions : OptionGroup(
   name = "Data Flow Options",
   helpTags = null
) {
   /** 是否启用污点 / 数据流分析 */
   val enableDataFlow: Boolean by option(
      "--enable-data-flow",
      help = "Enable taint/data-flow analysis"
   ).flag(default = false)

   /** 是否统计静态覆盖率 */
   val enableCoverage: Boolean by option(
      "--enable-coverage",
      help = "Enable coverage statistics"
   ).flag(default = false)

   /**
    * 调优因子；`null` 表示未设置
    * 用法：`--factor 3`
    */
   val factor1: Int? by option(
      "--factor",
      metavar = "N",
      help = "Tuning factor for heuristics"
   ).convert("N") {
      it.toIntOrNull() ?: fail("must be an integer")
   }

   /**
    * 被调方法超时（毫秒）；`null` 表示不限
    * 用法：`--callee-timeout 30000`
    */
   val dataFlowInterProceduralCalleeTimeOut: Int? by option(
      "--callee-timeout",
      metavar = "MS",
      help = "Per-callee time-out (ms)"
   ).convert("MS") {
      it.toIntOrNull() ?: fail("must be an integer")
   }
}

/* ---------- Option 工具扩展 ---------- */

/** 抛出一致的错误信息，保持与 Clikt 默认行为兼容 */
private fun OptionWithValues<*, *>.fail(msg: String): Nothing =
   fail("Invalid value for option ${names.joinToString()} : $msg")
