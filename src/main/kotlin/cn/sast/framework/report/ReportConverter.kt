package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.report.Report
import cn.sast.api.util.IMonitor
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.coverage.JacocoCompoundCoverage
import cn.sast.idfa.progressbar.ProgressBarExt
import com.feysh.corax.config.api.rules.ProcessRule
import com.github.ajalt.mordant.rendering.Theme
import java.io.Closeable
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.Scene
import soot.SootClass
import soot.util.Chain

@SourceDebugExtension(["SMAP\nReportConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReportConverter.kt\ncn/sast/framework/report/ReportConverter\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,391:1\n774#2:392\n865#2,2:393\n774#2:395\n865#2,2:396\n1279#2,2:398\n1293#2,4:400\n1053#2:404\n*S KotlinDebug\n*F\n+ 1 ReportConverter.kt\ncn/sast/framework/report/ReportConverter\n*L\n53#1:392\n53#1:393,2\n83#1:395\n83#1:396,2\n84#1:398,2\n84#1:400,4\n96#1:404\n*E\n"])
public class ReportConverter(mainConfig: MainConfig, progressBarExt: ProgressBarExt = new ProgressBarExt(0, 0, 3, null)) {
   public final val mainConfig: MainConfig
   private final val progressBarExt: ProgressBarExt

   init {
      this.mainConfig = mainConfig;
      this.progressBarExt = progressBarExt;
   }

