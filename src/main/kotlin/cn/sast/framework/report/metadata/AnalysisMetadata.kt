package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

@Serializable
public data class AnalysisMetadata(
    @SerialName("file_count")
    public val fileCount: Int,
    @SerialName("line_count")
    public val lineCount: Int,
    @SerialName("code_coverage")
    public val codeCoverage: Counter,
    @SerialName("num_of_report_dir")
    public val numOfReportDir: Int,
    @SerialName("source_paths")
    public val sourcePaths: List<String>,
    @SerialName("os_name")
    public val osName: String,
    public val tools: List<Tool>
) {
    public fun toJson(): String {
        return jsonFormat.encodeToString(serializer(), this)
    }

    public operator fun component1(): Int = this.fileCount

    public operator fun component2(): Int = this.lineCount

    public operator fun component3(): Counter = this.codeCoverage

    public operator fun component4(): Int = this.numOfReportDir

    public operator fun component5(): List<String> = this.sourcePaths

    public operator fun component6(): String = this.osName

    public operator fun component7(): List<Tool> = this.tools

    public fun copy(
        fileCount: Int = this.fileCount,
        lineCount: Int = this.lineCount,
        codeCoverage: Counter = this.codeCoverage,
        numOfReportDir: Int = this.numOfReportDir,
        sourcePaths: List<String> = this.sourcePaths,
        osName: String = this.osName,
        tools: List<Tool> = this.tools
    ): AnalysisMetadata {
        return AnalysisMetadata(fileCount, lineCount, codeCoverage, numOfReportDir, sourcePaths, osName, tools)
    }

    public override fun toString(): String {
        return "AnalysisMetadata(fileCount=$fileCount, lineCount=$lineCount, codeCoverage=$codeCoverage, numOfReportDir=$numOfReportDir, sourcePaths=$sourcePaths, osName=$osName, tools=$tools)"
    }

    public override fun hashCode(): Int {
        return (
            (
                (
                    ((fileCount.hashCode() * 31 + lineCount.hashCode()) * 31 + codeCoverage.hashCode()) * 31
                        + numOfReportDir.hashCode()
                    ) * 31
                    + sourcePaths.hashCode()
                ) * 31
                + osName.hashCode()
            ) * 31
            + tools.hashCode()
        )
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnalysisMetadata) return false
        
        return fileCount == other.fileCount &&
            lineCount == other.lineCount &&
            codeCoverage == other.codeCoverage &&
            numOfReportDir == other.numOfReportDir &&
            sourcePaths == other.sourcePaths &&
            osName == other.osName &&
            tools == other.tools
    }

    @JvmStatic
    private fun JsonBuilder.`jsonFormat$lambda$0`() {
        this.setUseArrayPolymorphism(true)
        this.setPrettyPrint(true)
        this.setEncodeDefaults(false)
    }

    public companion object {
        private val jsonFormat: Json = Json {
            jsonFormat$lambda$0()
        }

        public fun serializer(): KSerializer<AnalysisMetadata> = AnalysisMetadata.serializer()
    }
}