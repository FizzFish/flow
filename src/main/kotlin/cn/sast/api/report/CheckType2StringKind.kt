package cn.sast.api.report

import com.feysh.corax.config.api.CheckType
import java.util.Locale

/**
 * 运行时从 `CheckType` 变换到字符串的三种策略。
 * 可通过系统环境变量 / JVM 参数 `REPORT_RULE_KIND` 覆盖。
 */
enum class CheckType2StringKind(val convert: (CheckType) -> String) {

    /** `CERT.MSC13-C` → `CERT.MSC13-C.ClassName` */
    RuleDotTYName({ "${it.report.realName}.${it::class.simpleName}" }),

    /** 同上但把类名转小写 */
    RuleDotTYName2({
        "${it.report.realName}.${it::class.simpleName.lowercase(Locale.getDefault())}"
    }),

    /** 仅保留规则名 */
    RuleName({ it.report.realName });

    companion object {
        private const val ENV_KEY = "REPORT_RULE_KIND"

        /** 当前生效策略 */
        val active: CheckType2StringKind =
            runCatching { System.getenv(ENV_KEY) ?: System.getProperty(ENV_KEY) }
                .getOrNull()
                ?.let { valueOf(it) }
                ?: RuleDotTYName
    }
}
