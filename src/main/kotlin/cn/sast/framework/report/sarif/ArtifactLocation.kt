package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class ArtifactLocation(
    val uri: String,
    val uriBaseId: String = ""
)