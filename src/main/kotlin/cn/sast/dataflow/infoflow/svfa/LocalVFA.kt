package cn.sast.dataflow.infoflow.svfa

import kotlinx.collections.immutable.*
import soot.*
import soot.jimple.*
import soot.jimple.infoflow.util.BaseSelector
import soot.options.Options
import soot.toolkits.graph.DirectedGraph

/**
 * 在单个方法内，基于 **FlowAssignment/BackAssignment** 生成
 *  - uses     : Value 在某行被 **使用** 的所有行
 *  - defUses  : Value 在某行被 **定义** 后传播到的所有行
 *
 * 只保留关键流程，辅助方法以 `// …` 省略。
 */
class LocalVFA(
   graph: DirectedGraph<Unit>,
   private val trackControlFlowDependencies: Boolean
) : ILocalDFA {

   private val uses: Map<Unit, FlowFact>
   private val defUses: Map<Unit, FlowFact>

   init {
      if (Options.v().time()) Timers.v().defsTimer.start()

      val (usesTmp, defUsesTmp) = init(graph)
      uses = usesTmp
      defUses = defUsesTmp

      if (Options.v().time()) Timers.v().defsTimer.end()
   }

   /* ---------- ILocalDFA ---------- */

   override fun getUsesOfAt(ap: AP, stmt: Unit): List<Unit> =
      uses[stmt]?.data?.get(ap.value)?.map { it.stmt } ?: emptyList()

   override fun getDefUsesOfAt(ap: AP, stmt: Unit): List<Unit> =
      defUses[stmt]?.data?.get(ap.value)?.map { it.stmt } ?: emptyList()

   /* ---------- 主流程 ---------- */

   private fun init(graph: DirectedGraph<Unit>):
           Pair<Map<Unit, FlowFact>, Map<Unit, FlowFact>> {

      val paramAndThis = hashSetOf<Value>()

      /* 1. 收集每条语句读写的 (AP, ValueLocation) */
      val stmt2Info: Map<Stmt, Set<Pair<AP, ValueLocation>>> =
         graph.filterIsInstance<Stmt>().associateWith { stmt ->
            buildSet {
               collectStmtInfo(stmt) { v, loc ->
                  AP[v]?.let { ap ->
                     if (loc == ValueLocation.ParamAndThis) paramAndThis += ap.value
                     add(ap to loc)
                  }
               }
            }
         }

      /* 2. 双向数据流分析 */
      val flowAssign   = FlowAssignment(graph, paramAndThis, stmt2Info).before()
      val backAssign   = BackAssignment(graph, paramAndThis, stmt2Info).after()

      return flowAssign to backAssign
   }

   /* ---------- 解析语句读写 ---------- */

   private inline fun collectStmtInfo(
      stmt: Stmt,
      add: (Value, ValueLocation) -> Unit
   ) {
      when (stmt) {
         is AssignStmt -> {
            val left = stmt.leftOp
            val rights = BaseSelector.selectBaseList(stmt.rightOp, true)

            // 写
            if (left is ArrayRef) add(left.base, ValueLocation.Right)
            else add(left, ValueLocation.Left)

            // 读
            rights.forEach { add(it, ValueLocation.Right) }
         }

         is IdentityStmt -> {
            when (val rop = stmt.rightOp) {
               is ParameterRef,
               is ThisRef -> add(stmt.leftOp, ValueLocation.ParamAndThis)
            }
         }

         is IfStmt -> if (trackControlFlowDependencies) {
            val cond = stmt.condition as BinopExpr
            add(cond.op1, ValueLocation.Right)
            add(cond.op2, ValueLocation.Right)
         }

         is ReturnStmt   -> add(stmt.op, ValueLocation.Right)
         is ReturnVoidStmt -> add(returnVoidFake, ValueLocation.Right)
      }

      if (stmt.containsInvokeExpr()) {
         val ie = stmt.invokeExpr
         if (ie is InstanceInvokeExpr) add(ie.base, ValueLocation.Arg)
         repeat(ie.argCount) { idx -> add(ie.getArg(idx), ValueLocation.Arg) }
      }
   }

   /* ---------- 静态字段 ---------- */

   companion object {
      /** 伪返回值 (void) 占位 */
      val returnVoidFake: Value = IntConstant.v(0)

      /** 方法入口的伪节点 */
      val entryFake: Value = IntConstant.v(-1)
   }
}
