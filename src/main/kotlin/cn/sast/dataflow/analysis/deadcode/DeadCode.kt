package cn.sast.dataflow.analysis.deadcode

import cn.sast.dataflow.analysis.IBugReporter
import cn.sast.dataflow.analysis.constant.ConstantValues
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.builtin.checkers.DeadCodeChecker.UnreachableBranch
import org.jetbrains.annotations.NotNull
import soot.Body
import soot.SootClass
import soot.Unit as SootUnit
import soot.Value
import soot.jimple.IfStmt
import soot.toolkits.graph.DirectedBodyGraph

/**
 * 「条件恒真 / 恒假」导致的不可能分支检测。
 *
 * * 依赖 [ConstantValues] 做局部常量传播；
 * * 若判定某分支永远走不到，就把那条语句 [unreachableBranch] 上报给 [IBugReporter]。
 */
class DeadCode(
   @NotNull private val reporter: IBugReporter
) {

   /** 主入口：对一张方法图做分析 */
   fun analyze(graph: DirectedBodyGraph<Unit>) {
      findDeadCode(graph)                              // 目前占位，留接口
      val cv = ConstantValues(graph)
      findUnreachableBranch(graph.body, cv)
   }

   /* ───────────────────────── 占位 ───────────────────────── */

   private fun findDeadCode(@Suppress("UNUSED_PARAMETER") graph: DirectedBodyGraph<Unit>) {
      /* TODO: 实现真正的“死代码块”检测 */
   }

   /* ──────────────────── 恒真 / 恒假分支检测 ─────────────────── */

   private fun findUnreachableBranch(body: Body, constants: ConstantValues) {
      // 1. 遍历所有 if 语句
      body.units
         .filterIsInstance<IfStmt>()
         .forEach { ifStmt ->
            val cond: Value = ifStmt.condition
            val v: Int?     = constants.getValueAt(cond, ifStmt)

            // 2. 根据恒值决定“不可能执行的分支”
            val unreachable: SootUnit? = when (v) {
               1    -> body.units.getSuccOf(ifStmt)       // 条件恒真 → else 分支死
               0    -> ifStmt.target                      // 条件恒假 → then 分支死
               else -> null                               // 不确定
            }

            // 3. 上报
            if (unreachable != null) {
               val guard = if (unreachable == ifStmt.target)
                  cond.toString() else "(!${cond})"

               reportUnreachable(body.method.declaringClass, unreachable, guard)
            }
         }
   }

   /* ───────────────────────── 上报封装 ───────────────────────── */

   private fun reportUnreachable(cls: SootClass, stmt: SootUnit, guard: String) {
      reporter.report(
         UnreachableBranch as CheckType,
         cls,
         stmt
      ) { env: BugMessage.Env ->
         env.args["guard"]  = guard
         env.args["target"] = stmt
      }
   }

}
