package cn.sast.dataflow.analysis.unused

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootMethod

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 UnusedDetector.kt\ncn/sast/dataflow/analysis/unused/UnusedDetector\n*L\n1#1,102:1\n183#2:103\n*E\n"])
internal class `UnusedDetector$analyze$$inlined$sortedBy$1`<T> : Comparator {
   // QF: local property
internal fun <T> `<anonymous>`(a: T, b: T): Int {
      return ComparisonsKt.compareValues((a as SootMethod).getSignature(), (b as SootMethod).getSignature());
   }
}
