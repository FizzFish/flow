package cn.sast.framework.engine

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.PreAnalysisCoroutineScopeKt
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.CoverInst
import cn.sast.api.report.CoverTaint
import cn.sast.api.report.ICoverageCollector
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.util.IMonitor
import cn.sast.api.util.OthersKt
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.dataflow.infoflow.svfa.AP
import cn.sast.dataflow.interprocedural.analysis.AIContext
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import cn.sast.dataflow.interprocedural.check.KillEntry
import cn.sast.dataflow.interprocedural.check.PathCompanionKt
import cn.sast.dataflow.interprocedural.check.PointsToGraph
import cn.sast.dataflow.interprocedural.check.PointsToGraphBuilder
import cn.sast.dataflow.interprocedural.check.checker.CheckerModeling
import cn.sast.dataflow.interprocedural.check.checker.CheckerModelingKt
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValues
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.result.IMissingSummaryReporter
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.framework.result.ResultCollector
import cn.sast.graph.GraphPlot
import cn.sast.graph.GraphPlotKt
import cn.sast.graph.HashMutableDirectedGraph
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.UsefulMetrics
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.SerializersKt
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JvmStreamsKt
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Unit
import soot.Value
import soot.ValueBox
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.toolkits.graph.DirectedGraph

@SourceDebugExtension(["SMAP\nIPAnalysisEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IPAnalysisEngine.kt\ncn/sast/framework/engine/IPAnalysisEngine\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Logging.kt\norg/utbot/common/LoggingKt\n+ 5 JvmStreams.kt\nkotlinx/serialization/json/JvmStreamsKt\n*L\n1#1,344:1\n1557#2:345\n1628#2,3:346\n1619#2:349\n1863#2:350\n1864#2:352\n1620#2:353\n1863#2,2:354\n1557#2:356\n1628#2,3:357\n774#2:360\n865#2,2:361\n774#2:363\n865#2,2:364\n1557#2:366\n1628#2,3:367\n774#2:370\n865#2,2:371\n774#2:373\n865#2,2:374\n1557#2:376\n1628#2,3:377\n774#2:380\n865#2,2:381\n1557#2:383\n1628#2,3:384\n1#3:351\n49#4,24:387\n42#5:411\n*S KotlinDebug\n*F\n+ 1 IPAnalysisEngine.kt\ncn/sast/framework/engine/IPAnalysisEngine\n*L\n143#1:345\n143#1:346,3\n143#1:349\n143#1:350\n143#1:352\n143#1:353\n245#1:354,2\n259#1:356\n259#1:357,3\n260#1:360\n260#1:361,2\n261#1:363\n261#1:364,2\n262#1:366\n262#1:367,3\n263#1:370\n263#1:371,2\n264#1:373\n264#1:374,2\n265#1:376\n265#1:377,3\n267#1:380\n267#1:381,2\n268#1:383\n268#1:384,3\n143#1:351\n281#1:387,24\n303#1:411\n*E\n"])
public class IPAnalysisEngine(mainConfig: MainConfig, summaries: List<SummaryHandlePackage<IValue>> = (new ArrayList()) as java.util.List) {
   public final val mainConfig: MainConfig
   public final val summaries: List<SummaryHandlePackage<IValue>>
   private final val directedGraph: HashMutableDirectedGraph<SootMethod>

   init {
      this.mainConfig = mainConfig;
      this.summaries = summaries;
      this.directedGraph = new HashMutableDirectedGraph<>();
   }

