package cn.sast.framework.report

import cn.sast.framework.result.OutputType
import java.io.Closeable
import kotlin.coroutines.Continuation

public interface IReportConsumer : Closeable {
   public val type: OutputType

   public abstract suspend fun init() {
   }

   public open suspend fun run(locator: IProjectFileLocator) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun run(`$this`: IReportConsumer, locator: IProjectFileLocator, `$completion`: Continuation<? super Unit>): Any? {
         return Unit.INSTANCE;
      }
   }
}
