package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import soot.jimple.infoflow.handlers.ResultsAvailableHandler
import soot.jimple.infoflow.problems.TaintPropagationResults.OnTaintPropagationResultAdded

public interface IFlowDroidResultCollector : IResultCollector, OnTaintPropagationResultAdded, ResultsAvailableHandler {
    internal object DefaultImpls {
        @JvmStatic
        fun flush(`$this`: IFlowDroidResultCollector, `$completion`: Continuation<Unit>): Any? {
            val var10000 = IResultCollector.DefaultImpls.flush(`$this`, `$completion`)
            return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
        }
    }
}