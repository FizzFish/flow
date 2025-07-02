package cn.sast.common

public interface IResDirectory : IResource {
   public val normalize: IResDirectory
   public val absolute: IResDirectory

   public abstract fun expandRes(outPut: IResDirectory): IResDirectory {
   }

   public abstract fun listPathEntries(glob: String = ...): List<IResource> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun getZipLike(`$this`: IResDirectory): Boolean {
         return IResource.DefaultImpls.getZipLike(`$this`);
      }

      @Throws(java/io/IOException::class)
      @JvmStatic
      fun deleteDirectoryRecursively(`$this`: IResDirectory) {
         IResource.DefaultImpls.deleteDirectoryRecursively(`$this`);
      }

      @Throws(java/io/IOException::class)
      @JvmStatic
      fun deleteDirectoryContents(`$this`: IResDirectory) {
         IResource.DefaultImpls.deleteDirectoryContents(`$this`);
      }
   }
}
