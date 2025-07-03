package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisMethodConfig

internal class PreAnalysisMethodConfig(
    appOnly: Boolean = true,
    ignoreProjectConfigProcessFilter: Boolean = false
) : PreAnalysisAbsConfig(null, false, 3), IPreAnalysisMethodConfig {
    override var appOnly: Boolean = appOnly
        internal set

    override var ignoreProjectConfigProcessFilter: Boolean = ignoreProjectConfigProcessFilter
        internal set

    constructor() : this(false, false)
}