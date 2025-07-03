package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

public interface IReportsVisitor : IResultCollector {
    public fun accept(reports: Collection<Report>) {
    }

    internal class DefaultImpls {
        @JvmStatic
        fun flush(`$this`: IReportsVisitor, `$completion`: Continuation<Unit>): Any? {
            val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`)
            return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
        }
    }
}