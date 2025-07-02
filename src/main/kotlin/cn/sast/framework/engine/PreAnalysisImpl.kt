package cn.sast.framework.engine

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.SaConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.incremental.IncrementalAnalyze
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.FileResInfo
import cn.sast.api.util.IMonitor
import cn.sast.api.util.Kotlin_extKt
import cn.sast.api.util.OthersKt
import cn.sast.api.util.SootUtilsKt
import cn.sast.api.util.Timer
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.framework.util.SafeAnalyzeUtil
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.commons.NullableLateinit
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.ICheckPoint
import com.feysh.corax.config.api.IClassCheckPoint
import com.feysh.corax.config.api.IClassMatch
import com.feysh.corax.config.api.IFieldCheckPoint
import com.feysh.corax.config.api.IFieldMatch
import com.feysh.corax.config.api.IInvokeCheckPoint
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.INodeWithRange
import com.feysh.corax.config.api.IPreAnalysisClassConfig
import com.feysh.corax.config.api.IPreAnalysisConfig
import com.feysh.corax.config.api.IPreAnalysisFieldConfig
import com.feysh.corax.config.api.IPreAnalysisFileConfig
import com.feysh.corax.config.api.IPreAnalysisInvokeConfig
import com.feysh.corax.config.api.IPreAnalysisMethodConfig
import com.feysh.corax.config.api.ISourceFileCheckPoint
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.PreAnalysisApi.Result
import com.feysh.corax.config.api.report.Region
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.IMatchTarget
import com.feysh.corax.config.api.utils.UtilsKt
import com.github.javaparser.Position
import com.github.javaparser.ast.nodeTypes.NodeWithRange
import io.vertx.core.impl.ConcurrentHashSet
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Intrinsics.Kotlin
import kotlin.reflect.KCallable
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.SemaphoreKt
import mu.KLogger
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.SootMethodRef
import soot.Type
import soot.Value
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.tagkit.Host
import soot.util.Chain
import soot.util.HashMultiMap
import soot.util.MultiMap

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/PreAnalysisImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,760:1\n774#2:761\n865#2,2:762\n1368#2:764\n1454#2,5:765\n1368#2:770\n1454#2,5:771\n774#2:776\n865#2,2:777\n1368#2:779\n1454#2,5:780\n1368#2:785\n1454#2,5:786\n1557#2:792\n1628#2,3:793\n1454#2,5:796\n1#3:791\n*S KotlinDebug\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/PreAnalysisImpl\n*L\n82#1:761\n82#1:762,2\n83#1:764\n83#1:765,5\n84#1:770\n84#1:771,5\n86#1:776\n86#1:777,2\n87#1:779\n87#1:780,5\n88#1:785\n88#1:786,5\n466#1:792\n466#1:793,3\n543#1:796,5\n*E\n"])
public class PreAnalysisImpl(mainConfig: MainConfig,
      locator: IProjectFileLocator,
      cg: CallGraph,
      info: SootInfoCache,
      resultCollector: IPreAnalysisResultCollector,
      scene: Scene
   ) :
   PreAnalysisCoroutineScope,
   IAnalysisDepends {
   public open val mainConfig: MainConfig
   public final val locator: IProjectFileLocator
   public open val cg: CallGraph
   public final val info: SootInfoCache
   private final val resultCollector: IPreAnalysisResultCollector
   public final val scene: Scene
   public final val analyzedClasses: MutableSet<SootClass>
   public final val analyzedSourceFiles: MutableSet<IResFile>

   public open val fastCache: FastCache
      public open get() {
         return FastCacheImpl.INSTANCE;
      }


   private final val scanFilter: ScanFilter
   private final val monitor: IMonitor?
   private final val allClasses: Set<SootClass>
   private final val allMethods: List<SootMethod>
   private final val allFields: List<SootField>
   private final val appOnlyClasses: List<SootClass>
   private final val appOnlyMethods: List<SootMethod>
   private final val appOnlyFields: List<SootField>
   private final val changeFileBasedIncAnalysis: IncrementalAnalyzeByChangeFiles?
   private final val dg: SimpleDeclAnalysisDependsGraph?
   private final val preAnalysisConfig: PreAnalysisConfig?
   private final val cancelAnalysisInErrorCount: Int
   private final val scopeLateInit: NullableLateinit<CoroutineScope>

   public open var scope: CoroutineScope
      public open get() {
         return this.scopeLateInit.getValue(this, $$delegatedProperties[0]);
      }

      public open set(<set-?>) {
         this.scopeLateInit.setValue(this, $$delegatedProperties[0], var1);
      }


   private final val globalNormalAnalyzeSemaphore: Semaphore
      private final get() {
         return this.globalNormalAnalyzeSemaphore$delegate.getValue() as Semaphore;
      }


   private final val globalResourceSemaphore: Semaphore
      private final get() {
         return this.globalResourceSemaphore$delegate.getValue() as Semaphore;
      }


   private final val filesWhichHitSizeThreshold: MutableSet<IResource>
   private final val maximumFileSizeThresholdWarnings: Int

   public open val outputPath: Path
      public open get() {
         return this.getMainConfig().getOutput_dir().getPath();
      }


   public open val fullCanonicalPathString: String
      public open get() {
         return Resource.INSTANCE.of(`$this$fullCanonicalPathString`).getAbsolute().getNormalize().toString();
      }


   private final val OrigAction: String

   private final val invokePointData: cn.sast.framework.engine.PreAnalysisImpl.InvokePointData
      private final get() {
         return this.invokePointData$delegate.getValue() as PreAnalysisImpl.InvokePointData;
      }


   init {
      this.$$delegate_0 = MainConfigKt.simpleIAnalysisDepends(mainConfig);
      this.mainConfig = mainConfig;
      this.locator = locator;
      this.cg = cg;
      this.info = info;
      this.resultCollector = resultCollector;
      this.scene = scene;
      this.analyzedClasses = (new ConcurrentHashSet()) as MutableSet<SootClass>;
      this.analyzedSourceFiles = (new ConcurrentHashSet()) as MutableSet<IResFile>;
      this.scanFilter = this.getMainConfig().getScanFilter();
      this.monitor = this.getMainConfig().getMonitor();
      val var10001: Chain = this.scene.getClasses();
      var `$this$flatMap$iv`: java.lang.Iterable = var10001 as java.lang.Iterable;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         var var10000: Boolean;
         label91: {
            val `list$iv$iv`: SootClass = `element$iv$iv` as SootClass;
            if (!this.scene.isExcluded(`element$iv$iv` as SootClass)) {
               if (!OthersKt.isSyntheticComponent(`list$iv$iv`)) {
                  var10000 = true;
                  break label91;
               }
            }

            var10000 = false;
         }

         if (var10000) {
            `destination$iv$iv`.add(`element$iv$iv`);
         }
      }

      this.allClasses = CollectionsKt.toSet(`destination$iv$iv` as java.util.List);
      `$this$flatMap$iv` = this.allClasses;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val var62: java.util.List = (var43 as SootClass).getMethods();
         CollectionsKt.addAll(`destination$iv$iv`, var62);
      }

      this.allMethods = `destination$iv$iv` as MutableList<SootMethod>;
      `$this$flatMap$iv` = this.allClasses;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val var63: Chain = (var44 as SootClass).getFields();
         CollectionsKt.addAll(`destination$iv$iv`, var63 as java.lang.Iterable);
      }

      this.allFields = `destination$iv$iv` as MutableList<SootField>;
      `$this$flatMap$iv` = this.allClasses;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         if ((var45 as SootClass).isApplicationClass()) {
            `destination$iv$iv`.add(var45);
         }
      }

      this.appOnlyClasses = `destination$iv$iv` as MutableList<SootClass>;
      `$this$flatMap$iv` = this.appOnlyClasses;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivx : $this$flatMap$iv) {
         val var64: java.util.List = (`element$iv$ivx` as SootClass).getMethods();
         CollectionsKt.addAll(`destination$iv$iv`, var64);
      }

      this.appOnlyMethods = `destination$iv$iv` as MutableList<SootMethod>;
      `$this$flatMap$iv` = this.appOnlyClasses;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$ivx : $this$flatMap$iv) {
         val var65: Chain = (`element$iv$ivx` as SootClass).getFields();
         CollectionsKt.addAll(`destination$iv$iv`, var65 as java.lang.Iterable);
      }

      this.appOnlyFields = `destination$iv$iv` as MutableList<SootField>;
      val var22: IncrementalAnalyze = this.getMainConfig().getIncrementAnalyze();
      this.changeFileBasedIncAnalysis = var22 as? IncrementalAnalyzeByChangeFiles;
      this.dg = if (this.changeFileBasedIncAnalysis != null) this.changeFileBasedIncAnalysis.getSimpleDeclAnalysisDependsGraph() else null;
      val var66: SaConfig = this.getMainConfig().getSaConfig();
      this.preAnalysisConfig = if (var66 != null) var66.getPreAnalysisConfig() else null;
      this.cancelAnalysisInErrorCount = if (this.preAnalysisConfig != null) this.preAnalysisConfig.getCancelAnalysisInErrorCount() else 10;
      this.scopeLateInit = new NullableLateinit<>("scope is not initialized yet");
      this.globalNormalAnalyzeSemaphore$delegate = LazyKt.lazy(PreAnalysisImpl::globalNormalAnalyzeSemaphore_delegate$lambda$6);
      this.globalResourceSemaphore$delegate = LazyKt.lazy(PreAnalysisImpl::globalResourceSemaphore_delegate$lambda$7);
      this.filesWhichHitSizeThreshold = Kotlin_extKt.concurrentHashSetOf();
      this.maximumFileSizeThresholdWarnings = if (this.preAnalysisConfig != null) this.preAnalysisConfig.getMaximumFileSizeThresholdWarnings() else 20;
      this.OrigAction = "PreAnalysis: Process";
      this.invokePointData$delegate = LazyKt.lazy(PreAnalysisImpl::invokePointData_delegate$lambda$18);
   }

   @JvmStatic
   fun `getScope$delegate`(var0: PreAnalysisImpl): Any {
      return var0.scopeLateInit;
   }

   public override fun uninitializedScope() {
      this.scopeLateInit.uninitialized();
   }

   private fun createNormalAnalyzeSemaphore(permit: Int = this.getMainConfig().getParallelsNum() * 2): Semaphore {
      return SemaphoreKt.Semaphore$default(permit, 0, 2, null);
   }

   private fun createResourceSemaphore(permit: Int = if (this.preAnalysisConfig != null) this.preAnalysisConfig.getLargeFileSemaphorePermits() else 3): Semaphore {
      return this.createNormalAnalyzeSemaphore(permit);
   }

   public override fun <T> Result<T?>.nonNull(): Result<T> {
      return new PreAnalysisApi.Result<T>(this, `$this$nonNull`) {
         private final Deferred<java.util.List<T>> asyncResult;

         {
            this.asyncResult = BuildersKt.async$default(
               `$receiver`.getScope(),
               null,
               null,
               (new Function2<CoroutineScope, Continuation<? super java.util.List<? extends T>>, Object>(`$receiver`, null) {
                  int label;

                  {
                     super(2, `$completionx`);
                     this.$this_nonNull = `$receiver`;
                  }

                  public final Object invokeSuspend(Object $result) {
                     val var17: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     var var10000: Any;
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           var10000 = this.$this_nonNull;
                           val var10001: Continuation = this as Continuation;
                           this.label = 1;
                           var10000 = (PreAnalysisApi.Result)var10000.await(var10001);
                           if (var10000 === var17) {
                              return var17;
                           }
                           break;
                        case 1:
                           ResultKt.throwOnFailure(`$result`);
                           var10000 = (PreAnalysisApi.Result)`$result`;
                           break;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }

                     val `$this$mapNotNull$iv`: java.lang.Iterable = var10000 as java.lang.Iterable;
                     val `destination$iv$iv`: java.util.Collection = new ArrayList();

                     for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
                        if (`element$iv$iv$iv` != null) {
                           `destination$iv$iv`.add(`element$iv$iv$iv`);
                        }
                     }

                     return `destination$iv$iv` as java.util.List;
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     return (new <anonymous constructor>(this.$this_nonNull, `$completion`)) as Continuation<Unit>;
                  }

                  public final Object invoke(CoroutineScope p1, Continuation<? super java.util.List<? extends T>> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }
               }) as Function2,
               3,
               null
            );
         }

         @Override
         public Deferred<java.util.List<T>> getAsyncResult() {
            return this.asyncResult;
         }

         @Override
         public Object await(Continuation<? super java.util.List<? extends T>> $completion) {
            return PreAnalysisApi.Result.DefaultImpls.await(this, `$completion`);
         }
      };
   }

   private fun getPhaseTimer(unit: CheckerUnit, apiName: String): Timer? {
      return if (this.monitor != null) this.monitor.timer("PreAnalysis:$apiName.At:${UtilsKt.getSootTypeName(unit.getClass())}") else null;
   }

   context(CheckerUnit)
   public open fun <T> atClass(classes: Collection<SootClass>, config: (IPreAnalysisClassConfig) -> Unit, block: (IClassCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      val t: Timer = this.getPhaseTimer(`$context_receiver_0`, "atClass");
      val var7: PreAnalysisClassConfig = new PreAnalysisClassConfig(false, false, 3, null);
      config.invoke(var7);
      val conf: PreAnalysisClassConfig = var7;
      return new PreAnalysisApi.Result<T>(this, t, classes, conf, block)// $VF: Couldn't be decompiled
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
;
   }

   context(CheckerUnit)
   public open fun <T> atMethod(
      methods: Collection<SootMethod>,
      config: (IPreAnalysisMethodConfig) -> Unit,
      block: (IMethodCheckPoint, Continuation<T>) -> Any?
   ): Result<T> {
      val t: Timer = this.getPhaseTimer(`$context_receiver_0`, "atMethod");
      val var7: PreAnalysisMethodConfig = new PreAnalysisMethodConfig(false, false, 3, null);
      config.invoke(var7);
      val conf: PreAnalysisMethodConfig = var7;
      return new PreAnalysisApi.Result<T>(this, t, methods, conf, block)// $VF: Couldn't be decompiled
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
;
   }

   context(CheckerUnit)
   public open fun <T> atField(fields: Collection<SootField>, config: (IPreAnalysisFieldConfig) -> Unit, block: (IFieldCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      val t: Timer = this.getPhaseTimer(`$context_receiver_0`, "atField");
      val var7: PreAnalysisFieldConfig = new PreAnalysisFieldConfig(false, false, 3, null);
      config.invoke(var7);
      val conf: PreAnalysisFieldConfig = var7;
      return new PreAnalysisApi.Result<T>(this, t, fields, conf, block)// $VF: Couldn't be decompiled
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
;
   }

   context(CheckerUnit)
   public open fun <T> atInvoke(targets: List<SootMethod>?, config: (IPreAnalysisInvokeConfig) -> Unit, block: (IInvokeCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      val t: Timer = this.getPhaseTimer(`$context_receiver_0`, "atInvoke");
      val var7: PreAnalysisInvokeConfig = new PreAnalysisInvokeConfig(false, false, 3, null);
      config.invoke(var7);
      val conf: PreAnalysisInvokeConfig = var7;
      return new PreAnalysisApi.Result<T>(this, t, targets, conf, block)// $VF: Couldn't be decompiled
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
;
   }

   public fun chooseSemaphore(fileSize: Long): Semaphore {
      var var6: Int;
      label21: {
         val var10000: SaConfig = this.getMainConfig().getSaConfig();
         if (var10000 != null) {
            val var5: PreAnalysisConfig = var10000.getPreAnalysisConfig();
            if (var5 != null) {
               var6 = var5.getLargeFileSize();
               break label21;
            }
         }

         var6 = null;
      }

      return if (var6 != null && fileSize > var6.intValue()) this.getGlobalResourceSemaphore() else this.getGlobalNormalAnalyzeSemaphore();
   }

   context(CheckerUnit)
   public open fun <T> atSourceFile(
      files: (Continuation<Iterator<IResFile>>) -> Any?,
      config: (IPreAnalysisFileConfig) -> Unit,
      block: (ISourceFileCheckPoint, Continuation<T>) -> Any?
   ): Result<T> {
      val t: Timer = this.getPhaseTimer(`$context_receiver_0`, "atSourceFile");
      val var7: PreAnalysisFileConfig = new PreAnalysisFileConfig(false, false, 3, null);
      config.invoke(var7);
      val conf: PreAnalysisFileConfig = var7;
      return new PreAnalysisApi.Result<T>(this, files, conf, t, block) {
         private final SafeAnalyzeUtil safeAnalyzeUtil;
         private final Deferred<java.util.List<T>> asyncResult;

         {
            this.safeAnalyzeUtil = new SafeAnalyzeUtil(PreAnalysisImpl.access$getCancelAnalysisInErrorCount$p(`$receiver`), 0, 2, null);
            this.asyncResult = BuildersKt.async$default(
               `$receiver`.getScope(),
               null,
               null,
               (
                  new Function2<CoroutineScope, Continuation<? super java.util.List<? extends T>>, Object>(
                     `$files`, `$receiver`, `$conf`, `$t`, this, `$block`, null
                  ) {
                     Object L$1;
                     int label;

                     {
                        super(2, `$completionx`);
                        this.$files = `$files`;
                        this.this$0 = `$receiverx`;
                        this.$conf = `$confx`;
                        this.$t = `$tx`;
                        this.this$1 = `$receiverxx`;
                        this.$block = `$blockx`;
                     }

                     public final Object invokeSuspend(Object $result) {
                        val var10: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        var `$this$async`: CoroutineScope;
                        var r: java.util.List;
                        var var13: Any;
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              `$this$async` = this.L$0 as CoroutineScope;
                              r = new ArrayList();
                              var13 = this.$files;
                              this.L$0 = `$this$async`;
                              this.L$1 = r;
                              this.label = 1;
                              var13 = (Function1)var13.invoke(this);
                              if (var13 === var10) {
                                 return var10;
                              }
                              break;
                           case 1:
                              r = this.L$1 as java.util.List;
                              `$this$async` = this.L$0 as CoroutineScope;
                              ResultKt.throwOnFailure(`$result`);
                              var13 = (Function1)`$result`;
                              break;
                           case 2:
                              ResultKt.throwOnFailure(`$result`);
                              return CollectionsKt.filterNotNull(`$result` as java.lang.Iterable);
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        val var4: java.util.Iterator = var13 as java.util.Iterator;

                        while (var4.hasNext()) {
                           val file: IResFile = var4.next() as IResFile;
                           if (!CoroutineScopeKt.isActive(`$this$async`)) {
                              break;
                           }

                           if (file.isFile() && file.getExists() && !PreAnalysisImpl.access$skip(this.this$0, this.$conf, file.getPath())) {
                              if (this.$conf.getIncrementalAnalyze()) {
                                 val var15: IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph = PreAnalysisImpl.access$getDg$p(this.this$0);
                                 if ((if (var15 != null) var15.shouldReAnalyzeTarget(file) else null) === ProcessRule.ScanAction.Skip) {
                                    continue;
                                 }
                              }

                              if (!this.$conf.getSkipFilesInArchive() || !MainConfigKt.skipResourceInArchive(this.this$0.getMainConfig(), file)) {
                                 val fileSize: Long = Files.size(file.getPath());
                                 val var16: PreAnalysisConfig = PreAnalysisImpl.access$getPreAnalysisConfig$p(this.this$0);
                                 if (var16 != null && var16.fileSizeThresholdExceeded(file.getExtension(), fileSize)) {
                                    if (PreAnalysisImpl.access$getFilesWhichHitSizeThreshold$p(this.this$0).add(file)) {
                                       val async: Function0 = <unrepresentable>::invokeSuspend$lambda$0;
                                       if (PreAnalysisImpl.access$getFilesWhichHitSizeThreshold$p(this.this$0).size()
                                          < PreAnalysisImpl.access$getMaximumFileSizeThresholdWarnings$p(this.this$0)) {
                                          PreAnalysisImpl.Companion.getKLogger().warn(async);
                                       } else {
                                          if (PreAnalysisImpl.access$getFilesWhichHitSizeThreshold$p(this.this$0).size()
                                             == PreAnalysisImpl.access$getMaximumFileSizeThresholdWarnings$p(this.this$0)) {
                                             PreAnalysisImpl.Companion.getKLogger().warn(<unrepresentable>::invokeSuspend$lambda$1);
                                          }

                                          PreAnalysisImpl.Companion.getKLogger().debug(async);
                                       }
                                    }
                                 } else {
                                    val var11: Semaphore = this.this$0.chooseSemaphore(fileSize);
                                    r.add(
                                       BuildersKt.async$default(
                                          `$this$async`,
                                          null,
                                          null,
                                          (
                                             new Function2<CoroutineScope, Continuation<? super T>, Object>(
                                                var11, file, this.this$0, this.$t, this.this$1, this.$block, null
                                             )// $VF: Couldn't be decompiled
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
                                          3,
                                          null
                                       )
                                    );
                                 }
                              }
                           }
                        }

                        val var17: java.util.Collection = r;
                        val var10001: Continuation = this as Continuation;
                        this.L$0 = null;
                        this.L$1 = null;
                        this.label = 2;
                        var13 = (Function1)AwaitKt.awaitAll(var17, var10001);
                        return if (var13 === var10) var10 else CollectionsKt.filterNotNull(var13 as java.lang.Iterable);
                     }

                     public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                        val var3: Function2 = new <anonymous constructor>(
                           this.$files, this.this$0, this.$conf, this.$t, this.this$1, this.$block, `$completion`
                        );
                        var3.L$0 = value;
                        return var3 as Continuation<Unit>;
                     }

                     public final Object invoke(CoroutineScope p1, Continuation<? super java.util.List<? extends T>> p2) {
                        return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                     }

                     private static final java.lang.String invokeSuspend$lambda$0(Integer $threshold, IResFile $file, long $fileSize) {
                        return "File size threshold ($`$threshold`) exceeded for $`$file` (size: $`$fileSize`)";
                     }

                     private static final Object invokeSuspend$lambda$1(PreAnalysisImpl this$0) {
                        return "File size threshold exceeded ........ (more than ${PreAnalysisImpl.access$getMaximumFileSizeThresholdWarnings$p(`this$0`)}. check log: ${AnalyzerEnv.INSTANCE
                           .getLastLogFile()})";
                     }
                  }
               ) as Function2,
               3,
               null
            );
         }

         public final SafeAnalyzeUtil getSafeAnalyzeUtil() {
            return this.safeAnalyzeUtil;
         }

         @Override
         public Deferred<java.util.List<T>> getAsyncResult() {
            return this.asyncResult;
         }

         @Override
         public Object await(Continuation<? super java.util.List<? extends T>> $completion) {
            return PreAnalysisApi.Result.DefaultImpls.await(this, `$completion`);
         }
      };
   }

   context(CheckerUnit)
   public override fun <T> atClass(clazz: IClassMatch, config: (IPreAnalysisClassConfig) -> Unit, block: (IClassCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      return this.atClass(`$context_receiver_0`, clazz.matched(this.scene), config, block);
   }

   context(CheckerUnit)
   public override fun <T> atAnyClass(config: (IPreAnalysisClassConfig) -> Unit, block: (IClassCheckPoint, Continuation<T>) -> Any?): Result<T> {
      val var5: PreAnalysisClassConfig = new PreAnalysisClassConfig(false, false, 3, null);
      config.invoke(var5);
      return this.atClass(
         `$context_receiver_0`, (java.util.Collection<? extends SootClass>)(if (var5.getAppOnly()) this.appOnlyClasses else this.allClasses), config, block
      );
   }

   context(CheckerUnit)
   public override fun <T> atMethod(method: IMethodMatch, config: (IPreAnalysisMethodConfig) -> Unit, block: (IMethodCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      return this.atMethod(`$context_receiver_0`, method.matched(this.scene), config, block);
   }

   context(CheckerUnit)
   public override fun <T> atAnyMethod(config: (IPreAnalysisMethodConfig) -> Unit, block: (IMethodCheckPoint, Continuation<T>) -> Any?): Result<T> {
      val targets: PreAnalysisMethodConfig = new PreAnalysisMethodConfig(false, false, 3, null);
      config.invoke(targets);
      return this.atMethod(`$context_receiver_0`, if (targets.getAppOnly()) this.appOnlyMethods else this.allMethods, config, block);
   }

   context(CheckerUnit)
   public override fun <T> atField(field: IFieldMatch, config: (IPreAnalysisFieldConfig) -> Unit, block: (IFieldCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      return this.atField(`$context_receiver_0`, field.matched(this.scene), config, block);
   }

   context(CheckerUnit)
   public override fun <T> atAnyField(config: (IPreAnalysisFieldConfig) -> Unit, block: (IFieldCheckPoint) -> T): Result<T> {
      val targets: PreAnalysisFieldConfig = new PreAnalysisFieldConfig(false, false, 3, null);
      config.invoke(targets);
      return this.atField(
         `$context_receiver_0`,
         if (targets.getAppOnly()) this.appOnlyFields else this.allFields,
         config,
         (
            new Function2<IFieldCheckPoint, Continuation<? super T>, Object>(block) {
               {
                  super(
                     2,
                     receiver,
                     Kotlin::class.java,
                     "suspendConversion0",
                     "atAnyField$suspendConversion0(Lkotlin/jvm/functions/Function1;Lcom/feysh/corax/config/api/IFieldCheckPoint;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                     0
                  );
               }

               public final Object invoke(IFieldCheckPoint p0, Continuation<? super T> $completion) {
                  return PreAnalysisImpl.access$atAnyField$suspendConversion0(this.receiver as Function1, p0, `$completion`);
               }
            }
         ) as Function2
      );
   }

   context(CheckerUnit)
   public override fun <T> atInvoke(callee: IMethodMatch, config: (IPreAnalysisInvokeConfig) -> Unit, block: (IInvokeCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      return this.atInvoke(`$context_receiver_0`, callee.matched(this.scene), config, block);
   }

   context(CheckerUnit)
   public override fun <T> atAnyInvoke(config: (IPreAnalysisInvokeConfig) -> Unit, block: (IInvokeCheckPoint, Continuation<T>) -> Any?): Result<T> {
      return this.atInvoke(`$context_receiver_0`, null, config, block);
   }

   context(CheckerUnit)
   public override fun <T> atSourceFile(path: Path, config: (IPreAnalysisFileConfig) -> Unit, block: (ISourceFileCheckPoint, Continuation<T>) -> Any?): Result<
         T
      > {
      return this.atSourceFile(`$context_receiver_0`, (new Function1<Continuation<? super java.util.Iterator<? extends IResFile>>, Object>(path, null) {
         int label;

         {
            super(1, `$completionx`);
            this.$path = `$path`;
         }

         public final Object invokeSuspend(Object $result) {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  return CollectionsKt.listOf(Resource.INSTANCE.fileOf(this.$path)).iterator();
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
         }

         public final Continuation<Unit> create(Continuation<?> $completion) {
            return (new <anonymous constructor>(this.$path, `$completion`)) as Continuation<Unit>;
         }

         public final Object invoke(Continuation<? super java.util.Iterator<? extends IResFile>> p1) {
            return (this.create(p1) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as (Continuation<? super java.utilIterator<? extends IResFile>>?) -> Any, config, block);
   }

   context(CheckerUnit)
   public override fun <T> atAnySourceFile(
      extension: String?,
      filename: String?,
      config: (IPreAnalysisFileConfig) -> Unit,
      block: (ISourceFileCheckPoint, Continuation<T>) -> Any?
   ): Result<T> {
      return this.atSourceFile(
         `$context_receiver_0`, (new Function1<Continuation<? super java.util.Iterator<? extends IResFile>>, Object>(this, extension, filename, null) {
            int label;

            {
               super(1, `$completionx`);
               this.this$0 = `$receiver`;
               this.$extension = `$extension`;
               this.$filename = `$filename`;
            }

            public final Object invokeSuspend(Object $result) {
               val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               var var10000: Any;
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     var10000 = this.this$0;
                     val var10001: java.lang.String = this.$extension;
                     val var10002: java.lang.String = this.$filename;
                     val var10003: Continuation = this as Continuation;
                     this.label = 1;
                     var10000 = (PreAnalysisImpl)var10000.findFiles(var10001, var10002, var10003);
                     if (var10000 === var2) {
                        return var2;
                     }
                     break;
                  case 1:
                     ResultKt.throwOnFailure(`$result`);
                     var10000 = (PreAnalysisImpl)`$result`;
                     break;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               return (var10000 as Sequence).iterator();
            }

            public final Continuation<Unit> create(Continuation<?> $completion) {
               return (new <anonymous constructor>(this.this$0, this.$extension, this.$filename, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(Continuation<? super java.util.Iterator<? extends IResFile>> p1) {
               return (this.create(p1) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as (Continuation<? super java.utilIterator<? extends IResFile>>?) -> Any, config, block
      );
   }

   public override fun report(checkType: CheckType, file: Path, region: Region, env: (Env) -> Unit) {
      val fileInfo: FileResInfo = new FileResInfo(Resource.INSTANCE.fileOf(file));
      val info: DefaultEnv = new DefaultEnv(region.getMutable(), fileInfo.getReportFileName(), null, null, null, null, null, null, null, 508, null);
      env.invoke(info);
      this.resultCollector.report(checkType, new PreAnalysisReportEnv(fileInfo, info));
   }

   public override fun report(checkType: CheckType, sootHost: Host, env: (Env) -> Unit) {
      val var10000: Any;
      if (sootHost is SootClass) {
         var10000 = new ClassCheckPoint(sootHost as SootClass, this.info);
      } else if (sootHost is SootMethod) {
         var10000 = new MethodCheckPoint(sootHost as SootMethod, this.info);
      } else if (sootHost is SootField) {
         var10000 = new FieldCheckPoint(sootHost as SootField, this.info);
      } else {
         kLogger.error(PreAnalysisImpl::report$lambda$9);
         var10000 = null;
      }

      if (var10000 != null) {
         this.report(var10000 as ICheckPoint, checkType, env);
      }
   }

   context(CheckerUnit)
   public override fun <T> runInSceneAsync(block: (Continuation<T>) -> Any?): Deferred<T?> {
      val safeAnalyzeUtil: SafeAnalyzeUtil = new SafeAnalyzeUtil(this.cancelAnalysisInErrorCount, 0, 2, null);
      return BuildersKt.async$default(
         this.getScope(), null, null, (new Function2<CoroutineScope, Continuation<? super T>, Object>(safeAnalyzeUtil, `$context_receiver_0`, block, null) {
            int label;

            {
               super(2, `$completionx`);
               this.$safeAnalyzeUtil = `$safeAnalyzeUtil`;
               this.$$context_receiver_0 = `$$context_receiver_0`;
               this.$block = `$block`;
            }

            public final Object invokeSuspend(Object $result) {
               val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               var var10000: Any;
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     var10000 = this.$safeAnalyzeUtil;
                     val var10001: CheckerUnit = this.$$context_receiver_0;
                     val var10002: Function1 = this.$block;
                     val var10003: Continuation = this as Continuation;
                     this.label = 1;
                     var10000 = (SafeAnalyzeUtil)var10000.safeRunInSceneAsync(var10001, var10002, var10003);
                     if (var10000 === var2) {
                        return var2;
                     }
                     break;
                  case 1:
                     ResultKt.throwOnFailure(`$result`);
                     var10000 = (SafeAnalyzeUtil)`$result`;
                     break;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               return var10000;
            }

            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
               return (new <anonymous constructor>(this.$safeAnalyzeUtil, this.$$context_receiver_0, this.$block, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(CoroutineScope p1, Continuation<? super T> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2, 3, null
      );
   }

   public override fun <P> P.report(checkType: CheckType, env: (Env) -> Unit) where P : ICheckPoint, P : INodeWithRange {
      val info: DefaultEnv = (`$this$report` as CheckPoint).getEnv$corax_framework();
      env.invoke(info);
      this.resultCollector.report(checkType, new PreAnalysisReportEnv((`$this$report` as CheckPoint).getFile(), info));
   }

   public override fun archivePath(archiveFile: Path, entry: String): Path {
      return Resource.INSTANCE.archivePath(archiveFile, entry);
   }

   public override fun getZipEntry(innerFilePath: Path): Pair<Path, String>? {
      val file: IResource = Resource.INSTANCE.of(innerFilePath);
      val var10000: java.lang.String = file.getZipEntry();
      return if (var10000 == null) null else TuplesKt.to(file.getSchemePath(), var10000);
   }

   public override fun Path.getShadowFile(copyDest: Path?): File {
      var var10000: IResource;
      var var10001: IResDirectory;
      label16: {
         val file: IResource = Resource.INSTANCE.of(`$this$getShadowFile`);
         var10000 = file;
         if (copyDest != null) {
            val var7: IResDirectory = Resource.INSTANCE.dirOf(copyDest);
            var10001 = if (var7.isFileScheme()) var7 else null;
            var10000 = file;
            if (var10001 != null) {
               break label16;
            }
         }

         var10001 = this.getMainConfig().getOutput_dir();
      }

      return var10000.expandRes(var10001).getFile();
   }

   public override fun globPath(glob: String): List<Path>? {
      var var10000: java.util.List = ResourceImplKt.globPath(glob);
      if (var10000 != null) {
         val `$this$map$iv`: java.lang.Iterable = var10000;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var10000, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add((`item$iv$iv` as IResource).getPath());
         }

         var10000 = `destination$iv$iv` as java.util.List;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public suspend fun findFiles(extension: String?, filename: String?): Sequence<IResFile> {
      if (extension != null) {
         return this.locator.getByFileExtension(extension, `$completion`);
      } else {
         return if (filename != null) this.locator.getByFileName(filename, `$completion`) else this.locator.getAllFiles(`$completion`);
      }
   }

   private fun IPreAnalysisConfig.skip(file: Path): Boolean {
      return skip$default(
         this,
         `$this$skip`,
         null,
         this.scanFilter.getProcessRegex().getClazzRules(),
         LazyKt.lazy(LazyThreadSafetyMode.NONE, PreAnalysisImpl::skip$lambda$13),
         1,
         null
      );
   }

   private fun IPreAnalysisConfig.skip(sc: SootClass): Boolean {
      return skip$default(
         this,
         `$this$skip`,
         null,
         this.scanFilter.getProcessRegex().getClazzRules(),
         LazyKt.lazy(LazyThreadSafetyMode.NONE, PreAnalysisImpl::skip$lambda$14),
         1,
         null
      );
   }

   private fun IPreAnalysisConfig.skip(sf: SootField): Boolean {
      return this.skip(
         `$this$skip`, null, this.scanFilter.getProcessRegex().getClazzRules(), LazyKt.lazy(LazyThreadSafetyMode.NONE, PreAnalysisImpl::skip$lambda$15)
      );
   }

   private fun IPreAnalysisConfig.skip(sm: SootMethod): Boolean {
      return skip$default(
         this,
         `$this$skip`,
         null,
         this.scanFilter.getProcessRegex().getClazzRules(),
         LazyKt.lazy(LazyThreadSafetyMode.NONE, PreAnalysisImpl::skip$lambda$16),
         1,
         null
      );
   }

   private fun IPreAnalysisConfig.skip(origAction: String? = this.OrigAction, rule: List<IMatchItem>, target: Lazy<IMatchTarget>): Boolean {
      if (!`$this$skip`.getIgnoreProjectConfigProcessFilter()
         && ScanFilter.getActionOf$default(this.scanFilter, rule, origAction, target.getValue() as ProcessRule.IMatchTarget, null, 8, null) === ProcessRule.ScanAction.Skip
         )
       {
         return true;
      } else if (`$this$skip`.getProcessRules().isEmpty()) {
         return false;
      } else {
         return ProcessRule.INSTANCE.matches(`$this$skip`.getProcessRules(), target.getValue() as ProcessRule.IMatchTarget).getSecond() === ProcessRule.ScanAction.Skip;
      }
   }

   public fun invokeCheckPoints(atInvoke: List<SootMethod>?): Set<InvokeCheckPoint> {
      var var10: java.util.Set;
      if (atInvoke != null) {
         val `$this$flatMapTo$iv`: java.lang.Iterable = atInvoke;
         val `destination$iv`: java.util.Collection = new LinkedHashSet();

         for (Object element$iv : $this$flatMapTo$iv) {
            var10 = this.getInvokePointData().getTargetsToEdges().get(`element$iv` as SootMethod);
            CollectionsKt.addAll(`destination$iv`, var10);
         }

         var10 = `destination$iv` as java.util.Set;
      } else {
         var10 = this.getInvokePointData().getAllPoint();
      }

      return var10;
   }

   override fun processPreAnalysisUnits(`$completion`: Continuation<? super Unit>): Any? {
      return PreAnalysisCoroutineScope.DefaultImpls.processPreAnalysisUnits(this, `$completion`);
   }

   override fun <T> runInScene(`$context_receiver_0`: CheckerUnit, block: (Continuation<? super T>?) -> Any): Job {
      return PreAnalysisCoroutineScope.DefaultImpls.runInScene(this, `$context_receiver_0`, block);
   }

   override fun <T> atMethod(
      `$context_receiver_0`: CheckerUnit,
      method: KCallable<?>,
      config: (IPreAnalysisMethodConfig?) -> Unit,
      block: (IMethodCheckPoint?, Continuation<? super T>?) -> Any
   ): PreAnalysisApiResult<T> {
      return PreAnalysisCoroutineScope.DefaultImpls.atMethod(this, `$context_receiver_0`, method, config, block);
   }

   override fun <T> atInvoke(
      `$context_receiver_0`: CheckerUnit,
      callee: KCallable<?>,
      config: (IPreAnalysisInvokeConfig?) -> Unit,
      block: (IInvokeCheckPoint?, Continuation<? super T>?) -> Any
   ): PreAnalysisApiResult<T> {
      return PreAnalysisCoroutineScope.DefaultImpls.atInvoke(this, `$context_receiver_0`, callee, config, block);
   }

   override fun <P extends ICheckPoint & INodeWithRange> P.report(checkType: CheckType, region: Region, env: (BugMessage.Env?) -> Unit) {
      PreAnalysisCoroutineScope.DefaultImpls.report(this, (P)`$this$report`, checkType, region, env);
   }

   override fun ISourceFileCheckPoint.report(checkType: CheckType, region: Region, env: (BugMessage.Env?) -> Unit) {
      PreAnalysisCoroutineScope.DefaultImpls.report(this, `$this$report`, checkType, region, env);
   }

   override fun ISourceFileCheckPoint.report(checkType: CheckType, cpgRegion: de.fraunhofer.aisec.cpg.sarif.Region, env: (BugMessage.Env?) -> Unit): Unit? {
      return PreAnalysisCoroutineScope.DefaultImpls.report(this, `$this$report`, checkType, cpgRegion, env);
   }

   override fun ISourceFileCheckPoint.report(checkType: CheckType, jpsStart: Position, jpsEnd: Position?, env: (BugMessage.Env?) -> Unit): Unit? {
      return PreAnalysisCoroutineScope.DefaultImpls.report(this, `$this$report`, checkType, jpsStart, jpsEnd, env);
   }

   override fun ISourceFileCheckPoint.report(checkType: CheckType, regionNode: NodeWithRange<?>, env: (BugMessage.Env?) -> Unit): Unit? {
      return PreAnalysisCoroutineScope.DefaultImpls.report(this, `$this$report`, checkType, regionNode, env);
   }

   override fun report(checkType: CheckType, sootHost: Host, region: Region?, env: (BugMessage.Env?) -> Unit) {
      PreAnalysisCoroutineScope.DefaultImpls.report(this, checkType, sootHost, region, env);
   }

   public override fun toDecl(target: Any): XDecl {
      return this.$$delegate_0.toDecl(target);
   }

   public override infix fun XDecl.dependsOn(dep: XDecl) {
      this.$$delegate_0.dependsOn(`$this$dependsOn`, dep);
   }

   public override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
      this.$$delegate_0.dependsOn(`$this$dependsOn`, deps);
   }

   @JvmStatic
   fun `globalNormalAnalyzeSemaphore_delegate$lambda$6`(`this$0`: PreAnalysisImpl): Semaphore {
      return createNormalAnalyzeSemaphore$default(`this$0`, 0, 1, null);
   }

   @JvmStatic
   fun `globalResourceSemaphore_delegate$lambda$7`(`this$0`: PreAnalysisImpl): Semaphore {
      return createResourceSemaphore$default(`this$0`, 0, 1, null);
   }

   @JvmStatic
   fun `report$lambda$9`(`$sootHost`: Host): Any {
      return "SootHost type in report(,sootHost: ${`$sootHost`.getClass()},) is not support yet! only support types: SootClass, SootMethod, SootField";
   }

   @JvmStatic
   fun `skip$lambda$13`(`this$0`: PreAnalysisImpl, `$file`: Path): ProcessRule.FileMatch.MatchTarget {
      return `this$0`.scanFilter.get(`$file`);
   }

   @JvmStatic
   fun `skip$lambda$14`(`this$0`: PreAnalysisImpl, `$sc`: SootClass): ProcessRule.ClassMemberMatch.MatchTarget {
      return `this$0`.scanFilter.get(`$sc`);
   }

   @JvmStatic
   fun `skip$lambda$15`(`this$0`: PreAnalysisImpl, `$sf`: SootField): ProcessRule.ClassMemberMatch.MatchTarget {
      return `this$0`.scanFilter.get(`$sf`);
   }

   @JvmStatic
   fun `skip$lambda$16`(`this$0`: PreAnalysisImpl, `$sm`: SootMethod): ProcessRule.ClassMemberMatch.MatchTarget {
      return `this$0`.scanFilter.get(`$sm`);
   }

   @JvmStatic
   fun `invokePointData_delegate$lambda$18`(`this$0`: PreAnalysisImpl): PreAnalysisImpl.InvokePointData {
      val var10000: RefType = Scene.v().getRefTypeUnsafe("java.lang.Object");
      if (var10000 == null) {
         return new PreAnalysisImpl.InvokePointData((new HashMultiMap()) as MultiMap<SootMethod, InvokeCheckPoint>, SetsKt.emptySet());
      } else {
         val objectType: RefType = var10000;
         val targetsToEdges: MultiMap = (new HashMultiMap()) as MultiMap;
         val allPoint: java.util.Set = new LinkedHashSet();
         val var21: java.util.Iterator = `this$0`.getCg().iterator();
         val var4: java.util.Iterator = var21;

         while (var4.hasNext()) {
            val edge: Edge = var4.next() as Edge;
            val src: SootMethod = edge.src();
            if (src != null && `this$0`.appOnlyClasses.contains(src.getDeclaringClass())) {
               var tgt: SootMethod;
               var callSite: soot.Unit;
               label64: {
                  tgt = edge.tgt();
                  callSite = edge.srcUnit();
                  val var22: Stmt = callSite as? Stmt;
                  if ((callSite as? Stmt) != null) {
                     val var23: Stmt = if (var22.containsInvokeExpr()) var22 else null;
                     if (var23 != null) {
                        var24 = var23.getInvokeExpr();
                        break label64;
                     }
                  }

                  var24 = null;
               }

               var invokeExpr: InvokeExpr;
               label70: {
                  invokeExpr = var24;
                  val var25: InstanceInvokeExpr = var24 as? InstanceInvokeExpr;
                  if ((var24 as? InstanceInvokeExpr) != null) {
                     val var26: Value = var25.getBase();
                     if (var26 != null) {
                        var27 = var26.getType();
                        break label70;
                     }
                  }

                  var27 = null;
               }

               val declaredReceiverType: Type = var27;
               if (!(var27 == objectType)) {
                  val subSignature: java.lang.String = tgt.getSubSignature();
                  val var28: SootClass = tgt.getDeclaringClass();
                  var invokeCheckPoint: InvokeCheckPoint = null;

                  for (SootMethod find : SootUtilsKt.findMethodOrNull(var28, subSignature)) {
                     if (find == tgt || !find.isConcrete()) {
                        if (invokeCheckPoint == null) {
                           val var16: SootInfoCache = `this$0`.info;
                           val var17: SootMethodRef = if (invokeExpr != null) invokeExpr.getMethodRef() else null;
                           invokeCheckPoint = new InvokeCheckPoint(var16, src, callSite, declaredReceiverType, var17, tgt, invokeExpr);
                           allPoint.add(invokeCheckPoint);
                        }

                        targetsToEdges.put(find, invokeCheckPoint);
                     }
                  }
               }
            }
         }

         return new PreAnalysisImpl.InvokePointData(targetsToEdges, allPoint);
      }
   }

   @JvmStatic
   fun `kLogger$lambda$20`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val kLogger: KLogger
   }

   public data class InvokePointData(targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint>, allPoint: Set<InvokeCheckPoint>) {
      public final val targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint>
      public final val allPoint: Set<InvokeCheckPoint>

      init {
         this.targetsToEdges = targetsToEdges;
         this.allPoint = allPoint;
      }

      public operator fun component1(): MultiMap<SootMethod, InvokeCheckPoint> {
         return this.targetsToEdges;
      }

      public operator fun component2(): Set<InvokeCheckPoint> {
         return this.allPoint;
      }

      public fun copy(targetsToEdges: MultiMap<SootMethod, InvokeCheckPoint> = this.targetsToEdges, allPoint: Set<InvokeCheckPoint> = this.allPoint): cn.sast.framework.engine.PreAnalysisImpl.InvokePointData {
         return new PreAnalysisImpl.InvokePointData(targetsToEdges, allPoint);
      }

      public override fun toString(): String {
         return "InvokePointData(targetsToEdges=${this.targetsToEdges}, allPoint=${this.allPoint})";
      }

      public override fun hashCode(): Int {
         return this.targetsToEdges.hashCode() * 31 + this.allPoint.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is PreAnalysisImpl.InvokePointData) {
            return false;
         } else {
            val var2: PreAnalysisImpl.InvokePointData = other as PreAnalysisImpl.InvokePointData;
            if (!(this.targetsToEdges == (other as PreAnalysisImpl.InvokePointData).targetsToEdges)) {
               return false;
            } else {
               return this.allPoint == var2.allPoint;
            }
         }
      }
   }
}
