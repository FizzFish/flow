package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import mu.KLogger
import mu.KotlinLogging

public class Debug : SummaryHandlePackage<IValue> {
   public override fun ACheckCallAnalysis.register() {
      val logger: KLogger = KotlinLogging.INSTANCE.logger(Debug::register$lambda$0);
      `$this$register`.evalCallAtCaller(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), Debug::register$lambda$2);
      `$this$register`.evalCallAtCaller(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), Debug::register$lambda$4);
   }

   @JvmStatic
   fun `register$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$2$lambda$1`(): Any {
      return "debug break";
   }

   @JvmStatic
   fun `register$lambda$2`(`$logger`: KLogger, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall): Unit {
      `$logger`.debug(Debug::register$lambda$2$lambda$1);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$4$lambda$3`(`$res`: IHeapValues): Any {
      return "debug print($`$res`)";
   }

   @JvmStatic
   fun `register$lambda$4`(`$logger`: KLogger, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall): Unit {
      `$logger`.debug(Debug::register$lambda$4$lambda$3);
      return Unit.INSTANCE;
   }

   public companion object {
      public fun v(): Debug {
         return new Debug();
      }
   }
}
