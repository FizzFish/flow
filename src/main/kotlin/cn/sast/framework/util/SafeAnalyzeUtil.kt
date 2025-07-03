package cn.sast.framework.util

import cn.sast.framework.engine.PreAnalysisImpl
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ICheckPoint
import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nSafeAnalyzeUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeAnalyzeUtil.kt\ncn/sast/framework/util/SafeAnalyzeUtil\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,57:1\n1#2:58\n*E\n"])
class SafeAnalyzeUtil(errorLimitCount: Int, errorCount: Int = 0) {
    private val errorLimitCount: Int = errorLimitCount
    private var errorCount: Int = errorCount

    context(CoroutineScope)
    suspend fun <C : ICheckPoint, T> safeAnalyze(point: C, block: (C, Continuation<T>) -> Any?): T? {
        val continuation = object : ContinuationImpl(this@safeAnalyze as Continuation<Any?>) {
            var L$0: Any? = null
            var L$1: Any? = null
            var L$2: Any? = null
            var label: Int = 0

            @Nullable
            override fun invokeSuspend(@NotNull result: Any): Any? {
                this.result = result
                label = label or Int.MIN_VALUE
                return this@SafeAnalyzeUtil.safeAnalyze(point, block, this)
            }
        }

        val result = continuation.result
        val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
        when (continuation.label) {
            0 -> {
                ResultKt.throwOnFailure(result)
                try {
                    continuation.L$0 = this
                    continuation.L$1 = this@CoroutineScope
                    continuation.L$2 = point
                    continuation.label = 1
                    val value = block(point, continuation)
                    if (value === suspended) return suspended as T?
                } catch (t: Throwable) {
                    onCheckerError(t) { safeAnalyze$lambda$2(t, this, this@CoroutineScope, point) }
                    return null
                }
            }
            1 -> {
                val point = continuation.L$2 as ICheckPoint
                val scope = continuation.L$1 as CoroutineScope
                val instance = continuation.L$0 as SafeAnalyzeUtil
                try {
                    ResultKt.throwOnFailure(result)
                } catch (t: Throwable) {
                    instance.onCheckerError(t) { safeAnalyze$lambda$2(t, instance, scope, point) }
                    return null
                }
            }
            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
        }

        return try {
            if (result != Unit) result as? T else null
        } catch (t: Throwable) {
            onCheckerError(t) { safeAnalyze$lambda$2(t, this, this@CoroutineScope, point) }
            null
        }
    }

    context(CheckerUnit)
    suspend fun <T> safeRunInSceneAsync(block: (Continuation<T>) -> Any?): T? {
        val continuation = object : ContinuationImpl(this@safeRunInSceneAsync as Continuation<Any?>) {
            var L$0: Any? = null
            var L$1: Any? = null
            var label: Int = 0

            @Nullable
            override fun invokeSuspend(@NotNull result: Any): Any? {
                this.result = result
                label = label or Int.MIN_VALUE
                return this@SafeAnalyzeUtil.safeRunInSceneAsync(block, this)
            }
        }

        val result = continuation.result
        val suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED()
        when (continuation.label) {
            0 -> {
                ResultKt.throwOnFailure(result)
                try {
                    continuation.L$0 = this
                    continuation.L$1 = this@CheckerUnit
                    continuation.label = 1
                    val value = block(continuation)
                    if (value === suspended) return suspended as T?
                } catch (e: Exception) {
                    onCheckerError(e) { safeRunInSceneAsync$lambda$4(e, this@CheckerUnit) }
                    return null
                }
            }
            1 -> {
                val scope = continuation.L$1 as CheckerUnit
                val instance = continuation.L$0 as SafeAnalyzeUtil
                try {
                    ResultKt.throwOnFailure(result)
                } catch (e: Exception) {
                    instance.onCheckerError(e) { safeRunInSceneAsync$lambda$4(e, scope) }
                    return null
                }
            }
            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
        }

        return try {
            result as? T
        } catch (e: Exception) {
            onCheckerError(e) { safeRunInSceneAsync$lambda$4(e, this@CheckerUnit) }
            null
        }
    }

    private fun onCheckerError(t: Throwable, onCheckerError: () -> Unit) {
        when {
            t is CancellationException -> throw t
            t !is Exception && t !is NotImplementedError -> throw t
            else -> onCheckerError()
        }
    }

    companion object {
        @JvmStatic
        fun safeAnalyze$lambda$2$lambda$1(point: ICheckPoint): Any {
            return "When analyzing this location: $point, please file this bug to us"
        }

        @JvmStatic
        fun safeAnalyze$lambda$2(t: Throwable, this$0: SafeAnalyzeUtil, scope: CoroutineScope, point: ICheckPoint) {
            PreAnalysisImpl.Companion.getKLogger().error(t) { safeAnalyze$lambda$2$lambda$1(point) }
            this$0.errorCount++
            if (this$0.errorCount > this$0.errorLimitCount) {
                CoroutineScopeKt.cancel(scope)
            }
        }

        @JvmStatic
        fun safeRunInSceneAsync$lambda$4$lambda$3(scope: CheckerUnit): Any {
            return "Occur a exception while call $scope:runInSceneAsync, please file this bug to us"
        }

        @JvmStatic
        fun safeRunInSceneAsync$lambda$4(t: Exception, scope: CheckerUnit) {
            PreAnalysisImpl.Companion.getKLogger().error(t) { safeRunInSceneAsync$lambda$4$lambda$3(scope) }
        }
    }
}