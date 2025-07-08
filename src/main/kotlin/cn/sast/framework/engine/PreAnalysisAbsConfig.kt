
package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisConfig
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem

/**
 * Base class for all *PreAnalysisXxxConfig* implementations.
 *
 * The original byte‑code contains synthetic call‑sites like
 * `PreAnalysisAbsConfig(null, false, 3)`.  
 * In order to stay binary‑compatible we provide an additional `dummy`
 * parameter that is ignored at runtime.
 */
abstract class PreAnalysisAbsConfig @JvmOverloads constructor(
    processRules: MutableList<IMatchItem>? = null,
    incrementalAnalyze: Boolean = false,
    @Suppress("UNUSED_PARAMETER") dummy: Int = 0,
) : IPreAnalysisConfig
