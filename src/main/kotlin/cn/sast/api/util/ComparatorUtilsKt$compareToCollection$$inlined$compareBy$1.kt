package cn.sast.api.util

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.comparisons.ComparisonsKt

@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 ComparatorUtils.kt\ncn/sast/api/util/ComparatorUtilsKt\n*L\n1#1,102:1\n28#2:103\n*E\n"])
internal class `ComparatorUtilsKt$compareToCollection$$inlined$compareBy$1`<T> : Comparator<T> {
    override fun compare(a: T, b: T): Int {
        return ComparisonsKt.compareValues(a as Comparable<*>, b as Comparable<*>)
    }
}