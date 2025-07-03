package cn.sast.framework

import kotlin.enums.EnumEntries

public enum class CgAlgorithmProvider {
    Soot,
    QiLin;

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<CgAlgorithmProvider> {
            return entries
        }
    }
}