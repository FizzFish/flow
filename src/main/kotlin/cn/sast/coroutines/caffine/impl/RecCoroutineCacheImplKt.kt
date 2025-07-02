package cn.sast.coroutines.caffine.impl

import cn.sast.api.util.PhaseIntervalTimerKt
import com.github.benmanes.caffeine.cache.stats.CacheStats

public fun CacheStats.pp(): String {
   return "hit:${pp$n(`$this$pp`.hitRate())} miss:${pp$n(`$this$pp`.missRate())} penalty:${pp$n(`$this$pp`.averageLoadPenalty())} failure:${pp$n(
      `$this$pp`.loadFailureRate()
   )} $`$this$pp`";
}

fun Double.`pp$n`(): Double {
   return PhaseIntervalTimerKt.retainDecimalPlaces$default(`$this$pp_u24n`, 2, null, 4, null);
}
