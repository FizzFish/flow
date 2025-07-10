@file:Suppress(
    "MemberVisibilityCanBePrivate",
    "FunctionName",
    "NOTHING_TO_INLINE"
)

package cn.sast.api.util

/* ────────────────────────────────────────────────────────────────────────────
 *  公共工具 —— 时间单位 & 四舍五入
 * ─────────────────────────────────────────────────────────────────────────── */
import cn.sast.api.report.ProjectMetrics
import java.math.BigDecimal
import java.math.RoundingMode
import mu.KLogger
import mu.KotlinLogging
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.AtomicInt
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap
import java.util.function.Supplier
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import soot.ModuleUtil
import soot.SootClass
import soot.tagkit.Host
import kotlin.io.path.name

const val CONVERT_TO_SECONDS: Double = 1e9

interface IMonitor {
    val projectMetrics: ProjectMetrics
    fun timer(phase: String): Timer
}
/** 保留 [scale] 位小数；`NaN/Inf` 直接返回原值 */
fun retainDecimalPlaces(
    value: Double,
    scale: Int,
    roundingMode: RoundingMode = RoundingMode.HALF_EVEN
): Double = if (value.isFinite())
    BigDecimal(value).setScale(scale, roundingMode).toDouble()
else value

/** 把纳秒计时转换为秒，默认保留 3 位小数 */
fun Number?.nanoTimeInSeconds(scale: Int = 3): Double =
    this?.toDouble()?.let { d ->
        if (d.isFinite()) retainDecimalPlaces(d / CONVERT_TO_SECONDS, scale) else -1.0
    } ?: -1.0

/** 当前纳秒时间（System.nanoTime 包装） */
fun currentNanoTime(): Long = System.nanoTime()


/* ────────────────────────────────────────────────────────────────────────────
 *  Ⅰ. TimeRange —— 闭区间 / 并集 Merge
 * ─────────────────────────────────────────────────────────────────────────── */
data class TimeRange(
    var min: Long,
    var max: Long,
    var leftClose: Boolean = true,
    var rightClose: Boolean = true
) : Comparable<TimeRange> {

    init {
        require(min <= max) { "min:$min should not be larger than max: $max" }
    }

    /* ----------- 基本操作 ----------- */

    fun set(min: Long, max: Long) {
        require(min <= max) { "min:$min should not be larger than max: $max" }
        this.min = min; this.max = max
    }

    /** 是否完全包含另一个区间 */
    fun contains(r: TimeRange): Boolean = min <= r.min && max >= r.max

    /** [min,max] 是否落在本区间内 */
    fun contains(min: Long, max: Long): Boolean =
        when {
            leftClose && rightClose -> this.min <= min && this.max >= max
            leftClose               -> this.min <= min && this.max > max
            rightClose              -> this.min <  min && this.max >= max
            else                    -> this.min <  min && this.max >  max
        }

    fun contains(time: Long): Boolean =
        when {
            leftClose && rightClose -> time in this.min..this.max
            leftClose               -> time in this.min until this.max
            rightClose              -> time in (this.min + 1)..this.max
            else                    -> time in (this.min + 1) until this.max
        }

    /* ----------- 交并判定 ----------- */

    fun intersects(r: TimeRange): Boolean =
        (leftClose && r.rightClose || r.max >= min) &&
                (leftClose || r.rightClose || r.max > min) &&
                (!leftClose || !r.rightClose || r.max > min - 2) &&
                (rightClose && r.leftClose || r.min <= max) &&
                (rightClose || r.leftClose || r.min < max) &&
                (!rightClose || !r.leftClose || r.min < max + 2)

    fun overlaps(rhs: TimeRange): Boolean =
        (leftClose && rhs.rightClose || rhs.max > min) &&
                (leftClose || rhs.rightClose || rhs.max > min + 1) &&
                (!leftClose || !rhs.rightClose || rhs.max >= min) &&
                (rightClose && rhs.leftClose || rhs.min < max) &&
                (rightClose || rhs.leftClose || rhs.min + 1 < max) &&
                (!rightClose || !rhs.leftClose || rhs.min <= max)

    /** 并入另一区间（要求已确定相交） */
    fun merge(rhs: TimeRange) {
        set(minOf(min, rhs.min), maxOf(max, rhs.max))
    }

    override fun compareTo(other: TimeRange): Int =
        compareValuesBy(this, other, { it.min }, { it.max })

    /* ----------- 静态工具 ----------- */

    companion object {
        /**
         * 把一组可能重叠的区间 **排序并合并**。
         *
         * @return 合并后的新列表（保证无交叉、按 min 升序）
         */
        fun sortAndMerge(ranges: MutableList<TimeRange>): MutableList<TimeRange> {
            ranges.sort()
            val res = LinkedList<TimeRange>()
            var cur = ranges.firstOrNull() ?: return res
            for (next in ranges.drop(1)) {
                if (cur.intersects(next)) cur.merge(next)
                else { res += cur; cur = next }
            }
            res += cur
            return res
        }
    }
}

/* ────────────────────────────────────────────────────────────────────────────
 *  Ⅱ. PhaseIntervalTimer —— 支持多段开始/结束、区间去重
 * ─────────────────────────────────────────────────────────────────────────── */
open class PhaseIntervalTimer {

    /* ----------- 内部状态 ----------- */

    private val idGen          = atomic(0)
    private val activeCount    = atomic(0)
    val  phaseStartCount       = atomic(0)

