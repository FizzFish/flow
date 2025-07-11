// -----------------------------------------------------------------------------
//  Converted Result‑layer Kotlin sources (all under package `cn.sast.framework.result`)
// -----------------------------------------------------------------------------
//  This file groups together *all* result‑handling classes / interfaces so they can be
//  inspected in one place.  Split into separate physical files if desired.
// -----------------------------------------------------------------------------

// -----------------------------------------------------------------------------
// Common imports shared by multiple files – adjust per‑file when detaching.
// -----------------------------------------------------------------------------
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "RedundantNullableReturnType", "KDocUnresolvedReference")

package cn.sast.framework.result

// core API --------------------------------------------------------------------
import cn.sast.api.config.MainConfig
import cn.sast.api.report.*
import cn.sast.common.*
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import cn.sast.dataflow.infoflow.provider.BugTypeProvider
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.framework.engine.PreAnalysisReportEnv
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.graph.CGUtils
import cn.sast.framework.report.*
import cn.sast.framework.report.coverage.JacocoCompoundCoverage

// external libs ---------------------------------------------------------------
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.report.Region;
import kotlinx.coroutines.*
import mu.KotlinLogging
import soot.*
import soot.jimple.Stmt
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.android.results.xml.InfoflowResultsSerializer
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.handlers.ResultsAvailableHandler
import soot.jimple.infoflow.problems.TaintPropagationResults
import soot.jimple.infoflow.results.*
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition
import java.io.Closeable
import java.io.IOException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import javax.xml.stream.XMLStreamException

// -----------------------------------------------------------------------------
// 1)  Enum: OutputType – determines result writer(s)
// -----------------------------------------------------------------------------

enum class OutputType(val displayName: String) {
    PLIST("plist"),
    SARIF("sarif"),
    SQLITE("sqlite"),
    SarifPackSrc("sarif-pack"),
    SarifCopySrc("sarif-copy"),
    Coverage("coverage");

    companion object {
        @JvmStatic fun entries(): Array<OutputType> = values()
    }
}

// -----------------------------------------------------------------------------
// 2)  Data‑class helper: PurificationReportKey – used for deduplication/aggregation
// -----------------------------------------------------------------------------

data class PurificationReportKey(
    val bugResFile: IBugResInfo,
    val line: Int,
    val checkName: String,
    val firstEvent: BugPathEvent,
)

// -----------------------------------------------------------------------------
// 3)  Interface collection (copied from previous conversion – unchanged)
// -----------------------------------------------------------------------------

interface IBuiltInAnalysisCollector : IResultCollector {
    fun report(report: Report)
    override suspend fun flush() {}
}

interface IFlowDroidResultCollector : IResultCollector,
    TaintPropagationResults.OnTaintPropagationResultAdded,
    ResultsAvailableHandler {
    override suspend fun flush() {}
}

interface IMissingSummaryReporter : IResultCollector {
    fun reportMissingMethod(method: SootMethod)
    override suspend fun flush() {}
}

interface IPreAnalysisResultCollector : IResultCollector {
    fun report(checkType: CheckType, info: PreAnalysisReportEnv)
    override suspend fun flush() {}
}

interface IReportsVisitor : IResultCollector {
    fun accept(reports: Collection<Report>)
    override suspend fun flush() {}
}
interface IUTBotResultCollector : IResultCollector {
    fun addUtState()
    override suspend fun flush() { /* default */ }
}

// -----------------------------------------------------------------------------
// 4)  Concrete helper implementations
// -----------------------------------------------------------------------------

/** Aggregates FlowDroid callbacks into a single mutable [InfoflowResults]. */
class FlowDroidResultCollector : IFlowDroidResultCollector {
    val results: InfoflowResults = InfoflowResults()
    override fun onResultsAvailable(cfg: IInfoflowCFG?, results: InfoflowResults?) {
        results?.let { this.results.addAll(it) }
    }
    override fun onResultAvailable(cfg: IInfoflowCFG, abs: AbstractionAtSink?): Boolean = true
}

