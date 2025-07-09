package cn.sast.framework.metrics

import cn.sast.api.report.ProjectMetrics
import cn.sast.api.util.IMonitor
import cn.sast.api.util.Timer
import cn.sast.api.util.currentNanoTime
import cn.sast.common.CustomRepeatingTimer
import cn.sast.common.IResDirectory
import cn.sast.framework.result.ResultCollector
import cn.sast.framework.result.ResultCounter
import cn.sast.idfa.analysis.UsefulMetrics
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModuleBuilder
import java.nio.file.Files
import java.nio.file.OpenOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.LongUnaryOperator
import kotlin.math.max
import kotlin.time.Duration

/**
 * `MetricsMonitor` records JVM + project specific metrics from *start* to *end* of the analysis.
 * It exposes a small DSL so callers can add *phase* timers and arbitrary name/number pairs that
 * will end up in the final YAML file.
 */
@Serializable
class MetricsMonitor : IMonitor {

    // -------------------------------------------------------------------------
    //  Publicly exposed immutable timestamps
    // -------------------------------------------------------------------------
    val beginMillis: Long = System.currentTimeMillis()
    @Transient val beginNanoTime: Long = currentNanoTime()

    // -------------------------------------------------------------------------
    //  JVM runtime metrics
    // -------------------------------------------------------------------------
    private val jvmMemoryMaxGb: Double = UsefulMetrics.metrics.jvmMemoryMax?.let { it.value.inMemGB() } ?: -1.0
    @Transient private val maxUsedMemory = AtomicLong(0)

    // -------------------------------------------------------------------------
    //  Timers & helpers
    // -------------------------------------------------------------------------
    @Transient private val allPhaseTimer: ConcurrentMap<String, Timer> = ConcurrentHashMap()
    private val phaseTimer = mutableListOf<PhaseTimer>()
    @Transient private val timer = CustomRepeatingTimer(delayMillis = 2_000) { record() }
    @Transient private val analyzeFinishHook = mutableListOf<Thread>()

    // -------------------------------------------------------------------------
    //  Collected project metrics & results
    // -------------------------------------------------------------------------
    override val projectMetrics = ProjectMetrics()
    private val finalNumbers = mutableMapOf<String, Any>()
    private val reports = mutableListOf<ReportKey>()
    private val snapshot = mutableListOf<MetricsSnapshot>()

    // -------------------------------------------------------------------------
    //  Lazily filled at [serialize]
    // -------------------------------------------------------------------------
    private var endNanoTime: Long? = null
    private var elapsedSeconds: Double = -1.0

    // -------------------------------------------------------------------------
    //  IMonitor implementation
    // -------------------------------------------------------------------------
    override fun timer(phase: String): Timer =
        allPhaseTimer.computeIfAbsent(phase) { Timer(phase) }

    // -------------------------------------------------------------------------
    //  Public API – lifecycle
    // -------------------------------------------------------------------------
    fun start() = timer.start()
    fun stop() = timer.stop()

    /**
     * Snapshot *current* JVM runtime metrics and append to [snapshot] list.
     * Keeps track of maximum *used* memory encountered so far.
     */
    fun record() {
        synchronized(this) {
            val metrics = UsefulMetrics.metrics
            metrics.jvmMemoryUsed?.value?.let { usedBytes ->
                maxUsedMemory.updateAndGet(LongUnaryOperator { current -> max(current, usedBytes) })
            }

            snapshot += MetricsSnapshot(
                timeInSecond = nanoTimeInSeconds(
                    currentNanoTime() - beginNanoTime
                ),
                jvmMemoryUsed = metrics.jvmMemoryUsed?.value.inMemGB(),
                jvmMemoryUsedMax = maxUsedMemory.get().inMemGB(),
                jvmMemoryCommitted = metrics.jvmMemoryCommitted?.value.inMemGB(),
                freePhysicalSize = metrics.freePhysicalSize?.value.inMemGB(),
                cpuSystemCpuLoad = metrics.cpuSystemCpuLoad?.value?.let { retainDecimalPlaces(it, 2) },
            )
        }
    }
    // -------------------------------------------------------------------------
    //  Public API – custom numbers / strings
    // -------------------------------------------------------------------------
    fun <T : Number> put(name: String, value: T) = synchronized(this) { finalNumbers[name] = value }
    fun put(name: String, value: String)        = synchronized(this) { finalNumbers[name] = value }

