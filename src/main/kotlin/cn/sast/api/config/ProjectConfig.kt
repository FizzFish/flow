package cn.sast.api.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.ErrorCommit
import kotlinx.serialization.decodeFromString

/**
 * project-level YAML：只包含一组 [ProcessRegex]（scan include/exclude）。
 */
@Serializable
data class ProjectConfig(
    @SerialName("process-regex")
    val processRegex: ProcessRegex = ProcessRegex()
) {
    /** 反序列化时保存来源 yml 文件位置（运行期使用，不参与序列化） */
    @Transient
    var ymlFile: File? = null
        private set

    companion object {
        /** 缺省 YAML 文件名（可按需用） */
        const val RECORD_FILE_NAME = "project-config.yaml"

        /**
         * 从 YAML 文件读取 [ProjectConfig]。
         * 会把读取到的 File 存入 [ymlFile] 方便后续定位。
         */
        fun load(yamlFile: File): ProjectConfig =
            Yaml.default.decodeFromString<ProjectConfig>(yamlFile.readText())
                .also { it.ymlFile = yamlFile }
    }
}
fun List<IMatchItem>.validate() {
    for (e in this) {
        if (e is ErrorCommit && e.error != null) {
            error("Invalid process-regex: `$e`, error: ${e.error}")
        }
    }
}