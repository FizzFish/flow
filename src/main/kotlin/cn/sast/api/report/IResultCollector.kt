package cn.sast.api.report

import kotlin.coroutines.Continuation

public interface IResultCollector {
    public open suspend fun flush() {
    }

    internal object DefaultImpls {
        @JvmStatic
        fun flush(`$this`: IResultCollector, `$completion`: Continuation<Unit>): Any? {
            return Unit
        }
    }
}