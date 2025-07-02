package cn.sast.api.report

import com.feysh.corax.config.api.CheckType
import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 Report.kt\ncn/sast/api/report/Report\n*L\n1#1,102:1\n392#2:103\n*E\n"])
internal class `Report$compareTo$$inlined$compareBy$1`<T> : Comparator {
   // QF: local property
internal fun <T> `<anonymous>`(a: T, b: T): Int {
      return ComparisonsKt.compareValues((a as CheckType).getSerializerName(), (b as CheckType).getSerializerName());
   }
}