    private var startTime: Long? = null
    private var endTime:   Long? = null
    private var _elapsed:  Long? = null

    private val ranges = LinkedList<TimeRange>()
    private val queue  = ConcurrentSkipListMap<Int, Snapshot>()

    /** 子类可覆盖，用于日志前缀 */
    protected open val prefix: String get() = ""

    /** logger（懒加载一次即可） */
    protected val logger: KLogger by lazy { KotlinLogging.logger {} }

    /* ----------- 核心 API ----------- */

    data class Snapshot internal constructor(val startTime: Long, internal val id: Int)

    /** 开始一次子阶段计时 */
    fun start(): Snapshot = Snapshot(currentNanoTime(), idGen.getAndIncrement()).also { snap ->
        if (startTime == null) startTime = snap.startTime
        queue[snap.id] = snap
        activeCount.incrementAndGet()
        phaseStartCount.incrementAndGet()
    }

    /** 结束一次子阶段计时 */
    fun stop(snapshot: Snapshot) {
        val range = TimeRange(snapshot.startTime, currentNanoTime())
        endTime = maxOf(endTime ?: range.max, range.max)

        val cur = activeCount.decrementAndGet()
        ranges += range
        ranges.apply { TimeRange.sortAndMerge(this) }

        if (cur < 0) {
            logger.error { "${prefix}internal error: phaseTimerCount is negative" }
            return
        }

        // 若当前 snapshot 前面已无未结束阶段，则可把闭合区间计入总耗时
        if (queue.lowerKey(snapshot.id) == null) {
            var elapsed = _elapsed ?: 0L
            val higher  = queue.higherEntry(snapshot.id)

            val noSnapshotBefore = higher?.value?.startTime ?: ranges.last.max
            var i = 0
            while (i < ranges.size && ranges[i].max <= noSnapshotBefore) {
                val e = ranges.removeAt(i)
                elapsed += e.max - e.min
            }
            _elapsed = elapsed
        }

        queue.remove(snapshot.id)
    }

    /** 获取总耗时（纳秒）；若内部状态异常会打日志提示 */
    val elapsedTime: Long?
        get() {
            if (activeCount.value != 0)
                logger.error { "${prefix}internal error: phaseTimerCount is not zero" }
            if (ranges.isNotEmpty())
                logger.error { "${prefix}internal error: ranges is not empty" }
            if (queue.isNotEmpty())
                logger.error { "${prefix}internal error: queue is not empty" }
            return _elapsed
        }

    override fun toString(): String =
        "elapsed time: %.2fs".format(elapsedTime.nanoTimeInSeconds(2))
}

/* ────────────────────────────────────────────────────────────────────────────
 *  Ⅲ. Timer —— 带名字的 PhaseIntervalTimer
 * ─────────────────────────────────────────────────────────────────────────── */
class Timer(private val name: String) : PhaseIntervalTimer() {
    override val prefix: String get() = "timer: $name "
    override fun toString(): String =
        "[%s] elapsed time: %.2fs".format(name, elapsedTime.nanoTimeInSeconds(2))
}

/* ────────────────────────────────────────────────────────────────────────────
 *  Ⅳ. SimpleTimer —— 单段计时器（高精度纳秒）
 * ─────────────────────────────────────────────────────────────────────────── */
class SimpleTimer {

    var elapsedTime = 0L
        private set
    var start = 0L
        private set
    var end   = 0L
        private set

    private var _startTick = 0L

    fun start() {
        _startTick = currentNanoTime()
        if (start == 0L) start = _startTick
    }

    fun stop() {
        val now = currentNanoTime()
        elapsedTime += now - _startTick
        _startTick = 0L
        end = now
    }

    fun currentElapsedTime(): Long =
        elapsedTime + (currentNanoTime() - _startTick)

    fun inSecond(): Float = elapsedTime / 1_000_000_000f

    fun clear() { elapsedTime = 0L }

    override fun toString(): String =
        "elapsed time: %.2fs".format(inSecond())

    /* ---------- 静态计时包装 ---------- */

    companion object {
        private val logger: Logger? = null // 若您想启用 log4j，可在此注入

        @JvmStatic
        fun <T> runAndCount(task: Supplier<T>, taskName: String, level: Level? = Level.INFO): T {
            logger?.info("{} starts ...", taskName)
            val timer = SimpleTimer().apply { start() }
            val result = task.get()
            timer.stop()
            logger?.log(level, "{} finishes, elapsed time: {}", taskName,
                "%.2fs".format(timer.inSecond()))
            return result
        }

        @JvmStatic
        @JvmOverloads
        fun runAndCount(task: Runnable, taskName: String, level: Level? = Level.INFO) {
            runAndCount(Supplier {
                task.run(); null
            }, taskName, level)
        }
    }
}

/* ────────────────────────────────────────────────────────────────────────────
 *  Ⅴ. 扩展函数 —— PhaseIntervalTimer?.bracket { ... }
 * ─────────────────────────────────────────────────────────────────────────── */
inline fun <T> PhaseIntervalTimer?.bracket(block: () -> T): T =
    if (this == null) block()
    else {
        val s = start()
        try {
            block()
        } catch (t: Throwable) {
            stop(s); throw t
        } finally {
            stop(s)
        }
    }

/* ────────────────────────────────────────────────────────────────────────────
 *  ✂ 如果您想拆文件，可以从此处剪断，每个 `// ──` 段落一个 .kt 文件
 * ─────────────────────────────────────────────────────────────────────────── */
