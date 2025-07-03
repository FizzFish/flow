package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import soot.SootMethod

public interface IMissingSummaryReporter : IResultCollector {
    public open fun reportMissingMethod(method: SootMethod) {
    }

    internal object DefaultImpls {
        @JvmStatic
        fun reportMissingMethod(`$this`: IMissingSummaryReporter, method: SootMethod) {
        }

        @JvmStatic
        fun flush(`$this`: IMissingSummaryReporter, `$completion`: Continuation<Unit>): Any? {
            val var10000 = IResultCollector.DefaultImpls.flush(`$this`, `$completion`)
            return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
        }
    }
}