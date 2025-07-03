package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisConfig
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem

internal abstract class PreAnalysisAbsConfig(
    processRules: MutableList<ProcessRule.IMatchItem>,
    incrementalAnalyze: Boolean
) : IPreAnalysisConfig {
    public open var processRules: List<IMatchItem> = processRules
        internal set

    public open var incrementalAnalyze: Boolean = incrementalAnalyze
        internal set

    constructor() : this(mutableListOf(), false)
}