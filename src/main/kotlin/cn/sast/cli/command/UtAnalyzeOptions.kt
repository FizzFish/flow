package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nUtAnalyzeOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UtAnalyzeOptions.kt\ncn/sast/cli/command/UtAnalyzeOptions\n+ 2 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt\n*L\n1#1,16:1\n35#2,6:17\n70#2:23\n82#2,4:24\n*S KotlinDebug\n*F\n+ 1 UtAnalyzeOptions.kt\ncn/sast/cli/command/UtAnalyzeOptions\n*L\n12#1:17,6\n12#1:23\n12#1:24,4\n*E\n"])
public class UtAnalyzeOptions : OptionGroup("UtAnalyze Options", null, 2) {
   public final val enableUtAnalyze: Boolean
      public final get() {
         return this.enableUtAnalyze$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Boolean;
      }

}
