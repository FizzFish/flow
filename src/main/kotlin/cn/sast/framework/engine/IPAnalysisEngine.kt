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
class IPAnalysisEngine(
    val mainConfig: MainConfig,
    val summaries: List<SummaryHandlePackage<IValue>> = ArrayList()
) {
    private val directedGraph: HashMutableDirectedGraph<SootMethod> = HashMutableDirectedGraph()

    fun ICoverageCollector.coverTaint(
        hf: AbstractHeapFactory<IValue>,
        method: SootMethod,
        node: Unit,
        succ: Unit,
        out: IFact<IValue>,
        value: Any,
        obj: CompanionV<IValue>,
        visitElement: Boolean
    ) {
        val actual: IValue = PathCompanionKt.getBindDelegate(obj)
        val array: IData = out.getValueData(actual, CheckerModelingKt.getKeyTaintProperty())
        if ((array as? ImmutableElementSet)?.isNotEmpty() == true) {
            this@coverTaint.cover(CoverTaint(method, node, value))
        }

        if (visitElement) {
            val elements: IData = out.getValueData(actual, BuiltInModelT.Array)
            if (elements is IArrayHeapKV) {
                for (e in array.getElement(hf)) {
                    coverTaint(hf, method, node, succ, out, value, e, false)
                }
            }

            val elementData: IData = out.getValueData(actual, BuiltInModelT.Element)
            if (elementData is ObjectValues) {
                for (e in elements.getValues()) {
                    coverTaint(hf, method, node, succ, out, value, e, false)
                }
            }

            val mapValuesData: IData = out.getValueData(actual, BuiltInModelT.MapValues)
            if (mapValuesData is ObjectValues) {
                for (e in mapValuesData.getValues()) {
                    coverTaint(hf, method, node, succ, out, value, e, false)
                }
            }
        }
    }

    fun ICoverageCollector.coverTaint(
        hf: AbstractHeapFactory<IValue>,
        method: SootMethod,
        node: Unit,
        succ: Unit,
        out: IFact<IValue>
    ) {
        if (out.isValid()) {
            val boxes = node.getUseAndDefBoxes()
            val values = boxes.map { it.value }
            val accessPaths = values.mapNotNull { AP.Companion.get(it) }.toSet()

            for (accessPath in accessPaths) {
                if (accessPath.field == null) {
                    for (obj in (out as PointsToGraph).getTargets(accessPath.value)) {
                        coverTaint(hf, method, node, succ, out, accessPath.value, obj, true)
                    }
                }
            }
        }
    }

    suspend fun runAnalysisInScene(
        locator: ProjectFileLocator,
        info: SootInfoCache,
        soot: SootCtx,
        preAnalysisResult: IPreAnalysisResultCollector,
        result: IIPAnalysisResultCollector,
        coverage: ICoverageCollector? = null,
        entries: Collection<SootMethod>,
        methodsMustAnalyze: Collection<SootMethod>,
        missWrapper: IMissingSummaryReporter
    ) {
        val vg = IVGlobal(null, 1, null)
        val analysis = object : InterProceduralValueAnalysis(vg, info, result, coverage, this) {
            override fun makeContext(
                method: SootMethod,
                cfg: DirectedGraph<Unit>,
                entryValue: IFact<IValue>,
                reverse: Boolean,
                isAnalyzable: Boolean
            ): AIContext {
                val env = hf.env(cfg.heads.first())
                return if (entryValue.isValid()) {
                    val context = AIContext(info, icfg, result, method, cfg, reverse, isAnalyzable)
                    val entryBuilder = entryValue.builder() as PointsToGraphBuilder
                    val kill = KillEntry(method, env)
                    entryBuilder.apply(kill.factory)
                    context.setEntries(kill.entries)
                    context.setEntryValue(entryBuilder.build())
                    context
                } else {
                    val context = AIContext(info, icfg, result, method, cfg, reverse, isAnalyzable)
                    context.setEntries(emptySet())
                    context.setEntryValue(entryValue)
                    context
                }
            }

            override suspend fun normalFlowFunction(
                context: AIContext,
                node: Unit,
                succ: Unit,
                inValue: IFact<IValue>,
                isNegativeBranch: AtomicBoolean,
                completion: Continuation<IFact<IValue>>
            ): Any {
                if (coverage != null) {
                    coverage.cover(CoverInst(context.method, node))
                }

                val outValue = super.normalFlowFunction(context, node, succ, inValue, isNegativeBranch, completion) as IFact<IValue>

                if (coverage != null && coverage.enableCoveredTaint) {
                    this@IPAnalysisEngine.coverTaint(coverage, hf, context.method, node, succ, outValue)
                }

                return outValue
            }
        }

        analysis.apply {
            dataFlowInterProceduralCalleeTimeOut = ExtSettings.INSTANCE.dataFlowInterProceduralCalleeTimeOut
            dataFlowInterProceduralCalleeDepChainMaxNum = ExtSettings.INSTANCE.dataFlowInterProceduralCalleeDepChainMaxNum
            directedGraph = this@IPAnalysisEngine.directedGraph
            numberThreads = mainConfig.parallelsNum
            staticFieldTrackingMode = mainConfig.staticFieldTrackingMode
            analyzeLibraryClasses = !mainConfig.apponly
            needAnalyze = { method -> !mainConfig.apponly && method.declaringClass.isLibraryClass && locator.get(ClassResInfo.of(method), NullWrapperFileGenerator.INSTANCE) != null }
        }

        val preAnalysis = PreAnalysisImpl(mainConfig, locator, soot.sootMethodCallGraph, info, preAnalysisResult, Scene.v())
        val checker = CheckerModeling(mainConfig, analysis.icfg, preAnalysis)

        PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(checker, preAnalysis)

        summaries.forEach { analysis.summaries.add(it) }
        UsefulMetrics.Companion.metrics.warningThreshold = mainConfig.memoryThreshold

        val filteredEntries = mainConfig.interProceduralIncrementalAnalysisFilter(entries)
        val filteredMethods = mainConfig.interProceduralIncrementalAnalysisFilter(methodsMustAnalyze)

        System.gc()
        analysis.doAnalysis(filteredEntries, filteredMethods)
        result.afterAnalyze(analysis)

        mainConfig.monitor?.projectMetrics?.let { metrics ->
            metrics.analyzedMethodEntries?.addAll(filteredEntries.map { it.signature })
            
            val reachableMethods = analysis.reachableMethods
            metrics.analyzedApplicationMethods?.addAll(
                reachableMethods
                    .filter { it.declaringClass?.isApplicationClass == true }
                    .filter { !OthersKt.isSyntheticComponent(it.declaringClass) && !it.isAbstract }
                    .map { it.signature }
            )
            
            metrics.analyzedLibraryMethods?.addAll(
                reachableMethods
                    .filter { it.declaringClass?.isLibraryClass == true }
                    .filter { !OthersKt.isSyntheticComponent(it.declaringClass) && !it.isAbstract }
                    .map { it.signature }
            )
            
            metrics.analyzedClasses?.addAll(
                reachableMethods
                    .filter { !OthersKt.isSyntheticComponent(it.declaringClass) && !it.isAbstract }
                    .map { it.signature }
            )
        }

        analysis.callBackManager.reportMissSummaryMethod { miss -> missWrapper.reportMissingMethod(miss) }
    }

    suspend fun analyze(
        locator: ProjectFileLocator,
        info: SootInfoCache,
        soot: SootCtx,
        provider: IEntryPointProvider,
        result: ResultCollector,
        missWrapper: IMissingSummaryReporter
    ) {
        TODO("FIXME - Couldn't be decompiled")
    }

    private fun dumpJson(output: IResDirectory, name: String) {
        val cgJson = output.resolve(name)
        Files.newOutputStream(cgJson.path).use { out ->
            try {
                val json = IPAnalysisEngineKt.graphJson
                val serializer = HashMutableDirectedGraph.Companion.serializer(
                    SerializersKt.noCompiledSerializer(json.serializersModule, SootMethod::class)
                ) as SerializationStrategy<Any>
                JvmStreamsKt.encodeToStream(json, serializer, directedGraph, out)
                logger.info { "json call graph: $cgJson" }
            } catch (e: Exception) {
                logger.error("failed to encodeToStream jsonGraph", e)
            }
        }
    }

    private fun dumpDot(output: IResDirectory, name: String) {
        val cg = output.resolve(name).toFile()
        val plot = object : GraphPlot<SootClass, SootMethod>(directedGraph) {
            override fun getNodeContainer(method: SootMethod): SootClass = method.declaringClass
        }

        try {
            GraphPlotKt.dump(plot, cg)
            logger.info { "dot call graph: $cg" }
        } catch (e: Exception) {
            logger.error("failed to render dotGraph", e)
        }
    }

    fun dump(output: IResDirectory) {
        dumpDot(output, "forward_interprocedural_callgraph.dot")
        dumpJson(output, "forward_interprocedural_callgraph.json")

        Scene.v().callGraph.forEach { edge ->
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

    companion object {
        private val logger: KLogger = TODO("Initialize logger")
    }
}