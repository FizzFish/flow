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

/**
 * SARIF + **复制源码树**：
 * 将所有涉及的源码文件拷贝到 `src_root/` 目录，且在 SARIF
 * 内把绝对路径映射到 `%SRCROOT%`。
 */
class SarifDiagnosticsCopySrc(
    outputDir: IResDirectory,
    private val srcRootMapKey: String = "SRCROOT",
    srcRootMapValue: String = "%SRCROOT%",
    private val srcRootFolder: String = "src_root",
    type: OutputType = OutputType.SARIF_COPY_SRC
) : SarifDiagnostics(outputDir, type), Closeable {

    private val logger = KotlinLogging.logger {}

    /** `<virtual-path, realFile>` */
    val entries = ConcurrentHashMap<String, IResFile>()

    /** 输出目录下的源码根 */
    private val srcRoot: IResDirectory = outputDir.resolve(srcRootFolder).toDirectory()

    /** SARIF run-level uriBaseIds */
    private val uriBaseIds: Map<String, UriBase> = mapOf(
        srcRootMapKey to UriBase(
            id = srcRootMapValue,
            description = Description(
                "Replace $srcRootMapValue with path mapped to ${srcRoot.path.toUri()}"
            )
        )
    )

    /* ---------- Impl ---------- */

    override fun getSarifDiagnosticsImpl(
        metadata: MetaData,
        locator: IProjectFileLocator
    ) = PackImpl(metadata, locator)

    /** 拷贝源码并关闭 */
    override fun close() {
        if (entries.isEmpty()) return
        logger.info { "$type: copying ${entries.size} files …" }
        val started = LocalDateTime.now()
        var err = 0

        entries.forEach { (virtualPath, real) ->
            val target = srcRoot.resolve(virtualPath).path
            try {
                Files.createDirectories(target.parent)
                Files.copy(real.path, target)
            } catch (e: Exception) {
                if (++err <= 5) logger.warn(e) { "copy failed: $real → $target" }
            }
        }

        if (err > 0) logger.warn { "$type: $err errors generated during copying" }
        logger.info { "$type: done in ${java.time.Duration.between(started, LocalDateTime.now())}" }
    }

    /* =================================================================== */

    inner class PackImpl(
        metadata: MetaData,
        locator: IProjectFileLocator
    ) : SarifDiagnosticsImpl(metadata, locator) {

        /** 把绝对路径映射成 `%SRCROOT%/…` 的虚拟路径 */
        override fun getRun(reports: List<Report>): Run =
            Run.copy(
                super.getRun(reports),
                uriBaseIds = this@SarifDiagnosticsCopySrc.uriBaseIds
            )

        override fun ArtifactLocation(file: IResFile): ArtifactLocation {
            val virtual = absPathMapToFolder(file)
            entries.putIfAbsent(virtual, file)
            return super.ArtifactLocation(file).copy(
                uriBaseId = srcRootMapKey
            )
        }

        /** 将绝对路径转化为 jar/dir 中的相对路径 */
        private fun absPathMapToFolder(file: IResFile): String =
            file.absoluteNormalize.path
                .removePrefix("/")
                .replace("\\", "/")
                .replace(":", "")
    }
}
