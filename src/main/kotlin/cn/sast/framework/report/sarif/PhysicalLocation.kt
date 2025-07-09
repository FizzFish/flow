package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class PhysicalLocation(
    val artifactLocation: ArtifactLocation,
    val region: Region
)