    // -------------------------------------------------------------------------
    //  Aggregates [result] into *projectMetrics* & report statistics
    // -------------------------------------------------------------------------
    fun take(result: ResultCollector) = synchronized(this) {
        projectMetrics.serializedReports = result.reports.size

        // 1️⃣ Build a (category,type) → size map
        val grouped: Map<ReportKey, Int> = result.reports.groupingBy {
            ReportKey(it.category, it.type)
        }.eachCount()

        // 2️⃣ Update ReportKey.size and transfer to [reports]
        grouped.forEach { (key, size) ->
            key.size = size
            reports += key
        }

        // 3️⃣ Copy interesting numbers from the *first* [ResultCounter] (if present)
        result.counters.filterIsInstance<ResultCounter>().firstOrNull()?.let { c ->
            put("infoflow.results",          c.infoflowResCount.get())
            put("infoflow.abstraction",      c.infoflowAbsAtSinkCount.get())
            put("symbolic.execution",        c.symbolicUTbotCount.get())
            put("PreAnalysis.results",       c.preAnalysisResultCount.get())
            put("built‑in.Analysis.results", c.builtInAnalysisCount.get())
            put("AbstractInterpretationAnalysis.results", c.dataFlowCount.get())
        }
    }
    // -------------------------------------------------------------------------
    //  Serialisation → YAML file
    // -------------------------------------------------------------------------
    fun serialize(outDir: IResDirectory) {
        stop()                                   // make sure the repeating timer is stopped
        endNanoTime = currentNanoTime()

        // 1️⃣ Convert phase timers to serialisable DTOs
        val elapsedFromBegin: (Long) -> Double = { ns ->
            nanoTimeInSeconds(ns - beginNanoTime)
        }
        phaseTimer += allPhaseTimer.entries
            .sortedBy { it.value.startTime }
            .map { (name, timer) ->
                PhaseTimer(
                    name = name,
                    start = elapsedFromBegin(timer.startTime),
                    elapsedTime = nanoTimeInSeconds(timer.elapsedTime),
                    phaseStartCount = timer.phaseStartCount.get(),
                    averageElapsedTime = nanoTimeInSeconds(timer.phaseAverageElapsedTime),
                    end = elapsedFromBegin(timer.endTime)
                )
            }

        // 2️⃣ High‑level metrics
        elapsedSeconds = nanoTimeInSeconds(endNanoTime!! - beginNanoTime)

        // 3️⃣ Write YAML
        val file = outDir.resolve("metrics.yml").path
        Files.newOutputStream(file, *arrayOf<OpenOption>()).use { os ->
            yamlFormat.encodeToStream(serializer(), this, os)
        }
    }

    // -------------------------------------------------------------------------
    //  Analyse‑finish hooks
    // -------------------------------------------------------------------------
    fun addAnalyzeFinishHook(t: Thread) = analyzeFinishHook.add(t)

    fun runAnalyzeFinishHook() {
        synchronized(analyzeFinishHook) {
            analyzeFinishHook.forEach { th ->
                th.start(); th.join()
            }
            analyzeFinishHook.clear()
        }
    }

    // -------------------------------------------------------------------------
    //  Data classes used in the YAML file
    // -------------------------------------------------------------------------
    @Serializable
    data class MetricsSnapshot(
        val timeInSecond: Double,
        val jvmMemoryUsed: Double?,
        val jvmMemoryUsedMax: Double?,
        val jvmMemoryCommitted: Double?,
        val freePhysicalSize: Double?,
        val cpuSystemCpuLoad: Double?,
    )

    @Serializable
    private data class PhaseTimer(
        val name: String,
        val start: Double,
        val elapsedTime: Double,
        val phaseStartCount: Int,
        val averageElapsedTime: Double,
        val end: Double,
    )

    // -------------------------------------------------------------------------
    //  Companion – YAML format + KSerializer
    // -------------------------------------------------------------------------
    companion object {
        /** YAML format configured with contextual lookup for *Any* values. */
        val yamlFormat: Yaml by lazy {
            val module = SerializersModuleBuilder().apply {
                contextual(Any::class, DynamicLookupSerializer)
            }.build()
            Yaml(module, MainConfigKt.yamlConfiguration)
        }
    }
}

private val DATE_FMT = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

/**
 * Convert *absolute* `time` (a nano‑timestamp) into a *relative* duration since [begin].
 * Returns `null` if [time] < [begin].
 */
internal fun timeSub(time: Long?, begin: Long): Long? =
    time?.takeIf { it >= begin }?.let { it - begin }

/** Human‑readable *GB* string – e.g. `"1.23GB"`. */
internal fun Number.fmt(postfix: String, scale: Int = 2): String =
    "% .${scale}f$postfix".format(this.toDouble())

/** Bytes → Gigabytes (*rounded* to [scale] decimals). Returns `-1.0` on `null`. */
internal fun Number?.inMemGB(scale: Int = 3): Double =
    this?.let { retainDecimalPlaces(it.toDouble(), scale) } ?: -1.0

/** Convenience overload for [Duration]. */
fun getDateStringFromMillis(duration: Duration): String =
    getDateStringFromMillis(duration.inWholeMilliseconds)

fun getDateStringFromMillis(epochMillis: Long): String =
    DATE_FMT.format(Date(epochMillis))