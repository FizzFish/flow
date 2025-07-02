package cn.sast.framework.result

import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

public class FlowDroidResultCollector : IFlowDroidResultCollector {
   public final val result: InfoflowResults = new InfoflowResults()

   public open fun onResultsAvailable(cfg: IInfoflowCFG?, results: InfoflowResults?) {
      this.result.addAll(results);
   }

   public open fun onResultAvailable(cfg: IInfoflowCFG, abs: AbstractionAtSink?): Boolean {
      return true;
   }

   public override suspend fun flush() {
      return Unit.INSTANCE;
   }
}
