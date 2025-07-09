package cn.sast.framework.report

import cn.sast.api.report.Report
import cn.sast.common.*
import cn.sast.framework.report.sarif.*
import cn.sast.framework.result.OutputType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.Closeable
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.deleteIfExists

/**
 * SARIF + **打包源码到单一 JAR**：
 * 把相关源码收集后压缩进 `src_root.jar`；SARIF 中映射到
 * `%SRCROOT%/…`。
 */
class SarifDiagnosticsPack(
    outputDir: IResDirectory,
    private val srcRootMapKey: String = "SRCROOT",
    srcRootMapValue: String = "%SRCROOT%",
    private val srcJarName: String = "src_root",
    type: OutputType = OutputType.SARIF_PACK_SRC
) : SarifDiagnostics(outputDir, type), Closeable {

    private val logger = KotlinLogging.logger {}

    private val entries = ConcurrentHashMap<String, IResFile>()

    private val uriBaseIds: Map<String, UriBase> = mapOf(
        srcRootMapKey to UriBase(
            id = srcRootMapValue,
            description = Description(
                "Replace $srcRootMapValue with file://…/${srcJarName}.jar/$srcJarName/"
            )
        )
    )

    /* ----------------------- JAR 初始化 ----------------------- */

    private lateinit var srcJarPath: IResFile
    private lateinit var jar: JarMerger

    override suspend fun init() {
        super.init()
        withContext(Dispatchers.IO) {
            srcJarPath = outputDir.resolve("$srcJarName.jar").toFile()
            srcJarPath.path.deleteIfExists()
            jar = JarMerger(srcJarPath.path)
        }
    }

    /* -------------------- Impl / Copy ----------------------- */

    override fun getSarifDiagnosticsImpl(
        metadata: MetaData,
        locator: IProjectFileLocator
    ) = PackImpl(metadata, locator)

    override fun close() {
        if (entries.isEmpty()) return
        logger.info { "$type: compressing ${entries.size} files …" }
        val started = LocalDateTime.now()
        var err = 0
        entries.forEach { (entry, file) ->
            try {
                jar.addFile(entry, file.path)
            } catch (e: Exception) {
                if (++err <= 5) logger.warn(e) { "jar add failed: $entry" }
            }
        }
        jar.close()
        if (err > 0) logger.warn { "$type: $err errors generated during packing" }
        logger.info { "$type: done in ${java.time.Duration.between(started, LocalDateTime.now())}" }
    }

    /* =================================================================== */

    inner class PackImpl(
        metadata: MetaData,
        locator: IProjectFileLocator
    ) : SarifDiagnosticsImpl(metadata, locator) {

        override fun getRun(reports: List<Report>): Run =
            Run.copy(
                super.getRun(reports),
                uriBaseIds = this@SarifDiagnosticsPack.uriBaseIds
            )

        override fun ArtifactLocation(file: IResFile): ArtifactLocation {
            val entry = absPathMapToFolder(file)
            entries.putIfAbsent(entry, file)
            return super.ArtifactLocation(file).copy(
                uriBaseId = srcRootMapKey
            )
        }

        private fun absPathMapToFolder(file: IResFile): String =
            file.absoluteNormalize.path
                .removePrefix("/")
                .replace("\\", "/")
                .replace(":", "")
    }
}
