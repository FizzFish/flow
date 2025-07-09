package cn.sast.framework.report.coverage

import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.FileIndexerBuilder
import mu.KotlinLogging
import org.jacoco.core.analysis.ISourceFileCoverage
import org.jacoco.core.internal.analysis.CounterImpl

/**
 * 某项目所有源码的覆盖率快照；
 * `sourceCoverage`：<relativePath, JavaSourceCoverage>
 */
class SourceCoverage(
    private val sourceCoverage: Map<String, JavaSourceCoverage>
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 将已知覆盖率 + 未收集到的源码行，合并成 *全局行计数*。
     */
    fun calculateCoveredRatio(targetSources: Set<IResFile>): CounterImpl {
        var totalLines = 0
        var missed     = 0

        /* --- 双向映射：relativePath → IResFile --- */
        val indexer = FileIndexerBuilder().apply {
            targetSources.forEach { addIndexMap(it) }
        }.build()

        /* --- 累计已统计文件 --- */
        val usedSources = mutableSetOf<IResFile>()
        for ((relPath, cov) in sourceCoverage) {
            val srcFile = indexer.findFromFileIndexMap(
                relPath.split('/', '\\'),
                AbstractFileIndexer.defaultClassCompareMode
            ).firstOrNull() ?: continue

            usedSources += srcFile
            totalLines += cov.lineCount
            missed     += cov.sourceCoverage.lineCounter.missedCount
                .coerceAtMost(cov.lineCount)
        }

        /* --- 对剩余未统计源码补 0% 覆盖 --- */
        (targetSources - usedSources).forEach { file ->
            val lines = try {
                ResourceKt.readText(file).lineSequence().count()
            } catch (e: Exception) {
                logger.error(e) { "Read source failed: $file" }
                0
            }
            totalLines += lines
            missed     += lines
        }

        return CounterImpl.getInstance(missed, totalLines - missed)
    }

    /* ------------------------------------------------------------------ */
    data class JavaSourceCoverage(
        val lineCount: Int,
        val sourceCoverage: ISourceFileCoverage
    ) {
        init { require(lineCount >= 0) }
    }
}
