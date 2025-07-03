@file:SourceDebugExtension(["SMAP\nComparatorUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ComparatorUtils.kt\ncn/sast/api/util/ComparatorUtilsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,37:1\n1#2:38\n*E\n"])

package cn.sast.api.util

import java.util.Comparator
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

public fun <T> Comparator<T>.compareTo(a: Collection<T>, b: Collection<T>): Int {
    val bI = b.iterator()

    for (e in a) {
        if (!bI.hasNext()) {
            return 1
        }

        val c = compare(e, bI.next())
        if (c != 0) {
            return c
        }
    }

    return if (bI.hasNext()) -1 else 0
}

public fun <K : Comparable<K>, V : Comparable<V>> Comparator<Pair<K, V>>.compareToMap(a: Map<K, V>, b: Map<K, V>): Int {
    val bSorted = Intrinsics.compare(a.size, b.size)
    val aSorted = if (bSorted != 0) bSorted else null
    return if (aSorted != null)
        aSorted
    else
        compareTo(
            this,
            a.toList().sortedWith(this),
            b.toList().sortedWith(this)
        )
}

public fun <K : Comparable<K>, V : Comparable<V>> Map<K, V>.compareToMap(other: Map<K, V>): Int {
    return Comparator<Pair<K, V>> { p1, p2 -> p1.first.compareTo(p2.first) }
        .thenComparator { p1, p2 -> p1.second.compareTo(p2.second) }
        .compareToMap(this, other)
}

public fun <E : Comparable<E>> Collection<E>.compareToCollection(other: Collection<E>): Int {
    return Comparator<E> { a, b -> a.compareTo(b) }.compareTo(this, other)
}

public fun <T : Comparable<T>> T?.compareToNullable(other: T?): Int {
    if (this == null && other == null) {
        return 0
    } else if (this == null) {
        return 1
    } else {
        if (other == null) {
            return -1
        } else {
            val var3 = this.compareTo(other)
            val var2 = if (var3 != 0) var3 else null
            return var2 ?: 0
        }
    }
}