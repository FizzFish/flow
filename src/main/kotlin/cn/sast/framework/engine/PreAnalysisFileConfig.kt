
package cn.sast.framework.engine

import com.feysh.corax.config.api.IPreAnalysisFileConfig

internal class PreAnalysisFileConfig(
    appOnly: Boolean = true,
    ignoreProjectConfigProcessFilter: Boolean = false,
    incrementalAnalyze: Boolean = false,
    skipFilesInArchive: Boolean = true,
) : PreAnalysisAbsConfig(null, incrementalAnalyze, 3), IPreAnalysisFileConfig {

    var appOnly: Boolean = appOnly
        internal set

    override var ignoreProjectConfigProcessFilter: Boolean = ignoreProjectConfigProcessFilter
        set

    override var incrementalAnalyze: Boolean
        get() = super.incrementalAnalyze
        set(value) { super.incrementalAnalyze = value }

    override var skipFilesInArchive: Boolean = skipFilesInArchive
        set
}
