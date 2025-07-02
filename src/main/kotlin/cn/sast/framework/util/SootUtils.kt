package cn.sast.framework.util

import cn.sast.common.IResFile
import cn.sast.common.JarMerger
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.googlecode.d2j.dex.Dex2jar
import com.googlecode.d2j.dex.DexExceptionHandler
import com.googlecode.d2j.reader.BaseDexFileReader
import com.googlecode.d2j.reader.MultiDexFileReader
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.Optional
import java.time.LocalDateTime
import kotlin.io.path.PathsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.jvm.optionals.OptionalsKt
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import mu.KLogger
import mu.KotlinLogging
import soot.ClassSource
import soot.DexClassSource
import soot.FoundFile
import soot.IFoundFile
import soot.ModuleUtil
import soot.SourceLocator
import soot.asm.AsmClassSource

@SourceDebugExtension(["SMAP\nSootUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SootUtils.kt\ncn/sast/framework/util/SootUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ReportConverter.kt\ncn/sast/framework/report/ReportConverterKt\n+ 4 Logging.kt\norg/utbot/common/LoggingKt\n*L\n1#1,158:1\n1#2:159\n38#3,3:160\n38#3,3:163\n38#3,3:166\n38#3,3:169\n38#3,3:172\n49#4,24:175\n*S KotlinDebug\n*F\n+ 1 SootUtils.kt\ncn/sast/framework/util/SootUtils\n*L\n122#1:160,3\n123#1:163,3\n124#1:166,3\n125#1:169,3\n141#1:172,3\n41#1:175,24\n*E\n"])
public object SootUtils {
   private final val sootClass2classFileCache: LoadingCache<Path, Optional<Path>>
   private final val logger: KLogger = KotlinLogging.INSTANCE.logger(SootUtils::logger$lambda$7)

   @Throws(java/lang/Exception::class)
   public fun dex2jar(
      dexSource: Path,
      output: Path,
      notHandleException: Boolean = false,
      reuseReg: Boolean = false,
      debugInfo: Boolean = true,
      optmizeSynchronized: Boolean = true,
      printIR: Boolean = false,
      noCode: Boolean = false,
      skipExceptions: Boolean = false,
      dontSanitizeNames: Boolean = false,
      computeFrames: Boolean = true,
      topoLogicalSort: Boolean = true
   ): Path {
      ResourceKt.findCacheFromDeskOrCreate(dexSource, output) {
         `dex2jar\$lambda\$2`(
            dexSource,
            output,
            notHandleException,
            reuseReg,
            debugInfo,
            optmizeSynchronized,
            printIR,
            noCode,
            skipExceptions,
            dontSanitizeNames,
            computeFrames,
            topoLogicalSort
         )
      }
      return output;
   }

   @Throws(java/lang/Exception::class)
   public fun dex2jar(dexSource: Set<Path>, output: Path): Path {
      label43: {
         if (dexSource.size() == 1) {
            val var10000: Path = CollectionsKt.first(dexSource) as Path;
            val var10001: Array<OpenOption> = new OpenOption[0];
            val var29: InputStream = Files.newInputStream(var10000, Arrays.copyOf(var10001, var10001.length));
            val var3: Closeable = var29;
            var var4: java.lang.Throwable = null;

            try {
               try {
                  Dex2jar.from(var3 as InputStream).to(output);
               } catch (var11: java.lang.Throwable) {
                  var4 = var11;
                  throw var11;
               }
            } catch (var12: java.lang.Throwable) {
               CloseableKt.closeFinally(var3, var4);
            }

            CloseableKt.closeFinally(var3, null);
         } else {
            val var23: Closeable = new JarMerger(output, null, 2, null);
            var var24: java.lang.Throwable = null;

            try {
               try {
                  val var26: JarMerger = var23 as JarMerger;

                  for (Path dex : dexSource) {
                     val part: Path = Files.createTempFile(ResourceImplKt.getSAstTempDirectory(), PathsKt.getName(dex), ".jar");
                     val var30: SootUtils = INSTANCE;
                     JarMerger.addJar$default(
                        var26,
                        dex2jar$default(var30, dex, part, false, false, false, false, false, false, false, true, false, false, 3580, null),
                        null,
                        null,
                        6,
                        null
                     );
                     Files.deleteIfExists(part);
                  }
               } catch (var13: java.lang.Throwable) {
                  var24 = var13;
                  throw var13;
               }
            } catch (var14: java.lang.Throwable) {
               CloseableKt.closeFinally(var23, var24);
            }

            CloseableKt.closeFinally(var23, null);
         }

         return output;
      }
   }

