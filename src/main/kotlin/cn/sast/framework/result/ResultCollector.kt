package cn.sast.framework.result

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.dataflow.infoflow.provider.BugTypeProvider
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.framework.engine.PreAnalysisReportEnv
import cn.sast.framework.graph.CGUtils
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.IReportConsumer
import cn.sast.framework.report.PlistDiagnostics
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.report.ReportConverter
import cn.sast.framework.report.SarifDiagnostics
import cn.sast.framework.report.SarifDiagnosticsCopySrc
import cn.sast.framework.report.SarifDiagnosticsPack
import cn.sast.framework.report.SqliteDiagnostics
import cn.sast.framework.report.coverage.JacocoCompoundCoverage
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.CheckType
import java.io.Closeable
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.IntUnaryOperator
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.Maybe
import soot.SootMethod
import soot.jimple.infoflow.collect.ConcurrentHashSet
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.results.DataFlowResult
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.Ref.ObjectRef

@SourceDebugExtension(["SMAP\nResultCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResultCollector.kt\ncn/sast/framework/result/ResultCollector\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n*L\n1#1,284:1\n808#2,11:285\n808#2,11:296\n808#2,11:307\n808#2,11:318\n808#2,11:329\n808#2,11:340\n808#2,11:351\n1863#2,2:362\n1863#2,2:364\n1863#2,2:370\n1557#2:372\n1628#2,3:373\n1863#2,2:376\n1863#2,2:378\n1755#2,3:380\n1863#2,2:383\n1863#2,2:385\n1863#2,2:387\n1557#2:389\n1628#2,3:390\n1#3:366\n1#3:369\n72#4,2:367\n*S KotlinDebug\n*F\n+ 1 ResultCollector.kt\ncn/sast/framework/result/ResultCollector\n*L\n90#1:285,11\n91#1:296,11\n92#1:307,11\n93#1:318,11\n94#1:329,11\n95#1:340,11\n97#1:351,11\n118#1:362,2\n123#1:364,2\n150#1:370,2\n158#1:372\n158#1:373,3\n166#1:376,2\n172#1:378,2\n180#1:380,3\n187#1:383,2\n196#1:385,2\n205#1:387,2\n231#1:389\n231#1:390,3\n132#1:369\n132#1:367,2\n*E\n"])
public class ResultCollector(mainConfig: MainConfig,
      info: SootInfoCache?,
      outputDir: IResDirectory,
      locator: ProjectFileLocator,
      collectors: List<IResultCollector> = CollectionsKt.emptyList(),
      outputTypes: List<OutputType> = CollectionsKt.emptyList(),
      serializeTaintPath: Boolean = true,
      resultConverter: ResultConverter = new ResultConverter(info),
      coverage: JacocoCompoundCoverage = new JacocoCompoundCoverage(locator, null, null, false, 14, null),
      flushCoverage: Boolean = false,
      monitor: MetricsMonitor
   ) :
   IFlowDroidResultCollector,
   IIPAnalysisResultCollector,
   IUTBotResultCollector,
   IMissingSummaryReporter,
   IPreAnalysisResultCollector,
   IBuiltInAnalysisCollector,
   IReportsVisitor {
   private final val mainConfig: MainConfig
   private final val info: SootInfoCache?
   private final val outputDir: IResDirectory
   private final val locator: ProjectFileLocator
   private final val collectors: List<IResultCollector>
   private final val outputTypes: List<OutputType>
   private final val serializeTaintPath: Boolean
   private final val resultConverter: ResultConverter
   public final val coverage: JacocoCompoundCoverage
   public final val flushCoverage: Boolean
   public final val monitor: MetricsMonitor
   private final val collectorsFlowDroid: List<IFlowDroidResultCollector>
   private final val collectorsUTBot: List<IUTBotResultCollector>
   private final val collectorsDataFlow: List<IIPAnalysisResultCollector>
   private final val collectorsIFIChecker: List<IPreAnalysisResultCollector>
   private final val collectorsFlowSensitive: List<IBuiltInAnalysisCollector>
   private final val reportsVisitor: List<IReportsVisitor>
   private final val missingSummaryReporter: List<IMissingSummaryReporter>
   private final val reports: ConcurrentHashSet<Report>
   private final val purificationReports: ConcurrentHashMap<PurificationReportKey, AtomicInteger>

   public final lateinit var preAnalysis: PreAnalysisCoroutineScope
      internal set

   private final val bugTypeProvider: BugTypeProvider
      private final get() {
         return this.bugTypeProvider$delegate.getValue() as BugTypeProvider;
      }


   private final var flushing: Boolean

   init {
      this.mainConfig = mainConfig;
      this.info = info;
      this.outputDir = outputDir;
      this.locator = locator;
      this.collectors = collectors;
      this.outputTypes = outputTypes;
      this.serializeTaintPath = serializeTaintPath;
      this.resultConverter = resultConverter;
      this.coverage = coverage;
      this.flushCoverage = flushCoverage;
      this.monitor = monitor;
      var `$this$filterIsInstance$iv`: java.lang.Iterable = this.collectors;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$filterIsInstance$iv) {
         if (`element$iv$iv` is IFlowDroidResultCollector) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      this.collectorsFlowDroid = `destination$iv$iv` as MutableList<IFlowDroidResultCollector>;
      `$this$filterIsInstance$iv` = this.collectors;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivx : $this$filterIsInstance$iv) {
         if (`element$iv$ivx` is IUTBotResultCollector) {
            `destination$iv$iv`.add(`element$iv$ivx`);
         }
      }

      this.collectorsUTBot = `destination$iv$iv` as MutableList<IUTBotResultCollector>;
      `$this$filterIsInstance$iv` = this.collectors;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivxx : $this$filterIsInstance$iv) {
         if (`element$iv$ivxx` is IIPAnalysisResultCollector) {
            `destination$iv$iv`.add(`element$iv$ivxx`);
         }
      }

      this.collectorsDataFlow = `destination$iv$iv` as MutableList<IIPAnalysisResultCollector>;
      `$this$filterIsInstance$iv` = this.collectors;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivxxx : $this$filterIsInstance$iv) {
         if (`element$iv$ivxxx` is IPreAnalysisResultCollector) {
            `destination$iv$iv`.add(`element$iv$ivxxx`);
         }
      }

      this.collectorsIFIChecker = `destination$iv$iv` as MutableList<IPreAnalysisResultCollector>;
      `$this$filterIsInstance$iv` = this.collectors;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivxxxx : $this$filterIsInstance$iv) {
         if (`element$iv$ivxxxx` is IBuiltInAnalysisCollector) {
            `destination$iv$iv`.add(`element$iv$ivxxxx`);
         }
      }

      this.collectorsFlowSensitive = `destination$iv$iv` as MutableList<IBuiltInAnalysisCollector>;
      `$this$filterIsInstance$iv` = this.collectors;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivxxxxx : $this$filterIsInstance$iv) {
         if (`element$iv$ivxxxxx` is IReportsVisitor) {
            `destination$iv$iv`.add(`element$iv$ivxxxxx`);
         }
      }

      this.reportsVisitor = `destination$iv$iv` as MutableList<IReportsVisitor>;
      `$this$filterIsInstance$iv` = this.collectors;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivxxxxxx : $this$filterIsInstance$iv) {
         if (`element$iv$ivxxxxxx` is IMissingSummaryReporter) {
            `destination$iv$iv`.add(`element$iv$ivxxxxxx`);
         }
      }

      this.missingSummaryReporter = `destination$iv$iv` as MutableList<IMissingSummaryReporter>;
      this.reports = new ConcurrentHashSet();
      this.purificationReports = new ConcurrentHashMap<>();
      this.bugTypeProvider$delegate = LazyKt.lazy(ResultCollector::bugTypeProvider_delegate$lambda$2);
   }

   public fun getReports(): Set<Report> {
      val `$this$getReports_u24lambda_u240`: ResultCollector = this;
      this.flushing = true;
      return `$this$getReports_u24lambda_u240`.reports as MutableSet<Report>;
   }

   public fun getCollectors(): List<IResultCollector> {
      return this.collectors;
   }

   public override fun report(checkType: CheckType, info: PreAnalysisReportEnv) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IPreAnalysisResultCollector).report(checkType, info);
      }

      this.addReport(this.resultConverter.getReport(checkType, info));
   }

   public override fun report(report: Report) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IBuiltInAnalysisCollector).report(report);
      }

      this.addReport(report);
   }

   private fun addReport(reports: Collection<Report>) {
      if (this.flushing) {
         throw new IllegalArgumentException("internal error: emit bug reports when flush report".toString());
      } else {
         for (Report report : reports) {
            if (this.mainConfig.isEnable(report.getCheckType())) {
               val key: PurificationReportKey = new PurificationReportKey(
                  report.getBugResFile(), report.getRegion().startLine, report.getCheck_name(), CollectionsKt.first(report.getPathEvents()) as BugPathEvent
               );
               val `$this$getOrPut$iv`: ConcurrentMap = this.purificationReports;
               var var10000: Any = this.purificationReports.get(key);
               if (var10000 == null) {
                  val `default$iv`: Any = new AtomicInteger(0);
                  var10000 = `$this$getOrPut$iv`.putIfAbsent(key, `default$iv`);
                  if (var10000 == null) {
                     var10000 = `default$iv`;
                  }
               }

               (var10000 as AtomicInteger).getAndUpdate(new IntUnaryOperator(this, report) {
                  {
                     this.this$0 = `$receiver`;
                     this.$report = `$report`;
                  }

                  @Override
                  public final int applyAsInt(int it) {
                     return if (it > 5) it else (if (ResultCollector.access$getReports$p(this.this$0).add(this.$report)) it + 1 else it);
                  }
               });
            }
         }
      }
   }

   private fun addReport(report: Report) {
      this.addReport(CollectionsKt.listOf(report));
   }

   public override fun accept(reports: Collection<Report>) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IReportsVisitor).accept(reports);
      }
   }

   public override suspend fun flush() {
      var `$continuation`: Continuation;
      label48: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label48;
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
               return this.this$0.flush(this as Continuation<? super Unit>);
            }
         };
      }

      var job3: Job;
      var var19: Any;
      label40: {
         val `$result`: Any = `$continuation`.result;
         var19 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         var jobs2: java.util.List;
         switch ($continuation.label) {
            case 0:
               ResultKt.throwOnFailure(`$result`);
               this.monitor.addAnalyzeFinishHook(new Thread(new Runnable(this) {
                  {
                     this.this$0 = `$receiver`;
                  }

                  @Override
                  public final void run() {
                     CGUtils.INSTANCE.flushMissedClasses(ResultCollector.access$getOutputDir$p(this.this$0));
                  }
               }));
               val var20: Job = BuildersKt.launch$default(
                  CoroutineScopeKt.CoroutineScope(Dispatchers.getDefault() as CoroutineContext),
                  null,
                  null,
                  (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
                     int label;

                     {
                        super(2, `$completionx`);
                        this.this$0 = `$receiver`;
                     }

                     public final Object invokeSuspend(Object $result) {
                        IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              this.this$0.accept(this.this$0.getReports());
                              return Unit.INSTANCE;
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }
                     }

                     public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                        return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
                     }

                     public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                        return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                     }
                  }) as Function2,
                  3,
                  null
               );
               var20.start();
               val var21: java.lang.Iterable = this.collectors;
               val var7: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.collectors, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  val var13: Job = BuildersKt.launch$default(
                     CoroutineScopeKt.CoroutineScope(Dispatchers.getDefault() as CoroutineContext),
                     null,
                     null,
                     (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(`item$iv$iv` as IResultCollector, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$it = `$it`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val var10000: IResultCollector = this.$it;
                                 val var10001: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.flush(var10001) === var2) {
                                    return var2;
                                 }
                                 break;
                              case 1:
                                 ResultKt.throwOnFailure(`$result`);
                                 break;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }

                           return Unit.INSTANCE;
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           return (new <anonymous constructor>(this.$it, `$completion`)) as Continuation<Unit>;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2,
                     3,
                     null
                  );
                  var13.start();
                  var7.add(var13);
               }

               jobs2 = var7 as java.util.List;
               val var23: Job = BuildersKt.launch$default(
                  CoroutineScopeKt.CoroutineScope(Dispatchers.getDefault() as CoroutineContext),
                  null,
                  null,
                  (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
                     int label;

                     {
                        super(2, `$completionx`);
                        this.this$0 = `$receiver`;
                     }

                     public final Object invokeSuspend(Object $result) {
                        val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              val var10000: ResultCollector = this.this$0;
                              val var10001: MainConfig = ResultCollector.access$getMainConfig$p(this.this$0);
                              val var10002: MetricsMonitor = this.this$0.getMonitor();
                              val var10003: Continuation = this as Continuation;
                              this.label = 1;
                              if (ResultCollector.access$flushOutputType(var10000, var10001, var10002, var10003) === var2) {
                                 return var2;
                              }
                              break;
                           case 1:
                              ResultKt.throwOnFailure(`$result`);
                              break;
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        return Unit.INSTANCE;
                     }

                     public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                        return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
                     }

                     public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                        return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                     }
                  }) as Function2,
                  3,
                  null
               );
               var23.start();
               job3 = var23;
               `$continuation`.L$0 = jobs2;
               `$continuation`.L$1 = var23;
               `$continuation`.label = 1;
               if (var20.join(`$continuation`) === var19) {
                  return var19;
               }
               break;
            case 1:
               job3 = `$continuation`.L$1 as Job;
               jobs2 = `$continuation`.L$0 as java.util.List;
               ResultKt.throwOnFailure(`$result`);
               break;
            case 2:
               job3 = `$continuation`.L$0 as Job;
               ResultKt.throwOnFailure(`$result`);
               break label40;
            case 3:
               ResultKt.throwOnFailure(`$result`);
               return Unit.INSTANCE;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         val var10000: java.util.Collection = jobs2;
         `$continuation`.L$0 = job3;
         `$continuation`.L$1 = null;
         `$continuation`.label = 2;
         if (AwaitKt.joinAll(var10000, `$continuation`) === var19) {
            return var19;
         }
      }

      `$continuation`.L$0 = null;
      `$continuation`.label = 3;
      return if (job3.join(`$continuation`) === var19) var19 else Unit.INSTANCE;
   }

   public override fun reportMissingMethod(method: SootMethod) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IMissingSummaryReporter).reportMissingMethod(method);
      }
   }

   public open fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IFlowDroidResultCollector).onResultsAvailable(cfg, results);
      }

      var var10000: java.util.Set = results.getResultSet();
      if (var10000 == null) {
         var10000 = SetsKt.emptySet();
      }

      for (var10000 : var10000) {
         this.addReport(this.resultConverter.getReport(cfg, var10000 as DataFlowResult, this.getBugTypeProvider(), this.serializeTaintPath));
      }
   }

   public open fun onResultAvailable(icfg: IInfoflowCFG, abs: AbstractionAtSink): Boolean {
      val `$this$any$iv`: java.lang.Iterable = this.collectorsFlowDroid;
      var var10000: Boolean;
      if (this.collectorsFlowDroid is java.util.Collection && this.collectorsFlowDroid.isEmpty()) {
         var10000 = false;
      } else {
         label41: {
            for (Object element$iv : $this$any$iv) {
               if (!(`element$iv` as IFlowDroidResultCollector).onResultAvailable(icfg, abs)) {
                  var10000 = true;
                  break label41;
               }
            }

            var10000 = false;
         }
      }

      return !var10000;
   }

   public override fun afterAnalyze(analysis: InterProceduralValueAnalysis) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IIPAnalysisResultCollector).afterAnalyze(analysis);
      }
   }

   public override fun reportDataFlowBug(reports: List<Report>) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IIPAnalysisResultCollector).reportDataFlowBug(reports);
      }

      this.addReport(reports);
   }

   public override fun addUtState() {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as IUTBotResultCollector).addUtState();
      }
   }

   private suspend fun flushOutputType(mainConfig: MainConfig, monitor: MetricsMonitor) {
      val outputTypesLocal: java.util.Set = CollectionsKt.toMutableSet(this.outputTypes);
      if (outputTypesLocal.isEmpty()) {
         logger.warn(ResultCollector::flushOutputType$lambda$18);
         outputTypesLocal.add(OutputType.PLIST);
         outputTypesLocal.add(OutputType.SARIF);
      }

      if (this.flushCoverage) {
         outputTypesLocal.add(OutputType.Coverage);
      }

      val var10000: ReportConverter = new ReportConverter(mainConfig, null, 2, null);
      val var10002: IProjectFileLocator = this.locator;
      val `$this$map$iv`: java.lang.Iterable = outputTypesLocal;
      val var20: JacocoCompoundCoverage = this.coverage;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         val type: OutputType = `item$iv$iv` as OutputType;
         var var22: IReportConsumer;
         switch (ResultCollector.WhenMappings.$EnumSwitchMapping$0[((OutputType)item$iv$iv).ordinal()]) {
            case 1:
               var22 = new PlistDiagnostics(mainConfig, this.info, this.outputDir.resolve(type.getDisplayName()).toDirectory());
               break;
            case 2:
               var22 = new SarifDiagnostics(this.outputDir.resolve(type.getDisplayName()).toDirectory(), null, 2, null);
               break;
            case 3:
               var22 = new SarifDiagnosticsPack(this.outputDir.resolve(type.getDisplayName()).toDirectory(), null, null, null, null, 30, null);
               break;
            case 4:
               var22 = new SarifDiagnosticsCopySrc(this.outputDir.resolve(type.getDisplayName()).toDirectory(), null, null, null, null, 30, null);
               break;
            case 5:
               val var14: SqliteDiagnostics = flushOutputType$newSqliteDiagnostics(mainConfig, this, monitor);
               monitor.addAnalyzeFinishHook(new Thread(new Runnable(mainConfig, this, monitor) {
                  {
                     this.$mainConfig = `$mainConfig`;
                     this.this$0 = `$receiver`;
                     this.$monitor = `$monitor`;
                  }

                  @Override
                  public final void run() {
                     label19: {
                        val var1: Closeable = ResultCollector.access$flushOutputType$newSqliteDiagnostics(this.$mainConfig, this.this$0, this.$monitor);
                        var var2: java.lang.Throwable = null;

                        try {
                           try {
                              val it: SqliteDiagnostics = var1 as SqliteDiagnostics;
                              BuildersKt.runBlocking$default(null, (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(it, null) {
                                 int label;

                                 {
                                    super(2, `$completionx`);
                                    this.$it = `$it`;
                                 }

                                 public final Object invokeSuspend(Object $result) {
                                    IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                       case 0:
                                          ResultKt.throwOnFailure(`$result`);
                                          SqliteDiagnostics.open$default(this.$it, null, 1, null);
                                          return Unit.INSTANCE;
                                       default:
                                          throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                    }
                                 }

                                 public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                    return (new <anonymous constructor>(this.$it, `$completion`)) as Continuation<Unit>;
                                 }

                                 public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                    return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                 }
                              }) as Function2, 1, null);
                              it.writeAnalyzerResultFiles();
                           } catch (var5: java.lang.Throwable) {
                              var2 = var5;
                              throw var5;
                           }
                        } catch (var6: java.lang.Throwable) {
                           CloseableKt.closeFinally(var1, var2);
                        }

                        CloseableKt.closeFinally(var1, null);
                     }
                  }
               }));
               var22 = var14;
               break;
            case 6:
               var22 = this.getCoverageReportWriter();
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         `destination$iv$iv`.add(var22);
      }

      val var23: Any = var10000.flush(
         mainConfig, var10002, var20, `destination$iv$iv` as MutableList<IReportConsumer>, this.getReports(), this.outputDir, `$completion`
      );
      return if (var23 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var23 else Unit.INSTANCE;
   }

   private fun getCoverageReportWriter(): IReportConsumer {
      return new IReportConsumer(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public OutputType getType() {
            return OutputType.Coverage;
         }

         @Override
         public Object init(Continuation<? super Unit> $completion) {
            return Unit.INSTANCE;
         }

         @Override
         public Object run(IProjectFileLocator param1, Continuation<? super Unit> param2) {
            // $VF: Couldn't be decompiled
            // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
            // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.code.cfg.ExceptionRangeCFG.isCircular()" because "range" is null
            //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.graphToStatement(DomHelper.java:84)
            //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:203)
            //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
            //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:157)
            //
            // Bytecode:
            // 000: aload 2
            // 001: instanceof cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1
            // 004: ifeq 027
            // 007: aload 2
            // 008: checkcast cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1
            // 00b: astore 18
            // 00d: aload 18
            // 00f: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.label I
            // 012: ldc -2147483648
            // 014: iand
            // 015: ifeq 027
            // 018: aload 18
            // 01a: dup
            // 01b: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.label I
            // 01e: ldc -2147483648
            // 020: isub
            // 021: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.label I
            // 024: goto 032
            // 027: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1
            // 02a: dup
            // 02b: aload 0
            // 02c: aload 2
            // 02d: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.<init> (Lcn/sast/framework/result/ResultCollector$getCoverageReportWriter$1;Lkotlin/coroutines/Continuation;)V
            // 030: astore 18
            // 032: aload 18
            // 034: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.result Ljava/lang/Object;
            // 037: astore 17
            // 039: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
            // 03c: astore 20
            // 03e: aload 18
            // 040: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.label I
            // 043: tableswitch 770 0 2 25 227 424
            // 05c: aload 17
            // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
            // 061: nop
            // 062: getstatic cn/sast/framework/report/ReportConverter.Companion Lcn/sast/framework/report/ReportConverter$Companion;
            // 065: invokevirtual cn/sast/framework/report/ReportConverter$Companion.getLogger ()Lmu/KLogger;
            // 068: invokestatic org/utbot/common/LoggingKt.info (Lmu/KLogger;)Lorg/utbot/common/LoggerWithLogMethod;
            // 06b: astore 3
            // 06c: ldc "Calculate coverage data ..."
            // 06e: astore 4
            // 070: aload 0
            // 071: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1.this$0 Lcn/sast/framework/result/ResultCollector;
            // 074: astore 5
            // 076: bipush 0
            // 077: istore 6
            // 079: aload 3
            // 07a: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
            // 07d: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$1
            // 080: dup
            // 081: aload 4
            // 083: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$1.<init> (Ljava/lang/String;)V
            // 086: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
            // 08b: pop
            // 08c: invokestatic java/time/LocalDateTime.now ()Ljava/time/LocalDateTime;
            // 08f: astore 7
            // 091: bipush 0
            // 092: istore 8
            // 094: new kotlin/jvm/internal/Ref$ObjectRef
            // 097: dup
            // 098: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
            // 09b: astore 9
            // 09d: aload 9
            // 09f: getstatic org/utbot/common/Maybe.Companion Lorg/utbot/common/Maybe$Companion;
            // 0a2: invokevirtual org/utbot/common/Maybe$Companion.empty ()Lorg/utbot/common/Maybe;
            // 0a5: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
            // 0a8: nop
            // 0a9: aload 9
            // 0ab: astore 16
            // 0ad: bipush 0
            // 0ae: istore 10
            // 0b0: aload 5
            // 0b2: invokevirtual cn/sast/framework/result/ResultCollector.getMonitor ()Lcn/sast/framework/metrics/MetricsMonitor;
            // 0b5: ldc "flush.coverage"
            // 0b7: invokevirtual cn/sast/framework/metrics/MetricsMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer;
            // 0ba: checkcast cn/sast/api/util/PhaseIntervalTimer
            // 0bd: astore 11
            // 0bf: bipush 0
            // 0c0: istore 12
            // 0c2: aload 11
            // 0c4: ifnonnull 176
            // 0c7: bipush 0
            // 0c8: istore 13
            // 0ca: aload 5
            // 0cc: invokevirtual cn/sast/framework/result/ResultCollector.getCoverage ()Lcn/sast/framework/report/coverage/JacocoCompoundCoverage;
            // 0cf: aload 1
            // 0d0: aconst_null
            // 0d1: aconst_null
            // 0d2: bipush 0
            // 0d3: bipush 14
            // 0d5: aconst_null
            // 0d6: invokestatic cn/sast/framework/report/coverage/JacocoCompoundCoverage.copy$default (Lcn/sast/framework/report/coverage/JacocoCompoundCoverage;Lcn/sast/framework/report/IProjectFileLocator;Lcn/sast/framework/report/coverage/Coverage;Lcn/sast/framework/report/coverage/Coverage;ZILjava/lang/Object;)Lcn/sast/framework/report/coverage/JacocoCompoundCoverage;
            // 0d9: aload 5
            // 0db: invokestatic cn/sast/framework/result/ResultCollector.access$getMainConfig$p (Lcn/sast/framework/result/ResultCollector;)Lcn/sast/api/config/MainConfig;
            // 0de: invokevirtual cn/sast/api/config/MainConfig.getOutput_dir ()Lcn/sast/common/IResDirectory;
            // 0e1: aload 5
            // 0e3: invokestatic cn/sast/framework/result/ResultCollector.access$getMainConfig$p (Lcn/sast/framework/result/ResultCollector;)Lcn/sast/api/config/MainConfig;
            // 0e6: invokevirtual cn/sast/api/config/MainConfig.getSourceEncoding ()Ljava/nio/charset/Charset;
            // 0e9: aload 18
            // 0eb: aload 18
            // 0ed: aload 3
            // 0ee: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$0 Ljava/lang/Object;
            // 0f1: aload 18
            // 0f3: aload 4
            // 0f5: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$1 Ljava/lang/Object;
            // 0f8: aload 18
            // 0fa: aload 7
            // 0fc: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$2 Ljava/lang/Object;
            // 0ff: aload 18
            // 101: aload 9
            // 103: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$3 Ljava/lang/Object;
            // 106: aload 18
            // 108: aload 16
            // 10a: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$4 Ljava/lang/Object;
            // 10d: aload 18
            // 10f: iload 8
            // 111: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.I$0 I
            // 114: aload 18
            // 116: bipush 1
            // 117: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.label I
            // 11a: invokevirtual cn/sast/framework/report/coverage/JacocoCompoundCoverage.flush (Lcn/sast/common/IResDirectory;Ljava/nio/charset/Charset;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
            // 11d: dup
            // 11e: aload 20
            // 120: if_acmpne 172
            // 123: aload 20
            // 125: areturn
            // 126: bipush 0
            // 127: istore 6
            // 129: bipush 0
            // 12a: istore 10
            // 12c: bipush 0
            // 12d: istore 12
            // 12f: bipush 0
            // 130: istore 13
            // 132: aload 18
            // 134: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.I$0 I
            // 137: istore 8
            // 139: aload 18
            // 13b: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$4 Ljava/lang/Object;
            // 13e: checkcast kotlin/jvm/internal/Ref$ObjectRef
            // 141: astore 16
            // 143: aload 18
            // 145: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$3 Ljava/lang/Object;
            // 148: checkcast kotlin/jvm/internal/Ref$ObjectRef
            // 14b: astore 9
            // 14d: aload 18
            // 14f: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$2 Ljava/lang/Object;
            // 152: checkcast java/time/LocalDateTime
            // 155: astore 7
            // 157: aload 18
            // 159: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$1 Ljava/lang/Object;
            // 15c: checkcast java/lang/String
            // 15f: astore 4
            // 161: aload 18
            // 163: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$0 Ljava/lang/Object;
            // 166: checkcast org/utbot/common/LoggerWithLogMethod
            // 169: astore 3
            // 16a: nop
            // 16b: aload 17
            // 16d: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
            // 170: aload 17
            // 172: pop
            // 173: goto 267
            // 176: aload 11
            // 178: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
            // 17b: astore 14
            // 17d: nop
            // 17e: bipush 0
            // 17f: istore 13
            // 181: aload 5
            // 183: invokevirtual cn/sast/framework/result/ResultCollector.getCoverage ()Lcn/sast/framework/report/coverage/JacocoCompoundCoverage;
            // 186: aload 1
            // 187: aconst_null
            // 188: aconst_null
            // 189: bipush 0
            // 18a: bipush 14
            // 18c: aconst_null
            // 18d: invokestatic cn/sast/framework/report/coverage/JacocoCompoundCoverage.copy$default (Lcn/sast/framework/report/coverage/JacocoCompoundCoverage;Lcn/sast/framework/report/IProjectFileLocator;Lcn/sast/framework/report/coverage/Coverage;Lcn/sast/framework/report/coverage/Coverage;ZILjava/lang/Object;)Lcn/sast/framework/report/coverage/JacocoCompoundCoverage;
            // 190: aload 5
            // 192: invokestatic cn/sast/framework/result/ResultCollector.access$getMainConfig$p (Lcn/sast/framework/result/ResultCollector;)Lcn/sast/api/config/MainConfig;
            // 195: invokevirtual cn/sast/api/config/MainConfig.getOutput_dir ()Lcn/sast/common/IResDirectory;
            // 198: aload 5
            // 19a: invokestatic cn/sast/framework/result/ResultCollector.access$getMainConfig$p (Lcn/sast/framework/result/ResultCollector;)Lcn/sast/api/config/MainConfig;
            // 19d: invokevirtual cn/sast/api/config/MainConfig.getSourceEncoding ()Ljava/nio/charset/Charset;
            // 1a0: aload 18
            // 1a2: aload 18
            // 1a4: aload 3
            // 1a5: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$0 Ljava/lang/Object;
            // 1a8: aload 18
            // 1aa: aload 4
            // 1ac: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$1 Ljava/lang/Object;
            // 1af: aload 18
            // 1b1: aload 7
            // 1b3: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$2 Ljava/lang/Object;
            // 1b6: aload 18
            // 1b8: aload 9
            // 1ba: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$3 Ljava/lang/Object;
            // 1bd: aload 18
            // 1bf: aload 11
            // 1c1: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$4 Ljava/lang/Object;
            // 1c4: aload 18
            // 1c6: aload 14
            // 1c8: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$5 Ljava/lang/Object;
            // 1cb: aload 18
            // 1cd: aload 16
            // 1cf: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$6 Ljava/lang/Object;
            // 1d2: aload 18
            // 1d4: iload 8
            // 1d6: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.I$0 I
            // 1d9: aload 18
            // 1db: bipush 2
            // 1dc: putfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.label I
            // 1df: invokevirtual cn/sast/framework/report/coverage/JacocoCompoundCoverage.flush (Lcn/sast/common/IResDirectory;Ljava/nio/charset/Charset;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
            // 1e2: dup
            // 1e3: aload 20
            // 1e5: if_acmpne 24b
            // 1e8: aload 20
            // 1ea: areturn
            // 1eb: bipush 0
            // 1ec: istore 6
            // 1ee: bipush 0
            // 1ef: istore 10
            // 1f1: bipush 0
            // 1f2: istore 12
            // 1f4: bipush 0
            // 1f5: istore 13
            // 1f7: aload 18
            // 1f9: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.I$0 I
            // 1fc: istore 8
            // 1fe: aload 18
            // 200: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$6 Ljava/lang/Object;
            // 203: checkcast kotlin/jvm/internal/Ref$ObjectRef
            // 206: astore 16
            // 208: aload 18
            // 20a: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$5 Ljava/lang/Object;
            // 20d: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
            // 210: astore 14
            // 212: aload 18
            // 214: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$4 Ljava/lang/Object;
            // 217: checkcast cn/sast/api/util/PhaseIntervalTimer
            // 21a: astore 11
            // 21c: aload 18
            // 21e: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$3 Ljava/lang/Object;
            // 221: checkcast kotlin/jvm/internal/Ref$ObjectRef
            // 224: astore 9
            // 226: aload 18
            // 228: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$2 Ljava/lang/Object;
            // 22b: checkcast java/time/LocalDateTime
            // 22e: astore 7
            // 230: aload 18
            // 232: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$1 Ljava/lang/Object;
            // 235: checkcast java/lang/String
            // 238: astore 4
            // 23a: aload 18
            // 23c: getfield cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$1.L$0 Ljava/lang/Object;
            // 23f: checkcast org/utbot/common/LoggerWithLogMethod
            // 242: astore 3
            // 243: nop
            // 244: aload 17
            // 246: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
            // 249: aload 17
            // 24b: pop
            // 24c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
            // 24f: astore 15
            // 251: aload 11
            // 253: aload 14
            // 255: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
            // 258: goto 267
            // 25b: astore 13
            // 25d: aload 11
            // 25f: aload 14
            // 261: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
            // 264: aload 13
            // 266: athrow
            // 267: nop
            // 268: aload 16
            // 26a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
            // 26d: astore 19
            // 26f: new org/utbot/common/Maybe
            // 272: dup
            // 273: aload 19
            // 275: invokespecial org/utbot/common/Maybe.<init> (Ljava/lang/Object;)V
            // 278: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
            // 27b: aload 9
            // 27d: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
            // 280: checkcast org/utbot/common/Maybe
            // 283: invokevirtual org/utbot/common/Maybe.getOrThrow ()Ljava/lang/Object;
            // 286: astore 10
            // 288: nop
            // 289: aload 9
            // 28b: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
            // 28e: checkcast org/utbot/common/Maybe
            // 291: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
            // 294: ifeq 2b1
            // 297: aload 3
            // 298: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
            // 29b: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$2
            // 29e: dup
            // 29f: aload 7
            // 2a1: aload 4
            // 2a3: aload 9
            // 2a5: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$2.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
            // 2a8: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
            // 2ad: pop
            // 2ae: goto 2c6
            // 2b1: aload 3
            // 2b2: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
            // 2b5: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$3
            // 2b8: dup
            // 2b9: aload 7
            // 2bb: aload 4
            // 2bd: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$3.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
            // 2c0: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
            // 2c5: pop
            // 2c6: goto 341
            // 2c9: astore 11
            // 2cb: aload 3
            // 2cc: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
            // 2cf: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$4
            // 2d2: dup
            // 2d3: aload 7
            // 2d5: aload 4
            // 2d7: aload 11
            // 2d9: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$4.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Ljava/lang/Throwable;)V
            // 2dc: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
            // 2e1: pop
            // 2e2: bipush 1
            // 2e3: istore 8
            // 2e5: aload 11
            // 2e7: athrow
            // 2e8: astore 11
            // 2ea: iload 8
            // 2ec: ifne 32c
            // 2ef: aload 9
            // 2f1: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
            // 2f4: checkcast org/utbot/common/Maybe
            // 2f7: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
            // 2fa: ifeq 317
            // 2fd: aload 3
            // 2fe: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
            // 301: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$5
            // 304: dup
            // 305: aload 7
            // 307: aload 4
            // 309: aload 9
            // 30b: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$5.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
            // 30e: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
            // 313: pop
            // 314: goto 32c
            // 317: aload 3
            // 318: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
            // 31b: new cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$6
            // 31e: dup
            // 31f: aload 7
            // 321: aload 4
            // 323: invokespecial cn/sast/framework/result/ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$6.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
            // 326: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
            // 32b: pop
            // 32c: aload 11
            // 32e: athrow
            // 32f: astore 3
            // 330: getstatic cn/sast/framework/report/ReportConverter.Companion Lcn/sast/framework/report/ReportConverter$Companion;
            // 333: invokevirtual cn/sast/framework/report/ReportConverter$Companion.getLogger ()Lmu/KLogger;
            // 336: ldc "Calculate coverage data failed"
            // 338: aload 3
            // 339: checkcast java/lang/Throwable
            // 33c: invokeinterface mu/KLogger.error (Ljava/lang/String;Ljava/lang/Throwable;)V 3
            // 341: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
            // 344: areturn
            // 345: new java/lang/IllegalStateException
            // 348: dup
            // 349: ldc "call to 'resume' before 'invoke' with coroutine"
            // 34b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
            // 34e: athrow
         }

         @Override
         public void close() {
         }
      };
   }

   @JvmStatic
   fun `bugTypeProvider_delegate$lambda$2`(`this$0`: ResultCollector): BugTypeProvider {
      Thread.interrupted();
      val var1: BugTypeProvider = new BugTypeProvider(`this$0`.mainConfig, `this$0`.getPreAnalysis());
      var1.init();
      return var1;
   }

   @JvmStatic
   fun `flushOutputType$lambda$18`(): Any {
      return "not special any output types! Will use PLIST and SARIF formats and SQLITE for generating report";
   }

   @JvmStatic
   fun `flushOutputType$newSqliteDiagnostics`(`$mainConfig`: MainConfig, `this$0`: ResultCollector, `$monitor`: MetricsMonitor): SqliteDiagnostics {
      return Companion.newSqliteDiagnostics(`$mainConfig`, `this$0`.info, `this$0`.outputDir, `$monitor`);
   }

   @JvmStatic
   fun `logger$lambda$21`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger

      public fun newSqliteDiagnostics(mainConfig: MainConfig, info: SootInfoCache?, outputDir: IResDirectory, monitor: MetricsMonitor?): SqliteDiagnostics {
         return new SqliteDiagnostics(mainConfig, info, monitor, outputDir.resolve(OutputType.SQLITE.getDisplayName()).toDirectory()) {
            {
               super(`$mainConfig`, `$info`, `$super_call_param$1`, `$monitor`, null, 16, null);
               this.$mainConfig = `$mainConfig`;
            }

            @Override
            public Charset getSourceEncoding(IResFile $this$sourceEncoding) {
               return this.$mainConfig.getSourceEncoding();
            }
         };
      }
   }
}


internal class `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$1` : Function0<Object> {
   fun `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}

internal class `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$2` : Function0<Object> {
   fun `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}

internal class `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$3` : Function0<Object> {
   fun `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}


internal class `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$4` : Function0<Object> {
   fun `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$4`(
      `$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable
   ) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$t = `$t`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl(ResultKt.createFailure(this.$t));
      return "Finished (in $var10000): $var10001 :: EXCEPTION :: ";
   }
}

internal class `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$5` : Function0<Object> {
   fun `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}

internal class `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$6` : Function0<Object> {
   fun `ResultCollector$getCoverageReportWriter$1$run$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}