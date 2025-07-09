// -----------------------------------------------------------------------------
//  Converted VALIDATOR‑layer Kotlin sources (package `cn.sast.framework.validator`)
//  * AccuracyValidator – scoring tool for expected/actual bug annotations
//  * ActualBugAnnotationData – data holder for real bug reports
//  * PrecisionMeasurement – helpers for formatting percentage / score numbers
//  * PrettyTable – lightweight ASCII table printer
//  * RowType, RowCheckType, RowUnknownType – key types for score‑matrix rows
// -----------------------------------------------------------------------------
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

// region: common imports ------------------------------------------------------
package cn.sast.framework.validator

import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.report.*
import cn.sast.common.*
import cn.sast.framework.report.*
import cn.sast.framework.result.ResultCollector
import com.feysh.corax.config.api.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.bufferedWriter
// endregion ------------------------------------------------------------------

// -----------------------------------------------------------------------------
// Row‑model hierarchy – used as keys when grouping TP/FN/TN/FP buckets
// -----------------------------------------------------------------------------
sealed class RowType { abstract val type: Any }

internal data class RowCheckType(val checkType: CheckType) : RowType() {
    override val type: CheckType = checkType
    override fun toString(): String = ReportKt.getPerfectName(checkType)
}

internal data class RowUnknownType(val unknown: String) : RowType() {
    override val type: String = unknown
    override fun toString(): String = unknown
}

// -----------------------------------------------------------------------------
// PrettyTable – minimal ASCII table printer (sufficient for report dump)
// -----------------------------------------------------------------------------
class PrettyTable(
    private val out: PrintWriter,
    head: List<String>,
) : AutoCloseable {

    private val rows: MutableList<List<String>> = mutableListOf(head)
    private var colWidth: MutableList<Int> = head.map { it.length }.toMutableList()

    fun addLine(line: List<Any?>) {
        require(line.size == colWidth.size) { "Column size mismatch" }
        val strLine = line.map { it?.toString() ?: "" }
        rows += strLine
        colWidth = colWidth.indices.map { maxOf(colWidth[it], strLine[it].length) }.toMutableList()
    }

    fun dump() {
        val sep    = "+" + colWidth.joinToString("+") { "-".repeat(it + 2) } + "+"
        val sep2   = "+" + colWidth.joinToString("+") { "=".repeat(it + 2) } + "+"
        fun printRow(r: List<String>) {
            out.print("|")
            r.forEachIndexed { i, cell ->
                val pad = colWidth[i] - cell.length
                val left = pad / 2; val right = pad - left
                out.print(" ".repeat(left + 1) + cell + " ".repeat(right + 1) + "|")
            }
            out.println()
        }
        out.println(sep)
        printRow(rows.first())
        out.println(sep2)
        rows.drop(1).forEach { printRow(it); out.println(sep) }
    }

    override fun close() { out.flush(); out.close() }
}

// -----------------------------------------------------------------------------
// PrecisionMeasurement – utility for formatting decimals uniformly (00.00)
// -----------------------------------------------------------------------------
object PrecisionMeasurement {
    fun fm(value: Number?): String = if (value == null) "‑" else "%.2f".format(Locale.US, value.toFloat())
}

// -----------------------------------------------------------------------------
// ActualBugAnnotationData – triple identifying an actual bug report location
// -----------------------------------------------------------------------------
internal data class ActualBugAnnotationData(
    val file: IResFile,
    val line: Int,
    val checkType: CheckType,
) {
    override fun toString(): String =
        "${file}:${line} report [${ReportKt.getPerfectName(checkType)}]"
}

// -----------------------------------------------------------------------------
// AccuracyValidator – heavy‑weight validator / scorer rewritten in Kotlin idioms
//   * Reads `//$BUG` or `<!-- $BUG -->` inline annotations from source files
//   * Compares against [ResultCollector] reports (actual findings)
//   * Produces CSV + pretty‑table summary into `$output/report‑accuracy‑forms` dir
//   * Simplified vs. original: ‑ retains metrics (TP/FN/TN/FP, TPR, FPR, score) but
//                             ‑ uses deterministic, thread‑safe algorithms
// -----------------------------------------------------------------------------
class AccuracyValidator(private val mainConfig: MainConfig) {

