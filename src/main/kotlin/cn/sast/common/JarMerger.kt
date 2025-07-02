package cn.sast.common

import com.google.common.collect.ImmutableSortedMap
import com.google.common.collect.ImmutableSortedMap.Builder
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.util.Map.Entry
import java.util.function.Predicate
import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.jar.Attributes.Name
import kotlin.jvm.internal.SourceDebugExtension
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.jar.JarArchiveEntry
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipMethod

@SourceDebugExtension(["SMAP\nJarMerger.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JarMerger.kt\ncn/sast/common/JarMerger\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,200:1\n1#2:201\n216#3,2:202\n*S KotlinDebug\n*F\n+ 1 JarMerger.kt\ncn/sast/common/JarMerger\n*L\n148#1:202,2\n*E\n"])
public class JarMerger @JvmOverloads @Throws(java/io/IOException::class)
 public constructor(jarFile: Path, filter: Predicate<String>? = null) : Closeable {
   private final val filter: Predicate<String>?
   private final val buffer: ByteArray
   private final val jarOutputStream: JarArchiveOutputStream

   init {
      this.filter = filter;
      this.buffer = new byte[8192];
      Files.createDirectories(jarFile.getParent());
      this.jarOutputStream = new JarArchiveOutputStream(new BufferedOutputStream(Files.newOutputStream(jarFile)));
   }

   public fun toSystemIndependentPath(path: Path): String {
      val filePath: java.lang.String = path.toString();
      val var10000: java.lang.String;
      if (!(path.getFileSystem().getSeparator() == "/")) {
         val var10001: java.lang.String = path.getFileSystem().getSeparator();
         var10000 = StringsKt.replace$default(filePath, var10001, "/", false, 4, null);
      } else {
         var10000 = filePath;
      }

      return var10000;
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   public fun addDirectory(
      directory: Path,
      filterOverride: Predicate<String>? = this.filter,
      transformer: cn.sast.common.JarMerger.Transformer? = null,
      relocator: cn.sast.common.JarMerger.Relocator? = null
   ) {
      label34: {
         val candidateFiles: Builder = ImmutableSortedMap.naturalOrder();
         Files.walkFileTree(directory, new SimpleFileVisitor<Path>(this, directory, filterOverride, relocator, candidateFiles) {
            {
               this.this$0 = `$receiver`;
               this.$directory = `$directory`;
               this.$filterOverride = `$filterOverride`;
               this.$relocator = `$relocator`;
               this.$candidateFiles = `$candidateFiles`;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
               val var10000: JarMerger = this.this$0;
               val var10001: Path = this.$directory.relativize(file);
               var entryPath: java.lang.String = var10000.toSystemIndependentPath(var10001);
               if (this.$filterOverride != null && !this.$filterOverride.test(entryPath)) {
                  return FileVisitResult.CONTINUE;
               } else {
                  if (this.$relocator != null) {
                     entryPath = this.$relocator.relocate(entryPath);
                  }

                  this.$candidateFiles.put(entryPath, file);
                  return FileVisitResult.CONTINUE;
               }
            }
         });
         val sortedFiles: ImmutableSortedMap = candidateFiles.build();

         for (Entry var8 : ((java.util.Map)sortedFiles).entrySet()) {
            val entryPath: java.lang.String = var8.getKey() as java.lang.String;
            val var11: Closeable = new BufferedInputStream(Files.newInputStream(var8.getValue() as Path));
            var var12: java.lang.Throwable = null;

            try {
               try {
                  val `is`: BufferedInputStream = var11 as BufferedInputStream;
                  if (transformer != null) {
                     val is2: InputStream = transformer.filter(entryPath, `is`);
                     if (is2 != null) {
                        this.write(new JarArchiveEntry(entryPath), is2);
                     }
                  } else {
                     this.write(new JarArchiveEntry(entryPath), `is`);
                  }
               } catch (var16: java.lang.Throwable) {
                  var12 = var16;
                  throw var16;
               }
            } catch (var17: java.lang.Throwable) {
               CloseableKt.closeFinally(var11, var12);
            }

            CloseableKt.closeFinally(var11, null);
         }
      }
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   public fun addJar(file: Path, filterOverride: Predicate<String>? = this.filter, relocator: cn.sast.common.JarMerger.Relocator? = null) {
      label46: {
         val var4: Closeable = (new JarArchiveInputStream(Files.newInputStream(file))) as Closeable;
         var var5: java.lang.Throwable = null;

         try {
            try {
               val zis: JarArchiveInputStream = var4 as JarArchiveInputStream;

               while (true) {
                  val var10000: ZipArchiveEntry = zis.getNextZipEntry();
                  if (var10000 == null) {
                     break;
                  }

                  if (!var10000.isDirectory()) {
                     var name: java.lang.String = var10000.getName();
                     if (filterOverride == null || filterOverride.test(name)) {
                        if (relocator != null) {
                           name = relocator.relocate(name);
                        }

                        val newEntry: JarArchiveEntry = new JarArchiveEntry(name);
                        newEntry.setMethod(var10000.getMethod());
                        if (newEntry.getMethod() == ZipMethod.STORED.getCode()) {
                           newEntry.setSize(var10000.getSize());
                           newEntry.setCompressedSize(var10000.getCompressedSize());
                           newEntry.setCrc(var10000.getCrc());
                        }

                        newEntry.setLastModifiedTime(FileTime.fromMillis(0L));
                        this.write(newEntry, zis as InputStream);
                     }
                  }
               }
            } catch (var11: java.lang.Throwable) {
               var5 = var11;
               throw var11;
            }
         } catch (var12: java.lang.Throwable) {
            CloseableKt.closeFinally(var4, var5);
         }

         CloseableKt.closeFinally(var4, null);
      }
   }

   @Throws(java/io/IOException::class)
   public fun addFile(entryPath: String, file: Path) {
      label19: {
         val var3: Closeable = new BufferedInputStream(Files.newInputStream(file));
         var var4: java.lang.Throwable = null;

         try {
            try {
               this.write(new JarArchiveEntry(entryPath), var3 as BufferedInputStream);
            } catch (var7: java.lang.Throwable) {
               var4 = var7;
               throw var7;
            }
         } catch (var8: java.lang.Throwable) {
            CloseableKt.closeFinally(var3, var4);
         }

         CloseableKt.closeFinally(var3, null);
      }
   }

   @Throws(java/io/IOException::class)
   public fun addEntry(entryPath: String, input: InputStream) {
      label19: {
         val var3: Closeable = new BufferedInputStream(input);
         var var4: java.lang.Throwable = null;

         try {
            try {
               this.write(new JarArchiveEntry(entryPath), var3 as BufferedInputStream);
            } catch (var7: java.lang.Throwable) {
               var4 = var7;
               throw var7;
            }
         } catch (var8: java.lang.Throwable) {
            CloseableKt.closeFinally(var3, var4);
         }

         CloseableKt.closeFinally(var3, null);
      }
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.jarOutputStream.close();
   }

   @Throws(java/io/IOException::class)
   public fun setManifestProperties(properties: Map<String, String>) {
      label24: {
         val manifest: Manifest = new Manifest();
         val global: Attributes = manifest.getMainAttributes();
         global.put(Name.MANIFEST_VERSION, "1.0.0");

         for (Entry element$iv : properties.entrySet()) {
            global.put(new Name(`element$iv`.getKey() as java.lang.String), `element$iv`.getValue() as java.lang.String);
         }

         val manifestEntry: JarArchiveEntry = new JarArchiveEntry("META-INF/MANIFEST.MF");
         this.setEntryAttributes(manifestEntry);
         this.jarOutputStream.putArchiveEntry(manifestEntry as ArchiveEntry);

         try {
            manifest.write(this.jarOutputStream as OutputStream);
         } catch (var12: java.lang.Throwable) {
            this.jarOutputStream.closeArchiveEntry();
         }

         this.jarOutputStream.closeArchiveEntry();
      }
   }

   @Throws(java/io/IOException::class)
   private fun write(entry: JarArchiveEntry, from: InputStream) {
      this.setEntryAttributes(entry);
      this.jarOutputStream.putArchiveEntry(entry as ArchiveEntry);

      while (true) {
         val count: Int = from.read(this.buffer);
         if (count == -1) {
            this.jarOutputStream.closeArchiveEntry();
            return;
         }

         this.jarOutputStream.write(this.buffer, 0, count);
      }
   }

   private fun setEntryAttributes(entry: JarArchiveEntry) {
      entry.setLastModifiedTime(fileTime);
      entry.setLastAccessTime(fileTime);
      entry.setCreationTime(fileTime);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun JarMerger(jarFile: Path) {
      this(jarFile, null, 2, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun addDirectory(directory: Path, filterOverride: Predicate<java.lang.String>?, transformer: JarMerger.Transformer?) {
      addDirectory$default(this, directory, filterOverride, transformer, null, 8, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun addDirectory(directory: Path, filterOverride: Predicate<java.lang.String>?) {
      addDirectory$default(this, directory, filterOverride, null, null, 12, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun addDirectory(directory: Path) {
      addDirectory$default(this, directory, null, null, null, 14, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun addJar(file: Path, filterOverride: Predicate<java.lang.String>?) {
      addJar$default(this, file, filterOverride, null, 4, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun addJar(file: Path) {
      addJar$default(this, file, null, null, 6, null);
   }

   @JvmStatic
   fun {
      val var10000: FileTime = FileTime.fromMillis(0L);
      fileTime = var10000;
   }

   public companion object {
      public final val fileTime: FileTime
   }

   public interface Relocator {
      public abstract fun relocate(entryPath: String): String {
      }
   }

   public interface Transformer {
      public abstract fun filter(entryPath: String, input: InputStream): InputStream? {
      }
   }
}
