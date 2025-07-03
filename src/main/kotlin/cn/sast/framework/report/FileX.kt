package cn.sast.framework.report

import cn.sast.api.config.MainConfig.RelativePath
import cn.sast.common.IResFile
import cn.sast.framework.report.sqldelight.AbsoluteFilePath
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.File
import kotlin.collections.first

public class FileX(
    public val file: File,
    public val relativePath: RelativePath,
    public val associateAbsFile: IResFile,
    public val lines: List<String>
) {
    public val fileAbsPath: String = associateAbsFile.toString()

    public fun withId(id: Long): FileX.ID {
        return ID(this, id)
    }

    public fun insert(db: Database): FileX.ID {
        db.getFileQueries()
            .insert(
                file.file_raw_content_hash,
                file.relative_path,
                file.lines,
                file.file_raw_content_size,
                file.file_raw_content
            )
        val id: Long = db.getFileQueries()
            .id(file.file_raw_content_hash, file.relative_path)
            .executeAsList()
            .first()
            .toLong()
        db.getAbsoluteFilePathQueries().insert(AbsoluteFilePath(relativePath.absoluteNormalizePath, id))
        return ID(this, id)
    }

    public inner class ID(
        public val file: FileX,
        public val id: Long
    )
}