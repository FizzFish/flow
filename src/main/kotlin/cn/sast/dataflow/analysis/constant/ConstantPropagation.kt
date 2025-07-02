package cn.sast.dataflow.analysis.constant

import soot.Local
import soot.Unit
import soot.jimple.AssignStmt
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.ForwardFlowAnalysis

class ConstantPropagation(graph: DirectedGraph<Unit>) : ForwardFlowAnalysis<Unit, FlowMap>(graph) {

   override fun flowThrough(`in`: FlowMap, d: Unit, out: FlowMap) {
      copy(`in`, out)
      if (d is AssignStmt) {
         val lVal = d.leftOp
         if (lVal is Local) {
            val rightVal = d.rightOp
            out.put(lVal, `in`.computeValue(rightVal))
         }
      }
   }

   override fun newInitialFlow(): FlowMap = FlowMap()

   override fun merge(in1: FlowMap, in2: FlowMap, out: FlowMap) {
      val merged = FlowMap.meet(in1, in2)
      copy(merged, out)
   }

   override fun copy(source: FlowMap, dest: FlowMap) {
      dest.copyFrom(source)
   }
   public override fun doAnalysis() {
      super.doAnalysis()
   }

}