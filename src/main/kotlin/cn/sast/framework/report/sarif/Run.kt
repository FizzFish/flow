package cn.sast.framework.report.sarif

import kotlinx.serialization.Serializable

@Serializable
data class Run(
    val tool: Tool,
    val originalUriBaseIds: Map<String, UriBase> = emptyMap(),
    val results: List<Result>,
    val translations: List<TranslationToolComponent>
)