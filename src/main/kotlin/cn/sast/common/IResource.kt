package cn.sast.common

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path

public sealed interface IResource : java.lang.Comparable<IResource> {
   public val path: Path
   public val name: String
   public val extension: String
   public val exists: Boolean
   public val isFile: Boolean
   public val isDirectory: Boolean
   public val isRegularFile: Boolean
   public val absolutePath: String
   public val absolute: IResource
   public val normalize: IResource
   public val isFileScheme: Boolean
   public val isJrtScheme: Boolean
   public val isJarScheme: Boolean
   public val schemePath: Path
   public val uri: URI
   public val url: URL
   public val zipEntry: String?
   public val file: File
   public val parent: IResource?

   public open val zipLike: Boolean
      public open get() {
      }


   public val pathString: String

   public abstract fun resolve(name: String): IResource {
   }

   public abstract fun toFile(): IResFile {
   }

   public abstract fun toDirectory(): IResDirectory {
   }

   public abstract fun expandRes(outPut: IResDirectory): IResource {
   }

   public abstract fun mkdirs() {
   }

   @Throws(java/io/IOException::class)
   public open fun deleteDirectoryRecursively() {
   }

   @Throws(java/io/IOException::class)
   public open fun deleteDirectoryContents() {
   }

   public abstract fun seq(): Sequence<Path> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun getZipLike(`$this`: IResource): Boolean {
         return ResourceKt.getZipLike(`$this`.getPath());
      }

      @Throws(java/io/IOException::class)
      @JvmStatic
      fun deleteDirectoryRecursively(`$this`: IResource) {
         PathExtensionsKt.deleteDirectoryRecursively(`$this`.getPath());
      }

      @Throws(java/io/IOException::class)
      @JvmStatic
      fun deleteDirectoryContents(`$this`: IResource) {
         PathExtensionsKt.deleteDirectoryContents(`$this`.getPath());
      }
   }
}
