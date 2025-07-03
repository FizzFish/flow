package cn.sast.api.config

import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ICheckPoint
import com.feysh.corax.config.api.IInvokeCheckPoint
import com.feysh.corax.config.api.IMethodCheckPoint
import com.feysh.corax.config.api.INodeWithRange
import com.feysh.corax.config.api.IPreAnalysisInvokeConfig
import com.feysh.corax.config.api.IPreAnalysisMethodConfig
import com.feysh.corax.config.api.ISourceFileCheckPoint
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.Position
import com.github.javaparser.ast.nodeTypes.NodeWithRange
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KCallable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import soot.tagkit.Host

public interface PreAnalysisCoroutineScope : PreAnalysisApi {
    public val mainConfig: MainConfig

    public var scope: CoroutineScope
        internal set

    public fun uninitializedScope() {
    }

    public suspend fun processPreAnalysisUnits() {
    }

    @SourceDebugExtension(["SMAP\nPreAnalysisCoroutineScope.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisCoroutineScope.kt\ncn/sast/api/config/PreAnalysisCoroutineScope$DefaultImpls\n+ 2 Timer.kt\ncn/sast/api/util/TimerKt\n*L\n1#1,99:1\n16#2,8:100\n*S KotlinDebug\n*F\n+ 1 PreAnalysisCoroutineScope.kt\ncn/sast/api/config/PreAnalysisCoroutineScope$DefaultImpls\n*L\n28#1:100,8\n*E\n"])
    public companion object {
        @JvmStatic
        fun <T> runInScene(
            `$this`: PreAnalysisCoroutineScope,
            `$context_receiver_0`: CheckerUnit,
            block: (Continuation<in T>) -> Any
        ): Job {
            return PreAnalysisApi.DefaultImpls.runInScene(`$this`, `$context_receiver_0`, block)
        }

        @JvmStatic
        fun <T> atMethod(
            `$this`: PreAnalysisCoroutineScope,
            `$context_receiver_0`: CheckerUnit,
            method: KCallable<*>,
            config: (IPreAnalysisMethodConfig) -> Unit,
            block: (IMethodCheckPoint?, Continuation<in T>) -> Any
        ): PreAnalysisApiResult<T> {
            return PreAnalysisApi.DefaultImpls.atMethod(`$this`, `$context_receiver_0`, method, config, block)
        }

        @JvmStatic
        fun <T> atInvoke(
            `$this`: PreAnalysisCoroutineScope,
            `$context_receiver_0`: CheckerUnit,
            callee: KCallable<*>,
            config: (IPreAnalysisInvokeConfig) -> Unit,
            block: (IInvokeCheckPoint?, Continuation<in T>) -> Any
        ): PreAnalysisApiResult<T> {
            return PreAnalysisApi.DefaultImpls.atInvoke(`$this`, `$context_receiver_0`, callee, config, block)
        }

        @JvmStatic
        fun <P> report(
            `$this`: PreAnalysisCoroutineScope,
            `$receiver`: P,
            checkType: CheckType,
            region: Region,
            env: (BugMessage.Env) -> Unit
        ) where P : ICheckPoint, P : INodeWithRange {
            PreAnalysisApi.DefaultImpls.report(`$this`, `$receiver`, checkType, region, env)
        }

        @JvmStatic
        fun report(
            `$this`: PreAnalysisCoroutineScope,
            `$receiver`: ISourceFileCheckPoint,
            checkType: CheckType,
            region: Region,
            env: (BugMessage.Env) -> Unit
        ) {
            PreAnalysisApi.DefaultImpls.report(`$this`, `$receiver`, checkType, region, env)
        }

        @JvmStatic
        fun report(
            `$this`: PreAnalysisCoroutineScope,
            `$receiver`: ISourceFileCheckPoint,
            checkType: CheckType,
            cpgRegion: de.fraunhofer.aisec.cpg.sarif.Region,
            env: (BugMessage.Env) -> Unit
        ) {
            PreAnalysisApi.DefaultImpls.report(`$this`, `$receiver`, checkType, cpgRegion, env)
        }

        @JvmStatic
        fun report(
            `$this`: PreAnalysisCoroutineScope,
            `$receiver`: ISourceFileCheckPoint,
            checkType: CheckType,
            jpsStart: Position,
            jpsEnd: Position?,
            env: (BugMessage.Env) -> Unit
        ) {
            PreAnalysisApi.DefaultImpls.report(`$this`, `$receiver`, checkType, jpsStart, jpsEnd, env)
        }

        @JvmStatic
        fun report(
            `$this`: PreAnalysisCoroutineScope,
            `$receiver`: ISourceFileCheckPoint,
            checkType: CheckType,
            regionNode: NodeWithRange<*>,
            env: (BugMessage.Env) -> Unit
        ) {
            PreAnalysisApi.DefaultImpls.report(`$this`, `$receiver`, checkType, regionNode, env)
        }

        @JvmStatic
        fun report(
            `$this`: PreAnalysisCoroutineScope,
            checkType: CheckType,
            sootHost: Host,
            region: Region?,
            env: (BugMessage.Env) -> Unit
        ) {
            PreAnalysisApi.DefaultImpls.report(`$this`, checkType, sootHost, region, env)
        }
    }
}