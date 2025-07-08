package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisConfig
import com.feysh.corax.config.api.rules.ProcessRule

/**
 * Abstract base for every concrete *Pre-analysis XXXConfig*.
 *
 * The default values reproduce the behaviour found in the original
 * de-compiled byte-code:
 *
 * * `processRules` → empty list
 * * `incrementalAnalyze` → **true**
 *
 * Kotlin’s default-argument machinery automatically generates the
 * synthetic overloads (`(List;Z;I;DefaultConstructorMarker)`) that the
 * de-compiled Java shows, so no extra “dummy” parameter is required.
 */
abstract class PreAnalysisAbsConfig @JvmOverloads constructor(
    override var processRules: List<ProcessRule.IMatchItem> = emptyList(),
    override var incrementalAnalyze: Boolean = true,
) : IPreAnalysisConfig
