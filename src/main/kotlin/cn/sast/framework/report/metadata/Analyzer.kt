package cn.sast.framework.report.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 分析器条目：统计 + 启用的 checker 映射
 */
@Serializable
data class Analyzer(
    @SerialName("analyzer_statistics")
    val analyzerStatistics: AnalyzerStatistics,

    /** checkerId → checkerVersion */
    val checkers: Map<String, String> = emptyMap()
)
