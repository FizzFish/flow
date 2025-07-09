package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile

/**
 * 空实现：**不** 生成占位文件。
 */
object NullWrapperFileGenerator : IWrapperFileGenerator {
    override val name: String = "null"

    override fun makeWrapperFile(
        fileWrapperOutPutDir: IResDirectory,
        resInfo: IBugResInfo
    ): IResFile? = null
}
