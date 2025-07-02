package cn.sast.cli.command

import cn.sast.common.IResDirectory
import cn.sast.common.Resource
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,164:1\n237#2:165\n1#3:166\n*E\n"])
internal class `FySastCli$special$$inlined$convert$default$6` : Lambda, Function2<OptionCallTransformContext, java.lang.String, IResDirectory> {
   fun `FySastCli$special$$inlined$convert$default$6`(`$receiver`: OptionWithValues) {
      super(2);
      this.$this_convert = `$receiver`;
   }

   // QF: local property
internal fun <InT : Any, ValueT : Any> OptionCallTransformContext.`<anonymous>`(it: String): ValueT {
      try {
         val itx: java.lang.String = this.$this_convert.getTransformValue().invoke(`$this$null`, it) as java.lang.String;
         if (itx.length() <= 0) {
            throw new IllegalStateException("Check failed.".toString());
         } else {
            return Resource.INSTANCE.dirOf(itx);
         }
      } catch (var14: UsageError) {
         var var10000: UsageError = var14;
         var var16: java.lang.String = var14.getParamName();
         if (var16 == null) {
            val var9: java.lang.String = `$this$null`.getName();
            val var13: Boolean = var9.length() == 0;
            var10000 = var14;
            var16 = if (!var13) var9 else null;
            if ((if (!var13) var9 else null) == null) {
               var16 = OptionKt.longestName(`$this$null`.getOption());
            }
         }

         var10000.setParamName(var16);
         throw var14;
      } catch (var15: Exception) {
         var var10001: java.lang.String = var15.getMessage();
         if (var10001 == null) {
            var10001 = "";
         }

         `$this$null`.fail(var10001);
         throw new KotlinNothingValueException();
      }
   }
}
