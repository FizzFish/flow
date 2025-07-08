@file:Suppress(
    "TooManyFunctions",
    "LongParameterList",
    "LargeClass",
    "FunctionName",
)

package cn.sast.framework.engine

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScopeKt.processAIAnalysisUnits
import cn.sast.api.report.*
import cn.sast.api.util.IMonitor
import cn.sast.api.util.OthersKt
import cn.sast.common.*
import cn.sast.dataflow.infoflow.svfa.AP
import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.*
import cn.sast.dataflow.interprocedural.check.checker.CheckerModeling
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValues
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.result.IMissingSummaryReporter
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.framework.result.ResultCollector
import cn.sast.graph.GraphPlot
import cn.sast.graph.GraphPlotKt
import cn.sast.graph.HashMutableDirectedGraph
import cn.sast.idfa.analysis.UsefulMetrics
import com.feysh.corax.cache.analysis.SootInfoCache
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.elapsedSecFrom
import soot.*
import soot.jimple.toolkits.callgraph.ReachableMethods
import soot.toolkits.graph.DirectedGraph
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/* ─────────────────────────────  顶层 JSON 支持  ───────────────────────── */

val graphSerializersModule: SerializersModule = SerializersModule { /* register custom serializers */ }

val graphJson: Json = Json {
    encodeDefaults = true
    useArrayPolymorphism = true
    prettyPrint = true
    isLenient = true
    serializersModule = graphSerializersModule
}

/* ─────────────────────────────  引擎主体  ─────────────────────────────── */