   public fun ICoverageCollector.coverTaint(
      hf: AbstractHeapFactory<IValue>,
      method: SootMethod,
      node: Unit,
      succ: Unit,
      out: IFact<IValue>,
      value: Any,
      obj: CompanionV<IValue>,
      visitElement: Boolean
   ) {
      val actual: IValue = PathCompanionKt.getBindDelegate(obj);
      val array: IData = out.getValueData(actual, CheckerModelingKt.getKeyTaintProperty());
      if ((array as? ImmutableElementSet) != null && !(array as? ImmutableElementSet).isEmpty()) {
         `$this$coverTaint`.cover(new CoverTaint(method, node, value));
      }

      if (visitElement) {
         val elements: IData = out.getValueData(actual, BuiltInModelT.Array);
         val var17: IArrayHeapKV = elements as? IArrayHeapKV;
         if ((elements as? IArrayHeapKV) != null) {
            for (CompanionV e : array.getElement(hf)) {
               this.coverTaint(`$this$coverTaint`, hf, method, node, succ, out, value, mapValues, false);
            }
         }

         val var20: IData = out.getValueData(actual, BuiltInModelT.Element);
         val var19: ObjectValues = var20 as? ObjectValues;
         if ((var20 as? ObjectValues) != null) {
            for (CompanionV e : elements.getValues()) {
               this.coverTaint(`$this$coverTaint`, hf, method, node, succ, out, value, e, false);
            }
         }

         val var23: IData = out.getValueData(actual, BuiltInModelT.MapValues);
         val var22: ObjectValues = var23 as? ObjectValues;
         if ((var23 as? ObjectValues) != null) {
            for (CompanionV e : mapValues.getValues()) {
               this.coverTaint(`$this$coverTaint`, hf, method, node, succ, out, value, e, false);
            }
         }
      }
   }

