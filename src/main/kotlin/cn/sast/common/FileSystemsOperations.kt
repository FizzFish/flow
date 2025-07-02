package cn.sast.common

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension

public class FileSystemsOperations {
   @SourceDebugExtension(["SMAP\nFileSystemsOperations.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileSystemsOperations.kt\ncn/sast/common/FileSystemsOperations$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,84:1\n1863#2,2:85\n1863#2,2:87\n*S KotlinDebug\n*F\n+ 1 FileSystemsOperations.kt\ncn/sast/common/FileSystemsOperations$Companion\n*L\n62#1:85,2\n69#1:87,2\n*E\n"])
   public companion object {
      private fun copyPath(it: Path, src: Path, dest: Path): Path {
         if (_Assertions.ENABLED && !Files.isDirectory(src)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && !Files.isDirectory(dest)) {
            throw new AssertionError("Assertion failed");
         } else {
            val var8: Path = this.mapToDestination(it, src, dest);
            if (!(var8 == dest)) {
               if (_Assertions.ENABLED && Files.exists(var8)) {
                  throw new AssertionError("Assertion failed");
               }

               if (Files.isDirectory(it)) {
                  Files.createDirectory(var8);
               } else if (Files.isRegularFile(it)) {
                  Files.copy(it, var8);
               } else if (_Assertions.ENABLED) {
                  throw new AssertionError("Assertion failed");
               }
            }

            return var8;
         }
      }

      private fun mapToDestination(path: Path, srcDir: Path, destDir: Path): Path {
         val var10001: java.lang.String = srcDir.relativize(path).toString();
         val var10002: java.lang.String = srcDir.getFileSystem().getSeparator();
         val var10003: java.lang.String = destDir.getFileSystem().getSeparator();
         val var4: Path = destDir.resolve(StringsKt.replace$default(var10001, var10002, var10003, false, 4, null));
         return var4;
      }

      public fun copyDirRecursivelyToDirInDifferentFileSystem(dir: Path, dest: Path) {
         if (_Assertions.ENABLED && !Files.isDirectory(dir)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && !Files.isDirectory(dest)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && dir.getFileSystem() == dest.getFileSystem()) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && dir.getParent() == null) {
            throw new AssertionError("Assertion failed");
         } else {
            Files.walk(dir).forEach(new Consumer(FileSystemsOperations.Companion::copyDirRecursivelyToDirInDifferentFileSystem$lambda$0) {
               {
                  this.function = function;
               }
            });
         }
      }

      public fun copyDirContentsRecursivelyToDirInDifferentFileSystem(dir: Path, dest: Path) {
         if (_Assertions.ENABLED && !Files.isDirectory(dir)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && !Files.isDirectory(dest)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && dir.getFileSystem() == dest.getFileSystem()) {
            throw new AssertionError("Assertion failed");
         } else {
            Files.walk(dir).forEach(new Consumer(FileSystemsOperations.Companion::copyDirContentsRecursivelyToDirInDifferentFileSystem$lambda$1) {
               {
                  this.function = function;
               }
            });
         }
      }

      public fun copyFilesToDirInDifferentFileSystem(files: List<Path>, dest: Path) {
         if (_Assertions.ENABLED && !Files.isDirectory(dest)) {
            throw new AssertionError("Assertion failed");
         } else {
            val var11: java.lang.Iterable;
            for (Object element$iv : var11) {
               val it: Path = `element$iv` as Path;
               if (_Assertions.ENABLED && !Files.isRegularFile(`element$iv` as Path)) {
                  throw new AssertionError("Assertion failed");
               }

               if (_Assertions.ENABLED && it.getParent() == null) {
                  throw new AssertionError("Assertion failed");
               }

               if (_Assertions.ENABLED && !Files.isDirectory(it.getParent())) {
                  throw new AssertionError("Assertion failed");
               }

               if (_Assertions.ENABLED && it.getFileSystem() == dest.getFileSystem()) {
                  throw new AssertionError("Assertion failed");
               }
            }

            for (Object element$iv : var11) {
               val itx: Path = var16 as Path;
               val var10000: FileSystemsOperations.Companion = FileSystemsOperations.Companion;
               val var10002: Path = itx.getParent();
               var10000.copyPath(itx, var10002, dest);
            }
         }
      }

      public fun copyDirContentsRecursivelyToDirInSameFileSystem(dir: Path, dest: Path) {
         if (_Assertions.ENABLED && !Files.isDirectory(dir)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && !Files.isDirectory(dest)) {
            throw new AssertionError("Assertion failed");
         } else if (_Assertions.ENABLED && !(dir.getFileSystem() == dest.getFileSystem())) {
            throw new AssertionError("Assertion failed");
         } else {
            Files.walk(dir).forEach(new Consumer(FileSystemsOperations.Companion::copyDirContentsRecursivelyToDirInSameFileSystem$lambda$4) {
               {
                  this.function = function;
               }
            });
         }
      }

      @JvmStatic
      fun `copyDirRecursivelyToDirInDifferentFileSystem$lambda$0`(`$dir`: Path, `$dest`: Path, it: Path): Unit {
         val var10000: FileSystemsOperations.Companion = FileSystemsOperations.Companion;
         val var10002: Path = `$dir`.getParent();
         var10000.copyPath(it, var10002, `$dest`);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `copyDirContentsRecursivelyToDirInDifferentFileSystem$lambda$1`(`$dir`: Path, `$dest`: Path, it: Path): Unit {
         val var10000: FileSystemsOperations.Companion = FileSystemsOperations.Companion;
         var10000.copyPath(it, `$dir`, `$dest`);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `copyDirContentsRecursivelyToDirInSameFileSystem$lambda$4`(`$dir`: Path, `$dest`: Path, it: Path): Unit {
         val var10000: FileSystemsOperations.Companion = FileSystemsOperations.Companion;
         var10000.copyPath(it, `$dir`, `$dest`);
         return Unit.INSTANCE;
      }
   }
}
