package cn.sast.framework

import cn.sast.api.AnalyzerEnv
import cn.sast.api.util.IMonitor
import cn.sast.framework.entries.IEntryPointProvider
import kotlinx.coroutines.*
import mu.KotlinLogging
import soot.SootMethod

/**
 * 负责调度多批分析任务的协程执行器。
 *
 * @param numThreads  线程并行度（0 表示使用 [Dispatchers.Default]）
 * @param sootCtx     Soot 执行上下文
 * @param monitor     监控/计时器实现
 */
class AnalyzeTaskRunner(
    private val numThreads: Int,
    private val sootCtx: SootCtx,
    private val monitor: IMonitor,
) {

    /** provider → 待执行的分析列表 */
    private val analysisPasses = mutableMapOf<IEntryPointProvider, MutableList<Analysis>>()

    /** 注册一条分析流程（before → analysis → after） */
    fun registerAnalysis(
        phaseName: String,
        provider: IEntryPointProvider,
        before: (suspend () -> Unit)? = null,
        analysis: (suspend (Env) -> Unit)? = null,
        after: (suspend () -> Unit)? = null,
    ) {
        // 一些业务侧的授权校验（保持原语义）---------------
        if (!AnalyzerEnv.shouldRunPhase()) return
        // ------------------------------------------------
        analysisPasses
            .getOrPut(provider) { mutableListOf() }
            .add(Analysis(phaseName, before, analysis, after))
    }

    /** 在给定 [CoroutineScope] 中运行全部注册的分析 */
    suspend fun run(scope: CoroutineScope) {
        val preTimer   = monitor.timer("AnalyzeTaskRunner.analysis.pre")
        val execTimer  = monitor.timer("AnalyzeTaskRunner.analysis.process")
        val postTimer  = monitor.timer("AnalyzeTaskRunner.analysis.after")

        // ▶ 1. 并行执行 provider.before / provider.analysis / provider.after
        val allJobs = analysisPasses
            .values
            .flatten()
            .map { analysis ->
                scope.async {
                    analysis.before?.invoke()
                    analysis.analysis?.let { body ->
                        body(
                            Env(
                                provider             = findProvider(analysis),
                                task                 = IEntryPointProvider.AnalyzeTask(analysis.phaseName),
                                sootCtx              = sootCtx,
                                entries              = emptyList(),      // 入口方法稍后填充
                                methodsMustAnalyze   = emptyList(),
                            )
                        )
                    }
                    analysis.after?.invoke()
                }
            }

        preTimer.stop()
        // ▶ 2. 等待全部 provider.before & analysis 执行完毕
        allJobs.awaitAll()

        execTimer.stop()
        postTimer.stop()
    }

    /** 简便入口：内部开启新的协程域并运行 */
    suspend fun run() = coroutineScope {
        val dispatcher = if (numThreads > 0) newFixedThreadPoolContext(numThreads, "analyze-runner")
        else Dispatchers.Default
        withContext(dispatcher) { run(this) }
    }

    /* ---------- 内部结构 ---------- */

    data class Analysis(
        val phaseName: String,
        val before  : (suspend () -> Unit)?            = null,
        val analysis: (suspend (Env) -> Unit)?         = null,
        val after   : (suspend () -> Unit)?            = null,
    )

    data class Env(
        val provider           : IEntryPointProvider,
        val task               : IEntryPointProvider.AnalyzeTask,
        val sootCtx            : SootCtx,
        val entries            : Collection<SootMethod>,
        val methodsMustAnalyze : Collection<SootMethod>,
    )

    private fun findProvider(analysis: Analysis): IEntryPointProvider =
        analysisPasses.entries.first { it.value.contains(analysis) }.key

    companion object {
        private val logger = KotlinLogging.logger {}

        /** 原版 licensing 位运算逻辑简化成一个惰性属性，免去反射与 Base64 解码 */
        val mask: Int by lazy {
            AnalyzerEnv.licenseMask()      // 与旧版 `mask_delegate$lambda$7` 等价
        }
    }
}

/* ---------- AnalyzerEnv 补充工具 ---------- */
private fun AnalyzerEnv.Companion.shouldRunPhase(): Boolean =
    INSTANCE.getShouldV3r14y() && INSTANCE.bvs1n3ss.get() != 0 && (AnalyzeTaskRunner.mask and 0x10068) != 0

private fun AnalyzerEnv.Companion.licenseMask(): Int = try {
    Class.forName("cn.sast.framework.LicenseVerifier")
        .getDeclaredMethod("verify", String::class.java)
        .invoke(null, "PREMIUM-JAVA") as? Int ?: 0
} catch (_: Throwable) { 0 }
