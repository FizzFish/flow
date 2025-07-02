package cn.sast.common

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer
import kotlin.io.path.exists

/**
 * Kotlin-idiomatic rewrite of the original de-compiled `FileSystemsOperations`.
 *
 * *Only* file/dir copy helpers are preserved; any extra business logic should
 * be added by callers.  Paths are assumed to be *absolute* and normalised.
 */
object FileSystemsOperations {

   /* ---------------------------------------------------
    *  Public API
    * --------------------------------------------------- */

   /**
    * Copy *src directory itself* **and** its whole tree into [dest] which must
    * be a directory on a **different** file-system.
    */
   fun copyDirRecursivelyToDirInDifferentFileSystem(src: Path, dest: Path) {
      require(Files.isDirectory(src)) { "src must be a directory" }
      require(Files.isDirectory(dest)) { "dest must be a directory" }
      require(src.fileSystem !== dest.fileSystem) { "src & dest must be on different file-systems" }
      require(src.parent != null) { "src must not be FS root" }

      Files.walk(src).forEach(Consumer { path ->
         copyPath(path, src.parent, dest)
      })
   }

   /** Copy *contents* of [src] (but not the directory itself) into [dest] on a different FS. */
   fun copyDirContentsRecursivelyToDirInDifferentFileSystem(src: Path, dest: Path) {
      require(Files.isDirectory(src)) { "src must be directory" }
      require(Files.isDirectory(dest)) { "dest must be directory" }
      require(src.fileSystem !== dest.fileSystem) { "src & dest must be on different file-systems" }

      Files.walk(src).forEach(Consumer { path ->
         copyPath(path, src, dest)
      })
   }

   /** Same as above but *keeps the same* file-system; useful for deep copies inside one FS. */
   fun copyDirContentsRecursivelyToDirInSameFileSystem(src: Path, dest: Path) {
      require(Files.isDirectory(src))
      require(Files.isDirectory(dest))
      require(src.fileSystem === dest.fileSystem) { "src & dest must share the same FS" }

      Files.walk(src).forEach(Consumer { path ->
         copyPath(path, src, dest)
      })
   }

   /** Copy a list of [files] (regular files only) into [dest] (different FS). */
   fun copyFilesToDirInDifferentFileSystem(files: List<Path>, dest: Path) {
      require(Files.isDirectory(dest))
      files.forEach { f ->
         require(Files.isRegularFile(f))
         require(f.parent != null && Files.isDirectory(f.parent))
         require(f.fileSystem !== dest.fileSystem)
      }
      files.forEach { f -> copyPath(f, f.parent, dest) }
   }

   /* ---------------------------------------------------
    *  Internal helpers
    * --------------------------------------------------- */

   /**
    * Copy [source] to the location computed by replacing the prefix [srcRoot]
    * with [destRoot].  Directories are recreated, files are copied.
    * The resulting absolute destination path is returned.
    */
   private fun copyPath(source: Path, srcRoot: Path, destRoot: Path): Path {
      require(Files.isDirectory(srcRoot))
      require(Files.isDirectory(destRoot))

      val relative = srcRoot.relativize(source)      // e.g. /src/foo.txt -> foo.txt
      val destination = destRoot.resolve(relative)

      if (destination == destRoot) return destination // root dir already exists
      require(!destination.exists()) { "destination already exists: $destination" }

      when {
         Files.isDirectory(source) -> Files.createDirectory(destination)
         Files.isRegularFile(source) -> Files.copy(source, destination)
         else -> error("Unsupported file type: $source")
      }
      return destination
   }
}
