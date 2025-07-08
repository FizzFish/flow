
package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisFieldConfig

internal class PreAnalysisFieldConfig(
    appOnly: Boolean = true,
    ignoreProjectConfigProcessFilter: Boolean = false,
) : PreAnalysisAbsConfig(null, false, 3), IPreAnalysisFieldConfig {

    override var appOnly: Boolean = appOnly
        internal set

    override var ignoreProjectConfigProcessFilter: Boolean = ignoreProjectConfigProcessFilter
        internal set
}
