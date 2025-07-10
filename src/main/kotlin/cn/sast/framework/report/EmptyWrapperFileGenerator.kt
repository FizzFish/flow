package cn.sast.framework.report

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import mu.KotlinLogging
import cn.sast.common.writeText

/**
 * 当源码缺失/无法读取时，用空文件占位，避免下游处理 NPE。
 */
object EmptyWrapperFileGenerator : IWrapperFileGenerator {

    private val logger = KotlinLogging.logger {}

    override val name: String = "empty"

    /** 大文件只生成 8 000 行空行；否则 1 行 */
    private fun makeWrapperFileContent(resInfo: IBugResInfo): String =
        when (resInfo) {
            is ClassResInfo -> "\n".repeat(minOf(resInfo.maxLine, 8_000))
            else            -> "\n"
        }

    override fun makeWrapperFile(
        fileWrapperOutPutDir: IResDirectory,
        resInfo: IBugResInfo
    ): IResFile? {
        val outFile = fileWrapperOutPutDir
            .resolve(name)
            .resolve(getInternalFileName(resInfo))
            .toFile()

        if (outFile.exists) {
            return if (outFile.isFile) {
                outFile
            } else {
                logger.error { "duplicate folder exists $outFile" }
                null
            }
        }

        return try {
            outFile.parent?.mkdirs()
            writeText(outFile, makeWrapperFileContent(resInfo))
            outFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
