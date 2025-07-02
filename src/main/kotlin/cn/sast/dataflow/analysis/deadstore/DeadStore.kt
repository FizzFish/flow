package cn.sast.dataflow.analysis.deadstore

import cn.sast.dataflow.analysis.IBugReporter
import soot.Body
import soot.BodyTransformer
import soot.Local
import soot.Unit
import soot.jimple.AssignStmt
import soot.toolkits.graph.ExceptionalUnitGraph
import soot.toolkits.scalar.SimpleLiveLocals

class DeadStore(private val reporter: IBugReporter) : BodyTransformer() {
   override fun internalTransform(body: Body, phaseName: String, options: Map<String, String>) {
      val liveLocals = SimpleLiveLocals(ExceptionalUnitGraph(body))

      for (unit in body.units) {
         if (unit is AssignStmt) {
            val leftOp = unit.leftOp
            if (leftOp is Local && liveLocals.getLiveLocalsBefore(unit).contains(leftOp).not()) {
               reporter.report("DeadStore", unit)
            }
         }
      }
   }
}