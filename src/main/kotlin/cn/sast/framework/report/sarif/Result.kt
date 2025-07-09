package cn.sast.framework.report.sarif

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val ruleId: String,
    val ruleIndex: Int,
    @EncodeDefault(mode = Mode.ALWAYS)
    val message: IndexedMessage = IndexedMessage(null, 1, null),
    val locations: List<Location>,
    val codeFlows: List<CodeFlow> = emptyList()
)