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
   public final val infoflowResCount: AtomicInteger = new AtomicInteger(0)
   public final val infoflowAbsAtSinkCount: AtomicInteger = new AtomicInteger(0)
   public final val symbolicUTbotCount: AtomicInteger = new AtomicInteger(0)
   public final val dataFlowCount: AtomicInteger = new AtomicInteger(0)
   public final val builtInAnalysisCount: AtomicInteger = new AtomicInteger(0)
   public final val preAnalysisResultCount: AtomicInteger = new AtomicInteger(0)

   public open fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
      this.infoflowResCount.addAndGet(results.size());
   }

   public open fun onResultAvailable(icfg: IInfoflowCFG, abs: AbstractionAtSink): Boolean {
      this.infoflowAbsAtSinkCount.addAndGet(1);
      return true;
   }

   public override fun addUtState() {
      this.symbolicUTbotCount.addAndGet(1);
   }

   public override fun afterAnalyze(analysis: InterProceduralValueAnalysis) {
   }

   public override fun reportDataFlowBug(reports: List<Report>) {
      this.dataFlowCount.addAndGet(reports.size());
   }

   public override fun report(checkType: CheckType, info: PreAnalysisReportEnv) {
      this.preAnalysisResultCount.addAndGet(1);
   }

   public override fun report(report: Report) {
      this.builtInAnalysisCount.addAndGet(1);
   }

   public override suspend fun flush() {
      logger.info(ResultCounter::flush$lambda$0);
      logger.info(ResultCounter::flush$lambda$1);
      logger.info(ResultCounter::flush$lambda$2);
      logger.info(ResultCounter::flush$lambda$3);
      logger.info(ResultCounter::flush$lambda$4);
      logger.info(ResultCounter::flush$lambda$5);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `flush$lambda$0`(`this$0`: ResultCounter): Any {
      return "num of infoflow results: ${`this$0`.infoflowResCount.get()}";
   }

   @JvmStatic
   fun `flush$lambda$1`(`this$0`: ResultCounter): Any {
      return "num of infoflow abstraction at sink: ${`this$0`.infoflowAbsAtSinkCount.get()}";
   }

   @JvmStatic
   fun `flush$lambda$2`(`this$0`: ResultCounter): Any {
      return "num of symbolic execution results: ${`this$0`.symbolicUTbotCount.get()}";
   }

   @JvmStatic
   fun `flush$lambda$3`(`this$0`: ResultCounter): Any {
      return "num of PreAnalysis results: ${`this$0`.preAnalysisResultCount.get()}";
   }

   @JvmStatic
   fun `flush$lambda$4`(`this$0`: ResultCounter): Any {
      return "num of built-in Analysis results: ${`this$0`.builtInAnalysisCount.get()}";
   }

   @JvmStatic
   fun `flush$lambda$5`(`this$0`: ResultCounter): Any {
      return "num of AbstractInterpretationAnalysis results: ${`this$0`.dataFlowCount.get()}";
   }

   @JvmStatic
   fun `logger$lambda$6`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