   @JvmStatic
   private fun `dex2jar\$lambda\$2`(
      `$dexSource`: Path,
      `$output`: Path,
      `$notHandleException`: Boolean,
      `$reuseReg`: Boolean,
      `$debugInfo`: Boolean,
      `$optmizeSynchronized`: Boolean,
      `$printIR`: Boolean,
      `$noCode`: Boolean,
      `$skipExceptions`: Boolean,
      `$dontSanitizeNames`: Boolean,
      `$computeFrames`: Boolean,
      `$topoLogicalSort`: Boolean
   ) {
      val `$this$bracket_u24default$iv`: LoggerWithLogMethod = LoggingKt.info(logger)
      val `$msg$iv`: String = "dex2jar ${`$dexSource`} -> ${`$output`}"
      `$this$bracket_u24default$iv`.logMethod.invoke { `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$1`(`$msg$iv`) }
      val `$startTime$iv`: LocalDateTime = LocalDateTime.now()
      val `res$iv`: ObjectRef = ObjectRef()
      `res$iv`.element = Maybe.empty()

      try {
         val reader: BaseDexFileReader = MultiDexFileReader.open(Files.readAllBytes(`$dexSource`))
         val handler = if (`$notHandleException`) null else BaksmaliBaseDexExceptionHandler()
         val it = Dex2jar.from(reader).withExceptionHandler(handler as DexExceptionHandler?).reUseReg(`$reuseReg`)
         if (`$topoLogicalSort`) it.topoLogicalSort()
         it.skipDebug(!`$debugInfo`)
            .optimizeSynchronized(`$optmizeSynchronized`)
            .printIR(`$printIR`)
            .noCode(`$noCode`)
            .skipExceptions(`$skipExceptions`)
            .dontSanitizeNames(`$dontSanitizeNames`)
            .computeFrames(`$computeFrames`)
            .to(`$output`)
         `res$iv`.element = Maybe(Unit)
         (`res$iv`.element as Maybe).getOrThrow()
         if ((`res$iv`.element as Maybe).getHasValue()) {
            `$this$bracket_u24default$iv`.logMethod.invoke {
               `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$2`(`$startTime$iv`, `$msg$iv`, `res$iv`)
            }
         } else {
            `$this$bracket_u24default$iv`.logMethod.invoke {
               `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$3`(`$startTime$iv`, `$msg$iv`)
            }
         }
      } catch (t: Throwable) {
         if ((`res$iv`.element as Maybe).getHasValue()) {
            `$this$bracket_u24default$iv`.logMethod.invoke {
               `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$5`(`$startTime$iv`, `$msg$iv`, `res$iv`)
            }
         } else {
            `$this$bracket_u24default$iv`.logMethod.invoke {
               `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$6`(`$startTime$iv`, `$msg$iv`)
            }
         }
         throw t
      }
   }

   private fun lookupInArchive(archivePath: IResFile, fileName: String): IResFile? {
      return if (archivePath.getEntries().contains(fileName))
         Resource.INSTANCE.fileOf(Resource.INSTANCE.archivePath(archivePath.getPath(), fileName))
         else
         null;
   }

