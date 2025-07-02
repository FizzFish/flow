package cn.sast.framework.report.metadata

import cn.sast.api.report.ClassResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.OS
import cn.sast.common.Resource
import cn.sast.common.ResourceKt
import cn.sast.coroutines.MultiWorkerQueue
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.jacoco.core.analysis.ICounter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.Scene
import soot.SootClass
import soot.jimple.infoflow.collect.ConcurrentHashSet

@SourceDebugExtension(["SMAP\nMetadataGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MetadataGenerator.kt\ncn/sast/framework/report/metadata/MetadataGenerator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,104:1\n1557#2:105\n1628#2,3:106\n1557#2:109\n1628#2,3:110\n1#3:113\n*S KotlinDebug\n*F\n+ 1 MetadataGenerator.kt\ncn/sast/framework/report/metadata/MetadataGenerator\n*L\n80#1:105\n80#1:106,3\n82#1:109\n82#1:110,3\n*E\n"])
public class MetadataGenerator(projectRoot: String?,
   multipleProjectRoot: List<String>,
   outputPath: IResDirectory,
   sourcePaths: List<String>,
   coveredCounter: ICounter,
   successfulFiles: Set<IResFile>,
   failedFiles: Set<IResFile>
) {
   private final val projectRoot: String?
   private final val multipleProjectRoot: List<String>
   private final val outputPath: IResDirectory
   private final val sourcePaths: List<String>
   private final val coveredCounter: ICounter
   private final val successfulFiles: Set<IResFile>
   private final val failedFiles: Set<IResFile>
   private final var resultSourceFiles: MutableMap<String, String>

   init {
      this.projectRoot = projectRoot;
      this.multipleProjectRoot = multipleProjectRoot;
      this.outputPath = outputPath;
      this.sourcePaths = sourcePaths;
      this.coveredCounter = coveredCounter;
      this.successfulFiles = successfulFiles;
      this.failedFiles = failedFiles;
      this.resultSourceFiles = new LinkedHashMap<>();
   }

   public fun updateResultSourceMapping(result: String, source: String) {
      this.resultSourceFiles.put(this.outputPath.resolve(result).getAbsolute().getNormalize().getPath().toString(), source);
   }

   public suspend fun getMetadata(locator: IProjectFileLocator): AnalysisMetadata {
      var `$continuation`: Continuation;
      label32: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label32;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            Object L$3;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.getMetadata(null, this as Continuation<? super AnalysisMetadata>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var11: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var fileCount: AtomicInteger;
      var lineCount: AtomicInteger;
      var task: MultiWorkerQueue;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            fileCount = new AtomicInteger(0);
            lineCount = new AtomicInteger(0);
            val visited: ConcurrentHashSet = new ConcurrentHashSet();
            task = new MultiWorkerQueue(
               "Metadata",
               OS.INSTANCE.getMaxThreadNum(),
               (
                  new Function2<SootClass, Continuation<? super Unit>, Object>(locator, visited, fileCount, lineCount, null) {
                     int label;

                     {
                        super(2, `$completionx`);
                        this.$locator = `$locator`;
                        this.$visited = `$visited`;
                        this.$fileCount = `$fileCount`;
                        this.$lineCount = `$lineCount`;
                     }

                     public final Object invokeSuspend(Object $result) {
                        IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              val var10000: IResFile = this.$locator.get(new ClassResInfo(this.L$0 as SootClass), NullWrapperFileGenerator.INSTANCE);
                              if (var10000 != null) {
                                 val var5: AtomicInteger = this.$fileCount;
                                 val var6: AtomicInteger = this.$lineCount;
                                 if (this.$visited.add(var10000.getAbsolutePath())) {
                                    var5.incrementAndGet();
                                    var6.addAndGet(
                                       ResourceKt.<java.lang.Number>lineSequence(var10000, <unrepresentable>::invokeSuspend$lambda$1$lambda$0).intValue()
                                    );
                                 }
                              }

                              return Unit.INSTANCE;
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }
                     }

                     public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                        val var3: Function2 = new <anonymous constructor>(this.$locator, this.$visited, this.$fileCount, this.$lineCount, `$completion`);
                        var3.L$0 = value;
                        return var3 as Continuation<Unit>;
                     }

                     public final Object invoke(SootClass p1, Continuation<? super Unit> p2) {
                        return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                     }

                     private static final int invokeSuspend$lambda$1$lambda$0(Sequence it) {
                        return SequencesKt.count(it);
                     }
                  }
               ) as Function2
            );
            val var10000: java.util.Iterator = Scene.v().getApplicationClasses().iterator();
            val var7: java.util.Iterator = var10000;

            while (var7.hasNext()) {
               val appClass: SootClass = var7.next() as SootClass;
               if (!Scene.v().isExcluded(appClass)) {
                  task.dispatch(appClass);
               }
            }

            `$continuation`.L$0 = this;
            `$continuation`.L$1 = fileCount;
            `$continuation`.L$2 = lineCount;
            `$continuation`.L$3 = task;
            `$continuation`.label = 1;
            if (task.join(`$continuation`) === var11) {
               return var11;
            }
            break;
         case 1:
            task = `$continuation`.L$3 as MultiWorkerQueue;
            lineCount = `$continuation`.L$2 as AtomicInteger;
            fileCount = `$continuation`.L$1 as AtomicInteger;
            this = `$continuation`.L$0 as MetadataGenerator;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      task.close();
      logger.info(MetadataGenerator::getMetadata$lambda$0);
      return this.generateMetaData(fileCount.get(), lineCount.get());
   }

   public fun generateFailedMetaData(): AnalysisMetadata {
      return this.generateMetaData(0, 0);
   }

   private fun generateMetaData(fileCount: Int, lineCount: Int): AnalysisMetadata {
      var var10002: Counter = new Counter(this.coveredCounter.getMissedCount(), this.coveredCounter.getCoveredCount());
      var var10004: java.util.List = CollectionsKt.sorted(this.sourcePaths);
      var var10005: java.lang.String = System.getProperty("os.name");
      var10005 = var10005.toLowerCase(Locale.ROOT);
      val var10007: Int = this.failedFiles.size();
      var `$this$map$iv`: java.lang.Iterable = this.failedFiles;
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.failedFiles, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(Resource.INSTANCE.getOriginFileFromExpandPath(`item$iv$iv` as IResFile).getPathString());
      }

      val var10008: java.util.List = CollectionsKt.sorted(`destination$iv$iv` as java.util.List);
      val var10009: Int = this.successfulFiles.size();
      `$this$map$iv` = this.successfulFiles;
      `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.successfulFiles, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(Resource.INSTANCE.getOriginFileFromExpandPath(var53 as IResFile).getPathString());
      }

      val var23: java.util.List = `destination$iv$iv` as java.util.List;
      var var10000: Int = fileCount;
      var var10001: Int = lineCount;
      var10002 = var10002;
      var var10003: Byte = 1;
      var10004 = var10004;
      var10005 = var10005;
      val var10006: java.util.Map = MapsKt.mapOf(
         TuplesKt.to("corax", new Analyzer(new AnalyzerStatistics(var10007, var10008, var10009, CollectionsKt.sorted(var23), ""), MapsKt.emptyMap()))
      );
      var var63: java.util.List = OS.getCommandLine$default(OS.INSTANCE, null, false, 3, null);
      if (var63 == null) {
         val var64: java.lang.String = System.getProperty("java.home");
         if (var64 != null) {
            val var19: java.util.List = CollectionsKt.listOf(var64);
            var10000 = fileCount;
            var10001 = lineCount;
            var10002 = var10002;
            var10003 = 1;
            var10004 = var10004;
            var10005 = var10005;
            var63 = var19;
         } else {
            var63 = null;
         }

         if (var63 == null) {
            var63 = CollectionsKt.emptyList();
         }
      }

      val var65: java.lang.String = this.outputPath.getAbsolute().getNormalize().getPath().toString();
      var var66: java.lang.String = this.projectRoot;
      if (this.projectRoot == null) {
         var66 = "";
      }

      val var10011: java.util.List = this.multipleProjectRoot;
      val var10012: java.util.Map = MapsKt.toSortedMap(this.resultSourceFiles);
      val var10013: java.lang.String = System.getProperty("user.dir");
      return new AnalysisMetadata(
         var10000,
         var10001,
         var10002,
         var10003,
         var10004,
         var10005,
         CollectionsKt.listOf(new Tool(var10006, var63, "corax", var65, var66, var10011, var10012, var10013))
      );
   }

   @JvmStatic
   fun `getMetadata$lambda$0`(): Any {
      return "Metadata: Java executable file path: ${OS.INSTANCE.getJavaExecutableFilePath()}";
   }

   @JvmStatic
   fun `logger$lambda$4`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
