package cn.sast.framework.metrics

import cn.sast.api.config.MainConfigKt
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.report.Report
import cn.sast.api.util.IMonitor
import cn.sast.api.util.PhaseIntervalTimerKt
import cn.sast.api.util.Timer
import cn.sast.common.CustomRepeatingTimer
import cn.sast.common.IResDirectory
import cn.sast.framework.result.ResultCollector
import cn.sast.framework.result.ResultCounter
import cn.sast.idfa.analysis.UsefulMetrics
import com.charleskorn.kaml.Yaml
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.LongUnaryOperator
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModuleBuilder
import org.eclipse.microprofile.metrics.Gauge
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.Map.Entry
import kotlin.collections.addAll
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4
import kotlin.collections.component5
import kotlin.collections.component6
import kotlin.collections.firstOrNull
import kotlin.collections.mapOf
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.comparisons.compareValues

@Serializable
@SourceDebugExtension(["SMAP\nMetricsMonitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitor\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 6 SerializersModuleBuilders.kt\nkotlinx/serialization/modules/SerializersModuleBuildersKt\n*L\n1#1,249:1\n72#2,2:250\n1#3:252\n1#3:253\n1485#4:254\n1510#4,3:255\n1513#4,3:265\n1246#4,4:270\n1246#4,4:276\n808#4,11:280\n1863#4,2:291\n381#5,7:258\n462#5:268\n412#5:269\n477#5:274\n423#5:275\n31#6,3:293\n*S KotlinDebug\n*F\n+ 1 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitor\n*L\n92#1:250,2\n92#1:252\n159#1:254\n159#1:255,3\n159#1:265,3\n159#1:270,4\n160#1:276,4\n165#1:280,11\n218#1:291,2\n159#1:258,7\n159#1:268\n159#1:269\n160#1:274\n160#1:275\n228#1:293,3\n*E\n"])
class MetricsMonitor : IMonitor {
    private var beginDate: String = ""
    val beginMillis: Long = System.currentTimeMillis()
    private var elapsedSeconds: Double = -1.0
    private var elapsedTime: String = ""
    private var endDate: String = ""
    private var endTime: Long = 0L
    private var jvmMemoryUsedMax: Double = -1.0
    private val jvmMemoryMax: Double
    val projectMetrics: ProjectMetrics
    private val phaseTimer: MutableList<PhaseTimer> = mutableListOf()
    private val final: MutableMap<String, Any> = LinkedHashMap()
    private val reports: MutableList<ReportKey> = mutableListOf()
    private val snapshot: MutableList<MetricsSnapshot> = mutableListOf()

    @Transient
    val beginNanoTime: Long = PhaseIntervalTimerKt.currentNanoTime()

    @Transient
    private val allPhaseTimer: ConcurrentMap<String, Timer> = ConcurrentHashMap()

    @Transient
    private val analyzeFinishHook: MutableList<Thread> = mutableListOf()

    @Transient
    val maxUsedMemory: AtomicLong = AtomicLong(0L)

    @Transient
    private val timer: CustomRepeatingTimer

    private val g: Double
        get() = TODO("FIXME — Original logic was unclear")

    init {
        val gauge = UsefulMetrics.Companion.getMetrics().getJvmMemoryMax()
        this.jvmMemoryMax = MetricsMonitorKt.inMemGB$default(if (gauge != null) this.getG(gauge) else null, 0, 1, null)
        this.projectMetrics = ProjectMetrics(null, null, 0, 0, 0, 0, 0, 0, 0.0F, 0, 0.0F, 0, 0, 0, 0L, 0L, 0L, 0, 0, 0, 0.0F, 0, 0.0F, 0, 16777215, null)
        
        val timer = CustomRepeatingTimer(2000L) { this.record() }
        timer.setRepeats(true)
        this.timer = timer
        this.record()
    }

    override fun timer(phase: String): Timer {
        return allPhaseTimer.getOrPut(phase) { Timer(phase) }
    }

    fun record() {
        val m = UsefulMetrics.Companion.getMetrics()
        synchronized(this) {
            val gauge = m.getJvmMemoryUsed()
            if (gauge != null) {
                val value = gauge.value as? Long
                if (value != null) {
                    maxUsedMemory.updateAndGet { current -> if (current < value) value else current }
                }
            }

            val timeInSeconds = PhaseIntervalTimerKt.nanoTimeInSeconds(
                MetricsMonitorKt.timeSub(PhaseIntervalTimerKt.currentNanoTime(), beginNanoTime)
            )
            val memoryUsed = MetricsMonitorKt.inMemGB(m.getJvmMemoryUsed()?.let { getG(it) })
            val memoryMax = MetricsMonitorKt.inMemGB(getG(maxUsedMemory.get()))
            val memoryCommitted = MetricsMonitorKt.inMemGB(m.getJvmMemoryCommitted()?.let { getG(it) })
            val freePhysical = MetricsMonitorKt.inMemGB(m.getFreePhysicalSize()?.let { getG(it) })
            val cpuLoad = m.getCpuSystemCpuLoad()?.value as? Double?.let { PhaseIntervalTimerKt.retainDecimalPlaces(it, 2) }

            snapshot.add(
                MetricsSnapshot(
                    timeInSeconds,
                    memoryUsed,
                    memoryMax,
                    memoryCommitted,
                    freePhysical,
                    cpuLoad
                )
            )
        }
    }

