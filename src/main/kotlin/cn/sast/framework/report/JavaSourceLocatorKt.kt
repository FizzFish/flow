package cn.sast.framework.report

public fun listEndsWith(array1: List<String>, array2: List<String>): ListSuffixRelation {
    val minSize: Int = minOf(array1.size, array2.size)
    if (minSize == 0) {
        return ListSuffixRelation.NeitherSuffix
    } else {
        for (i in 0 until minSize) {
            if (array1[array1.size - minSize + i] != array2[array2.size - minSize + i]) {
                return ListSuffixRelation.NeitherSuffix
            }
        }

        return when {
            array1.size < array2.size -> ListSuffixRelation.AIsSuffixOfB
            array1.size > array2.size -> ListSuffixRelation.BIsSuffixOfA
            else -> ListSuffixRelation.Equals
        }
    }
}