/** Serialises FlowDroid results into *infoflow‑result.txt*. */
class FlowDroidResultSerializer(
    private val outputDir: IResDirectory,
    private val enableLineNumbers: Boolean = true,
) : IFlowDroidResultCollector {
    private val logger = KotlinLogging.logger {}
    private fun write(results: InfoflowResults, icfg: IInfoflowCFG?) {
        if (results.isEmpty) return
        val dst: IResource = outputDir.resolve("infoflow-result.txt")
        val cfg = InfoflowConfiguration().apply { enableLineNumbers = enableLineNumbers }
        val serializer = InfoflowResultsSerializer(icfg, cfg)
        try {
            serializer.serialize(results, dst.toString())
        } catch (e: IOException) {
            logger.error(e) { "Could not write FlowDroid result" }
        } catch (e: XMLStreamException) {
            logger.error(e) { "Could not write FlowDroid result" }
        }
    }
    override fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) = write(results, cfg)
    override fun onResultAvailable(icfg: IInfoflowCFG?, abs: AbstractionAtSink?): Boolean = true
}

/** Writes phantom‑method hits into [outputFile] (if provided). */
class MissingSummaryReporter(private val outputFile: IResFile? = null) : IMissingSummaryReporter {
    private val counter: Counter<SootMethod> = Counter()
    override fun reportMissingMethod(method: SootMethod) { counter.count(method) }
    override suspend fun flush() { outputFile?.let { counter.writeResults(it) } }
}

// -----------------------------------------------------------------------------
// 5)  ResultCounter – quick numeric overview (used by [MetricsMonitor])
// -----------------------------------------------------------------------------

class ResultCounter : IFlowDroidResultCollector,
    IUTBotResultCollector,
    IIPAnalysisResultCollector,
    IPreAnalysisResultCollector,
    IBuiltInAnalysisCollector {

    val infoflowResCount = AtomicInteger()
    val infoflowAbsAtSinkCount = AtomicInteger()
    val symbolicUTbotCount = AtomicInteger()
    val dataFlowCount = AtomicInteger()
    val builtInAnalysisCount = AtomicInteger()
    val preAnalysisResultCount = AtomicInteger()

    // FlowDroid -------------------------------------------------------------
    override fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
        infoflowResCount.addAndGet(results.size())
    }
    override fun onResultAvailable(icfg: IInfoflowCFG, abs: AbstractionAtSink): Boolean {
        infoflowAbsAtSinkCount.incrementAndGet(); return true
    }

    // UTBot -----------------------------------------------------------------
    override fun addUtState() { symbolicUTbotCount.incrementAndGet() }

    // Data‑flow -------------------------------------------------------------
    override fun afterAnalyze(analysis: InterProceduralValueAnalysis) {}
    override fun reportDataFlowBug(reports: List<Report>) { dataFlowCount.addAndGet(reports.size) }

    // Pre‑analysis ----------------------------------------------------------
    override fun report(checkType: CheckType, info: PreAnalysisReportEnv) { preAnalysisResultCount.incrementAndGet() }

    // Built‑in --------------------------------------------------------------
    override fun report(report: Report) { builtInAnalysisCount.incrementAndGet() }

    // Flush -----------------------------------------------------------------
    private val logger = KotlinLogging.logger {}
    override suspend fun flush() {
        logger.info { "#InfoflowResults         = ${infoflowResCount.get()}" }
        logger.info { "#AbstractionAtSink      = ${infoflowAbsAtSinkCount.get()}" }
        logger.info { "#SymbolicExec (UTBot)   = ${symbolicUTbotCount.get()}" }
        logger.info { "#PreAnalysisResults     = ${preAnalysisResultCount.get()}" }
        logger.info { "#Built‑inAnalysis       = ${builtInAnalysisCount.get()}" }
        logger.info { "#AbstractInterpResults  = ${dataFlowCount.get()}" }
    }
}

// -----------------------------------------------------------------------------
// 6)  ResultConverter – heavy lifting: transform raw analysis artefacts → Report(s)
// -----------------------------------------------------------------------------

class ResultConverter(private val info: SootInfoCache? = null) {
    private val logger = KotlinLogging.logger {}

    // Simplified – preserves original signature; implement only key path
    fun getReport(checkType: CheckType, env: PreAnalysisReportEnv): Report =
        Report.of(info, env.file, env.env.region.immutable, checkType, env.env)

