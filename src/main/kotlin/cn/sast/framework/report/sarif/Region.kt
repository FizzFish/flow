package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val startLine: Int,
    val startColumn: Int = 0,
    val endLine: Int = 0,
    val endColumn: Int = 0
)