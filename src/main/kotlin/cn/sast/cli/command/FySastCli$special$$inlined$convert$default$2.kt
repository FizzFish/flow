package cn.sast.cli.command

import cn.sast.common.IResFile
import cn.sast.common.Resource
import cn.sast.common.ResourceKt
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionCallTransformContext
import com.github.ajalt.clikt.parameters.options.OptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Lambda
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nConvert.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt$convert$valueTransform$1\n+ 2 FySastCli.kt\ncn/sast/cli/command/FySastCli\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,164:1\n118#2,2:165\n120#2,5:168\n1#3:167\n1557#4:173\n1628#4,3:174\n774#4:177\n865#4,2:178\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli\n*L\n124#1:173\n124#1:174,3\n124#1:177\n124#1:178,2\n*E\n"])
internal class `FySastCli$special$$inlined$convert$default$2`
   : Lambda,
   Function2<OptionCallTransformContext, java.lang.String, java.util.List<? extends java.lang.String>> {
   fun `FySastCli$special$$inlined$convert$default$2`(`$receiver`: OptionWithValues) {
      super(2);
      this.$this_convert = `$receiver`;
   }

   // QF: local property
internal fun <InT : Any, ValueT : Any> OptionCallTransformContext.`<anonymous>`(it: String): ValueT {
      try {
         val option: java.lang.String = this.$this_convert.getTransformValue().invoke(`$this$null`, it) as java.lang.String;

         var var6: IResFile;
         try {
            var6 = Resource.INSTANCE.fileOf(option);
            var6 = if (var6.getExists() && var6.isFile()) var6 else null;
         } catch (var25: Exception) {
            var6 = null;
         }

         if (var6 != null) {
            val var29: java.util.List = new Gson()
               .fromJson(ResourceKt.readText$default(var6, null, 1, null), (new TypeToken<java.util.List<? extends java.lang.String>>() {}).getType()) as java.util.List;
            if (var29 != null) {
               return var29;
            }
         }

         var var30: java.lang.Iterable = StringsKt.split$default(StringsKt.removeSurrounding(option, "[", "]"), new java.lang.String[]{","}, false, 0, 6, null);
         var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var30, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(StringsKt.trim(`element$iv$iv` as java.lang.String).toString());
         }

         var30 = `destination$iv$iv` as java.util.List;
         `destination$iv$iv` = new ArrayList();

         for (Object element$iv$iv : $this$map$iv) {
            if ((var37 as java.lang.String).length() > 0) {
               `destination$iv$iv`.add(var37);
            }
         }

         return `destination$iv$iv` as MutableList<java.lang.String>;
      } catch (var26: UsageError) {
         var var10000: UsageError = var26;
         var var42: java.lang.String = var26.getParamName();
         if (var42 == null) {
            val var20: java.lang.String = `$this$null`.getName();
            val var24: Boolean = var20.length() == 0;
            var10000 = var26;
            var42 = if (!var24) var20 else null;
            if ((if (!var24) var20 else null) == null) {
               var42 = OptionKt.longestName(`$this$null`.getOption());
            }
         }

         var10000.setParamName(var42);
         throw var26;
      } catch (var27: Exception) {
         var var10001: java.lang.String = var27.getMessage();
         if (var10001 == null) {
            var10001 = "";
         }

         `$this$null`.fail(var10001);
         throw new KotlinNothingValueException();
      }
   }
}
