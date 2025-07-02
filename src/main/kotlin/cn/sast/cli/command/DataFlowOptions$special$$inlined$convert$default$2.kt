package cn.sast.cli.command

import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 DataFlowOptions.kt\ncn/sast/cli/command/DataFlowOptions\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,164:1\n10#2:165\n1#3:166\n*E\n"])
internal class `DataFlowOptions$special$$inlined$convert$default$2` : Lambda, Function2<OptionCallTransformContext, java.lang.String, java.lang.Boolean> {
   fun `DataFlowOptions$special$$inlined$convert$default$2`(`$receiver`: OptionWithValues) {
      super(2);
      this.$this_convert = `$receiver`;
   }

   // QF: local property
internal fun <InT : Any, ValueT : Any> OptionCallTransformContext.`<anonymous>`(it: String): ValueT {
      try {
         return java.lang.Boolean.parseBoolean(this.$this_convert.getTransformValue().invoke(`$this$null`, it) as java.lang.String);
      } catch (var13: UsageError) {
         var var10000: UsageError = var13;
         var var15: java.lang.String = var13.getParamName();
         if (var15 == null) {
            val var8: java.lang.String = `$this$null`.getName();
            val var12: Boolean = var8.length() == 0;
            var10000 = var13;
            var15 = if (!var12) var8 else null;
            if ((if (!var12) var8 else null) == null) {
               var15 = OptionKt.longestName(`$this$null`.getOption());
            }
         }

         var10000.setParamName(var15);
         throw var13;
      } catch (var14: Exception) {
         var var10001: java.lang.String = var14.getMessage();
         if (var10001 == null) {
            var10001 = "";
         }

         `$this$null`.fail(var10001);
         throw new KotlinNothingValueException();
      }
   }
}