   private fun filterSourceFiles(sources: Collection<IResFile?>): Set<IResFile> {
      val `$this$filter$iv`: java.lang.Iterable = sources;
      val `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filter$iv) {
         val it: IResFile = `element$iv$iv` as IResFile;
         if (`element$iv$iv` as IResFile != null
            && (!((`element$iv$iv` as IResFile).getExtension() == "kts") || !StringsKt.contains((`element$iv$iv` as IResFile).getName(), "gradle", true))
            && !StringsKt.contains((`element$iv$iv` as IResFile).getName(), "package-info", true)
            && ScanFilter.getActionOf$default(this.mainConfig.getScanFilter(), null, (`element$iv$iv` as IResFile).getPath(), null, 4, null)
               != ProcessRule.ScanAction.Skip) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      return CollectionsKt.filterNotNullTo(`destination$iv$iv` as java.util.List, new LinkedHashSet()) as MutableSet<IResFile>;
   }

   private suspend fun findAllSourceFiles(locator: IProjectFileLocator): Set<IResFile> {
      var `$continuation`: Continuation;
      label33: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label33;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            Object L$3;
            Object L$4;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return ReportConverter.access$findAllSourceFiles(this.this$0, null, this as Continuation);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var allSourceFiles: java.util.Set;
      var var4: java.util.Iterator;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            allSourceFiles = new LinkedHashSet();
            var4 = ResourceKt.getJavaExtensions().iterator();
            break;
         case 1:
            val var6: java.util.Collection = `$continuation`.L$4 as java.util.Collection;
            var4 = `$continuation`.L$3 as java.util.Iterator;
            allSourceFiles = `$continuation`.L$2 as java.util.Set;
            locator = `$continuation`.L$1 as IProjectFileLocator;
            this = `$continuation`.L$0 as ReportConverter;
            ResultKt.throwOnFailure(`$result`);
            CollectionsKt.addAll(var6, SequencesKt.filter(`$result` as Sequence, ReportConverter::findAllSourceFiles$lambda$1));
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (var4.hasNext()) {
         val javaExtension: java.lang.String = var4.next() as java.lang.String;
         val var10: java.util.Collection = allSourceFiles;
         `$continuation`.L$0 = this;
         `$continuation`.L$1 = locator;
         `$continuation`.L$2 = allSourceFiles;
         `$continuation`.L$3 = var4;
         `$continuation`.L$4 = var10;
         `$continuation`.label = 1;
         val var10000: Any = locator.getByFileExtension(javaExtension, `$continuation`);
         if (var10000 === var9) {
            return var9;
         }

         CollectionsKt.addAll(var10, SequencesKt.filter(var10000 as Sequence, ReportConverter::findAllSourceFiles$lambda$1));
      }

      return allSourceFiles;
   }

   private fun reportSourceFileWhichClassNotFound(allSourceFiles: Set<IResFile>, outputDir: IResDirectory, locator: IProjectFileLocator): Pair<
         MutableSet<IResFile>,
         Set<IResFile>
      > {
      label114: {
         val var10000: IMonitor = this.mainConfig.getMonitor();
         if (var10000 != null) {
            val var44: ProjectMetrics = var10000.getProjectMetrics();
            if (var44 != null) {
               var44.setTotalSourceFileNum((long)allSourceFiles.size());
            }
         }

         val var10001: Chain = Scene.v().getApplicationClasses();
         val var45: java.util.Collection = var10001 as java.util.Collection;
         val var10002: Chain = Scene.v().getLibraryClasses();
         val foundSourceCodes: java.lang.Iterable = CollectionsKt.plus(var45, var10002 as java.lang.Iterable);
         val nfd: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filter$iv) {
            if (!(writer as SootClass).isPhantom()) {
               nfd.add(writer);
            }
         }

         val var24: java.lang.Iterable = nfd as java.util.List;
         val var27: LinkedHashMap = new LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(nfd as java.util.List, 10)), 16)
         );

         for (Object element$iv$ivx : $this$associateWith$iv) {
            val var46: java.util.Map = var27;
            val var40: SootClass = `element$iv$ivx` as SootClass;
            var46.put(`element$iv$ivx`, locator.get(new ClassResInfo(var40), NullWrapperFileGenerator.INSTANCE));
         }

         val var25: java.util.Set = CollectionsKt.filterNotNullTo(var27.values(), new LinkedHashSet()) as java.util.Set;
         val var26: java.util.Set = SetsKt.minus(allSourceFiles, var25);
         val var28: IResFile = outputDir.resolve("source_files_which_class_not_found.txt").toFile();
         if (!var26.isEmpty()) {
            logger.warn(ReportConverter::reportSourceFileWhichClassNotFound$lambda$4);
            var28.mkdirs();
            val var29: Path = var28.getPath();
            val var35: Array<OpenOption> = new OpenOption[0];
            val var32: Charset = Charsets.UTF_8;
            val var30: Closeable = new OutputStreamWriter(Files.newOutputStream(var29, Arrays.copyOf(var35, var35.length)), var32);
            var var33: java.lang.Throwable = null;

            try {
               try {
                  val var36: OutputStreamWriter = var30 as OutputStreamWriter;

                  for (IResFile file : CollectionsKt.sortedWith(
                     classNotFoundSourceFile, new ReportConverter$reportSourceFileWhichClassNotFound$lambda$6$$inlined$sortedBy$1()
                  )) {
                     var36.write("$var42\n");
                  }

                  var36.flush();
               } catch (var19: java.lang.Throwable) {
                  var33 = var19;
                  throw var19;
               }
            } catch (var20: java.lang.Throwable) {
               CloseableKt.closeFinally(var30, var33);
            }

            CloseableKt.closeFinally(var30, null);
         } else {
            Files.deleteIfExists(var28.getPath());
         }

         return TuplesKt.to(var25, var26);
      }
   }

   public suspend fun flush(
      mainConfig: MainConfig,
      locator: IProjectFileLocator,
      coverage: JacocoCompoundCoverage,
      consumers: List<IReportConsumer>,
      reports: Collection<Report>,
      outputDir: IResDirectory
   ) {
      val var10000: Any = CoroutineScopeKt.coroutineScope(
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, locator, outputDir, consumers, reports, coverage, mainConfig, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
         ) as Function2,
         `$completion`
      );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   @JvmStatic
   fun `findAllSourceFiles$lambda$1`(`this$0`: ReportConverter, it: IResFile): Boolean {
      return `this$0`.mainConfig.getAutoAppSrcInZipScheme() || it.isFileScheme();
   }

   @JvmStatic
   fun `reportSourceFileWhichClassNotFound$lambda$4`(`$classNotFoundSourceFile`: java.util.Set, `$nfd`: IResFile): Any {
      return Theme.Companion
         .getDefault()
         .getWarning()
         .invoke(
            "Incomplete analysis! The num of ${`$classNotFoundSourceFile`.size()} source files not found any class!!! check: ${`$nfd`.getAbsolute()
               .getNormalize()}"
         );
   }

   @JvmStatic
   fun `logger$lambda$7`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger
   }
}
