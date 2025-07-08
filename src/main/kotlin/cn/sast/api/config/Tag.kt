package cn.sast.api.config

import kotlinx.serialization.Serializable

/**
 * Checker tag 元数据：<标准, 规则号>
 * 例如 `"CERT","MSC13-C"`
 */
@Serializable
data class Tag(
    val standard: String,
    val rule: String
)
