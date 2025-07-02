package cn.sast.common

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.ProviderNotFoundException
import java.nio.file.spi.FileSystemProvider
import java.time.Duration
import java.util.ArrayList
import java.util.Enumeration
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.PathsKt
import kotlin.jdk7.AutoCloseableKt
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import mu.KotlinLogging
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.io.FileUtils
import org.utbot.common.FileUtilKt
import soot.util.SharedCloseable

@SourceDebugExtension(["SMAP\nResourceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/Resource\n+ 2 Resource.kt\ncn/sast/common/ResourceKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,730:1\n36#2:731\n36#2:734\n36#2:736\n1#3:732\n1#3:733\n1#3:735\n1#3:737\n1#3:747\n1368#4:738\n1454#4,5:739\n1628#4,3:744\n*S KotlinDebug\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/Resource\n*L\n152#1:731\n197#1:734\n212#1:736\n152#1:732\n197#1:735\n212#1:737\n301#1:738\n301#1:739,5\n302#1:744,3\n*E\n"])
public object Resource {
   public const val EXTRACT_DIR_NAME: String = "archive-extract"

   public final var zipExtractOutputDir: Path = ResourceImplKt.getSAstTempDirectory()
      internal set

   public final var newFileSystemEnv: Map<String, Any> = MapsKt.mapOf(new Pair[]{TuplesKt.to("create", "true"), TuplesKt.to("zipinfo-time", "false")})
      internal set

   public final var fileSystemEncodings: List<String> = CollectionsKt.listOf(new java.lang.String[]{"UTF-8", "GBK"})
      internal set

   private final val logger: KLogger = KotlinLogging.INSTANCE.logger(Resource::logger$lambda$0)
   private const val PATH_CACHE_CAPACITY: Int = 50000
   public final val archivePathToZip: SharedZipFileCacheWrapper = new SharedZipFileCacheWrapper(5, 50000)
   private final val archivePathToEntriesCache: LoadingCache<Path, Set<String>>
   public final val uriToPathCached: LoadingCache<Pair<URI, Boolean>, Path>

   private final val entry: String?
      private final get() {
         val it: java.lang.String = `$this$entry`.getSchemeSpecificPart();
         val index: Int = StringsKt.lastIndexOf$default(it, "!/", 0, false, 6, null);
         val var10000: java.lang.String;
         if (index == -1) {
            var10000 = null;
         } else {
            var10000 = it.substring(index + 2, it.length());
         }

         return var10000;
      }


   private final var zipFsProvider: FileSystemProvider?
   private final val archiveSystemCache: LoadingCache<Path, FileSystem> = Caffeine.newBuilder().weakValues().build(<unrepresentable>.INSTANCE)
   private final val jarCacheBuilder: Caffeine<Any, Any> = Caffeine.newBuilder().initialCapacity(4)

   @Throws(java/lang/Exception::class)
   public fun getEntriesUnsafe(path: Path): Set<String> {
      val var10000: LoadingCache = archivePathToEntriesCache;
      val var10001: Path = path.toAbsolutePath();
      val var2: Any = var10000.get(var10001.normalize());
      return var2 as MutableSet<java.lang.String>;
   }

   public fun of(uri: URI): IResource {
      val var10000: IResource;
      if (uri.getScheme() == "file") {
         val var10002: Path = Paths.get(uri);
         var10000 = new Resource.ResourceBasic(var10002);
      } else {
         val system: java.lang.String = uri.getScheme();
         val var10: Resource.ResourceBasic;
         if (system == "jar" || StringsKt.equals(system, "zip", true)) {
            val spec: java.lang.String = uri.getSchemeSpecificPart();
            val var7: Int = StringsKt.lastIndexOf$default(spec, "!/", 0, false, 6, null);
            if (var7 == -1) {
               throw new IllegalArgumentException("URI: $uri does not contain path info ex. jar:file:/c:/foo.zip!/BAR");
            }

            var var10001: java.lang.String = spec.substring(0, var7);
            val var11: Path = Paths.get(ResourceKt.uriOf(var10001));
            val var8: FileSystem = this.getZipFileSystem(var11);
            var10001 = spec.substring(var7 + 1);
            val var9: Path = var8.getPath(var10001);
            var10 = new Resource.ResourceBasic(var9);
         } else {
            try {
               FileSystems.getFileSystem(uri);
            } catch (var6: FileSystemNotFoundException) {
               ResourceImplKt.createFileSystem(uri);
            }

            val var13: Path = Paths.get(uri);
            var10 = new Resource.ResourceBasic(var13);
         }

         var10000 = var10;
      }

      return var10000;
   }

   public fun of(url: URL): IResource {
      val var10001: URI = url.toURI();
      return this.of(var10001);
   }

   public fun of(path: Path): IResource {
      return new Resource.ResourceBasic(path);
   }

