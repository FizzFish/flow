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

public class ApkLifeCycleComponent(
    targetAPKFile: String,
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
) : IEntryPointProvider {
    public var targetAPKFile: String = targetAPKFile
        internal set

    public var androidPlatformDir: String = androidPlatformDir
        internal set

    public val oneComponentAtATime: Boolean = oneComponentAtATime

    public var ignoreFlowsInSystemPackages: Boolean = ignoreFlowsInSystemPackages
        internal set

    public val enableCallbacks: Boolean = enableCallbacks
    public val callbacksFile: String = callbacksFile

    public var callbackAnalyzer: CallbackAnalyzerType = callbackAnalyzer
        internal set

    public var filterThreadCallbacks: Boolean = filterThreadCallbacks
        internal set

    public val maxCallbacksPerComponent: Int = maxCallbacksPerComponent
    public val callbackAnalysisTimeout: Int = callbackAnalysisTimeout
    public val maxCallbackAnalysisDepth: Int = maxCallbackAnalysisDepth
    public val serializeCallbacks: Boolean = serializeCallbacks

    public var iccModel: String? = iccModel
        internal set

    public val iccResultsPurify: Boolean = iccResultsPurify

    public var taintWrapper: SummaryTaintWrapper? = null
        internal set

    public val config: InfoflowAndroidConfiguration
        get() = config$delegate.getValue()

    private val delegateSetupApplication: WSetupApplication
        get() = delegateSetupApplication$delegate.getValue()

    private val config$delegate = lazy { config_delegate$lambda$0(this) }
    private val delegateSetupApplication$delegate = lazy { delegateSetupApplication_delegate$lambda$1(this) }

    public override val iterator: Flow<AnalyzeTask>
        get() = FlowKt.flow { collector ->
            try {
                delegateSetupApplication.parseAppResources()
            } catch (e: IOException) {
                logger.error("Parse app resource failed", e)
                throw RuntimeException("Parse app resource failed", e)
            } catch (e: XmlPullParserException) {
                logger.error("Parse app resource failed", e)
                throw RuntimeException("Parse app resource failed", e)
            }

            val entrypointClasses = delegateSetupApplication.getEntrypointClasses()
            if (entrypointClasses == null || entrypointClasses.isEmpty()) {
                logger.warn("No entry entrypoint classes")
                return@flow
            }

            injectStubDroidHierarchy(delegateSetupApplication)
            var callbackDurationTotal = 0L
            val callbackDuration = System.nanoTime()

            try {
                if (oneComponentAtATime) {
                    val callbackDurationSeconds = ArrayList(entrypointClasses)
                    while (!callbackDurationSeconds.isEmpty()) {
                        val entryPoint = callbackDurationSeconds.removeAt(0) as SootClass
                        delegateSetupApplication.calculateCallbacks(null, entryPoint)
                        val duration = System.nanoTime() - callbackDuration
                        callbackDurationTotal += duration
                        val currentDuration = System.nanoTime()
                        logger.info("\nCollecting callbacks took ${duration / 1.0E9} seconds for component $entryPoint")
                        val dummyMain = delegateSetupApplication.getEntryPoint()
                        collector.emit("(component: $entryPoint)", setOf(dummyMain))
                        delegateSetupApplication.clearCallBackCache()
                    }
                } else {
                    delegateSetupApplication.calculateCallbacks(null, null)
                    callbackDurationTotal += System.nanoTime() - callbackDuration
                    val entryPoint = delegateSetupApplication.getEntryPoint()
                    collector.emit("(mixed dummy main: ${delegateSetupApplication.getEntryPoint()} of components: $entrypointClasses)", setOf(entryPoint))
                    delegateSetupApplication.clearCallBackCache()
                }
            } catch (e: IOException) {
                logger.error("Call graph construction failed: ${e.message}", e)
                throw RuntimeException("Call graph construction failed", e)
            } catch (e: XmlPullParserException) {
                logger.error("Call graph construction failed: ${e.message}", e)
                throw RuntimeException("Call graph construction failed", e)
            }

            logger.info("Collecting callbacks and building a call graph took ${callbackDurationTotal / 1.0E9} seconds")
        }

    public constructor(c: InfoflowAndroidConfiguration, mainConfig: MainConfig, targetAPKFile: String) : this(
        targetAPKFile = targetAPKFile,
        androidPlatformDir = mainConfig.getAndroidPlatformDir(),
        oneComponentAtATime = c.getOneComponentAtATime(),
        ignoreFlowsInSystemPackages = c.getIgnoreFlowsInSystemPackages(),
        enableCallbacks = c.getCallbackConfig().getEnableCallbacks(),
        callbacksFile = c.getCallbackConfig().getCallbacksFile(),
        callbackAnalyzer = ApkLifeCycleComponentKt.getConvert(c.getCallbackConfig().getCallbackAnalyzer()),
        filterThreadCallbacks = c.getCallbackConfig().getFilterThreadCallbacks(),
        maxCallbacksPerComponent = c.getCallbackConfig().getMaxCallbacksPerComponent(),
        callbackAnalysisTimeout = c.getCallbackConfig().getCallbackAnalysisTimeout(),
        maxCallbackAnalysisDepth = c.getCallbackConfig().getMaxAnalysisCallbackDepth(),
        serializeCallbacks = c.getCallbackConfig().isSerializeCallbacks(),
        iccModel = c.getIccConfig().getIccModel(),
        iccResultsPurify = c.getIccConfig().isIccResultsPurifyEnabled()
    )

    public fun injectStubDroidHierarchy(analyzer: WSetupApplication) {
        taintWrapper?.let { wrapper ->
            val provider = wrapper.getProvider()
            analyzer.addPreprocessor(object : PreAnalysisHandler(provider) {
                override fun onBeforeCallgraphConstruction() {
                    for (className in provider.getAllClassesWithSummaries()) {
                        val sc = Scene.v().forceResolve(className, 2)
                        if (sc.isPhantom) {
                            val summaries = provider.getClassFlows(className)
                            if (summaries != null) {
                                if (summaries.hasInterfaceInfo()) {
                                    if (summaries.isInterface) {
                                        sc.modifiers = sc.modifiers or 512
                                    } else {
                                        sc.modifiers = sc.modifiers and -513
                                    }
                                }

                                if (summaries.hasSuperclass()) {
                                    sc.superclass = Scene.v().forceResolve(summaries.getSuperClass(), 2)
                                }

                                if (summaries.hasInterfaces()) {
                                    for (intfName in summaries.getInterfaces()) {
                                        val scIntf = Scene.v().forceResolve(intfName, 2)
                                        if (!sc.implementsInterface(intfName)) {
                                            sc.addInterface(scIntf)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onAfterCallgraphConstruction() {}
            })
        }
    }

    public suspend fun FlowCollector<AnalyzeTask>.emit(name: String, entries: Set<SootMethod>) {
        emit(object : AnalyzeTask {
            override val name: String = name
            override val entries: Set<SootMethod> = entries
            override val additionalEntries: Set<SootMethod> = delegateSetupApplication.lifecycleMethods()
            override val components: Set<SootClass> = delegateSetupApplication.getEntrypointClasses()

            override fun needConstructCallGraph(sootCtx: SootCtx) {
                sootCtx.showPta()
            }
        })
    }

    override fun startAnalyse() {
        IEntryPointProvider.DefaultImpls.startAnalyse(this)
    }

    override fun endAnalyse() {
        IEntryPointProvider.DefaultImpls.endAnalyse(this)
    }

    @JvmStatic
    private fun config_delegate$lambda$0(this$0: ApkLifeCycleComponent): InfoflowAndroidConfiguration {
        val config = InfoflowAndroidConfiguration()
        config.setIgnoreFlowsInSystemPackages(this$0.ignoreFlowsInSystemPackages)
        config.setSootIntegrationMode(SootIntegrationMode.CreateNewInstance)
        config.setCallgraphAlgorithm(null)
        val iccConfig = config.getIccConfig()
        iccConfig.setIccModel(this$0.iccModel)
        iccConfig.setIccResultsPurify(this$0.iccResultsPurify)
        config.getAnalysisFileConfig().setTargetAPKFile(this$0.targetAPKFile)
        val callbackConfig = config.getCallbackConfig()
        callbackConfig.setEnableCallbacks(this$0.enableCallbacks)
        callbackConfig.setCallbacksFile(this$0.callbacksFile)
        callbackConfig.setCallbackAnalyzer(ApkLifeCycleComponentKt.getConvert(this$0.callbackAnalyzer))
        callbackConfig.setFilterThreadCallbacks(this$0.filterThreadCallbacks)
        callbackConfig.setMaxCallbacksPerComponent(this$0.maxCallbacksPerComponent)
        callbackConfig.setCallbackAnalysisTimeout(this$0.callbackAnalysisTimeout)
        callbackConfig.setMaxAnalysisCallbackDepth(this$0.maxCallbackAnalysisDepth)
        callbackConfig.setSerializeCallbacks(this$0.serializeCallbacks)
        val targetFile = Resource.INSTANCE.of(this$0.targetAPKFile)
        if (!targetFile.exists) {
            throw IllegalStateException("Target APK file ${targetFile.absolutePath} does not exist")
        }
        return config
    }

    @JvmStatic
    private fun delegateSetupApplication_delegate$lambda$1(this$0: ApkLifeCycleComponent): WSetupApplication {
        this$0.config.getAnalysisFileConfig().setAndroidPlatformDir(this$0.androidPlatformDir)
        val wSetupApplication = this$0.WSetupApplication(this$0.config)
        this$0.config.getAnalysisFileConfig().setAndroidPlatformDir("unused")
        return wSetupApplication
    }

    public inner class WSetupApplication(config: InfoflowAndroidConfiguration) : SetupApplication(config, null) {
        public val entryPoint: SootMethod
            get() = entryPointCreator.generatedMainMethod

        public fun lifecycleMethods(): Set<SootMethod>? {
            return entryPointCreator?.componentToEntryPointInfo?.lifecycleMethods?.let { CollectionsKt.toSet(it) }
        }

        public fun clearCallBackCache() {
            callbackMethods.clear()
            fragmentClasses.clear()
        }

        @Throws(XmlPullParserException::class, IOException::class)
        public override fun parseAppResources() {
            super.parseAppResources()
        }

        public fun calculateCallbacks(sourcesAndSinks: ISourceSinkDefinitionProvider?, entryPoint: SootClass?) {
            val method = SetupApplication::class.java.getDeclaredMethod(
                "calculateCallbacks", 
                ISourceSinkDefinitionProvider::class.java, 
                SootClass::class.java
            )
            method.isAccessible = true
            method.invoke(this, sourcesAndSinks, entryPoint)
        }

        protected override fun createEntryPointCreator(components: MutableSet<SootClass>?): AndroidEntryPointCreator {
            return object : AndroidEntryPointCreator(components, this.manifest) {
                private val p = PhantomValueForType(null, 1, null)

                override fun getValueForType(
                    tp: Type,
                    constructionStack: MutableSet<SootClass>,
                    parentClasses: MutableSet<out SootClass>,
                    generatedLocals: MutableSet<Local>,
                    ignoreExcludes: Boolean
                ): Value {
                    return p.getValueForType(body, generator, tp)
                }
            }
        }
    }

    public companion object {
        private val logger: KLogger = TODO("Initialize logger")
    }
}