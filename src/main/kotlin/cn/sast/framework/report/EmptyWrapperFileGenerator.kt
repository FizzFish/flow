package cn.sast.framework.report

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.ResourceKt
import java.io.IOException
import mu.KLogger
import mu.KotlinLogging

object EmptyWrapperFileGenerator : IWrapperFileGenerator {
    private val logger: KLogger = KotlinLogging.logger { }

    override val name: String
        get() = "empty"

    private fun makeWrapperFileContent(resInfo: IBugResInfo): String {
        return if (resInfo is ClassResInfo) {
            var maxLine = resInfo.getMaxLine()
            if (maxLine > 8000) {
                maxLine = 8000
            }
            "\n".repeat(maxLine)
        } else {
            "\n"
        }
    }

    override fun makeWrapperFile(fileWrapperOutPutDir: IResDirectory, resInfo: IBugResInfo): IResFile? {
        val missingSourceFile = fileWrapperOutPutDir.resolve(name).resolve(getInternalFileName(resInfo)).toFile()
        return if (missingSourceFile.exists()) {
            if (missingSourceFile.isFile) {
                missingSourceFile
            } else {
                logger.error { "duplicate folder exists $missingSourceFile" }
                null
            }
        } else {
            val text = makeWrapperFileContent(resInfo)

            try {
                missingSourceFile.parent?.mkdirs()
                ResourceKt.writeText(missingSourceFile, text)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

            missingSourceFile.toFile()
        }
    }

    override fun getInternalFileName(resInfo: IBugResInfo): String {
        return IWrapperFileGenerator.DefaultImpls.getInternalFileName(this, resInfo)
    }
}