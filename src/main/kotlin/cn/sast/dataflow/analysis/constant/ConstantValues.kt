package cn.sast.dataflow.analysis.constant

import soot.Unit
import soot.Value
import soot.toolkits.graph.DirectedBodyGraph

class ConstantValues(graph: DirectedBodyGraph<Unit>) {

   private val analysis = ConstantPropagation(graph)

   init {
      analysis.doAnalysis()
   }

   fun getValueAt(v: Value, unit: Unit): Int? {
      val cpValue = (analysis.getFlowBefore(unit) as FlowMap).computeValue(v)
      return if (cpValue != CPValue.undef && cpValue != CPValue.nac) cpValue.value() else null
   }
}