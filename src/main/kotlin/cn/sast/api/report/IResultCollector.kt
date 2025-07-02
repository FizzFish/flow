package cn.sast.api.report

import kotlin.coroutines.Continuation

public interface IResultCollector {
   public open suspend fun flush() {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun flush(`$this`: IResultCollector, `$completion`: Continuation<? super Unit>): Any? {
         return Unit.INSTANCE;
      }
   }
}
