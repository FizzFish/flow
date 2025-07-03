package cn.sast.framework.report

import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.framework.report.AbstractFileIndexer.CompareMode

public interface IProjectFileLocator {
    public var sourceDir: Set<IResource>
        internal set

    public abstract fun get(resInfo: IBugResInfo, fileWrapperIfNotEExists: IWrapperFileGenerator = TODO("FIXME — default parameter value")): IResFile?

    public abstract fun update()

    public abstract suspend fun getByFileExtension(extension: String): Sequence<IResFile>

    public abstract suspend fun getByFileName(filename: String): Sequence<IResFile>

    public abstract suspend fun getAllFiles(): Sequence<IResFile>

    public abstract fun findFromFileIndexMap(parentSubPath: List<String>, mode: CompareMode): Sequence<IResFile>

    internal class DefaultImpls
}