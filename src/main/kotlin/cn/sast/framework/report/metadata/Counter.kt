package cn.sast.framework.report.metadata

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS

/**
 * JaCoCo 风格的行级计数器
 */
@Serializable
data class Counter(
    @EncodeDefault(ALWAYS) var missed:  Int = 0,
    @EncodeDefault(ALWAYS) var covered: Int = 0
)
