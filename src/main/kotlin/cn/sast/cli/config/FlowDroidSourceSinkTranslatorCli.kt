package cn.sast.cli.config

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import kotlin.io.path.exists
import kotlin.io.path.extension
import java.nio.file.Path
import java.nio.file.Paths
import java.io.File
import mu.KotlinLogging
import kotlin.system.exitProcess

/**
 * 将 FlowDroid 的 Source/Sink 列表转换为其他格式或拷贝到目标位置。
 *
 * 目前仅做输入校验 —— 如需实际转换逻辑，可在 TODO 处补充。
 */
class FlowDroidSourceSinkTranslatorCli : CliktCommand(
    name = "flowdroid-ss-translator",
    help = "Translate FlowDroid Sources&Sinks definitions"
) {
    /** 源文件：缺省使用 FlowDroid 自带 `SourcesAndSinks.txt` */
    private val sourceSinkFile: String by option(
        "--source-sink-file",
        help = "Path to FlowDroid source-sink definition file"
    ).default("DEFAULT")

    /** 输出目录 */
    private val outDir: String by option(
        "--out",
        help = "Output directory"
    ).default("out/flowdroid/Taint")

    private val logger = KotlinLogging.logger {}

    override fun run() {
        // ------------------------------------------------------------------ //
        // 1) 确定输入文件
        // ------------------------------------------------------------------ //
        val ssPath: Path = if (sourceSinkFile == "DEFAULT") {
            val defaultFile = flowDroidClass.resolve("SourcesAndSinks.txt")
            logger.info { "Use default source-sink file: $defaultFile" }
            defaultFile
        } else {
            Paths.get(sourceSinkFile)
        }

        // 校验存在性 / 文件属性
        val ssFile: File = ssPath.toFile()
        require(ssFile.exists()) { "[$ssPath] does not exist" }
        require(ssFile.isFile)   { "[$ssPath] is not a file" }

        // ------------------------------------------------------------------ //
        // 2) 尝试解析，验证格式
        // ------------------------------------------------------------------ //
        val provider = getFlowDroidSourceSinkProvider(
            ssPath.extension,
            ssFile.canonicalPath
        ) ?: error("[$ssPath] is not a recognised FlowDroid source-sink file")

        // ------------------------------------------------------------------ //
        // 3) TODO: 具体转换 / 输出逻辑
        // ------------------------------------------------------------------ //
        logger.info {
            "Parsed ${provider.javaClass.simpleName} successfully; will output to '$outDir'"
        }
    }
}