   public fun ICoverageCollector.coverTaint(hf: AbstractHeapFactory<IValue>, method: SootMethod, node: Unit, succ: Unit, out: IFact<IValue>) {
      if (out.isValid()) {
         val var10000: java.util.List = node.getUseAndDefBoxes();
         var `$this$mapNotNullTo$iv`: java.lang.Iterable = var10000;
         val `$this$forEach$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var10000, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `$this$forEach$iv$iv`.add((`element$iv$iv` as ValueBox).getValue());
         }

         `$this$mapNotNullTo$iv` = `$this$forEach$iv$iv` as java.util.List;
         val var24: java.util.Collection = new LinkedHashSet();

         for (Object element$iv$iv : $this$map$iv) {
            val it: Value = var31 as Value;
            val var33: AP.Companion = AP.Companion;
            val var34: AP = var33.get(it);
            if (var34 != null) {
               var24.add(var34);
            }
         }

         for (AP accessPath : (java.util.Set)var24) {
            if (var25.getField() == null) {
               for (CompanionV obj : ((PointsToGraph)out).getTargets(accessPath.getValue())) {
                  this.coverTaint(`$this$coverTaint`, hf, method, node, succ, out, var25.getValue(), var29, true);
               }
            }
         }
      }
   }

   public suspend fun runAnalysisInScene(
      locator: ProjectFileLocator,
      info: SootInfoCache,
      soot: SootCtx,
      preAnalysisResult: IPreAnalysisResultCollector,
      result: IIPAnalysisResultCollector,
      coverage: ICoverageCollector? = ...,
      entries: Collection<SootMethod>,
      methodsMustAnalyze: Collection<SootMethod>,
      missWrapper: IMissingSummaryReporter
   ) {
      var `$continuation`: Continuation;
      label211: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label211;
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
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.runAnalysisInScene(null, null, null, null, null, null, null, null, null, this as Continuation<? super kotlin.Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var33: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var analysis: <unrepresentable>;
      var checker: CheckerModeling;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            val vg: IVGlobal = new IVGlobal(null, 1, null);
            analysis = new InterProceduralValueAnalysis(vg, info, result, coverage, this) {
               {
                  super(`$vg`, null, null, 6, null);
                  this.$info = `$info`;
                  this.$result = `$result`;
                  this.$coverage = `$coverage`;
                  this.this$0 = `$receiver`;
               }

               public AIContext makeContext(SootMethod method, DirectedGraph<Unit> cfg, IFact<IValue> entryValue, boolean reverse, boolean isAnalyzable) {
                  val var10000: AbstractHeapFactory = this.getHf();
                  var var10001: java.util.List = cfg.getHeads();
                  var10001 = (java.util.List)CollectionsKt.first(var10001);
                  val env: HeapValuesEnv = var10000.env(var10001 as Unit);
                  val var14: AIContext;
                  if (entryValue.isValid()) {
                     val var7: AIContext = new AIContext(this.$info, this.getIcfg(), this.$result, method, cfg, reverse, isAnalyzable);
                     val entryBuilder: IFact.Builder = entryValue.builder();
                     val kill: KillEntry = new KillEntry(method, env);
                     (entryBuilder as PointsToGraphBuilder).apply(kill.getFactory());
                     var7.setEntries(kill.getEntries());
                     var7.setEntryValue((entryBuilder as PointsToGraphBuilder).build());
                     var14 = var7;
                  } else {
                     val var12: AIContext = new AIContext(this.$info, this.getIcfg(), this.$result, method, cfg, reverse, isAnalyzable);
                     var12.setEntries(SetsKt.emptySet());
                     var12.setEntryValue(entryValue);
                     var14 = var12;
                  }

                  return var14;
               }

               public Object normalFlowFunction(
                  AIContext context,
                  Unit node,
                  Unit succ,
                  IFact<IValue> inValue,
                  AtomicBoolean isNegativeBranch,
                  Continuation<? super IFact<IValue>> $completion
               ) {
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
                        Object L$2;
                        Object L$3;
                        int label;

                        {
                           super(`$completionx`);
                           this.this$0 = `this$0`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           this.result = `$result`;
                           this.label |= Integer.MIN_VALUE;
                           return this.this$0.normalFlowFunction(null, null, null, null, null, this as Continuation<? super IFact<IValue>>);
                        }
                     };
                  }

                  val `$result`: Any = `$continuation`.result;
                  val var10: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var var10000: Any;
                  switch ($continuation.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        if (this.$coverage != null) {
                           this.$coverage.cover(new CoverInst(context.getMethod(), node));
                        }

                        val var10001: Context = context;
                        `$continuation`.L$0 = this;
                        `$continuation`.L$1 = context;
                        `$continuation`.L$2 = node;
                        `$continuation`.L$3 = succ;
                        `$continuation`.label = 1;
                        var10000 = super.normalFlowFunction(var10001, node, succ, inValue, isNegativeBranch, `$continuation`);
                        if (var10000 === var10) {
                           return var10;
                        }
                        break;
                     case 1:
                        succ = `$continuation`.L$3 as Unit;
                        node = `$continuation`.L$2 as Unit;
                        context = `$continuation`.L$1 as AIContext;
                        this = `$continuation`.L$0 as <unrepresentable>;
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = `$result`;
                        break;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  val outValue: IFact = var10000 as IFact;
                  if (this.$coverage != null && this.$coverage.getEnableCoveredTaint()) {
                     this.this$0.coverTaint(this.$coverage, this.getHf(), context.getMethod(), node, succ, outValue);
                  }

                  return outValue;
               }
            };
            analysis.setDataFlowInterProceduralCalleeTimeOut(ExtSettings.INSTANCE.getDataFlowInterProceduralCalleeTimeOut());
            analysis.setDataFlowInterProceduralCalleeDepChainMaxNum(ExtSettings.INSTANCE.getDataFlowInterProceduralCalleeDepChainMaxNum());
            analysis.setDirectedGraph(this.directedGraph);
            analysis.setNumberThreads(this.mainConfig.getParallelsNum());
            analysis.setStaticFieldTrackingMode(this.mainConfig.getStaticFieldTrackingMode());
            val apponly: Boolean = this.mainConfig.getApponly();
            analysis.setAnalyzeLibraryClasses(!apponly);
            analysis.setNeedAnalyze(IPAnalysisEngine::runAnalysisInScene$lambda$2);
            val var10002: MainConfig = this.mainConfig;
            val var10003: IProjectFileLocator = locator;
            val var10004: CallGraph = soot.getSootMethodCallGraph();
            val var34: Scene = Scene.v();
            val preAnalysis: PreAnalysisImpl = new PreAnalysisImpl(var10002, var10003, var10004, info, preAnalysisResult, var34);
            checker = new CheckerModeling(this.mainConfig, analysis.getIcfg(), preAnalysis);
            val var10000: AIAnalysisBaseImpl = checker;
            val var10001: PreAnalysisCoroutineScope = preAnalysis;
            `$continuation`.L$0 = this;
            `$continuation`.L$1 = result;
            `$continuation`.L$2 = entries;
            `$continuation`.L$3 = methodsMustAnalyze;
            `$continuation`.L$4 = missWrapper;
            `$continuation`.L$5 = analysis;
            `$continuation`.L$6 = checker;
            `$continuation`.label = 1;
            if (PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(var10000, var10001, `$continuation`) === var33) {
               return var33;
            }
            break;
         case 1:
            checker = `$continuation`.L$6 as CheckerModeling;
            analysis = `$continuation`.L$5 as <unrepresentable>;
            missWrapper = `$continuation`.L$4 as IMissingSummaryReporter;
            methodsMustAnalyze = `$continuation`.L$3 as java.util.Collection;
            entries = `$continuation`.L$2 as java.util.Collection;
            result = `$continuation`.L$1 as IIPAnalysisResultCollector;
            this = `$continuation`.L$0 as IPAnalysisEngine;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      val var35: java.lang.Iterable;
      for (Object element$iv : var35) {
         analysis.getSummaries().add(`element$iv` as SummaryHandlePackage<IValue>);
      }

      CollectionsKt.plus(this.summaries, kotlin.Unit.INSTANCE);
      UsefulMetrics.Companion.getMetrics().setWarningThreshold(this.mainConfig.getMemoryThreshold());
      val var36: java.util.Collection = this.mainConfig.InterProceduralIncrementalAnalysisFilter(entries);
      val var37: java.util.Collection = this.mainConfig.InterProceduralIncrementalAnalysisFilter(methodsMustAnalyze);
      System.gc();
      analysis.doAnalysis(var36, var37);
      result.afterAnalyze(analysis);
      val var113: IMonitor = this.mainConfig.getMonitor();
      val var38: ProjectMetrics = if (var113 != null) var113.getProjectMetrics() else null;
      if (var38 != null) {
         val var114: java.util.Set = var38.getAnalyzedMethodEntries();
         if (var114 != null) {
            val var39: java.util.Collection = var114;
            val var43: java.lang.Iterable = var36;
            val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var36, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add((`item$iv$iv` as SootMethod).getSignature());
            }

            CollectionsKt.addAll(var39, `destination$iv$iv` as java.util.List);
         }
      }

      if (var38 != null) {
         val var115: java.util.Set = var38.getAnalyzedApplicationMethods();
         if (var115 != null) {
            val var40: java.util.Collection = var115;
            var var45: java.lang.Iterable = analysis.getReachableMethods() as java.lang.Iterable;
            var var64: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filter$iv) {
               val var116: SootClass = (var88 as SootMethod).getDeclaringClass();
               if (var116 != null && var116.isApplicationClass()) {
                  var64.add(var88);
               }
            }

            var45 = var64 as java.util.List;
            var64 = new ArrayList();

            for (Object element$iv$ivx : $this$filter$iv) {
               val var97: SootMethod = `element$iv$ivx` as SootMethod;
               val var117: SootClass = (`element$iv$ivx` as SootMethod).getDeclaringClass();
               if (!OthersKt.isSyntheticComponent(var117) && !var97.isAbstract()) {
                  var64.add(`element$iv$ivx`);
               }
            }

            var45 = var64 as java.util.List;
            var64 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var64 as java.util.List, 10));

            for (Object item$iv$iv : $this$filter$iv) {
               var64.add((var90 as SootMethod).getSignature());
            }

            CollectionsKt.addAll(var40, var64 as java.util.List);
         }
      }

      if (var38 != null) {
         val var118: java.util.Set = var38.getAnalyzedLibraryMethods();
         if (var118 != null) {
            val var41: java.util.Collection = var118;
            var var49: java.lang.Iterable = analysis.getReachableMethods() as java.lang.Iterable;
            var var67: java.util.Collection = new ArrayList();

            for (Object element$iv$ivxx : $this$filter$iv) {
               val var119: SootClass = (`element$iv$ivxx` as SootMethod).getDeclaringClass();
               if (var119 != null && var119.isLibraryClass()) {
                  var67.add(`element$iv$ivxx`);
               }
            }

            var49 = var67 as java.util.List;
            var67 = new ArrayList();

            for (Object element$iv$ivxxx : $this$filter$iv) {
               val var100: SootMethod = `element$iv$ivxxx` as SootMethod;
               val var120: SootClass = (`element$iv$ivxxx` as SootMethod).getDeclaringClass();
               if (!OthersKt.isSyntheticComponent(var120) && !var100.isAbstract()) {
                  var67.add(`element$iv$ivxxx`);
               }
            }

            var49 = var67 as java.util.List;
            var67 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var67 as java.util.List, 10));

            for (Object item$iv$iv : $this$filter$iv) {
               var67.add((var93 as SootMethod).getSignature());
            }

            CollectionsKt.addAll(var41, var67 as java.util.List);
         }
      }

      if (var38 != null) {
         val var121: java.util.Set = var38.getAnalyzedClasses();
         if (var121 != null) {
            val var42: java.util.Collection = var121;
            var var53: java.lang.Iterable = analysis.getReachableMethods() as java.lang.Iterable;
            var var70: java.util.Collection = new ArrayList();

            for (Object element$iv$ivxxxx : $this$filter$iv) {
               val var102: SootMethod = `element$iv$ivxxxx` as SootMethod;
               val var122: SootClass = (`element$iv$ivxxxx` as SootMethod).getDeclaringClass();
               if (!OthersKt.isSyntheticComponent(var122) && !var102.isAbstract()) {
                  var70.add(`element$iv$ivxxxx`);
               }
            }

            var53 = var70 as java.util.List;
            var70 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var70 as java.util.List, 10));

            for (Object item$iv$iv : $this$filter$iv) {
               var70.add((var95 as SootMethod).getSignature());
            }

            CollectionsKt.addAll(var42, var70 as java.util.List);
         }
      }

      analysis.getCallBackManager().reportMissSummaryMethod(IPAnalysisEngine::runAnalysisInScene$lambda$13);
      return kotlin.Unit.INSTANCE;
   }

   public suspend fun analyze(
      locator: ProjectFileLocator,
      info: SootInfoCache,
      soot: SootCtx,
      provider: IEntryPointProvider,
      result: ResultCollector,
      missWrapper: IMissingSummaryReporter
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.code.cfg.ExceptionRangeCFG.isCircular()" because "range" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.graphToStatement(DomHelper.java:84)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:203)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:157)
      //
      // Bytecode:
      // 000: aload 7
      // 002: instanceof cn/sast/framework/engine/IPAnalysisEngine$analyze$1
      // 005: ifeq 02b
      // 008: aload 7
      // 00a: checkcast cn/sast/framework/engine/IPAnalysisEngine$analyze$1
      // 00d: astore 19
      // 00f: aload 19
      // 011: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.label I
      // 014: ldc_w -2147483648
      // 017: iand
      // 018: ifeq 02b
      // 01b: aload 19
      // 01d: dup
      // 01e: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.label I
      // 021: ldc_w -2147483648
      // 024: isub
      // 025: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.label I
      // 028: goto 037
      // 02b: new cn/sast/framework/engine/IPAnalysisEngine$analyze$1
      // 02e: dup
      // 02f: aload 0
      // 030: aload 7
      // 032: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$1.<init> (Lcn/sast/framework/engine/IPAnalysisEngine;Lkotlin/coroutines/Continuation;)V
      // 035: astore 19
      // 037: aload 19
      // 039: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.result Ljava/lang/Object;
      // 03c: astore 18
      // 03e: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 041: astore 21
      // 043: aload 19
      // 045: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.label I
      // 048: tableswitch 470 0 1 24 190
      // 060: aload 18
      // 062: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 065: getstatic cn/sast/framework/engine/IPAnalysisEngine.logger Lmu/KLogger;
      // 068: invokestatic org/utbot/common/LoggingKt.info (Lmu/KLogger;)Lorg/utbot/common/LoggerWithLogMethod;
      // 06b: astore 8
      // 06d: ldc_w "Run inter-procedural data flow analysis analysis"
      // 070: astore 9
      // 072: bipush 0
      // 073: istore 10
      // 075: aload 8
      // 077: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 07a: new cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$1
      // 07d: dup
      // 07e: aload 9
      // 080: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$1.<init> (Ljava/lang/String;)V
      // 083: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 088: pop
      // 089: invokestatic java/time/LocalDateTime.now ()Ljava/time/LocalDateTime;
      // 08c: astore 11
      // 08e: bipush 0
      // 08f: istore 12
      // 091: new kotlin/jvm/internal/Ref$ObjectRef
      // 094: dup
      // 095: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
      // 098: astore 13
      // 09a: aload 13
      // 09c: getstatic org/utbot/common/Maybe.Companion Lorg/utbot/common/Maybe$Companion;
      // 09f: invokevirtual org/utbot/common/Maybe$Companion.empty ()Lorg/utbot/common/Maybe;
      // 0a2: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 0a5: nop
      // 0a6: aload 13
      // 0a8: astore 17
      // 0aa: bipush 0
      // 0ab: istore 14
      // 0ad: aload 4
      // 0af: invokeinterface cn/sast/framework/entries/IEntryPointProvider.getIterator ()Lkotlinx/coroutines/flow/Flow; 1
      // 0b4: new cn/sast/framework/engine/IPAnalysisEngine$analyze$2$1
      // 0b7: dup
      // 0b8: aload 3
      // 0b9: aload 0
      // 0ba: aload 1
      // 0bb: aload 2
      // 0bc: aload 5
      // 0be: aload 6
      // 0c0: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$2$1.<init> (Lcn/sast/framework/SootCtx;Lcn/sast/framework/engine/IPAnalysisEngine;Lcn/sast/framework/report/ProjectFileLocator;Lcom/feysh/corax/cache/analysis/SootInfoCache;Lcn/sast/framework/result/ResultCollector;Lcn/sast/framework/result/IMissingSummaryReporter;)V
      // 0c3: checkcast kotlinx/coroutines/flow/FlowCollector
      // 0c6: aload 19
      // 0c8: aload 19
      // 0ca: aload 8
      // 0cc: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$0 Ljava/lang/Object;
      // 0cf: aload 19
      // 0d1: aload 9
      // 0d3: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$1 Ljava/lang/Object;
      // 0d6: aload 19
      // 0d8: aload 11
      // 0da: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$2 Ljava/lang/Object;
      // 0dd: aload 19
      // 0df: aload 13
      // 0e1: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$3 Ljava/lang/Object;
      // 0e4: aload 19
      // 0e6: aload 17
      // 0e8: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$4 Ljava/lang/Object;
      // 0eb: aload 19
      // 0ed: iload 12
      // 0ef: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.I$0 I
      // 0f2: aload 19
      // 0f4: bipush 1
      // 0f5: putfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.label I
      // 0f8: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 0fd: dup
      // 0fe: aload 21
      // 100: if_acmpne 14d
      // 103: aload 21
      // 105: areturn
      // 106: bipush 0
      // 107: istore 10
      // 109: bipush 0
      // 10a: istore 14
      // 10c: aload 19
      // 10e: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.I$0 I
      // 111: istore 12
      // 113: aload 19
      // 115: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$4 Ljava/lang/Object;
      // 118: checkcast kotlin/jvm/internal/Ref$ObjectRef
      // 11b: astore 17
      // 11d: aload 19
      // 11f: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$3 Ljava/lang/Object;
      // 122: checkcast kotlin/jvm/internal/Ref$ObjectRef
      // 125: astore 13
      // 127: aload 19
      // 129: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$2 Ljava/lang/Object;
      // 12c: checkcast java/time/LocalDateTime
      // 12f: astore 11
      // 131: aload 19
      // 133: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$1 Ljava/lang/Object;
      // 136: checkcast java/lang/String
      // 139: astore 9
      // 13b: aload 19
      // 13d: getfield cn/sast/framework/engine/IPAnalysisEngine$analyze$1.L$0 Ljava/lang/Object;
      // 140: checkcast org/utbot/common/LoggerWithLogMethod
      // 143: astore 8
      // 145: nop
      // 146: aload 18
      // 148: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 14b: aload 18
      // 14d: pop
      // 14e: aload 17
      // 150: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 153: astore 20
      // 155: new org/utbot/common/Maybe
      // 158: dup
      // 159: aload 20
      // 15b: invokespecial org/utbot/common/Maybe.<init> (Ljava/lang/Object;)V
      // 15e: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 161: aload 13
      // 163: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 166: checkcast org/utbot/common/Maybe
      // 169: invokevirtual org/utbot/common/Maybe.getOrThrow ()Ljava/lang/Object;
      // 16c: astore 15
      // 16e: nop
      // 16f: aload 13
      // 171: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 174: checkcast org/utbot/common/Maybe
      // 177: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
      // 17a: ifeq 198
      // 17d: aload 8
      // 17f: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 182: new cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$2
      // 185: dup
      // 186: aload 11
      // 188: aload 9
      // 18a: aload 13
      // 18c: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$2.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
      // 18f: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 194: pop
      // 195: goto 1ae
      // 198: aload 8
      // 19a: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 19d: new cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$3
      // 1a0: dup
      // 1a1: aload 11
      // 1a3: aload 9
      // 1a5: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$3.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
      // 1a8: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 1ad: pop
      // 1ae: goto 21a
      // 1b1: astore 16
      // 1b3: aload 8
      // 1b5: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 1b8: new cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$4
      // 1bb: dup
      // 1bc: aload 11
      // 1be: aload 9
      // 1c0: aload 16
      // 1c2: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$4.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Ljava/lang/Throwable;)V
      // 1c5: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 1ca: pop
      // 1cb: bipush 1
      // 1cc: istore 12
      // 1ce: aload 16
      // 1d0: athrow
      // 1d1: astore 16
      // 1d3: iload 12
      // 1d5: ifne 217
      // 1d8: aload 13
      // 1da: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 1dd: checkcast org/utbot/common/Maybe
      // 1e0: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
      // 1e3: ifeq 201
      // 1e6: aload 8
      // 1e8: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 1eb: new cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$5
      // 1ee: dup
      // 1ef: aload 11
      // 1f1: aload 9
      // 1f3: aload 13
      // 1f5: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$5.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
      // 1f8: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 1fd: pop
      // 1fe: goto 217
      // 201: aload 8
      // 203: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 206: new cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$6
      // 209: dup
      // 20a: aload 11
      // 20c: aload 9
      // 20e: invokespecial cn/sast/framework/engine/IPAnalysisEngine$analyze$$inlined$bracket$default$6.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
      // 211: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 216: pop
      // 217: aload 16
      // 219: athrow
      // 21a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 21d: areturn
      // 21e: new java/lang/IllegalStateException
      // 221: dup
      // 222: ldc_w "call to 'resume' before 'invoke' with coroutine"
      // 225: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 228: athrow
   }

   private fun dumpJson(output: IResDirectory, name: String) {
      label27: {
         val cgJson: IResource = output.resolve(name);
         val var10000: Path = cgJson.getPath();
         val var10001: Array<OpenOption> = new OpenOption[0];
         val var18: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var10001, var10001.length));
         val var4: Closeable = var18;
         var var5: java.lang.Throwable = null;

         try {
            try {
               val out: OutputStream = var4 as OutputStream;

               try {
                  val e: Json = IPAnalysisEngineKt.getGraphJson();
                  val `value$iv`: Any = this.directedGraph;
                  JvmStreamsKt.encodeToStream(
                     e,
                     HashMutableDirectedGraph.Companion.serializer(SerializersKt.noCompiledSerializer(e.getSerializersModule(), SootMethod::class)) as SerializationStrategy,
                     `value$iv`,
                     out
                  );
                  logger.info(IPAnalysisEngine::dumpJson$lambda$16$lambda$15);
               } catch (var11: Exception) {
                  logger.error("failed to encodeToStream jsonGraph", var11);
               }
            } catch (var12: java.lang.Throwable) {
               var5 = var12;
               throw var12;
            }
         } catch (var13: java.lang.Throwable) {
            CloseableKt.closeFinally(var4, var5);
         }

         CloseableKt.closeFinally(var4, null);
      }
   }

   private fun dumpDot(output: IResDirectory, name: String) {
      val cg: IResFile = output.resolve(name).toFile();
      val plot: <unrepresentable> = new GraphPlot<SootClass, SootMethod>(this.directedGraph) {
         {
            super(`$super_call_param$1` as DirectedGraph<SootMethod>);
         }

         public SootClass getNodeContainer(SootMethod $this$getNodeContainer) {
            val var10000: SootClass = `$this$getNodeContainer`.getDeclaringClass();
            return var10000;
         }
      };

      try {
         GraphPlotKt.dump(GraphPlot.plot$default(plot, null, 1, null), cg);
         logger.info(IPAnalysisEngine::dumpDot$lambda$17);
      } catch (var6: Exception) {
         logger.error("failed to render dotGraph", var6);
      }
   }

   public fun dump(output: IResDirectory) {
      this.dumpDot(output, "forward_interprocedural_callgraph.dot");
      this.dumpJson(output, "forward_interprocedural_callgraph.json");
      val var10000: java.util.Iterator = Scene.v().getCallGraph().iterator();
      val var3: java.util.Iterator = var10000;

      while (var3.hasNext()) {
         val edge: Edge = var3.next() as Edge;
         val var7: SootMethod = edge.src();
         if (var7 != null) {
            val var8: SootMethod = edge.tgt();
            if (var8 != null) {
               this.directedGraph.addEdge(var7, var8);
            }
         }
      }

      this.dumpJson(output, "forward_interprocedural_callgraph_complete.json");
      if (ExtSettings.INSTANCE.getDumpCompleteDotCg()) {
         this.dumpDot(output, "forward_interprocedural_callgraph_complete.dot");
      }
   }

   @JvmStatic
   fun `runAnalysisInScene$lambda$2`(`$apponly`: Boolean, `$locator`: ProjectFileLocator, it: SootMethod): Boolean {
      return !`$apponly` && it.getDeclaringClass().isLibraryClass() && `$locator`.get(ClassResInfo.Companion.of(it), NullWrapperFileGenerator.INSTANCE) != null;
   }

   @JvmStatic
   fun `runAnalysisInScene$lambda$13`(`$missWrapper`: IMissingSummaryReporter, miss: SootMethod): kotlin.Unit {
      `$missWrapper`.reportMissingMethod(miss);
      return kotlin.Unit.INSTANCE;
   }

   @JvmStatic
   fun `dumpJson$lambda$16$lambda$15`(`$cgJson`: IResource): Any {
      return "json call graph: $`$cgJson`";
   }

   @JvmStatic
   fun `dumpDot$lambda$17`(`$cg`: IResFile): Any {
      return "dot call graph: $`$cg`";
   }

   @JvmStatic
   fun `logger$lambda$18`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
