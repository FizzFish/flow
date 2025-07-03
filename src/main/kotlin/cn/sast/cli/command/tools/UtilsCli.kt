package cn.sast.cli.command.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.options.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import kotlin.io.path.*

/**
 * 工具 CLI：当前仅实现 **CSV 删除列** 功能：
 *
 * ```bash
 * utils --input a.csv --output b.csv --csv-del "col1,col2"
 * ```
 */
class UtilsCli : CliktCommand(
    name = "Utils",
    printHelpOnEmptyArgs = true
) {
    /* ---------- CLI 选项 ---------- */

    private val input by option("-i", "--input", help = "Input CSV file")
        .path(exists = true, mustBeReadable = true)

    private val output by option("-o", "--output", help = "Output CSV file")
        .path()

    private val csvDeleteColumns by option(
        "--csv-del",
        help = "Columns to delete, comma-separated or repeatable"
    ).multiple()

    /* ---------- help formatter ---------- */

    override fun context(): Context = super.context().apply {
        helpFormatter = MordantHelpFormatter(this)
    }

    /* ---------- 主逻辑 ---------- */

    override fun run() {
        if (csvDeleteColumns.isEmpty()) {
            echo("Nothing to do (no --csv-del)."); return
        }
        val inFile  = input ?: error("input is required (-i)")
        val outFile = output ?: error("output is required (-o)")

        val deleteCols = csvDeleteColumns
            .flatMap { it.split(',') }
            .map { it.trim() }
            .also { require(it.none(String::isBlank)) { "column names cannot be blank" } }

        /* ---------- 读取 ---------- */
        val rows = csvReader().readAll(inFile.toFile())
        require(rows.isNotEmpty()) { "CSV is empty" }

        val header = rows.first()
        val deleteIdx = deleteCols.map { name ->
            header.indexOf(name).takeIf { it >= 0 }
                ?: error("$name not found in header: $header")
        }.sortedDescending()   // 从大到小便于删除

        /* ---------- 删除列 ---------- */
        val newRows = rows.map { row ->
            val mutable = row.toMutableList()
            deleteIdx.forEach { mutable.removeAt(it) }
            mutable
        }

        /* ---------- 写入 ---------- */
        outFile.parent?.createDirectories()
        csvWriter().open(outFile.toFile()) { newRows.forEach(::writeRow) }
    }
}
