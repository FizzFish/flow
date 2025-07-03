package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

public interface IBuiltInAnalysisCollector : IResultCollector {
    public fun report(report: Report) {
    }

    internal class DefaultImpls {
        companion object {
            @JvmStatic
            fun flush(`$this`: IBuiltInAnalysisCollector, `$completion`: Continuation<Unit>): Any? {
                val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`)
                return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
            }
        }
    }
}