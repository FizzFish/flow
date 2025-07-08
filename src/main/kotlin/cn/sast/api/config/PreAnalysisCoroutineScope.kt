package cn.sast.api.config

/* ─────────────────────────────────── Imports ────────────────────────────────── */
import com.feysh.corax.config.api.*
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.Position
import com.github.javaparser.ast.nodeTypes.NodeWithRange
import de.fraunhofer.aisec.cpg.sarif.Region as CpgRegion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.reflect.KCallable
import soot.tagkit.Host

/* ============================================================================ */
/*  PreAnalysisCoroutineScope — 接口 & 默认工具                                 */
/* ============================================================================ */
interface PreAnalysisCoroutineScope : PreAnalysisApi {

    /* ---------- 运行环境 ---------- */

    val mainConfig: MainConfig
    override var scope: CoroutineScope

    /** 由框架调用，用来重置 / 标记未初始化 */
    fun uninitializedScope()

    /** 实际执行预分析（实现类自定义并发策略） */
    suspend fun processPreAnalysisUnits()

    /* ---------- 默认帮助方法 ---------- */

    companion object {

        /* ============ Scene 级 ============ */

        @JvmStatic
        fun <T> runInScene(
            receiver: PreAnalysisCoroutineScope,
            checkerUnit: CheckerUnit,
            block: suspend () -> T
        ): Job = receiver.runInScene(checkerUnit) { block() }

        /* ============ Method 级 ============ */

        @JvmStatic
        fun <T> atMethod(
            receiver: PreAnalysisCoroutineScope,
            checkerUnit: CheckerUnit,
            method: KCallable<*>,
            config: IPreAnalysisMethodConfig.() -> Unit = {},
            block: suspend (IMethodCheckPoint) -> T
        ): PreAnalysisApiResult<T> =
            receiver.atMethod(checkerUnit, method, config, block)

        /* ============ Invoke 级 ============ */

        @JvmStatic
        fun <T> atInvoke(
            receiver: PreAnalysisCoroutineScope,
            checkerUnit: CheckerUnit,
            callee: KCallable<*>,
            config: IPreAnalysisInvokeConfig.() -> Unit = {},
            block: suspend (IInvokeCheckPoint) -> T
        ): PreAnalysisApiResult<T> =
            receiver.atInvoke(checkerUnit, callee, config, block)

        /* ============ 报告族 ============ */

        /** AST/CPG 节点带范围 */
        @JvmStatic
        fun <P> report(
            receiver: PreAnalysisCoroutineScope,
            cp: P,
            type: CheckType,
            region: Region,
            env: BugMessage.Env.() -> Unit
        ) where P : ICheckPoint, P : INodeWithRange =
            receiver.report(cp, type, region, env)

        /** SourceFile + Region */
        @JvmStatic
        fun report(
            receiver: PreAnalysisCoroutineScope,
            cp: ISourceFileCheckPoint,
            type: CheckType,
            region: Region,
            env: BugMessage.Env.() -> Unit
        ) = receiver.report(cp, type, region, env)

        /** SourceFile + SARIF Region */
        @JvmStatic
        fun report(
            receiver: PreAnalysisCoroutineScope,
            cp: ISourceFileCheckPoint,
            type: CheckType,
            cpgRegion: CpgRegion,
            env: BugMessage.Env.() -> Unit
        ) = receiver.report(cp, type, cpgRegion, env)

        /** SourceFile + JP Position */
        @JvmStatic
        fun report(
            receiver: PreAnalysisCoroutineScope,
            cp: ISourceFileCheckPoint,
            type: CheckType,
            start: Position,
            end: Position?,
            env: BugMessage.Env.() -> Unit
        ) = receiver.report(cp, type, start, end, env)

        /** SourceFile + JP NodeWithRange */
        @JvmStatic
        fun report(
            receiver: PreAnalysisCoroutineScope,
            cp: ISourceFileCheckPoint,
            type: CheckType,
            node: NodeWithRange<*>,
            env: BugMessage.Env.() -> Unit
        ) = receiver.report(cp, type, node, env)

        /** Soot Host */
        @JvmStatic
        fun report(
            receiver: PreAnalysisCoroutineScope,
            type: CheckType,
            sootHost: Host,
            region: Region?,
            env: BugMessage.Env.() -> Unit
        ) = receiver.report(type, sootHost, region, env)
    }
}

/* ============================================================================ */
/*  AIAnalysisBaseImpl 预分析单元调度扩展                                       */
/* ============================================================================ */

suspend fun AIAnalysisBaseImpl.processAIAnalysisUnits(
    preAnalysisScope: PreAnalysisCoroutineScope
) {
    require(preAnalysis === preAnalysisScope) { "PreAnalysisScope mismatch" }

    val checkers = preAnalysisScope.mainConfig.saConfig?.checkers ?: emptySet()
    val semaphore = Semaphore(preAnalysisScope.mainConfig.parallelsNum)

    for (checker in checkers) {
        semaphore.withPermit {
            preAnalysisScope.processPreAnalysisUnits() // TODO: 若需按 checker 过滤，请在此实现
        }
    }
}