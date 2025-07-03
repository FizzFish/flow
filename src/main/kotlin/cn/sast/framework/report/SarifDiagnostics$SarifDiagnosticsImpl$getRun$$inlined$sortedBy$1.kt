package cn.sast.framework.report

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 SarifDiagnostics.kt\ncn/sast/framework/report/SarifDiagnostics$SarifDiagnosticsImpl\n*L\n1#1,102:1\n118#2:103\n*E\n"])
internal class `SarifDiagnostics$SarifDiagnosticsImpl$getRun$$inlined$sortedBy$1`<T> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return compareValues((a as Pair<*, *>).second as Int, (b as Pair<*, *>).second as Int)
    }
}