package cn.sast.framework.report.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 整体工具运行信息（对应 SARIF `tool` 概念）
 */
@Serializable
data class Tool(
    val analyzers: Map<String, Analyzer> = emptyMap(),
    val command: List<String>            = emptyList(),
    val name: String                     = "corax",

    @SerialName("output_path")
    val outputPath: String,

    @SerialName("project_root")
    val projectRoot: String = "",

    @SerialName("multiple_project_root")
    val multipleProjectRoot: List<String> = emptyList(),

    /** 结果文件 → 源文件 的映射 */
    @SerialName("result_source_files")
    val resultSourceFiles: Map<String, String> = emptyMap(),

    @SerialName("working_directory")
    val workingDirectory: String = System.getProperty("user.dir")
)
