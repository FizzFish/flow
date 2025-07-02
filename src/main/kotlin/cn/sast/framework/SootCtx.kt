package cn.sast.framework

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.config.SrcPrecedence
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.util.IMonitor
import cn.sast.api.util.Kotlin_extKt
import cn.sast.api.util.OthersKt
import cn.sast.api.util.PhaseIntervalTimer
import cn.sast.api.util.Timer
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.Resource
import cn.sast.framework.compiler.EcjCompiler
import cn.sast.framework.graph.CGUtils
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.rewrite.StringConcatRewriterTransform
import cn.sast.idfa.analysis.ProcessInfoView
import com.feysh.corax.config.api.ISootInitializeHandler
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.Theme
import driver.PTAFactory
import driver.PTAPattern
import java.io.File
import java.lang.reflect.Field
import java.nio.file.InvalidPathException
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.NoSuchElementException
import java.util.SortedSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.Function
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import java.util.Comparator
import kotlin.comparisons.compareValues
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import org.utbot.framework.plugin.services.JdkInfo
import org.utbot.framework.plugin.services.JdkInfoService
import qilin.CoreConfig
import qilin.CoreConfig.CorePTAConfiguration
import qilin.core.PTA
import qilin.core.PTAScene
import qilin.core.pag.ValNode
import qilin.pta.PTAConfig
import qilin.util.MemoryWatcher
import qilin.util.PTAUtils
import soot.G
import soot.Main
import soot.MethodOrMethodContext
import soot.PackManager
import soot.PointsToAnalysis
import soot.Scene
import soot.Singletons
import soot.SootClass
import soot.SootMethod
import soot.Transform
import soot.Transformer
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.jimple.toolkits.pointer.DumbPointerAnalysis
import soot.options.Options
import soot.util.Chain

@SourceDebugExtension(["SMAP\nSootCtx.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SootCtx.kt\ncn/sast/framework/SootCtx\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Logging.kt\norg/utbot/common/LoggingKt\n+ 4 Timer.kt\ncn/sast/api/util/TimerKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,731:1\n2632#2,3:732\n1557#2:767\n1628#2,2:768\n2318#2,14:770\n1630#2:784\n1053#2:785\n1368#2:787\n1454#2,5:788\n774#2:793\n865#2,2:794\n774#2:796\n865#2,2:797\n1053#2:871\n1053#2:872\n49#3,13:735\n62#3,11:756\n49#3,13:799\n62#3,11:836\n49#3,24:847\n16#4,8:748\n16#4,8:812\n16#4,8:820\n16#4,8:828\n1#5:786\n*S KotlinDebug\n*F\n+ 1 SootCtx.kt\ncn/sast/framework/SootCtx\n*L\n192#1:732,3\n313#1:767\n313#1:768,2\n314#1:770,14\n313#1:784\n315#1:785\n537#1:787\n537#1:788,5\n537#1:793\n537#1:794,2\n538#1:796\n538#1:797,2\n580#1:871\n586#1:872\n201#1:735,13\n201#1:756,11\n608#1:799,13\n608#1:836,11\n706#1:847,24\n212#1:748,8\n609#1:812,8\n612#1:820,8\n615#1:828,8\n*E\n"])
public open class SootCtx(mainConfig: MainConfig) : ISootInitializeHandler {
   public final val mainConfig: MainConfig
   public final val monitor: IMonitor?
   private final val _loadClassesTimer: Timer?
   private final val _classesClassificationTimer: Timer?
   private final val _cgConstructTimer: Timer?

   public final lateinit var cgAlgorithmProvider: CgAlgorithmProvider
      internal set

   public final val autoAppClassesLocator: ProjectFileLocator
      public final get() {
         return this.autoAppClassesLocator$delegate.getValue() as ProjectFileLocator;
      }


   public final var callGraph: CallGraph
      public final get() {
         val var10000: CallGraph = Scene.v().getCallGraph();
         return var10000;
      }

      public final set(value) {
         Scene.v().setCallGraph(value);
      }


   public final val sootMethodCallGraph: CallGraph
      public final get() {
         val cg: CallGraph = new CallGraph();
         var var10000: java.util.Iterator = this.getCallGraph().iterator();
         val edgeIterator: java.util.Iterator = var10000;

         while (edgeIterator.hasNext()) {
            var10000 = (java.util.Iterator)edgeIterator.next();
            val e: Edge = var10000 as Edge;
            if (!(var10000 as Edge).isInvalid()) {
               cg.addEdge(new Edge(e.src() as MethodOrMethodContext, e.srcUnit(), e.tgt() as MethodOrMethodContext, e.kind()));
            }
         }

         return cg;
      }


  public final val entryPoints: List<SootMethod>
      public final get() {
         val var10000: java.util.List = Scene.v().getEntryPoints();
         return var10000;
      }

   private class JarRelativePathComparator(private val ctx: SootCtx) : Comparator<IResFile> {
      override fun compare(a: IResFile, b: IResFile): Int {
         return compareValues(
            ctx.mainConfig.tryGetRelativePath(a).getRelativePath(),
            ctx.mainConfig.tryGetRelativePath(b).getRelativePath()
         )
      }
   }

   private object StringComparator : Comparator<String> {
      override fun compare(a: String, b: String): Int {
         return a.compareTo(b)
      }
   }


   init {
      this.mainConfig = mainConfig;
      this.monitor = this.mainConfig.getMonitor();
      this._loadClassesTimer = if (this.monitor != null) this.monitor.timer("loadClasses") else null;
      this._classesClassificationTimer = if (this.monitor != null) this.monitor.timer("classes.classification") else null;
      this._cgConstructTimer = if (this.monitor != null) this.monitor.timer("callgraph.construct") else null;
      this.autoAppClassesLocator$delegate = LazyKt.lazy(SootCtx::autoAppClassesLocator_delegate$lambda$25);
   }

   public open fun configureCallGraph(options: Options) {
      this.setCgAlgorithmProvider(
         this.configureCallGraph(options, this.mainConfig.getCallGraphAlgorithm(), this.mainConfig.getApponly(), this.mainConfig.getEnableReflection())
      );
   }

   public open fun configureCallGraph(options: Options, callGraphAlgorithm: String, appOnly: Boolean, enableReflection: Boolean): CgAlgorithmProvider {
      val appOnlySootOptionValue: java.lang.String = "apponly:${if (appOnly) "true" else "false"}";
      options.set_ignore_resolving_levels(true);
      logger.info(SootCtx::configureCallGraph$lambda$2$lambda$0);
      val var12: CgAlgorithmProvider;
      if (StringsKt.equals(callGraphAlgorithm, "SPARK", true)) {
         options.setPhaseOption("cg.spark", "on");
         options.setPhaseOption("cg.spark", "on-fly-cg:true");
         options.setPhaseOption("cg.spark", appOnlySootOptionValue);
         var12 = CgAlgorithmProvider.Soot;
      } else if (StringsKt.startsWith(callGraphAlgorithm, "GEOM", true)) {
         options.setPhaseOption("cg.spark", "on");
         options.setPhaseOption("cg.spark", appOnlySootOptionValue);
         AbstractInfoflow.setGeomPtaSpecificOptions();
         if (StringsKt.equals(StringsKt.substringAfter$default(callGraphAlgorithm, "-", null, 2, null), "HeapIns", true)) {
            options.setPhaseOption("cg.spark", "geom-encoding:HeapIns");
         } else {
            if (!StringsKt.equals(StringsKt.substringAfter$default(callGraphAlgorithm, "-", null, 2, null), "PtIns", true)) {
               throw new IllegalStateException(("$callGraphAlgorithm is incorrect").toString());
            }

            options.setPhaseOption("cg.spark", "geom-encoding:PtIns");
         }

         var12 = CgAlgorithmProvider.Soot;
      } else if (StringsKt.equals(callGraphAlgorithm, "CHA", true)) {
         options.setPhaseOption("cg.cha", "on");
         options.setPhaseOption("cg.cha", appOnlySootOptionValue);
         var12 = CgAlgorithmProvider.Soot;
      } else if (StringsKt.equals(callGraphAlgorithm, "RTA", true)) {
         options.setPhaseOption("cg.spark", "on");
         options.setPhaseOption("cg.spark", "rta:true");
         options.setPhaseOption("cg.spark", "on-fly-cg:false");
         options.setPhaseOption("cg.spark", appOnlySootOptionValue);
         var12 = CgAlgorithmProvider.Soot;
      } else if (StringsKt.equals(callGraphAlgorithm, "VTA", true)) {
         options.setPhaseOption("cg.spark", "on");
         options.setPhaseOption("cg.spark", "vta:true");
         options.setPhaseOption("cg.spark", appOnlySootOptionValue);
         var12 = CgAlgorithmProvider.Soot;
      } else {
         val `$this$configureCallGraph_u24lambda_u242_u24lambda_u241`: PTAConfig = PTAConfig.v();
         `$this$configureCallGraph_u24lambda_u242_u24lambda_u241`.getPtaConfig().ptaPattern = new PTAPattern(callGraphAlgorithm);
         `$this$configureCallGraph_u24lambda_u242_u24lambda_u241`.getPtaConfig().singleentry = false;
         `$this$configureCallGraph_u24lambda_u242_u24lambda_u241`.getPtaConfig().ctxDebloating = true;
         `$this$configureCallGraph_u24lambda_u242_u24lambda_u241`.getPtaConfig().stringConstants = true;
         var12 = CgAlgorithmProvider.QiLin;
      }

      if (enableReflection) {
         options.setPhaseOption("cg", "types-for-invoke:true");
      }

      options.setPhaseOption("cg.spark", "set-impl:hybrid");
      options.setPhaseOption("jb", "model-lambdametafactory:false");
      options.setPhaseOption("jb.ulp", "off");
      return var12;
   }

