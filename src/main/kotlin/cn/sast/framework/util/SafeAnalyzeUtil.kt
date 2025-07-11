package cn.sast.framework.util

import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ICheckPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import mu.KotlinLogging
import kotlin.coroutines.coroutineContext

/**
 * Executes analyzer logic *safely*: exceptions below [errorLimit] are logged & swallowed.
 * When the limit is exceeded the surrounding [CoroutineScope] is *cancelled* to abort further work.
 */
class SafeAnalyzeUtil(private val errorLimit: Int, private var errorCount: Int = 0) {

    private val logger = KotlinLogging.logger {}

    /** Run [block] with *point*; return *null* on handled failure. */
    suspend fun <C : ICheckPoint, T> CoroutineScope.safeAnalyze(point: C, block: suspend (C) -> T): T? =
        try { block(point) } catch (e: Throwable) {
            onError(e) { "When analyzing $point please file this bug 😊" }; null
        }

    /** Run [block] inside *CheckerUnit.runInSceneAsync* with friendly error handling. */
    suspend fun <T> CheckerUnit.safeRunInSceneAsync(block: suspend () -> T): T? =
        try { block() } catch (e: Throwable) {
            onError(e) { "Exception in ${this::class.simpleName}.runInSceneAsync – please file bug" }; null
        }

    private fun onError(t: Throwable, msg: () -> String) {
        if (t is kotlinx.coroutines.CancellationException) throw t // propagate cancels
        if (t !is Exception && t !is NotImplementedError) throw t   // fatal -> rethrow

        logger.error(t) { msg() }
        if (++errorCount > errorLimit) {
            logger.error { "Error‑limit $errorLimit exceeded – cancelling analysis scope" }
            coroutineContext[CoroutineScope]?.cancel()
        }
    }
}