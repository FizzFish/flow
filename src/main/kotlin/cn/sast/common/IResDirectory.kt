package cn.sast.common

/**
 * Representation of a directory resource.
 */
interface IResDirectory : IResource {
    override val normalize: IResDirectory
    override val absolute: IResDirectory

    fun expandRes(outPut: IResDirectory): IResDirectory

    fun listPathEntries(glob: String = "*"): List<IResource>
}
