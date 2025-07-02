package cn.sast.dataflow.analysis.constant

import soot.Local
import soot.Unit
import soot.Value
import soot.jimple.AssignStmt
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.ForwardFlowAnalysis

internal class ConstantPropagation(graph: DirectedGraph<Unit>) : ForwardFlowAnalysis(graph) {
   protected open fun flowThrough(`in`: FlowMap, d: Unit, out: FlowMap) {
      this.copy(`in`, out);
      if (d is AssignStmt) {
         val lVal: Value = (d as AssignStmt).getLeftOp();
         if (lVal is Local) {
            val rightVal: Value = (d as AssignStmt).getRightOp();
            out.put(lVal as Local, `in`.computeValue(rightVal));
         }
      }
   }

   protected open fun newInitialFlow(): FlowMap {
      return new FlowMap(null, 1, null);
   }

   protected open fun merge(in1: FlowMap, in2: FlowMap, out: FlowMap) {
      this.copy(FlowMap.Companion.meet(in1, in2), out);
   }

   protected open fun copy(source: FlowMap, dest: FlowMap) {
      dest.copyFrom(source);
   }

   public open fun doAnalysis() {
      super.doAnalysis();
   }
}
