package cn.sast.framework.metrics

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitor\n*L\n1#1,102:1\n196#2:103\n*E\n"])
internal class `MetricsMonitor$serialize$lambda$15$$inlined$compareBy$1`<T> : Comparator {
   // QF: local property
internal fun <T> `<anonymous>`(a: T, b: T): Int {
      return ComparisonsKt.compareValues((a as MetricsMonitor.PhaseTimer).getName(), (b as MetricsMonitor.PhaseTimer).getName());
   }
}
