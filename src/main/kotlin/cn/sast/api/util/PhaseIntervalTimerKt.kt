@file:SourceDebugExtension(["SMAP\nPhaseIntervalTimer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PhaseIntervalTimer.kt\ncn/sast/api/util/PhaseIntervalTimerKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,124:1\n1#2:125\n*E\n"])

package cn.sast.api.util

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.jvm.internal.SourceDebugExtension

public const val CONVERT_TO_SECONDS: Double = 1.0E9

public fun retainDecimalPlaces(value: Double, scale: Int, roundingMode: RoundingMode = RoundingMode.HALF_EVEN): Double {
    return if (value.isInfinite() || value.isNaN()) value else BigDecimal(value).setScale(scale, roundingMode).toDouble()
}

@JvmSynthetic
internal fun retainDecimalPlaces$default(value: Double, scale: Int, roundingMode: RoundingMode?, flags: Int): Double {
    val actualRoundingMode = if ((flags and 4) != 0) RoundingMode.HALF_EVEN else roundingMode!!
    return retainDecimalPlaces(value, scale, actualRoundingMode)
}

public fun Number?.nanoTimeInSeconds(scale: Int = 3): Double {
    if (this != null) {
        val doubleValue = this.toDouble()
        if (!doubleValue.isInfinite() && !doubleValue.isNaN()) {
            return retainDecimalPlaces$default(doubleValue / CONVERT_TO_SECONDS, scale, null, 4)
        }
    }
    return -1.0
}

@JvmSynthetic
internal fun nanoTimeInSeconds$default(number: Number?, scale: Int, flags: Int): Double {
    val actualScale = if ((flags and 1) != 0) 3 else scale
    return nanoTimeInSeconds(number, actualScale)
}

public fun currentNanoTime(): Long {
    return System.nanoTime()
}