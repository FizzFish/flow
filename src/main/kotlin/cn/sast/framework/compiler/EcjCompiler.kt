package cn.sast.framework.compiler

import cn.sast.common.IResource
import java.io.Closeable
import java.io.File
import java.io.PrintWriter
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.PersistentSet
import mu.KLogger
import org.eclipse.jdt.core.compiler.CompilationProgress
import org.eclipse.jdt.internal.compiler.batch.Main
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nEcjCompiler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EcjCompiler.kt\ncn/sast/framework/compiler/EcjCompiler\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Logging.kt\norg/utbot/common/LoggingKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,150:1\n1279#2,2:151\n1293#2,4:153\n1557#2:157\n1628#2,3:158\n1863#2,2:161\n1863#2,2:163\n1#3:165\n49#4,13:166\n62#4,11:181\n37#5,2:179\n*S KotlinDebug\n*F\n+ 1 EcjCompiler.kt\ncn/sast/framework/compiler/EcjCompiler\n*L\n113#1:151,2\n113#1:153,4\n118#1:157\n118#1:158,3\n121#1:161,2\n130#1:163,2\n143#1:166,13\n143#1:181,11\n144#1:179,2\n*E\n"])
public class EcjCompiler(sourcePath: PersistentSet<IResource>,
   classpath: PersistentSet<String>,
   class_opt: IResource,
   customOptions: List<String>,
   useDefaultJava: Boolean,
   outWriter: PrintWriter = new PrintWriter(System.out),
   errWriter: PrintWriter = new PrintWriter(System.err),
   systemExitWhenFinished: Boolean = false,
   customDefaultOptions: MutableMap<String, String>? = null,
   compilationProgress: CompilationProgress? = null
) : Main(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress) {
   public final val sourcePath: PersistentSet<IResource>
   public final val classpath: PersistentSet<String>
   public final val class_opt: IResource
   public final val customOptions: List<String>
   public final val useDefaultJava: Boolean
   public final val collectClassPath: MutableSet<String>
   private final val currentClasspathNameHack: MutableSet<String>

   init {
      this.sourcePath = sourcePath;
      this.classpath = classpath;
      this.class_opt = class_opt;
      this.customOptions = customOptions;
      this.useDefaultJava = useDefaultJava;
      this.collectClassPath = new LinkedHashSet<>();
      this.currentClasspathNameHack = new LinkedHashSet<>();
   }

   private fun getDefaultClasspath(): ArrayList<Classpath> {
      val var10000: ArrayList = super.handleClasspath(null, null);
      return var10000;
   }

   protected open fun addNewEntry(
      paths: ArrayList<Classpath?>?,
      currentClasspathName: String,
      currentRuleSpecs: ArrayList<String?>?,
      customEncoding: String?,
      destPath: String?,
      isSourceOnly: Boolean,
      rejectDestinationPathOnJars: Boolean
   ) {
      this.currentClasspathNameHack.add(currentClasspathName);
      super.addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, destPath, isSourceOnly, rejectDestinationPathOnJars);
   }

   private fun getPathsFrom(path: String): ArrayList<Classpath>? {
      this.currentClasspathNameHack.clear();
      val paths: ArrayList = new ArrayList();

      try {
         this.processPathEntries(4, paths, path, null, false, false);
         return paths;
      } catch (var4: IllegalArgumentException) {
         return null;
      }
   }

   private fun replace(ecjClasspathName: String): List<String> {
      val path: ArrayList = this.getPathsFrom(ecjClasspathName);
      label43:
      if (this.currentClasspathNameHack.size() == 1 && path != null && path.size() == 1) {
         val origClassPathFileName: java.lang.String = CollectionsKt.first(this.currentClasspathNameHack) as java.lang.String;
         val res: java.util.List = new ArrayList();
         if (origClassPathFileName.length() > 0) {
            val cpf: File = new File(origClassPathFileName);
            if (!cpf.exists()) {
               val var10000: Path = cpf.getParentFile().toPath();
               val var18: java.lang.String = cpf.getName();
               val var7: Closeable = Files.newDirectoryStream(var10000, var18);
               var var8: java.lang.Throwable = null;

               try {
                  try {
                     val var9: DirectoryStream = var7 as DirectoryStream;

                     for (Path subClasspathName : var9) {
                        res.add(StringsKt.replace(ecjClasspathName, origClassPathFileName, subClasspathName.toString(), false));
                        this.collectClassPath.add(subClasspathName.toString());
                     }
                  } catch (var14: java.lang.Throwable) {
                     var8 = var14;
                     throw var14;
                  }
               } catch (var15: java.lang.Throwable) {
                  CloseableKt.closeFinally(var7, var8);
               }

               CloseableKt.closeFinally(var7, null);
            }
         }

         this.collectClassPath.add(ecjClasspathName);
         return CollectionsKt.listOf(ecjClasspathName);
      } else {
         this.collectClassPath.add(ecjClasspathName);
         return CollectionsKt.listOf(ecjClasspathName);
      }
   }

   public fun compile(): Boolean {
      label108: {
         val args: java.util.List = new ArrayList();
         var var9: java.util.Collection;
         if (this.customOptions.isEmpty()) {
            val `$this$bracket_u24default$iv`: java.util.Set = CollectionsKt.toMutableSet(this.classpath as java.lang.Iterable);
            args.addAll(
               CollectionsKt.listOf(
                  new java.lang.String[]{"-source", "11", "-target", "11", "-proceedOnError", "-warn:none", "-g:lines,vars,source", "-preserveAllLocals"}
               )
            );
            if (!`$this$bracket_u24default$iv`.isEmpty()) {
               val `$i$f$forEach`: java.lang.Iterable = `$this$bracket_u24default$iv`;
               val `startTime$iv`: LinkedHashMap = new LinkedHashMap(
                  RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$bracket_u24default$iv`, 10)), 16)
               );

               for (Object element$iv$iv : $this$associateWith$iv) {
                  `startTime$iv`.put(`t$iv`, this.replace(`t$iv` as java.lang.String));
               }

               val var33: java.util.List = CollectionsKt.toMutableList(CollectionsKt.flatten(`startTime$iv`.values()));
               if (this.useDefaultJava) {
                  val var39: java.lang.Iterable = this.getDefaultClasspath();
                  var9 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var39, 10));

                  for (Object item$iv$iv : var39) {
                     var9.add((var52 as Classpath).getPath());
                  }

                  var33.addAll(var9 as java.util.List);
               }

               val var36: java.lang.Iterable;
               for (Object element$iv : var36) {
                  var9 = var46 as java.lang.String;
                  args.add("-classpath");
                  args.add(var9);
               }
            }

            args.addAll(CollectionsKt.listOf(new java.lang.String[]{"-d", this.class_opt.toString()}));
            if (!(this.sourcePath as java.util.Collection).isEmpty()) {
               val var27: java.lang.Iterable;
               for (Object element$iv : var27) {
                  val var10001: java.lang.String = (var41 as IResource).getFile().getPath();
                  args.add(var10001);
               }
            }

            kLogger.info(EcjCompiler::compile$lambda$5);
         } else {
            if (!this.sourcePath.isEmpty()) {
               throw new IllegalStateException(("sourcePath: ${this.sourcePath} must be empty while use customOptions: ${this.customOptions}").toString());
            }

            if (!this.classpath.isEmpty()) {
               throw new IllegalStateException(("classpath: ${this.classpath} must be empty while use customOptions: ${this.customOptions}").toString());
            }

            args.addAll(this.customOptions);
         }

         val var24: LoggerWithLogMethod = LoggingKt.info(kLogger);
         val var28: java.lang.String = "compile java source";
         var24.getLogMethod().invoke(new EcjCompiler$compile$$inlined$bracket$default$1("compile java source"));
         val var42: LocalDateTime = LocalDateTime.now();
         var var45: Boolean = false;
         val var48: ObjectRef = new ObjectRef();
         var48.element = Maybe.Companion.empty();

         try {
            try {
               ;
            } catch (var19: java.lang.Throwable) {
               var24.getLogMethod().invoke(new EcjCompiler$compile$$inlined$bracket$default$4(var42, var28, var19));
               var45 = true;
               throw var19;
            }
         } catch (var20: java.lang.Throwable) {
            if (!var45) {
               if ((var48.element as Maybe).getHasValue()) {
                  var24.getLogMethod().invoke(new EcjCompiler$compile$$inlined$bracket$default$5(var42, "compile java source", var48));
               } else {
                  var24.getLogMethod().invoke(new EcjCompiler$compile$$inlined$bracket$default$6(var42, "compile java source"));
               }
            }
         }

         if ((var48.element as Maybe).getHasValue()) {
            var24.getLogMethod().invoke(new EcjCompiler$compile$$inlined$bracket$default$2(var42, "compile java source", var48));
         } else {
            var24.getLogMethod().invoke(new EcjCompiler$compile$$inlined$bracket$default$3(var42, "compile java source"));
         }

         return var9 as java.lang.Boolean;
      }
   }

   @JvmStatic
   fun `compile$lambda$5`(`$args`: java.util.List): Any {
      return "ecj cmd args:\n[ ${CollectionsKt.joinToString$default(`$args`, "\n", null, null, 0, null, null, 62, null)} ]\n";
   }

   @JvmStatic
   fun `kLogger$lambda$9`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val kLogger: KLogger
   }
}
package cn.sast.framework.compiler

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `EcjCompiler$compile$$inlined$bracket$default$1` : Function0<Object> {
   fun `EcjCompiler$compile$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}
package cn.sast.framework.compiler

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `EcjCompiler$compile$$inlined$bracket$default$2` : Function0<Object> {
   fun `EcjCompiler$compile$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.compiler

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `EcjCompiler$compile$$inlined$bracket$default$3` : Function0<Object> {
   fun `EcjCompiler$compile$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.compiler

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `EcjCompiler$compile$$inlined$bracket$default$4` : Function0<Object> {
   fun `EcjCompiler$compile$$inlined$bracket$default$4`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$t = `$t`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl(ResultKt.createFailure(this.$t));
      return "Finished (in $var10000): $var10001 :: EXCEPTION :: ";
   }
}
package cn.sast.framework.compiler

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `EcjCompiler$compile$$inlined$bracket$default$5` : Function0<Object> {
   fun `EcjCompiler$compile$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.compiler

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `EcjCompiler$compile$$inlined$bracket$default$6` : Function0<Object> {
   fun `EcjCompiler$compile$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
