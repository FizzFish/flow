package cn.sast.framework.report.coverage

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.AnalysisKey
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.io.Reader
import java.lang.reflect.Field
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.time.Instant
import java.util.Arrays
import java.util.BitSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.UUID
import java.util.Map.Entry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.function.IntConsumer
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorsKt
import kotlinx.coroutines.future.FutureKt
import mu.KLogger
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.analysis.ICoverageVisitor
import org.jacoco.core.analysis.ISourceFileCoverage
import org.jacoco.core.data.ExecutionData
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.data.IExecutionDataVisitor
import org.jacoco.core.data.SessionInfo
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.internal.analysis.ClassAnalyzer
import org.jacoco.core.internal.analysis.ClassCoverageImpl
import org.jacoco.core.internal.analysis.Instruction
import org.jacoco.core.internal.analysis.StringPool
import org.jacoco.core.internal.flow.ClassProbesAdapter
import org.jacoco.core.internal.flow.ClassProbesVisitor
import org.jacoco.core.internal.flow.MethodProbesVisitor
import org.jacoco.core.internal.instr.InstrSupport
import org.jacoco.report.DirectorySourceFileLocator
import org.jacoco.report.FileMultiReportOutput
import org.jacoco.report.IMultiReportOutput
import org.jacoco.report.IReportVisitor
import org.jacoco.report.ISourceFileLocator
import org.jacoco.report.InputStreamSourceFileLocator
import org.jacoco.report.MultiSourceFileLocator
import org.jacoco.report.html.HTMLFormatter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.tree.MethodNode
import soot.jimple.infoflow.collect.ConcurrentHashSet

@SourceDebugExtension(["SMAP\nCoverage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n1863#2,2:491\n1863#2,2:493\n*S KotlinDebug\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage\n*L\n363#1:491,2\n420#1:493,2\n*E\n"])
public open class Coverage {
   private final var startTimestamp: Instant?
   public final val stringPool: StringPool = new StringPool()
   public final val classCoverageMap: MutableMap<String, cn.sast.framework.report.coverage.Coverage.ClassCoverage> =
      (new ConcurrentHashMap(1000)) as java.util.Map
      private final val coverQueue: ConcurrentHashSet<cn.sast.framework.report.coverage.Coverage.JacocoCover> = new ConcurrentHashSet()
   private final val cache: AsyncLoadingCache<ClassSourceInfo, cn.sast.framework.report.coverage.Coverage.ClassCoverage?>

   public final var coverageBuilderPair: Pair<ExecutionDataStore, CoverageBuilder>?
      internal set

