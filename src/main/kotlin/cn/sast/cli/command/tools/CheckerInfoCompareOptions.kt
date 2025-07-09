package cn.sast.cli.command.tools

import cn.sast.common.Resource
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.file
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.system.exitProcess

class CheckerInfoCompareOptions : OptionGroup("Compare checker_info.json Options") {

    private val compareLeft: File by option("--left", help = "Left checker_info.json").file(mustExist = true).required()
    private val compareRight: File by option("--right", help = "Right checker_info.json").file(mustExist = true).required()

    fun run() {
        val output: Path = compareLeft.toPath().parent.resolve("compare-result").apply { if (!exists()) createDirectories() }.normalize()
        logger.info { "The compare output path is: $output" }

        CheckerInfoCompare().compareWith(
            output,
            Resource.fileOf(compareLeft.path),
            Resource.fileOf(compareRight.path)
        )

        exitProcess(0)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}