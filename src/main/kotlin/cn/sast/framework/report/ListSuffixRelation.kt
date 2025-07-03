package cn.sast.framework.report

import kotlin.enums.EnumEntries

public enum class ListSuffixRelation(public val neitherSuffix: Boolean) {
    Equals(false),
    AIsSuffixOfB(false),
    BIsSuffixOfA(false),
    NeitherSuffix(true);

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<ListSuffixRelation> {
            return entries
        }
    }
}