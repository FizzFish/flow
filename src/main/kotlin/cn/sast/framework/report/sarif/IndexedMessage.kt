package cn.sast.framework.report.sarif

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * SARIF “messageStrings” 区里的索引消息。
 *
 * - **默认值** `"default"`
 * - 始终写出（即使取默认），因此使用 `@EncodeDefault(Mode.ALWAYS)`
 */
@Serializable
data class IndexedMessage(
    @EncodeDefault(Mode.ALWAYS)
    val id: String = "default",
)