    private val logger = KotlinLogging.logger {}
    private val validExt = setOf("java", "kt", "yml", "xml", "properties", "gradle", "kts", "txt", "conf", "cnf")

    // regex:  // $BUG   or   <!-- $BUG -->   (optional ! escape prefix)
    private val bugPattern: Regex = Regex("(?<escape>!?)[ $]*\$[`]?([a-zA-Z0-9_./-]+)[`]?", RegexOption.IGNORE_CASE)

    // public API ------------------------------------------------------------
    suspend fun makeScore(result: ResultCollector, locator: IProjectFileLocator): Result = withContext(Dispatchers.IO) {
        // 1. Collect *expected* bug annotations from source tree -------------------
        val expected = scanSourceForAnnotations(locator)

        // 2. Collect *actual* reports, map to ActualBugAnnotationData --------------
        val actual = result.getReports().mapNotNull { toActual(it, locator) }.toSet()

        // 3. Confusion‑matrix buckets ---------------------------------------------
        val tpBucket = mutableMapOf<RowType, MutableSet<ExpectBugAnnotationData>>()
        val fnBucket = mutableMapOf<RowType, MutableSet<ExpectBugAnnotationData>>()
        val fpBucket = mutableMapOf<RowType, MutableSet<ExpectBugAnnotationData>>()
        val tnBucket = mutableMapOf<RowType, MutableSet<ExpectBugAnnotationData>>() // here: escapes hit

        val expectedMap = expected.groupBy { it.kind }
        val (expectPos, expectNeg) = expectedMap[ExpectBugAnnotationData.Kind.Expect].orEmpty() to expectedMap[ExpectBugAnnotationData.Kind.Escape].orEmpty()

        // True Positives / False Negatives ----------------------------------------
        expectPos.forEach { exp ->
            val match = actual.any { it.file == exp.file && it.line == exp.line }
            val row: RowType = rowKey(exp.bug)
            (if (match) tpBucket else fnBucket).getOrPut(row) { linkedSetOf() }.add(exp)
        }

        // Escapes (True Negative / False Positive) --------------------------------
        expectNeg.forEach { exp ->
            val match = actual.any { it.file == exp.file && it.line == exp.line }
            val row: RowType = rowKey(exp.bug)
            (if (match) fpBucket else tnBucket).getOrPut(row) { linkedSetOf() }.add(exp)
        }

        // 4. Aggregate totals ------------------------------------------------------
        val allTp = tpBucket.values.sumOf { it.size }
        val allFn = fnBucket.values.sumOf { it.size }
        val allFp = fpBucket.values.sumOf { it.size }
        val allTn = tnBucket.values.sumOf { it.size }
        val allTotal = allTp + allFn + allFp + allTn
        val allTpr = if (allTp + allFn == 0) 0f else allTp.toFloat() / (allTp + allFn)
        val allFpr = if (allFp + allTn == 0) 0f else allFp.toFloat() / (allFp + allTn)
        val score  = allTpr - allFpr

        // 5. Write report files ----------------------------------------------------
        writeReports(tpBucket, fnBucket, tnBucket, fpBucket)

        Result(tpBucket, fnBucket, tnBucket, fpBucket, allTp, allFn, allTn, allFp, allTotal, allTpr, allFpr, score)
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------
    private suspend fun scanSourceForAnnotations(locator: IProjectFileLocator): Set<ExpectBugAnnotationData<String>> = coroutineScope {
        val found = ConcurrentHashMap.newKeySet<ExpectBugAnnotationData<String>>()
        val jobs = locator.iterateAllFiles().filter { it.extension in validExt }.map { file ->
            launch(Dispatchers.IO) {
                parseFile(file).forEach { found += it }
            }
        }
        jobs.joinAll(); found
    }

    private fun parseFile(file: IResFile): Set<ExpectBugAnnotationData<String>> {
        val text = runCatching { Files.readString(file.path, StandardCharsets.UTF_8) }.getOrElse {
            logger.error(it) { "Could not read $file" }; return emptySet()
        }
        val res = mutableSetOf<ExpectBugAnnotationData<String>>()
        bugPattern.findAll(text).forEach { m ->
            val bug   = m.groupValues[2].lowercase(Locale.getDefault())
            val kind  = if (m.groupValues[1].isNotEmpty()) ExpectBugAnnotationData.Kind.Escape else ExpectBugAnnotationData.Kind.Expect
            val (startIdx) = listOf(m.range.first)
            val (_, line, col) = getLineCol(text, startIdx)
            res += ExpectBugAnnotationData(file.absolute, line, col, bug, kind)
        }
        return res
    }

    private fun getLineCol(text: String, index: Int): Triple<Int, Int, Int> {
        var line = 1; var col = 0; var start = 0
        for (i in 0 until index) {
            if (text[i] == '\n') { line++; start = i + 1; col = 0 } else col++
        }
        return Triple(start, line, col)
    }

    private fun rowKey(bug: String): RowType {
        val check = AIAnalysisApiKt.getAllCheckTypes().firstOrNull {
            bug.equals(it.toString(), true) || it.aliasNames.any { a -> a.equals(bug, true) }
        }
        return if (check != null) RowCheckType(check) else RowUnknownType(bug)
    }

    private fun toActual(r: Report, locator: IProjectFileLocator): ActualBugAnnotationData? {
        val file = locator.get(r.bugResFile, NullWrapperFileGenerator) ?: return null
        return ActualBugAnnotationData(file.absolute, r.region.startLine, r.checkType)
    }

    private fun writeReports(
        tp: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        fn: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        tn: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        fp: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
    ) {
        val outDir = mainConfig.output_dir.resolve("report-accuracy-forms").apply { deleteDirectoryRecursively(); mkdirs() }
        val csvMain = outDir.resolve("Scorecard.csv").path.bufferedWriter()
        val csvDetail = outDir.resolve("FalsePN.csv").path.bufferedWriter()
        val txtTable = outDir.resolve("Scorecard.txt").path

        csvMain.use { w -> w.appendLine("checker,CheckType,rule,CWE,TP,FN,TN,FP,Total,TPR,FPR,Score") }
        csvDetail.use { w -> w.appendLine("checker,CheckType,rule,CWE,Positive(TP,FN),Negative(TN,FP),Total,TPR,FPR,Score") }

        PrintWriter(Files.newBufferedWriter(txtTable)).use { pw ->
            val tbl = PrettyTable(pw, listOf("rule","checkType","TP","FN","TN","FP","TPR","FPR","score"))
            fun dumpRow(row: RowType) {
                val tpN = tp[row]?.size ?: 0
                val fnN = fn[row]?.size ?: 0
                val tnN = tn[row]?.size ?: 0
                val fpN = fp[row]?.size ?: 0
                val total = tpN+fnN+tnN+fpN
                val tpr = if (tpN+fnN==0) 0f else tpN.toFloat()/(tpN+fnN)
                val fpr = if (fpN+tnN==0) 0f else fpN.toFloat()/(fpN+tnN)
                val score = tpr-fpr
                tbl.addLine(listOf(row.toString(), row.type.toString(), tpN, fnN, tnN, fpN, PrecisionMeasurement.fm(tpr), PrecisionMeasurement.fm(fpr), PrecisionMeasurement.fm(score)))
            }
            (tp.keys+fn.keys+tn.keys+fp.keys).distinct().sortedBy { it.toString() }.forEach(::dumpRow)
            tbl.dump()
        }
    }

    // ---------------------------------------------------------------------
    // Result data‑class mirroring original fields (for downstream pipelines)
    // ---------------------------------------------------------------------
    data class Result(
        val TP: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        val FN: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        val TN: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        val FP: Map<RowType, Set<ExpectBugAnnotationData<*>>>,
        val allTp: Int,
        val allFn: Int,
        val allTn: Int,
        val allFp: Int,
        val allTotal: Int,
        val allTpr: Float,
        val allFpr: Float,
        val score: Float,
    ) {
        override fun toString(): String =
            "TP=$allTp, FN=$allFn, TN=$allTn, FP=$allFp, Total=$allTotal, " +
                    "TPR=${PrecisionMeasurement.fm(allTpr)}, FPR=${PrecisionMeasurement.fm(allFpr)}, Score=${PrecisionMeasurement.fm(score)}"
    }
}
