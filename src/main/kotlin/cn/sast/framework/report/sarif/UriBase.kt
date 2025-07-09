package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class UriBase(
    val uri: String,
    val description: Description? = null
)