    fun start() {
        timer.start()
    }

    fun stop() {
        timer.stop()
    }

    @JvmName("putNumber")
    fun <T : Number> put(name: String, value: T) {
        synchronized(this) {
            final[name] = value
        }
    }

    fun put(name: String, value: String) {
        synchronized(this) {
            final[name] = value
        }
    }

    fun take(result: ResultCollector) {
        synchronized(this) {
            projectMetrics.serializedReports = result.reports.size
            val reportsByKey = result.reports.groupBy { report ->
                ReportKey(report.category, report.type)
            }.mapValues { (_, reports) -> reports.size }

            reports.addAll(reportsByKey.keys)
            
            val counter = result.collectors.filterIsInstance<ResultCounter>().firstOrNull()
            if (counter != null) {
                put("infoflow.results", counter.infoflowResCount.get())
                put("infoflow.abstraction", counter.infoflowAbsAtSinkCount.get())
                put("symbolic.execution", counter.symbolicUTbotCount.get())
                put("PreAnalysis.results", counter.preAnalysisResultCount.get())
                put("built-in.Analysis.results", counter.builtInAnalysisCount.get())
                put("AbstractInterpretationAnalysis.results", counter.dataFlowCount.get())
            }
        }
    }

    fun serialize(out: IResDirectory) {
        stop()
        val file = out.resolve("metrics.yml").path
        synchronized(this) {
            projectMetrics.process()
            beginDate = MetricsMonitorKt.getDateStringFromMillis(beginMillis)

            allPhaseTimer.forEach { (phaseName, timer) ->
                phaseTimer.add(
                    PhaseTimer(
                        phaseName,
                        PhaseIntervalTimerKt.nanoTimeInSeconds(MetricsMonitorKt.timeSub(timer.startTime, beginNanoTime)),
                        PhaseIntervalTimerKt.nanoTimeInSeconds(timer.elapsedTime),
                        timer.phaseStartCount.value,
                        PhaseIntervalTimerKt.nanoTimeInSeconds(timer.phaseAverageElapsedTime, 6),
                        PhaseIntervalTimerKt.nanoTimeInSeconds(MetricsMonitorKt.timeSub(timer.endTime, beginNanoTime))
                    )
                )
            }

            phaseTimer.sortWith(compareBy { it.name })
            endTime = PhaseIntervalTimerKt.currentNanoTime()
            val totalTime = MetricsMonitorKt.timeSub(endTime, beginNanoTime)
            endDate = MetricsMonitorKt.getDateStringFromMillis(System.currentTimeMillis())
            elapsedSeconds = PhaseIntervalTimerKt.nanoTimeInSeconds(totalTime)
            elapsedTime = totalTime?.let { 
                Duration.milliseconds(it).toString() 
            } ?: "invalid"
            
            jvmMemoryUsedMax = MetricsMonitorKt.inMemGB(getG(maxUsedMemory.get()))
            
            Files.newOutputStream(file).use { output ->
                Yaml.default.encodeToStream(serializer(), this, output)
            }
        }
    }

    fun addAnalyzeFinishHook(t: Thread) {
        analyzeFinishHook.add(t)
    }

    fun runAnalyzeFinishHook() {
        synchronized(analyzeFinishHook) {
            analyzeFinishHook.forEach { thread ->
                thread.start()
                thread.join()
            }
            analyzeFinishHook.clear()
        }
    }

    companion object {
        val yamlFormat: Yaml = Yaml(SerializersModuleBuilder().apply {
            contextual(Any::class, DynamicLookupSerializer())
        }.build(), MainConfigKt.yamlConfiguration)

        fun serializer(): KSerializer<MetricsMonitor> = MetricsMonitor.serializer()
    }

    @Serializable
    data class MetricsSnapshot(
        val timeInSecond: Double,
        val jvmMemoryUsed: Double?,
        val jvmMemoryUsedMax: Double?,
        val jvmMemoryCommitted: Double?,
        val freePhysicalSize: Double?,
        val cpuSystemCpuLoad: Double?
    )

    @Serializable
    private data class PhaseTimer(
        val name: String,
        val start: Double,
        val elapsedTime: Double,
        val phaseStartCount: Int,
        val averageElapsedTime: Double,
        val end: Double
    )
}

private class MetricsMonitor$serialize$lambda$15$$inlined$compareBy$1<T> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return compareValues((a as MetricsMonitor.PhaseTimer).name, (b as MetricsMonitor.PhaseTimer).name)
    }
}