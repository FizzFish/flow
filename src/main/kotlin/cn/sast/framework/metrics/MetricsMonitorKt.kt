@file:SourceDebugExtension(["SMAP\nMetricsMonitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitorKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,249:1\n1#2:250\n*E\n"])

package cn.sast.framework.metrics

import cn.sast.api.util.PhaseIntervalTimerKt
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.Duration

private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

internal fun timeSub(time: Long?, begin: Long): Long? {
    if (time != null) {
        val it = time
        val var10000 = if (it >= begin) time else null
        if (var10000 != null) {
            return var10000 - begin
        }
    }
    return null
}

internal fun Number.fmt(postfix: String, scale: Int = 2): String {
    val formatString = "%.${scale}f$postfix"
    val args = arrayOf<Any>(this.toDouble())
    return String.format(formatString, Arrays.copyOf(args, args.size))
}

@JvmSynthetic
internal fun fmt$default(`$this$fmt`: Number, postfix: String, scale: Int, mask: Int, any: Any?): String {
    var scaleVar = scale
    if ((mask and 2) != 0) {
        scaleVar = 2
    }
    return `$this$fmt`.fmt(postfix, scaleVar)
}

internal fun Number?.inMemGB(scale: Int = 3): Double {
    return this?.let {
        PhaseIntervalTimerKt.retainDecimalPlaces(it.toDouble(), scale)
    } ?: -1.0
}

@JvmSynthetic
internal fun inMemGB$default(`$this$inMemGB`: Number?, scale: Int, mask: Int, any: Any?): Double {
    var scaleVar = scale
    if ((mask and 1) != 0) {
        scaleVar = 3
    }
    return `$this$inMemGB`.inMemGB(scaleVar)
}

public fun getDateStringFromMillis(duration: Duration): String {
    return getDateStringFromMillis(duration.inWholeMilliseconds)
}

public fun getDateStringFromMillis(beginMillis: Long): String {
    return dateFormat.format(Date(beginMillis))
}