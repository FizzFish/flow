package cn.sast.dataflow.interprocedural.check

import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

public class Sanitizer(cfg: IInfoflowCFG, absAndSink: AbstractionAtSink) {
   public final val cfg: IInfoflowCFG
   public final val absAndSink: AbstractionAtSink

   init {
      this.cfg = cfg;
      this.absAndSink = absAndSink;
   }

   public fun doAnalysis() {
      val var10002: Abstraction = this.absAndSink.getAbstraction();
      new DefaultAbstractionGraph(var10002);
   }
}
