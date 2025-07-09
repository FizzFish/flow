package cn.sast.framework.report.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 单个分析器统计信息
 */
@Serializable
data class AnalyzerStatistics(
    val failed: Int = 0,

    @SerialName("failed_sources")
    val failedSources: List<String> = emptyList(),

    val successful: Int = 0,

    @SerialName("successful_sources")
    val successfulSources: List<String> = emptyList(),

    /** 版本号（留空表示未知） */
    val version: String = ""
)