class IPAnalysisEngine(
    private val mainConfig: MainConfig,
    private val summaries: List<SummaryHandlePackage<IValue>> = emptyList(),
) {

    // 前向过程调用图，用于可视化或增量分析
    private val directedGraph: HashMutableDirectedGraph<SootMethod> = HashMutableDirectedGraph()
    private val logger: KLogger = KotlinLogging.logger {}

    /* ---------- ICoverageCollector 扩展：覆盖污点 --------------- */

    context(ICoverageCollector)
    private fun coverTaint(
        hf: AbstractHeapFactory<IValue>,
        method: SootMethod,
        node: Unit,
        succ: Unit,
        out: IFact<IValue>,
        value: Any,
        obj: CompanionV<IValue>,
        visitElement: Boolean,
    ) {
        val actual = PathCompanionKt.getBindDelegate(obj)
        val taintData = out.getValueData(actual, CheckerModelingKt.getKeyTaintProperty())
        if ((taintData as? ImmutableElementSet)?.isNotEmpty() == true) {
            cover(CoverTaint(method, node, value))
        }

        if (visitElement) {
            /* 递归覆盖数组 / 集合元素的污点 */
            val elements = out.getValueData(actual, BuiltInModelT.Array)
            if (elements is IArrayHeapKV<*, *>) {
                elements.getElement(hf).forEach { e ->
                    coverTaint(hf, method, node, succ, out, value, e, false)
                }
            }

            val objVals = out.getValueData(actual, BuiltInModelT.Element)
            if (objVals is ObjectValues) {
                objVals.values.forEach { e ->
                    coverTaint(hf, method, node, succ, out, value, e, false)
                }
            }

            val mapVals = out.getValueData(actual, BuiltInModelT.MapValues)
            if (mapVals is ObjectValues) {
                mapVals.values.forEach { e ->
                    coverTaint(hf, method, node, succ, out, value, e, false)
                }
            }
        }
    }

    /** 简化版入口：自动收集 `node` 中所有 AP 无字段的值。  */
    context(ICoverageCollector)
    private fun coverTaint(
        hf: AbstractHeapFactory<IValue>,
        method: SootMethod,
        node: Unit,
        succ: Unit,
        out: IFact<IValue>,
    ) {
        if (!out.isValid()) return

        val apSet = node.useAndDefBoxes
            .map(ValueBox::getValue)
            .mapNotNull(AP::get)
            .filter { it.field == null }
            .toSet()

        for (ap in apSet) {
            (out as PointsToGraph).getTargets(ap.value).forEach { target ->
                coverTaint(hf, method, node, succ, out, ap.value, target, visitElement = true)
            }
        }
    }

    /* ------------------------  核心分析逻辑  ----------------------------- */

    suspend fun runAnalysisInScene(
        locator: ProjectFileLocator,
        info: SootInfoCache,
        soot: SootCtx,
        preAnalysisResult: IPreAnalysisResultCollector,
        result: IIPAnalysisResultCollector,
        coverage: ICoverageCollector? = null,
        entries: Collection<SootMethod>,
        methodsMustAnalyze: Collection<SootMethod>,
        missWrapper: IMissingSummaryReporter,
    ) = withContext(Dispatchers.Default) {

        /* 全局值域与分析实例 */
        val vg = IVGlobal(null, 1, null)

        val analysis = object : InterProceduralValueAnalysis<IValue>(
            vg, info, result, coverage, this@IPAnalysisEngine
        ) {
            override fun makeContext(
                method: SootMethod,
                cfg: DirectedGraph<Unit>,
                entryValue: IFact<IValue>,
                reverse: Boolean,
                isAnalyzable: Boolean,
            ): AIContext {
                val env = hf.env(cfg.heads.first())
                val ctx = AIContext(info, icfg, result, method, cfg, reverse, isAnalyzable)
                if (entryValue.isValid()) {
                    /* 构造带入口值的上下文 */
                    val builder = entryValue.builder() as PointsToGraphBuilder
                    val kill = KillEntry(method, env)
                    builder.apply(kill.factory)
                    ctx.setEntries(kill.entries)
                    ctx.setEntryValue(builder.build())
                } else {
                    ctx.setEntries(emptySet())
                    ctx.setEntryValue(entryValue)
                }
                return ctx
            }

            override suspend fun normalFlowFunction(
                context: AIContext,
                node: Unit,
                succ: Unit,
                inValue: IFact<IValue>,
                isNegativeBranch: AtomicBoolean,
                completion: kotlin.coroutines.Continuation<IFact<IValue>>,
            ): Any {
                coverage?.cover(CoverInst(context.method, node))

                val outVal = super.normalFlowFunction(
                    context, node, succ, inValue, isNegativeBranch, completion
                ) as IFact<IValue>

                if (coverage?.enableCoveredTaint == true) {
                    coverage.coverTaint(hf, context.method, node, succ, outVal)
                }
                return outVal
            }
        }

        /* 配置分析参数 */
        analysis.apply {
            dataFlowInterProceduralCalleeTimeOut = ExtSettings.INSTANCE.dataFlowInterProceduralCalleeTimeOut
            dataFlowInterProceduralCalleeDepChainMaxNum = ExtSettings.INSTANCE.dataFlowInterProceduralCalleeDepChainMaxNum
            directedGraph = this@IPAnalysisEngine.directedGraph
            numberThreads = mainConfig.parallelsNum
            staticFieldTrackingMode = mainConfig.staticFieldTrackingMode
            analyzeLibraryClasses = !mainConfig.apponly
            needAnalyze = { m ->
                !mainConfig.apponly &&
                        m.declaringClass.isLibraryClass &&
                        locator[ClassResInfo.of(m), NullWrapperFileGenerator.INSTANCE] != null
            }
        }

        /* Pre-analysis for modelling external resources */
        val preAnalysis = PreAnalysisImpl(
            mainConfig,
            locator,
            soot.sootMethodCallGraph,
            info,
            preAnalysisResult,
            Scene.v(),
        )
        val checker = CheckerModeling(mainConfig, analysis.icfg, preAnalysis)
        processAIAnalysisUnits(checker, preAnalysis)

        /* 汇入本次需要的 summaries */
        analysis.summaries.addAll(summaries)
        UsefulMetrics.metrics.warningThreshold = mainConfig.memoryThreshold

        /* 增量过滤入口与强制分析方法 */
        val filteredEntries = mainConfig.interProceduralIncrementalAnalysisFilter(entries)
        val filteredMethods = mainConfig.interProceduralIncrementalAnalysisFilter(methodsMustAnalyze)

        /* 触发一次 GC，释放创建 call-graph 的临时对象 */
        System.gc()

        analysis.doAnalysis(filteredEntries, filteredMethods)
        result.afterAnalyze(analysis)

        /* 采集项目级别指标（若启用） */
        mainConfig.monitor?.projectMetrics?.let { metrics ->
            metrics.analyzedMethodEntries?.addAll(filteredEntries.map { it.signature })

            val reachable = analysis.reachableMethods
            metrics.analyzedApplicationMethods?.addAll(
                reachable.filter { it.declaringClass.isApplicationClass && !OthersKt.isSyntheticComponent(it.declaringClass) && !it.isAbstract }
                    .map(SootMethod::getSignature)
            )
            metrics.analyzedLibraryMethods?.addAll(
                reachable.filter { it.declaringClass.isLibraryClass && !OthersKt.isSyntheticComponent(it.declaringClass) && !it.isAbstract }
                    .map(SootMethod::getSignature)
            )
            metrics.analyzedClasses?.addAll(
                reachable.filter { !OthersKt.isSyntheticComponent(it.declaringClass) && !it.isAbstract }
                    .map(SootMethod::getSignature)
            )
        }

        /* 上报缺失摘要方法 */
        analysis.callBackManager.reportMissSummaryMethod(missWrapper::reportMissingMethod)
    }

    /* ------------------------  统一对外接口  ----------------------------- */

    suspend fun analyze(
        locator: ProjectFileLocator,
        info: SootInfoCache,
        soot: SootCtx,
        provider: IEntryPointProvider,
        result: ResultCollector,
        missWrapper: IMissingSummaryReporter,
    ) {
        TODO("Implement orchestration of entry-point provider to feed runAnalysisInScene()")
    }

    /* ------------------------  调用图导出  ------------------------------- */

    private fun dumpJson(output: IResDirectory, name: String) {
        val jsonFile = output.resolve(name)
        runCatching {
            Files.newOutputStream(jsonFile.path).use { os ->
                val serializer = HashMutableDirectedGraph.serializer(
                    graphSerializersModule.serializer(SootMethod::class)
                ) as SerializationStrategy<Any>
                graphJson.encodeToStream(serializer, directedGraph, os)
            }
            logger.info { "JSON call-graph dumped to: $jsonFile" }
        }.onFailure { logger.error(it) { "Failed to encode call-graph JSON" } }
    }

    private fun dumpDot(output: IResDirectory, name: String) {
        val dotFile = output.resolve(name).toFile()
        val plot = object : GraphPlot<SootClass, SootMethod>(directedGraph) {
            override fun getNodeContainer(method: SootMethod): SootClass = method.declaringClass
        }

        runCatching {
            GraphPlotKt.dump(plot, dotFile)
            logger.info { "DOT call-graph dumped to: $dotFile" }
        }.onFailure { logger.error(it) { "Failed to render DOT graph" } }
    }

    /** 生成简化 / 完整两份调用图（JSON & DOT）。 */
    fun dump(output: IResDirectory) {
        dumpDot(output, "forward_interprocedural_callgraph.dot")
        dumpJson(output, "forward_interprocedural_callgraph.json")

        /* 把 Soot 全局 CG 合并进来生成完整图 */
        Scene.v().callGraph.forEach { edge: Edge ->
            edge.src()?.let { src ->
                edge.tgt()?.let { tgt ->
                    directedGraph.addEdge(src, tgt)
                }
            }
        }

        dumpJson(output, "forward_interprocedural_callgraph_complete.json")
        if (ExtSettings.INSTANCE.dumpCompleteDotCg) {
            dumpDot(output, "forward_interprocedural_callgraph_complete.dot")
        }
    }
}
