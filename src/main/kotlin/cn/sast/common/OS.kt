package cn.sast.common

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.net.URL
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.security.CodeSource
import java.security.ProtectionDomain
import java.util.ArrayList
import java.util.Locale
import java.util.Optional
import kotlin.io.path.PathsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.optionals.OptionalsKt
import org.utbot.common.PathUtil

@SourceDebugExtension(["SMAP\nOS.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OS.kt\ncn/sast/common/OS\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,63:1\n1#2:64\n*E\n"])
public object OS {
   public final var maxThreadNum: Int = Runtime.getRuntime().availableProcessors()
      internal set

   public final val isWindows: Boolean

   public final val binaryUrl: URL? by LazyKt.lazy(LazyThreadSafetyMode.NONE, OS::binaryUrl_delegate$lambda$0)
      public final get() {
         return binaryUrl$delegate.getValue() as URL;
      }


   public final val jarBinPath: Path? by LazyKt.lazy(LazyThreadSafetyMode.NONE, OS::jarBinPath_delegate$lambda$3)
      public final get() {
         return jarBinPath$delegate.getValue() as Path;
      }


   public final var args: Array<String>
      internal set

   public final val javaExecutableFilePath: String?
      public final get() {
         val var10000: Optional = ProcessHandle.current().info().command();
         return OptionalsKt.getOrNull(var10000) as java.lang.String;
      }


   public final val posixFilePermissions: Set<PosixFilePermission>?

   public fun getCommandLine(args: Array<String>? = null, jvmOptions: Boolean = true): List<String>? {
      var var10000: java.lang.String = this.getJavaExecutableFilePath();
      if (var10000 == null) {
         return null;
      } else {
         val javaExecutableFilePath: java.lang.String = var10000;

         var r: java.util.List;
         try {
            r = new ArrayList();
            r.add(javaExecutableFilePath);
            if (jvmOptions) {
               val var12: RuntimeMXBean = ManagementFactory.getRuntimeMXBean();
               if (var12 != null) {
                  val var13: java.util.List = var12.getInputArguments();
                  if (var13 != null) {
                     r.addAll(var13);
                  }
               }
            }

            val var14: URL = this.getBinaryUrl();
            if (var14 != null) {
               var10000 = var14.getPath();
               if (var10000 != null) {
                  r.add("-cp");
                  r.add(var10000);
                  val var16: java.util.Collection = r;
                  var var10001: Array<java.lang.String> = args;
                  if (args == null) {
                     var10001 = args;
                  }

                  CollectionsKt.addAll(var16, var10001);
               }
            }

            r = r;
         } catch (var8: java.lang.Throwable) {
            r = null;
         }

         return r;
      }
   }

   @JvmStatic
   fun `binaryUrl_delegate$lambda$0`(): URL {
      val var10000: ProtectionDomain = OS.class.getProtectionDomain();
      if (var10000 != null) {
         val var4: CodeSource = var10000.getCodeSource();
         if (var4 != null) {
            val var5: URL = var4.getLocation();
            if (var5 != null) {
               val var6: java.lang.String = var5.toString();
               if (!StringsKt.endsWith$default(var6, "BOOT-INF/classes!/", false, 2, null)
                  && !StringsKt.endsWith$default(var6, "BOOT-INF/classes", false, 2, null)) {
                  return var5;
               }

               return PathUtil.INSTANCE.toURL(Resource.INSTANCE.of(ResourceKt.uriOf(StringsKt.removeSuffix(var6, "!/"))).getSchemePath());
            }
         }
      }

      return null;
   }

   @JvmStatic
   fun `jarBinPath_delegate$lambda$3`(): Path {
      val var10000: URL = INSTANCE.getBinaryUrl();
      val var5: Path;
      if (var10000 != null) {
         val var2: Path = Path.of(var10000.toURI());
         var5 = if (PathExtensionsKt.isRegularFile(var2) && StringsKt.endsWith$default(PathsKt.getName(var2), ".jar", false, 2, null)) var2 else null;
      } else {
         var5 = null;
      }

      return var5;
   }

   @JvmStatic
   fun {
      var var10000: java.lang.String = System.getProperty("os.name");
      val var1: Locale = Locale.getDefault();
      var10000 = var10000.toLowerCase(var1);
      isWindows = StringsKt.contains$default(var10000, "windows", false, 2, null);
      posixFilePermissions = if (isWindows) null else PosixFilePermissions.fromString("rwxr--r--");
   }
}