   public open fun constructCallGraph(cgAlgorithm: CgAlgorithmProvider, appOnly: Boolean, record: Boolean = true) {
      label151: {
         val scene: Scene = Scene.v();
         this.releaseCallGraph();
         this.onBeforeCallGraphConstruction();
         if (!this.mainConfig.getSkipClass()) {
            val var10000: Chain = scene.getApplicationClasses();
            val cgs: java.lang.Iterable = var10000 as java.lang.Iterable;
            var var70: Boolean;
            if (var10000 as java.lang.Iterable is java.util.Collection && ((var10000 as java.lang.Iterable) as java.util.Collection).isEmpty()) {
               var70 = true;
            } else {
               label386: {
                  for (Object element$iv : $this$none$iv) {
                     val `$i$f$bracket`: SootClass = var8 as SootClass;
                     if (!OthersKt.isSyntheticComponent(`$i$f$bracket`)) {
                        var70 = false;
                        break label386;
                     }
                  }

                  var70 = true;
               }
            }

            if (var70) {
               throw new IllegalStateException(
                  "application classes must not be empty. check your --auto-app-classes, --process, --source-path, --class-path options".toString()
               );
            }
         }

         scene.getOrMakeFastHierarchy();
         val var48: PhaseIntervalTimer.Snapshot = if (this._cgConstructTimer != null) this._cgConstructTimer.start() else null;
         val var49: LoggerWithLogMethod = LoggingKt.info(logger);
         val var50: java.lang.String = "Constructing the call graph [$cgAlgorithm] ...";
         var49.getLogMethod().invoke { `SootCtx$constructCallGraph$$inlined$bracket$default$1`(var50) };
         val var58: LocalDateTime = LocalDateTime.now();
         var total: Boolean = (boolean)0;
         val `res$iv`: ObjectRef = new ObjectRef();
         `res$iv`.element = Maybe.Companion.empty();

         try {
            try {
               try {
                  val `$this$bracket$iv`: PhaseIntervalTimer = if (this.monitor != null) this.monitor.timer("cgAlgorithm:$cgAlgorithm") else null;
                  if (`$this$bracket$iv` == null) {
                     switch (SootCtx.WhenMappings.$EnumSwitchMapping$0[cgAlgorithm.ordinal()]) {
                        case 1:
                           PackManager.v().getPack("cg").apply();
                           break;
                        case 2:
                           PTAUtils.setAppOnly(appOnly);
                           val ptaConfig: CorePTAConfiguration = CoreConfig.v().getPtaConfig();
                           ptaConfig.printAliasInfo = ExtSettings.INSTANCE.getPrintAliasInfo();
                           ptaConfig.castNeverFailsOfPhantomClass = ExtSettings.INSTANCE.getCastNeverFailsOfPhantomClass();
                           ValNode.UseRoaringPointsToSet = ExtSettings.INSTANCE.getUseRoaringPointsToSet();
                           val entries: java.util.List = scene.getEntryPoints();
                           val pta: PTA = PTAFactory.createPTA(PTAConfig.v().getPtaConfig().ptaPattern);
                           pta.getCgb().getReachableMethods();
                           pta.run();
                           val qilingCG: CallGraph = pta.getCallGraph();
                           val sootCG: CallGraph = scene.getCallGraph();
                           scene.setPointsToAnalysis(pta as PointsToAnalysis);
                           PTAUtils.clear();
                           pta.getPag().small();
                           break;
                        default:
                           throw new NoWhenBranchMatchedException();
                     }
                  } else {
                     val `s$iv`: PhaseIntervalTimer.Snapshot = `$this$bracket$iv`.start();

                     try {
                        switch (SootCtx.WhenMappings.$EnumSwitchMapping$0[cgAlgorithm.ordinal()]) {
                           case 1:
                              PackManager.v().getPack("cg").apply();
                              break;
                           case 2:
                              PTAUtils.setAppOnly(appOnly);
                              val var65: CorePTAConfiguration = CoreConfig.v().getPtaConfig();
                              var65.printAliasInfo = ExtSettings.INSTANCE.getPrintAliasInfo();
                              var65.castNeverFailsOfPhantomClass = ExtSettings.INSTANCE.getCastNeverFailsOfPhantomClass();
                              ValNode.UseRoaringPointsToSet = ExtSettings.INSTANCE.getUseRoaringPointsToSet();
                              val var66: java.util.List = scene.getEntryPoints();
                              val var67: PTA = PTAFactory.createPTA(PTAConfig.v().getPtaConfig().ptaPattern);
                              var67.getCgb().getReachableMethods();
                              var67.run();
                              val var68: CallGraph = var67.getCallGraph();
                              val var69: CallGraph = scene.getCallGraph();
                              scene.setPointsToAnalysis(var67 as PointsToAnalysis);
                              PTAUtils.clear();
                              var67.getPag().small();
                              break;
                           default:
                              throw new NoWhenBranchMatchedException();
                        }
                     } catch (var31: java.lang.Throwable) {
                        `$this$bracket$iv`.stop(`s$iv`);
                     }

                     `$this$bracket$iv`.stop(`s$iv`);
                  }
               } catch (var32: java.lang.Throwable) {
                  val memoryWatcher: Any;
                  memoryWatcher.stop();
                  logger.info(SootCtx::constructCallGraph$lambda$8$lambda$6);
               }

               val var63: Any;
               var63.stop();
               logger.info(SootCtx::constructCallGraph$lambda$8$lambda$6);
            } catch (var33: java.lang.Throwable) {
               var49.getLogMethod().invoke { `SootCtx$constructCallGraph$$inlined$bracket$default$4`(var58, var50, var33) };
               total = (boolean)1;
               throw var33;
            }
         } catch (var34: java.lang.Throwable) {
            if (!total) {
               if ((`res$iv`.element as Maybe).getHasValue()) {
                  var49.getLogMethod().invoke { `SootCtx$constructCallGraph$$inlined$bracket$default$5`(var58, var50, `res$iv`) };
               } else {
                  var49.getLogMethod().invoke { `SootCtx$constructCallGraph$$inlined$bracket$default$6`(var58, var50) };
               }
            }
         }

         if ((`res$iv`.element as Maybe).getHasValue()) {
            var49.getLogMethod().invoke { `SootCtx$constructCallGraph$$inlined$bracket$default$2`(var58, var50, `res$iv`) };
         } else {
            var49.getLogMethod().invoke { `SootCtx$constructCallGraph$$inlined$bracket$default$3`(var58, var50) };
         }

         val var71: Timer = this._cgConstructTimer;
         if (this._cgConstructTimer != null) {
            var71.stop(var48);
         }

         CGUtils.INSTANCE.addCallEdgeForPhantomMethods();
         this.showPta();
         scene.releaseReachableMethods();
         val var72: CGUtils = CGUtils.INSTANCE;
         var72.fixScene(scene);
         if (record) {
            val var51: Chain = scene.getApplicationClasses();
            var var56: Pair = this.activeBodyMethods(var51);
            val var59: Int = (var56.component1() as java.lang.Number).intValue();
            total = (boolean)(var56.component2() as java.lang.Number).intValue();
            if (this.monitor != null) {
               val var73: ProjectMetrics = this.monitor.getProjectMetrics();
               if (var73 != null) {
                  var73.setApplicationMethodsHaveBody(var59);
               }
            }

            if (this.monitor != null) {
               val var10001: ProjectMetrics = this.monitor.getProjectMetrics();
               if (var10001 != null) {
                  var10001.setApplicationMethods(total);
               }
            }

            val var52: Chain = scene.getLibraryClasses();
            var56 = this.activeBodyMethods(var52);
            val activex: Int = (var56.component1() as java.lang.Number).intValue();
            total = (boolean)(var56.component2() as java.lang.Number).intValue();
            if (this.monitor != null) {
               val var10002: ProjectMetrics = this.monitor.getProjectMetrics();
               if (var10002 != null) {
                  var10002.setLibraryMethodsHaveBody(activex);
               }
            }

            if (this.monitor != null) {
               val var10003: ProjectMetrics = this.monitor.getProjectMetrics();
               if (var10003 != null) {
                  var10003.setLibraryMethods(total);
               }
            }
         }

         this.onAfterCallGraphConstruction();
      }
   }

   public open fun constructCallGraph() {
      constructCallGraph$default(this, this.getCgAlgorithmProvider(), this.mainConfig.getApponly(), false, 4, null);
   }

