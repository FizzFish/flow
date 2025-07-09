package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class CodeFlow(
    val threadFlows: List<ThreadFlow>
)