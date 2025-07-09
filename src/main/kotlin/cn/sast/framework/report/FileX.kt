package cn.sast.framework.report

import cn.sast.api.config.MainConfig.RelativePath
import cn.sast.common.IResFile
import cn.sast.framework.report.sqldelight.AbsoluteFilePath
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.File as DbFile

/**
 * 把数据库中的 **File** 行 + 真实文件 / 源码行
 * 绑定在一起，便于后续访问。
 */
class FileX(
    val file: DbFile,
    val relativePath: RelativePath,
    val associateAbsFile: IResFile,
    val lines: List<String>
) {
    val fileAbsPath: String = associateAbsFile.toString()

    /** 包装一个已知 ID（无需写库） */
    fun withId(id: Long): ID = ID(id)

    /** 插入数据库并返回写入后的行 ID */
    fun insert(db: Database): ID {
        with(db.fileQueries) {
            insert(
                file_raw_content_hash = file.file_raw_content_hash,
                relative_path         = file.relative_path,
                lines                 = file.lines,
                file_raw_content_size = file.file_raw_content_size,
                file_raw_content      = file.file_raw_content
            )
        }

        val id = db.fileQueries
            .id(file.file_raw_content_hash, file.relative_path)
            .executeAsOne()

        db.absoluteFilePathQueries
            .insert(AbsoluteFilePath(relativePath.absoluteNormalizePath, id))

        return ID(id)
    }

    /** *(inner class)* 行 ID + 外部文件对象 */
    inner class ID(val id: Long) {
        val file: FileX = this@FileX
    }
}
