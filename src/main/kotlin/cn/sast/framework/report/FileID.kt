package cn.sast.framework.report

import cn.sast.common.IResFile

/**
 * 数据库行 → 真实文件 的映射。
 */
class FileID(
    val id: Long,
    val associateAbsFile: IResFile
) {
    val fileAbsPath: String = associateAbsFile.toString()
}
