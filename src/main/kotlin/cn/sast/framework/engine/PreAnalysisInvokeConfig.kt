package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisInvokeConfig

internal class PreAnalysisInvokeConfig(
    appOnly: Boolean = true,
    ignoreProjectConfigProcessFilter: Boolean = false
) : PreAnalysisAbsConfig(null, false, 3), IPreAnalysisInvokeConfig {
    var appOnly: Boolean = appOnly
        internal set

    var ignoreProjectConfigProcessFilter: Boolean = ignoreProjectConfigProcessFilter
        internal set

    constructor() : this(false, false) {
        TODO("FIXME — unclear what the 3 and null parameters in the original were for")
    }
}