package cn.sast.framework.report.coverage

import cn.sast.api.report.CoverData
import cn.sast.api.report.CoverInst
import cn.sast.api.report.CoverTaint
import cn.sast.api.report.ICoverageCollector
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Deferred
import org.jacoco.core.analysis.ICounter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

public class JacocoCompoundCoverage(
    private val locator: IProjectFileLocator,
    private val taintCoverage: Coverage = TaintCoverage() as Coverage,
    private val executionCoverage: Coverage = Coverage(),
    public val enableCoveredTaint: Boolean = false
) : ICoverageCollector {

    public override fun cover(coverInfo: CoverData) {
        when (coverInfo) {
            is CoverTaint -> {
                taintCoverage.coverByQueue(coverInfo.className, coverInfo.lineNumber)
            }
            is CoverInst -> {
                executionCoverage.coverByQueue(coverInfo.className, coverInfo.lineNumber)
            }
            else -> throw NoWhenBranchMatchedException()
        }
    }

    public override suspend fun flush(output: IResDirectory, sourceEncoding: Charset) {
        return CoroutineScopeKt.coroutineScope { scope, continuation ->
            object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                private var label = 0
                private var L$0: Any? = null

                override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                    return invokeSuspend(Unit)
                }

                override fun invokeSuspend(result: Any): Any? {
                    val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
                    when (label) {
                        0 -> {
                            ResultKt.throwOnFailure(result)
                            val taintTask = BuildersKt.async(scope) { _, continuation ->
                                object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                                    private var label = 0

                                    override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                                        return invokeSuspend(Unit)
                                    }

                                    override fun invokeSuspend(result: Any): Any? {
                                        when (label) {
                                            0 -> {
                                                ResultKt.throwOnFailure(result)
                                                label = 1
                                                val res = this@JacocoCompoundCoverage.flush(
                                                    taintCoverage,
                                                    output.resolve("taint-coverage").toDirectory(),
                                                    sourceEncoding,
                                                    this as Continuation<Unit>
                                                )
                                                if (res == suspended) return suspended
                                            }
                                            1 -> {
                                                ResultKt.throwOnFailure(result)
                                            }
                                            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                                        }
                                        return Unit
                                    }
                                }.invoke(scope, continuation)
                            }
                            val execTask = BuildersKt.async(scope) { _, continuation ->
                                object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
                                    private var label = 0

                                    override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                                        return invokeSuspend(Unit)
                                    }

                                    override fun invokeSuspend(result: Any): Any? {
                                        when (label) {
                                            0 -> {
                                                ResultKt.throwOnFailure(result)
                                                label = 1
                                                val res = this@JacocoCompoundCoverage.flush(
                                                    executionCoverage,
                                                    output.resolve("code-coverage").toDirectory(),
                                                    sourceEncoding,
                                                    this as Continuation<Unit>
                                                )
                                                if (res == suspended) return suspended
                                            }
                                            1 -> {
                                                ResultKt.throwOnFailure(result)
                                            }
                                            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                                        }
                                        return Unit
                                    }
                                }.invoke(scope, continuation)
                            }
                            L$0 = execTask
                            label = 1
                            val awaitResult = taintTask.await(this as Continuation<Unit>)
                            if (awaitResult == suspended) return suspended
                        }
                        1 -> {
                            val execTask = L$0 as Deferred<*>
                            ResultKt.throwOnFailure(result)
                        }
                        2 -> {
                            ResultKt.throwOnFailure(result)
                            return Unit
                        }
                        else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                    }

                    label = 2
                    val awaitResult = (L$0 as Deferred<*>).await(this as Continuation<Unit>)
                    return if (awaitResult == suspended) suspended else Unit
                }
            }.invoke(scope, continuation)
        }
    }

    public override suspend fun getCoveredLineCounter(allSourceFiles: Set<IResFile>, encoding: Charset): ICounter {
        return suspendCoroutine { continuation ->
            val result = executionCoverage.calculateSourceCoverage(locator, encoding, continuation)
            if (result is ICounter) {
                continuation.resumeWith(Result.success(result))
            } else {
                (result as SourceCoverage).calculateCoveredRatio(allSourceFiles)
            }
        }
    }

    private suspend fun flush(coverage: Coverage, out: IResDirectory, sourceEncoding: Charset, continuation: Continuation<Unit>): Any? {
        return coverage.flushCoverage(locator, out, sourceEncoding, continuation)
    }

    public fun copy(
        locator: IProjectFileLocator = this.locator,
        taintCoverage: Coverage = this.taintCoverage,
        executionCoverage: Coverage = this.executionCoverage,
        enableCoveredTaint: Boolean = this.enableCoveredTaint
    ): JacocoCompoundCoverage {
        return JacocoCompoundCoverage(locator, taintCoverage, executionCoverage, enableCoveredTaint)
    }
}