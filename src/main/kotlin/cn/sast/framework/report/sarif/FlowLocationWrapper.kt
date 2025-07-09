package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class FlowLocationWrapper(val location: FlowLocation)