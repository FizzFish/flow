package cn.sast.cli.command

import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import java.io.File
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nValidate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$validate$1\n+ 2 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n+ 3 AndroidOptions.kt\ncn/sast/cli/command/AndroidOptions\n+ 4 OptionWithValues.kt\ncom/github/ajalt/clikt/parameters/options/OptionTransformContext\n+ 5 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt$check$2\n*L\n1#1,71:1\n69#2:72\n20#3:73\n55#4:74\n56#4:76\n66#5:75\n*S KotlinDebug\n*F\n+ 1 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n*L\n69#1:74\n69#1:76\n*E\n"])
internal class `AndroidOptions$special$$inlined$check$default$1` : Lambda, Function2<OptionTransformContext, File, Unit> {
   fun `AndroidOptions$special$$inlined$check$default$1`() {
      super(2);
   }

   // QF: local property
internal fun <AllT, EachT, ValueT> OptionTransformContext.`<anonymous>`(it: AllT) {
      if (it != null && !((it as File).isDirectory() or FilesKt.getExtension(it as File) == "jar")) {
         `$this$copy`.fail(it.toString());
         throw new KotlinNothingValueException();
      }
   }
}
