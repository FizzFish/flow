package cn.sast.framework.report

import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.JarMerger
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.SarifDiagnostics.SarifDiagnosticsImpl
import cn.sast.framework.report.sarif.ArtifactLocation
import cn.sast.framework.report.sarif.Description
import cn.sast.framework.report.sarif.Run
import cn.sast.framework.report.sarif.UriBase
import cn.sast.framework.result.OutputType
import java.io.Closeable
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nSarifDiagnosticsPack.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SarifDiagnosticsPack.kt\ncn/sast/framework/report/SarifDiagnosticsPack\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,92:1\n49#2,13:93\n62#2,11:108\n216#3,2:106\n*S KotlinDebug\n*F\n+ 1 SarifDiagnosticsPack.kt\ncn/sast/framework/report/SarifDiagnosticsPack\n*L\n71#1:93,13\n71#1:108,11\n72#1:106,2\n*E\n"])
class SarifDiagnosticsPack(
    outputDir: IResDirectory,
    sourceJarRootMapKey: String = "SRCROOT",
    sourceJarRootMapValue: String = "%SRCROOT%",
    sourceJarFileName: String = "src_root",
    type: OutputType = OutputType.SarifPackSrc
) : SarifDiagnostics(outputDir, type), Closeable {
    private val sourceJarRootMapKey: String
    private val sourceJarFileName: String
    private lateinit var sourceJarPath: IResFile
    private lateinit var sourceJar: JarMerger
    val originalUriBaseIds: Map<String, UriBase>
    val entriesMap: ConcurrentHashMap<String, IResFile>

    init {
        this.sourceJarRootMapKey = sourceJarRootMapKey
        this.sourceJarFileName = sourceJarFileName
        this.originalUriBaseIds = mapOf(
            sourceJarRootMapKey to UriBase(
                sourceJarRootMapValue,
                Description(
                    "Should replace $sourceJarRootMapValue with file:///{absolute-uncompressed-path-of-${this.sourceJarFileName}.jar}/${this.sourceJarFileName}/"
                )
            )
        )
        this.entriesMap = ConcurrentHashMap()
    }

    override suspend fun init(completion: Continuation<Unit>): Any? {
        val continuation = if (completion is ContinuationImpl && (completion.label and Int.MIN_VALUE) != 0) {
            completion.label -= Int.MIN_VALUE
            completion
        } else {
            object : ContinuationImpl(completion) {
                var L$0: Any? = null
                var label = 0

                override fun invokeSuspend(result: Any?): Any? {
                    this.result = result
                    label = label or Int.MIN_VALUE
                    return this@SarifDiagnosticsPack.init(this)
                }
            }
        }

        val result = continuation.result
        val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
        when (continuation.label) {
            0 -> {
                ResultKt.throwOnFailure(result)
                continuation.L$0 = this
                continuation.label = 1
                if (super.init(continuation) === suspended) {
                    return suspended
                }
            }
            1 -> {
                ResultKt.throwOnFailure(result)
            }
            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
        }

        sourceJarPath = getOutputDir().resolve("${sourceJarFileName}.jar").toFile()
        Files.deleteIfExists(sourceJarPath.path)
        sourceJar = JarMerger(sourceJarPath.path, null, 2, null)
        return Unit
    }

    override fun getSarifDiagnosticsImpl(metadata: MetaData, locator: IProjectFileLocator): SarifDiagnosticsImpl {
        return SarifDiagnosticsPackImpl(this, metadata, locator)
    }

    override fun close() {
        val errorCnt = IntRef()
        val loggerWithLogMethod = LoggingKt.info(logger)
        val msg = "${getType()}: Compressing ..."
        loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsPack$close$$inlined$bracket$default$1(msg))
        val startTime = LocalDateTime.now()
        var alreadyLogged = false
        val res = ObjectRef<Maybe<Unit>>()
        res.element = Maybe.empty()

        try {
            try {
                for (element in entriesMap.entries) {
                    val entry = element.key
                    val file = element.value

                    try {
                        sourceJar.addFile(entry, file.path)
                    } catch (e: Exception) {
                        errorCnt.element++
                        if (errorCnt.element < 5) {
                            logger.warn(e) { "An error occurred" }
                        }
                    }
                }
                res.element = Maybe(Unit)
                res.element?.getOrThrow()
            } catch (t: Throwable) {
                loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsPack$close$$inlined$bracket$default$4(startTime, msg, t))
                alreadyLogged = true
                throw t
            }
        } catch (t: Throwable) {
            if (!alreadyLogged) {
                if (res.element?.hasValue == true) {
                    loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsPack$close$$inlined$bracket$default$5(startTime, msg, res))
                } else {
                    loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsPack$close$$inlined$bracket$default$6(startTime, msg))
                }
            }
        }

        if (res.element?.hasValue == true) {
            loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsPack$close$$inlined$bracket$default$2(startTime, msg, res))
        } else {
            loggerWithLogMethod.logMethod.invoke(SarifDiagnosticsPack$close$$inlined$bracket$default$3(startTime, msg))
        }

        if (errorCnt.element > 0) {
            logger.warn { "${getType()}: A total of ${errorCnt.element} errors were generated" }
        }

        sourceJar.close()
    }

    companion object {
        val logger: KLogger
    }

    inner class SarifDiagnosticsPackImpl(
        private val this$0: SarifDiagnosticsPack,
        metadata: MetaData,
        locator: IProjectFileLocator
    ) : SarifDiagnosticsImpl(this$0, metadata, locator) {
        val file2uri: String
            get() {
                val entry = getAbsPathMapToFolder(this)
                this@SarifDiagnosticsPack.entriesMap.putIfAbsent(entry, this)
                return entry
            }

        override fun getArtifactLocation(file: IResFile): ArtifactLocation {
            return ArtifactLocation.copy$default(super.getArtifactLocation(file), null, this@SarifDiagnosticsPack.sourceJarRootMapKey, 1, null)
        }

        override fun getRun(reports: List<Report>): Run {
            return Run.copy$default(super.getRun(reports), null, this@SarifDiagnosticsPack.originalUriBaseIds, null, null, 13, null)
        }
    }
}