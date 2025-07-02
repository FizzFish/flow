package cn.sast.dataflow.infoflow.svfa

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.toPersistentMap
import soot.Unit
import soot.Value
import soot.jimple.Stmt
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.ForwardFlowAnalysis
import soot.toolkits.scalar.FlowAnalysis.Flow

/**
 * 向后数据流回溯，用于构造 *Value-Flow* 映射。
 *
 * 依赖外部定义：
 * - **FlowFact**            ：封装 `PersistentMap<Value, PersistentSet<VFNode>>`
 * - **ValueLocation / VFNode**：图节点模型
 */
internal class BackAssignment(
   graph: DirectedGraph<Unit>,
   private val paramAndThis: Set<Value>,
   private val unit2locals: Map<Stmt, Set<Pair<AP, ValueLocation>>>
) : ForwardFlowAnalysis<Unit, FlowFact>(graph) {

   init {
      doAnalysis()
   }

   /* ---------- ForwardFlowAnalysis 覆写 ---------- */

   override fun newInitialFlow(): FlowFact = FlowFact()

   override fun copy(source: FlowFact, dest: FlowFact) {
      if (source !== dest) dest.data = source.data
   }

   override fun merge(in1: FlowFact, in2: FlowFact, out: FlowFact) {
      // 理论上不应被调用（参见 Sparse-Propagation 设计）
      error("BackAssignment.merge should never be called")
   }

   override fun getFlow(from: Unit, to: Unit): Flow =
      FlowAnalysisOp.getFlow(graph, from, to)

   override fun mergeInto(succNode: Unit, inout: FlowFact, in1: FlowFact) =
      FlowAnalysisOp.mergeInto(succNode, inout, in1)

   override fun flowThrough(src: FlowFact, unit: Unit, dest: FlowFact) {
      copy(src, dest)

      if (unit !is Stmt) return

      /* step-1: 把 unit2locals 中记录的 (AP, ValueLocation) 合入映射 */
      unit2locals[unit]?.forEach { (ap, _) ->
         val curSet = dest.data[ap.value] ?: persistentHashSetOf()
         dest.data = dest.data.put(ap.value, curSet.add(VFNode(ap.value, unit)))
      }

      /* step-2: 方法入口位置，初始化 this/参数 对应集合 */
      if (graph.heads.contains(unit)) {
         val addMap = paramAndThis.associateWith { v ->
            persistentHashSetOf(VFNode(v, unit))
         }.toPersistentMap()
         dest.data = dest.data.putAll(addMap)
      }
   }

   /* ---------- 结果暴露 ---------- */

   fun after(): Map<Unit, FlowFact> = unitToAfterFlow
}
