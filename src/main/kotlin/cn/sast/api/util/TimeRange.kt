package cn.sast.api.util

import java.util.ArrayList
import java.util.LinkedList
import java.util.Objects
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nTimeRange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TimeRange.kt\ncn/sast/api/util/TimeRange\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,331:1\n1#2:332\n*E\n"])
class TimeRange(min: Long, max: Long) : Comparable<TimeRange> {
    var min: Long
        private set

    var max: Long
        private set

    var leftClose: Boolean = true
        internal set

    var rightClose: Boolean = true
        internal set

    init {
        set(min, max)
    }

    override fun compareTo(r: TimeRange): Int {
        return when {
            this.min > r.min -> 1
            this.min < r.min -> -1
            this.max > r.max -> 1
            this.max < r.max -> -1
            else -> 0
        }
    }

    fun setMin(min: Long) {
        if (min < 0L || min > this.max) {
            throw IllegalArgumentException("Invalid input!")
        } else {
            this.min = min
        }
    }

    fun setMax(max: Long) {
        if (max < 0L || max < this.min) {
            throw IllegalArgumentException("Invalid input!")
        } else {
            this.max = max
        }
    }

    fun contains(r: TimeRange): Boolean {
        return this.min <= r.min && this.max >= r.max
    }

    fun contains(min: Long, max: Long): Boolean {
        return when {
            leftClose && rightClose -> this.min <= min && this.max >= max
            leftClose -> this.min <= min && this.max > max
            rightClose -> this.min < min && this.max >= max
            else -> this.min < min && this.max > max
        }
    }

    fun contains(time: Long): Boolean {
        return when {
            leftClose && rightClose -> time >= this.min && time <= this.max
            leftClose -> time >= this.min && time < this.max
            rightClose -> time > this.min && time <= this.max
            else -> time > this.min && time < this.max
        }
    }

    fun set(min: Long, max: Long) {
        if (min > max) {
            throw IllegalArgumentException("min:$min should not be larger than max: $max")
        } else {
            this.min = min
            this.max = max
        }
    }

    fun intersects(r: TimeRange): Boolean {
        return (leftClose && r.rightClose || r.max >= min) &&
                (leftClose || r.rightClose || r.max > min) &&
                (!leftClose || !r.rightClose || r.max > min - 2) &&
                (rightClose && r.leftClose || r.min <= max) &&
                (rightClose || r.leftClose || r.min < max) &&
                (!rightClose || !r.leftClose || r.min < max + 2)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        return o is TimeRange && min == o.min && max == o.max
    }

    override fun hashCode(): Int {
        return Objects.hash(min, max)
    }

    fun overlaps(rhs: TimeRange): Boolean {
        return (leftClose && rhs.rightClose || rhs.max > min) &&
                (leftClose || rhs.rightClose || rhs.max > min + 1L) &&
                (!leftClose || !rhs.rightClose || rhs.max >= min) &&
                (rightClose && rhs.leftClose || rhs.min < max) &&
                (rightClose || rhs.leftClose || rhs.min + 1L < max) &&
                (!rightClose || !rhs.leftClose || rhs.min <= max)
    }

    override fun toString(): String {
        val res = StringBuilder()
        if (leftClose) {
            res.append("[ ")
        } else {
            res.append("( ")
        }

        res.append(min).append(" : ").append(max)
        if (rightClose) {
            res.append(" ]")
        } else {
            res.append(" )")
        }

        return res.toString()
    }

    fun merge(rhs: TimeRange) {
        set(kotlin.ranges.coerceAtMost(min, rhs.min), kotlin.ranges.coerceAtLeast(max, rhs.max))
    }

    fun getRemains(timeRangesPrev: List<TimeRange>): List<TimeRange> {
        val remains = ArrayList<TimeRange>()

        for (prev in timeRangesPrev) {
            if (prev.min >= max + 2) {
                break
            }

            if (intersects(prev)) {
                if (prev.contains(this)) {
                    return remains
                }

                if (contains(prev)) {
                    if (prev.min > min && prev.max == max) {
                        setMax(prev.min)
                        rightClose = false
                        remains.add(this)
                        return remains
                    }

                    if (prev.min == min) {
                        min = prev.max
                        leftClose = false
                    } else {
                        val r = TimeRange(min, prev.min)
                        r.leftClose = leftClose
                        r.rightClose = false
                        remains.add(r)
                        min = prev.max
                        leftClose = false
                    }
                } else {
                    if (prev.min >= min) {
                        setMax(prev.min)
                        rightClose = false
                        remains.add(this)
                        return remains
                    }

                    min = prev.max
                    leftClose = false
                }
            }
        }

        remains.add(this)
        return remains
    }

    companion object {
        fun sortAndMerge(unionCandidates: MutableList<TimeRange>): MutableList<TimeRange> {
            unionCandidates.sort()
            val unionResult = LinkedList<TimeRange>()
            val iterator = unionCandidates.iterator()
            if (!iterator.hasNext()) {
                return unionResult
            }

            var current = iterator.next()

            while (iterator.hasNext()) {
                val rangeNext = iterator.next()
                if (current.intersects(rangeNext)) {
                    current.merge(rangeNext)
                } else {
                    unionResult.add(current)
                    current = rangeNext
                }
            }

            unionResult.add(current)
            return unionResult
        }
    }
}