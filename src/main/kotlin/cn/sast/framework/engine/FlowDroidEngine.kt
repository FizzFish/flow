package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.dataflow.infoflow.FlowDroidFactory
import cn.sast.dataflow.infoflow.InfoflowConfigurationExt
import cn.sast.dataflow.infoflow.provider.MethodSummaryProvider
import cn.sast.dataflow.infoflow.provider.MissingSummaryWrapper
import cn.sast.dataflow.infoflow.provider.SourceSinkProvider
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import cn.sast.framework.entries.apk.ApkLifeCycleComponent
import cn.sast.framework.result.IFlowDroidResultCollector
import cn.sast.framework.result.IMissingSummaryReporter
import java.time.LocalDateTime
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.DataFlowDirection
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.data.AndroidMemoryManager
import soot.jimple.infoflow.android.source.AccessPathBasedSourceSinkManager
import soot.jimple.infoflow.cfg.BiDirICFGFactory
import soot.jimple.infoflow.cfg.DefaultBiDiICFGFactory
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.data.FlowDroidMemoryManager.PathDataErasureMode
import soot.jimple.infoflow.handlers.PostAnalysisHandler
import soot.jimple.infoflow.handlers.ResultsAvailableHandler
import soot.jimple.infoflow.methodSummary.data.provider.IMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.taintWrappers.SummaryTaintWrapper
import soot.jimple.infoflow.problems.TaintPropagationResults.OnTaintPropagationResultAdded
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import soot.jimple.infoflow.solver.memory.IMemoryManager
import soot.jimple.infoflow.solver.memory.IMemoryManagerFactory
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.ReachableMethods

@SourceDebugExtension(["SMAP\nFlowDroidEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowDroidEngine.kt\ncn/sast/framework/engine/FlowDroidEngine\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Logging.kt\norg/utbot/common/LoggingKt\n*L\n1#1,235:1\n1863#2,2:236\n1628#2,3:251\n49#3,13:238\n62#3,11:254\n*S KotlinDebug\n*F\n+ 1 FlowDroidEngine.kt\ncn/sast/framework/engine/FlowDroidEngine\n*L\n177#1:236,2\n184#1:251,3\n183#1:238,13\n183#1:254,11\n*E\n"])
public class FlowDroidEngine(mainConfig: MainConfig, infoFlowConfig: InfoflowConfiguration, extInfoFlowConfig: InfoflowConfigurationExt) {
   public final val mainConfig: MainConfig
   public final val infoFlowConfig: InfoflowConfiguration
   public final val extInfoFlowConfig: InfoflowConfigurationExt

   init {
      this.mainConfig = mainConfig;
      this.infoFlowConfig = infoFlowConfig;
      this.extInfoFlowConfig = extInfoFlowConfig;
   }

   public fun sourceSinkManager(sourceSinkProvider: ISourceSinkDefinitionProvider): ISourceSinkManager {
      val config: InfoflowAndroidConfiguration = new InfoflowAndroidConfiguration();
      config.getSourceSinkConfig().merge(this.infoFlowConfig.getSourceSinkConfig());
      return (new AccessPathBasedSourceSinkManager(sourceSinkProvider.getSources(), sourceSinkProvider.getSinks(), config)) as ISourceSinkManager;
   }

