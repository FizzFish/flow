package cn.sast.cli.config

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

@SourceDebugExtension(["SMAP\nFlowDroidSourceSinkTranslate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowDroidSourceSinkTranslate.kt\ncn/sast/cli/config/FlowDroidSourceSinkTranslatorCli\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,143:1\n1#2:144\n*E\n"])
public class FlowDroidSourceSinkTranslatorCli : CliktCommand(
      "help", null, "Flow Droid Source Sink Translator", false, false, null, null, false, false, false, 1002
   ) {
   public final val sourceSinkFile: String by OptionWithValuesKt.default$default(
         OptionWithValuesKt.option$default(
            this as ParameterHolder, new java.lang.String[0], "sourceSinkFile", null, false, null, null, null, null, false, 508, null
         ),
         "DEFAULT",
         null,
         2,
         null
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[0])
         public final get() {
         return this.sourceSinkFile$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.String;
      }


   public final val out: String by OptionWithValuesKt.default$default(
         OptionWithValuesKt.option$default(
            this as ParameterHolder, new java.lang.String[0], "sourceSinkFile", null, false, null, null, null, null, false, 508, null
         ),
         "out/flowdroid/Taint",
         null,
         2,
         null
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[1])
         public final get() {
         return this.out$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.String;
      }


   public open fun run() {
      val var10000: Path;
      if (this.getSourceSinkFile() == "DEFAULT") {
         logger.info(FlowDroidSourceSinkTranslatorCli::run$lambda$0);
         var10000 = FlowDroidSourceSinkTranslateKt.getFlowDroidClass().resolve("SourcesAndSinks.txt");
      } else {
         var10000 = Paths.get(this.getSourceSinkFile());
      }

      val file: File = var10000.toFile();
      if (!file.exists()) {
         throw new IllegalArgumentException(("[$var10000] not exists").toString());
      } else if (!file.isFile()) {
         throw new IllegalArgumentException(("[$var10000] not a file").toString());
      } else {
         val var10: java.lang.String = FilesKt.getExtension(file);
         val var10001: java.lang.String = file.getCanonicalPath();
         if (FlowDroidSourceSinkTranslateKt.getFlowDroidSourceSinkProvider(var10, var10001) == null) {
            throw new IllegalArgumentException(("[$var10000] not a valid flowdroid source sink file").toString());
         }
      }
   }

   @JvmStatic
   fun `run$lambda$0`(): Any {
      return "use default source sink file: ${FlowDroidSourceSinkTranslateKt.getFlowDroidLoc()}";
   }

   @JvmStatic
   fun `logger$lambda$4`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
