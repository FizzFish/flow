@file:SourceDebugExtension(["SMAP\nResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Resource.kt\ncn/sast/common/ResourceKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,177:1\n37#1:179\n38#1:180\n1#2:178\n*S KotlinDebug\n*F\n+ 1 Resource.kt\ncn/sast/common/ResourceKt\n*L\n142#1:179\n146#1:180\n*E\n"])

package cn.sast.common

import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URI
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Arrays
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.io.path.PathsKt
import kotlin.jvm.internal.SourceDebugExtension

public final val text: String
   public final get() {
      val var2: Charset = Charsets.UTF_8;
      return new java.lang.String(TextStreamsKt.readBytes(`$this$text`), var2);
   }


public final val javaExtensions: List<String> = CollectionsKt.listOf(new java.lang.String[]{"java", "kt", "kts", "scala", "groovy", "jsp"})

public final val zipLike: Boolean
   public final get() {
      return ResourceImplKt.getZipExtensions().contains(PathsKt.getExtension(`$this$zipLike`));
   }


public final val isJarScheme: Boolean
   public final inline get() {
      val it: java.lang.String = `$this$isJarScheme`.getScheme();
      return it == "jar" || StringsKt.equals(it, "zip", true);
   }


public final val isFileScheme: Boolean
   public final inline get() {
      return `$this$isFileScheme`.getScheme() == "file";
   }


public final val isJrtScheme: Boolean
   public final inline get() {
      return `$this$isJrtScheme`.getScheme() == "jrt";
   }


private final val escapes: Map<String, String> =
   MapsKt.mapOf(
      new Pair[]{
         TuplesKt.to("[", "%5B"),
         TuplesKt.to("]", "%5D"),
         TuplesKt.to("{", "%7B"),
         TuplesKt.to("}", "%7D"),
         TuplesKt.to("<", "%3C"),
         TuplesKt.to(">", "%3E"),
         TuplesKt.to("#", "%23"),
         TuplesKt.to("?", "%3F"),
         TuplesKt.to("@", "%40"),
         TuplesKt.to(" ", "%20")
      }
   )
   private final val urlEscapePattern: Pattern

@Throws(java/io/IOException::class)
public fun IResFile.readText(charset: Charset = Charsets.UTF_8): String {
   label19: {
      val var2: Path = `$this$readText`.getPath();
      val var3: Array<OpenOption> = new OpenOption[0];
      val var10: Closeable = new InputStreamReader(Files.newInputStream(var2, Arrays.copyOf(var3, var3.length)), charset);
      var var11: java.lang.Throwable = null;

      try {
         try {
            val var12: java.lang.String = TextStreamsKt.readText(var10 as InputStreamReader);
         } catch (var6: java.lang.Throwable) {
            var11 = var6;
            throw var6;
         }
      } catch (var7: java.lang.Throwable) {
         CloseableKt.closeFinally(var10, var11);
      }

      CloseableKt.closeFinally(var10, null);
   }
}

@Throws(java/io/IOException::class)
@JvmSynthetic
fun `readText$default`(var0: IResFile, var1: Charset, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = Charsets.UTF_8;
   }

   return readText(var0, var1);
}

@Throws(java/io/IOException::class)
public fun <T> IResFile.lineSequence(apply: (Sequence<String>) -> T): T {
   label19: {
      val var2: Path = `$this$lineSequence`.getPath();
      val var5: Array<OpenOption> = new OpenOption[0];
      val var3: Charset = Charsets.UTF_8;
      val var10: Closeable = new BufferedReader(new InputStreamReader(Files.newInputStream(var2, Arrays.copyOf(var5, var5.length)), var3), 8192);
      var var11: java.lang.Throwable = null;

      try {
         try {
            val var13: Any = apply.invoke(TextStreamsKt.lineSequence(var10 as BufferedReader));
         } catch (var6: java.lang.Throwable) {
            var11 = var6;
            throw var6;
         }
      } catch (var7: java.lang.Throwable) {
         CloseableKt.closeFinally(var10, var11);
      }

      CloseableKt.closeFinally(var10, null);
   }
}

