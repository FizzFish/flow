package cn.sast.dataflow.infoflow.svfa

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentHashSetOf
import soot.IdentityUnit
import soot.Trap
import soot.Unit
import soot.Value
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.ExceptionalGraph
import soot.toolkits.scalar.FlowAnalysis.Flow

/**
 * 提供 Forward/Backward Flow-Analysis 通用操作（合并、边类型判定等）。
 */
internal object FlowAnalysisOp {

   /**
    * 把 `in1` 合并到 `inout`；若同一 Value 已存在，则并集其节点集合。
    */
   fun mergeInto(succNode: Unit, inout: FlowFact, in1: FlowFact) {
      for (k in in1.data.keys) {
         val set0: PersistentSet<VFNode> = inout.data[k] ?: persistentHashSetOf()
         val set1: PersistentSet<VFNode> = in1.data[k] ?: persistentHashSetOf()
         inout.data = inout.data.put(k, set0.addAll(set1))
      }
   }

   /**
    * 判断边 `from → to` 属于哪一类（IN / OUT），主要处理异常图首部特殊情况。
    */
   fun getFlow(graph: DirectedGraph<Unit>, from: Unit, to: Unit): Flow {
      if (to is IdentityUnit && graph is ExceptionalGraph) {
         val eg = graph as ExceptionalGraph
         if (eg.exceptionalPredsOf(to).isNotEmpty()) {
            for (exDest in eg.getExceptionDests(from)) {
               val trap: Trap? = exDest.trap
               if (trap != null && trap.handlerUnit === to) {
                  return Flow.IN
               }
            }
         }
      }
      return Flow.OUT
   }
}
