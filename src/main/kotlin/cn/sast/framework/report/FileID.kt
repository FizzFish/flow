package cn.sast.framework.report

import cn.sast.common.IResFile

public class FileID(public final val id: Long, public final val associateAbsFile: IResFile) {
    public final val fileAbsPath: String = associateAbsFile.toString()
}