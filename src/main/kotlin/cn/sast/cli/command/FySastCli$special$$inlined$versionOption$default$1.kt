package cn.sast.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nEagerOption.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EagerOption.kt\ncom/github/ajalt/clikt/parameters/options/EagerOptionKt$versionOption$2\n+ 2 EagerOption.kt\ncom/github/ajalt/clikt/parameters/options/EagerOptionKt$versionOption$1\n*L\n1#1,75:1\n73#2:76\n*E\n"])
internal class `FySastCli$special$$inlined$versionOption$default$1` : Lambda, Function1<OptionTransformContext, Unit> {
   fun `FySastCli$special$$inlined$versionOption$default$1`(`$version`: java.lang.String, var2: CliktCommand) {
      super(1);
      this.$version = `$version`;
      this.$this_versionOption$inlined = var2;
   }

   // QF: local property
internal fun <T : CliktCommand> OptionTransformContext.`<anonymous>`() {
      val it: java.lang.String = this.$version;
      throw new PrintMessage("${this.$this_versionOption$inlined.getCommandName()} version $it", 0, false, 6, null);
   }
}
