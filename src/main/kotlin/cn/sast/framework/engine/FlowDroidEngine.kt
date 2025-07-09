package cn.sast.framework.engine

/* ──────────────────────────────────────────────────────────────────────────
 *  FlowDroid 引擎适配层：融合原先 FlowDroidEngineKt.kt 与 FlowDroidEngine.kt
 *  顶层扩展函数 + 引擎封装类
 * ────────────────────────────────────────────────────────────────────────── */

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
import com.feysh.corax.config.api.rules.ProcessRule
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import soot.Scene
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
import soot.jimple.infoflow.sourcesSinks.manager.AccessPathBasedSourceSinkManager
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper
import soot.jimple.toolkits.callgraph.ReachableMethods
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.*

/* ──  顶层扩展函数  ─────────────────────────────────────────────────────── */

/**
 * FlowDroid 在开启 ICC 建模时需强制关闭生命周期源，否则会出现 Source/Sink 冲突。
 */
fun InfoflowConfiguration.fix() {
    val logger: KLogger = KotlinLogging.logger {}
    if (this is InfoflowAndroidConfiguration &&
        sourceSinkConfig.enableLifecycleSources &&
        iccConfig.isIccEnabled
    ) {
        logger.warn { "ICC model specified, automatically disabling lifecycle sources" }
        sourceSinkConfig.enableLifecycleSources = false
    }
}

/**
 * AbstractInfoflow.runAnalysis(...) 是 protected；通过反射调起。
 */
fun AbstractInfoflow.runAnalysisReflect(
    sourcesSinks: ISourceSinkManager,
    additionalSeeds: Set<String>?,
) {
    val m: Method = AbstractInfoflow::class.java
        .getDeclaredMethod("runAnalysis", ISourceSinkManager::class.java, Set::class.java)
    m.isAccessible = true
    m.invoke(this, sourcesSinks, additionalSeeds)
}

/* ──  引擎封装类  ───────────────────────────────────────────────────────── */