   private fun URI.extractJarName(): URI? {
      val it: java.lang.String = `$this$extractJarName`.getSchemeSpecificPart();
      val index: Int = StringsKt.lastIndexOf$default(it, "!/", 0, false, 6, null);
      val var10000: URI;
      if (index == -1) {
         var10000 = null;
      } else {
         val var5: java.lang.String = it.substring(0, index);
         var10000 = ResourceKt.uriOf(var5);
      }

      return var10000;
   }

   public fun locateClass(clazz: Class<*>): URL {
      val path: java.lang.String = FileUtilKt.toClassFilePath(clazz);
      val var10000: URL = clazz.getClassLoader().getResource(path);
      if (var10000 == null) {
         throw new IllegalArgumentException(("No such file: $path").toString());
      } else {
         return var10000;
      }
   }

   public fun locateAllClass(clazz: Class<*>): IResource {
      val path: java.lang.String = FileUtilKt.toClassFilePath(clazz);
      val resource: URL = this.locateClass(clazz);
      var var10000: URI = resource.toURI();
      val `it$iv`: java.lang.String = var10000.getScheme();
      val var11: IResource;
      if (`it$iv` == "jar" || StringsKt.equals(`it$iv`, "zip", true)) {
         val var10001: URI = resource.toURI();
         var10000 = this.extractJarName(var10001);
         if (var10000 == null) {
            throw new IllegalStateException(("internal error: resource=$resource").toString());
         }

         var11 = this.of(var10000);
      } else {
         var11 = this.of(
            StringsKt.removeSuffix(
               StringsKt.replace$default(
                  StringsKt.replace$default(this.of(resource).getAbsolute().getNormalize().toString(), "\\", "/", false, 4, null), "//", "/", false, 4, null
               ),
               path
            )
         );
      }

      return var11;
   }

   private fun Path.extract(scheme: String): Path {
      if ((if (scheme == "zip") "jar" else scheme) == "jar") {
         val var10000: URI = `$this$extract`.toUri();
         val `it$iv`: java.lang.String = var10000.getScheme();
         if (`it$iv` == "jar" || StringsKt.equals(`it$iv`, "zip", true)) {
            return this.extract(this.of(`$this$extract`).expandRes(this.dirOf(zipExtractOutputDir)).getPath(), scheme);
         }
      }

      return `$this$extract`;
   }

   private fun getZipFsProvider(): FileSystemProvider {
      var result: FileSystemProvider = zipFsProvider;
      if (zipFsProvider == null) {
         result = this.findZipFsProvider();
         zipFsProvider = result;
      }

      return result;
   }

   private fun findZipFsProvider(): FileSystemProvider {
      for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
         try {
            if (provider.getScheme() == "jar") {
               return provider;
            }
         } catch (var4: UnsupportedOperationException) {
         }
      }

