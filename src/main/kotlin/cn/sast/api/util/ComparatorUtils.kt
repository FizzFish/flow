package cn.sast.api.util

import java.util.Comparator
import kotlin.Comparator as KtComparator   // disambiguate
import kotlin.comparisons.thenComparator

/**
 * 逐元素比较两个集合（按迭代器顺序）。
 * – 若 A 比 B 长且 B 是 A 的前缀 → 1
 * – 若 B 比 A 长且 A 是 B 的前缀 → −1
 * – 否则返回第一个不相等元素的比较结果
 */
fun <T> KtComparator<T>.compareCollections(a: Collection<T>, b: Collection<T>): Int {
    val iterB = b.iterator()
    for (elemA in a) {
        if (!iterB.hasNext()) return 1
        val c = compare(elemA, iterB.next())
        if (c != 0) return c
    }
    return if (iterB.hasNext()) -1 else 0
}

/* ---------- Map 深度比较 ---------- */

/**
 * 按 *key → value* 的优先级对两个 Map 进行深层比较。
 * 当大小不同先按 size；若 size 相同再逐条按给定 Comparator。
 */
fun <K : Comparable<K>, V : Comparable<V>>
        KtComparator<Pair<K, V>>.compareMaps(a: Map<K, V>, b: Map<K, V>): Int {

    val sizeDiff = a.size.compareTo(b.size)
    if (sizeDiff != 0) return sizeDiff

    /** 按设定 Comparator 排序后当作列表继续比较 */
    return compareCollections(
        a.toList().sortedWith(this),
        b.toList().sortedWith(this)
    )
}

/**
 * 默认使用 `(key asc) -> (value asc)` 的双重排序规则。
 */
fun <K : Comparable<K>, V : Comparable<V>> Map<K, V>.compareToMap(other: Map<K, V>): Int =
    Comparator<Pair<K, V>> { p1, p2 -> p1.first.compareTo(p2.first) }
        .thenComparator { p1, p2 -> p1.second.compareTo(p2.second) }
        .compareMaps(this, other)

/* ---------- Collection 简易比较 ---------- */

/** 使用元素本身的 `compareTo` 依序比较两个集合。 */
fun <E : Comparable<E>> Collection<E>.compareToCollection(other: Collection<E>): Int =
    Comparator<E> { a, b -> a.compareTo(b) }
        .compareCollections(this, other)

/* ---------- Nullable 比较 ---------- */

/**
 * `null` 被视为 *最小*，与 Java/Kotlin 标准排序保持一致：
 * - both null  → 0
 * - `this` null → 1
 * - `other` null → −1
 */
fun <T : Comparable<T>> T?.compareToNullable(other: T?): Int =
    when {
        this === null && other === null -> 0
        this === null -> 1
        other === null -> -1
        else -> this.compareTo(other).let { if (it != 0) it else 0 }
    }