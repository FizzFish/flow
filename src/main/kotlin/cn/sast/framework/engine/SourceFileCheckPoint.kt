package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfig.RelativePath
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.FileResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import com.feysh.corax.config.api.ISourceFileCheckPoint
import com.feysh.corax.config.api.report.Region
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KLogger
import mu.KotlinLogging
import java.io.Closeable
import java.io.IOException
import java.net.URI
import java.nio.file.Path

/**
 * Lazily reads the contents of a source file and exposes helpers used by the Corax runtime.
 */
class SourceFileCheckPoint(
    private val sFile: IResFile,
    val mainConfig: MainConfig
) : CheckPoint, ISourceFileCheckPoint, Closeable {

    val path: Path = sFile.path
    private val archiveFileDelegate by lazy { deriveArchivePath() }
    override val file: IBugResInfo = FileResInfo(sFile)

    val relativePath: RelativePath
        get() = mainConfig.tryGetRelativePath(sFile)

    val uri: URI
        get() = sFile.uri

    val archiveFile: Path?
        get() = archiveFileDelegate

    /** Whole‑file default region. */
    internal val env: DefaultEnv
        get() = DefaultEnv(
            Region.ERROR.mutable,
            null, null, null, null, null, null, null, null
        )

    private var bytes: ByteArray? = null
    private var textCache: String? = null
    private var lineCache: List<IndexedValue<String>>? = null

    override suspend fun readAllBytes(): ByteArray = bytes ?: withContext(Dispatchers.IO) {
        ResourceKt.readAllBytes(sFile).also { bytes = it }
    }

    override suspend fun text(): String? = textCache ?: try {
        val data = readAllBytes()
        String(data, Charsets.UTF_8).also { textCache = it }
    } catch (e: IOException) {
        logger.error(e) { "Failed to read source file $path" }
        null
    }

    override suspend fun lines(): List<IndexedValue<String>> =
        lineCache ?: run {
            val content = text() ?: return emptyList()
            content.lineSequence()
                .mapIndexed { index, value -> IndexedValue(index + 1, value) }
                .toList()
                .also { lineCache = it }
        }

    override fun close() {
        bytes = null
        textCache = null
        lineCache = null
    }

    override fun getFilename(): String =
        ISourceFileCheckPoint.DefaultImpls.getFilename(this)

    private fun deriveArchivePath(): Path? =
        if (sFile.isJarScheme()) sFile.schemePath.toAbsolutePath() else null

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