   public fun showPta() {
      val pta: PointsToAnalysis = Scene.v().getPointsToAnalysis();
      if (Scene.v().getPointsToAnalysis() is DumbPointerAnalysis) {
         logger.warn(SootCtx::showPta$lambda$11);
         Scene.v().setPointsToAnalysis(pta);
      }

      logger.info(SootCtx::showPta$lambda$12);
      logger.info(SootCtx::showPta$lambda$13);
      val var10001: Scene = Scene.v();
      this.showClasses(var10001, "After PTA: ", (new Function1<java.lang.String, java.lang.String>(Theme.Companion.getDefault().getInfo()) {
         {
            super(1, receiver, TextStyle::class.java, "invoke", "invoke(Ljava/lang/String;)Ljava/lang/String;", 0);
         }

         public final java.lang.String invoke(java.lang.String p0) {
            return (this.receiver as TextStyle).invoke(p0);
         }
      }) as (java.lang.String?) -> java.lang.String);
   }

   public suspend fun findClassesInnerJar(locator: ProjectFileLocator): Map<String, MutableSet<IResFile>> {
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
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.findClassesInnerJar(null, this as Continuation<? super java.util.Map<java.lang.String, ? extends java.utilSet<IResFile>>>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var md5Group: ConcurrentHashMap;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            val md5Map: ConcurrentHashMap = new ConcurrentHashMap();
            md5Group = new ConcurrentHashMap();
            val var10000: Function2 = (
               new Function2<CoroutineScope, Continuation<? super Unit>, Object>(locator, md5Group, md5Map, null) {
                  int label;

                  {
                     super(2, `$completionx`);
                     this.$locator = `$locator`;
                     this.$md5Group = `$md5Group`;
                     this.$md5Map = `$md5Map`;
                  }

                  public final Object invokeSuspend(Object $result) {
                     val var13: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     var `$this$coroutineScope`: CoroutineScope;
                     var var10000: Any;
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           `$this$coroutineScope` = this.L$0 as CoroutineScope;
                           var10000 = this.$locator;
                           val var10002: Continuation = this as Continuation;
                           this.L$0 = `$this$coroutineScope`;
                           this.label = 1;
                           var10000 = (ProjectFileLocator)var10000.getByFileExtension("class", var10002);
                           if (var10000 === var13) {
                              return var13;
                           }
                           break;
                        case 1:
                           `$this$coroutineScope` = this.L$0 as CoroutineScope;
                           ResultKt.throwOnFailure(`$result`);
                           var10000 = (ProjectFileLocator)`$result`;
                           break;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }

                     val `$this$forEach$iv`: Sequence = var10000 as Sequence;
                     val var4: ConcurrentHashMap = this.$md5Group;
                     val var5: ConcurrentHashMap = this.$md5Map;

                     for (Object element$iv : $this$forEach$iv) {
                        val clz: IResFile = `element$iv` as IResFile;
                        if ((`element$iv` as IResFile).isJarScheme()) {
                           try {
                              val jar: IResFile = Resource.INSTANCE.fileOf(clz.getSchemePath());
                              BuildersKt.launch$default(
                                 `$this$coroutineScope`,
                                 null,
                                 null,
                                 (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(var4, var5, jar, null) {
                                    int label;

                                    {
                                       super(2, `$completionx`);
                                       this.$md5Group = `$md5Group`;
                                       this.$md5Map = `$md5Map`;
                                       this.$jar = `$jar`;
                                    }

                                    public final Object invokeSuspend(Object $result) {
                                       IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                       switch (this.label) {
                                          case 0:
                                             ResultKt.throwOnFailure(`$result`);
                                             val `$this$getOrPut$iv`: ConcurrentMap = this.$md5Group;
                                             val var8: Any = this.$md5Map.computeIfAbsent(this.$jar, new Function(<unrepresentable>::invokeSuspend$lambda$0) {
                                                {
                                                   this.function = function;
                                                }
                                             });
                                             var var9: Any = `$this$getOrPut$iv`.get(var8);
                                             if (var9 == null) {
                                                val `default$iv`: Any = Kotlin_extKt.concurrentHashSetOf();
                                                var9 = `$this$getOrPut$iv`.putIfAbsent(var8, `default$iv`);
                                                if (var9 == null) {
                                                   var9 = `default$iv`;
                                                }
                                             }

                                             (var9 as java.util.Set).add(this.$jar);
                                             return Unit.INSTANCE;
                                          default:
                                             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                       }
                                    }

                                    public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                       return (new <anonymous constructor>(this.$md5Group, this.$md5Map, this.$jar, `$completion`)) as Continuation<Unit>;
                                    }

                                    public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                       return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                    }

                                    private static final java.lang.String invokeSuspend$lambda$0(IResFile $jar, IResFile it) {
                                       return `$jar`.getMd5();
                                    }
                                 }) as Function2,
                                 3,
                                 null
                              );
                           } catch (var14: Exception) {
                              SootCtx.access$getLogger$cp().error(<unrepresentable>::invokeSuspend$lambda$2$lambda$0);
                              SootCtx.access$getLogger$cp().debug(var14, <unrepresentable>::invokeSuspend$lambda$2$lambda$1);
                           }
                        }
                     }

                     return Unit.INSTANCE;
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     val var3: Function2 = new <anonymous constructor>(this.$locator, this.$md5Group, this.$md5Map, `$completion`);
                     var3.L$0 = value;
                     return var3 as Continuation<Unit>;
                  }

                  public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }

                  private static final Object invokeSuspend$lambda$2$lambda$0(IResFile $clz, Exception $e) {
                     return "extract jar file ${`$clz`.getPath()} with error: ${`$e`.getMessage()}";
                  }

