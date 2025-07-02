package cn.sast.cli.command.tools

import cn.sast.framework.plugin.ConfigPluginLoader
import cn.sast.framework.plugin.PluginDefinitions
import com.feysh.corax.config.api.CheckType
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.FlagOptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import com.github.ajalt.clikt.parameters.types.BooleanKt
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import mu.KLogger

@SourceDebugExtension(["SMAP\nSubToolsOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SubToolsOptions.kt\ncn/sast/cli/command/tools/SubToolsOptions\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 SerialFormat.kt\nkotlinx/serialization/SerialFormatKt\n*L\n1#1,56:1\n1557#2:57\n1628#2,3:58\n1557#2:62\n1628#2,3:63\n1557#2:67\n1628#2,3:68\n1053#2:71\n113#3:61\n113#3:66\n*S KotlinDebug\n*F\n+ 1 SubToolsOptions.kt\ncn/sast/cli/command/tools/SubToolsOptions\n*L\n39#1:57\n39#1:58,3\n42#1:62\n42#1:63,3\n36#1:67\n36#1:68,3\n36#1:71\n39#1:61\n42#1:66\n*E\n"])
public class SubToolsOptions : OptionGroup("Sub tools Options", null, 2) {
   private final val subtools: Boolean by OptionWithValuesKt.required(
         BooleanKt.boolean(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[]{"--subtools"}, "subtools", "", false, null, null, null, null, false, 504, null
            )
         )
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[0])
         private final get() {
         return this.subtools$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Boolean;
      }


   private final val listRules: Boolean by FlagOptionKt.flag$default(
         OptionWithValuesKt.option$default(
            this as ParameterHolder, new java.lang.String[]{"--list-rules"}, "Print all the rules", null, false, null, null, null, null, false, 508, null
         ),
         new java.lang.String[0],
         false,
         null,
         6,
         null
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[1])
         private final get() {
         return this.listRules$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.Boolean;
      }


   private final val listCheckTypes: Boolean by FlagOptionKt.flag$default(
         OptionWithValuesKt.option$default(
            this as ParameterHolder, new java.lang.String[]{"--list-check-types"}, "Print all the rules", null, false, null, null, null, null, false, 508, null
         ),
         new java.lang.String[0],
         false,
         null,
         6,
         null
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[2])
         private final get() {
         return this.listCheckTypes$delegate.getValue(this, $$delegatedProperties[2]) as java.lang.Boolean;
      }


   public fun run(pl: ConfigPluginLoader, rules: Set<String>?) {
      if (this.getSubtools()) {
         val `checkTypes$delegate`: Lazy = LazyKt.lazy(SubToolsOptions::run$lambda$4);
         if (this.getListRules()) {
            val `$this$encodeToString$iv`: StringFormat = jsonFormat as StringFormat;
            val `value$iv`: java.lang.Iterable = run$lambda$5(`checkTypes$delegate`);
            val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`value$iv`, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add(CheckerInfoGeneratorKt.getId(`item$iv$iv` as CheckType));
            }

            val var19: java.util.List = `destination$iv$iv` as java.util.List;
            `$this$encodeToString$iv`.getSerializersModule();
            System.out.println(`$this$encodeToString$iv`.encodeToString((new ArrayListSerializer(StringSerializer.INSTANCE)) as SerializationStrategy, var19));
         }

         if (this.getListCheckTypes()) {
            val var17: StringFormat = jsonFormat as StringFormat;
            val var20: java.lang.Iterable = run$lambda$5(`checkTypes$delegate`);
            val var25: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var20, 10));

            for (Object item$iv$iv : $this$map$iv) {
               var25.add((var28 as CheckType).toString());
            }

            val var21: java.util.List = var25 as java.util.List;
            var17.getSerializersModule();
            System.out.println(var17.encodeToString((new ArrayListSerializer(StringSerializer.INSTANCE)) as SerializationStrategy, var21));
         }
      }

      System.exit(0);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }

   @JvmStatic
   fun `run$lambda$0`(`$pl`: ConfigPluginLoader): PluginDefinitions {
      return PluginDefinitions.Companion.load(`$pl`.getPluginManager());
   }

   @JvmStatic
   fun `run$lambda$1`(`$pluginDefinitions$delegate`: Lazy<PluginDefinitions>): PluginDefinitions {
      return `$pluginDefinitions$delegate`.getValue() as PluginDefinitions;
   }

   @JvmStatic
   fun `run$lambda$4`(`$pluginDefinitions$delegate`: Lazy): java.util.List {
      val `$this$sortedBy$iv`: java.lang.Iterable = run$lambda$1(`$pluginDefinitions$delegate`).getCheckTypeDefinition(CheckType.class);
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$sortedBy$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as PluginDefinitions.CheckTypeDefinition).getSingleton());
      }

      return CollectionsKt.sortedWith(`destination$iv$iv` as java.util.List, new SubToolsOptions$run$lambda$4$$inlined$sortedBy$1());
   }

   @JvmStatic
   fun `run$lambda$5`(`$checkTypes$delegate`: Lazy<? extends java.utilList<? extends CheckType>>): MutableList<CheckType> {
      return `$checkTypes$delegate`.getValue() as MutableList<CheckType>;
   }

   @JvmStatic
   fun `logger$lambda$8`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun JsonBuilder.`jsonFormat$lambda$9`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(true);
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
      private final val jsonFormat: Json
   }
}
