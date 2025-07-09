package cn.sast.framework.report

/**
 * 判断两个路径片段列表的后缀关系。
 *
 * @param a 第一个列表
 * @param b 第二个列表
 */
fun listEndsWith(
    a: List<String>,
    b: List<String>
): ListSuffixRelation {
    val minSize = minOf(a.size, b.size)
    if (minSize == 0) return ListSuffixRelation.NeitherSuffix

    // 比较最后 `minSize` 个元素
    for (i in 0 until minSize) {
        if (a[a.size - minSize + i] != b[b.size - minSize + i]) {
            return ListSuffixRelation.NeitherSuffix
        }
    }

    return when {
        a.size < b.size -> ListSuffixRelation.AIsSuffixOfB
        a.size > b.size -> ListSuffixRelation.BIsSuffixOfA
        else            -> ListSuffixRelation.Equals
    }
}
