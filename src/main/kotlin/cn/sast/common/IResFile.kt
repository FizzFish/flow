package cn.sast.common

public interface IResFile : IResource {
   public val entries: Set<String>
   public val md5: String
   public val absolute: IResFile
   public val normalize: IResFile

   public abstract fun expandRes(outPut: IResDirectory): IResFile {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun getZipLike(`$this`: IResFile): Boolean {
         return IResource.DefaultImpls.getZipLike(`$this`);
      }

      @Throws(java/io/IOException::class)
      @JvmStatic
      fun deleteDirectoryRecursively(`$this`: IResFile) {
         IResource.DefaultImpls.deleteDirectoryRecursively(`$this`);
      }

      @Throws(java/io/IOException::class)
      @JvmStatic
      fun deleteDirectoryContents(`$this`: IResFile) {
         IResource.DefaultImpls.deleteDirectoryContents(`$this`);
      }
   }
}
