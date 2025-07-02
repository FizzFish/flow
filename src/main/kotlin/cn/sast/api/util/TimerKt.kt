package cn.sast.api.util

import kotlin.jvm.internal.InlineMarker

public inline fun <T> PhaseIntervalTimer?.bracket(block: () -> T): T {
   if (`$this$bracket` == null) {
      return (T)block.invoke();
   } else {
      label28: {
         val s: PhaseIntervalTimer.Snapshot = `$this$bracket`.start();

         try {
            val var4: Any = block.invoke();
         } catch (var6: java.lang.Throwable) {
            InlineMarker.finallyStart(1);
            `$this$bracket`.stop(s);
            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         `$this$bracket`.stop(s);
         InlineMarker.finallyEnd(1);
      }
   }
}