class FlowDroidEngine(
    private val mainConfig: MainConfig,
    private val infoFlowConfig: InfoflowConfiguration,
    private val extInfoFlowConfig: InfoflowConfigurationExt,
) {

    /* ---------------------------------------------------------- logger -- */
    private val logger: KLogger = KotlinLogging.logger {}

    /* ---------------------------------------------------- public API  -- */

    /**
     * 生成符合当前配置的 [ISourceSinkManager]。
     */
    fun sourceSinkManager(
        provider: ISourceSinkDefinitionProvider,
    ): ISourceSinkManager {
        val cfg = InfoflowAndroidConfiguration().apply {
            sourceSinkConfig.merge(infoFlowConfig.sourceSinkConfig)
        }
        return AccessPathBasedSourceSinkManager(
            provider.sources,
            provider.sinks,
            cfg,
        )
    }

    /**
     * 外部只需调用此方法即可完成单个 [IEntryPointProvider] 的 TA 分析。
     * 内部会自动以 runBlocking 包装 suspend 版本。
     */
    fun analyze(
        preAnalysis: PreAnalysisCoroutineScope,
        soot: SootCtx,
        provider: IEntryPointProvider,
        cfgFactory: BiDirICFGFactory? = null,
        result: IFlowDroidResultCollector,
        missWrapper: IMissingSummaryReporter,
    ) = runBlocking {
        analyze(
            preAnalysis,
            soot,
            provider,
            cfgFactory,
            resultAddedHandlers = setOf(result),
            onResultsAvailable = setOf(result),
            methodSummariesMissing = missWrapper,
        )
    }

    /* -------------------------------------------------  suspend 内核  -- */

    /**
     * 逐个入口任务执行 IFDS 分析。
     */
    suspend fun analyze(
        preAnalysis: PreAnalysisCoroutineScope,
        soot: SootCtx,
        provider: IEntryPointProvider,
        cfgFactory: BiDirICFGFactory? = null,
        resultAddedHandlers: Set<OnTaintPropagationResultAdded>,
        onResultsAvailable: Set<ResultsAvailableHandler>,
        methodSummariesMissing: IMissingSummaryReporter,
    ) = withContext(Dispatchers.Default) {
        beforeAnalyze(cfgFactory)

        provider.iterator().collect(object : FlowCollector<AnalyzeTask> {
            override suspend fun emit(task: AnalyzeTask) {
                /* 设置 Soot scene */
                Scene.v().entryPoints = task.entries.toList()
                soot.constructCallGraph()
                val cg = soot.sootMethodCallGraph
                Scene.v().apply {
                    callGraph = cg
                    reachableMethods = ReachableMethods(callGraph, ArrayList(task.entries))
                }

                /* 全局 Source/Sink + Method Summary Provider */
                val sourceSinkProvider = SourceSinkProvider(mainConfig, preAnalysis).also { it.initialize() }

                val summaryProvider: SummaryTaintWrapper? =
                    if (mainConfig.use_wrapper) {
                        val prov = MethodSummaryProvider(mainConfig, preAnalysis).apply { initialize() }
                        logger.info { "Taint-wrapper summaries loaded: ${prov.classSummaries.allSummaries.size}" }
                        MissingSummaryWrapper(prov as IMethodSummaryProvider) {
                            methodSummariesMissing.reportMissingMethod(it)
                        }
                    } else null

                if (provider is ApkLifeCycleComponent) provider.taintWrapper = summaryProvider

                /* 构造并配置 InfoFlow 实例 */
                val infoflow = FlowDroidFactory
                    .createInfoFlow(
                        infoFlowConfig.dataFlowDirection,
                        mainConfig.androidPlatformDir,
                        mainConfig.forceAndroidJar,
                        null,
                        cfgFactory,
                        extInfoFlowConfig.useSparseOpt,
                        resultAddedHandlers,
                    )

                configureInfoFlow(infoflow, task)
                infoflow.apply {
                    config = infoFlowConfig
                    sootConfig = null
                    taintWrapper = summaryProvider as ITaintPropagationWrapper?
                    onResultsAvailable.forEach { addResultsAvailableHandler(it) }
                }

                /* 记录 & 执行分析（反射调用 protected 方法） */
                val srcSinkMgr = sourceSinkManager(sourceSinkProvider)
                val tag = "IFDS-analysis for task: ${task.name}"
                val start = LocalDateTime.now()
                logger.info { "Started: $tag" }

                val resultHolder = Maybe.empty<Unit>()
                try {
                    infoflow.runAnalysisReflect(srcSinkMgr, task.entries.map(SootMethod::getSignature).toSet())
                    resultHolder.setValue(Unit)
                } catch (t: Throwable) {
                    logger.error(t) { "Exception in $tag" }
                    throw t
                } finally {
                    val elapsed = LoggingKt.elapsedSecFrom(start)
                    logger.info { "Finished ($elapsed s): $tag ${if (resultHolder.hasValue) "" else "<Nothing>"}" }
                }
            }
        })
    }

    /* ---------------------------------------------------- internal  -- */

    private fun beforeAnalyze(cfgFactory: BiDirICFGFactory?) {
        /* 关闭文件输出，加速 */
        infoFlowConfig.writeOutputFiles = false

        /* 若外部自定义 ICFGFactory，补充 Android 标记 */
        if (cfgFactory != null && infoFlowConfig is InfoflowAndroidConfiguration) {
            if (cfgFactory is DefaultBiDiICFGFactory) {
                cfgFactory.isAndroid = mainConfig.androidPlatformDir != null
            }
        }

        /* 防御性检查：FlowDroid 工厂在非 Android 模式下要求这些字段为 "unused" */
        if (infoFlowConfig is InfoflowAndroidConfiguration) {
            check(infoFlowConfig.analysisFileConfig.androidPlatformDir == "unused") { "androidPlatformDir must be 'unused' here" }
            check(infoFlowConfig.analysisFileConfig.targetAPKFile == "unused") { "targetAPKFile must be 'unused' here" }
        }

        /* 应用 fix() 针对 ICC & Lifecycle */
        infoFlowConfig.fix()
    }

    /** 为单次任务定制内存管理与后置处理器 */
    private fun configureInfoFlow(infoflow: AbstractInfoflow, task: AnalyzeTask) {
        infoflow.memoryManagerFactory = object : IMemoryManagerFactory {
            override fun getMemoryManager(
                tracingEnabled: Boolean,
                erasePathData: PathDataErasureMode,
            ): IMemoryManager<Abstraction, soot.Unit> =
                AndroidMemoryManager(tracingEnabled, erasePathData, task.components)
        }

        infoflow.postProcessors = setOf(
            PostAnalysisHandler { results: InfoflowResults, _: IInfoflowCFG -> results }
        )
    }
}
