package cn.sast.cli.command.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.Context.Builder
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.client.ICsvFileWriter
import com.github.doyaaaaaken.kotlincsv.dsl.CsvReaderDslKt
import com.github.doyaaaaaken.kotlincsv.dsl.CsvWriterDslKt
import java.io.File
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nUtilsCli.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UtilsCli.kt\ncn/sast/cli/command/tools/UtilsCli\n+ 2 EagerOption.kt\ncom/github/ajalt/clikt/parameters/options/EagerOptionKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,59:1\n65#2,10:60\n1368#3:70\n1454#3,5:71\n2632#3,3:76\n1628#3,3:80\n1863#3:83\n1797#3,3:84\n1864#3:87\n1#4:79\n*S KotlinDebug\n*F\n+ 1 UtilsCli.kt\ncn/sast/cli/command/tools/UtilsCli\n*L\n18#1:60,10\n28#1:70\n28#1:71,5\n29#1:76,3\n34#1:80,3\n38#1:83\n39#1:84,3\n38#1:87\n*E\n"])
public class UtilsCli : CliktCommand(null, null, "Utils", false, false, null, null, false, false, false, 1019) {
   private final val input: File?
      private final get() {
         return this.input$delegate.getValue(this, $$delegatedProperties[0]) as File;
      }


   private final val output: File?
      private final get() {
         return this.output$delegate.getValue(this, $$delegatedProperties[1]) as File;
      }


   private final val csvDeleteColumns: List<String>
      private final get() {
         return this.csvDeleteColumns$delegate.getValue(this, $$delegatedProperties[2]) as MutableList<java.lang.String>;
      }


   public open fun run() {
      if (!this.getCsvDeleteColumns().isEmpty()) {
         var rows: java.lang.Iterable = this.getCsvDeleteColumns();
         val `$this$mapTo$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$flatMap$iv) {
            CollectionsKt.addAll(`$this$mapTo$iv`, StringsKt.split$default(`element$iv$iv` as java.lang.String, new java.lang.String[]{","}, false, 0, 6, null));
         }

         val csvDeleteColumnNames: java.util.List = `$this$mapTo$iv` as java.util.List;
         rows = `$this$mapTo$iv` as java.util.List;
         var var38: Boolean;
         if (`$this$mapTo$iv` as java.util.List is java.util.Collection && ((`$this$mapTo$iv` as java.util.List) as java.util.Collection).isEmpty()) {
            var38 = true;
         } else {
            label105: {
               for (Object element$iv : $this$flatMap$iv) {
                  if ((var27 as java.lang.String).length() == 0) {
                     var38 = false;
                     break label105;
                  }
               }

               var38 = true;
            }
         }

         if (!var38) {
            throw new IllegalStateException(("$csvDeleteColumnNames has a empty string element").toString());
         }

         if (this.getInput() == null) {
            throw new IllegalStateException("input is required".toString());
         }

         if (this.getOutput() == null) {
            throw new IllegalStateException("output is required".toString());
         }

         val var39: CsvReader = CsvReaderDslKt.csvReader$default(null, 1, null);
         var var10001: File = this.getInput();
         val var17: java.util.List = var39.readAll(var10001);
         val var19: java.util.List = CollectionsKt.first(var17) as java.util.List;
         val var28: java.lang.Iterable = csvDeleteColumnNames;
         val var30: java.util.Collection = new LinkedHashSet();

         for (Object item$iv : var28) {
            val var36: java.lang.String = var35 as java.lang.String;
            val var12: Int = var19.indexOf(var35 as java.lang.String);
            val it: Int = var12.intValue();
            val var40: Int = if (it >= 0) var12 else null;
            if ((if (it >= 0) var12 else null) == null) {
               throw new IllegalStateException(("$var36 not exists in header: $var19").toString());
            }

            var30.add(var40);
         }

         val var26: java.util.List = CollectionsKt.reversed(CollectionsKt.sorted(var30));
         val var41: CsvWriter = CsvWriterDslKt.csvWriter$default(null, 1, null);
         var10001 = this.getOutput();
         CsvWriter.open$default(var41, var10001, false, UtilsCli::run$lambda$11, 2, null);
      }
   }

   @JvmStatic
   fun `lambda$1$lambda$0`(it: Context): MordantHelpFormatter {
      return new MordantHelpFormatter(it, null, true, true, 2, null);
   }

   @JvmStatic
   fun Builder.`_init_$lambda$1`(): Unit {
      `$this$context`.setHelpFormatter(UtilsCli::lambda$1$lambda$0);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `run$lambda$11`(`$rows`: java.util.List, `$columnIdxToDelete`: java.util.List, `$this$open`: ICsvFileWriter): Unit {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val it: java.util.List = `element$iv` as java.util.List;
         val `$this$fold$iv`: java.lang.Iterable = `$columnIdxToDelete`;
         var `accumulator$iv`: Any = CollectionsKt.toMutableList(it);

         for (Object element$ivx : $this$fold$iv) {
            `accumulator$iv`.remove((`element$ivx` as java.lang.Number).intValue());
            `accumulator$iv` = `accumulator$iv`;
         }

         `$this$open`.writeRow((java.util.List)`accumulator$iv`);
      }

      return Unit.INSTANCE;
   }
}
