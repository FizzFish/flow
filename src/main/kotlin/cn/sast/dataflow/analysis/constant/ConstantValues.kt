package cn.sast.dataflow.analysis.constant

import soot.Unit
import soot.Value
import soot.toolkits.graph.DirectedBodyGraph
import soot.toolkits.graph.DirectedGraph

public class ConstantValues(graph: DirectedBodyGraph<Unit>) {
   private final val analysis: ConstantPropagation

   init {
      this.analysis = new ConstantPropagation(graph as DirectedGraph<Unit>);
      this.analysis.doAnalysis();
   }

   public fun getValueAt(v: Value, unit: Unit): Int? {
      val cpValue: CPValue = (this.analysis.getFlowBefore(unit) as FlowMap).computeValue(v);
      return if (cpValue != CPValue.Companion.getUndef() && cpValue != CPValue.Companion.getNac()) cpValue.value() else null;
   }
}
