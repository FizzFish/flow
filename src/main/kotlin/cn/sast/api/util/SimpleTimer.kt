package cn.sast.api.util

import java.util.Arrays
import java.util.function.Supplier
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger

public class SimpleTimer {
    public var elapsedTime: Long = 0L
        private set

    public var start: Long = 0L
        private set

    private var startTime: Long = 0L

    public var end: Long = 0L
        private set

    public fun start() {
        this.startTime = PhaseIntervalTimerKt.currentNanoTime()
        if (this.start == 0L) {
            this.start = this.startTime
        }
    }

    public fun stop() {
        val cur: Long = PhaseIntervalTimerKt.currentNanoTime()
        this.elapsedTime = this.elapsedTime + (cur - this.startTime)
        this.startTime = 0L
        this.end = cur
    }

    public fun currentElapsedTime(): Long {
        return this.elapsedTime + (PhaseIntervalTimerKt.currentNanoTime() - this.startTime)
    }

    public fun inSecond(): Float {
        return this.elapsedTime.toFloat() / 1000.0F
    }

    public fun clear() {
        this.elapsedTime = 0L
    }

    public override fun toString(): String {
        return String.format("elapsed time: %.2fs", inSecond())
    }

    public companion object {
        private val logger: Logger? = null

        public fun <T> runAndCount(task: Supplier<T>, taskName: String, level: Level?): T {
            logger?.info("{} starts ...", taskName)
            val timer = SimpleTimer()
            timer.start()
            val result = task.get()
            timer.stop()
            logger?.log(level, "{} finishes, elapsed time: {}", taskName, String.format("%.2fs", timer.inSecond()))
            return result
        }

        @JvmOverloads
        public fun runAndCount(task: Runnable, taskName: String, level: Level? = Level.INFO) {
            this.runAndCount(Supplier {
                task.run()
                null
            }, taskName, level)
        }

        @JvmOverloads
        fun runAndCount(task: Runnable, taskName: String) {
            runAndCount(task, taskName, Level.INFO)
        }
    }
}