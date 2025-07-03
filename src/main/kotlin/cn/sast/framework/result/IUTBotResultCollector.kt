package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

public interface IUTBotResultCollector : IResultCollector {
    public fun addUtState() {
    }

    internal object DefaultImpls {
        @JvmStatic
        fun flush(`$this`: IUTBotResultCollector, `$completion`: Continuation<Unit>): Any? {
            val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`)
            return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit
        }
    }
}