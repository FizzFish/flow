package cn.sast.dataflow.analysis.deadstore

import cn.sast.dataflow.analysis.IBugReporter
import soot.Unit
import soot.toolkits.graph.DirectedBodyGraph

public class DeadStore(reporter: IBugReporter) {
   private final val reporter: IBugReporter

   init {
      this.reporter = reporter;
   }

   public fun analyze(graph: DirectedBodyGraph<Unit>) {
   }
}
