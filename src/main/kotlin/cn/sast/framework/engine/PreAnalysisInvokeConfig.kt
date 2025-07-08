
package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisInvokeConfig

internal class PreAnalysisInvokeConfig(
    appOnly: Boolean = true,
    ignoreProjectConfigProcessFilter: Boolean = false,
) : PreAnalysisAbsConfig(null, false, 3), IPreAnalysisInvokeConfig {

    override var appOnly: Boolean = appOnly
        set

    override var ignoreProjectConfigProcessFilter: Boolean = ignoreProjectConfigProcessFilter
        set
}
