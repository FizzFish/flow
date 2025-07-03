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
class FlowDroidEngine(
    val mainConfig: MainConfig,
    val infoFlowConfig: InfoflowConfiguration,
    val extInfoFlowConfig: InfoflowConfigurationExt
) {
    fun sourceSinkManager(sourceSinkProvider: ISourceSinkDefinitionProvider): ISourceSinkManager {
        val config = InfoflowAndroidConfiguration()
        config.sourceSinkConfig.merge(infoFlowConfig.sourceSinkConfig)
        return AccessPathBasedSourceSinkManager(
            sourceSinkProvider.sources,
            sourceSinkProvider.sinks,
            config
        ) as ISourceSinkManager
    }

    fun analyze(
        preAnalysis: PreAnalysisCoroutineScope,
        soot: SootCtx,
        provider: IEntryPointProvider,
        cfgFactory: BiDirICFGFactory? = null,
        result: IFlowDroidResultCollector,
        missWrapper: IMissingSummaryReporter
    ) {
        BuildersKt.runBlocking$default(
            null,
            object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                private var label = 0
                private val this$0 = this@FlowDroidEngine
                private val $preAnalysis = preAnalysis
                private val $soot = soot
                private val $provider = provider
                private val $cfgFactory = cfgFactory
                private val $result = result
                private val $missWrapper = missWrapper

                override fun invokeSuspend(result: Any?): Any? {
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            label = 1
                            val res = this$0.analyze(
                                $preAnalysis,
                                $soot,
                                $provider,
                                $cfgFactory,
                                setOf($result),
                                setOf($result),
                                $missWrapper,
                                this as Continuation<Unit>
                            )
                            if (res === IntrinsicsKt.COROUTINE_SUSPENDED) return res
                        }
                        1 -> {
                            ResultKt.throwOnFailure(result)
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }
                    return Unit
                }

                override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                    return create(p1, p2).invokeSuspend(Unit)
                }

                override fun create(p1: Any?, p2: Continuation<*>): Continuation<Unit> {
                    return object : ContinuationImpl(p2 as Continuation<Any?>) {
                        override fun invokeSuspend(result: Any?): Any? {
                            return this@Function2.invokeSuspend(result)
                        }
                    }
                }
            },
            1,
            null
        )
    }

    fun beforeAnalyze(cfgFactory: BiDirICFGFactory? = null) {
        infoFlowConfig.writeOutputFiles = false
        if (cfgFactory != null && infoFlowConfig is InfoflowAndroidConfiguration) {
            val androidPath = mainConfig.androidPlatformDir
            if (cfgFactory is DefaultBiDiICFGFactory) {
                cfgFactory.isAndroid = androidPath != null
            }
        }

        if (infoFlowConfig is InfoflowAndroidConfiguration) {
            check((infoFlowConfig as InfoflowAndroidConfiguration).analysisFileConfig.androidPlatformDir == "unused") {
                "Check failed."
            }
            check((infoFlowConfig as InfoflowAndroidConfiguration).analysisFileConfig.targetAPKFile == "unused") {
                "Check failed."
            }
        }

        FlowDroidEngineKt.fix(infoFlowConfig)
    }

    suspend fun analyzeInScene(
        task: AnalyzeTask,
        provider: IEntryPointProvider,
        soot: SootCtx,
        preAnalysis: PreAnalysisCoroutineScope,
        cfgFactory: BiDirICFGFactory? = null,
        resultAddedHandlers: Set<OnTaintPropagationResultAdded>,
        onResultsAvailable: Set<ResultsAvailableHandler>,
        methodSummariesMissing: IMissingSummaryReporter,
        continuation: Continuation<Unit>
    ): Any? {
        class AnalyzeInSceneContinuation(
            val this$0: FlowDroidEngine,
            completion: Continuation<Unit>
        ) : ContinuationImpl(completion) {
            var L$0: Any? = null
            var L$1: Any? = null
            var L$2: Any? = null
            var L$3: Any? = null
            var L$4: Any? = null
            var L$5: Any? = null
            var L$6: Any? = null
            var L$7: Any? = null
            var L$8: Any? = null
            var L$9: Any? = null
            var label = 0

            override fun invokeSuspend(result: Any?): Any? {
                this.result = result
                label = label or IntrinsicsKt.COROUTINE_SUSPENDED
                return this$0.analyzeInScene(
                    null, null, null, null, null, null, null, null, this
                )
            }
        }

        val $continuation = if (continuation is AnalyzeInSceneContinuation) {
            continuation
        } else {
            AnalyzeInSceneContinuation(this, continuation)
        }

        when ($continuation.label) {
            0 -> {
                ResultKt.throwOnFailure($continuation.result)
                val cg = soot.sootMethodCallGraph
                Scene.v().callGraph = cg
                Scene.v().reachableMethods = ReachableMethods(cg, ArrayList(task.entries))
                
                Scene.v().classes.forEach { sc ->
                    sc.methods.forEach { sm ->
                        if (sm.isConcrete && !sm.hasActiveBody) {
                            sm.setPhantom(true)
                        }
                    }
                }

                val sourceSinkProviderInstance = SourceSinkProvider(mainConfig, preAnalysis)
                $continuation.L$0 = this
                $continuation.L$1 = task
                $continuation.L$2 = provider
                $continuation.L$3 = soot
                $continuation.L$4 = preAnalysis
                $continuation.L$5 = cfgFactory
                $continuation.L$6 = resultAddedHandlers
                $continuation.L$7 = onResultsAvailable
                $continuation.L$8 = methodSummariesMissing
                $continuation.L$9 = sourceSinkProviderInstance
                $continuation.label = 1
                val res = sourceSinkProviderInstance.initialize($continuation)
                if (res === IntrinsicsKt.COROUTINE_SUSPENDED) return res
            }
            1 -> {
                val sourceSinkProviderInstance = $continuation.L$9 as SourceSinkProvider
                val methodSummariesMissing = $continuation.L$8 as IMissingSummaryReporter
                val onResultsAvailable = $continuation.L$7 as Set<ResultsAvailableHandler>
                val resultAddedHandlers = $continuation.L$6 as Set<OnTaintPropagationResultAdded>
                val cfgFactory = $continuation.L$5 as BiDirICFGFactory?
                val preAnalysis = $continuation.L$4 as PreAnalysisCoroutineScope
                val soot = $continuation.L$3 as SootCtx
                val provider = $continuation.L$2 as IEntryPointProvider
                val task = $continuation.L$1 as AnalyzeTask
                val this$0 = $continuation.L$0 as FlowDroidEngine
                ResultKt.throwOnFailure($continuation.result)
            }
            2 -> {
                val infoflow = $continuation.L$9 as MethodSummaryProvider
                val sourceSinkProviderInstance = $continuation.L$8 as SourceSinkProvider
                val methodSummariesMissing = $continuation.L$7 as IMissingSummaryReporter
                val onResultsAvailable = $continuation.L$6 as Set<ResultsAvailableHandler>
                val resultAddedHandlers = $continuation.L$5 as Set<OnTaintPropagationResultAdded>
                val cfgFactory = $continuation.L$4 as BiDirICFGFactory?
                val soot = $continuation.L$3 as SootCtx
                val provider = $continuation.L$2 as IEntryPointProvider
                val task = $continuation.L$1 as AnalyzeTask
                val this$0 = $continuation.L$0 as FlowDroidEngine
                ResultKt.throwOnFailure($continuation.result)
            }
            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
        }

        val var55 = if (!mainConfig.use_wrapper) {
            null
        } else {
            val infoflow = MethodSummaryProvider(mainConfig, preAnalysis)
            $continuation.L$0 = this
            $continuation.L$1 = task
            $continuation.L$2 = provider
            $continuation.L$3 = soot
            $continuation.L$4 = cfgFactory
            $continuation.L$5 = resultAddedHandlers
            $continuation.L$6 = onResultsAvailable
            $continuation.L$7 = methodSummariesMissing
            $continuation.L$8 = sourceSinkProviderInstance
            $continuation.L$9 = infoflow
            $continuation.label = 2
            val res = infoflow.initialize($continuation)
            if (res === IntrinsicsKt.COROUTINE_SUSPENDED) return res
            
            logger.info { "taint wrapper size: ${infoflow.classSummaries.allSummaries.size}" }
            MissingSummaryWrapper(infoflow as IMethodSummaryProvider) { method ->
                methodSummariesMissing.reportMissingMethod(method)
            }
        }

        if (provider is ApkLifeCycleComponent) {
            provider.taintWrapper = var55 as SummaryTaintWrapper
        }

        val var56 = FlowDroidFactory.INSTANCE
        val var47 = var56.createInfoFlow(
            infoFlowConfig.dataFlowDirection,
            mainConfig.androidPlatformDir,
            mainConfig.forceAndroidJar,
            null,
            cfgFactory,
            extInfoFlowConfig.useSparseOpt,
            resultAddedHandlers
        )
        configureInfoFlow(var47, task)
        var47.config = infoFlowConfig
        var47.sootConfig = null
        var47.taintWrapper = var55 as ITaintPropagationWrapper?

        onResultsAvailable.forEach { handler ->
            var47.addResultsAvailableHandler(handler)
        }

        val var49 = sourceSinkManager(sourceSinkProviderInstance)
        val var50 = LoggingKt.info(logger)
        val var51 = "Run IFDS analysis for task: ${task.name}"
        var50.logMethod.invoke("Started: $var51")
        val var53 = LocalDateTime.now()
        var alreadyLogged = false
        val res = ObjectRef()
        res.element = Maybe.empty()

        try {
            try {
                val destination = mutableSetOf<String>()
                task.entries.forEach { method ->
                    destination.add(method.signature)
                }

                FlowDroidEngineKt.runAnalysisReflect(var47, var49, destination)
                res.element = Maybe(Unit)
            } catch (t: Throwable) {
                var50.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(var53)}): $var51 :: EXCEPTION :: ")
                alreadyLogged = true
                throw t
            }
        } catch (t: Throwable) {
            if (!alreadyLogged) {
                if ((res.element as Maybe<*>).hasValue) {
                    var50.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(var53)}): $var51 ")
                } else {
                    var50.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(var53)}): $var51 <Nothing>")
                }
            }
        }

        if ((res.element as Maybe<*>).hasValue) {
            var50.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(var53)}): $var51 ")
        } else {
            var50.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(var53)}): $var51 <Nothing>")
        }

        return Unit
    }

    suspend fun analyze(
        preAnalysis: PreAnalysisCoroutineScope,
        soot: SootCtx,
        provider: IEntryPointProvider,
        cfgFactory: BiDirICFGFactory? = null,
        resultAddedHandlers: Set<OnTaintPropagationResultAdded>,
        onResultsAvailable: Set<ResultsAvailableHandler>,
        methodSummariesMissing: IMissingSummaryReporter,
        continuation: Continuation<Unit>
    ): Any? {
        beforeAnalyze(cfgFactory)
        val result = provider.iterator().collect(
            object : FlowCollector<AnalyzeTask> {
                private val $soot = soot
                private val this$0 = this@FlowDroidEngine
                private val $provider = provider
                private val $preAnalysis = preAnalysis
                private val $cfgFactory = cfgFactory
                private val $resultAddedHandlers = resultAddedHandlers
                private val $onResultsAvailable = onResultsAvailable
                private val $methodSummariesMissing = methodSummariesMissing

                override suspend fun emit(task: AnalyzeTask, continuation: Continuation<Unit>): Any? {
                    val entries = task.entries.toMutableSet()
                    task.additionalEntries?.let { entries.addAll(it) }
                    Scene.v().entryPoints = entries.toList()
                    $soot.constructCallGraph()
                    return this$0.analyzeInScene(
                        task,
                        $provider,
                        $soot,
                        $preAnalysis,
                        $cfgFactory,
                        $resultAddedHandlers,
                        $onResultsAvailable,
                        $methodSummariesMissing,
                        continuation
                    )
                }
            },
            continuation
        )
        return if (result === IntrinsicsKt.COROUTINE_SUSPENDED) result else Unit
    }

    private fun configureInfoFlow(infoflow: AbstractInfoflow, task: AnalyzeTask) {
        infoflow.memoryManagerFactory = object : IMemoryManagerFactory {
            private val $task = task

            override fun getMemoryManager(
                tracingEnabled: Boolean,
                erasePathData: PathDataErasureMode
            ): IMemoryManager<Abstraction, soot.Unit> {
                return AndroidMemoryManager(tracingEnabled, erasePathData, $task.components)
            }
        }
        infoflow.memoryManagerFactory = null
        infoflow.postProcessors = setOf(object : PostAnalysisHandler {
            override fun onResultsAvailable(results: InfoflowResults, cfg: IInfoflowCFG): InfoflowResults {
                return results
            }
        })
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")
    }
}