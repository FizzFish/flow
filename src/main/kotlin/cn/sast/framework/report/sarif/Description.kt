package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class Description(public val text: String)