package cn.sast.framework.report.coverage

import cn.sast.api.report.*
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import kotlinx.coroutines.*
import org.jacoco.core.analysis.ICounter
import java.nio.charset.Charset

/**
 * 把 **执行覆盖率** + **污点覆盖率** 两张表合并管理；
 * 提供统一的 flush / 统计 API。
 *
 * @param enableCoveredTaint flush 时是否在 taint 报告中标注“已执行行”
 */
class JacocoCompoundCoverage(
    private val locator: IProjectFileLocator,
    private val taintCoverage: Coverage = TaintCoverage(),
    private val executionCoverage: Coverage = Coverage(),
    val enableCoveredTaint: Boolean = false
) : ICoverageCollector {

    /* 收集单条记录 */
    override fun cover(coverInfo: CoverData) = when (coverInfo) {
        is CoverTaint -> taintCoverage  .coverByQueue(coverInfo.className, coverInfo.lineNumber)
        is CoverInst  -> executionCoverage.coverByQueue(coverInfo.className, coverInfo.lineNumber)
    }

    /* 并行输出两份报告 */
    override suspend fun flush(output: IResDirectory, sourceEncoding: Charset) = coroutineScope {
        val taintJob = async { taintCoverage.flushCoverage(locator, output.resolve("taint-coverage"), sourceEncoding) }
        val execJob  = async { executionCoverage.flushCoverage(locator, output.resolve("code-coverage" ), sourceEncoding) }
        taintJob.await(); execJob.await()
    }

    /* 统计整体覆盖率（行级） */
    override suspend fun getCoveredLineCounter(
        allSourceFiles: Set<IResFile>,
        encoding: Charset
    ): ICounter = executionCoverage
        .calculateSourceCoverage(locator, encoding)
        .calculateCoveredRatio(allSourceFiles)
}
