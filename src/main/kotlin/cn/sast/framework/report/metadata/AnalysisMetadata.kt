package cn.sast.framework.report.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.serializer

/**
 * 最终写入 `metadata.json` 的结构
 */
@Serializable
data class AnalysisMetadata(
    @SerialName("file_count")       val fileCount:      Int,
    @SerialName("line_count")       val lineCount:      Int,
    @SerialName("code_coverage")    val codeCoverage:   Counter,
    @SerialName("num_of_report_dir")val numOfReportDir: Int = 1,
    @SerialName("source_paths")     val sourcePaths:    List<String> = emptyList(),
    @SerialName("os_name")          val osName:         String       = System.getProperty("os.name"),
    val tools: List<Tool> = emptyList()
) {
    fun toJson(pretty: Boolean = true): String =
        jsonFormat(pretty).encodeToString(serializer(), this)

    companion object {
        private fun jsonFormat(pretty: Boolean): Json = Json {
            prettyPrint          = pretty
            encodeDefaults       = false
            useArrayPolymorphism = true
        }
    }
}
