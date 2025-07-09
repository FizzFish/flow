package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class ToolComponent(
    val name: String,
    val organization: String,
    val version: String,
    val rules: List<Rule>
)