package cn.sast.api.util

import java.util.Arrays
import java.util.LinkedList
import java.util.TreeMap
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.atomicfu.AtomicFU
import kotlinx.atomicfu.AtomicInt
import mu.KLogger

@SourceDebugExtension(["SMAP\nPhaseIntervalTimer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PhaseIntervalTimer.kt\ncn/sast/api/util/PhaseIntervalTimer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,124:1\n1#2:125\n*E\n"])
open class PhaseIntervalTimer {
    private val id: AtomicInt = AtomicFU.atomic(0)

    var startTime: Long? = null
        private set

    var endTime: Long? = null
        private set

    private var _elapsedTime: Long? = null

    open val prefix: String
        get() = ""

    var elapsedTime: Long?
        get() {
            if (this.phaseTimerActiveCount.value != 0) {
                logger.error(PhaseIntervalTimer::_get_elapsedTime_$lambda$0)
            }

            if (this.ranges.size != 0) {
                logger.error(PhaseIntervalTimer::_get_elapsedTime_$lambda$1)
            }

            if (this.queue.size != 0) {
                logger.error(PhaseIntervalTimer::_get_elapsedTime_$lambda$2)
            }

            return this._elapsedTime
        }
        private set

    private var phaseTimerActiveCount: AtomicInt = AtomicFU.atomic(0)

    var phaseStartCount: AtomicInt = AtomicFU.atomic(0)
        private set

    val phaseAverageElapsedTime: Double?
        get() {
            val var1 = this.phaseStartCount.value
            var c = var1
            val var10000 = if (c > 0) var1 else null
            val var14: Any?
            if ((if (c > 0) var1 else null) != null) {
                c = var10000
                val var12 = this.elapsedTime
                if (var12 != null) {
                    val var4 = var12.toDouble()
                    val itx = var4
                    val var13 = if (!itx.isInfinite() && !itx.isNaN()) var4 else null
                    if (var13 != null) {
                        return var13 / c.toDouble()
                    }
                }
                var14 = null
            } else {
                var14 = null
            }
            return var14 as? Double
        }

    private var ranges: MutableList<TimeRange> = LinkedList()
    private val queue: TreeMap<Int, Snapshot> = TreeMap()

    fun start(): Snapshot {
        val result = Snapshot(PhaseIntervalTimerKt.currentNanoTime(), this.id.getAndIncrement())
        if (this.startTime == null) {
            this.startTime = result.startTime
        }

        synchronized(this.queue) {
            this.queue[result.id] = result
            this.phaseTimerActiveCount.getAndIncrement()
        }

        this.phaseStartCount.getAndIncrement()
        return result
    }

    fun stop(snapshot: Snapshot) {
        val timeRange = TimeRange(snapshot.startTime, PhaseIntervalTimerKt.currentNanoTime())
        this.endTime = maxOf(this.endTime ?: timeRange.max, timeRange.max)
        synchronized(this.queue) {
            val cur = this.phaseTimerActiveCount.decrementAndGet()
            this.ranges.add(timeRange)
            this.ranges = TimeRange.Companion.sortAndMerge(this.ranges)
            if (cur >= 0) {
                if (this.queue.lowerKey(snapshot.id) == null) {
                    var i = 0
                    var elapsedTime = this._elapsedTime ?: 0L
                    val higher = this.queue.higherEntry(snapshot.id)

                    val noSnapshotBefore = if (higher == null) {
                        this.ranges.last().max
                    } else {
                        if (timeRange.max <= higher.value.startTime) timeRange.max else timeRange.min
                    }

                    while (i < this.ranges.size && this.ranges[i].max <= noSnapshotBefore) {
                        val e = this.ranges.removeAt(i--)
                        elapsedTime += e.max - e.min
                        i++
                    }

                    this._elapsedTime = elapsedTime
                }

                this.queue.remove(snapshot.id)
            } else {
                logger.error(PhaseIntervalTimer::stop$lambda$8$lambda$7)
            }
        }
    }

    override fun toString(): String {
        val var2 = arrayOf<Any>(PhaseIntervalTimerKt.nanoTimeInSeconds$default(this._elapsedTime, 0, 1, null))
        return String.format("elapsed time: %.2fs", Arrays.copyOf(var2, var2.size))
    }

    companion object {
        private lateinit var logger: KLogger

        @JvmStatic
        fun _get_elapsedTime_$lambda$0(this$0: PhaseIntervalTimer): Any {
            return "${this$0.prefix}internal error: phaseTimerCount is not zero"
        }

        @JvmStatic
        fun _get_elapsedTime_$lambda$1(this$0: PhaseIntervalTimer): Any {
            return "${this$0.prefix}internal error: ranges is not empty"
        }

        @JvmStatic
        fun _get_elapsedTime_$lambda$2(this$0: PhaseIntervalTimer): Any {
            return "${this$0.prefix}internal error: queue is not empty"
        }

        @JvmStatic
        fun stop$lambda$8$lambda$7(): Any {
            return "internal error: phaseTimerCount is negative"
        }

        @JvmStatic
        fun logger$lambda$9() {
        }
    }

    data class Snapshot internal constructor(val startTime: Long, internal val id: Int) {
        fun copy(startTime: Long = this.startTime, id: Int = this.id): Snapshot {
            return Snapshot(startTime, id)
        }

        override fun toString(): String {
            return "Snapshot(startTime=$startTime, id=$id)"
        }

        override fun hashCode(): Int {
            return startTime.hashCode() * 31 + id.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Snapshot) return false
            if (startTime != other.startTime) return false
            return id == other.id
        }
    }
}