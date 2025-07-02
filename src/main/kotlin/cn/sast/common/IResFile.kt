package cn.sast.common

/**
 * Representation of a file resource.
 */
interface IResFile : IResource {
    val entries: Set<String>
    val md5: String
    override val absolute: IResFile
    override val normalize: IResFile

    fun expandRes(outPut: IResDirectory): IResFile
}
