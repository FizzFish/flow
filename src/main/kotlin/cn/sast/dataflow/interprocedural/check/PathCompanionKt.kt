@file:SourceDebugExtension(["SMAP\nPathCompanion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PathCompanionKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,994:1\n1628#2,3:995\n*S KotlinDebug\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PathCompanionKt\n*L\n643#1:995,3\n*E\n"])

package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import java.util.Collections
import java.util.HashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension

public val bindDelegate: IValue
    get() = if (this is CompanionValueOfConst) {
        (this as CompanionValueOfConst).getAttr()
    } else {
        this.getValue() as IValue
    }

private val <K, V> Map<K, V>.short: Map<K, V>
    get() = when (size) {
        0 -> Collections.emptyMap()
        1 -> {
            val entry = entries.iterator().next()
            Collections.singletonMap(entry.key, entry.value)
        }
        else -> this
    }

private val <E> Set<E>.short: Set<E>
    get() = when (size) {
        0 -> Collections.emptySet()
        1 -> Collections.singleton(iterator().next())
        else -> this
    }

public fun IHeapValues<IValue>.path(env: HeapValuesEnv): IPath {
    val `$this$mapTo$iv`: Iterable<*> = getValuesCompanion() as Iterable<*>
    val destination = HashSet<IPath>(getValuesCompanion().size)

    for (item in `$this$mapTo$iv`) {
        val it = item as CompanionV
        destination.add((it as PathCompanionV).path)
    }

    return MergePath.Companion.v(env, destination)
}

@JvmSynthetic
internal fun access$getShort(receiver: Map<*, *>): Map<*, *> = receiver.short