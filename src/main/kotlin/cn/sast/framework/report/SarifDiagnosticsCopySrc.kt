package cn.sast.framework.report

import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.SarifDiagnostics.SarifDiagnosticsImpl
import cn.sast.framework.report.sarif.ArtifactLocation
import cn.sast.framework.report.sarif.Description
import cn.sast.framework.report.sarif.Run
import cn.sast.framework.report.sarif.UriBase
import cn.sast.framework.result.OutputType
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.Arrays
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nSarifDiagnosticsCopySrc.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SarifDiagnosticsCopySrc.kt\ncn/sast/framework/report/SarifDiagnosticsCopySrc\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,85:1\n49#2,13:86\n62#2,11:101\n216#3,2:99\n*S KotlinDebug\n*F\n+ 1 SarifDiagnosticsCopySrc.kt\ncn/sast/framework/report/SarifDiagnosticsCopySrc\n*L\n62#1:86,13\n62#1:101,11\n63#1:99,2\n*E\n"])
class SarifDiagnosticsCopySrc(
    outputDir: IResDirectory,
    private val sourceJarRootMapKey: String = "SRCROOT",
    sourceJarRootMapValue: String = "%SRCROOT%",
    private val sourceJarFileName: String = "src_root",
    type: OutputType = OutputType.SarifCopySrc
) : SarifDiagnostics(outputDir, type), Closeable {
    private val sourceRoot: IResDirectory
    val originalUriBaseIds: Map<String, UriBase>
    val entriesMap: ConcurrentHashMap<String, IResFile> = ConcurrentHashMap()

    init {
        sourceRoot = outputDir.resolve(sourceJarFileName).toDirectory()
        originalUriBaseIds = mapOf(
            sourceJarRootMapKey to UriBase(
                sourceJarRootMapValue,
                Description(
                    "The path $sourceJarRootMapValue should be replaced with path where be mapped to the virtual path ${sourceRoot.path.toUri()}"
                )
            )
        )
    }

    override fun getSarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator): SarifDiagnosticsImpl {
        return SarifDiagnosticsPackImpl(metadata, locator)
    }

    override fun close() {
        val errorCnt = IntRef()
        val loggerWithLogMethod = LoggingKt.info(logger)
        val msg = "${type}: copying ..."
        loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsCopySrc$close$$inlined$bracket$default$1(msg))
        val startTime = LocalDateTime.now()
        var alreadyLogged = false
        val res = ObjectRef<Maybe<Unit>>().apply { element = Maybe.empty() }

        try {
            try {
                for (element in entriesMap.entries) {
                    val entry = element.key
                    val file = element.value
                    val target = sourceRoot.resolve(entry).path

                    try {
                        val parent = target.parent
                            ?: throw IllegalStateException("output not allow here: $target")

                        if (!Files.exists(parent, *LinkOption.values())) {
                            Files.createDirectories(parent)
                        }

                        Files.copy(file.path, target)
                    } catch (e: Exception) {
                        errorCnt.element++
                        if (errorCnt.element < 5) {
                            logger.warn(e) { "An error occurred" }
                        }
                    }
                }

                res.element = Maybe(Unit)
                res.element.getOrThrow()
            } catch (t: Throwable) {
                loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsCopySrc$close$$inlined$bracket$default$4(startTime, msg, t))
                alreadyLogged = true
                throw t
            }
        } catch (t: Throwable) {
            if (!alreadyLogged) {
                if (res.element.hasValue) {
                    loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsCopySrc$close$$inlined$bracket$default$5(startTime, msg, res))
                } else {
                    loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsCopySrc$close$$inlined$bracket$default$6(startTime, msg))
                }
            }
        }

        if (res.element.hasValue) {
            loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsCopySrc$close$$inlined$bracket$default$2(startTime, msg, res))
        } else {
            loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsCopySrc$close$$inlined$bracket$default$3(startTime, msg))
        }

        if (errorCnt.element > 0) {
            logger.warn { "${type}: A total of ${errorCnt.element} errors were generated" }
        }
    }

    companion object {
        val logger: KLogger
    }

    inner class SarifDiagnosticsPackImpl(
        metadata: MetaData,
        locator: IProjectFileLocator
    ) : SarifDiagnosticsImpl(this@SarifDiagnosticsCopySrc, metadata, locator) {
        val file2uri: String
            get() {
                val entry = getAbsPathMapToFolder(file2uri)
                this@SarifDiagnosticsCopySrc.entriesMap.putIfAbsent(entry, file2uri)
                return entry
            }

        override fun getArtifactLocation(file: IResFile): ArtifactLocation {
            return ArtifactLocation.copy$default(
                super.getArtifactLocation(file),
                null,
                sourceJarRootMapKey,
                1,
                null
            )
        }

        override fun getRun(reports: List<Report>): Run {
            return Run.copy$default(
                super.getRun(reports),
                null,
                originalUriBaseIds,
                null,
                null,
                13,
                null
            )
        }
    }
}