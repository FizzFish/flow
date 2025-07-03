package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Tool(
    public val analyzers: Map<String, Analyzer>,
    public val command: List<String>,
    public val name: String,
    @SerialName("output_path")
    public val outputPath: String,
    @SerialName("project_root")
    public val projectRoot: String,
    @SerialName("multiple_project_root")
    public val multipleProjectRoot: List<String>,
    @SerialName("result_source_files")
    public val resultSourceFiles: Map<String, String>,
    @SerialName("working_directory")
    public val workingDirectory: String
) {
    public operator fun component1(): Map<String, Analyzer> = analyzers

    public operator fun component2(): List<String> = command

    public operator fun component3(): String = name

    public operator fun component4(): String = outputPath

    public operator fun component5(): String = projectRoot

    public operator fun component6(): List<String> = multipleProjectRoot

    public operator fun component7(): Map<String, String> = resultSourceFiles

    public operator fun component8(): String = workingDirectory

    public fun copy(
        analyzers: Map<String, Analyzer> = this.analyzers,
        command: List<String> = this.command,
        name: String = this.name,
        outputPath: String = this.outputPath,
        projectRoot: String = this.projectRoot,
        multipleProjectRoot: List<String> = this.multipleProjectRoot,
        resultSourceFiles: Map<String, String> = this.resultSourceFiles,
        workingDirectory: String = this.workingDirectory
    ): Tool {
        return Tool(analyzers, command, name, outputPath, projectRoot, multipleProjectRoot, resultSourceFiles, workingDirectory)
    }

    public override fun toString(): String {
        return "Tool(analyzers=$analyzers, command=$command, name=$name, outputPath=$outputPath, projectRoot=$projectRoot, multipleProjectRoot=$multipleProjectRoot, resultSourceFiles=$resultSourceFiles, workingDirectory=$workingDirectory)"
    }

    public override fun hashCode(): Int {
        return (
            (
                (
                    (((analyzers.hashCode() * 31 + command.hashCode()) * 31 + name.hashCode()) * 31 + outputPath.hashCode())
                        * 31
                        + projectRoot.hashCode()
                )
                * 31
                + multipleProjectRoot.hashCode()
            )
            * 31
            + resultSourceFiles.hashCode()
        )
        * 31
        + workingDirectory.hashCode()
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Tool) {
            return false
        } else {
            if (analyzers != other.analyzers) {
                return false
            } else if (command != other.command) {
                return false
            } else if (name != other.name) {
                return false
            } else if (outputPath != other.outputPath) {
                return false
            } else if (projectRoot != other.projectRoot) {
                return false
            } else if (multipleProjectRoot != other.multipleProjectRoot) {
                return false
            } else if (resultSourceFiles != other.resultSourceFiles) {
                return false
            } else {
                return workingDirectory == other.workingDirectory
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Tool> {
            return Tool.$serializer.INSTANCE as KSerializer<Tool>
        }
    }
}