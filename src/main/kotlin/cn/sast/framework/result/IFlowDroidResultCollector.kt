package cn.sast.framework.result

import cn.sast.api.report.IResultCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import soot.jimple.infoflow.handlers.ResultsAvailableHandler
import soot.jimple.infoflow.problems.TaintPropagationResults.OnTaintPropagationResultAdded

public interface IFlowDroidResultCollector : IResultCollector, OnTaintPropagationResultAdded, ResultsAvailableHandler {
   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun flush(`$this`: IFlowDroidResultCollector, `$completion`: Continuation<? super Unit>): Any? {
         val var10000: Any = IResultCollector.DefaultImpls.flush(`$this`, `$completion`);
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
      }
   }
}
