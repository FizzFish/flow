package cn.sast.api.util

import java.util.Arrays

public class Timer(name: String) : PhaseIntervalTimer {
    public val name: String = name

    public open val prefix: String
        get() = "timer: ${this.name} "

    public override fun toString(): String {
        val var2 = arrayOf<Any>(this.name, PhaseIntervalTimerKt.nanoTimeInSeconds$default(this.getElapsedTime(), 0, 1, null))
        return String.format("[%s] elapsed time: %.2fs", Arrays.copyOf(var2, var2.length))
    }
}