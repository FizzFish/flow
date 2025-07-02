package cn.sast.dataflow.infoflow.svfa

import kotlinx.collections.immutable.*
import soot.Unit
import soot.Value
import soot.jimple.ReturnStmt
import soot.jimple.ReturnVoidStmt
import soot.jimple.Stmt
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.BackwardFlowAnalysis
import soot.toolkits.scalar.FlowAnalysis.Flow

/**
 * 反向回溯 —— 生成 “def-use” 关系。
 */
internal class FlowAssignment(
   graph: DirectedGraph<Unit>,
   private val paramAndThis: MutableSet<Value>,
   private val unit2locals: Map<Stmt, Set<Pair<AP, ValueLocation>>>
) : BackwardFlowAnalysis<Unit, FlowFact>(graph) {

   init {
      doAnalysis()
   }

   /* ---------- BackwardFlowAnalysis 覆写 ---------- */

   override fun newInitialFlow(): FlowFact = FlowFact()

   override fun copy(source: FlowFact, dest: FlowFact) {
      if (source !== dest) dest.data = source.data
   }

   /** 不应被调用（同框架设计） */
   override fun merge(in1: FlowFact, in2: FlowFact, out: FlowFact) =
      error("FlowAssignment.merge should never be called")

   override fun flowThrough(infact: FlowFact, unit: Unit, outfact: FlowFact) {
      copy(infact, outfact)

      if (unit !is Stmt) return

      /* ---------- (1) 处理本条语句的读写 ---------- */
      unit2locals[unit]?.forEach { (ap, loc) ->
         when {
            loc.isLeft() -> {
               // 定义
               if (ap.field == null) {
                  outfact.data = outfact.data.remove(ap.value)
               } else {
                  outfact.data = outfact.data.put(
                     ap.value,
                     persistentHashSetOf(VFNode(ap.value, unit))
                  )
               }
            }
            else -> {
               // 使用
               outfact.data = outfact.data.put(
                  ap.value,
                  (outfact.data[ap.value] ?: persistentHashSetOf())
                     .add(VFNode(ap.value, unit))
               )
            }
         }
      }

      /* ---------- (2) Return 语句 —— 把 this/参数 合入 ---------- */
      if (unit is ReturnVoidStmt || unit is ReturnStmt) {
         val extra = paramAndThis.associateWith { v ->
            persistentHashSetOf(VFNode(v, unit))
         }
         outfact.data = outfact.data.putAll(extra)
      }
   }

   /* ---------- FlowAnalysis Hook ---------- */

   override fun getFlow(from: Unit, to: Unit): Flow =
      FlowAnalysisOp.getFlow(graph, from, to)

   override fun mergeInto(succNode: Unit, inout: FlowFact, in1: FlowFact) =
      FlowAnalysisOp.mergeInto(succNode, inout, in1)

   /* ---------- 结果公开 ---------- */

   /** 语句 **执行前** 的流事实 */
   fun before(): Map<Unit, FlowFact> = unitToBeforeFlow
}
