package cn.sast.api.report

import com.feysh.corax.config.api.CheckType
import java.util.Locale
import kotlin.enums.EnumEntries
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/CheckType2StringKind\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public enum class CheckType2StringKind(convert: (CheckType) -> String) {
    RuleDotTYName(CheckType2StringKind::_init_$lambda$0),
    RuleDotTYName2(CheckType2StringKind::_init_$lambda$1),
    RuleName(CheckType2StringKind::_init_$lambda$2);

    public final val convert: (CheckType) -> String

    @JvmStatic
    public companion object {
        private const val ruleNameKindEnv: String = "REPORT_RULE_KIND"
        public val checkType2StringKind: CheckType2StringKind

        init {
            val envValue = System.getenv(ruleNameKindEnv) ?: System.getProperty(ruleNameKindEnv)
            checkType2StringKind = if (envValue != null) {
                valueOf(envValue)
            } else {
                RuleDotTYName
            }
        }
    }

    init {
        this.convert = convert
    }

    @JvmStatic
    public fun getEntries(): EnumEntries<CheckType2StringKind> {
        return entries
    }

    @JvmStatic
    private fun _init_$lambda$0(t: CheckType): String {
        return "${t.getReport().getRealName()}.${t.getClass().getSimpleName()}"
    }

    @JvmStatic
    private fun _init_$lambda$1(t: CheckType): String {
        val var10000 = t.getReport().getRealName()
        var var10001 = t.getClass().getSimpleName()
        val var2 = Locale.getDefault()
        var10001 = var10001.toLowerCase(var2)
        return "$var10000.$var10001"
    }

    @JvmStatic
    private fun _init_$lambda$2(t: CheckType): String {
        return t.getReport().getRealName()
    }
}