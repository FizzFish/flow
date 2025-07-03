package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile

public interface IWrapperFileGenerator {
    public val name: String

    public fun makeWrapperFile(fileWrapperOutPutDir: IResDirectory, resInfo: IBugResInfo): IResFile?

    public fun getInternalFileName(resInfo: IBugResInfo): String {
        return resInfo.getPath()
    }

    internal object DefaultImpls {
        @JvmStatic
        fun getInternalFileName(`$this`: IWrapperFileGenerator, resInfo: IBugResInfo): String {
            return resInfo.getPath()
        }
    }
}