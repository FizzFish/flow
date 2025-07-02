package cn.sast.cli.command.tools

import cn.sast.common.IResFile
import cn.sast.common.Resource
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import com.github.ajalt.clikt.parameters.types.FileKt
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.Arrays
import mu.KLogger

public class CheckerInfoCompareOptions : OptionGroup("Compare checker_info.json Options", null, 2) {
   private final val compareRight: File by OptionWithValuesKt.required(
         FileKt.file$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder,
               new java.lang.String[0],
               "Compare and diff an other checker_info.json",
               null,
               false,
               null,
               null,
               null,
               null,
               false,
               508,
               null
            ),
            true,
            false,
            false,
            false,
            false,
            false,
            58,
            null
         )
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[0])
         private final get() {
         return this.compareRight$delegate.getValue(this, $$delegatedProperties[0]) as File;
      }


   private final val compareLeft: File by OptionWithValuesKt.required(
         FileKt.file$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder,
               new java.lang.String[0],
               "Compare and diff an other checker_info.json",
               null,
               false,
               null,
               null,
               null,
               null,
               false,
               508,
               null
            ),
            true,
            false,
            false,
            false,
            false,
            false,
            58,
            null
         )
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[1])
         private final get() {
         return this.compareLeft$delegate.getValue(this, $$delegatedProperties[1]) as File;
      }


   public fun run() {
      val `output$delegate`: Lazy = LazyKt.lazy(CheckerInfoCompareOptions::run$lambda$3);
      val var10000: CheckerInfoCompare = new CheckerInfoCompare();
      val var10001: Path = run$lambda$4(`output$delegate`);
      val var10002: Resource = Resource.INSTANCE;
      val var10003: java.lang.String = this.getCompareLeft().getPath();
      val var2: IResFile = var10002.fileOf(var10003);
      val var3: Resource = Resource.INSTANCE;
      val var10004: java.lang.String = this.getCompareRight().getPath();
      var10000.compareWith(var10001, var2, var3.fileOf(var10004));
      System.exit(0);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }

   @JvmStatic
   fun `run$lambda$3$lambda$2$lambda$1`(`$it`: Path): Any {
      return "The compare output path is: $`$it`";
   }

   @JvmStatic
   fun `run$lambda$3`(`this$0`: CheckerInfoCompareOptions): Path {
      var var1: Path = `this$0`.getCompareLeft().toPath().getParent().resolve("compare-result");
      val var10001: Array<LinkOption> = new LinkOption[0];
      if (!Files.exists(var1, Arrays.copyOf(var10001, var10001.length))) {
         Files.createDirectories(var1);
      }

      var1 = var1.normalize();
      logger.info(CheckerInfoCompareOptions::run$lambda$3$lambda$2$lambda$1);
      return var1;
   }

   @JvmStatic
   fun `run$lambda$4`(`$output$delegate`: Lazy<? extends Path>): Path {
      return `$output$delegate`.getValue() as Path;
   }

   @JvmStatic
   fun `logger$lambda$5`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
