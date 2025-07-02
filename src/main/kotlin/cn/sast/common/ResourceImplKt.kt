@file:SourceDebugExtension(["SMAP\nResourceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/ResourceImplKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,730:1\n1#2:731\n1557#3:732\n1628#3,3:733\n1734#3,3:736\n1557#3:739\n1628#3,3:740\n1557#3:743\n1628#3,3:744\n1368#3:747\n1454#3,5:748\n*S KotlinDebug\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/ResourceImplKt\n*L\n681#1:732\n681#1:733,3\n682#1:736,3\n694#1:739\n694#1:740,3\n696#1:743\n696#1:744,3\n697#1:747\n697#1:748,5\n*E\n"])

package cn.sast.common

import java.io.ByteArrayInputStream
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.zip.ZipException
import kotlin.jvm.internal.SourceDebugExtension

public final val tempDirectoryPath: String

public final val sAstTempDirectory: Path
   public final get() {
      val var3: Path = Paths.get(tempDirectoryPath, "0a-sast-corax-can-delete", new SimpleDateFormat("YYYY.MM.DD.HH.mm").format(System.currentTimeMillis()));
      var3.toFile().mkdirs();
      return var3;
   }


public final val zipExtensions: List<String> = CollectionsKt.listOf(new java.lang.String[]{"zip", "jar", "war", "apk", "aar"})

public fun createFileSystem(uri: URI): FileSystem {
   val env: java.util.Map = MapsKt.toMutableMap(Resource.INSTANCE.getNewFileSystemEnv());
   var zipException: ZipException = null;
   val encodings: java.util.List = CollectionsKt.toMutableList(Resource.INSTANCE.getFileSystemEncodings());
   if (encodings.isEmpty()) {
      encodings.add("UTF-8");
   }

   for (IndexedValue encodingI : CollectionsKt.withIndex(encodings)) {
      env.put("encoding", encodingI.getValue() as java.lang.String);

      try {
         val var10000: FileSystem = FileSystems.newFileSystem(uri, env);
         return var10000;
      } catch (var8: ZipException) {
         zipException = var8;
      }
   }

   throw zipException;
}

public fun createFileSystem(path: Path): FileSystem {
   val env: java.util.Map = MapsKt.toMutableMap(Resource.INSTANCE.getNewFileSystemEnv());
   var zipException: ZipException = null;
   val encodings: java.util.List = CollectionsKt.toMutableList(Resource.INSTANCE.getFileSystemEncodings());
   if (encodings.isEmpty()) {
      encodings.add("UTF-8");
   }

   for (IndexedValue encodingI : CollectionsKt.withIndex(encodings)) {
      env.put("encoding", encodingI.getValue() as java.lang.String);

      try {
         val var10000: FileSystem = FileSystems.newFileSystem(path, env);
         return var10000;
      } catch (var8: ZipException) {
         zipException = var8;
      }
   }

   throw zipException;
}

public fun calculate(fis: InputStream, algorithm: String): String {
   val buffer: ByteArray = new byte[8192];
   val md: MessageDigest = MessageDigest.getInstance(algorithm);

   while (true) {
      val var5: Int = fis.read(buffer);
      if (var5 == -1) {
         val var10000: ByteArray = md.digest();
         return ArraysKt.joinToString$default(var10000, "", null, null, 0, null, ResourceImplKt::calculate$lambda$2, 30, null);
      }

      md.update(buffer, 0, var5);
   }
}

public fun ByteArray.calculate(algorithm: String): String {
   label19: {
      val var2: Closeable = new ByteArrayInputStream(`$this$calculate`);
      var var3: java.lang.Throwable = null;

      try {
         try {
            val var10: java.lang.String = calculate(var2 as ByteArrayInputStream, algorithm);
         } catch (var6: java.lang.Throwable) {
            var3 = var6;
            throw var6;
         }
      } catch (var7: java.lang.Throwable) {
         CloseableKt.closeFinally(var2, var3);
      }

      CloseableKt.closeFinally(var2, null);
   }
}

public fun Path.calculate(algorithm: String): String {
   label19: {
      val var10001: Array<OpenOption> = new OpenOption[0];
      val var10000: InputStream = Files.newInputStream(`$this$calculate`, Arrays.copyOf(var10001, var10001.length));
      val var2: Closeable = var10000;
      var var3: java.lang.Throwable = null;

      try {
         try {
            val var10: java.lang.String = calculate(var2 as InputStream, algorithm);
         } catch (var6: java.lang.Throwable) {
            var3 = var6;
            throw var6;
         }
      } catch (var7: java.lang.Throwable) {
         CloseableKt.closeFinally(var2, var3);
      }

      CloseableKt.closeFinally(var2, null);
   }
}

public fun IResFile.calculate(algorithm: String): String {
   return calculate(`$this$calculate`.getPath(), algorithm);
}

