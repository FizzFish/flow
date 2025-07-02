package cn.sast.framework.report

import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.JarMerger
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.SarifDiagnostics.SarifDiagnosticsImpl
import cn.sast.framework.report.sarif.ArtifactLocation
import cn.sast.framework.report.sarif.Description
import cn.sast.framework.report.sarif.Run
import cn.sast.framework.report.sarif.UriBase
import cn.sast.framework.result.OutputType
import java.io.Closeable
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nSarifDiagnosticsPack.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SarifDiagnosticsPack.kt\ncn/sast/framework/report/SarifDiagnosticsPack\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,92:1\n49#2,13:93\n62#2,11:108\n216#3,2:106\n*S KotlinDebug\n*F\n+ 1 SarifDiagnosticsPack.kt\ncn/sast/framework/report/SarifDiagnosticsPack\n*L\n71#1:93,13\n71#1:108,11\n72#1:106,2\n*E\n"])
public class SarifDiagnosticsPack(outputDir: IResDirectory,
      sourceJarRootMapKey: String = "SRCROOT",
      sourceJarRootMapValue: String = "%SRCROOT%",
      sourceJarFileName: String = "src_root",
      type: OutputType = OutputType.SarifPackSrc
   ) : SarifDiagnostics(outputDir, type),
   Closeable {
   private final val sourceJarRootMapKey: String
   private final val sourceJarFileName: String
   private final lateinit var sourceJarPath: IResFile
   private final lateinit var sourceJar: JarMerger
   public final val originalUriBaseIds: Map<String, UriBase>
   public final val entriesMap: ConcurrentHashMap<String, IResFile>

   init {
      this.sourceJarRootMapKey = sourceJarRootMapKey;
      this.sourceJarFileName = sourceJarFileName;
      this.originalUriBaseIds = MapsKt.mapOf(
         TuplesKt.to(
            this.sourceJarRootMapKey,
            new UriBase(
               sourceJarRootMapValue,
               new Description(
                  "Should replace $sourceJarRootMapValue with file:///{absolute-uncompressed-path-of-${this.sourceJarFileName}.jar}/${this.sourceJarFileName}/"
               )
            )
         )
      );
      this.entriesMap = new ConcurrentHashMap<>();
   }

   public override suspend fun init() {
      var `$continuation`: Continuation;
      label28: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label28;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.init(this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var4: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            `$continuation`.L$0 = this;
            `$continuation`.label = 1;
            if (super.init(`$continuation`) === var4) {
               return var4;
            }
            break;
         case 1:
            this = `$continuation`.L$0 as SarifDiagnosticsPack;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      this.sourceJarPath = this.getOutputDir().resolve("${this.sourceJarFileName}.jar").toFile();
      var var10000: IResFile = this.sourceJarPath;
      if (this.sourceJarPath == null) {
         Intrinsics.throwUninitializedPropertyAccessException("sourceJarPath");
         var10000 = null;
      }

      Files.deleteIfExists(var10000.getPath());
      val var10001: JarMerger = new JarMerger;
      var var10003: IResFile = this.sourceJarPath;
      if (this.sourceJarPath == null) {
         Intrinsics.throwUninitializedPropertyAccessException("sourceJarPath");
         var10003 = null;
      }

      var10001./* $VF: Unable to resugar constructor */<init>(var10003.getPath(), null, 2, null);
      this.sourceJar = var10001;
      return Unit.INSTANCE;
   }

   public override fun getSarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator): SarifDiagnosticsImpl {
      return new SarifDiagnosticsPack.SarifDiagnosticsPackImpl(this, metadata, locator);
   }

   public override fun close() {
      label66: {
         val errorCnt: IntRef = new IntRef();
         val `$this$bracket_u24default$iv`: LoggerWithLogMethod = LoggingKt.info(logger);
         val `msg$iv`: java.lang.String = "${this.getType()}: Compressing ...";
         `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsPack$close$$inlined$bracket$default$1(`msg$iv`));
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

                  try {
                     var var10000: JarMerger = this.sourceJar;
                     if (this.sourceJar == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("sourceJar");
                        var10000 = null;
                     }

                     var10000.addFile(entry, file.getPath());
                  } catch (var21: Exception) {
                     val var18: Int = errorCnt.element++;
                     if (errorCnt.element < 5) {
                        logger.warn(var21, SarifDiagnosticsPack::close$lambda$2$lambda$1$lambda$0);
                     }
                  }
               }

               val var19: <unknown>;
               var19.element = new Maybe(Unit.INSTANCE);
               val var8: Any = (`res$iv`.element as Maybe).getOrThrow();
            } catch (var22: java.lang.Throwable) {
               `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsPack$close$$inlined$bracket$default$4(`startTime$iv`, `msg$iv`, var22));
               `alreadyLogged$iv` = true;
               throw var22;
            }
         } catch (var23: java.lang.Throwable) {
            if (!`alreadyLogged$iv`) {
               if ((`res$iv`.element as Maybe).getHasValue()) {
                  `$this$bracket_u24default$iv`.getLogMethod()
                     .invoke(new SarifDiagnosticsPack$close$$inlined$bracket$default$5(`startTime$iv`, `msg$iv`, `res$iv`));
               } else {
                  `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsPack$close$$inlined$bracket$default$6(`startTime$iv`, `msg$iv`));
               }
            }
         }

         if ((`res$iv`.element as Maybe).getHasValue()) {
            `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsPack$close$$inlined$bracket$default$2(`startTime$iv`, `msg$iv`, `res$iv`));
         } else {
            `$this$bracket_u24default$iv`.getLogMethod().invoke(new SarifDiagnosticsPack$close$$inlined$bracket$default$3(`startTime$iv`, `msg$iv`));
         }

         if (errorCnt.element > 0) {
            logger.warn(SarifDiagnosticsPack::close$lambda$3);
         }

         var var29: JarMerger = this.sourceJar;
         if (this.sourceJar == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sourceJar");
            var29 = null;
         }

         var29.close();
      }
   }

   @JvmStatic
   fun `close$lambda$2$lambda$1$lambda$0`(): Any {
      return "An error occurred";
   }

   @JvmStatic
   fun `close$lambda$3`(`this$0`: SarifDiagnosticsPack, `$errorCnt`: IntRef): Any {
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
         return ArtifactLocation.copy$default(super.getArtifactLocation(file), null, SarifDiagnosticsPack.access$getSourceJarRootMapKey$p(this.this$0), 1, null);
      }

      public override fun getRun(reports: List<Report>): Run {
         return Run.copy$default(super.getRun(reports), null, this.this$0.getOriginalUriBaseIds(), null, null, 13, null);
      }
   }
}
