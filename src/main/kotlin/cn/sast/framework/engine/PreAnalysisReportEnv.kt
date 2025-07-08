
package cn.sast.framework.engine

import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.BugMessage.Env

data class PreAnalysisReportEnv(
    val file: IBugResInfo,
    val env: Env,
)
