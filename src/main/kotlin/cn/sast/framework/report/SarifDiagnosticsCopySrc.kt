package cn.sast.framework.report

import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.SarifDiagnostics.SarifDiagnosticsImpl
import cn.sast.framework.report.sarif.ArtifactLocation
import cn.sast.framework.report.sarif.Description
import cn.sast.framework.report.sarif.Run
import cn.sast.framework.report.sarif.UriBase
import cn.sast.framework.result.OutputType
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.Arrays
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nSarifDiagnosticsCopySrc.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SarifDiagnosticsCopySrc.kt\ncn/sast/framework/report/SarifDiagnosticsCopySrc\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,85:1\n49#2,13:86\n62#2,11:101\n216#3,2:99\n*S KotlinDebug\n*F\n+ 1 SarifDiagnosticsCopySrc.kt\ncn/sast/framework/report/SarifDiagnosticsCopySrc\n*L\n62#1:86,13\n62#1:101,11\n63#1:99,2\n*E\n"])
public class SarifDiagnosticsCopySrc(outputDir: IResDirectory,
      sourceJarRootMapKey: String = "SRCROOT",
      sourceJarRootMapValue: String = "%SRCROOT%",
      sourceJarFileName: String = "src_root",
      type: OutputType = OutputType.SarifCopySrc
   ) : SarifDiagnostics(outputDir, type),
   Closeable {
   private final val sourceJarRootMapKey: String
   private final val sourceJarFileName: String
   private final val sourceRoot: IResDirectory
   public final val originalUriBaseIds: Map<String, UriBase>
   public final val entriesMap: ConcurrentHashMap<String, IResFile>

   init {
      this.sourceJarRootMapKey = sourceJarRootMapKey;
      this.sourceJarFileName = sourceJarFileName;
      this.sourceRoot = outputDir.resolve(this.sourceJarFileName).toDirectory();
      this.originalUriBaseIds = MapsKt.mapOf(
         TuplesKt.to(
            this.sourceJarRootMapKey,
            new UriBase(
               sourceJarRootMapValue,
               new Description(
                  "The path $sourceJarRootMapValue should be replaced with path where be mapped to the virtual path ${this.sourceRoot.getPath().toUri()}"
               )
            )
         )
      );
      this.entriesMap = new ConcurrentHashMap<>();
   }

   public override fun getSarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator): SarifDiagnosticsImpl {
      return new SarifDiagnosticsCopySrc.SarifDiagnosticsPackImpl(this, metadata, locator);
   }

   public override fun close() {
      label68: {
         val errorCnt: IntRef = new IntRef();
         val `$this$bracket_u24default$iv`: LoggerWithLogMethod = LoggingKt.info(logger);
         val `msg$iv`: java.lang.String = "${this.getType()}: copying ...";
         `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsCopySrc$close$$inlined$bracket$default$1(`msg$iv`));
         val `startTime$iv`: LocalDateTime = LocalDateTime.now();
         var `alreadyLogged$iv`: Boolean = false;
         val `res$iv`: ObjectRef = new ObjectRef();
         `res$iv`.element = Maybe.Companion.empty();

         try {
            try {
               val var11: <unknown>;
               while (var11.hasNext()) {
                  val `element$iv`: Entry = var11.next() as Entry;
                  val entry: java.lang.String = `element$iv`.getKey() as java.lang.String;
                  val file: IResFile = `element$iv`.getValue() as IResFile;
                  val target: Path = this.sourceRoot.resolve(entry).getPath();

                  try {
                     val var10000: Path = target.getParent();
                     if (var10000 == null) {
                        throw new IllegalStateException(("output not allow here: $target").toString());
                     }

                     val var10001: Array<LinkOption> = new LinkOption[0];
                     if (!Files.exists(var10000, Arrays.copyOf(var10001, var10001.length))) {
                        Files.createDirectories(var10000);
                     }

                     val var31: Path = Files.copy(file.getPath(), target);
                  } catch (var23: Exception) {
                     val var20: Int = errorCnt.element++;
                     if (errorCnt.element < 5) {
                        logger.warn(var23, SarifDiagnosticsCopySrc::close$lambda$2$lambda$1$lambda$0);
                     }
                  }
               }

               val var21: <unknown>;
               var21.element = new Maybe(Unit.INSTANCE);
               val var8: Any = (`res$iv`.element as Maybe).getOrThrow();
            } catch (var24: java.lang.Throwable) {
               `$this$bracket_u24default$iv`.getLogMethod()
                  .invoke(new SarifDiagnosticsCopySrc$close$$inlined$bracket$default$4(`startTime$iv`, `msg$iv`, var24));
               `alreadyLogged$iv` = true;
               throw var24;
            }
         } catch (var25: java.lang.Throwable) {
            if (!`alreadyLogged$iv`) {
               if ((`res$iv`.element as Maybe).getHasValue()) {
                  `$this$bracket_u24default$iv`.getLogMethod()
                     .invoke(new SarifDiagnosticsCopySrc$close$$inlined$bracket$default$5(`startTime$iv`, `msg$iv`, `res$iv`));
               } else {
                  `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsCopySrc$close$$inlined$bracket$default$6(`startTime$iv`, `msg$iv`));
               }
            }
         }

         if ((`res$iv`.element as Maybe).getHasValue()) {
            `$this$bracket_u24default$iv`.getLogMethod()
               .invoke(new SarifDiagnosticsCopySrc$close$$inlined$bracket$default$2(`startTime$iv`, `msg$iv`, `res$iv`));
         } else {
            `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsCopySrc$close$$inlined$bracket$default$3(`startTime$iv`, `msg$iv`));
         }

         if (errorCnt.element > 0) {
            logger.warn(SarifDiagnosticsCopySrc::close$lambda$3);
         }
      }
   }

   @JvmStatic
   fun `close$lambda$2$lambda$1$lambda$0`(): Any {
      return "An error occurred";
   }

   @JvmStatic
   fun `close$lambda$3`(`this$0`: SarifDiagnosticsCopySrc, `$errorCnt`: IntRef): Any {
      return "${`this$0`.getType()}: A total of ${`$errorCnt`.element} errors were generated";
   }

   @JvmStatic
   fun `logger$lambda$4`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger
   }

   public open inner class SarifDiagnosticsPackImpl(metadata: MetaData, locator: IProjectFileLocator) : SarifDiagnostics.SarifDiagnosticsImpl(
         `this$0`, metadata, locator
      ) {
      public open val file2uri: String
         public open get() {
            val entry: java.lang.String = this.getAbsPathMapToFolder(`$this$file2uri`);
            this.this$0.getEntriesMap().putIfAbsent(entry, `$this$file2uri`);
            return entry;
         }


      init {
         this.this$0 = `this$0`;
      }

      public override fun getArtifactLocation(file: IResFile): ArtifactLocation {
         return ArtifactLocation.copy$default(
            super.getArtifactLocation(file), null, SarifDiagnosticsCopySrc.access$getSourceJarRootMapKey$p(this.this$0), 1, null
         );
      }

      public override fun getRun(reports: List<Report>): Run {
         return Run.copy$default(super.getRun(reports), null, this.this$0.getOriginalUriBaseIds(), null, null, 13, null);
      }
   }
}
