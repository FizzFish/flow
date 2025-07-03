package cn.sast.framework.report.coverage

import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.FileIndexer
import cn.sast.framework.report.FileIndexerBuilder
import java.util.LinkedHashMap
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.jacoco.core.analysis.ISourceFileCoverage
import org.jacoco.core.internal.analysis.CounterImpl

@SourceDebugExtension(["SMAP\nCoverage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/SourceCoverage\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n1863#2,2:491\n1279#2,2:493\n1293#2,4:495\n*S KotlinDebug\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/SourceCoverage\n*L\n59#1:491,2\n63#1:493,2\n63#1:495,4\n*E\n"])
class SourceCoverage(sourceCoverage: MutableMap<String, SourceCoverage.JavaSourceCoverage>) {
    val sourceCoverage: MutableMap<String, JavaSourceCoverage> = sourceCoverage

    fun calculateCoveredRatio(targetSources: Set<IResFile>): CounterImpl {
        var allLineCount = 0
        var missedCount = 0
        val fileIndexerBuilder = FileIndexerBuilder()

        val fileIndexer: Iterable<Any> = TODO("FIXME — uninitialized iterable")
        for (element in fileIndexer) {
            fileIndexerBuilder.addIndexMap(element as IResFile)
        }

        val fileIndexerInstance = fileIndexerBuilder.build()
        val coverageList = this.sourceCoverage.toList()
        val sourceMap = LinkedHashMap<Any, IResFile>(coverageList.size.coerceAtLeast(16))

        for (element in sourceMap) {
            sourceMap[element] = fileIndexerInstance.findFromFileIndexMap(
                (element as Pair<*, *>).first.toString().split("/", "\\"),
                AbstractFileIndexer.Companion.defaultClassCompareMode
            ).firstOrNull() as IResFile
        }

        for (entry in sourceMap.entries) {
            val coverage = (entry.key as Pair<*, *>).second as JavaSourceCoverage
            allLineCount += coverage.lineCount
            val missed = coverage.sourceCoverage.lineCounter.missedCount
            missedCount += if (missed <= coverage.lineCount) missed else coverage.lineCount
        }

        for (source in targetSources.minus(sourceMap.values.filterNotNull().toSet())) {
            val lineCount = try {
                ResourceKt.readText(source).lineSequence().count()
            } catch (e: Exception) {
                logger.error("File $source cannot be read!", e)
                0
            }
            allLineCount += lineCount
            missedCount += lineCount
        }

        return CounterImpl.getInstance(missedCount, allLineCount - missedCount)
    }

    companion object {
        private val logger: KLogger = TODO("FIXME — initialize logger")
    }

    data class JavaSourceCoverage(
        val lineCount: Int,
        val sourceCoverage: ISourceFileCoverage
    ) {
        init {
            require(lineCount >= 0) { "Check failed." }
        }

        fun copy(
            lineCount: Int = this.lineCount,
            sourceCoverage: ISourceFileCoverage = this.sourceCoverage
        ) = JavaSourceCoverage(lineCount, sourceCoverage)

        override fun hashCode(): Int {
            return lineCount.hashCode() * 31 + sourceCoverage.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is JavaSourceCoverage) return false
            return lineCount == other.lineCount && sourceCoverage == other.sourceCoverage
        }

        override fun toString() = "JavaSourceCoverage(lineCount=$lineCount, sourceCoverage=$sourceCoverage)"
    }
}