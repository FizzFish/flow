package cn.sast.framework.report

import cn.sast.common.IResFile
import java.util.Comparator
import kotlin.comparisons.ComparisonsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 ReportConverter.kt\ncn/sast/framework/report/ReportConverter\n*L\n1#1,102:1\n96#2:103\n*E\n"])
internal class `ReportConverter$reportSourceFileWhichClassNotFound$lambda$6$$inlined$sortedBy$1`<T> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return ComparisonsKt.compareValues((a as IResFile).toString(), (b as IResFile).toString())
    }
}