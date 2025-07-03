package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

public interface IIPAnalysisResultCollector : IResultCollector {
    public open fun afterAnalyze(analysis: InterProceduralValueAnalysis) {
    }

    public open fun reportDataFlowBug(reports: List<Report>) {
    }

    internal class DefaultImpls {
        @JvmStatic
        fun flush(`$this`: IIPAnalysisResultCollector, `$completion`: Continuation<Unit>): Any? {
            val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`)
            return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
        }
    }
}