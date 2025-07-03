package cn.sast.framework.result

import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

class FlowDroidResultCollector : IFlowDroidResultCollector {
    val result: InfoflowResults = InfoflowResults()

    override fun onResultsAvailable(cfg: IInfoflowCFG?, results: InfoflowResults?) {
        result.addAll(results)
    }

    override fun onResultAvailable(cfg: IInfoflowCFG, abs: AbstractionAtSink?): Boolean {
        return true
    }

    override suspend fun flush() {
        return Unit
    }
}