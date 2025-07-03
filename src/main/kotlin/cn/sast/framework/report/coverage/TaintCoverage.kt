package cn.sast.framework.report.coverage

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nCoverageCompnment.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoverageCompnment.kt\ncn/sast/framework/report/coverage/TaintCoverage\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,72:1\n1#2:73\n*E\n"])
public class TaintCoverage : Coverage {
    private fun copyResource(name: String, to: IResFile) {
        val path: Path = to.getPath()
        val options: Array<OpenOption> = emptyArray()
        val outputStream: OutputStream = Files.newOutputStream(path, Arrays.copyOf(options, options.size))
        val closeable: Closeable = outputStream
        var exception: Throwable? = null
        
        try {
            val inputStream: InputStream = TaintCoverage::class.java.getResourceAsStream(name)
            ByteStreamsKt.copyTo$default(inputStream, outputStream, 0, 2, null)
        } catch (t: Throwable) {
            exception = t
            throw t
        } finally {
            closeable.closeFinally(exception)
        }
    }

    public fun changeColor(reportOutputRoot: IResDirectory) {
        this.copyResource("/jacoco/taint-report.css", reportOutputRoot.resolve("jacoco-resources").resolve("report.css").toFile())
        this.copyResource("/jacoco/greenbar.gif", reportOutputRoot.resolve("jacoco-resources").resolve("redbar.gif").toFile())
        this.copyResource("/jacoco/redbar.gif", reportOutputRoot.resolve("jacoco-resources").resolve("greenbar.gif").toFile())
    }

    public override suspend fun flushCoverage(locator: IProjectFileLocator, outputDir: IResDirectory, encoding: Charset) {
        val continuation = object : ContinuationImpl(this as Continuation<Unit>, null) {
            var L$0: Any? = null
            var L$1: Any? = null
            var label: Int = 0

            override fun invokeSuspend(result: Any?): Any? {
                this.result = result
                this.label = this.label or Int.MIN_VALUE
                return this@TaintCoverage.flushCoverage(null, null, null, this)
            }
        }

        val result = continuation.result
        val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
        when (continuation.label) {
            0 -> {
                ResultKt.throwOnFailure(result)
                continuation.L$0 = this
                continuation.L$1 = outputDir
                continuation.label = 1
                if (super.flushCoverage(locator, outputDir, encoding, continuation) === suspended) {
                    return suspended
                }
            }
            1 -> {
                outputDir = continuation.L$1 as IResDirectory
                this@TaintCoverage = continuation.L$0 as TaintCoverage
                ResultKt.throwOnFailure(result)
            }
            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
        }

        this.changeColor(outputDir)
        return Unit
    }
}