   public fun analyze(
      preAnalysis: PreAnalysisCoroutineScope,
      soot: SootCtx,
      provider: IEntryPointProvider,
      cfgFactory: BiDirICFGFactory? = null,
      result: IFlowDroidResultCollector,
      missWrapper: IMissingSummaryReporter
   ) {
      BuildersKt.runBlocking$default(
         null,
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, preAnalysis, soot, provider, cfgFactory, result, missWrapper, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
                  this.$preAnalysis = `$preAnalysis`;
                  this.$soot = `$soot`;
                  this.$provider = `$provider`;
                  this.$cfgFactory = `$cfgFactory`;
                  this.$result = `$result`;
                  this.$missWrapper = `$missWrapper`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        val var10000: FlowDroidEngine = this.this$0;
                        val var10001: PreAnalysisCoroutineScope = this.$preAnalysis;
                        val var10002: SootCtx = this.$soot;
                        val var10003: IEntryPointProvider = this.$provider;
                        val var10004: BiDirICFGFactory = this.$cfgFactory;
                        val var10005: java.util.Set = SetsKt.setOf(this.$result);
                        val var10006: java.util.Set = SetsKt.setOf(this.$result);
                        val var10007: IMissingSummaryReporter = this.$missWrapper;
                        val var10008: Continuation = this as Continuation;
                        this.label = 1;
                        if (var10000.analyze(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008) === var2) {
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
                  return (
                     new <anonymous constructor>(
                        this.this$0, this.$preAnalysis, this.$soot, this.$provider, this.$cfgFactory, this.$result, this.$missWrapper, `$completion`
                     )
                  ) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2,
         1,
         null
      );
   }

   public fun beforeAnalyze(cfgFactory: BiDirICFGFactory? = null) {
      this.infoFlowConfig.setWriteOutputFiles(false);
      if (cfgFactory != null && this.infoFlowConfig is InfoflowAndroidConfiguration) {
         val androidPath: java.lang.String = this.mainConfig.getAndroidPlatformDir();
         if (cfgFactory is DefaultBiDiICFGFactory) {
            (cfgFactory as DefaultBiDiICFGFactory).setIsAndroid(androidPath != null);
         }
      }

      if (this.infoFlowConfig is InfoflowAndroidConfiguration) {
         if (!((this.infoFlowConfig as InfoflowAndroidConfiguration).getAnalysisFileConfig().getAndroidPlatformDir() == "unused")) {
            throw new IllegalStateException("Check failed.".toString());
         }

         if (!((this.infoFlowConfig as InfoflowAndroidConfiguration).getAnalysisFileConfig().getTargetAPKFile() == "unused")) {
            throw new IllegalStateException("Check failed.".toString());
         }
      }

      FlowDroidEngineKt.fix(this.infoFlowConfig);
   }

   public suspend fun analyzeInScene(
      task: AnalyzeTask,
      provider: IEntryPointProvider,
      soot: SootCtx,
      preAnalysis: PreAnalysisCoroutineScope,
      cfgFactory: BiDirICFGFactory? = ...,
      resultAddedHandlers: Set<OnTaintPropagationResultAdded>,
      onResultsAvailable: Set<ResultsAvailableHandler>,
      methodSummariesMissing: IMissingSummaryReporter
   ) {
      label182: {
         var `$continuation`: Continuation;
         label107: {
            if (`$completion` is <unrepresentable>) {
               `$continuation` = `$completion` as <unrepresentable>;
               if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
                  `$continuation`.label -= Integer.MIN_VALUE;
                  break label107;
               }
            }

            `$continuation` = new ContinuationImpl(this, `$completion`) {
               Object L$0;
               Object L$1;
               Object L$2;
               Object L$3;
               Object L$4;
               Object L$5;
               Object L$6;
               Object L$7;
               Object L$8;
               Object L$9;
               int label;

               {
                  super(`$completion`);
                  this.this$0 = `this$0`;
               }

               @Nullable
               public final Object invokeSuspend(@NotNull Object $result) {
                  this.result = `$result`;
                  this.label |= Integer.MIN_VALUE;
                  return this.this$0.analyzeInScene(null, null, null, null, null, null, null, null, this as Continuation<? super Unit>);
               }
            };
         }

         var sourceSinkProviderInstance: SourceSinkProvider;
         var var55: MissingSummaryWrapper;
         label101: {
            var infoflow: MethodSummaryProvider;
            label111: {
               val `$result`: Any = `$continuation`.result;
               val var37: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch ($continuation.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     val cg: CallGraph = soot.getSootMethodCallGraph();
                     Scene.v().setCallGraph(cg);
                     Scene.v().setReachableMethods(new ReachableMethods(cg, new ArrayList<>(task.getEntries())));
                     val var10000: java.util.Iterator = Scene.v().getClasses().iterator();
                     val var44: java.util.Iterator = var10000;

                     while (var44.hasNext()) {
                        for (SootMethod sm : ((SootClass)var44.next()).getMethods()) {
                           if (sourceSinkManager.isConcrete() && !sourceSinkManager.hasActiveBody()) {
                              sourceSinkManager.setPhantom(true);
                           }
                        }
                     }

                     sourceSinkProviderInstance = new SourceSinkProvider(this.mainConfig, preAnalysis);
                     `$continuation`.L$0 = this;
                     `$continuation`.L$1 = task;
                     `$continuation`.L$2 = provider;
                     `$continuation`.L$3 = soot;
                     `$continuation`.L$4 = preAnalysis;
                     `$continuation`.L$5 = cfgFactory;
                     `$continuation`.L$6 = resultAddedHandlers;
                     `$continuation`.L$7 = onResultsAvailable;
                     `$continuation`.L$8 = methodSummariesMissing;
                     `$continuation`.L$9 = sourceSinkProviderInstance;
                     `$continuation`.label = 1;
                     if (sourceSinkProviderInstance.initialize(`$continuation`) === var37) {
                        return var37;
                     }
                     break;
                  case 1:
                     sourceSinkProviderInstance = `$continuation`.L$9 as SourceSinkProvider;
                     methodSummariesMissing = `$continuation`.L$8 as IMissingSummaryReporter;
                     onResultsAvailable = `$continuation`.L$7 as java.util.Set;
                     resultAddedHandlers = `$continuation`.L$6 as java.util.Set;
                     cfgFactory = `$continuation`.L$5 as BiDirICFGFactory;
                     preAnalysis = `$continuation`.L$4 as PreAnalysisCoroutineScope;
                     soot = `$continuation`.L$3 as SootCtx;
                     provider = `$continuation`.L$2 as IEntryPointProvider;
                     task = `$continuation`.L$1 as IEntryPointProvider.AnalyzeTask;
                     this = `$continuation`.L$0 as FlowDroidEngine;
                     ResultKt.throwOnFailure(`$result`);
                     break;
                  case 2:
                     infoflow = `$continuation`.L$9 as MethodSummaryProvider;
                     sourceSinkProviderInstance = `$continuation`.L$8 as SourceSinkProvider;
                     methodSummariesMissing = `$continuation`.L$7 as IMissingSummaryReporter;
                     onResultsAvailable = `$continuation`.L$6 as java.util.Set;
                     resultAddedHandlers = `$continuation`.L$5 as java.util.Set;
                     cfgFactory = `$continuation`.L$4 as BiDirICFGFactory;
                     soot = `$continuation`.L$3 as SootCtx;
                     provider = `$continuation`.L$2 as IEntryPointProvider;
                     task = `$continuation`.L$1 as IEntryPointProvider.AnalyzeTask;
                     this = `$continuation`.L$0 as FlowDroidEngine;
                     ResultKt.throwOnFailure(`$result`);
                     break label111;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               if (!this.mainConfig.getUse_wrapper()) {
                  var55 = null;
                  break label101;
               }

               infoflow = new MethodSummaryProvider(this.mainConfig, preAnalysis);
               `$continuation`.L$0 = this;
               `$continuation`.L$1 = task;
               `$continuation`.L$2 = provider;
               `$continuation`.L$3 = soot;
               `$continuation`.L$4 = cfgFactory;
               `$continuation`.L$5 = resultAddedHandlers;
               `$continuation`.L$6 = onResultsAvailable;
               `$continuation`.L$7 = methodSummariesMissing;
               `$continuation`.L$8 = sourceSinkProviderInstance;
               `$continuation`.L$9 = infoflow;
               `$continuation`.label = 2;
               if (infoflow.initialize(`$continuation`) === var37) {
                  return var37;
               }
            }

            logger.info(FlowDroidEngine::analyzeInScene$lambda$0);
            var55 = new MissingSummaryWrapper(infoflow as IMethodSummaryProvider, FlowDroidEngine::analyzeInScene$lambda$1);
         }

         if (provider is ApkLifeCycleComponent) {
            (provider as ApkLifeCycleComponent).setTaintWrapper(var55 as SummaryTaintWrapper);
         }

         val var56: FlowDroidFactory = FlowDroidFactory.INSTANCE;
         val var10001: DataFlowDirection = this.infoFlowConfig.getDataFlowDirection();
         val var47: AbstractInfoflow = var56.createInfoFlow(
            var10001,
            this.mainConfig.getAndroidPlatformDir(),
            this.mainConfig.getForceAndroidJar(),
            null,
            cfgFactory,
            this.extInfoFlowConfig.getUseSparseOpt(),
            resultAddedHandlers
         );
         this.configureInfoFlow(var47, task);
         var47.setConfig(this.infoFlowConfig);
         var47.setSootConfig(null);
         var47.setTaintWrapper(var55 as ITaintPropagationWrapper);

         val var48: java.lang.Iterable;
         for (Object element$iv : var48) {
            var47.addResultsAvailableHandler(`element$iv` as ResultsAvailableHandler);
         }

         val var49: ISourceSinkManager = this.sourceSinkManager(sourceSinkProviderInstance);
         val var50: LoggerWithLogMethod = LoggingKt.info(logger);
         val var51: java.lang.String = "Run IFDS analysis for task: ${task.getName()}";
         var50.getLogMethod().invoke(new FlowDroidEngine$analyzeInScene$$inlined$bracket$default$1(var51));
         val var53: LocalDateTime = LocalDateTime.now();
         var `alreadyLogged$iv`: Boolean = false;
         val `res$iv`: ObjectRef = new ObjectRef();
         `res$iv`.element = Maybe.Companion.empty();

         try {
            try {
               val var28: <unknown>;
               val `destination$iv`: <unknown>;
               while (var28.hasNext()) {
                  `destination$iv`.add((var28.next() as SootMethod).getSignature());
               }

               val var25: Any;
               val var26: Any;
               FlowDroidEngineKt.runAnalysisReflect((AbstractInfoflow)var26, (ISourceSinkManager)var25, `destination$iv` as MutableSet<java.lang.String>);
               val var33: <unknown>;
               var33.element = new Maybe(Unit.INSTANCE);
               val var22: Any = (`res$iv`.element as Maybe).getOrThrow();
            } catch (var38: java.lang.Throwable) {
               var50.getLogMethod().invoke(new FlowDroidEngine$analyzeInScene$$inlined$bracket$default$4(var53, var51, var38));
               `alreadyLogged$iv` = true;
               throw var38;
            }
         } catch (var39: java.lang.Throwable) {
            if (!`alreadyLogged$iv`) {
               if ((`res$iv`.element as Maybe).getHasValue()) {
                  var50.getLogMethod().invoke(new FlowDroidEngine$analyzeInScene$$inlined$bracket$default$5(var53, var51, `res$iv`));
               } else {
                  var50.getLogMethod().invoke(new FlowDroidEngine$analyzeInScene$$inlined$bracket$default$6(var53, var51));
               }
            }
         }

         if ((`res$iv`.element as Maybe).getHasValue()) {
            var50.getLogMethod().invoke(new FlowDroidEngine$analyzeInScene$$inlined$bracket$default$2(var53, var51, `res$iv`));
         } else {
            var50.getLogMethod().invoke(new FlowDroidEngine$analyzeInScene$$inlined$bracket$default$3(var53, var51));
         }

         return Unit.INSTANCE;
      }
   }

   public suspend fun analyze(
      preAnalysis: PreAnalysisCoroutineScope,
      soot: SootCtx,
      provider: IEntryPointProvider,
      cfgFactory: BiDirICFGFactory? = ...,
      resultAddedHandlers: Set<OnTaintPropagationResultAdded>,
      onResultsAvailable: Set<ResultsAvailableHandler>,
      methodSummariesMissing: IMissingSummaryReporter
   ) {
      this.beforeAnalyze(cfgFactory);
      val var10000: Any = provider.getIterator()
         .collect(
            new FlowCollector(soot, this, provider, preAnalysis, cfgFactory, resultAddedHandlers, onResultsAvailable, methodSummariesMissing) {
               {
                  this.$soot = `$soot`;
                  this.this$0 = `$receiver`;
                  this.$provider = `$provider`;
                  this.$preAnalysis = `$preAnalysis`;
                  this.$cfgFactory = `$cfgFactory`;
                  this.$resultAddedHandlers = `$resultAddedHandlers`;
                  this.$onResultsAvailable = `$onResultsAvailable`;
                  this.$methodSummariesMissing = `$methodSummariesMissing`;
               }

               public final Object emit(IEntryPointProvider.AnalyzeTask task, Continuation<? super Unit> $completion) {
                  val entries: java.util.Set = CollectionsKt.toMutableSet(task.getEntries());
                  var var10000: java.util.Set = task.getAdditionalEntries();
                  if (var10000 != null) {
                     Boxing.boxBoolean(entries.addAll(var10000));
                  }

                  Scene.v().setEntryPoints(CollectionsKt.toList(entries));
                  this.$soot.constructCallGraph();
                  var10000 = (java.util.Set)this.this$0
                     .analyzeInScene(
                        task,
                        this.$provider,
                        this.$soot,
                        this.$preAnalysis,
                        this.$cfgFactory,
                        this.$resultAddedHandlers,
                        this.$onResultsAvailable,
                        this.$methodSummariesMissing,
                        `$completion`
                     );
                  return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
               }
            },
            `$completion`
         );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   private fun configureInfoFlow(infoflow: AbstractInfoflow, task: AnalyzeTask) {
      infoflow.setMemoryManagerFactory(new IMemoryManagerFactory(task) {
         {
            this.$task = `$task`;
         }

         public final IMemoryManager<Abstraction, soot.Unit> getMemoryManager(boolean tracingEnabled, PathDataErasureMode erasePathData) {
            return (new AndroidMemoryManager(tracingEnabled, erasePathData, this.$task.getComponents())) as IMemoryManager<Abstraction, soot.Unit>;
         }
      });
      infoflow.setMemoryManagerFactory(null);
      infoflow.setPostProcessors(SetsKt.setOf(new PostAnalysisHandler() {
         public InfoflowResults onResultsAvailable(InfoflowResults results, IInfoflowCFG cfg) {
            return results;
         }
      }));
   }

   @JvmStatic
   fun `analyzeInScene$lambda$0`(`$wrapper`: MethodSummaryProvider): Any {
      return "taint wrapper size: ${`$wrapper`.getClassSummaries().getAllSummaries().size()}";
   }

   @JvmStatic
   fun `analyzeInScene$lambda$1`(`$methodSummariesMissing`: IMissingSummaryReporter, method: SootMethod): Unit {
      `$methodSummariesMissing`.reportMissingMethod(method);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `logger$lambda$5`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$1` : Function0<Object> {
   fun `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$2` : Function0<Object> {
   fun `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
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
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$3` : Function0<Object> {
   fun `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$4` : Function0<Object> {
   fun `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$4`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable) {
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
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$5` : Function0<Object> {
   fun `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
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
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$6` : Function0<Object> {
   fun `FlowDroidEngine$analyzeInScene$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
