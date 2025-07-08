package cn.sast.dataflow.analysis.deadcode

import cn.sast.dataflow.analysis.IBugReporter
import cn.sast.dataflow.analysis.constant.ConstantValues
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.builtin.checkers.DeadCodeChecker.UnreachableBranch
import soot.Body
import soot.Unit as SootUnit          // ← 避免与 Kotlin 自带的 Unit 冲突
import soot.Value
import soot.jimple.IfStmt
import soot.toolkits.graph.DirectedBodyGraph

/**
 * 负责检查 *dead code*（永远无法执行的分支 / 语句）。
 */
class DeadCode(private val reporter: IBugReporter) {

   /**
    * 对整张 CFG 进行死代码 & 不可达分支分析。
    */
   fun analyze(graph: DirectedBodyGraph<SootUnit>) {
      findDeadCode(graph)                          // 未来可以在这里补充完整的死代码扫描
      val constantValues = ConstantValues(graph)   // 常量传播结果
      findUnreachableBranch(graph.body, constantValues)
   }

   // —— 以下为内部实现 ——————————————————————————————————————————————

   private fun findDeadCode(graph: DirectedBodyGraph<SootUnit>) {
      /* TODO: 真正的 dead-statement 扫描逻辑仍待补充 */
   }

   /**
    * 利用常量传播结果，把 `if (const)` 或 `if (!const)` 一类
    * 在编译期即可确定真假、因此另一侧永远不走到的分支标记出来。
    */
   private fun findUnreachableBranch(body: Body, constantValues: ConstantValues) {
      body.units
         .filterIsInstance<IfStmt>()              // 仅遍历 If 语句
         .forEach { ifStmt ->
            val const = constantValues.getValueAt(ifStmt.condition, ifStmt)

            // 根据条件恒值推断“另一条”不可达的分支
            val unreachable: SootUnit? = when (const) {
               1    -> body.units.getSuccOf(ifStmt)  // 条件恒真 → else 分支不可达
               0    -> ifStmt.target as SootUnit     // 条件恒假 → then 分支不可达
               else -> null
            }

            unreachable?.let { branch ->
               val guard = if (branch == ifStmt.target)
                  ifStmt.condition.toString()
               else
                  "!(" + ifStmt.condition + ")"

               reporter.report(
                   (UnreachableBranch as CheckType).toString(),
                  body.method.declaringClass,
                  branch
               ) { env: BugMessage.Env ->
                  env.args["guard"] = guard
                  env.args["target"] = branch
               }
            }
         }
   }
}