      throw new ProviderNotFoundException("Provider not found");
   }

   public fun getZipFileSystem(path: Path): FileSystem {
      val var10000: LoadingCache = archiveSystemCache;
      val var10001: Path = path.toAbsolutePath();
      val var2: Any = var10000.get(var10001.normalize());
      return var2 as FileSystem;
   }

   public fun archivePath(archive: Path, entry: String): Path {
      val index: Int = StringsKt.indexOf$default(entry, "!", 0, false, 6, null);
      val var7: Path;
      if (index != -1) {
         val first: Path = this.archivePath(archive, StringsKt.substringBefore$default(entry, "!", null, 2, null));
         val var10000: java.lang.String = entry.substring(index + 1);
         var7 = this.archivePath(first, var10000);
      } else {
         val var6: Path = if (StringsKt.startsWith$default(entry, "/", false, 2, null))
            this.getZipFileSystem(archive).getPath(entry)
            else
            this.getZipFileSystem(archive).getPath("/$entry");
         var7 = var6;
      }

      return var7;
   }

   public fun of(path: String): IResource {
      val index: Int = StringsKt.indexOf$default(path, "!", 0, false, 6, null);
      val var10000: Path;
      if (index == -1) {
         var10000 = Paths.get(path);
      } else {
         val var6: java.lang.String = path.substring(0, index);
         val var7: java.lang.String = path.substring(index + 1);
         val var10001: Path = Paths.get(var6);
         var10000 = this.archivePath(var10001, var7);
      }

      return new Resource.ResourceBasic(var10000);
   }

   public fun dirOf(directory: String): IResDirectory {
      return this.of(directory).toDirectory();
   }

   public fun dirOf(directory: Path): IResDirectory {
      return this.of(directory).toDirectory();
   }

   public fun fileOf(file: String): IResFile {
      return this.of(file).toFile();
   }

   public fun fileOf(file: Path): IResFile {
      return this.of(file).toFile();
   }

   public fun multipleOf(paths: List<String>): Set<IResource> {
      var `$this$mapTo$iv`: java.lang.Iterable = paths;
      val `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val `list$iv$iv`: java.lang.String = var9 as java.lang.String;
         val var10000: java.lang.CharSequence = var9 as java.lang.String;
         val var10001: java.lang.String = File.pathSeparator;
         CollectionsKt.addAll(
            `destination$iv$iv`,
            if (StringsKt.contains$default(var10000, var10001, false, 2, null))
               StringsKt.split$default(`list$iv$iv`, new java.lang.String[]{File.pathSeparator}, false, 0, 6, null)
               else
               CollectionsKt.listOf(`list$iv$iv`)
         );
      }

      `$this$mapTo$iv` = `destination$iv$iv` as java.util.List;
      val var15: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$flatMap$iv) {
         var15.add(INSTANCE.of(var17 as java.lang.String));
      }

      return var15 as MutableSet<IResource>;
   }

   public fun getOriginFileFromExpandPath(p: IResource): IResource {
      return this.getOriginFileFromExpandAbsPath(p.getAbsolute().getNormalize());
   }

   public fun getOriginFileFromExpandAbsPath(absolute: IResource): IResource {
      var var10000: IResource = INSTANCE.getOriginFileFromExpandAbsolutePath(absolute);
      if (var10000 == null) {
         var10000 = absolute;
      }

      return var10000;
   }

   private fun isUnderExtractDir(p: IResource): Boolean {
      return p.isFileScheme() && StringsKt.contains$default(p.toString(), "archive-extract", false, 2, null);
   }

   private fun getOriginFileFromExpandAbsolutePath(abs: IResource): IResource? {
      if (this.isUnderExtractDir(abs)) {
         val var4: Path = this.findFileMappingInZip(abs);
         return if (var4 != null) this.of(var4) else null;
      } else {
         val entry: java.lang.String = abs.getZipEntry();
         if (entry != null) {
            val schemePath: IResource = this.of(abs.getSchemePath());
            if (this.isUnderExtractDir(schemePath)) {
               val var10000: IResource = this.getOriginFileFromExpandAbsolutePath(schemePath);
               return if (var10000 != null) var10000.resolve(entry) else null;
            }
         }

         return null;
      }
   }

   private fun findFileMappingInZip(nFile: IResource): Path? {
      if (!nFile.isFileScheme()) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         val p: Path = nFile.getPath();

         for (Entry var4 : Resource.ExpandResKey.Companion.getMappingUnzipDirToZip$corax_api().entrySet()) {
            val uncompress: IResDirectory = var4.getKey() as IResDirectory;
            val fromZip: IResource = var4.getValue() as IResource;
            if (p.startsWith(uncompress.getPath())) {
               try {
                  return fromZip.resolve(p.subpath(uncompress.getPath().getNameCount(), p.getNameCount()).toString()).getPath();
               } catch (var8: Exception) {
                  logger.warn(Resource::findFileMappingInZip$lambda$8);
               }
            }
         }

         return null;
      }
   }

   public fun clean() {
      Resource.ResourceBasic.Companion.getJarInnerResourceCache$corax_api().cleanUp();
      Resource.ExpandResKey.Companion.getJarTempCache$corax_api().cleanUp();
      Resource.ExpandResKey.Companion.getMappingUnzipDirToZip$corax_api().clear();
      archiveSystemCache.cleanUp();
   }

   @Throws(java/io/IOException::class)
   public fun extractZipToFolder(archiveFile: Path, entryPrefix: String, destFolder: Path): Path {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot read field "bytecode" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInstance()" is null
      //   at org.vineflower.kotlin.pass.KMergePass.matchForEach(KMergePass.java:165)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoop(KMergePass.java:52)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:39)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
      //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
      //   at org.vineflower.kotlin.pass.KMergePass.run(KMergePass.java:23)
      //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
      //   at org.jetbrains.java.decompiler.api.plugin.pass.LoopingPassBuilder$CompiledPass.run(LoopingPassBuilder.java:43)
      //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
      //   at org.jetbrains.java.decompiler.api.plugin.pass.LoopingPassBuilder$CompiledPass.run(LoopingPassBuilder.java:43)
      //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
      //   at org.jetbrains.java.decompiler.api.plugin.pass.MainPassBuilder$CompiledPass.run(MainPassBuilder.java:34)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:160)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc_w "archiveFile"
      // 004: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 007: aload 2
      // 008: ldc_w "entryPrefix"
      // 00b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 00e: aload 3
      // 00f: ldc_w "destFolder"
      // 012: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 015: aload 2
      // 016: ldc_w "/"
      // 019: checkcast java/lang/CharSequence
      // 01c: invokestatic kotlin/text/StringsKt.removePrefix (Ljava/lang/String;Ljava/lang/CharSequence;)Ljava/lang/String;
      // 01f: astore 4
      // 021: bipush 0
      // 022: istore 5
      // 024: getstatic cn/sast/common/Resource.archivePathToZip Lcn/sast/common/SharedZipFileCacheWrapper;
      // 027: aload 1
      // 028: invokevirtual cn/sast/common/SharedZipFileCacheWrapper.getRef (Ljava/nio/file/Path;)Lsoot/util/SharedCloseable;
      // 02b: checkcast java/lang/AutoCloseable
      // 02e: astore 6
      // 030: aconst_null
      // 031: astore 7
      // 033: nop
      // 034: aload 6
      // 036: checkcast soot/util/SharedCloseable
      // 039: astore 8
      // 03b: bipush 0
      // 03c: istore 9
      // 03e: aload 8
      // 040: invokevirtual soot/util/SharedCloseable.get ()Ljava/io/Closeable;
      // 043: checkcast org/apache/commons/compress/archivers/zip/ZipFile
      // 046: astore 10
      // 048: aload 10
      // 04a: invokevirtual org/apache/commons/compress/archivers/zip/ZipFile.getEntries ()Ljava/util/Enumeration;
      // 04d: astore 11
      // 04f: aload 11
      // 051: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 054: aload 11
      // 056: invokestatic kotlin/collections/CollectionsKt.iterator (Ljava/util/Enumeration;)Ljava/util/Iterator;
      // 059: astore 12
      // 05b: aload 12
      // 05d: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 062: ifeq 1ae
      // 065: aload 12
      // 067: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 06c: checkcast org/apache/commons/compress/archivers/zip/ZipArchiveEntry
      // 06f: astore 13
      // 071: aload 13
      // 073: invokevirtual org/apache/commons/compress/archivers/zip/ZipArchiveEntry.getName ()Ljava/lang/String;
      // 076: dup
      // 077: ldc_w "getName(...)"
      // 07a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 07d: ldc_w "/"
      // 080: checkcast java/lang/CharSequence
      // 083: invokestatic kotlin/text/StringsKt.removePrefix (Ljava/lang/String;Ljava/lang/CharSequence;)Ljava/lang/String;
      // 086: astore 14
      // 088: aload 14
      // 08a: aload 4
      // 08c: bipush 0
      // 08d: bipush 2
      // 08e: aconst_null
      // 08f: invokestatic kotlin/text/StringsKt.startsWith$default (Ljava/lang/String;Ljava/lang/String;ZILjava/lang/Object;)Z
      // 092: ifne 098
      // 095: goto 05b
      // 098: aload 14
      // 09a: checkcast java/lang/CharSequence
      // 09d: ldc_w ".."
      // 0a0: bipush 0
      // 0a1: bipush 0
      // 0a2: bipush 6
      // 0a4: aconst_null
      // 0a5: invokestatic kotlin/text/StringsKt.indexOf$default (Ljava/lang/CharSequence;Ljava/lang/String;IZILjava/lang/Object;)I
      // 0a8: bipush -1
      // 0a9: if_icmpeq 0bf
      // 0ac: getstatic cn/sast/common/Resource.logger Lmu/KLogger;
      // 0af: aload 1
      // 0b0: aload 14
      // 0b2: invokedynamic invoke (Ljava/nio/file/Path;Ljava/lang/String;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/common/Resource.extractZipToFolder$lambda$14$lambda$9 (Ljava/nio/file/Path;Ljava/lang/String;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 0b7: invokeinterface mu/KLogger.warn (Lkotlin/jvm/functions/Function0;)V 2
      // 0bc: goto 05b
      // 0bf: nop
      // 0c0: aload 3
      // 0c1: aload 14
      // 0c3: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 0c8: astore 15
      // 0ca: goto 0f7
      // 0cd: astore 16
      // 0cf: iload 5
      // 0d1: istore 17
      // 0d3: iload 17
      // 0d5: bipush 1
      // 0d6: iadd
      // 0d7: istore 5
      // 0d9: iload 17
      // 0db: bipush 3
      // 0dc: if_icmpge 0f4
      // 0df: getstatic cn/sast/common/Resource.logger Lmu/KLogger;
      // 0e2: aload 16
      // 0e4: checkcast java/lang/Throwable
      // 0e7: aload 14
      // 0e9: aload 3
      // 0ea: invokedynamic invoke (Ljava/lang/String;Ljava/nio/file/Path;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/common/Resource.extractZipToFolder$lambda$14$lambda$10 (Ljava/lang/String;Ljava/nio/file/Path;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 0ef: invokeinterface mu/KLogger.error (Ljava/lang/Throwable;Lkotlin/jvm/functions/Function0;)V 3
      // 0f4: goto 05b
      // 0f7: aload 15
      // 0f9: astore 18
      // 0fb: aload 13
      // 0fd: invokevirtual org/apache/commons/compress/archivers/zip/ZipArchiveEntry.isDirectory ()Z
      // 100: ifeq 110
      // 103: aload 18
      // 105: bipush 0
      // 106: anewarray 703
      // 109: invokestatic java/nio/file/Files.createDirectories (Ljava/nio/file/Path;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;
      // 10c: pop
      // 10d: goto 05b
      // 110: aload 18
      // 112: invokeinterface java/nio/file/Path.getParent ()Ljava/nio/file/Path; 1
      // 117: dup
      // 118: ifnull 142
      // 11b: astore 17
      // 11d: bipush 0
      // 11e: istore 19
      // 120: aload 17
      // 122: bipush 0
      // 123: anewarray 714
      // 126: dup
      // 127: arraylength
      // 128: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 12b: checkcast [Ljava/nio/file/LinkOption;
      // 12e: invokestatic java/nio/file/Files.exists (Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z
      // 131: ifne 13e
      // 134: aload 17
      // 136: bipush 0
      // 137: anewarray 703
      // 13a: invokestatic java/nio/file/Files.createDirectories (Ljava/nio/file/Path;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;
      // 13d: pop
      // 13e: nop
      // 13f: goto 144
      // 142: pop
      // 143: nop
      // 144: aload 10
      // 146: aload 13
      // 148: invokevirtual org/apache/commons/compress/archivers/zip/ZipFile.getInputStream (Lorg/apache/commons/compress/archivers/zip/ZipArchiveEntry;)Ljava/io/InputStream;
      // 14b: checkcast java/io/Closeable
      // 14e: astore 15
      // 150: aconst_null
      // 151: astore 16
      // 153: nop
      // 154: aload 15
      // 156: checkcast java/io/InputStream
      // 159: astore 17
      // 15b: bipush 0
      // 15c: istore 19
      // 15e: aload 18
      // 160: aload 17
      // 162: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 165: aload 17
      // 167: invokestatic kotlin/io/ByteStreamsKt.readBytes (Ljava/io/InputStream;)[B
      // 16a: bipush 0
      // 16b: anewarray 742
      // 16e: invokestatic java/nio/file/Files.write (Ljava/nio/file/Path;[B[Ljava/nio/file/OpenOption;)Ljava/nio/file/Path;
      // 171: pop
      // 172: getstatic cn/sast/common/OS.INSTANCE Lcn/sast/common/OS;
      // 175: invokevirtual cn/sast/common/OS.getPosixFilePermissions ()Ljava/util/Set;
      // 178: dup
      // 179: ifnull 18b
      // 17c: astore 20
      // 17e: bipush 0
      // 17f: istore 21
      // 181: aload 18
      // 183: aload 20
      // 185: invokestatic java/nio/file/Files.setPosixFilePermissions (Ljava/nio/file/Path;Ljava/util/Set;)Ljava/nio/file/Path;
      // 188: goto 18d
      // 18b: pop
      // 18c: aconst_null
      // 18d: astore 17
      // 18f: aload 15
      // 191: aload 16
      // 193: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 196: goto 05b
      // 199: astore 17
      // 19b: aload 17
      // 19d: astore 16
      // 19f: aload 17
      // 1a1: athrow
      // 1a2: astore 17
      // 1a4: aload 15
      // 1a6: aload 16
      // 1a8: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 1ab: aload 17
      // 1ad: athrow
      // 1ae: nop
      // 1af: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 1b2: astore 8
      // 1b4: aload 6
      // 1b6: aload 7
      // 1b8: invokestatic kotlin/jdk7/AutoCloseableKt.closeFinally (Ljava/lang/AutoCloseable;Ljava/lang/Throwable;)V
      // 1bb: goto 1d3
      // 1be: astore 8
      // 1c0: aload 8
      // 1c2: astore 7
      // 1c4: aload 8
      // 1c6: athrow
      // 1c7: astore 8
      // 1c9: aload 6
      // 1cb: aload 7
      // 1cd: invokestatic kotlin/jdk7/AutoCloseableKt.closeFinally (Ljava/lang/AutoCloseable;Ljava/lang/Throwable;)V
      // 1d0: aload 8
      // 1d2: athrow
      // 1d3: aload 3
      // 1d4: aload 2
      // 1d5: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 1da: dup
      // 1db: ldc_w "resolve(...)"
      // 1de: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 1e1: areturn
   }

   @JvmStatic
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `uriToPathCached$lambda$1`(args: Pair): Path {
      return ResourceKt.uriToPath(args.component1() as URI, args.component2() as java.lang.Boolean);
   }

   @JvmStatic
   fun `findFileMappingInZip$lambda$8`(`$nFile`: IResource, `$e`: Exception): Any {
      return "failed to get findFileMappingInZip of $`$nFile`. e: ${`$e`.getMessage()}";
   }

   @JvmStatic
   fun `extractZipToFolder$lambda$14$lambda$9`(`$archiveFile`: Path, `$entryName`: java.lang.String): Any {
      return "skipping bad zip entry: `$`$archiveFile`!/$`$entryName``. (zip slip)";
   }

   @JvmStatic
   fun `extractZipToFolder$lambda$14$lambda$10`(`$entryName`: java.lang.String, `$destFolder`: Path): Any {
      return "Illegal entry name [$`$entryName`] in $`$destFolder`";
   }

   @JvmStatic
   fun {
      var var10000: LoadingCache = Caffeine.newBuilder()
         .expireAfterAccess(Duration.ofSeconds(12L))
         .build(new CacheLoader<Path, java.util.Set<? extends java.lang.String>>() {
            public java.util.Set<java.lang.String> load(Path archivePath) throws Exception {
               label36: {
                  val var2: AutoCloseable = Resource.INSTANCE.getArchivePathToZip().getRef(archivePath) as AutoCloseable;
                  var var3: java.lang.Throwable = null;

                  try {
                     try {
                        val archive: SharedCloseable = var2 as SharedCloseable;
                        val ret: java.util.Set = new HashSet();
                        val var10000: Enumeration = (archive.get() as ZipFile).getEntries();
                        val it: Enumeration = var10000;

                        while (it.hasMoreElements()) {
                           val var14: ZipArchiveEntry = it.nextElement() as ZipArchiveEntry;
                           if (var14 != null) {
                              val var15: java.lang.String = var14.getName();
                              if (var15 != null && !StringsKt.contains$default(var15, "..", false, 2, null)) {
                                 ret.add(var15);
                              }
                           }
                        }
                     } catch (var10: java.lang.Throwable) {
                        var3 = var10;
                        throw var10;
                     }
                  } catch (var11: java.lang.Throwable) {
                     AutoCloseableKt.closeFinally(var2, var3);
                  }

                  AutoCloseableKt.closeFinally(var2, null);
               }
            }
         });
      archivePathToEntriesCache = var10000;
      var10000 = Caffeine.newBuilder().initialCapacity(5).maximumSize(5000L).softValues().build(new CacheLoader(Resource::uriToPathCached$lambda$1) {
         {
            this.function = function;
         }
      });
      uriToPathCached = var10000;
   }

   internal data class ExpandResKey(jarPath: Path, entry: String, outPut: IResDirectory) {
      public final val jarPath: Path
      public final val entry: String
      public final val outPut: IResDirectory

      init {
         this.jarPath = jarPath;
         this.entry = entry;
         this.outPut = outPut;
      }

      internal fun getTemplate(): IResDirectory {
         val var10000: Any = jarTempCache.get(this);
         return var10000 as IResDirectory;
      }

      public operator fun component1(): Path {
         return this.jarPath;
      }

      public operator fun component2(): String {
         return this.entry;
      }

      public operator fun component3(): IResDirectory {
         return this.outPut;
      }

      public fun copy(jarPath: Path = this.jarPath, entry: String = this.entry, outPut: IResDirectory = this.outPut): cn.sast.common.Resource.ExpandResKey {
         return new Resource.ExpandResKey(jarPath, entry, outPut);
      }

      public override fun toString(): String {
         return "ExpandResKey(jarPath=${this.jarPath}, entry=${this.entry}, outPut=${this.outPut})";
      }

      public override fun hashCode(): Int {
         return (this.jarPath.hashCode() * 31 + this.entry.hashCode()) * 31 + this.outPut.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is Resource.ExpandResKey) {
            return false;
         } else {
            val var2: Resource.ExpandResKey = other as Resource.ExpandResKey;
            if (!(this.jarPath == (other as Resource.ExpandResKey).jarPath)) {
               return false;
            } else if (!(this.entry == var2.entry)) {
               return false;
            } else {
               return this.outPut == var2.outPut;
            }
         }
      }

      public companion object {
         internal final val mappingUnzipDirToZip: ConcurrentHashMap<IResDirectory, IResource>
         internal final val jarTempCache: LoadingCache<cn.sast.common.Resource.ExpandResKey, IResDirectory>
      }
   }

   @SourceDebugExtension(["SMAP\nResourceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/Resource$ResDirectoryImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,730:1\n1557#2:731\n1628#2,3:732\n*S KotlinDebug\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/Resource$ResDirectoryImpl\n*L\n603#1:731\n603#1:732,3\n*E\n"])
   public class ResDirectoryImpl(path: Path) : Resource.ResourceBasic(path), IResDirectory {
      public open val absolute: IResDirectory
         public open get() {
            return super.getAbsolute().toDirectory();
         }


      public open val normalize: IResDirectory
         public open get() {
            return super.getNormalize().toDirectory();
         }


      public override fun listPathEntries(glob: String): List<IResource> {
         if (this.getExists() && !this.isFile()) {
            val `$this$map$iv`: java.lang.Iterable = PathsKt.listDirectoryEntries(this.getPath(), glob);
            val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add(Resource.INSTANCE.of(`item$iv$iv` as Path));
            }

            return `destination$iv$iv` as MutableList<IResource>;
         } else {
            return CollectionsKt.emptyList();
         }
      }

      public override fun expandRes(outPut: IResDirectory): IResDirectory {
         return super.expandRes(outPut).toDirectory();
      }

      public override fun mkdirs() {
         if (!this.isFileScheme()) {
            throw new IllegalStateException(("mkdirs is not support of $this").toString());
         } else {
            FileUtils.forceMkdir(this.getPath().toFile());
         }
      }

      public override fun toFile(): IResFile {
         return Resource.INSTANCE.fileOf(this.getPath());
      }

      public override fun toDirectory(): IResDirectory {
         return this;
      }
   }

   public class ResFileImpl(path: Path) : Resource.ResourceBasic(path), IResFile {
      public open val absolute: IResFile
         public open get() {
            return super.getAbsolute().toFile();
         }


      public open val normalize: IResFile
         public open get() {
            return super.getNormalize().toFile();
         }


      public open val entries: Set<String>
         public open get() {
            var var1: java.util.Set;
            try {
               var1 = Resource.INSTANCE.getEntriesUnsafe(this.getPath());
            } catch (var3: NoSuchFileException) {
               Resource.access$getLogger$p().warn(Resource.ResFileImpl::_get_entries_$lambda$0);
               var1 = SetsKt.emptySet();
            } catch (var4: Exception) {
               Resource.access$getLogger$p().warn(Resource.ResFileImpl::_get_entries_$lambda$1);
               Resource.access$getLogger$p().debug(var4, Resource.ResFileImpl::_get_entries_$lambda$2);
               var1 = SetsKt.emptySet();
            }

            return var1;
         }


      public open val md5: String
         public open get() {
            return ResourceImplKt.calculate(this, "MD5");
         }


      public override fun toFile(): IResFile {
         return this;
      }

      public override fun toDirectory(): IResDirectory {
         val var10002: Path = this.getPath().getParent();
         return new Resource.ResDirectoryImpl(var10002);
      }

      public override fun mkdirs() {
         val var10000: Resource = Resource.INSTANCE;
         val var10001: Path = this.getPath().getParent();
         var10000.of(var10001).mkdirs();
      }

      public override fun expandRes(outPut: IResDirectory): IResFile {
         return super.expandRes(outPut).toFile();
      }

      @JvmStatic
      fun `_get_entries_$lambda$0`(`this$0`: Resource.ResFileImpl, `$e`: NoSuchFileException): Any {
         return "no such file: ${`this$0`.getPath()}. e: ${`$e`.getMessage()}";
      }

      @JvmStatic
      fun `_get_entries_$lambda$1`(`this$0`: Resource.ResFileImpl, `$e`: Exception): Any {
         return "failed to get entries of ${`this$0`.getPath()}. e: ${`$e`.getMessage()}";
      }

      @JvmStatic
      fun `_get_entries_$lambda$2`(`this$0`: Resource.ResFileImpl): Any {
         return "failed to get entries of ${`this$0`.getPath()}.";
      }
   }

   @SourceDebugExtension(["SMAP\nResourceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/Resource$ResourceBasic\n+ 2 Resource.kt\ncn/sast/common/ResourceKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,730:1\n37#2:731\n38#2:732\n36#2:733\n37#2,2:736\n1#3:734\n1#3:735\n*S KotlinDebug\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/Resource$ResourceBasic\n*L\n426#1:731\n427#1:732\n428#1:733\n515#1:736,2\n428#1:734\n*E\n"])
   public open class ResourceBasic(path: Path) : IResource {
      public final val path: Path

      public open val uri: URI
         public open get() {
            val var10000: Any = this.uri$delegate.getValue();
            return var10000 as URI;
         }


      public open val url: URL
         public open get() {
            val var10000: Any = this.url$delegate.getValue();
            return var10000 as URL;
         }


      public open val zipEntry: String?
         public open get() {
            return if (this.isJarScheme()) Resource.access$getEntry(Resource.INSTANCE, this.getUri()) else null;
         }


      public open val exists: Boolean
         public open get() {
            return Files.exists(this.path);
         }


      public open val isFile: Boolean
         public open get() {
            return this.isFile$delegate.getValue() as java.lang.Boolean;
         }


      public open val isDirectory: Boolean
         public open get() {
            return Files.isDirectory(this.path);
         }


      public open val isRegularFile: Boolean
         public open get() {
            return Files.isRegularFile(this.path);
         }


      public open val extension: String
         public open get() {
            return PathsKt.getExtension(this.path);
         }


      public open val isFileScheme: Boolean
         public open get() {
            return this.getUri().getScheme() == "file";
         }


      public open val isJrtScheme: Boolean
         public open get() {
            return this.getUri().getScheme() == "jrt";
         }


      public open val isJarScheme: Boolean
         public open get() {
            val `it$iv`: java.lang.String = this.getUri().getScheme();
            return `it$iv` == "jar" || StringsKt.equals(`it$iv`, "zip", true);
         }


      public open val schemePath: Path
         public open get() {
            val var10000: Path;
            if (!this.isFileScheme() && !this.isJrtScheme()) {
               val var1: Any = Resource.INSTANCE.getUriToPathCached().get(TuplesKt.to(this.getUri(), true));
               var10000 = var1 as Path;
            } else {
               var10000 = this.path;
            }

            return var10000;
         }


      public open val name: String
         public open get() {
            return PathsKt.getName(this.path);
         }


      public open val parent: IResource?
         public open get() {
            return this.parent$delegate.getValue() as IResource;
         }


      public open val file: File
         public open get() {
            val var10000: File = this.path.toFile();
            return var10000;
         }


      public open val absolutePath: String
         public open get() {
            return this.path.toAbsolutePath().toString();
         }


      public open val absolute: IResource
         public open get() {
            var var10002: Path = this.path.toAbsolutePath();
            var10002 = var10002.normalize();
            return new Resource.ResourceBasic(var10002);
         }


      public open val normalize: IResource
         public open get() {
            val var10002: Path = this.path.normalize();
            return new Resource.ResourceBasic(var10002);
         }


      private final var _string: String?
      private final var _hash: Int?

      public open val pathString: String
         public open get() {
            return this.toString();
         }


      init {
         this.path = path;
         this.uri$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, Resource.ResourceBasic::uri_delegate$lambda$0);
         this.url$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, Resource.ResourceBasic::url_delegate$lambda$1);
         this.isFile$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, Resource.ResourceBasic::isFile_delegate$lambda$2);
         this.parent$delegate = LazyKt.lazy(Resource.ResourceBasic::parent_delegate$lambda$4);
      }

      public fun expandRes(outPut: IResDirectory, zipEntry: String): IResource {
         var var10002: Path = this.getSchemePath().toAbsolutePath();
         var10002 = var10002.normalize();
         val var10000: Any = jarInnerResourceCache.get(new Resource.ExpandResKey(var10002, zipEntry, outPut.getAbsolute().getNormalize()));
         return var10000 as IResource;
      }

      public override fun expandRes(outPut: IResDirectory): IResource {
         val zipEntry: java.lang.String = this.getZipEntry();
         return if (zipEntry != null) this.expandRes(outPut, zipEntry) else this;
      }

      public override fun mkdirs() {
         if (this.getExists() && !this.isDirectory()) {
            this.toFile().mkdirs();
         } else {
            this.toDirectory().mkdirs();
         }
      }

      public open operator fun compareTo(other: IResource): Int {
         return this.toString().compareTo(other.toString());
      }

      public override fun resolve(name: String): IResource {
         if (this.isFile() && this.getZipLike()) {
            return new Resource.ResourceBasic(Resource.INSTANCE.archivePath(this.path, name));
         } else {
            val var10002: Path = this.path.resolve(name);
            return new Resource.ResourceBasic(var10002);
         }
      }

      public override fun toFile(): IResFile {
         return new Resource.ResFileImpl(this.path);
      }

      public override fun toDirectory(): IResDirectory {
         return new Resource.ResDirectoryImpl(this.path);
      }

      public override fun toString(): String {
         if (this._string != null) {
            return this._string;
         } else {
            val var2: java.lang.String = if (this.isFileScheme())
               this.path.toString()
               else
               CollectionsKt.joinToString$default(CollectionsKt.asReversed(SequencesKt.toList(this.seq())), "!", null, null, 0, null, null, 62, null);
            this._string = var2;
            return var2;
         }
      }

      public override fun seq(): Sequence<Path> {
         return if (this.isFileScheme())
            SequencesKt.sequenceOf(new Path[]{this.path})
            else
            SequencesKt.generateSequence(this.path, Resource.ResourceBasic::seq$lambda$5);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else {
            return other is Resource.ResourceBasic && this.path == (other as Resource.ResourceBasic).path;
         }
      }

      public override fun hashCode(): Int {
         if (this._hash != null) {
            return this._hash;
         } else {
            val var2: Int = 2234 + this.path.hashCode();
            this._hash = var2;
            return var2;
         }
      }

      override fun getZipLike(): Boolean {
         return IResource.DefaultImpls.getZipLike(this);
      }

      @Throws(java/io/IOException::class)
      override fun deleteDirectoryRecursively() {
         IResource.DefaultImpls.deleteDirectoryRecursively(this);
      }

      @Throws(java/io/IOException::class)
      override fun deleteDirectoryContents() {
         IResource.DefaultImpls.deleteDirectoryContents(this);
      }

      @JvmStatic
      fun `uri_delegate$lambda$0`(`this$0`: Resource.ResourceBasic): URI {
         return `this$0`.path.toUri();
      }

      @JvmStatic
      fun `url_delegate$lambda$1`(`this$0`: Resource.ResourceBasic): URL {
         return `this$0`.getUri().toURL();
      }

      @JvmStatic
      fun `isFile_delegate$lambda$2`(`this$0`: Resource.ResourceBasic): Boolean {
         return Files.isRegularFile(`this$0`.path);
      }

      @JvmStatic
      fun `parent_delegate$lambda$4`(`this$0`: Resource.ResourceBasic): IResource {
         val parent: Path = `this$0`.path.getParent();
         return if (parent == null && `this$0`.isJarScheme())
            Resource.INSTANCE.of(Resource.INSTANCE.archivePath(`this$0`.getSchemePath(), "/"))
            else
            (if (parent != null) Resource.INSTANCE.of(parent) else null);
      }

      @JvmStatic
      fun `seq$lambda$5`(it: Path): Path {
         val curUri: URI = it.toUri();
         return if (!(curUri.getScheme() == "file") && !(curUri.getScheme() == "jrt"))
            Resource.INSTANCE.getUriToPathCached().get(TuplesKt.to(curUri, true)) as Path
            else
            null;
      }

      public companion object {
         internal final val jarInnerResourceCache: LoadingCache<cn.sast.common.Resource.ExpandResKey, IResource>
      }
   }
}
