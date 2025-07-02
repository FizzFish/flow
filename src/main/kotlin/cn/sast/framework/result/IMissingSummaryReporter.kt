package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import soot.SootMethod

public interface IMissingSummaryReporter : IResultCollector {
   public open fun reportMissingMethod(method: SootMethod) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun reportMissingMethod(`$this`: IMissingSummaryReporter, method: SootMethod) {
      }

      @JvmStatic
      fun flush(`$this`: IMissingSummaryReporter, `$completion`: Continuation<? super Unit>): Any? {
         val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`);
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
      }
   }
}
