package cn.sast.framework.entries.apk

import kotlin.enums.EnumEntries

public enum class CallbackAnalyzerType {
    Default,
    Fast;

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<CallbackAnalyzerType> {
            return entries
        }
    }
}