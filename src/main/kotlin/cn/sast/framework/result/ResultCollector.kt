package cn.sast.framework.result

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.IResultCollector
import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.dataflow.infoflow.provider.BugTypeProvider
import cn.sast.dataflow.interprocedural.check.InterProceduralValueAnalysis
import cn.sast.dataflow.interprocedural.check.checker.IIPAnalysisResultCollector
import cn.sast.framework.engine.PreAnalysisReportEnv
import cn.sast.framework.graph.CGUtils
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.IReportConsumer
import cn.sast.framework.report.PlistDiagnostics
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.report.ReportConverter
import cn.sast.framework.report.SarifDiagnostics
import cn.sast.framework.report.SarifDiagnosticsCopySrc
import cn.sast.framework.report.SarifDiagnosticsPack
import cn.sast.framework.report.SqliteDiagnostics
import cn.sast.framework.report.coverage.JacocoCompoundCoverage
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.CheckType
import java.io.Closeable
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.IntUnaryOperator
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.Maybe
import soot.SootMethod
import soot.jimple.infoflow.collect.ConcurrentHashSet
import soot.jimple.infoflow.data.AbstractionAtSink
import soot.jimple.infoflow.results.DataFlowResult
import soot.jimple.infoflow.results.InfoflowResults
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.Ref.ObjectRef

