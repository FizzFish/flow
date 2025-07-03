package cn.sast.api.util

import kotlin.jvm.internal.InlineMarker

public inline fun <T> PhaseIntervalTimer?.bracket(block: () -> T): T {
    if (this == null) {
        return block()
    } else {
        val s: PhaseIntervalTimer.Snapshot = this.start()
        try {
            return block()
        } catch (t: Throwable) {
            InlineMarker.finallyStart(1)
            this.stop(s)
            InlineMarker.finallyEnd(1)
            throw t
        } finally {
            InlineMarker.finallyStart(1)
            this.stop(s)
            InlineMarker.finallyEnd(1)
        }
    }
}