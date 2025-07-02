package cn.sast.framework.report

import cn.sast.common.IResFile

public class FileID(id: Long, associateAbsFile: IResFile) {
   public final val id: Long
   public final val associateAbsFile: IResFile
   public final val fileAbsPath: String

   init {
      this.id = id;
      this.associateAbsFile = associateAbsFile;
      this.fileAbsPath = this.associateAbsFile.toString();
   }
}