   public suspend fun cover(clazz: ClassSourceInfo, line: Int) {
      var `$continuation`: Continuation;
      label24: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label24;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            int I$0;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.cover(null, 0, this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            `$continuation`.I$0 = line;
            `$continuation`.label = 1;
            var10000 = (Coverage.ClassCoverage)this.analyzeClass(clazz, `$continuation`);
            if (var10000 === var7) {
               return var7;
            }
            break;
         case 1:
            line = `$continuation`.I$0;
            ResultKt.throwOnFailure(`$result`);
            var10000 = (Coverage.ClassCoverage)`$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      var10000 = var10000;
      if (var10000 == null) {
         return Unit.INSTANCE;
      } else {
         var10000.cover(line);
         return Unit.INSTANCE;
      }
   }

   public suspend fun cover(className: String, line: Int) {
      var `$continuation`: Continuation;
      label31: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label31;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            int I$0;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.cover(null, 0, this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            var10000 = AnalysisCache.G.INSTANCE;
            val var10001: AnalysisKey = new ClassSourceOfSCKey(className);
            val var10002: CoroutineContext = Dispatchers.getDefault() as CoroutineContext;
            `$continuation`.L$0 = this;
            `$continuation`.I$0 = line;
            `$continuation`.label = 1;
            var10000 = (AnalysisCache.G)var10000.getAsync(var10001, var10002, `$continuation`);
            if (var10000 === var7) {
               return var7;
            }
            break;
         case 1:
            line = `$continuation`.I$0;
            this = `$continuation`.L$0 as Coverage;
            ResultKt.throwOnFailure(`$result`);
            var10000 = (AnalysisCache.G)`$result`;
            break;
         case 2:
            ResultKt.throwOnFailure(`$result`);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      val var9: ClassSourceInfo = var10000 as ClassSourceInfo;
      if (var10000 as ClassSourceInfo == null) {
         return Unit.INSTANCE;
      } else {
         `$continuation`.L$0 = null;
         `$continuation`.label = 2;
         return if (this.cover(var9, line, `$continuation`) === var7) var7 else Unit.INSTANCE;
      }
   }

   public suspend fun cover(clazz: ByteArray, line: Int) {
      val var10000: Any = this.cover(ClassSourceInfo.Companion.invoke(clazz), line, `$completion`);
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   public fun coverByQueue(className: String, line: Int) {
      (this.coverQueue as java.util.Collection).add(new Coverage.JacocoCover(className, line));
   }

   private fun createAnalyzingVisitor(classCoverage: cn.sast.framework.report.coverage.Coverage.ClassCoverage, classId: Long, className: String): ClassVisitor {
      val coverage: ClassCoverageImpl = new ClassCoverageImpl(className, classId, false);
      val builder: <unrepresentable> = new InstructionsBuilder(classCoverage) {
         {
            super(null);
            this.$classCoverage = `$classCoverage`;
         }

         @Override
         protected void addProbe(int probeId, int branch) {
            super.addProbe(probeId, branch);
            val var10000: Coverage.ClassCoverage = this.$classCoverage;
            val var10001: Instruction = this.currentInsn;
            var10000.addProbe(var10001, probeId);
         }
      };
      return (
         new ClassProbesAdapter(
            (
               new ClassAnalyzer(coverage, builder, classCoverage, this.stringPool) {
                  {
                     super(`$coverage`, null, `$super_call_param$1`);
                     this.$builder = `$builder`;
                     this.$classCoverage = `$classCoverage`;
                  }

                  public MethodProbesVisitor visitMethod(
                     int access, java.lang.String name, java.lang.String desc, java.lang.String signature, java.lang.String[] exceptions
                  ) {
                     return new MethodAnalyzer(this.$builder) {
                        {
                           super(`$builder`);
                        }

                        @Override
                        public void accept(MethodNode methodNode, MethodVisitor methodVisitor) {
                           super.accept(methodNode, methodVisitor);
                        }
                     };
                  }

                  public void visitTotalProbeCount(int count) {
                     super.visitTotalProbeCount(count);
                     this.$classCoverage.setCount(count);
                  }
               }
            ) as ClassProbesVisitor,
            false
         )
      ) as ClassVisitor;
   }

   public suspend fun analyzeClass(source: ClassSourceInfo): cn.sast.framework.report.coverage.Coverage.ClassCoverage? {
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
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.analyzeClass(null, this as Continuation<? super Coverage.ClassCoverage>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var8: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            if (this.startTimestamp == null) {
               this.startTimestamp = Instant.now();
            }

            val exists: Coverage.ClassCoverage = this.classCoverageMap.get(source.getClassName());
            if (exists != null) {
               return exists;
            }

            val classCoverageCompletableFuture: CompletableFuture = this.cache.get(source);
            var10000 = classCoverageCompletableFuture;
            `$continuation`.L$0 = this;
            `$continuation`.L$1 = source;
            `$continuation`.label = 1;
            var10000 = FutureKt.await((CompletionStage)var10000, `$continuation`);
            if (var10000 === var8) {
               return var8;
            }
            break;
         case 1:
            source = `$continuation`.L$1 as ClassSourceInfo;
            this = `$continuation`.L$0 as Coverage;
            ResultKt.throwOnFailure(`$result`);
            var10000 = `$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      val classCoverage: Coverage.ClassCoverage = var10000 as Coverage.ClassCoverage;
      if (var10000 as Coverage.ClassCoverage == null) {
         return null;
      } else {
         this.classCoverageMap.put(source.getClassName(), classCoverage);
         return classCoverage;
      }
   }

   public fun getSessionInfo(): SessionInfo {
      var var10000: Instant = this.startTimestamp;
      if (this.startTimestamp == null) {
         var10000 = Instant.now();
      }

      return new SessionInfo(UUID.randomUUID().toString(), var10000.getEpochSecond(), Instant.now().getEpochSecond());
   }

   public fun getSessionInfoStore(): SessionInfoStore {
      val sessionInfo: SessionInfo = this.getSessionInfo();
      val sessionInfoStore: SessionInfoStore = new SessionInfoStore();
      sessionInfoStore.visitSessionInfo(sessionInfo);
      return sessionInfoStore;
   }

   public suspend fun flushExecutionDataFile(sessionInfoStore: SessionInfoStore, executionDataStore: ExecutionDataStore, dumpFile: IResFile) {
      dumpFile.mkdirs();
      val var10000: Any = BuildersKt.withContext(
         Dispatchers.getIO() as CoroutineContext,
         (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(dumpFile, sessionInfoStore, executionDataStore, null) {
            int label;

            {
               super(2, `$completionx`);
               this.$dumpFile = `$dumpFile`;
               this.$sessionInfoStore = `$sessionInfoStore`;
               this.$executionDataStore = `$executionDataStore`;
            }

            public final Object invokeSuspend(Object $result) {
               IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     val var10000: Path = this.$dumpFile.getPath();
                     val var10001: Array<OpenOption> = new OpenOption[0];
                     val var21: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var10001, var10001.length));
                     val var2: Closeable = var21;
                     val var3: SessionInfoStore = this.$sessionInfoStore;
                     val var4: ExecutionDataStore = this.$executionDataStore;
                     var var5: java.lang.Throwable = null;

                     try {
                        try {
                           val outWriter: ExecutionDataWriter = new ExecutionDataWriter(var2 as OutputStream);

                           val `$this$forEach$iv`: java.lang.Iterable;
                           for (Object element$iv : $this$forEach$iv) {
                              outWriter.visitSessionInfo(`element$iv` as SessionInfo);
                           }

                           var4.accept(outWriter as IExecutionDataVisitor);
                        } catch (var16: java.lang.Throwable) {
                           var5 = var16;
                           throw var16;
                        }
                     } catch (var17: java.lang.Throwable) {
                        CloseableKt.closeFinally(var2, var5);
                     }

                     CloseableKt.closeFinally(var2, null);
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }
            }

            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
               return (new <anonymous constructor>(this.$dumpFile, this.$sessionInfoStore, this.$executionDataStore, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2,
         `$completion`
      );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   public suspend fun processCoverQueueData(): Boolean {
      var `$continuation`: Continuation;
      label37: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label37;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.processCoverQueueData(this as Continuation<? super java.lang.Boolean>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var8: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var2: java.util.Iterator;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            if (this.coverQueue.isEmpty()) {
               return Boxing.boxBoolean(false);
            }

            val var10000: java.util.Iterator = this.coverQueue.iterator();
            var2 = var10000;
            break;
         case 1:
            var2 = `$continuation`.L$1 as java.util.Iterator;
            this = `$continuation`.L$0 as Coverage;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (var2.hasNext()) {
         val var3: Coverage.JacocoCover = var2.next() as Coverage.JacocoCover;
         val name: java.lang.String = var3.component1();
         val ln: Int = var3.component2();
         `$continuation`.L$0 = this;
         `$continuation`.L$1 = var2;
         `$continuation`.label = 1;
         if (this.cover(name, ln, `$continuation`) === var8) {
            return var8;
         }
      }

      this.coverQueue.clear();
      return Boxing.boxBoolean(false);
   }

   public suspend fun computeCoverageBuilder(): Pair<ExecutionDataStore, CoverageBuilder> {
      var `$continuation`: Continuation;
      label42: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label42;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.computeCoverageBuilder(this as Continuation<? super Pair<ExecutionDataStore, ? extends CoverageBuilder>>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var10: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var coverageBuilderPair: Pair;
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            coverageBuilderPair = this.coverageBuilderPair;
            `$continuation`.L$0 = this;
            `$continuation`.L$1 = coverageBuilderPair;
            `$continuation`.label = 1;
            var10000 = this.processCoverQueueData(`$continuation`);
            if (var10000 === var10) {
               return var10;
            }
            break;
         case 1:
            coverageBuilderPair = `$continuation`.L$1 as Pair;
            this = `$continuation`.L$0 as Coverage;
            ResultKt.throwOnFailure(`$result`);
            var10000 = `$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      if (!var10000 as java.lang.Boolean && coverageBuilderPair != null) {
         return coverageBuilderPair;
      } else {
         val executionDataStore: ExecutionDataStore = new ExecutionDataStore();
         val coverageBuilder: java.util.Iterator = this.classCoverageMap.entrySet().iterator();

         while (coverageBuilder.hasNext()) {
            val classCoverage: Coverage.ClassCoverage = (coverageBuilder.next() as Entry).getValue() as Coverage.ClassCoverage;
            val probes: BooleanArray = new boolean[classCoverage.getCount()];
            classCoverage.getProbes().stream().forEach(new IntConsumer(probes) {
               {
                  this.$probes = `$probes`;
               }

               @Override
               public final void accept(int classLocalEdgeId) {
                  this.$probes[classLocalEdgeId] = true;
               }
            });

            try {
               executionDataStore.put(
                  new ExecutionData(classCoverage.getClassId(), StringsKt.replace$default(classCoverage.getClassName(), ".", "/", false, 4, null), probes)
               );
            } catch (var11: Exception) {
               logger.error(var11, Coverage::computeCoverageBuilder$lambda$1);
            }
         }

         coverageBuilderPair = TuplesKt.to(executionDataStore, this.makeCoverageBuilder(executionDataStore));
         this.coverageBuilderPair = coverageBuilderPair;
         return coverageBuilderPair;
      }
   }

   @Throws(java/io/IOException::class)
   public suspend fun createReport(
      coverageBuilder: CoverageBuilder,
      sessionInfoStore: SessionInfoStore,
      executionDataStore: ExecutionDataStore,
      mLocator: MultiSourceFileLocator,
      reportDirectory: IResDirectory
   ) {
      val bundleCoverage: IBundleCoverage = coverageBuilder.getBundle("CoraxCoverage");
      val var10000: Any = this.createReport(
         sessionInfoStore, executionDataStore, bundleCoverage, mLocator as ISourceFileLocator, reportDirectory.getFile(), `$completion`
      );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   @Throws(java/io/IOException::class)
   public suspend fun createReport(
      coverageBuilder: CoverageBuilder,
      sessionInfoStore: SessionInfoStore,
      executionDataStore: ExecutionDataStore,
      sourceDirectory: List<ISourceFileLocator>,
      reportDirectory: IResDirectory
   ) {
      val mLocator: MultiSourceFileLocator = new MultiSourceFileLocator(4);

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         mLocator.add(`element$iv` as ISourceFileLocator);
      }

      val var10000: Any = this.createReport(coverageBuilder, sessionInfoStore, executionDataStore, mLocator, reportDirectory, `$completion`);
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   @Throws(java/io/IOException::class)
   public suspend fun createReport(
      sessionInfoStore: SessionInfoStore,
      executionDataStore: ExecutionDataStore,
      bundleCoverage: IBundleCoverage,
      locator: ISourceFileLocator,
      reportDirectory: File
   ) {
      var `$continuation`: Continuation;
      label24: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label24;
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
               return this.this$0.createReport(null, null, null, null, null, this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            if (!reportDirectory.exists()) {
               reportDirectory.mkdir();
            }

            val var10000: CoroutineContext = Dispatchers.getIO() as CoroutineContext;
            val var10001: Function2 = (
               new Function2<CoroutineScope, Continuation<? super Unit>, Object>(
                  reportDirectory, sessionInfoStore, executionDataStore, bundleCoverage, locator, null
               ) {
                  int label;

                  {
                     super(2, `$completionx`);
                     this.$reportDirectory = `$reportDirectory`;
                     this.$sessionInfoStore = `$sessionInfoStore`;
                     this.$executionDataStore = `$executionDataStore`;
                     this.$bundleCoverage = `$bundleCoverage`;
                     this.$locator = `$locator`;
                  }

                  public final Object invokeSuspend(Object $result) {
                     IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           val visitor: IReportVisitor = new HTMLFormatter()
                              .createVisitor((new FileMultiReportOutput(this.$reportDirectory)) as IMultiReportOutput);
                           visitor.visitInfo(this.$sessionInfoStore.getInfos(), this.$executionDataStore.getContents());
                           visitor.visitBundle(this.$bundleCoverage, this.$locator);
                           visitor.visitEnd();
                           return Unit.INSTANCE;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     return (
                        new <anonymous constructor>(
                           this.$reportDirectory, this.$sessionInfoStore, this.$executionDataStore, this.$bundleCoverage, this.$locator, `$completion`
                        )
                     ) as Continuation<Unit>;
                  }

                  public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }
               }
            ) as Function2;
            `$continuation`.L$0 = reportDirectory;
            `$continuation`.label = 1;
            if (BuildersKt.withContext(var10000, var10001, `$continuation`) === var9) {
               return var9;
            }
            break;
         case 1:
            reportDirectory = `$continuation`.L$0 as File;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      logger.info(Coverage::createReport$lambda$2);
      return Unit.INSTANCE;
   }

   public fun makeCoverageBuilder(executionDataStore: ExecutionDataStore): CoverageBuilder {
      val var3: CoverageBuilder = new CoverageBuilder();
      val analyzer: Analyzer = new Analyzer(executionDataStore, var3 as ICoverageVisitor);
      val var7: java.util.Iterator = this.classCoverageMap.entrySet().iterator();

      while (var7.hasNext()) {
         val classCoverage: Coverage.ClassCoverage = (var7.next() as Entry).getValue() as Coverage.ClassCoverage;
         analyzer.analyzeClass(classCoverage.getByteArray(), classCoverage.getClassName());
      }

      return var3;
   }

   public fun getMultiSourceFileLocator(locator: IProjectFileLocator, encoding: Charset): MultiSourceFileLocator {
      val jacocoLocators: java.util.List = CollectionsKt.listOf(
         new InputStreamSourceFileLocator[]{
            new DirectorySourceFileLocator(new File("."), encoding.name(), 4), new JacocoSourceLocator(locator, null, 0, 6, null)
         }
      );
      val var12: MultiSourceFileLocator = new MultiSourceFileLocator(4);

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         var12.add(`element$iv` as ISourceFileLocator);
      }

      return var12;
   }

   public open suspend fun flushCoverage(locator: IProjectFileLocator, outputDir: IResDirectory, encoding: Charset) {
      return flushCoverage$suspendImpl(this, locator, outputDir, encoding, `$completion`);
   }

   public fun calculateSourceCoverage(coverage: CoverageBuilder, mLocator: MultiSourceFileLocator): SourceCoverage {
      val sourceCoverage: java.util.Map = new LinkedHashMap();

      for (ISourceFileCoverage srcCov : coverage.getSourceFiles()) {
         val sourceKey: java.lang.String = "${srcCov.getPackageName()}/${srcCov.getName()}";

         var var11: Int;
         try {
            val reader: Reader = mLocator.getSourceFile(srcCov.getPackageName(), srcCov.getName());
            if (reader == null) {
               continue;
            }

            var11 = TextStreamsKt.readLines(reader).size();
         } catch (var10: Exception) {
            logger.error("Source file $sourceKey cannot be read!", var10);
            continue;
         }

         sourceCoverage.put(sourceKey, new SourceCoverage.JavaSourceCoverage(var11, srcCov));
      }

      return new SourceCoverage(sourceCoverage);
   }

   public suspend fun calculateSourceCoverage(locator: IProjectFileLocator, encoding: Charset): SourceCoverage {
      var `$continuation`: Continuation;
      label20: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.calculateSourceCoverage(null, null, this as Continuation<? super SourceCoverage>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var8: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            `$continuation`.L$0 = this;
            `$continuation`.L$1 = locator;
            `$continuation`.L$2 = encoding;
            `$continuation`.label = 1;
            var10000 = this.computeCoverageBuilder(`$continuation`);
            if (var10000 === var8) {
               return var8;
            }
            break;
         case 1:
            encoding = `$continuation`.L$2 as Charset;
            locator = `$continuation`.L$1 as IProjectFileLocator;
            this = `$continuation`.L$0 as Coverage;
            ResultKt.throwOnFailure(`$result`);
            var10000 = `$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return this.calculateSourceCoverage((var10000 as Pair).component2() as CoverageBuilder, this.getMultiSourceFileLocator(locator, encoding));
   }

   @JvmStatic
   fun `cache$lambda$0`(`this$0`: Coverage, source: ClassSourceInfo, executor: Executor): CompletableFuture {
      val classCoverage: Coverage.ClassCoverage = new Coverage.ClassCoverage(source.getClassName(), source.getByteArray(), source.getJacocoClassId());
      return FutureKt.future$default(
         CoroutineScopeKt.CoroutineScope(ExecutorsKt.from(executor) as CoroutineContext),
         null,
         null,
         (new Function2<CoroutineScope, Continuation<? super Coverage.ClassCoverage>, Object>(source, `this$0`, classCoverage, null) {
            int label;

            {
               super(2, `$completionx`);
               this.$source = `$source`;
               this.this$0 = `$receiver`;
               this.$classCoverage = `$classCoverage`;
            }

            public final Object invokeSuspend(Object $result) {
               IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);

                     var reader: Coverage.ClassCoverage;
                     try {
                        val var5: ClassReader = InstrSupport.classReaderFor(this.$source.getByteArray());
                        val var10000: Coverage = this.this$0;
                        val var10001: Coverage.ClassCoverage = this.$classCoverage;
                        val var10002: Long = this.$source.getJacocoClassId();
                        val var10003: java.lang.String = var5.getClassName();
                        var5.accept(Coverage.access$createAnalyzingVisitor(var10000, var10001, var10002, var10003), 0);
                        reader = this.$classCoverage;
                     } catch (var4: Exception) {
                        reader = null;
                     }

                     return reader;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }
            }

            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
               return (new <anonymous constructor>(this.$source, this.this$0, this.$classCoverage, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(CoroutineScope p1, Continuation<? super Coverage.ClassCoverage> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2,
         3,
         null
      );
   }

   @JvmStatic
   fun `computeCoverageBuilder$lambda$1`(`$classCoverage`: Coverage.ClassCoverage): Any {
      return "An error occurred: class=${`$classCoverage`.getClassName()}";
   }

   @JvmStatic
   fun `createReport$lambda$2`(`$reportDirectory`: File): Any {
      val var10000: File = FilesKt.resolve(`$reportDirectory`, "index.html").getAbsoluteFile();
      return "Jacoco coverage html reports: ${FilesKt.normalize(var10000)}";
   }

   @JvmStatic
   fun `logger$lambda$5`(): Unit {
      return Unit.INSTANCE;
   }

   @SourceDebugExtension(["SMAP\nCoverage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage$ClassCoverage\n+ 2 ReportConverter.kt\ncn/sast/framework/report/ReportConverterKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,490:1\n38#2,3:491\n381#3,7:494\n*S KotlinDebug\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/Coverage$ClassCoverage\n*L\n110#1:491,3\n118#1:494,7\n*E\n"])
   public class ClassCoverage(className: String, byteArray: ByteArray, classId: Long) {
      public final val className: String
      public final val byteArray: ByteArray
      public final val classId: Long
      private final val lineMap: MutableMap<Int, MutableSet<Int>>

      public final var count: Int
         internal set

      public final var probes: BitSet
         public final get() {
            if (this.probes.size() == 0 && this.count >= 0) {
               this.probes = new BitSet(this.count);
            }

            return this.probes;
         }

         private set

      private final val predecessor: Instruction?
         private final get() {
            val `it$iv`: Field = `$this$predecessor`.getClass().getDeclaredField("predecessor");
            `it$iv`.setAccessible(true);
            var var10000: Any = `it$iv`.get(`$this$predecessor`);
            if (var10000 !is Instruction) {
               var10000 = null;
            }

            return var10000 as Instruction;
         }


      init {
         this.className = className;
         this.byteArray = byteArray;
         this.classId = classId;
         this.lineMap = new LinkedHashMap<>(100);
         this.probes = new BitSet(0);
      }

      public fun addProbe(instruction: Instruction, probeId: Int) {
         var insn: Instruction = instruction;

         while (insn != null) {
            val line: Int = insn.getLine();
            if (line >= 0) {
               val `$this$getOrPut$iv`: java.util.Map = this.lineMap;
               val `key$iv`: Any = line;
               val `value$iv`: Any = `$this$getOrPut$iv`.get(`key$iv`);
               val var10000: Any;
               if (`value$iv` == null) {
                  val var10: Any = new LinkedHashSet();
                  `$this$getOrPut$iv`.put(`key$iv`, var10);
                  var10000 = var10;
               } else {
                  var10000 = `value$iv`;
               }

               (var10000 as java.util.Set).add(probeId);
            }

            insn = this.getPredecessor(insn);
         }
      }

      public fun cover(line: Int) {
         val var10000: java.util.Set = this.lineMap.get(line);
         if (var10000 != null) {
            val var3: java.util.Iterator = var10000.iterator();

            while (var3.hasNext()) {
               this.getProbes().set((var3.next() as java.lang.Number).intValue());
            }
         }
      }
   }

   public companion object {
      private final val logger: KLogger
   }

   internal data class JacocoCover(className: String, line: Int) {
      public final val className: String
      public final val line: Int

      init {
         this.className = className;
         this.line = line;
      }

      public operator fun component1(): String {
         return this.className;
      }

      public operator fun component2(): Int {
         return this.line;
      }

      public fun copy(className: String = this.className, line: Int = this.line): cn.sast.framework.report.coverage.Coverage.JacocoCover {
         return new Coverage.JacocoCover(className, line);
      }

      public override fun toString(): String {
         return "JacocoCover(className=${this.className}, line=${this.line})";
      }

      public override fun hashCode(): Int {
         return this.className.hashCode() * 31 + Integer.hashCode(this.line);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is Coverage.JacocoCover) {
            return false;
         } else {
            val var2: Coverage.JacocoCover = other as Coverage.JacocoCover;
            if (!(this.className == (other as Coverage.JacocoCover).className)) {
               return false;
            } else {
               return this.line == var2.line;
            }
         }
      }
   }
}
