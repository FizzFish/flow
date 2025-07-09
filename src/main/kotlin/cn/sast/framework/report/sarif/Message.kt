package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
public data class Message(
    public val text: String,
    public val markdown: String = ""
)