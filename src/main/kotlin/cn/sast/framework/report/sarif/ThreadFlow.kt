package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class ThreadFlow(
    val locations: List<FlowLocationWrapper>
)