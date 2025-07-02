package cn.sast.framework.report

import cn.sast.api.report.Report
import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 ReportConverter.kt\ncn/sast/framework/report/ReportConverter$flush$2$5$worker$1\n*L\n1#1,102:1\n327#2:103\n*E\n"])
internal class `ReportConverter$flush$2$5$worker$1$invokeSuspend$$inlined$sortedBy$1`<T> : Comparator {
   // QF: local property
internal fun <T> `<anonymous>`(a: T, b: T): Int {
      return ComparisonsKt.compareValues(a as Report, b as Report);
   }
}
