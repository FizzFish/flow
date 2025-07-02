package cn.sast.common

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationKt
import kotlin.time.DurationUnit

public fun LocalDateTime.prettyPrintTime(c: ChronoUnit = ChronoUnit.SECONDS): String {
   val duration: Long = DurationKt.toDuration(c.between(`$this$prettyPrintTime`, LocalDateTime.now()), DurationUnit.SECONDS);
   val var10000: java.lang.String = Duration.toString-impl(duration);
   val var10001: Long = Duration.getInWholeSeconds-impl(duration);
   val var10002: java.lang.String = c.name().toLowerCase(Locale.ROOT);
   return "$var10000 ($var10001$var10002)";
}

@JvmSynthetic
fun `prettyPrintTime$default`(var0: LocalDateTime, var1: ChronoUnit, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = ChronoUnit.SECONDS;
   }

   return prettyPrintTime(var0, var1);
}