                  private static final Object invokeSuspend$lambda$2$lambda$1(IResFile $clz) {
                     return "extract jar file ${`$clz`.getPath()}";
                  }
               }
            ) as Function2;
            `$continuation`.L$0 = md5Group;
            `$continuation`.label = 1;
            if (CoroutineScopeKt.coroutineScope(var10000, `$continuation`) === var7) {
               return var7;
            }
            break;
         case 1:
            md5Group = `$continuation`.L$0 as ConcurrentHashMap;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return md5Group;
   }

   public suspend fun findClassesInnerJarUnderAutoAppClassPath(): Set<IResFile> {
      var `$continuation`: Continuation;
      label84: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label84;
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
               return this.this$0.findClassesInnerJarUnderAutoAppClassPath(this as Continuation<? super java.utilSet<? extends IResFile>>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var24: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            if ((this.mainConfig.getAutoAppClasses() as java.util.Collection).isEmpty()) {
               throw new IllegalStateException("Check failed.".toString());
            }

            val var10001: ProjectFileLocator = this.getAutoAppClassesLocator();
            `$continuation`.L$0 = this;
            `$continuation`.label = 1;
            var10000 = this.findClassesInnerJar(var10001, `$continuation`);
            if (var10000 === var24) {
               return var24;
            }
            break;
         case 1:
            this = `$continuation`.L$0 as SootCtx;
            ResultKt.throwOnFailure(`$result`);
            var10000 = `$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      val result: java.lang.Iterable = (var10000 as java.util.Map).values();
      val extract: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(result, 10));

      for (Object item$iv$iv : $this$map$iv) {
         val `iterator$iv`: java.util.Iterator = (`item$iv$iv` as java.util.Set).iterator();
         if (!`iterator$iv`.hasNext()) {
            throw new NoSuchElementException();
         }

         var `minElem$iv`: Any = `iterator$iv`.next();
         if (!`iterator$iv`.hasNext()) {
            var10000 = `minElem$iv`;
         } else {
            var var33: java.lang.Comparable = this.mainConfig.tryGetRelativePath(`minElem$iv` as IResFile).getRelativePath();

            do {
               val var34: Any = `iterator$iv`.next();
               val var35: java.lang.Comparable = this.mainConfig.tryGetRelativePath(var34 as IResFile).getRelativePath();
               if (var33.compareTo(var35) > 0) {
                  `minElem$iv` = var34;
                  var33 = var35;
               }
            } while (iterator$iv.hasNext());

            var10000 = `minElem$iv`;
         }

         extract.add(var10000 as IResFile);
      }

      val jars: java.util.List = CollectionsKt.sortedWith(
         extract as java.util.List, JarRelativePathComparator(this)
      );
      val var28: LinkedHashSet = new LinkedHashSet();
      val var30: java.util.Iterator = jars.iterator();

      while (true) {
         while (true) {
            if (!var30.hasNext()) {
               return var28;
            }

            val jar: IResFile = var30.next() as IResFile;
            if (jar.isFileScheme()) {
               var10000 = jar;
               break;
            }

            if (jar.isJarScheme()) {
               var var32: IResFile;
               try {
                  var32 = jar.expandRes(this.mainConfig.getOutput_dir()).toFile();
               } catch (var25: InvalidPathException) {
                  logger.error(var25, SootCtx::findClassesInnerJarUnderAutoAppClassPath$lambda$17);
                  continue;
               }

               var10000 = var32;
               break;
            } else {
               logger.error(SootCtx::findClassesInnerJarUnderAutoAppClassPath$lambda$18);
            }
         }

         var28.add(var10000);
      }
   }

   public override fun configure(options: Options) {
      if (this.mainConfig.getSrc_precedence() === SrcPrecedence.prec_java) {
         val var10000: java.lang.Boolean = this.mainConfig.isAndroidScene();
         if (!var10000) {
            val autoAppClasses: IResDirectory = this.mainConfig.getOutput_dir().resolve("gen-classes").toDirectory();
            autoAppClasses.deleteDirectoryRecursively();
            autoAppClasses.mkdirs();
            val var5: EcjCompiler = new EcjCompiler(
               ExtensionsKt.toPersistentSet(this.mainConfig.getProcessDir() as java.lang.Iterable),
               this.mainConfig.getClasspath(),
               autoAppClasses,
               this.mainConfig.getEcj_options(),
               this.mainConfig.getUseDefaultJavaClassPath(),
               null,
               null,
               false,
               null,
               null,
               992,
               null
            );
            if (!var5.compile()) {
               logger.error(SootCtx::configure$lambda$19);
            }

            if (IResDirectory.DefaultImpls.listPathEntries$default(autoAppClasses, null, 1, null).isEmpty()) {
               throw new IllegalStateException(("\n\n!!! no class file found under $autoAppClasses !!!\n\n").toString());
            }

            this.mainConfig.setProcessDir(this.mainConfig.getProcessDir().add(autoAppClasses));
            this.mainConfig.setClasspath(ExtensionsKt.toPersistentSet(var5.getCollectClassPath()));
         }
      }

      this.getAutoAppClassesLocator().update();
      if (!(this.mainConfig.getAutoAppClasses() as java.util.Collection).isEmpty()) {
         val var16: java.lang.Boolean = this.mainConfig.isAndroidScene();
         if (!var16 && !this.mainConfig.getSkipClass()) {
            BuildersKt.runBlocking$default(null, (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var3: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var var10000: Any;
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = this.this$0;
                        val var10001: Continuation = this as Continuation;
                        this.label = 1;
                        var10000 = (SootCtx)var10000.findClassesInnerJarUnderAutoAppClassPath(var10001);
                        if (var10000 === var3) {
                           return var3;
                        }
                        break;
                     case 1:
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = (SootCtx)`$result`;
                        break;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  this.this$0.getMainConfig().setProcessDir(this.this$0.getMainConfig().getProcessDir().addAll(var10000 as java.util.Set));
                  return Unit.INSTANCE;
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }) as Function2, 1, null);
         }
      }

      val var12: MainConfig = this.mainConfig;
      options.set_verbose(true);
      options.set_allow_phantom_elms(true);
      options.set_whole_program(var12.getWhole_program());
      options.set_src_prec(var12.getSrc_precedence().getSootFlag());
      options.set_prepend_classpath(var12.getPrepend_classpath());
      options.set_no_bodies_for_excluded(var12.getNo_bodies_for_excluded());
      options.set_include_all(true);
      options.set_allow_phantom_refs(var12.getAllow_phantom_refs());
      options.set_ignore_classpath_errors(false);
      options.set_throw_analysis(var12.getThrow_analysis());
      options.set_process_multiple_dex(var12.getProcess_multiple_dex());
      options.set_field_type_mismatches(2);
      options.set_full_resolver(true);
      options.classes().addAll(var12.getAppClasses());
      options.set_app(false);
      options.set_search_dex_in_archives(true);
      options.set_process_dir(CollectionsKt.toList(var12.getSoot_process_dir()));
      options.set_output_format(var12.getOutput_format());
      var12.getSoot_output_dir().deleteDirectoryRecursively();
      var12.getSoot_output_dir().mkdirs();
      options.set_output_dir(var12.getSoot_output_dir().getAbsolutePath());
      options.set_keep_offset(false);
      options.set_keep_line_number(var12.getEnableLineNumbers());
      options.set_ignore_resolution_errors(true);
      var var17: java.lang.Boolean = var12.getForceAndroidJar();
      if (var17 != null) {
         if (var17) {
            options.set_force_android_jar(var12.getAndroidPlatformDir());
         } else {
            options.set_android_jars(var12.getAndroidPlatformDir());
         }

         logger.info(SootCtx::configure$lambda$24$lambda$23$lambda$22$lambda$21);
      }

      var17 = this.mainConfig.isAndroidScene();
      if (var17) {
         options.set_throw_analysis(3);
      }

      if (var12.getEnableOriginalNames()) {
         options.setPhaseOption("jb", "use-original-names:true");
      }

      options.set_debug(false);
      options.set_verbose(false);
      options.set_validate(false);
      this.configureCallGraph(options);
      PackManager.v().getPack("jb").add(new Transform("jb.rewriter", (new StringConcatRewriterTransform()) as Transformer));
      val var19: SaConfig = this.mainConfig.getSaConfig();
      if (var19 != null) {
         val var20: ISootInitializeHandler = var19.getSootConfig();
         if (var20 != null) {
            var20.configure(options);
         }
      }
   }

   public fun configureSootClassPath(options: Options) {
      val cp: SortedSet = CollectionsKt.toSortedSet(this.mainConfig.get_soot_classpath());
      Scene.v().setSootClassPath(null);
      val var10001: java.lang.Iterable = cp;
      val var10002: java.lang.String = File.pathSeparator;
      options.set_soot_classpath(CollectionsKt.joinToString$default(var10001, var10002, null, null, 0, null, null, 62, null));
   }

   public override fun configureAfterSceneInit(scene: Scene, options: Options) {
      this.configureSootClassPath(options);
   }

   public override fun configure(scene: Scene) {
      logger.info(SootCtx::configure$lambda$26);
      Scene.v().addBasicClass("java.lang.String", 3);
      Scene.v().addBasicClass("java.lang.StringLatin1", 3);
      Scene.v().addBasicClass("java.util.Arrays", 3);
      Scene.v().addBasicClass("java.lang.Math", 3);
      Scene.v().addBasicClass("java.lang.StringCoding", 3);
      val var10000: SaConfig = this.mainConfig.getSaConfig();
      if (var10000 != null) {
         val var2: ISootInitializeHandler = var10000.getSootConfig();
         if (var2 != null) {
            var2.configure(scene);
         }
      }
   }

   public override fun configure(main: Main) {
      main.autoSetOptions();
      val var10000: SaConfig = this.mainConfig.getSaConfig();
      if (var10000 != null) {
         val var2: ISootInitializeHandler = var10000.getSootConfig();
         if (var2 != null) {
            var2.configure(main);
         }
      }
   }

   public fun classesClassification(scene: Scene, locator: ProjectFileLocator?) {
      val st: PhaseIntervalTimer.Snapshot = if (this._classesClassificationTimer != null) this._classesClassificationTimer.start() else null;
      if (this.mainConfig.getSkipClass()) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         var findAny: Boolean = false;
         val autoAppClasses: PersistentSet = this.mainConfig.getAutoAppClasses();
         showClasses$default(this, scene, "Before classes classification: ", null, 4, null);
         val var10000: java.util.Iterator = Scene.v().getClasses().iterator();
         val hintAppend1: java.util.Iterator = var10000;

         while (hintAppend1.hasNext()) {
            val sc: SootClass = hintAppend1.next() as SootClass;
            if (!sc.isPhantom()) {
               val var16: ProjectFileLocator = this.getAutoAppClassesLocator();
               val var10001: ClassResInfo.Companion = ClassResInfo.Companion;
               val sourceFile: IResFile = var16.get(var10001.of(sc), NullWrapperFileGenerator.INSTANCE);
               var origAction: java.lang.String = null;
               if (!(autoAppClasses as java.util.Collection).isEmpty()) {
                  if (sourceFile == null || !this.mainConfig.getAutoAppSrcInZipScheme() && !sourceFile.isFileScheme()) {
                     if (sc.isApplicationClass()) {
                        sc.setLibraryClass();
                        origAction = "library";
                     }
                  } else {
                     findAny = true;
                     sc.setApplicationClass();
                     origAction = "application";
                  }
               }

               var var17: java.lang.String = origAction;
               if (origAction == null) {
                  var17 = if (sc.isApplicationClass()) "application" else (if (sc.isLibraryClass()) "library" else "phantom");
               }

               switch (SootCtx.WhenMappings.$EnumSwitchMapping$1[ScanFilter.getActionOf$default(
                     this.mainConfig.getScanFilter(),
                     (locator != null ? locator.get(ClassResInfo.Companion.of(sc), NullWrapperFileGenerator.INSTANCE) : null) != null
                        ? "(src exists) " + var17
                        : "(src not exists) " + var17,
                     sc,
                     null,
                     4,
                     null
                  )
                  .ordinal()]) {
                  case 1:
                     sc.setApplicationClass();
                     break;
                  case 2:
                     sc.setLibraryClass();
                  case 3:
                     break;
                  default:
                     throw new NoWhenBranchMatchedException();
               }
            }
         }

         val var18: Timer = this._classesClassificationTimer;
         if (this._classesClassificationTimer != null) {
            var18.stop(st);
         }

         if (!(autoAppClasses as java.util.Collection).isEmpty() && !findAny) {
            logger.error(SootCtx::classesClassification$lambda$27);
         }

         showClasses$default(this, scene, "After classes classification: ", null, 4, null);
      }
   }

   public fun Chain<SootClass>.activeBodyMethods(): Pair<Int, Int> {
      var active: java.lang.Iterable = `$this$activeBodyMethods` as java.lang.Iterable;
      var `$this$filterTo$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val var10000: java.util.List = (`element$iv$iv` as SootClass).getMethods();
         CollectionsKt.addAll(`$this$filterTo$iv$iv`, var10000);
      }

      active = `$this$filterTo$iv$iv` as java.util.List;
      `$this$filterTo$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         if (!(var22 as SootMethod).isAbstract()) {
            `$this$filterTo$iv$iv`.add(var22);
         }
      }

      val total: java.util.List = `$this$filterTo$iv$iv` as java.util.List;
      val var16: java.lang.Iterable = `$this$filterTo$iv$iv` as java.util.List;
      val `destination$iv$ivx`: java.util.Collection = new ArrayList();

      for (Object element$iv$ivx : $this$filter$iv) {
         if ((`element$iv$ivx` as SootMethod).hasActiveBody()) {
            `destination$iv$ivx`.add(`element$iv$ivx`);
         }
      }

      return TuplesKt.to((`destination$iv$ivx` as java.util.List).size(), total.size());
   }

   public fun Chain<SootClass>.show(): String {
      val var2: Pair = this.activeBodyMethods(`$this$show`);
      val active: Int = (var2.component1() as java.lang.Number).intValue();
      val total: Int = (var2.component2() as java.lang.Number).intValue();
      if (total == 0) {
         return "empty";
      } else {
         val var10000: Int = `$this$show`.size();
         val var6: Array<Any> = new Object[]{(float)active / (float)total};
         val var10002: java.lang.String = java.lang.String.format("%.2f", Arrays.copyOf(var6, var6.length));
         return "$var10000($total*$var10002)";
      }
   }

   public fun showClasses(scene: Scene, prefix: String = "", fx: (String) -> String = SootCtx::showClasses$lambda$31) {
      logger.info(SootCtx::showClasses$lambda$33$lambda$32);
   }

   public open fun loadClasses(scene: Scene, locator: ProjectFileLocator?) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:361)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:504)
      //   at org.jetbrains.java.decompiler.util.collections.ListStack.pop(ListStack.java:31)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.<init>(FunctionExprent.java:159)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processBlock(ExprProcessor.java:459)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:134)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc_w "scene"
      // 004: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 007: invokestatic soot/G.v ()Lsoot/G;
      // 00a: new cn/sast/framework/SourceLocatorPlus
      // 00d: dup
      // 00e: aload 0
      // 00f: getfield cn/sast/framework/SootCtx.mainConfig Lcn/sast/api/config/MainConfig;
      // 012: invokespecial cn/sast/framework/SourceLocatorPlus.<init> (Lcn/sast/api/config/MainConfig;)V
      // 015: checkcast soot/SourceLocator
      // 018: invokevirtual soot/G.set_SourceLocator (Lsoot/SourceLocator;)V
      // 01b: aload 0
      // 01c: invokevirtual cn/sast/framework/SootCtx.getCgAlgorithmProvider ()Lcn/sast/framework/CgAlgorithmProvider;
      // 01f: getstatic cn/sast/framework/SootCtx$WhenMappings.$EnumSwitchMapping$0 [I
      // 022: swap
      // 023: invokevirtual cn/sast/framework/CgAlgorithmProvider.ordinal ()I
      // 026: iaload
      // 027: tableswitch 53 1 2 21 24
      // 03c: goto 064
      // 03f: invokestatic soot/Scene.v ()Lsoot/Scene;
      // 042: ldc_w "java.lang.ClassLoader"
      // 045: bipush 3
      // 046: invokevirtual soot/Scene.addBasicClass (Ljava/lang/String;I)V
      // 049: invokestatic soot/Scene.v ()Lsoot/Scene;
      // 04c: ldc_w "java.lang.ref.Finalizer"
      // 04f: bipush 3
      // 050: invokevirtual soot/Scene.addBasicClass (Ljava/lang/String;I)V
      // 053: invokestatic qilin/core/PTAScene.v ()Lqilin/core/PTAScene;
      // 056: invokevirtual qilin/core/PTAScene.addBasicClasses ()V
      // 059: goto 064
      // 05c: new kotlin/NoWhenBranchMatchedException
      // 05f: dup
      // 060: invokespecial kotlin/NoWhenBranchMatchedException.<init> ()V
      // 063: athrow
      // 064: aload 0
      // 065: getfield cn/sast/framework/SootCtx.mainConfig Lcn/sast/api/config/MainConfig;
      // 068: invokevirtual cn/sast/api/config/MainConfig.getSkipClass ()Z
      // 06b: ifne 09e
      // 06e: aload 1
      // 06f: invokestatic cn/sast/framework/SootCtxKt.access$getExcludedPackages (Lsoot/Scene;)Ljava/util/LinkedList;
      // 072: astore 3
      // 073: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 076: aload 3
      // 077: invokedynamic invoke (Ljava/util/LinkedList;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$34 (Ljava/util/LinkedList;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 07c: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 081: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 084: aload 1
      // 085: aload 0
      // 086: invokedynamic invoke (Lsoot/Scene;Lcn/sast/framework/SootCtx;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$37 (Lsoot/Scene;Lcn/sast/framework/SootCtx;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 08b: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 090: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 093: aload 0
      // 094: invokedynamic invoke (Lcn/sast/framework/SootCtx;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$40 (Lcn/sast/framework/SootCtx;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 099: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 09e: invokestatic soot/options/Options.v ()Lsoot/options/Options;
      // 0a1: invokevirtual soot/options/Options.src_prec ()I
      // 0a4: bipush 4
      // 0a5: if_icmpne 100
      // 0a8: aload 0
      // 0a9: getfield cn/sast/framework/SootCtx.mainConfig Lcn/sast/api/config/MainConfig;
      // 0ac: invokevirtual cn/sast/api/config/MainConfig.getSunBootClassPath ()Ljava/lang/String;
      // 0af: astore 3
      // 0b0: aload 0
      // 0b1: getfield cn/sast/framework/SootCtx.mainConfig Lcn/sast/api/config/MainConfig;
      // 0b4: invokevirtual cn/sast/api/config/MainConfig.getJavaExtDirs ()Ljava/lang/String;
      // 0b7: astore 4
      // 0b9: aload 3
      // 0ba: checkcast java/lang/CharSequence
      // 0bd: astore 5
      // 0bf: aload 5
      // 0c1: ifnull 0ce
      // 0c4: aload 5
      // 0c6: invokeinterface java/lang/CharSequence.length ()I 1
      // 0cb: ifne 0d2
      // 0ce: bipush 1
      // 0cf: goto 0d3
      // 0d2: bipush 0
      // 0d3: aload 4
      // 0d5: checkcast java/lang/CharSequence
      // 0d8: astore 5
      // 0da: aload 5
      // 0dc: ifnull 0e9
      // 0df: aload 5
      // 0e1: invokeinterface java/lang/CharSequence.length ()I 1
      // 0e6: ifne 0ed
      // 0e9: bipush 1
      // 0ea: goto 0ee
      // 0ed: bipush 0
      // 0ee: ior
      // 0ef: ifeq 100
      // 0f2: new java/lang/IllegalStateException
      // 0f5: dup
      // 0f6: ldc_w "sunBootClassPath or javaExtDirs must not be null"
      // 0f9: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 0fc: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 0ff: athrow
      // 100: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 103: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$41 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 108: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 10d: aload 0
      // 10e: getfield cn/sast/framework/SootCtx.mainConfig Lcn/sast/api/config/MainConfig;
      // 111: invokevirtual cn/sast/api/config/MainConfig.getSkipClass ()Z
      // 114: ifne 3bc
      // 117: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 11a: invokestatic org/utbot/common/LoggingKt.info (Lmu/KLogger;)Lorg/utbot/common/LoggerWithLogMethod;
      // 11d: astore 3
      // 11e: ldc_w "Loading Necessary Classes..."
      // 121: astore 4
      // 123: bipush 0
      // 124: istore 6
      // 126: aload 3
      // 127: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 12a: new cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$1
      // 12d: dup
      // 12e: aload 4
      // 130: invokespecial cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$1.<init> (Ljava/lang/String;)V
      // 133: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 138: pop
      // 139: invokestatic java/time/LocalDateTime.now ()Ljava/time/LocalDateTime;
      // 13c: astore 7
      // 13e: bipush 0
      // 13f: istore 8
      // 141: new kotlin/jvm/internal/Ref$ObjectRef
      // 144: dup
      // 145: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
      // 148: astore 9
      // 14a: aload 9
      // 14c: getstatic org/utbot/common/Maybe.Companion Lorg/utbot/common/Maybe$Companion;
      // 14f: invokevirtual org/utbot/common/Maybe$Companion.empty ()Lorg/utbot/common/Maybe;
      // 152: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 155: nop
      // 156: aload 9
      // 158: astore 18
      // 15a: bipush 0
      // 15b: istore 10
      // 15d: aload 0
      // 15e: getfield cn/sast/framework/SootCtx._loadClassesTimer Lcn/sast/api/util/Timer;
      // 161: dup
      // 162: ifnull 1aa
      // 165: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 168: astore 11
      // 16a: bipush 0
      // 16b: istore 12
      // 16d: aload 11
      // 16f: ifnonnull 17e
      // 172: bipush 0
      // 173: istore 13
      // 175: aload 1
      // 176: bipush 0
      // 177: invokevirtual soot/Scene.loadNecessaryClasses (Z)V
      // 17a: nop
      // 17b: goto 1ac
      // 17e: aload 11
      // 180: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
      // 183: astore 14
      // 185: nop
      // 186: bipush 0
      // 187: istore 13
      // 189: aload 1
      // 18a: bipush 0
      // 18b: invokevirtual soot/Scene.loadNecessaryClasses (Z)V
      // 18e: nop
      // 18f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 192: astore 13
      // 194: aload 11
      // 196: aload 14
      // 198: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 19b: goto 1ac
      // 19e: astore 15
      // 1a0: aload 11
      // 1a2: aload 14
      // 1a4: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 1a7: aload 15
      // 1a9: athrow
      // 1aa: pop
      // 1ab: nop
      // 1ac: aload 0
      // 1ad: getfield cn/sast/framework/SootCtx.monitor Lcn/sast/api/util/IMonitor;
      // 1b0: dup
      // 1b1: ifnull 1bf
      // 1b4: ldc_w "classesClassification"
      // 1b7: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
      // 1bc: goto 1c1
      // 1bf: pop
      // 1c0: aconst_null
      // 1c1: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 1c4: astore 16
      // 1c6: bipush 0
      // 1c7: istore 11
      // 1c9: aload 16
      // 1cb: ifnonnull 1db
      // 1ce: bipush 0
      // 1cf: istore 12
      // 1d1: aload 0
      // 1d2: aload 1
      // 1d3: aload 2
      // 1d4: invokevirtual cn/sast/framework/SootCtx.classesClassification (Lsoot/Scene;Lcn/sast/framework/report/ProjectFileLocator;)V
      // 1d7: nop
      // 1d8: goto 208
      // 1db: aload 16
      // 1dd: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
      // 1e0: astore 13
      // 1e2: nop
      // 1e3: bipush 0
      // 1e4: istore 12
      // 1e6: aload 0
      // 1e7: aload 1
      // 1e8: aload 2
      // 1e9: invokevirtual cn/sast/framework/SootCtx.classesClassification (Lsoot/Scene;Lcn/sast/framework/report/ProjectFileLocator;)V
      // 1ec: nop
      // 1ed: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 1f0: astore 14
      // 1f2: aload 16
      // 1f4: aload 13
      // 1f6: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 1f9: goto 208
      // 1fc: astore 15
      // 1fe: aload 16
      // 200: aload 13
      // 202: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 205: aload 15
      // 207: athrow
      // 208: aload 0
      // 209: getfield cn/sast/framework/SootCtx._loadClassesTimer Lcn/sast/api/util/Timer;
      // 20c: dup
      // 20d: ifnull 2c7
      // 210: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 213: astore 11
      // 215: bipush 0
      // 216: istore 12
      // 218: aload 11
      // 21a: ifnonnull 262
      // 21d: bipush 0
      // 21e: istore 13
      // 220: aload 1
      // 221: invokevirtual soot/Scene.getApplicationClasses ()Lsoot/util/Chain;
      // 224: invokeinterface soot/util/Chain.iterator ()Ljava/util/Iterator; 1
      // 229: dup
      // 22a: ldc_w "iterator(...)"
      // 22d: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 230: astore 14
      // 232: aload 14
      // 234: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 239: ifeq 25e
      // 23c: aload 14
      // 23e: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 243: checkcast soot/SootClass
      // 246: astore 15
      // 248: aload 15
      // 24a: invokevirtual soot/SootClass.isPhantom ()Z
      // 24d: ifne 232
      // 250: aload 1
      // 251: aload 15
      // 253: invokevirtual soot/SootClass.getName ()Ljava/lang/String;
      // 256: bipush 3
      // 257: invokevirtual soot/Scene.loadClass (Ljava/lang/String;I)Lsoot/SootClass;
      // 25a: pop
      // 25b: goto 232
      // 25e: nop
      // 25f: goto 2c9
      // 262: aload 11
      // 264: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
      // 267: astore 17
      // 269: nop
      // 26a: bipush 0
      // 26b: istore 13
      // 26d: aload 1
      // 26e: invokevirtual soot/Scene.getApplicationClasses ()Lsoot/util/Chain;
      // 271: invokeinterface soot/util/Chain.iterator ()Ljava/util/Iterator; 1
      // 276: dup
      // 277: ldc_w "iterator(...)"
      // 27a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 27d: astore 14
      // 27f: aload 14
      // 281: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 286: ifeq 2ab
      // 289: aload 14
      // 28b: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 290: checkcast soot/SootClass
      // 293: astore 15
      // 295: aload 15
      // 297: invokevirtual soot/SootClass.isPhantom ()Z
      // 29a: ifne 27f
      // 29d: aload 1
      // 29e: aload 15
      // 2a0: invokevirtual soot/SootClass.getName ()Ljava/lang/String;
      // 2a3: bipush 3
      // 2a4: invokevirtual soot/Scene.loadClass (Ljava/lang/String;I)Lsoot/SootClass;
      // 2a7: pop
      // 2a8: goto 27f
      // 2ab: nop
      // 2ac: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 2af: astore 13
      // 2b1: aload 11
      // 2b3: aload 17
      // 2b5: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 2b8: goto 2c9
      // 2bb: astore 14
      // 2bd: aload 11
      // 2bf: aload 17
      // 2c1: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 2c4: aload 14
      // 2c6: athrow
      // 2c7: pop
      // 2c8: nop
      // 2c9: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 2cc: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$46$lambda$45 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 2d1: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 2d6: nop
      // 2d7: aload 18
      // 2d9: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 2dc: astore 19
      // 2de: new org/utbot/common/Maybe
      // 2e1: dup
      // 2e2: aload 19
      // 2e4: invokespecial org/utbot/common/Maybe.<init> (Ljava/lang/Object;)V
      // 2e7: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 2ea: aload 9
      // 2ec: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 2ef: checkcast org/utbot/common/Maybe
      // 2f2: invokevirtual org/utbot/common/Maybe.getOrThrow ()Ljava/lang/Object;
      // 2f5: astore 10
      // 2f7: nop
      // 2f8: aload 9
      // 2fa: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 2fd: checkcast org/utbot/common/Maybe
      // 300: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
      // 303: ifeq 320
      // 306: aload 3
      // 307: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 30a: new cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$2
      // 30d: dup
      // 30e: aload 7
      // 310: aload 4
      // 312: aload 9
      // 314: invokespecial cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$2.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
      // 317: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 31c: pop
      // 31d: goto 335
      // 320: aload 3
      // 321: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 324: new cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$3
      // 327: dup
      // 328: aload 7
      // 32a: aload 4
      // 32c: invokespecial cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$3.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
      // 32f: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 334: pop
      // 335: goto 39e
      // 338: astore 11
      // 33a: aload 3
      // 33b: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 33e: new cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$4
      // 341: dup
      // 342: aload 7
      // 344: aload 4
      // 346: aload 11
      // 348: invokespecial cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$4.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Ljava/lang/Throwable;)V
      // 34b: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 350: pop
      // 351: bipush 1
      // 352: istore 8
      // 354: aload 11
      // 356: athrow
      // 357: astore 11
      // 359: iload 8
      // 35b: ifne 39b
      // 35e: aload 9
      // 360: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 363: checkcast org/utbot/common/Maybe
      // 366: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
      // 369: ifeq 386
      // 36c: aload 3
      // 36d: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 370: new cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$5
      // 373: dup
      // 374: aload 7
      // 376: aload 4
      // 378: aload 9
      // 37a: invokespecial cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$5.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
      // 37d: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 382: pop
      // 383: goto 39b
      // 386: aload 3
      // 387: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 38a: new cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$6
      // 38d: dup
      // 38e: aload 7
      // 390: aload 4
      // 392: invokespecial cn/sast/framework/SootCtx$loadClasses$$inlined$bracket$default$6.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
      // 395: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 39a: pop
      // 39b: aload 11
      // 39d: athrow
      // 39e: aload 0
      // 39f: aload 1
      // 3a0: aconst_null
      // 3a1: new cn/sast/framework/SootCtx$loadClasses$6
      // 3a4: dup
      // 3a5: getstatic com/github/ajalt/mordant/rendering/Theme.Companion Lcom/github/ajalt/mordant/rendering/Theme$Companion;
      // 3a8: invokevirtual com/github/ajalt/mordant/rendering/Theme$Companion.getDefault ()Lcom/github/ajalt/mordant/rendering/Theme;
      // 3ab: invokevirtual com/github/ajalt/mordant/rendering/Theme.getWarning ()Lcom/github/ajalt/mordant/rendering/TextStyle;
      // 3ae: invokespecial cn/sast/framework/SootCtx$loadClasses$6.<init> (Ljava/lang/Object;)V
      // 3b1: checkcast kotlin/jvm/functions/Function1
      // 3b4: bipush 2
      // 3b5: aconst_null
      // 3b6: invokestatic cn/sast/framework/SootCtx.showClasses$default (Lcn/sast/framework/SootCtx;Lsoot/Scene;Ljava/lang/String;Lkotlin/jvm/functions/Function1;ILjava/lang/Object;)V
      // 3b9: goto 3d6
      // 3bc: aload 1
      // 3bd: invokevirtual soot/Scene.loadBasicClasses ()V
      // 3c0: aload 1
      // 3c1: invokevirtual soot/Scene.loadDynamicClasses ()V
      // 3c4: aload 1
      // 3c5: invokevirtual soot/Scene.doneResolving ()Z
      // 3c8: pop
      // 3c9: getstatic cn/sast/framework/graph/CGUtils.INSTANCE Lcn/sast/framework/graph/CGUtils;
      // 3cc: aload 1
      // 3cd: aconst_null
      // 3ce: aconst_null
      // 3cf: bipush 6
      // 3d1: aconst_null
      // 3d2: invokestatic cn/sast/framework/graph/CGUtils.createDummyMain$default (Lcn/sast/framework/graph/CGUtils;Lsoot/Scene;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Lsoot/SootClass;
      // 3d5: pop
      // 3d6: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 3d9: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$47 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 3de: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 3e3: getstatic cn/sast/framework/graph/CGUtils.INSTANCE Lcn/sast/framework/graph/CGUtils;
      // 3e6: aload 1
      // 3e7: invokevirtual cn/sast/framework/graph/CGUtils.removeLargeClasses (Lsoot/Scene;)V
      // 3ea: getstatic cn/sast/framework/graph/CGUtils.INSTANCE Lcn/sast/framework/graph/CGUtils;
      // 3ed: invokevirtual cn/sast/framework/graph/CGUtils.makeSpuriousMethodFromInvokeExpr ()V
      // 3f0: invokestatic soot/PackManager.v ()Lsoot/PackManager;
      // 3f3: ldc_w "wjpp"
      // 3f6: invokevirtual soot/PackManager.getPack (Ljava/lang/String;)Lsoot/Pack;
      // 3f9: invokevirtual soot/Pack.apply ()V
      // 3fc: new cn/sast/framework/rewrite/LibraryClassPatcher
      // 3ff: dup
      // 400: invokespecial cn/sast/framework/rewrite/LibraryClassPatcher.<init> ()V
      // 403: invokevirtual cn/sast/framework/rewrite/LibraryClassPatcher.patchLibraries ()V
      // 406: aload 0
      // 407: getfield cn/sast/framework/SootCtx.monitor Lcn/sast/api/util/IMonitor;
      // 40a: dup
      // 40b: ifnull 430
      // 40e: invokeinterface cn/sast/api/util/IMonitor.getProjectMetrics ()Lcn/sast/api/report/ProjectMetrics; 1
      // 413: dup
      // 414: ifnull 430
      // 417: aload 1
      // 418: invokevirtual soot/Scene.getApplicationClasses ()Lsoot/util/Chain;
      // 41b: dup
      // 41c: ldc_w "getApplicationClasses(...)"
      // 41f: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 422: checkcast java/util/Collection
      // 425: invokeinterface java/util/Collection.size ()I 1
      // 42a: invokevirtual cn/sast/api/report/ProjectMetrics.setApplicationClasses (I)V
      // 42d: goto 431
      // 430: pop
      // 431: aload 0
      // 432: getfield cn/sast/framework/SootCtx.monitor Lcn/sast/api/util/IMonitor;
      // 435: dup
      // 436: ifnull 45b
      // 439: invokeinterface cn/sast/api/util/IMonitor.getProjectMetrics ()Lcn/sast/api/report/ProjectMetrics; 1
      // 43e: dup
      // 43f: ifnull 45b
      // 442: aload 1
      // 443: invokevirtual soot/Scene.getLibraryClasses ()Lsoot/util/Chain;
      // 446: dup
      // 447: ldc_w "getLibraryClasses(...)"
      // 44a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 44d: checkcast java/util/Collection
      // 450: invokeinterface java/util/Collection.size ()I 1
      // 455: invokevirtual cn/sast/api/report/ProjectMetrics.setLibraryClasses (I)V
      // 458: goto 45c
      // 45b: pop
      // 45c: aload 0
      // 45d: getfield cn/sast/framework/SootCtx.monitor Lcn/sast/api/util/IMonitor;
      // 460: dup
      // 461: ifnull 486
      // 464: invokeinterface cn/sast/api/util/IMonitor.getProjectMetrics ()Lcn/sast/api/report/ProjectMetrics; 1
      // 469: dup
      // 46a: ifnull 486
      // 46d: aload 1
      // 46e: invokevirtual soot/Scene.getPhantomClasses ()Lsoot/util/Chain;
      // 471: dup
      // 472: ldc_w "getPhantomClasses(...)"
      // 475: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 478: checkcast java/util/Collection
      // 47b: invokeinterface java/util/Collection.size ()I 1
      // 480: invokevirtual cn/sast/api/report/ProjectMetrics.setPhantomClasses (I)V
      // 483: goto 487
      // 486: pop
      // 487: aload 0
      // 488: getfield cn/sast/framework/SootCtx.mainConfig Lcn/sast/api/config/MainConfig;
      // 48b: invokevirtual cn/sast/api/config/MainConfig.getIncrementAnalyze ()Lcn/sast/api/incremental/IncrementalAnalyze;
      // 48e: astore 4
      // 490: aload 4
      // 492: instanceof cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles
      // 495: ifeq 4a0
      // 498: aload 4
      // 49a: checkcast cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles
      // 49d: goto 4a1
      // 4a0: aconst_null
      // 4a1: dup
      // 4a2: ifnull 4ad
      // 4a5: aload 1
      // 4a6: aload 2
      // 4a7: invokevirtual cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.update (Lsoot/Scene;Lcn/sast/framework/report/ProjectFileLocator;)V
      // 4aa: goto 4ae
      // 4ad: pop
      // 4ae: getstatic cn/sast/framework/SootCtx.logger Lmu/KLogger;
      // 4b1: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/SootCtx.loadClasses$lambda$48 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 4b6: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 4bb: return
   }

   public open fun configureSoot() {
      Companion.restAll();
      this.mainConfig.validate();
      val jdkInfo: JdkInfo = JdkInfoService.INSTANCE.provide();
      val options: Options = Options.v();
      this.configure(options);
      if (Companion.getInstance_soot_Scene() != null) {
         throw new IllegalStateException("Soot should not be initialized in clinit or init. check your plugins".toString());
      } else {
         val scene: Scene = Scene.v();
         this.configure(scene);
         this.configureAfterSceneInit(scene, options);
         val main: Main = Main.v();
         this.configure(main);
      }
   }

   public open fun constructSoot(locator: ProjectFileLocator? = null) {
      val scene: Scene = Scene.v();
      this.loadClasses(scene, locator);
   }

   public open fun releaseCallGraph(scene: Scene, g: G) {
      scene.releaseCallGraph();
      scene.releasePointsToAnalysis();
      scene.releaseReachableMethods();
      g.resetSpark();
   }

   public open fun releaseCallGraph() {
      val var10001: Scene = Scene.v();
      val var10002: G = G.v();
      this.releaseCallGraph(var10001, var10002);
   }

   public open fun onBeforeCallGraphConstruction() {
      val var10000: SaConfig = this.mainConfig.getSaConfig();
      if (var10000 != null) {
         val var1: ISootInitializeHandler = var10000.getSootConfig();
         if (var1 != null) {
            val var10001: Scene = Scene.v();
            val var10002: Options = Options.v();
            var1.onBeforeCallGraphConstruction(var10001, var10002);
         }
      }
   }

   public open fun onAfterCallGraphConstruction() {
      label63: {
         val var10000: SaConfig = this.mainConfig.getSaConfig();
         if (var10000 != null) {
            val var16: ISootInitializeHandler = var10000.getSootConfig();
            if (var16 != null) {
               val var10001: CallGraph = this.getCallGraph();
               val var10002: Scene = Scene.v();
               val var10003: Options = Options.v();
               var16.onAfterCallGraphConstruction(var10001, var10002, var10003);
            }
         }

         val `$this$bracket_u24default$iv`: LoggerWithLogMethod = LoggingKt.info(logger);
         val `msg$iv`: java.lang.String = "Rewrite soot scene";
         `$this$bracket_u24default$iv`.getLogMethod().invoke(new SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$1("Rewrite soot scene"));
         val `startTime$iv`: LocalDateTime = LocalDateTime.now();
         var `alreadyLogged$iv`: Boolean = false;
         val `res$iv`: ObjectRef = new ObjectRef();
         `res$iv`.element = Maybe.Companion.empty();

         try {
            try {
               ;
            } catch (var11: java.lang.Throwable) {
               `$this$bracket_u24default$iv`.getLogMethod()
                  .invoke(new SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$4(`startTime$iv`, `msg$iv`, var11));
               `alreadyLogged$iv` = true;
               throw var11;
            }
         } catch (var12: java.lang.Throwable) {
            if (!`alreadyLogged$iv`) {
               if ((`res$iv`.element as Maybe).getHasValue()) {
                  `$this$bracket_u24default$iv`.getLogMethod()
                     .invoke(new SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$5(`startTime$iv`, "Rewrite soot scene", `res$iv`));
               } else {
                  `$this$bracket_u24default$iv`.getLogMethod()
                     .invoke(new SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$6(`startTime$iv`, "Rewrite soot scene"));
               }
            }
         }

         if ((`res$iv`.element as Maybe).getHasValue()) {
            `$this$bracket_u24default$iv`.getLogMethod()
               .invoke(new SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$2(`startTime$iv`, "Rewrite soot scene", `res$iv`));
         } else {
            `$this$bracket_u24default$iv`.getLogMethod()
               .invoke(new SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$3(`startTime$iv`, "Rewrite soot scene"));
         }
      }
   }

   override fun onBeforeCallGraphConstruction(scene: Scene, options: Options) {
      ISootInitializeHandler.DefaultImpls.onBeforeCallGraphConstruction(this, scene, options);
   }

   override fun onAfterCallGraphConstruction(cg: CallGraph, scene: Scene, options: Options) {
      ISootInitializeHandler.DefaultImpls.onAfterCallGraphConstruction(this, cg, scene, options);
   }

   @JvmStatic
   fun `configureCallGraph$lambda$2$lambda$0`(`$callGraphAlgorithm`: java.lang.String, `$appOnly`: Boolean): Any {
      return "using cg algorithm: $`$callGraphAlgorithm` with $`$appOnly`";
   }

   @JvmStatic
   fun `constructCallGraph$lambda$8$lambda$4`(): Any {
      return "Before build CG: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `constructCallGraph$lambda$8$lambda$6`(`$memoryWatcher`: MemoryWatcher): Any {
      return `$memoryWatcher`.toString();
   }

   @JvmStatic
   fun `constructCallGraph$lambda$8$lambda$7`(): Any {
      return "After build CG: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `showPta$lambda$11`(): Any {
      return "PointsToAnalysis of scene is DumbPointerAnalysis!!!";
   }

   @JvmStatic
   fun `showPta$lambda$12`(): Any {
      return "PointsToAnalysis of scene is ${Scene.v().getPointsToAnalysis().getClass().getSimpleName()}";
   }

   @JvmStatic
   fun `showPta$lambda$13`(): Any {
      return "Call Graph has been constructed with ${Scene.v().getCallGraph().size()} edges.";
   }

   @JvmStatic
   fun `findClassesInnerJarUnderAutoAppClassPath$lambda$17`(`$jar`: IResFile): Any {
      return "Bad archive file: $`$jar`";
   }

   @JvmStatic
   fun `findClassesInnerJarUnderAutoAppClassPath$lambda$18`(`$jar`: IResFile): Any {
      return "unknown scheme: ${`$jar`.getUri()}";
   }

   @JvmStatic
   fun `configure$lambda$19`(): Any {
      return "\n\n!!! There are some errors in source code compilation !!!\n\n";
   }

   @JvmStatic
   fun `configure$lambda$24$lambda$23$lambda$22$lambda$21`(`$this_with`: MainConfig): Any {
      return "android platform dir: ${`$this_with`.getAndroidPlatformDir()}";
   }

   @JvmStatic
   fun `autoAppClassesLocator_delegate$lambda$25`(`this$0`: SootCtx): ProjectFileLocator {
      return new ProjectFileLocator(
         `this$0`.mainConfig.getMonitor(),
         `this$0`.mainConfig.getAutoAppClasses() as java.util.Set,
         null,
         `this$0`.mainConfig.getAutoAppTraverseMode(),
         false,
         16,
         null
      );
   }

   @JvmStatic
   fun `configure$lambda$26`(): Any {
      return "Initializing Soot Scene...";
   }

   @JvmStatic
   fun `classesClassification$lambda$27`(`$autoAppClasses`: PersistentSet, `$hintAppend1`: java.lang.String): Any {
      return "\n\n\nSince $`$autoAppClasses` has no source files corresponding to the classes, the classifier is unable to classify based on the location of the source code\n$`$hintAppend1`\n\n";
   }

   @JvmStatic
   fun `showClasses$lambda$31`(it: java.lang.String): java.lang.String {
      return it;
   }

   @JvmStatic
   fun `showClasses$lambda$33$lambda$32`(
      `$fx`: Function1,
      `$prefix`: java.lang.String,
      `this$0`: SootCtx,
      `$applicationClasses`: Chain,
      `$libraryClasses`: Chain,
      `$phantomClasses`: Chain,
      `$classes`: Chain
   ): Any {
      val var10002: java.lang.String = `this$0`.show(`$applicationClasses`);
      val var10003: java.lang.String = `this$0`.show(`$libraryClasses`);
      val var10004: java.lang.String = `this$0`.show(`$phantomClasses`);
      return `$fx`.invoke(
         "$`$prefix`applicationClasses: $var10002. libraryClasses: $var10003. phantomClasses: $var10004. classes: ${`this$0`.show(`$classes`)}"
      );
   }

   @JvmStatic
   fun `loadClasses$lambda$34`(`$exclude`: LinkedList): Any {
      return "\nsoot exclude $`$exclude`";
   }

   @JvmStatic
   fun `loadClasses$lambda$37$lambda$36`(`this$0`: SootCtx, p: java.lang.String): java.lang.CharSequence {
      val var10000: MainConfig = `this$0`.mainConfig;
      return "\t${var10000.tryGetRelativePathFromAbsolutePath(p).getIdentifier()}";
   }

   @JvmStatic
   fun `loadClasses$lambda$37`(`$scene`: Scene, `this$0`: SootCtx): Any {
      val var10000: java.util.Collection = CollectionsKt.listOf("*PROCESS_DIRS");
      val var10001: java.lang.String = `$scene`.getSootClassPath();
      val var6: java.lang.Iterable = StringsKt.split$default(var10001, new java.lang.String[]{File.pathSeparator}, false, 0, 6, null);
      val var10002: java.util.List = Options.v().process_dir();
      return "\nsoot classpath:\n ${CollectionsKt.joinToString$default(
         CollectionsKt.sortedWith(
            CollectionsKt.plus(var10000, CollectionsKt.minus(var6, CollectionsKt.toSet(var10002))), StringComparator
         ),
         "\n",
         null,
         null,
         0,
         null,
         SootCtx::loadClasses$lambda$37$lambda$36,
         30,
         null
      )}\n";
   }

   @JvmStatic
   fun `loadClasses$lambda$40$lambda$39`(`this$0`: SootCtx, p: java.lang.String): java.lang.CharSequence {
      val var10000: MainConfig = `this$0`.mainConfig;
      return "\t${var10000.tryGetRelativePathFromAbsolutePath(p).getIdentifier()}";
   }

   @JvmStatic
   fun `loadClasses$lambda$40`(`this$0`: SootCtx): Any {
      val var10000: java.util.List = Options.v().process_dir();
      return "\nsoot process_dir:\n ${CollectionsKt.joinToString$default(
         CollectionsKt.sortedWith(var10000, StringComparator),
         "\n",
         null,
         null,
         0,
         null,
         SootCtx::loadClasses$lambda$40$lambda$39,
         30,
         null
      )}\n";
   }

   @JvmStatic
   fun `loadClasses$lambda$41`(): Any {
      return "Before Loading Classes: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `loadClasses$lambda$46$lambda$45`(): Any {
      return "Load classes done";
   }

   @JvmStatic
   fun `loadClasses$lambda$47`(): Any {
      return "After Loading Classes: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `loadClasses$lambda$48`(): Any {
      return "After Rewrite Classes: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
  fun `logger$lambda$51`(): Unit {
      return Unit.INSTANCE;
  }

   @JvmStatic
   fun `SootCtx$constructCallGraph$$inlined$bracket$default$1`(`$msg`: java.lang.String): Any {
      return "Started: ${`$msg`}";
   }

   @JvmStatic
   fun `SootCtx$constructCallGraph$$inlined$bracket$default$2`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `SootCtx$constructCallGraph$$inlined$bracket$default$3`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   @JvmStatic
   fun `SootCtx$constructCallGraph$$inlined$bracket$default$4`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$t`: java.lang.Throwable
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} :: EXCEPTION :: ";
   }

   @JvmStatic
   fun `SootCtx$constructCallGraph$$inlined$bracket$default$5`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `SootCtx$constructCallGraph$$inlined$bracket$default$6`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   @JvmStatic
   fun `SootCtx$loadClasses$$inlined$bracket$default$1`(`$msg`: java.lang.String): Any {
      return "Started: ${`$msg`}";
   }

   @JvmStatic
   fun `SootCtx$loadClasses$$inlined$bracket$default$2`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `SootCtx$loadClasses$$inlined$bracket$default$3`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   @JvmStatic
   fun `SootCtx$loadClasses$$inlined$bracket$default$4`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$t`: java.lang.Throwable
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} :: EXCEPTION :: ";
   }

   @JvmStatic
   fun `SootCtx$loadClasses$$inlined$bracket$default$5`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `SootCtx$loadClasses$$inlined$bracket$default$6`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   @JvmStatic
   fun `SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$1`(`$msg`: java.lang.String): Any {
      return "Started: ${`$msg`}";
   }

   @JvmStatic
   fun `SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$2`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$3`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   @JvmStatic
   fun `SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$4`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$t`: java.lang.Throwable
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} :: EXCEPTION :: ";
   }

   @JvmStatic
   fun `SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$5`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `SootCtx$onAfterCallGraphConstruction$$inlined$bracket$default$6`(
      `$startTime`: LocalDateTime,
      `$msg`: java.lang.String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   public companion object {
      private final val logger: KLogger

      public final val instance_soot_Scene: Scene?
         public final get() {
            val it: Field = Singletons.class.getDeclaredField("instance_soot_Scene");
            it.setAccessible(true);
            val var3: Any = it.get(G.v());
            return var3 as? Scene;
         }


      public fun restAll() {
         G.reset();
         PTAScene.reset();
      }
   }
}