   public fun getClassSourceFromSoot(className: String): IResFile? {
      var var10000: ClassSource = SourceLocator.v().getClassSource(className);
      if (var10000 == null) {
         return null;
      } else {
         val var38: IResFile;
         if (var10000 is AsmClassSource) {
            var `it$iv`: Field = var10000.getClass().getDeclaredField("foundFile");
            `it$iv`.setAccessible(true);
            var10000 = (ClassSource)`it$iv`.get(var10000);
            if (var10000 !is IFoundFile) {
               var10000 = null;
            }

            val jar: IFoundFile = var10000 as IFoundFile;
            val var34: FoundFile = var10000 as IFoundFile as? FoundFile;
            if ((jar as? FoundFile) == null) {
               return null;
            }

            val `it$ivx`: Field = var34.getClass().getDeclaredField("path");
            `it$ivx`.setAccessible(true);
            var10000 = (ClassSource)`it$ivx`.get(var34);
            if (var10000 !is Path) {
               var10000 = null;
            }

            val dex: Path = var10000 as Path;
            `it$iv` = var34.getClass().getDeclaredField("file");
            `it$iv`.setAccessible(true);
            var10000 = (ClassSource)`it$iv`.get(var34);
            if (var10000 !is File) {
               var10000 = null;
            }

            val var16: File = var10000 as File;
            val `it$ivxx`: Field = var34.getClass().getDeclaredField("entryName");
            `it$ivxx`.setAccessible(true);
            var10000 = (ClassSource)`it$ivxx`.get(var34);
            if (var10000 !is java.lang.String) {
               var10000 = null;
            }

            val var18: java.lang.String = var10000 as java.lang.String;
            if (dex != null) {
               var38 = Resource.INSTANCE.of(dex).toFile();
            } else {
               if (var16 == null || var18 == null) {
                  return null;
               }

               val var39: Resource = Resource.INSTANCE;
               val var10001: Path = var16.toPath();
               var38 = var39.fileOf(var10001).resolve(var18).toFile();
            }
         } else {
            if (var10000 !is DexClassSource) {
               return null;
            }

            if (ModuleUtil.module_mode()) {
               var38 = null;
            } else {
               val var14: java.lang.String = "${StringsKt.replace$default(className, '.', '/', false, 4, null)}.class";
               val `it$ivxxx`: Field = var10000.getClass().getDeclaredField("path");
               `it$ivxxx`.setAccessible(true);
               var10000 = (ClassSource)`it$ivxxx`.get(var10000);
               if (var10000 !is File) {
                  var10000 = null;
               }

               val var41: File = var10000 as File;
               if (var10000 as File == null) {
                  return null;
               }

               val var42: LoadingCache = sootClass2classFileCache;
               var var45: Path = var41.toPath();
               var45 = var45.toAbsolutePath();
               var10000 = (ClassSource)var42.get(var45.normalize());
               val var44: Path = OptionalsKt.getOrNull(var10000 as Optional) as Path;
               if (var44 == null) {
                  return null;
               }

               var38 = this.lookupInArchive(Resource.INSTANCE.fileOf(var44), var14);
            }
         }

         return var38;
      }
   }

   public fun cleanUp() {
      sootClass2classFileCache.cleanUp();
   }

   @JvmStatic
   fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$1`(`$msg`: String): Any {
      return "Started: ${`$msg`}"
   }

   @JvmStatic
   fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$2`(
      `$startTime`: LocalDateTime,
      `$msg`: String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} "
   }

   @JvmStatic
   fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$3`(
      `$startTime`: LocalDateTime,
      `$msg`: String
   ): Any {
      val var1: LocalDateTime = `$startTime`
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>"
   }

   @JvmStatic
   fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$4`(
      `$startTime`: LocalDateTime,
      `$msg`: String,
      `$t`: Throwable
   ): Any {
      val var1: LocalDateTime = `$startTime`
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} :: EXCEPTION :: "
   }

   @JvmStatic
   fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$5`(
      `$startTime`: LocalDateTime,
      `$msg`: String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} "
   }

   @JvmStatic
   fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$6`(
      `$startTime`: LocalDateTime,
      `$msg`: String
   ): Any {
      val var1: LocalDateTime = `$startTime`
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>"
   }

   @JvmStatic
   fun `sootClass2classFileCache$lambda$6$lambda$5`(`$dexSource`: Path): Any {
      return "failed to convert dex: $`$dexSource` to jar file.";
   }

   @JvmStatic
   fun `sootClass2classFileCache$lambda$6`(dexSource: Path): Optional {
      try {
         val var10000: Path = Resource.INSTANCE.getZipExtractOutputDir().resolve("dex2jar");
         val output: Path = var10000.resolve(
            "${StringsKt.substringBeforeLast$default(PathsKt.getName(dexSource), ".", null, 2, null)}-${Math.abs(dexSource.toString().hashCode() + 1)}.jar"
         );
         val var4: SootUtils = INSTANCE;
         return Optional.of(dex2jar$default(var4, dexSource, output, false, false, false, false, false, false, false, false, false, false, 3580, null));
      } catch (var3: Exception) {
         logger.warn(var3, SootUtils::sootClass2classFileCache$lambda$6$lambda$5);
         return Optional.ofNullable(null);
      }
   }

   @JvmStatic
   fun `logger$lambda$7`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun {
      val var1: LoadingCache = Caffeine.newBuilder().build(new CacheLoader(SootUtils::sootClass2classFileCache$lambda$6) {
         {
            this.function = function;
         }
      });
      sootClass2classFileCache = var1;
   }
}
