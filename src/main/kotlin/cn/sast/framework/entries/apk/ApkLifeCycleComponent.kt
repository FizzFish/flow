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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import mu.KLogger
import mu.KotlinLogging
import org.xmlpull.v1.XmlPullParserException
import soot.*
import soot.jimple.infoflow.InfoflowConfiguration.SootIntegrationMode
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackAnalyzer
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.IccConfiguration
import soot.jimple.infoflow.android.SetupApplication
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointCreator
import soot.jimple.infoflow.android.entryPointCreators.components.ComponentEntryPointCollection
import soot.jimple.infoflow.handlers.PreAnalysisHandler
import soot.jimple.infoflow.methodSummary.data.provider.IMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.data.summary.ClassMethodSummaries
import soot.jimple.infoflow.methodSummary.taintWrappers.SummaryTaintWrapper
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider

/**
 * Kotlin‑idiomatic re‑implementation of the de‑compiled `ApkLifeCycleComponent`.
 * The API surface and behaviour remain functionally equivalent to the original Java bytecode,
 * while compiling cleanly with Kotlin 1.9+.
 */
class ApkLifeCycleComponent(
    targetAPKFile: String,
    androidPlatformDir: String,
    val oneComponentAtATime: Boolean = true,
    ignoreFlowsInSystemPackages: Boolean = false,
    val enableCallbacks: Boolean = true,
    val callbacksFile: String = "",
    callbackAnalyzer: CallbackAnalyzerType = CallbackAnalyzerType.Default,
    filterThreadCallbacks: Boolean = true,
    val maxCallbacksPerComponent: Int = 100,
    val callbackAnalysisTimeout: Int = 0,
    val maxCallbackAnalysisDepth: Int = -1,
    val serializeCallbacks: Boolean = false,
    iccModel: String? = null,
    val iccResultsPurify: Boolean = true
) : IEntryPointProvider {

    /** Path to the APK that will be analysed. */
    var targetAPKFile: String = targetAPKFile
        private set

    /** Path to Android platforms (e.g. `$ANDROID_HOME/platforms`). */
    var androidPlatformDir: String = androidPlatformDir
        private set

    /** Whether flows in system packages are ignored. */
    var ignoreFlowsInSystemPackages: Boolean = ignoreFlowsInSystemPackages
        private set

    /** Which callback‑extraction strategy is used. */
    var callbackAnalyzer: CallbackAnalyzerType = callbackAnalyzer
        private set

    /** Whether callbacks executed inside worker threads should be filtered. */
    var filterThreadCallbacks: Boolean = filterThreadCallbacks
        private set

    /** Optional ICC model file path. */
    var iccModel: String? = iccModel
        private set

    /** Optional StubDroid summaries. */
    var taintWrapper: SummaryTaintWrapper? = null

    /** Flow‑Droid configuration lazily rebuilt from primary constructor args. */
    val config: InfoflowAndroidConfiguration by lazy { buildConfig() }

    /** Thin wrapper around Flow‑Droid `SetupApplication` that exposes a few extras. */
    private val delegateSetupApplication: WSetupApplication by lazy { buildSetupApplication() }

    //--------------------------------------------------------------------------
    // IEntryPointProvider implementation
    //--------------------------------------------------------------------------

    override val iterator: Flow<AnalyzeTask> = flow {
        try {
            delegateSetupApplication.parseAppResources()
        } catch (e: IOException) {
            logger.error(e) { "Parse app resource failed" }
            throw RuntimeException("Parse app resource failed", e)
        } catch (e: XmlPullParserException) {
            logger.error(e) { "Parse app resource failed" }
            throw RuntimeException("Parse app resource failed", e)
        }

        val entrypointClasses = delegateSetupApplication.entrypointClasses
        if (entrypointClasses.isNullOrEmpty()) {
            logger.warn { "No entrypoint classes detected" }
            return@flow
        }

        injectStubDroidHierarchy(delegateSetupApplication)

        var callbackDurationTotal = 0L
        var callbackStart = System.nanoTime()

        fun buildCallbacks(entryPoint: SootClass?) {
            try {
                delegateSetupApplication.calculateCallbacks(null, entryPoint)
            } catch (e: IOException) {
                logger.error(e) { "Call graph construction failed: ${e.message}" }
                throw RuntimeException("Call graph construction failed", e)
            } catch (e: XmlPullParserException) {
                logger.error(e) { "Call graph construction failed: ${e.message}" }
                throw RuntimeException("Call graph construction failed", e)
            }
        }

        suspend fun FlowCollector<AnalyzeTask>.emitTask(name: String, entries: Set<SootMethod>) {
            emit(object : AnalyzeTask {
                override val name: String = name
                override val entries: Set<SootMethod> = entries
                override val additionalEntries: Set<SootMethod> =
                    delegateSetupApplication.lifecycleMethods() ?: emptySet()
                override val components: Set<SootClass> = delegateSetupApplication.entrypointClasses
                override fun needConstructCallGraph(sootCtx: SootCtx) = sootCtx.showPta()
            })
        }

        if (oneComponentAtATime) {
            val components = entrypointClasses.toMutableList()
            while (components.isNotEmpty()) {
                val component = components.removeAt(0)
                buildCallbacks(component)
                val duration = System.nanoTime() - callbackStart
                callbackDurationTotal += duration
                callbackStart = System.nanoTime()

                logger.info {
                    "Collecting callbacks took ${duration / 1.0e9} seconds for component $component"
                }

                val dummyMain = delegateSetupApplication.entryPoint
                emitTask("(component: $component)", setOf(dummyMain))
                delegateSetupApplication.clearCallBackCache()
            }
        } else {
            buildCallbacks(null)
            callbackDurationTotal = System.nanoTime() - callbackStart

            val dummyMain = delegateSetupApplication.entryPoint
            emitTask(
                "(mixed dummy main: ${delegateSetupApplication.entryPoint} of components: $entrypointClasses)",
                setOf(dummyMain)
            )
            delegateSetupApplication.clearCallBackCache()
        }

        logger.info {
            "Collecting callbacks and building a call graph took ${callbackDurationTotal / 1.0e9} seconds"
        }
    }

    //--------------------------------------------------------------------------
    // Public helpers
    //--------------------------------------------------------------------------

    fun injectStubDroidHierarchy(analyzer: WSetupApplication) {
        taintWrapper?.provider?.let { provider ->
            analyzer.addPreprocessor(object : PreAnalysisHandler(provider) {
                override fun onBeforeCallgraphConstruction() {
                    for (className in provider.allClassesWithSummaries) {
                        val sc = Scene.v().forceResolve(className, SootClass.BODIES)
                        if (sc.isPhantom) {
                            provider.getClassFlows(className)?.let { summaries ->
                                if (summaries.hasInterfaceInfo()) {
                                    if (summaries.isInterface) {
                                        sc.modifiers = sc.modifiers or Modifier.INTERFACE
                                    } else {
                                        sc.modifiers = sc.modifiers and Modifier.INTERFACE.inv()
                                    }
                                }
                                if (summaries.hasSuperclass()) {
                                    sc.superclass = Scene.v().forceResolve(summaries.superClass, SootClass.BODIES)
                                }
                                if (summaries.hasInterfaces()) {
                                    for (intfName in summaries.interfaces) {
                                        val scIntf = Scene.v().forceResolve(intfName, SootClass.BODIES)
                                        if (!sc.implementsInterface(intfName)) sc.addInterface(scIntf)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onAfterCallgraphConstruction() = Unit
            })
        }
    }

    //--------------------------------------------------------------------------
    // Secondary constructor matching the original bytecode signature
    //--------------------------------------------------------------------------

    constructor(c: InfoflowAndroidConfiguration, mainConfig: MainConfig, targetAPKFile: String) : this(
        targetAPKFile = targetAPKFile,
        androidPlatformDir = mainConfig.androidPlatformDir,
        oneComponentAtATime = c.oneComponentAtATime,
        ignoreFlowsInSystemPackages = c.isIgnoreFlowsInSystemPackages,
        enableCallbacks = c.callbackConfig.enableCallbacks,
        callbacksFile = c.callbackConfig.callbacksFile,
        callbackAnalyzer = c.callbackConfig.callbackAnalyzer.toCallbackAnalyzerType(),
        filterThreadCallbacks = c.callbackConfig.filterThreadCallbacks,
        maxCallbacksPerComponent = c.callbackConfig.maxCallbacksPerComponent,
        callbackAnalysisTimeout = c.callbackConfig.callbackAnalysisTimeout,
        maxCallbackAnalysisDepth = c.callbackConfig.maxAnalysisCallbackDepth,
        serializeCallbacks = c.callbackConfig.serializeCallbacks,
        iccModel = c.iccConfig.iccModel,
        iccResultsPurify = c.iccConfig.isIccResultsPurifyEnabled
    )

    //--------------------------------------------------------------------------
    // IEntryPointProvider boilerplate
    //--------------------------------------------------------------------------

    override fun startAnalyse() = IEntryPointProvider.DefaultImpls.startAnalyse(this)
    override fun endAnalyse() = IEntryPointProvider.DefaultImpls.endAnalyse(this)

    //--------------------------------------------------------------------------
    // Private helpers
    //--------------------------------------------------------------------------

    private fun buildConfig(): InfoflowAndroidConfiguration = InfoflowAndroidConfiguration().apply {
        ignoreFlowsInSystemPackages = this@ApkLifeCycleComponent.ignoreFlowsInSystemPackages
        sootIntegrationMode = SootIntegrationMode.CreateNewInstance
        callgraphAlgorithm = null
        iccConfig.apply {
            iccModel = this@ApkLifeCycleComponent.iccModel
            isIccResultsPurify = this@ApkLifeCycleComponent.iccResultsPurify
        }
        analysisFileConfig.targetAPKFile = this@ApkLifeCycleComponent.targetAPKFile
        callbackConfig.apply {
            enableCallbacks = this@ApkLifeCycleComponent.enableCallbacks
            callbacksFile = this@ApkLifeCycleComponent.callbacksFile
            callbackAnalyzer = this@ApkLifeCycleComponent.callbackAnalyzer.toCallbackAnalyzer()
            filterThreadCallbacks = this@ApkLifeCycleComponent.filterThreadCallbacks
            maxCallbacksPerComponent = this@ApkLifeCycleComponent.maxCallbacksPerComponent
            callbackAnalysisTimeout = this@ApkLifeCycleComponent.callbackAnalysisTimeout
            maxAnalysisCallbackDepth = this@ApkLifeCycleComponent.maxCallbackAnalysisDepth
            serializeCallbacks = this@ApkLifeCycleComponent.serializeCallbacks
        }

        val targetFile: IResource = Resource.of(this@ApkLifeCycleComponent.targetAPKFile)
        require(targetFile.exists) { "Target APK file ${targetFile.absolutePath} does not exist" }
    }

    private fun buildSetupApplication(): WSetupApplication {
        config.analysisFileConfig.androidPlatformDir = androidPlatformDir
        val app = WSetupApplication(config)
        config.analysisFileConfig.androidPlatformDir = "unused" // placeholder to avoid re‑parsing
        return app
    }

    //--------------------------------------------------------------------------
    // Nested wrapper around Flow‑Droid SetupApplication
    //--------------------------------------------------------------------------

    inner class WSetupApplication(config: InfoflowAndroidConfiguration) : SetupApplication(config, null) {
        val entryPoint: SootMethod
            get() = entryPointCreator.generatedMainMethod

        val entrypointClasses: Set<SootClass>
            get() = super.getEntrypointClasses()

        fun lifecycleMethods(): Set<SootMethod>? =
            entryPointCreator?.componentToEntryPointInfo?.lifecycleMethods?.toSet()

        fun clearCallBackCache() {
            callbackMethods.clear()
            fragmentClasses.clear()
        }

        fun calculateCallbacks(
            sourcesAndSinks: ISourceSinkDefinitionProvider?,
            entryPoint: SootClass?
        ) {
            val m: Method = SetupApplication::class.java.getDeclaredMethod(
                "calculateCallbacks",
                ISourceSinkDefinitionProvider::class.java,
                SootClass::class.java
            )
            m.isAccessible = true
            m.invoke(this, sourcesAndSinks, entryPoint)
        }

        override fun createEntryPointCreator(components: MutableSet<SootClass>?): AndroidEntryPointCreator =
            object : AndroidEntryPointCreator(components, manifest) {
                private val phantomGenerator = PhantomValueForType()
                override fun getValueForType(
                    tp: Type,
                    constructionStack: MutableSet<SootClass>,
                    parentClasses: Set<out SootClass>,
                    generatedLocals: MutableSet<Local>,
                    ignoreExcludes: Boolean
                ): Value = phantomGenerator.getValueForType(body, generator, tp)
            }
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}

// -----------------------------------------------------------------------------
//  Enum bridges between Flow‑Droid's `CallbackAnalyzer` and project‑level enum.
// -----------------------------------------------------------------------------

internal enum class CallbackAnalyzerType { Default, Fast }

fun CallbackAnalyzerType.toCallbackAnalyzer(): CallbackAnalyzer = when (this) {
    CallbackAnalyzerType.Default -> CallbackAnalyzer.Default
    CallbackAnalyzerType.Fast -> CallbackAnalyzer.Fast
}

fun CallbackAnalyzer.toCallbackAnalyzerType(): CallbackAnalyzerType = when (this) {
    CallbackAnalyzer.Default -> CallbackAnalyzerType.Default
    CallbackAnalyzer.Fast -> CallbackAnalyzerType.Fast
}
