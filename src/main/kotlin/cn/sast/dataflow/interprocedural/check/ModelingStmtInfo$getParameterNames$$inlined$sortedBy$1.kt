package cn.sast.dataflow.interprocedural.check

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/ModelingStmtInfo\n*L\n1#1,102:1\n447#2:103\n*E\n"])
internal class `ModelingStmtInfo$getParameterNames$$inlined$sortedBy$1`<T> : Comparator {
   // QF: local property
internal fun <T> `<anonymous>`(a: T, b: T): Int {
      return ComparisonsKt.compareValues(a.toString(), b.toString());
   }
}
