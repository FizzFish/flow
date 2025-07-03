package cn.sast.framework.report.sarif

import kotlin.enums.EnumEntries

public enum class Level(public val value: String) {
    None("none"),
    Note("note"),
    Warning("warning"),
    Error("error");

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<Level> {
            return entries
        }
    }
}