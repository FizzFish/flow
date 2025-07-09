package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class FlowLocation(
    val message: Message,
    val physicalLocation: PhysicalLocation
)