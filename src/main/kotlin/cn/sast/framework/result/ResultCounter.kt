package cn.sast.framework.result

import cn.sast.api.report.Report
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.framework.engine.PreAnalysisReportEnv
import com.feysh.corax.config.api.CheckType
import java.util.concurrent.atomic.AtomicInteger
import mu.KLogger
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG

public class ResultCounter :
    IFlowDroidResultCollector,
    IUTBotResultCollector,
    IIPAnalysisResultCollector,
    IPreAnalysisResultCollector,
    IBuiltInAnalysisCollector {
    
    public val infoflowResCount: AtomicInteger = AtomicInteger(0)
    public val infoflowAbsAtSinkCount: AtomicInteger = AtomicInteger(0)
    public val symbolicUTbotCount: AtomicInteger = AtomicInteger(0)
    public val dataFlowCount: AtomicInteger = AtomicInteger(0)
    public val builtInAnalysisCount: AtomicInteger = AtomicInteger(0)
    public val preAnalysisResultCount: AtomicInteger = AtomicInteger(0)

    public open fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
        this.infoflowResCount.addAndGet(results.size)
    }

    public open fun onResultAvailable(icfg: IInfoflowCFG, abs: AbstractionAtSink): Boolean {
        this.infoflowAbsAtSinkCount.incrementAndGet()
        return true
    }

    public override fun addUtState() {
        this.symbolicUTbotCount.incrementAndGet()
    }

    public override fun afterAnalyze(analysis: InterProceduralValueAnalysis) {
    }

    public override fun reportDataFlowBug(reports: List<Report>) {
        this.dataFlowCount.addAndGet(reports.size)
    }

    public override fun report(checkType: CheckType, info: PreAnalysisReportEnv) {
        this.preAnalysisResultCount.incrementAndGet()
    }

    public override fun report(report: Report) {
        this.builtInAnalysisCount.incrementAndGet()
    }

    public override suspend fun flush() {
        logger.info { "num of infoflow results: ${infoflowResCount.get()}" }
        logger.info { "num of infoflow abstraction at sink: ${infoflowAbsAtSinkCount.get()}" }
        logger.info { "num of symbolic execution results: ${symbolicUTbotCount.get()}" }
        logger.info { "num of PreAnalysis results: ${preAnalysisResultCount.get()}" }
        logger.info { "num of built-in Analysis results: ${builtInAnalysisCount.get()}" }
        logger.info { "num of AbstractInterpretationAnalysis results: ${dataFlowCount.get()}" }
    }

    public companion object {
        private val logger: KLogger
    }
}