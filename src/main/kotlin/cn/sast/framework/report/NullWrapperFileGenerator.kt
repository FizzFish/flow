package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile

object NullWrapperFileGenerator : IWrapperFileGenerator {
    override val name: String
        get() = "null"

    override fun makeWrapperFile(fileWrapperOutPutDir: IResDirectory, resInfo: IBugResInfo): IResFile? {
        return null
    }

    override fun getInternalFileName(resInfo: IBugResInfo): String {
        return IWrapperFileGenerator.DefaultImpls.getInternalFileName(this, resInfo)
    }
}