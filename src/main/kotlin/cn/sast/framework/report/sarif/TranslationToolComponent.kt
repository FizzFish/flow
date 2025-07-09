package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class TranslationToolComponent(
    val language: String,
    val name: String,
    val rules: List<Rule>
)