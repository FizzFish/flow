package cn.sast.framework.report

/**
 * 判断两个 **字符串列表** 的“后缀”关系。
 */
enum class ListSuffixRelation(val neitherSuffix: Boolean) {
    /** 完全相同 */
    Equals(false),

    /** A ⊂ B 且 A 是 B 的后缀 */
    AIsSuffixOfB(false),

    /** B ⊂ A 且 B 是 A 的后缀 */
    BIsSuffixOfA(false),

    /** 既不是 A⊂B 也不是 B⊂A 的后缀关系 */
    NeitherSuffix(true)
}
