package cn.sast.dataflow.util

import com.feysh.corax.config.api.AIAnalysisApi
import java.util.ArrayList
import mu.KLogger
import mu.KotlinLogging

public class ConfigInfoLogger : AIAnalysisApi.Error {
   public final val errors: MutableList<String> = (new ArrayList()) as java.util.List
   public final val warnings: MutableList<String> = (new ArrayList()) as java.util.List

   public open val logger: KLogger = KotlinLogging.INSTANCE.logger(ConfigInfoLogger::logger$lambda$2)
      public open get() {
         return logger;
      }


   public override fun error(msg: String) {
      this.getLogger().error(ConfigInfoLogger::error$lambda$0);
      this.errors.add(msg);
   }

   public override fun warning(msg: String) {
      this.getLogger().warn(ConfigInfoLogger::warning$lambda$1);
      this.warnings.add(msg);
   }

   @JvmStatic
   fun `error$lambda$0`(`$msg`: java.lang.String): Any {
      return `$msg`;
   }

   @JvmStatic
   fun `warning$lambda$1`(`$msg`: java.lang.String): Any {
      return `$msg`;
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
