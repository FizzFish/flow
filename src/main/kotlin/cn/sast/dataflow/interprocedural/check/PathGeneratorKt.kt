@file:SourceDebugExtension(["SMAP\nPathGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,213:1\n774#2:214\n865#2,2:215\n1557#2:217\n1628#2,3:218\n*S KotlinDebug\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorKt\n*L\n22#1:214\n22#1:215,2\n23#1:217\n23#1:218,3\n*E\n"])

package cn.sast.dataflow.interprocedural.check

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.collections.CollectionsKt
import kotlin.Pair

public fun <E> List<E>.removeAdjacentDuplicates(): List<E> {
    return if (isEmpty()) {
        this
    } else {
        val zipped = this.zipWithNext()
        val filtered = zipped.filter { (first, second) -> first != second }
        val mapped = filtered.map { (first, _) -> first }
        mapped + last()
    }
}