package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile

/**
 * 当真实源码/资源缺失时，按需生成“占位文件”。
 */
interface IWrapperFileGenerator {
    /** 供输出目录区分不同生成器的子目录名 */
    val name: String

    /**
     * 生成占位文件，返回生成后的 *物理* 文件。（可返回 `null` 表示失败）
     */
    fun makeWrapperFile(
        fileWrapperOutPutDir: IResDirectory,
        resInfo: IBugResInfo
    ): IResFile?

    /**
     * 计算在输出目录中应使用的文件名。<br/>
     * 默认返回漏洞资源自身的 `path` 字段。
     */
    fun getInternalFileName(resInfo: IBugResInfo): String = resInfo.path
}
