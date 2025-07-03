package cn.sast.api.config

import kotlin.enums.EnumEntries

public enum class SrcPrecedence(public val sootFlag: Int) {
    prec_class(1),
    prec_only_class(2),
    prec_jimple(3),
    prec_java(6),
    prec_java_soot(4),
    prec_apk(5),
    prec_apk_class_jimple(6),
    prec_dotnet(7);

    public val isSootJavaSourcePrec: Boolean
        get() = this == prec_java_soot

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<SrcPrecedence> {
            return entries
        }
    }
}