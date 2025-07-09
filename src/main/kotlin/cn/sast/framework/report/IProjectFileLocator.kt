package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.framework.report.AbstractFileIndexer.CompareMode

/**
 * 负责把报告里的 **逻辑文件** 映射到真实文件系统路径。
 */
interface IProjectFileLocator {

    /** 项目源码根目录集合（可运行时更新） */
    var sourceDir: Set<IResource>

    /**
     * 根据 [resInfo] 定位文件。若不存在且允许，可用
     * [fileWrapperIfNotExists] 生成占位文件。
     */
    fun get(
        resInfo: IBugResInfo,
        fileWrapperIfNotExists: IWrapperFileGenerator = NullWrapperFileGenerator
    ): IResFile?

    /** 刷新内部索引（源码目录改变后需调用） */
    fun update()

    /** ---- 批量文件检索（可能较慢，均为 `suspend`） ---- */

    suspend fun getByFileExtension(extension: String): Sequence<IResFile>

    suspend fun getByFileName(filename: String): Sequence<IResFile>

    suspend fun getAllFiles(): Sequence<IResFile>

    /** 底层委托给 [AbstractFileIndexer.findFromFileIndexMap] */
    fun findFromFileIndexMap(
        parentSubPath: List<String>,
        mode: CompareMode = CompareMode.Path
    ): Sequence<IResFile>
}