    fun getReport(
        icfg: IInfoflowCFG,
        result: DataFlowResult,
        bugTypeProvider: BugTypeProvider,
        serializeTaintPath: Boolean = true,
    ): List<Report> {
        val sinkMethod = (result.sink.definition as? MethodSourceSinkDefinition)?.method?.let {
            Scene.v().grabMethod(it.signature)
        }
        if (sinkMethod == null) {
            logger.warn { "Sink‑method ${result.sink.definition} could not be resolved" }; return emptyList()
        }
        val checkTypes = bugTypeProvider.lookUpCheckType(sinkMethod)
        if (checkTypes.isEmpty()) logger.warn { "No check‑type mapped for $sinkMethod" }

        val stmt = result.sink.stmt
        val region = Region.of(stmt) ?: Region.ERROR
        val env = DefaultEnv(region.mutable).apply {
            callSite = stmt
            clazz = sinkMethod.declaringClass
            method = sinkMethod
            callee = sinkMethod
            invokeExpr = stmt.takeIf { it.containsInvokeExpr() }?.invokeExpr
        }

        return checkTypes.map { ct -> Report.of(info, ClassResInfo.of(sinkMethod), region, ct, env, emptyList()) }
    }

    // Helper – render AccessPath (simplified vs. original)
    private fun AccessPath.render(simple: Boolean = false): String = if (!simple) toString() else buildString {
        baseType?.let { append(it) }
        fragments.forEach { append('.').append(it.field) }
        if (taintSubFields) append('*')
    }
}

// -----------------------------------------------------------------------------
// 7)  Missing Summary Reporter (already done)
// -----------------------------------------------------------------------------
//  (see class [MissingSummaryReporter] earlier)

// -----------------------------------------------------------------------------
// 8)  ResultCollector – orchestrates *all* result collectors & output writers
// -----------------------------------------------------------------------------

