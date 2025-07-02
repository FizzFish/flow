package cn.sast.framework.entries.apk

import cn.sast.api.config.MainConfig
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import cn.sast.framework.entries.utils.PhantomValueForType
import java.io.IOException
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import org.xmlpull.v1.XmlPullParserException
import soot.Body
import soot.Local
import soot.LocalGenerator
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.Value
import soot.jimple.infoflow.InfoflowConfiguration.SootIntegrationMode
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.SetupApplication
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackAnalyzer
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.IccConfiguration
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointCreator
import soot.jimple.infoflow.android.entryPointCreators.components.ComponentEntryPointCollection
import soot.jimple.infoflow.handlers.PreAnalysisHandler
import soot.jimple.infoflow.methodSummary.data.provider.IMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.data.summary.ClassMethodSummaries
import soot.jimple.infoflow.methodSummary.taintWrappers.SummaryTaintWrapper
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider

public class ApkLifeCycleComponent(targetAPKFile: String,
      androidPlatformDir: String,
      oneComponentAtATime: Boolean = true,
      ignoreFlowsInSystemPackages: Boolean = false,
      enableCallbacks: Boolean = true,
      callbacksFile: String = "",
      callbackAnalyzer: CallbackAnalyzerType = CallbackAnalyzerType.Default,
      filterThreadCallbacks: Boolean = true,
      maxCallbacksPerComponent: Int = 100,
      callbackAnalysisTimeout: Int = 0,
      maxCallbackAnalysisDepth: Int = -1,
      serializeCallbacks: Boolean = false,
      iccModel: String? = null,
      iccResultsPurify: Boolean = true
   ) :
   IEntryPointProvider {
   public final var targetAPKFile: String
      internal set

   public final var androidPlatformDir: String
      internal set

   public final val oneComponentAtATime: Boolean

   public final var ignoreFlowsInSystemPackages: Boolean
      internal set

   public final val enableCallbacks: Boolean
   public final val callbacksFile: String

   public final var callbackAnalyzer: CallbackAnalyzerType
      internal set

   public final var filterThreadCallbacks: Boolean
      internal set

   public final val maxCallbacksPerComponent: Int
   public final val callbackAnalysisTimeout: Int
   public final val maxCallbackAnalysisDepth: Int
   public final val serializeCallbacks: Boolean

   public final var iccModel: String?
      internal set

   public final val iccResultsPurify: Boolean

   public final var taintWrapper: SummaryTaintWrapper?
      internal set

   public final val config: InfoflowAndroidConfiguration
      public final get() {
         return this.config$delegate.getValue() as InfoflowAndroidConfiguration;
      }


   private final val delegateSetupApplication: cn.sast.framework.entries.apk.ApkLifeCycleComponent.WSetupApplication
      private final get() {
         return this.delegateSetupApplication$delegate.getValue() as ApkLifeCycleComponent.WSetupApplication;
      }


   public open val iterator: Flow<AnalyzeTask>
      public open get() {
         return FlowKt.flow(
            (
               new Function2<FlowCollector<? super IEntryPointProvider.AnalyzeTask>, Continuation<? super Unit>, Object>(this, null) {
                  Object L$1;
                  long J$0;
                  long J$1;
                  int label;

                  {
                     super(2, `$completionx`);
                     this.this$0 = `$receiver`;
                  }

                  // $VF: Handled exception range with multiple entry points by splitting it
                  // $VF: Duplicated exception handlers to handle obfuscated exceptions
                  // $VF: Irreducible bytecode was duplicated to produce valid code
                  public final Object invokeSuspend(Object $result) {
                     var callbackDurationTotal: Long;
                     label119: {
                        label103: {
                           val var13: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           var `$this$flow`: FlowCollector;
                           var callbackDuration: Long;
                           var callbackDurationSeconds: ArrayList;
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 `$this$flow` = this.L$0 as FlowCollector;

                                 try {
                                    ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).parseAppResources();
                                 } catch (var24: IOException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Parse app resource failed", var24);
                                    throw new RuntimeException("Parse app resource failed", var24);
                                 } catch (var25: XmlPullParserException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Parse app resource failed", var25 as java.lang.Throwable);
                                    throw new RuntimeException("Parse app resource failed", var25 as java.lang.Throwable);
                                 }

                                 val entrypointClasses: java.util.Set = ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0)
                                    .getEntrypointClasses();
                                 if (entrypointClasses == null || entrypointClasses.isEmpty()) {
                                    ApkLifeCycleComponent.access$getLogger$cp().warn("No entry entrypoint classes");
                                    return Unit.INSTANCE;
                                 }

                                 this.this$0.injectStubDroidHierarchy(ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0));
                                 callbackDuration = System.nanoTime();
                                 callbackDurationTotal = 0L;

                                 try {
                                    if (this.this$0.getOneComponentAtATime()) {
                                       callbackDurationSeconds = new ArrayList(entrypointClasses);
                                       break;
                                    }
                                 } catch (var28: IOException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var28.getMessage()}", var28);
                                    throw new RuntimeException("Call graph construction failed", var28);
                                 } catch (var29: XmlPullParserException) {
                                    ApkLifeCycleComponent.access$getLogger$cp()
                                       .error("Call graph construction failed: ${var29.getMessage()}", var29 as java.lang.Throwable);
                                    throw new RuntimeException("Call graph construction failed", var29 as java.lang.Throwable);
                                 }

                                 var var34: ApkLifeCycleComponent;
                                 try {
                                    ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).calculateCallbacks(null, null);
                                    callbackDurationTotal += System.nanoTime() - callbackDuration;
                                    val var32: SootMethod = ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).getEntryPoint();
                                    var34 = this.this$0;
                                    val var10002: java.lang.String = "(mixed dummy main: ${ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0)
                                       .getEntryPoint()} of components: $entrypointClasses)";
                                    val var10003: java.util.Set = SetsKt.setOf(var32);
                                    val var10004: Continuation = this as Continuation;
                                    this.J$0 = callbackDurationTotal;
                                    this.label = 2;
                                    var34 = (ApkLifeCycleComponent)var34.emit(`$this$flow`, var10002, var10003, var10004);
                                 } catch (var18: IOException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var18.getMessage()}", var18);
                                    throw new RuntimeException("Call graph construction failed", var18);
                                 } catch (var19: XmlPullParserException) {
                                    ApkLifeCycleComponent.access$getLogger$cp()
                                       .error("Call graph construction failed: ${var19.getMessage()}", var19 as java.lang.Throwable);
                                    throw new RuntimeException("Call graph construction failed", var19 as java.lang.Throwable);
                                 }

                                 if (var34 === var13) {
                                    return var13;
                                 }
                                 break label103;
                              case 1:
                                 callbackDurationTotal = this.J$1;
                                 callbackDuration = this.J$0;
                                 callbackDurationSeconds = this.L$1 as ArrayList;
                                 `$this$flow` = this.L$0 as FlowCollector;

                                 try {
                                    ResultKt.throwOnFailure(`$result`);
                                 } catch (var22: IOException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var22.getMessage()}", var22);
                                    throw new RuntimeException("Call graph construction failed", var22);
                                 } catch (var23: XmlPullParserException) {
                                    ApkLifeCycleComponent.access$getLogger$cp()
                                       .error("Call graph construction failed: ${var23.getMessage()}", var23 as java.lang.Throwable);
                                    throw new RuntimeException("Call graph construction failed", var23 as java.lang.Throwable);
                                 }

                                 try {
                                    ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).clearCallBackCache();
                                    break;
                                 } catch (var30: IOException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var30.getMessage()}", var30);
                                    throw new RuntimeException("Call graph construction failed", var30);
                                 } catch (var31: XmlPullParserException) {
                                    ApkLifeCycleComponent.access$getLogger$cp()
                                       .error("Call graph construction failed: ${var31.getMessage()}", var31 as java.lang.Throwable);
                                    throw new RuntimeException("Call graph construction failed", var31 as java.lang.Throwable);
                                 }
                              case 2:
                                 callbackDurationTotal = this.J$0;

                                 try {
                                    ResultKt.throwOnFailure(`$result`);
                                    break label103;
                                 } catch (var16: IOException) {
                                    ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var16.getMessage()}", var16);
                                    throw new RuntimeException("Call graph construction failed", var16);
                                 } catch (var17: XmlPullParserException) {
                                    ApkLifeCycleComponent.access$getLogger$cp()
                                       .error("Call graph construction failed: ${var17.getMessage()}", var17 as java.lang.Throwable);
                                    throw new RuntimeException("Call graph construction failed", var17 as java.lang.Throwable);
                                 }
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }

                           while (true) {
                              var var37: ApkLifeCycleComponent;
                              try {
                                 if (callbackDurationSeconds.isEmpty()) {
                                    break label119;
                                 }

                                 var37 = callbackDurationSeconds.remove(0);
                                 val entryPoint: SootClass = var37 as SootClass;
                                 ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).calculateCallbacks(null, var37 as SootClass);
                                 val duration: Long = System.nanoTime() - callbackDuration;
                                 callbackDurationTotal += duration;
                                 callbackDuration = System.nanoTime();
                                 ApkLifeCycleComponent.access$getLogger$cp().info(<unrepresentable>::invokeSuspend$lambda$0);
                                 val dummyMain: SootMethod = ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).getEntryPoint();
                                 var37 = this.this$0;
                                 val var38: java.lang.String = "(component: $entryPoint)";
                                 val var39: java.util.Set = SetsKt.setOf(dummyMain);
                                 val var40: Continuation = this as Continuation;
                                 this.L$0 = `$this$flow`;
                                 this.L$1 = callbackDurationSeconds;
                                 this.J$0 = callbackDuration;
                                 this.J$1 = callbackDurationTotal;
                                 this.label = 1;
                                 var37 = (ApkLifeCycleComponent)var37.emit(`$this$flow`, var38, var39, var40);
                              } catch (var26: IOException) {
                                 ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var26.getMessage()}", var26);
                                 throw new RuntimeException("Call graph construction failed", var26);
                              } catch (var27: XmlPullParserException) {
                                 ApkLifeCycleComponent.access$getLogger$cp()
                                    .error("Call graph construction failed: ${var27.getMessage()}", var27 as java.lang.Throwable);
                                 throw new RuntimeException("Call graph construction failed", var27 as java.lang.Throwable);
                              }

                              if (var37 === var13) {
                                 return var13;
                              }

                              try {
                                 ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).clearCallBackCache();
                              } catch (var20: IOException) {
                                 ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var20.getMessage()}", var20);
                                 throw new RuntimeException("Call graph construction failed", var20);
                              } catch (var21: XmlPullParserException) {
                                 ApkLifeCycleComponent.access$getLogger$cp()
                                    .error("Call graph construction failed: ${var21.getMessage()}", var21 as java.lang.Throwable);
                                 throw new RuntimeException("Call graph construction failed", var21 as java.lang.Throwable);
                              }
                           }
                        }

                        try {
                           ApkLifeCycleComponent.access$getDelegateSetupApplication(this.this$0).clearCallBackCache();
                        } catch (var14: IOException) {
                           ApkLifeCycleComponent.access$getLogger$cp().error("Call graph construction failed: ${var14.getMessage()}", var14);
                           throw new RuntimeException("Call graph construction failed", var14);
                        } catch (var15: XmlPullParserException) {
                           ApkLifeCycleComponent.access$getLogger$cp()
                              .error("Call graph construction failed: ${var15.getMessage()}", var15 as java.lang.Throwable);
                           throw new RuntimeException("Call graph construction failed", var15 as java.lang.Throwable);
                        }
                     }

                     ApkLifeCycleComponent.access$getLogger$cp().info(<unrepresentable>::invokeSuspend$lambda$1);
                     return Unit.INSTANCE;
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     val var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
                     var3.L$0 = value;
                     return var3 as Continuation<Unit>;
                  }

                  public final Object invoke(FlowCollector<? super IEntryPointProvider.AnalyzeTask> p1, Continuation<? super Unit> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }

                  private static final Object invokeSuspend$lambda$0(long $duration, SootClass $entryPoint) {
                     return "\nCollecting callbacks took ${`$duration` / 1.0E9} seconds for component $`$entryPoint`";
                  }

                  private static final Object invokeSuspend$lambda$1(long $callbackDurationSeconds) {
                     return "Collecting callbacks and building a call graph took $`$callbackDurationSeconds` seconds";
                  }
               }
            ) as Function2
         );
      }


   init {
      this.targetAPKFile = targetAPKFile;
      this.androidPlatformDir = androidPlatformDir;
      this.oneComponentAtATime = oneComponentAtATime;
      this.ignoreFlowsInSystemPackages = ignoreFlowsInSystemPackages;
      this.enableCallbacks = enableCallbacks;
      this.callbacksFile = callbacksFile;
      this.callbackAnalyzer = callbackAnalyzer;
      this.filterThreadCallbacks = filterThreadCallbacks;
      this.maxCallbacksPerComponent = maxCallbacksPerComponent;
      this.callbackAnalysisTimeout = callbackAnalysisTimeout;
      this.maxCallbackAnalysisDepth = maxCallbackAnalysisDepth;
      this.serializeCallbacks = serializeCallbacks;
      this.iccModel = iccModel;
      this.iccResultsPurify = iccResultsPurify;
      this.config$delegate = LazyKt.lazy(ApkLifeCycleComponent::config_delegate$lambda$0);
      this.delegateSetupApplication$delegate = LazyKt.lazy(ApkLifeCycleComponent::delegateSetupApplication_delegate$lambda$1);
   }

   public constructor(c: InfoflowAndroidConfiguration, mainConfig: MainConfig, targetAPKFile: String)  {
      val var10002: java.lang.String = mainConfig.getAndroidPlatformDir();
      val var10003: Boolean = c.getOneComponentAtATime();
      val var10004: Boolean = c.getIgnoreFlowsInSystemPackages();
      val var10005: Boolean = c.getCallbackConfig().getEnableCallbacks();
      val var10006: java.lang.String = c.getCallbackConfig().getCallbacksFile();
      val var10007: CallbackAnalyzer = c.getCallbackConfig().getCallbackAnalyzer();
      this(
         targetAPKFile,
         var10002,
         var10003,
         var10004,
         var10005,
         var10006,
         ApkLifeCycleComponentKt.getConvert(var10007),
         c.getCallbackConfig().getFilterThreadCallbacks(),
         c.getCallbackConfig().getMaxCallbacksPerComponent(),
         c.getCallbackConfig().getCallbackAnalysisTimeout(),
         c.getCallbackConfig().getMaxAnalysisCallbackDepth(),
         c.getCallbackConfig().isSerializeCallbacks(),
         c.getIccConfig().getIccModel(),
         c.getIccConfig().isIccResultsPurifyEnabled()
      );
   }

   public fun injectStubDroidHierarchy(analyzer: cn.sast.framework.entries.apk.ApkLifeCycleComponent.WSetupApplication) {
      if (this.taintWrapper != null) {
         val provider: IMethodSummaryProvider = this.taintWrapper.getProvider();
         analyzer.addPreprocessor(new PreAnalysisHandler(provider) {
            {
               this.$provider = `$provider`;
            }

            public void onBeforeCallgraphConstruction() {
               for (java.lang.String className : this.$provider.getAllClassesWithSummaries()) {
                  val sc: SootClass = Scene.v().forceResolve(className, 2);
                  if (sc.isPhantom()) {
                     val summaries: ClassMethodSummaries = this.$provider.getClassFlows(className);
                     if (summaries != null) {
                        if (summaries.hasInterfaceInfo()) {
                           if (summaries.isInterface()) {
                              sc.setModifiers(sc.getModifiers() or 512);
                           } else {
                              sc.setModifiers(sc.getModifiers() and -513);
                           }
                        }

                        if (summaries.hasSuperclass()) {
                           sc.setSuperclass(Scene.v().forceResolve(summaries.getSuperClass(), 2));
                        }

                        if (summaries.hasInterfaces()) {
                           for (java.lang.String intfName : summaries.getInterfaces()) {
                              val scIntf: SootClass = Scene.v().forceResolve(var9, 2);
                              if (!sc.implementsInterface(var9)) {
                                 sc.addInterface(scIntf);
                              }
                           }
                        }
                     }
                  }
               }
            }

            public void onAfterCallgraphConstruction() {
            }
         });
      }
   }

   public suspend fun FlowCollector<AnalyzeTask>.emit(name: String, entries: Set<SootMethod>) {
      val var10000: Any = `$this$emit`.emit(new IEntryPointProvider.AnalyzeTask(name, entries, this) {
         private final java.lang.String name;
         private final java.util.Set<SootMethod> entries;
         private final java.util.Set<SootMethod> additionalEntries;
         private final java.util.Set<SootClass> components;

         {
            this.name = `$name`;
            this.entries = `$entries`;
            this.additionalEntries = ApkLifeCycleComponent.access$getDelegateSetupApplication(`$receiver`).lifecycleMethods();
            this.components = ApkLifeCycleComponent.access$getDelegateSetupApplication(`$receiver`).getEntrypointClasses();
         }

         @Override
         public java.lang.String getName() {
            return this.name;
         }

         @Override
         public java.util.Set<SootMethod> getEntries() {
            return this.entries;
         }

         @Override
         public java.util.Set<SootMethod> getAdditionalEntries() {
            return this.additionalEntries;
         }

         @Override
         public java.util.Set<SootClass> getComponents() {
            return this.components;
         }

         @Override
         public void needConstructCallGraph(SootCtx sootCtx) {
            sootCtx.showPta();
         }

         @Override
         public java.util.Set<SootMethod> getMethodsMustAnalyze() {
            return IEntryPointProvider.AnalyzeTask.DefaultImpls.getMethodsMustAnalyze(this);
         }
      }, `$completion`);
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   override fun startAnalyse() {
      IEntryPointProvider.DefaultImpls.startAnalyse(this);
   }

   override fun endAnalyse() {
      IEntryPointProvider.DefaultImpls.endAnalyse(this);
   }

   @JvmStatic
   fun `config_delegate$lambda$0`(`this$0`: ApkLifeCycleComponent): InfoflowAndroidConfiguration {
      val config: InfoflowAndroidConfiguration = new InfoflowAndroidConfiguration();
      config.setIgnoreFlowsInSystemPackages(`this$0`.ignoreFlowsInSystemPackages);
      config.setSootIntegrationMode(SootIntegrationMode.CreateNewInstance);
      config.setCallgraphAlgorithm(null);
      val iccConfig: IccConfiguration = config.getIccConfig();
      iccConfig.setIccModel(`this$0`.iccModel);
      iccConfig.setIccResultsPurify(`this$0`.iccResultsPurify);
      config.getAnalysisFileConfig().setTargetAPKFile(`this$0`.targetAPKFile);
      val callbackConfig: CallbackConfiguration = config.getCallbackConfig();
      callbackConfig.setEnableCallbacks(`this$0`.enableCallbacks);
      callbackConfig.setCallbacksFile(`this$0`.callbacksFile);
      callbackConfig.setCallbackAnalyzer(ApkLifeCycleComponentKt.getConvert(`this$0`.callbackAnalyzer));
      callbackConfig.setFilterThreadCallbacks(`this$0`.filterThreadCallbacks);
      callbackConfig.setMaxCallbacksPerComponent(`this$0`.maxCallbacksPerComponent);
      callbackConfig.setCallbackAnalysisTimeout(`this$0`.callbackAnalysisTimeout);
      callbackConfig.setMaxAnalysisCallbackDepth(`this$0`.maxCallbackAnalysisDepth);
      callbackConfig.setSerializeCallbacks(`this$0`.serializeCallbacks);
      val targetFile: IResource = Resource.INSTANCE.of(`this$0`.targetAPKFile);
      if (!targetFile.getExists()) {
         val var8: Array<Any> = new Object[]{targetFile.getAbsolutePath()};
         val var10000: java.lang.String = java.lang.String.format("Target APK file %s does not exist", Arrays.copyOf(var8, var8.length));
         throw new IllegalStateException(var10000.toString());
      } else {
         return config;
      }
   }

   @JvmStatic
   fun `delegateSetupApplication_delegate$lambda$1`(`this$0`: ApkLifeCycleComponent): ApkLifeCycleComponent.WSetupApplication {
      `this$0`.getConfig().getAnalysisFileConfig().setAndroidPlatformDir(`this$0`.androidPlatformDir);
      val wSetupApplication: ApkLifeCycleComponent.WSetupApplication = `this$0`.new WSetupApplication(`this$0`, `this$0`.getConfig());
      `this$0`.getConfig().getAnalysisFileConfig().setAndroidPlatformDir("unused");
      return wSetupApplication;
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   public inner class WSetupApplication(config: InfoflowAndroidConfiguration) : SetupApplication(config, null) {
      public final val entryPoint: SootMethod
         public final get() {
            val var10000: SootMethod = this.entryPointCreator.getGeneratedMainMethod();
            return var10000;
         }


      init {
         this.this$0 = `this$0`;
      }

      public fun lifecycleMethods(): Set<SootMethod>? {
         var lifecycleMethods: java.util.Set = null;
         if (this.entryPointCreator != null) {
            val var10000: ComponentEntryPointCollection = this.entryPointCreator.getComponentToEntryPointInfo();
            val var3: java.util.Collection = var10000.getLifecycleMethods();
            lifecycleMethods = CollectionsKt.toSet(var3);
         }

         return lifecycleMethods;
      }

      public fun clearCallBackCache() {
         this.callbackMethods.clear();
         this.fragmentClasses.clear();
      }

      @Throws(org/xmlpull/v1/XmlPullParserException::class, java/io/IOException::class)
      public open fun parseAppResources() {
         super.parseAppResources();
      }

      public fun calculateCallbacks(sourcesAndSinks: ISourceSinkDefinitionProvider?, entryPoint: SootClass?) {
         val it: Method = SetupApplication.class.getDeclaredMethod("calculateCallbacks", ISourceSinkDefinitionProvider.class, SootClass.class);
         it.setAccessible(true);
         it.invoke(this, sourcesAndSinks, entryPoint);
      }

      protected open fun createEntryPointCreator(components: MutableSet<SootClass>?): AndroidEntryPointCreator {
         return new AndroidEntryPointCreator(components, this.manifest) {
            private final PhantomValueForType p;

            {
               super(`$super_call_param$1`, `$components`);
               this.p = new PhantomValueForType(null, 1, null);
            }

            protected Value getValueForType(
               Type tp,
               java.util.Set<SootClass> constructionStack,
               java.util.Set<? extends SootClass> parentClasses,
               java.util.Set<Local> generatedLocals,
               boolean ignoreExcludes
            ) {
               val var10000: PhantomValueForType = this.p;
               val var10001: Body = this.body;
               val var10002: LocalGenerator = this.generator;
               return var10000.getValueForType(var10001, var10002, tp) as Value;
            }
         };
      }
   }
}
