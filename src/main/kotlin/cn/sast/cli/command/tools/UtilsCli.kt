package cn.sast.cli.command.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import cn.sast.common.OS
import mu.KotlinLogging
import kotlin.system.exitProcess

class UtilsCli : CliktCommand(name = "Utils", help = "Utility commands") {

    private val input: File? by option("-i", "--input", help = "Input CSV").file().nullable()
    private val output: File? by option("-o", "--output", help = "Output CSV").file().nullable()
    private val csvDeleteColumns: List<String> by option("--csv-delete-columns", help = "Comma-separated columns to delete")
        .multiple()
        .split(",")

    override fun run() {
        if (csvDeleteColumns.isEmpty()) return

        val deleteColumns = csvDeleteColumns.map { it.trim() }.filter { it.isNotEmpty() }

        val inputFile = input ?: error("input is required")
        val outputFile = output ?: error("output is required")

        val rows = csvReader().readAll(inputFile)
        val header = rows.first()

        val columnIdxToDelete = deleteColumns.map { column ->
            val idx = header.indexOf(column)
            require(idx >= 0) { "$column not exists in header: $header" }
            idx
        }.sortedDescending()

        csvWriter().open(outputFile) {
            rows.forEach { row ->
                val mutable = row.toMutableList()
                columnIdxToDelete.forEach { idx -> mutable.removeAt(idx) }
                writeRow(mutable)
            }
        }
    }
}

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    OS.setArgs(args)

    try {
        UtilsCli().main(args)
        exitProcess(0)
    } catch (t: Throwable) {
        logger.error(t) { "An error occurred: $t" }
        exitProcess(1)
    }
}
