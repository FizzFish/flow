package cn.sast.api.config

import com.charleskorn.kaml.Yaml
import java.io.File
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ProjectConfig(
    @SerialName("process-regex")
    public val processRegex: ProcessRegex = ProcessRegex(null, null, null, 7, null)
) {
    @Transient
    public var ymlFile: File? = null

    public operator fun component1(): ProcessRegex {
        return this.processRegex
    }

    public fun copy(processRegex: ProcessRegex = this.processRegex): ProjectConfig {
        return ProjectConfig(processRegex)
    }

    public override fun toString(): String {
        return "ProjectConfig(processRegex=${this.processRegex})"
    }

    public override fun hashCode(): Int {
        return this.processRegex.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is ProjectConfig) {
            return false
        } else {
            return this.processRegex == other.processRegex
        }
    }

    @SourceDebugExtension(["SMAP\nProjectConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ProjectConfig.kt\ncn/sast/api/config/ProjectConfig$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,45:1\n1#2:46\n*E\n"])
    public companion object {
        public const val RECORD_FILE_NAME: String = TODO("FIXME — missing constant value")

        public fun load(yamlFile: File): ProjectConfig {
            val var2 = Yaml.default
                .decodeFromString(serializer(), yamlFile.readText())
            (var2 as ProjectConfig).ymlFile = yamlFile
            return var2
        }

        public fun serializer(): KSerializer<ProjectConfig> {
            return ProjectConfig.serializer()
        }
    }
}