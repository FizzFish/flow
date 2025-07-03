package cn.sast.dataflow.interprocedural.check

import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

public class Sanitizer(cfg: IInfoflowCFG, absAndSink: AbstractionAtSink) {
    public val cfg: IInfoflowCFG = cfg
    public val absAndSink: AbstractionAtSink = absAndSink

    public fun doAnalysis() {
        val abstraction: Abstraction = this.absAndSink.getAbstraction()
        DefaultAbstractionGraph(abstraction)
    }
}