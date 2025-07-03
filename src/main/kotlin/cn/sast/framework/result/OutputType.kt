package cn.sast.framework.result

import kotlin.enums.EnumEntries

public enum class OutputType(public val displayName: String) {
    PLIST("plist"),
    SARIF("sarif"),
    SQLITE("sqlite"),
    SarifPackSrc("sarif-pack"),
    SarifCopySrc("sarif-copy"),
    Coverage("coverage");

    @JvmStatic
    fun getEntries(): EnumEntries<OutputType> {
        return entries
    }
}