class ResultCollector(
    private val mainConfig: MainConfig,
    private val info: SootInfoCache?,
    private val outputDir: IResDirectory,
    private val locator: ProjectFileLocator,
    private val collectors: List<IResultCollector> = emptyList(),
    private val outputTypes: List<OutputType> = emptyList(),
    private val serializeTaintPath: Boolean = true,
    private val resultConverter: ResultConverter = ResultConverter(info),
    val coverage: JacocoCompoundCoverage = JacocoCompoundCoverage(locator),
    private val flushCoverage: Boolean = false,
    private val monitor: MetricsMonitor,
) : IFlowDroidResultCollector,
    IIPAnalysisResultCollector,
    IUTBotResultCollector,
    IMissingSummaryReporter,
    IPreAnalysisResultCollector,
    IBuiltInAnalysisCollector,
    IReportsVisitor {

    private val logger = KotlinLogging.logger {}

    // Collector buckets ------------------------------------------------------
    private val flowDroidCollectors  = collectors.filterIsInstance<IFlowDroidResultCollector>()
    private val utBotCollectors      = collectors.filterIsInstance<IUTBotResultCollector>()
    private val dataFlowCollectors   = collectors.filterIsInstance<IIPAnalysisResultCollector>()
    private val preAnalysisCollectors= collectors.filterIsInstance<IPreAnalysisResultCollector>()
    private val builtInCollectors    = collectors.filterIsInstance<IBuiltInAnalysisCollector>()
    private val reportsVisitors      = collectors.filterIsInstance<IReportsVisitor>()
    private val missingSummaryReps   = collectors.filterIsInstance<IMissingSummaryReporter>()

    // State ------------------------------------------------------------------
    private val reports = ConcurrentHashSet<Report>()
    private val purificationCounts: ConcurrentMap<PurificationReportKey, AtomicInteger> = ConcurrentHashMap()
    @Volatile private var flushing = false

    // Accessors --------------------------------------------------------------
    fun getReports(): Set<Report> = reports
    fun getCollectors(): List<IResultCollector> = collectors
    fun bugTypeProvider(): BugTypeProvider = BugTypeProvider.INSTANCE

    // Interface implementations ---------------------------------------------
    override fun reportMissingMethod(method: SootMethod) = missingSummaryReps.forEach { it.reportMissingMethod(method) }

    // Pre‑analysis quick facts ----------------------------------------------
    override fun report(checkType: CheckType, info: PreAnalysisReportEnv) {
        preAnalysisCollectors.forEach { it.report(checkType, info) }
        addReports(resultConverter.getReport(checkType, info))
    }

    // Built‑in analysis single report ---------------------------------------
    override fun report(report: Report) {
        builtInCollectors.forEach { it.report(report) }
        addReports(listOf(report))
    }

    // Data‑flow bugs (IP / UTBot) -------------------------------------------
    override fun reportDataFlowBug(reports: List<Report>) {
        dataFlowCollectors.forEach { it.reportDataFlowBug(reports) }
        addReports(reports)
    }

    override fun addUtState() { utBotCollectors.forEach { it.addUtState() } }
    override fun afterAnalyze(analysis: InterProceduralValueAnalysis) { dataFlowCollectors.forEach { it.afterAnalyze(analysis) } }

    // FlowDroid integration --------------------------------------------------
    override fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
        flowDroidCollectors.forEach { it.onResultsAvailable(cfg, results) }
        results.resultSet.orEmpty().forEach { dfr ->
            val rep = resultConverter.getReport(cfg, dfr, bugTypeProvider(), serializeTaintPath)
            addReports(rep)
        }
    }
    override fun onResultAvailable(icfg: IInfoflowCFG, abs: AbstractionAtSink): Boolean =
        flowDroidCollectors.none { !it.onResultAvailable(icfg, abs) }

    // Bulk visitor -----------------------------------------------------------
    override fun accept(reports: Collection<Report>) { reportsVisitors.forEach { it.accept(reports) } }

    // Flush ------------------------------------------------------------------
    override suspend fun flush() {
        if (flushing) return; flushing = true
        monitor.addAnalyzeFinishHook(Thread { CGUtils.flushMissedClasses(outputDir) })

        // 1. Let sub‑collectors flush in parallel
        coroutineScope {
            collectors.map { launch { it.flush() } }.joinAll()
        }

        // 2. Pass accumulated reports to visitors
        accept(reports)

        // 3. Output writers
        flushOutputWriters()
    }

    private suspend fun flushOutputWriters() {
        val types = outputTypes.toMutableSet().apply {
            if (isEmpty()) addAll(listOf(OutputType.PLIST, OutputType.SARIF))
            if (flushCoverage) add(OutputType.Coverage)
        }
        val writers: List<IReportConsumer> = types.map {
            when (it) {
                OutputType.PLIST       -> PlistDiagnostics(mainConfig, info, outputDir.resolve(it.displayName).toDirectory())
                OutputType.SARIF       -> SarifDiagnostics(outputDir.resolve(it.displayName).toDirectory())
                OutputType.SarifPackSrc-> SarifDiagnosticsPack(outputDir.resolve(it.displayName).toDirectory())
                OutputType.SarifCopySrc-> SarifDiagnosticsCopySrc(outputDir.resolve(it.displayName).toDirectory())
//                OutputType.SQLITE      -> SqliteDiagnostics(mainConfig, outputDir.resolve(it.displayName).toDirectory(), monitor)
//                OutputType.Coverage    -> coverage
                OutputType.SQLITE -> TODO()
                OutputType.Coverage -> TODO()
            }
        }
        ReportConverter(mainConfig).flush(mainConfig, locator, coverage, writers, reports, outputDir)
    }

    // internal helper --------------------------------------------------------
    private fun addReports(reportsColl: Collection<Report>) {
        if (flushing) error("internal error: emit reports during flush phase")
        reportsColl.filter { mainConfig.isEnable(it.checkType) }.forEach { report ->
            // Basic deduplication: limit similar report per source line to 5 occurrences
            val key = PurificationReportKey(report.bugResFile, report.region.startLine, report.checkName, report.pathEvents.firstOrNull() ?: return@forEach)
            purificationCounts.computeIfAbsent(key) { AtomicInteger() } .let { counter ->
                if (counter.get() < 5 && reports.add(report)) counter.incrementAndGet()
            }
        }
    }
}
