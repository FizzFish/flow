package cn.sast.api.report

import com.feysh.corax.config.api.IRule
import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.comparisons.ComparisonsKt

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 Report.kt\ncn/sast/api/report/Report\n*L\n1#1,102:1\n393#2:103\n*E\n"])
internal class `Report$compareTo$$inlined$compareBy$2`<T> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return ComparisonsKt.compareValues((a as IRule).getRealName(), (b as IRule).getRealName())
    }
}