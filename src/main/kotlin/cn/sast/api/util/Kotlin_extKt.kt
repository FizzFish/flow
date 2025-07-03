@file:SourceDebugExtension(["SMAP\nkotlin-ext.kt\nKotlin\n*S Kotlin\n*F\n+ 1 kotlin-ext.kt\ncn/sast/api/util/Kotlin_extKt\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,13:1\n136#2,9:14\n216#2:23\n217#2:25\n145#2:26\n1#3:24\n9326#4,2:27\n9476#4,4:29\n*S KotlinDebug\n*F\n+ 1 kotlin-ext.kt\ncn/sast/api/util/Kotlin_extKt\n*L\n7#1:14,9\n7#1:23\n7#1:25\n7#1:26\n7#1:24\n10#1:27,2\n10#1:29,4\n*E\n"])

package cn.sast.api.util

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap

public fun <K, V> Map<K, V?>.nonNullValue(): Map<K, V> {
    val destination = ArrayList<Pair<K, V>>()

    for (element in this.entries) {
        val value = element.value
        if (value != null) {
            destination.add(element.key to value)
        }
    }

    return destination.toMap()
}

public fun <E> concurrentHashSetOf(vararg pairs: E): MutableSet<E> {
    val result = LinkedHashMap<E, Boolean>(maxOf(MapsKt.mapCapacity(pairs.size), 16)

    for (element in pairs) {
        result[element] = true
    }

    return Collections.newSetFromMap(ConcurrentHashMap(result))
}

public fun <E> concurrentHashSetOf(): MutableSet<E> {
    return Collections.newSetFromMap(ConcurrentHashMap<E, Boolean>())
}