@Throws(java/io/IOException::class)
public fun IResFile.readAllBytes(): ByteArray {
   label19: {
      val var10000: Path = `$this$readAllBytes`.getPath();
      val var10001: Array<OpenOption> = new OpenOption[0];
      val var10: InputStream = Files.newInputStream(var10000, Arrays.copyOf(var10001, var10001.length));
      val var1: Closeable = var10;
      var var2: java.lang.Throwable = null;

      try {
         try {
            val var9: ByteArray = (var1 as InputStream).readAllBytes();
         } catch (var5: java.lang.Throwable) {
            var2 = var5;
            throw var5;
         }
      } catch (var6: java.lang.Throwable) {
         CloseableKt.closeFinally(var1, var2);
      }

      CloseableKt.closeFinally(var1, null);
   }
}

@Throws(java/io/IOException::class)
public fun IResFile.writeText(text: String, charset: Charset = Charsets.UTF_8) {
   label19: {
      val var3: Path = `$this$writeText`.getPath();
      val var4: Array<OpenOption> = new OpenOption[0];
      val var11: Closeable = new OutputStreamWriter(Files.newOutputStream(var3, Arrays.copyOf(var4, var4.length)), charset);
      var var12: java.lang.Throwable = null;

      try {
         try {
            (var11 as OutputStreamWriter).write(text);
         } catch (var7: java.lang.Throwable) {
            var12 = var7;
            throw var7;
         }
      } catch (var8: java.lang.Throwable) {
         CloseableKt.closeFinally(var11, var12);
      }

      CloseableKt.closeFinally(var11, null);
   }
}

@Throws(java/io/IOException::class)
@JvmSynthetic
fun `writeText$default`(var0: IResFile, var1: java.lang.String, var2: Charset, var3: Int, var4: Any) {
   if ((var3 and 2) != 0) {
      var2 = Charsets.UTF_8;
   }

   writeText(var0, var1, var2);
}

public fun uriOf(uri: String): URI {
   val var10000: Matcher = urlEscapePattern.matcher(uri);
   val m: Matcher = var10000;
   val sb: StringBuffer = new StringBuffer();

   while (m.find()) {
      m.appendReplacement(sb, escapes.get(m.group(1)));
   }

   m.appendTail(sb);
   return new URI(new URI(sb.toString()).toASCIIString());
}

public fun uriToPath(uri: URI, getRootFsPath: Boolean = false): Path {
   if (uri.getScheme() == "file") {
      if (getRootFsPath) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         val var17: Path = Paths.get(uri);
         return var17;
      }
   } else if (uri.getScheme() == "jrt") {
      throw new IllegalStateException(("unsupported uri: $uri with a JRT scheme.").toString());
   } else {
      val spec: java.lang.String = uri.getSchemeSpecificPart();
      val var9: Int = StringsKt.lastIndexOf$default(spec, "!/", 0, false, 6, null);
      val var16: Path;
      if (var9 != -1) {
         val var10000: java.lang.String = spec.substring(0, var9);
         val var15: java.lang.String = spec.substring(var9 + 1);
         val archive: Path = uriToPath$default(uriOf(var10000), false, 2, null);
         if (getRootFsPath) {
            return archive;
         }

         val var4: Path = Resource.INSTANCE.getZipFileSystem(archive).getPath(var15);
         var16 = var4;
      } else {
         if (getRootFsPath) {
            throw new IllegalStateException("Check failed.".toString());
         }

         val var13: Path = Paths.get(uriOf(spec));
         var16 = var13;
      }

      return var16;
   }
}

@JvmSynthetic
fun `uriToPath$default`(var0: URI, var1: Boolean, var2: Int, var3: Any): Path {
   if ((var2 and 2) != 0) {
      var1 = false;
   }

   return uriToPath(var0, var1);
}

public fun findCacheFromDeskOrCreate(input: Path, output: Path, creator: () -> Unit): Path {
   val md5: java.lang.String = Resource.INSTANCE.fileOf(input).getMd5();
   var var10000: Path = output.toAbsolutePath();
   var10000 = var10000.getParent();
   if (var10000 != null) {
      var10000 = var10000.resolve(".${StringsKt.substringBeforeLast$default(PathsKt.getName(output), ".", null, 2, null)}.successful");
      if (var10000 != null) {
         val var8: File = var10000.toFile();
         if (var8 != null) {
            if (var8.exists() && md5 == FilesKt.readText$default(var8, null, 1, null)) {
               val var10001: Array<LinkOption> = new LinkOption[0];
               if (Files.exists(output, Arrays.copyOf(var10001, var10001.length))) {
                  return output;
               }
            } else {
               Files.deleteIfExists(output);
            }

            creator.invoke();
            FilesKt.writeText$default(var8, md5, null, 2, null);
            return output;
         }
      }
   }

   throw new IllegalStateException(("bad output paths: $output").toString());
}
