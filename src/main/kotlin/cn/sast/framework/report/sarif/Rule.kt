package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val id: String,
    val name: String,
    val messageStrings: MessageStrings
)