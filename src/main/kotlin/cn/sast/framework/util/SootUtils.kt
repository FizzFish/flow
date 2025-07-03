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
object SootUtils {
    private val sootClass2classFileCache: LoadingCache<Path, Optional<Path>> = Caffeine.newBuilder()
        .build(object : CacheLoader<Path, Optional<Path>> {
            override fun load(key: Path): Optional<Path> {
                return `sootClass2classFileCache$lambda$6`(key)
            }
        })

    private val logger: KLogger = KotlinLogging.logger { "SootUtils" }

    @Throws(Exception::class)
    fun dex2jar(
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
        return output
    }

    @Throws(Exception::class)
    fun dex2jar(dexSource: Set<Path>, output: Path): Path {
        if (dexSource.size == 1) {
            Files.newInputStream(dexSource.first()).use { input ->
                Dex2jar.from(input).to(output)
            }
        } else {
            JarMerger(output).use { merger ->
                for (dex in dexSource) {
                    val part = Files.createTempFile(
                        ResourceImplKt.getSAstTempDirectory(),
                        PathsKt.getName(dex),
                        ".jar"
                    )
                    merger.addJar(dex2jar(dex, part))
                    Files.deleteIfExists(part)
                }
            }
        }
        return output
    }

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
            val it = Dex2jar.from(reader).withExceptionHandler(handler).reUseReg(`$reuseReg`)
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
            null
    }

    fun getClassSourceFromSoot(className: String): IResFile? {
        var classSource: ClassSource? = SourceLocator.v().getClassSource(className) ?: return null

        return if (classSource is AsmClassSource) {
            val foundFileField = classSource.javaClass.getDeclaredField("foundFile").apply { isAccessible = true }
            val jar = foundFileField.get(classSource) as? IFoundFile ?: return null
            val foundFile = jar as? FoundFile ?: return null

            val pathField = foundFile.javaClass.getDeclaredField("path").apply { isAccessible = true }
            val dex = pathField.get(foundFile) as? Path ?: return null

            val fileField = foundFile.javaClass.getDeclaredField("file").apply { isAccessible = true }
            val file = fileField.get(foundFile) as? File ?: return null

            val entryNameField = foundFile.javaClass.getDeclaredField("entryName").apply { isAccessible = true }
            val entryName = entryNameField.get(foundFile) as? String ?: return null

            if (dex != null) {
                Resource.INSTANCE.of(dex).toFile()
            } else {
                if (file == null || entryName == null) return null
                Resource.INSTANCE.fileOf(file.toPath()).resolve(entryName).toFile()
            }
        } else if (classSource is DexClassSource) {
            if (ModuleUtil.module_mode()) {
                null
            } else {
                val classFileName = "${className.replace('.', '/')}.class"
                val pathField = classSource.javaClass.getDeclaredField("path").apply { isAccessible = true }
                val file = pathField.get(classSource) as? File ?: return null

                val path = file.toPath().toAbsolutePath().normalize()
                val cachedPath = OptionalsKt.getOrNull(sootClass2classFileCache.get(path)) ?: return null
                lookupInArchive(Resource.INSTANCE.fileOf(cachedPath), classFileName)
            }
        } else {
            null
        }
    }

    fun cleanUp() {
        sootClass2classFileCache.cleanUp()
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
        return "Finished (in ${LoggingKt.elapsedSecFrom(`$startTime`)}): ${`$msg`} "
    }

    @JvmStatic
    fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$3`(
        `$startTime`: LocalDateTime,
        `$msg`: String
    ): Any {
        return "Finished (in ${LoggingKt.elapsedSecFrom(`$startTime`)}): ${`$msg`} <Nothing>"
    }

    @JvmStatic
    fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$4`(
        `$startTime`: LocalDateTime,
        `$msg`: String,
        `$t`: Throwable
    ): Any {
        return "Finished (in ${LoggingKt.elapsedSecFrom(`$startTime`)}): ${`$msg`} :: EXCEPTION :: "
    }

    @JvmStatic
    fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$5`(
        `$startTime`: LocalDateTime,
        `$msg`: String,
        `$res`: ObjectRef
    ): Any {
        return "Finished (in ${LoggingKt.elapsedSecFrom(`$startTime`)}): ${`$msg`} "
    }

    @JvmStatic
    fun `SootUtils$dex2jar$lambda$2$$inlined$bracket$default$6`(
        `$startTime`: LocalDateTime,
        `$msg`: String
    ): Any {
        return "Finished (in ${LoggingKt.elapsedSecFrom(`$startTime`)}): ${`$msg`} <Nothing>"
    }

    @JvmStatic
    fun `sootClass2classFileCache$lambda$6$lambda$5`(`$dexSource`: Path): Any {
        return "failed to convert dex: ${`$dexSource`} to jar file."
    }

    @JvmStatic
    fun `sootClass2classFileCache$lambda$6`(dexSource: Path): Optional<Path> {
        try {
            val output = Resource.INSTANCE.getZipExtractOutputDir().resolve("dex2jar")
                .resolve("${PathsKt.getName(dexSource).substringBeforeLast(".")}-${Math.abs(dexSource.toString().hashCode() + 1)}.jar")
            return Optional.of(dex2jar(dexSource, output))
        } catch (e: Exception) {
            logger.warn(e) { `sootClass2classFileCache$lambda$6$lambda$5`(dexSource) }
            return Optional.empty()
        }
    }
}