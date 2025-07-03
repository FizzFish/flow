package cn.sast.cli.config

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

@SourceDebugExtension(["SMAP\nFlowDroidSourceSinkTranslate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowDroidSourceSinkTranslate.kt\ncn/sast/cli/config/FlowDroidSourceSinkTranslatorCli\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,143:1\n1#2:144\n*E\n"])
class FlowDroidSourceSinkTranslatorCli : CliktCommand(
    name = "help",
    invokeWithoutSubcommand = false,
    printHelpOnEmptyArgs = false,
    help = "Flow Droid Source Sink Translator"
) {
    val sourceSinkFile: String by OptionWithValuesKt.option(
        ParameterHolder = this,
        names = emptyArray(),
        help = "sourceSinkFile"
    ).default("DEFAULT")

    val out: String by OptionWithValuesKt.option(
        ParameterHolder = this,
        names = emptyArray(),
        help = "sourceSinkFile"
    ).default("out/flowdroid/Taint")

    override fun run() {
        val path: Path = if (sourceSinkFile == "DEFAULT") {
            logger.info { run$lambda$0() }
            FlowDroidSourceSinkTranslateKt.getFlowDroidClass().resolve("SourcesAndSinks.txt")
        } else {
            Paths.get(sourceSinkFile)
        }

        val file = path.toFile()
        when {
            !file.exists() -> throw IllegalArgumentException("[$path] not exists")
            !file.isFile -> throw IllegalArgumentException("[$path] not a file")
            FlowDroidSourceSinkTranslateKt.getFlowDroidSourceSinkProvider(
                file.extension,
                file.canonicalPath
            ) == null -> throw IllegalArgumentException("[$path] not a valid flowdroid source sink file")
        }
    }

    private fun run$lambda$0(): String {
        return "use default source sink file: ${FlowDroidSourceSinkTranslateKt.getFlowDroidLoc()}"
    }

    companion object {
        private val logger: KLogger
    }
}