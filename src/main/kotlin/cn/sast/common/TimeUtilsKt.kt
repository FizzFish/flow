package cn.sast.common

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

/**
 * 当前时间与此 [LocalDateTime] 的时间差，用漂亮格式打印。
 */
fun LocalDateTime.prettyPrintTime(unit: ChronoUnit = ChronoUnit.SECONDS): String {
   val seconds = unit.between(this, LocalDateTime.now())
   val duration = seconds.seconds
   return "$duration (${duration.inWholeSeconds} ${unit.name.lowercase(Locale.ROOT)})"
}
