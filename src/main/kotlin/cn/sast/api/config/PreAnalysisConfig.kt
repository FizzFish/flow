package cn.sast.api.config

import cn.sast.common.zipExtensions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 预分析阶段的资源大小/并发等限制配置。
 */
@Serializable
@SerialName("PreAnalysisConfig")
data class PreAnalysisConfig(
    @SerialName("cancel_analysis_in_error_count")
    val cancelAnalysisInErrorCount: Int = 10,

    @SerialName("large_file_size")
    val largeFileSize: Int = 500 * 1024,               // 500 KB

    @SerialName("large_file_semaphore_permits")
    val largeFileSemaphorePermits: Int = 3,

    @SerialName("file_extension_to_size_threshold")
    private val fileSizeThresholdsRaw: Map<String, Int> = mapOf(
        // Java/Kotlin 源文件
        listOf(
            "java", "kt").joinToString(",") to 1 * 1024 * 1024, // 1 MB
        // 压缩包
        zipExtensions.joinToString(",")                    to -1,              // 无限
        // 文本类
        listOf(
            "html", "htm", "adoc", "gradle", "properties", "config",
            "cnf", "txt", "json", "xml", "yml", "yaml", "toml", "ini"
        ).joinToString(",")                                to 5 * 1024 * 1024, // 5 MB
        "*"                                               to 5 * 1024 * 1024  // 其它
    ),

    @SerialName("maximum_file_size_threshold_warnings")
    val maximumFileSizeThresholdWarnings: Int = 20
) {

    /** 展平后的扩展名 → 阈值映射（"." 不带） */
    @Transient
    val fileSizeThresholds: Map<String, Int> by lazy {
        fileSizeThresholdsRaw.flatMap { (ks, v) ->
            ks.split(',').map { it.trim() to v }
        }.toMap()
    }

    /** 取得某扩展名对应阈值（<=0 视为“无限制”） */
    fun sizeThreshold(ext: String): Int? =
        fileSizeThresholds[ext] ?: fileSizeThresholds["*"]?.takeIf { it > 0 }

    /** 判断文件大小是否超限 */
    fun exceeds(ext: String, size: Long): Boolean =
        sizeThreshold(ext)?.let { size > it } ?: false
}
