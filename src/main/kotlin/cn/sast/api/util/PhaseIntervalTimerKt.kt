@file:SourceDebugExtension(["SMAP\nPhaseIntervalTimer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PhaseIntervalTimer.kt\ncn/sast/api/util/PhaseIntervalTimerKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,124:1\n1#2:125\n*E\n"])

package cn.sast.api.util

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.jvm.internal.SourceDebugExtension

public const val CONVERT_TO_SECONDS: Double = 1.0E9

public fun retainDecimalPlaces(value: Double, scale: Int, roundingMode: RoundingMode = RoundingMode.HALF_EVEN): Double {
   return if (java.lang.Double.isInfinite(value) || java.lang.Double.isNaN(value)) value else new BigDecimal(value).setScale(scale, roundingMode).doubleValue();
}

@JvmSynthetic
fun `retainDecimalPlaces$default`(var0: Double, var2: Int, var3: RoundingMode, var4: Int, var5: Any): Double {
   if ((var4 and 4) != 0) {
      var3 = RoundingMode.HALF_EVEN;
   }

   return retainDecimalPlaces(var0, var2, var3);
}

public fun Number?.nanoTimeInSeconds(scale: Int = 3): Double {
   if (`$this$nanoTimeInSeconds` != null) {
      val var2: java.lang.Double = `$this$nanoTimeInSeconds`.doubleValue();
      val it: Double = var2.doubleValue();
      val var10000: java.lang.Double = if (!java.lang.Double.isInfinite(it) && !java.lang.Double.isNaN(it)) var2 else null;
      if (var10000 != null) {
         return retainDecimalPlaces$default(var10000 / 1.0E9, scale, null, 4, null);
      }
   }

   return -1.0;
}

@JvmSynthetic
fun `nanoTimeInSeconds$default`(var0: java.lang.Number, var1: Int, var2: Int, var3: Any): Double {
   if ((var2 and 1) != 0) {
      var1 = 3;
   }

   return nanoTimeInSeconds(var0, var1);
}

public fun currentNanoTime(): Long {
   return System.nanoTime();
}