public fun splitGlobPath(p: String): Pair<IResource, String?> {
   var var6: java.lang.String = StringsKt.replace$default(StringsKt.replace$default(p, "\\", "/", false, 4, null), "//", "/", false, 4, null);
   val glob: java.util.List = new ArrayList();

   while (true) {
      try {
         val r: IResource = Resource.INSTANCE.of(var6);
         if (r.getExists()) {
            if (glob.isEmpty()) {
               return TuplesKt.to(r, null);
            }

            return TuplesKt.to(r, CollectionsKt.joinToString$default(glob, "/", null, null, 0, null, null, 62, null));
         }

         val var8: Any = Result.constructor-impl(Unit.INSTANCE);
      } catch (var5: java.lang.Throwable) {
         val index: Any = Result.constructor-impl(ResultKt.createFailure(var5));
      }

      if (var6 == "./") {
         throw new IllegalStateException(("\"$p\" is a invalid path").toString());
      }

      val var9: Int = StringsKt.lastIndexOf$default(var6, "/", 0, false, 6, null);
      if (var9 == -1) {
         glob.add(0, var6);
         var6 = "./";
      } else {
         val var10002: java.lang.String = var6.substring(var9 + 1);
         glob.add(0, var10002);
         val var10000: java.lang.String = var6.substring(0, var9);
         var6 = var10000;
      }
   }
}

public fun globPaths(path: IResource, pattern: String): List<IResource> {
   val pathMatcher: PathMatcher = path.getPath().getFileSystem().getPathMatcher("glob:$pattern");
   val r: java.util.List = new ArrayList();
   val p: Path = path.getPath().normalize();
   Files.walkFileTree(p, new SimpleFileVisitor<Path>(p, pathMatcher, r) {
      {
         this.$p = `$p`;
         this.$pathMatcher = `$pathMatcher`;
         this.$r = `$r`;
      }

      public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
         val var10000: Path = this.$p.relativize(filePath);
         if (this.$pathMatcher.matches(var10000.subpath(0, var10000.getNameCount()))) {
            this.$r.add(Resource.INSTANCE.of(filePath));
         }

         return FileVisitResult.CONTINUE;
      }

      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
         return this.visitFile(dir, attrs);
      }
   });
   return r;
}

public fun globPaths(p: String): List<IResource>? {
   var var12: java.lang.Iterable = StringsKt.split$default(p, new java.lang.String[]{File.pathSeparator}, false, 0, 6, null);
   val `element$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10));

   for (Object item$iv$iv : $this$map$iv) {
      `element$iv`.add(globPath(`item$iv$iv` as java.lang.String));
   }

   val r: java.util.List = `element$iv` as java.util.List;
   var12 = `element$iv` as java.util.List;
   var var20: Boolean;
   if (`element$iv` as java.util.List is java.util.Collection && ((`element$iv` as java.util.List) as java.util.Collection).isEmpty()) {
      var20 = true;
   } else {
      label61: {
         for (Object element$ivx : $this$map$iv) {
            if (`element$ivx` as java.util.List != null) {
               var20 = false;
               break label61;
            }
         }

         var20 = true;
      }
   }

   if (var20) {
      return null;
   } else {
      val var14: java.util.List = CollectionsKt.flatten(CollectionsKt.filterNotNull(r));
      return if (!var14.isEmpty()) var14 else null;
   }
}

public fun globPath(p: String): List<IResource>? {
   var var40: java.util.List;
   if (!new File(p).exists()) {
      val parts: java.util.List = StringsKt.split$default(p, new java.lang.String[]{"!"}, false, 0, 6, null);
      var var22: java.util.List = new ArrayList();
      var first: Boolean = true;

      for (java.lang.String part : parts) {
         if (first) {
            var40 = CollectionsKt.listOf(part);
         } else {
            val splitFileAndGlob: java.lang.Iterable = var22;
            val `$i$f$flatMap`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var22, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `$i$f$flatMap`.add("${`$i$f$flatMapTo` as IResource}!$part");
            }

            var40 = `$i$f$flatMap` as java.util.List;
         }

         first = false;
         val var24: java.lang.Iterable = var40;
         val var28: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var40, 10));

         for (Object item$iv$iv : $this$map$iv) {
            var28.add(splitGlobPath(var33 as java.lang.String));
         }

         val var26: java.lang.Iterable = var28 as java.util.List;
         val `destination$iv$ivx`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$flatMap$iv) {
            val g: java.lang.String = (var36 as Pair).getSecond() as java.lang.String;
            CollectionsKt.addAll(
               `destination$iv$ivx`, if (g == null) CollectionsKt.listOf((var36 as Pair).getFirst()) else globPaths((var36 as Pair).getFirst() as IResource, g)
            );
         }

         var22 = CollectionsKt.toMutableList(`destination$iv$ivx` as java.util.List);
      }

      if (!var22.isEmpty()) {
         return var22;
      }

      var40 = null;
   } else {
      var40 = CollectionsKt.listOf(Resource.INSTANCE.of(p));
   }

   return var40;
}

fun `calculate$lambda$2`(it: Byte): java.lang.CharSequence {
   val var2: Array<Any> = new Object[]{it};
   val var10000: java.lang.String = java.lang.String.format("%02x", Arrays.copyOf(var2, var2.length));
   return var10000;
}
