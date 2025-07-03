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
import java.io.Closeable
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/SourceFileCheckPoint\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,760:1\n1#2:761\n1557#3:762\n1628#3,3:763\n*S KotlinDebug\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/SourceFileCheckPoint\n*L\n747#1:762\n747#1:763,3\n*E\n"])
class SourceFileCheckPoint(
    private val sFile: IResFile,
    val mainConfig: MainConfig
) : CheckPoint, ISourceFileCheckPoint, Closeable {
    val path: Path = sFile.getPath()
    private val archiveFile$delegate = lazy { archiveFile_delegate$lambda$0(this) }
    val file: IBugResInfo = FileResInfo(sFile)

    val relativePath: RelativePath
        get() = mainConfig.tryGetRelativePath(sFile)

    val uri: URI
        get() = sFile.getUri()

    val archiveFile: Path?
        get() = archiveFile$delegate.value

    internal val env: DefaultEnv
        get() = DefaultEnv(Region.Companion.ERROR.mutable, null, null, null, null, null, null, null, null, 510, null)

    private var bytes: ByteArray? = null
    private var text: String? = null
    private var lines: List<IndexedValue<String>>? = null

    override suspend fun readAllBytes(): ByteArray {
        return bytes ?: BuildersKt.withContext(Dispatchers.IO, Function2<CoroutineScope, Continuation<ByteArray>, Any> { _, continuation ->
            object : ContinuationImpl(continuation as Continuation<Any>) {
                var label = 0

                override fun invokeSuspend(result: Any): Any {
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            val bytes = ResourceKt.readAllBytes(this@SourceFileCheckPoint.sFile)
                            this@SourceFileCheckPoint.bytes = bytes
                            return bytes
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }
                }
            }.invokeSuspend(Unit)
        )
    }

    override suspend fun text(): String? {
        return text ?: run {
            try {
                val bytes = readAllBytes()
                String(bytes, Charsets.UTF_8).also { text = it }
            } catch (e: IOException) {
                logger.error("read config file ${path} failed")
                null
            }
        }
    }

    override suspend fun lines(): List<IndexedValue<String>> {
        return lines ?: run {
            val text = text() ?: return emptyList()
            StringsKt.split(text, "\n", false)
                .withIndex()
                .map { IndexedValue(it.index + 1, it.value) }
                .also { lines = it }
        }
    }

    override fun close() {
        text = null
        lines = null
        bytes = null
    }

    override fun getFilename(): String {
        return ISourceFileCheckPoint.DefaultImpls.getFilename(this)
    }

    companion object {
        private val logger: KLogger

        @JvmStatic
        private fun archiveFile_delegate$lambda$0(this$0: SourceFileCheckPoint): Path? {
            return if (this$0.sFile.isJarScheme()) {
                this$0.sFile.getSchemePath().toAbsolutePath()
            } else {
                null
            }
        }

        @JvmStatic
        private fun logger$lambda$7() {
            // No-op
        }
    }
}