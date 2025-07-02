package cn.sast.cli.command.tools

import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.framework.plugin.ConfigPluginLoader
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import mu.KLogger

public class CheckerInfoGeneratorOptions : OptionGroup("Generate checker_info.json for CoraxServer Options", null, 2) {
   private final val genCheckerInfoJson: Boolean
      private final get() {
         return this.genCheckerInfoJson$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Boolean;
      }


   private final val language: List<String>
      private final get() {
         return this.language$delegate.getValue(this, $$delegatedProperties[1]) as MutableList<java.lang.String>;
      }


   public fun run(pl: ConfigPluginLoader, rules: Set<String>?) {
      if (this.getGenCheckerInfoJson()) {
         val var10000: CheckerInfoGenerator = CheckerInfoGenerator.Companion.createCheckerInfoGenerator$default(
            CheckerInfoGenerator.Companion, pl, this.getLanguage(), false, 4, null
         );
         if (var10000 == null) {
            System.exit(2);
            throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
         }

         val checkerInfo: CheckerInfoGenResult = CheckerInfoGenerator.getCheckerInfo$default(var10000, false, 1, null);
         var10000.dumpCheckerInfoJson(checkerInfo, true);
         var10000.dumpRuleAndRuleMappingDB(checkerInfo, rules);
      }

      System.exit(0);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }

   @JvmStatic
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
