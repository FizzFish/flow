@file:SourceDebugExtension(["SMAP\nMetricsMonitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitorKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,249:1\n1#2:250\n*E\n"])

package cn.sast.framework.metrics

import cn.sast.api.util.PhaseIntervalTimerKt
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.Duration

private final val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

internal fun timeSub(time: Long?, begin: Long): Long? {
   if (time != null) {
      val it: Long = time.longValue();
      val var10000: java.lang.Long = if (it >= begin) time else null;
      if ((if (it >= begin) time else null) != null) {
         return var10000.longValue() - begin;
      }
   }

   return null;
}

internal fun Number.fmt(postfix: String, scale: Int = 2): String {
   val var3: java.lang.String = "%.$scalef$postfix";
   val var4: Array<Any> = new Object[]{`$this$fmt`.doubleValue()};
   val var10000: java.lang.String = java.lang.String.format(var3, Arrays.copyOf(var4, var4.length));
   return var10000;
}

@JvmSynthetic
fun `fmt$default`(var0: java.lang.Number, var1: java.lang.String, var2: Int, var3: Int, var4: Any): java.lang.String {
   if ((var3 and 2) != 0) {
      var2 = 2;
   }

   return fmt(var0, var1, var2);
}

internal fun Number?.inMemGB(scale: Int = 3): Double {
   return if (`$this$inMemGB` != null) PhaseIntervalTimerKt.retainDecimalPlaces$default(`$this$inMemGB`.doubleValue(), scale, null, 4, null) else -1.0;
}

@JvmSynthetic
fun `inMemGB$default`(var0: java.lang.Number, var1: Int, var2: Int, var3: Any): Double {
   if ((var2 and 1) != 0) {
      var1 = 3;
   }

   return inMemGB(var0, var1);
}

public fun getDateStringFromMillis(duration: Duration): String {
   return getDateStringFromMillis(Duration.getInWholeMilliseconds-impl(duration));
}

public fun getDateStringFromMillis(beginMillis: Long): String {
   val var10000: java.lang.String = dateFormat.format(new Date(beginMillis));
   return var10000;
}
