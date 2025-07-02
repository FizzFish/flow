package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

public interface IIPAnalysisResultCollector : IResultCollector {
   public abstract fun afterAnalyze(analysis: InterProceduralValueAnalysis) {
   }

   public abstract fun reportDataFlowBug(reports: List<Report>) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun flush(`$this`: IIPAnalysisResultCollector, `$completion`: Continuation<? super Unit>): Any? {
         val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`);
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
      }
   }
}