@SourceDebugExtension(["SMAP\nResultCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResultCollector.kt\ncn/sast/framework/result/ResultCollector\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n*L\n1#1,284:1\n808#2,11:285\n808#2,11:296\n808#2,11:307\n808#2,11:318\n808#2,11:329\n808#2,11:340\n808#2,11:351\n1863#2,2:362\n1863#2,2:364\n1863#2,2:370\n1557#2:372\n1628#2,3:373\n1863#2,2:376\n1863#2,2:378\n1755#2,3:380\n1863#2,2:383\n1863#2,2:385\n1863#2,2:387\n1557#2:389\n1628#2,3:390\n1#3:366\n1#3:369\n72#4,2:367\n*S KotlinDebug\n*F\n+ 1 ResultCollector.kt\ncn/sast/framework/result/ResultCollector\n*L\n90#1:285,11\n91#1:296,11\n92#1:307,11\n93#1:318,11\n94#1:329,11\n95#1:340,11\n97#1:351,11\n118#1:362,2\n123#1:364,2\n150#1:370,2\n158#1:372\n158#1:373,3\n166#1:376,2\n172#1:378,2\n180#1:380,3\n187#1:383,2\n196#1:385,2\n205#1:387,2\n231#1:389\n231#1:390,3\n132#1:369\n132#1:367,2\n*E\n"])
class ResultCollector(
    private val mainConfig: MainConfig,
    private val info: SootInfoCache?,
    private val outputDir: IResDirectory,
    private val locator: ProjectFileLocator,
    private val collectors: List<IResultCollector> = emptyList(),
    private val outputTypes: List<OutputType> = emptyList(),
    private val serializeTaintPath: Boolean = true,
    private val resultConverter: ResultConverter = ResultConverter(info),
    val coverage: JacocoCompoundCoverage = JacocoCompoundCoverage(locator, null, null, false, 14, null),
    val flushCoverage: Boolean = false,
    val monitor: MetricsMonitor
) : IFlowDroidResultCollector, IIPAnalysisResultCollector, IUTBotResultCollector, IMissingSummaryReporter, IPreAnalysisResultCollector, IBuiltInAnalysisCollector, IReportsVisitor {

    private val collectorsFlowDroid: List<IFlowDroidResultCollector>
    private val collectorsUTBot: List<IUTBotResultCollector>
    private val collectorsDataFlow: List<IIPAnalysisResultCollector>
    private val collectorsIFIChecker: List<IPreAnalysisResultCollector>
    private val collectorsFlowSensitive: List<IBuiltInAnalysisCollector>
    private val reportsVisitor: List<IReportsVisitor>
    private val missingSummaryReporter: List<IMissingSummaryReporter>
    private val reports: ConcurrentHashSet<Report> = ConcurrentHashSet()
    private val purificationReports: ConcurrentHashMap<PurificationReportKey, AtomicInteger> = ConcurrentHashMap()

    lateinit var preAnalysis: PreAnalysisCoroutineScope
        internal set

    private val bugTypeProvider by lazy { bugTypeProvider_delegate$lambda$2(this) }

    private var flushing: Boolean = false

    init {
        collectorsFlowDroid = collectors.filterIsInstance<IFlowDroidResultCollector>().toMutableList()
        collectorsUTBot = collectors.filterIsInstance<IUTBotResultCollector>().toMutableList()
        collectorsDataFlow = collectors.filterIsInstance<IIPAnalysisResultCollector>().toMutableList()
        collectorsIFIChecker = collectors.filterIsInstance<IPreAnalysisResultCollector>().toMutableList()
        collectorsFlowSensitive = collectors.filterIsInstance<IBuiltInAnalysisCollector>().toMutableList()
        reportsVisitor = collectors.filterIsInstance<IReportsVisitor>().toMutableList()
        missingSummaryReporter = collectors.filterIsInstance<IMissingSummaryReporter>().toMutableList()
    }

    fun getReports(): Set<Report> {
        flushing = true
        return reports
    }

    fun getCollectors(): List<IResultCollector> = collectors

    override fun report(checkType: CheckType, info: PreAnalysisReportEnv) {
        collectorsIFIChecker.forEach { it.report(checkType, info) }
        addReport(resultConverter.getReport(checkType, info))
    }

    override fun report(report: Report) {
        collectorsFlowSensitive.forEach { it.report(report) }
        addReport(report)
    }

    private fun addReport(reports: Collection<Report>) {
        if (flushing) {
            throw IllegalArgumentException("internal error: emit bug reports when flush report")
        }
        reports.forEach { report ->
            if (mainConfig.isEnable(report.checkType)) {
                val key = PurificationReportKey(
                    report.bugResFile,
                    report.region.startLine,
                    report.check_name,
                    report.pathEvents.first() as BugPathEvent
                )
                val counter = purificationReports.getOrPut(key) { AtomicInteger(0) }
                counter.getAndUpdate { count ->
                    if (count > 5) count else if (reports.add(report)) count + 1 else count
                }
            }
        }
    }

    private fun addReport(report: Report) = addReport(listOf(report))

    override fun accept(reports: Collection<Report>) {
        reportsVisitor.forEach { it.accept(reports) }
    }

    override suspend fun flush() {
        monitor.addAnalyzeFinishHook(Thread {
            CGUtils.INSTANCE.flushMissedClasses(outputDir)
        })

        val job1 = BuildersKt.launch(CoroutineScope(Dispatchers.Default)) {
            accept(getReports())
        }

        val jobs = collectors.map { collector ->
            BuildersKt.launch(CoroutineScope(Dispatchers.Default)) {
                collector.flush()
            }
        }

        val job2 = BuildersKt.launch(CoroutineScope(Dispatchers.Default)) {
            flushOutputType(mainConfig, monitor)
        }

        job1.join()
        AwaitKt.joinAll(jobs)
        job2.join()
    }

    override fun reportMissingMethod(method: SootMethod) {
        missingSummaryReporter.forEach { it.reportMissingMethod(method) }
    }

    override fun onResultsAvailable(cfg: IInfoflowCFG, results: InfoflowResults) {
        collectorsFlowDroid.forEach { it.onResultsAvailable(cfg, results) }
        results.resultSet?.forEach { result ->
            addReport(resultConverter.getReport(cfg, result, bugTypeProvider, serializeTaintPath))
        }
    }

    override fun onResultAvailable(icfg: IInfoflowCFG, abs: AbstractionAtSink): Boolean {
        return collectorsFlowDroid.none { !it.onResultAvailable(icfg, abs) }
    }

    override fun afterAnalyze(analysis: InterProceduralValueAnalysis) {
        collectorsDataFlow.forEach { it.afterAnalyze(analysis) }
    }

    override fun reportDataFlowBug(reports: List<Report>) {
        collectorsDataFlow.forEach { it.reportDataFlowBug(reports) }
        addReport(reports)
    }

    override fun addUtState() {
        collectorsUTBot.forEach { it.addUtState() }
    }

    private suspend fun flushOutputType(mainConfig: MainConfig, monitor: MetricsMonitor) {
        val outputTypesLocal = outputTypes.toMutableSet()
        if (outputTypesLocal.isEmpty()) {
            logger.warn { "not special any output types! Will use PLIST and SARIF formats and SQLITE for generating report" }
            outputTypesLocal.add(OutputType.PLIST)
            outputTypesLocal.add(OutputType.SARIF)
        }

        if (flushCoverage) {
            outputTypesLocal.add(OutputType.Coverage)
        }

        val consumers = outputTypesLocal.map { type ->
            when (type) {
                OutputType.PLIST -> PlistDiagnostics(mainConfig, info, outputDir.resolve(type.displayName).toDirectory())
                OutputType.SARIF -> SarifDiagnostics(outputDir.resolve(type.displayName).toDirectory())
                OutputType.SARIF_PACK -> SarifDiagnosticsPack(outputDir.resolve(type.displayName).toDirectory())
                OutputType.SARIF_COPY_SRC -> SarifDiagnosticsCopySrc(outputDir.resolve(type.displayName).toDirectory())
                OutputType.SQLITE -> {
                    val diagnostics = SqliteDiagnostics(mainConfig, info, monitor, outputDir.resolve(OutputType.SQLITE.displayName).toDirectory())
                    monitor.addAnalyzeFinishHook(Thread {
                        runBlocking {
                            diagnostics.open()
                            diagnostics.writeAnalyzerResultFiles()
                        }
                    })
                    diagnostics
                }
                OutputType.Coverage -> getCoverageReportWriter()
                else -> throw NoWhenBranchMatchedException()
            }
        }

        ReportConverter(mainConfig).flush(mainConfig, locator, coverage, consumers, reports, outputDir)
    }

    private fun getCoverageReportWriter(): IReportConsumer = object : IReportConsumer {
        override fun getType(): OutputType = OutputType.Coverage

        override suspend fun init(continuation: Continuation<Unit>): Any? = Unit

        override suspend fun run(locator: IProjectFileLocator, continuation: Continuation<Unit>): Any? {
            return try {
                val startTime = LocalDateTime.now()
                logger.info { "Calculate coverage data ..." }

                val result = Maybe.empty<Unit>()
                coverage.copy(projectFileLocator = locator).flush(
                    mainConfig.output_dir,
                    mainConfig.sourceEncoding
                )
                result.set(Unit)

                if (result.hasValue) {
                    logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Calculate coverage data" }
                } else {
                    logger.info { "Finished (in ${elapsedSecFrom(startTime)}: Calculate coverage data <Nothing>" }
                }
                Unit
            } catch (t: Throwable) {
                logger.error("Calculate coverage data failed", t)
                throw t
            }
        }

        override fun close() {}
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")

        fun newSqliteDiagnostics(
            mainConfig: MainConfig,
            info: SootInfoCache?,
            outputDir: IResDirectory,
            monitor: MetricsMonitor?
        ): SqliteDiagnostics {
            return object : SqliteDiagnostics(mainConfig, info, monitor, outputDir.resolve(OutputType.SQLITE.displayName).toDirectory()) {
                override fun getSourceEncoding(file: IResFile): Charset = mainConfig.sourceEncoding
            }
        }

        @JvmStatic
        private fun bugTypeProvider_delegate$lambda$2(collector: ResultCollector): BugTypeProvider {
            Thread.interrupted()
            return BugTypeProvider(collector.mainConfig, collector.preAnalysis).apply { init() }
        }
    }
}

private fun elapsedSecFrom(start: LocalDateTime): String = TODO("Implement elapsed time calculation")