package cn.sast.framework.report

import cn.sast.api.config.MainConfig.RelativePath
import cn.sast.common.IResFile
import cn.sast.framework.report.sqldelight.AbsoluteFilePath
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.File

public class FileX(file: File, relativePath: RelativePath, associateAbsFile: IResFile, lines: List<String>) {
   public final val file: File
   public final val relativePath: RelativePath
   public final val associateAbsFile: IResFile
   public final val lines: List<String>
   public final val fileAbsPath: String

   init {
      this.file = file;
      this.relativePath = relativePath;
      this.associateAbsFile = associateAbsFile;
      this.lines = lines;
      this.fileAbsPath = this.associateAbsFile.toString();
   }

   public fun withId(id: Long): cn.sast.framework.report.FileX.ID {
      return new FileX.ID((long)this, id);
   }

   public fun insert(db: Database): cn.sast.framework.report.FileX.ID {
      db.getFileQueries()
         .insert(
            this.file.getFile_raw_content_hash(),
            this.file.getRelative_path(),
            this.file.getLines(),
            this.file.getFile_raw_content_size(),
            this.file.getFile_raw_content()
         );
      val id: Long = (CollectionsKt.first(db.getFileQueries().id(this.file.getFile_raw_content_hash(), this.file.getRelative_path()).executeAsList()) as java.lang.Number)
         .longValue();
      db.getAbsoluteFilePathQueries().insert(new AbsoluteFilePath(this.relativePath.getAbsoluteNormalizePath(), id));
      return new FileX.ID((long)this, id);
   }

   public inner class ID(id: Long) {
      public final val id: Long
      public final val file: FileX

      init {
         this.this$0 = `this$0`;
         this.id = id;
         this.file = this.this$0;
      }
   }
}
