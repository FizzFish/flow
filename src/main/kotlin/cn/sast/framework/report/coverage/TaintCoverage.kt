package cn.sast.framework.report.coverage

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

/**
 * Jacoco HTML 结果美化：把“红/绿条”颜色对调，突出污点覆盖。
 */
class TaintCoverage : Coverage() {

    /* 将资源文件复制/替换到输出目录 */
    private fun copyResource(name: String, to: IResFile) {
        javaClass.getResourceAsStream(name)?.use { ins ->
            to.path.outputStream().use { outs -> ins.copyTo(outs) }
        }
    }

    private fun applyTheme(reportRoot: IResDirectory) {
        val resDir = reportRoot.resolve("jacoco-resources")
        copyResource("/jacoco/taint-report.css", resDir.resolve("report.css").toFile())
        copyResource("/jacoco/greenbar.gif",    resDir.resolve("redbar.gif").toFile())
        copyResource("/jacoco/redbar.gif",      resDir.resolve("greenbar.gif").toFile())
    }

    override suspend fun flushCoverage(
        locator: IProjectFileLocator,
        outputDir: IResDirectory,
        encoding: Charset
    ) {
        super.flushCoverage(locator, outputDir, encoding)
        withContext(Dispatchers.IO) { applyTheme(outputDir) }
    }
}
