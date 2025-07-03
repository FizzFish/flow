package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.common.GLB
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.IBoolExpr
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IJDecl
import com.feysh.corax.config.api.IMethodDecl
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MethodConfig
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import mu.KLogger
import soot.SootMethod

@SourceDebugExtension(["SMAP\nCheckerModeling.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/CheckerModeling\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CheckerManager.kt\ncn/sast/idfa/check/CallBackManager\n*L\n1#1,565:1\n1863#2:566\n1864#2:585\n1863#2:586\n1864#2:605\n1863#2,2:606\n1863#2,2:608\n83#3,3:567\n83#3,3:570\n83#3,3:573\n83#3,3:576\n83#3,3:579\n83#3,3:582\n83#3,3:587\n83#3,3:590\n83#3,3:593\n83#3,3:596\n83#3,3:599\n83#3,3:602\n*S KotlinDebug\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/CheckerModeling\n*L\n392#1:566\n392#1:585\n436#1:586\n436#1:605\n500#1:606,2\n526#1:608,2\n399#1:567,3\n405#1:570,3\n411#1:573,3\n416#1:576,3\n422#1:579,3\n427#1:582,3\n443#1:587,3\n449#1:590,3\n455#1:593,3\n460#1:596,3\n466#1:599,3\n471#1:602,3\n*E\n"])
class CheckerModeling(
    val mainConfig: MainConfig,
    val icfg: InterproceduralCFG,
    override val preAnalysis: PreAnalysisApi
) : AIAnalysisBaseImpl(),
    SummaryHandlePackage<IValue>,
    IAnalysisDepends {

    private val summaries: MutableList<Triple<SootMethod, (MethodConfig) -> Unit, IStmt>> = ArrayList()
    private val checkPoints: MutableList<Checker> = ArrayList()
    val error: ConfigInfoLogger = ConfigInfoLogger()

    override val fastCache: FastCache
        get() = FastCacheImpl.INSTANCE

    override val scope: CoroutineScope
        get() = GlobalScope as CoroutineScope

    override fun ACheckCallAnalysis.register() {
        getLogger().info { "summaries model size: ${summaries.size}" }

        summaries.forEach { (imp, cb, method) ->
            val this_ = MethodConfig(MethodConfig.CheckCall.PostCallInCaller)
            cb.invoke(this_)
            val cbObj = ModelingCallBack(imp, method)
            
            when (imp.getAt()) {
                MethodConfig.CheckCall.PrevCallInCaller -> getCallBackManager().put(
                    CallerSiteCBImpl.PrevCall::class.java,
                    imp,
                    Function2 { p1: CallerSiteCBImpl.PrevCall, p2: Continuation<Unit> ->
                        cbObj.model(getIcfg(), p1.hf, p1.env, p1)
                        Unit
                    }
                )
                MethodConfig.CheckCall.EvalCallInCaller -> getCallBackManager().put(
                    CallerSiteCBImpl.EvalCall::class.java,
                    imp,
                    Function2 { p1: CallerSiteCBImpl.EvalCall, p2: Continuation<Unit> ->
                        cbObj.model(getIcfg(), p1.hf, p1.env, p1)
                        Unit
                    }
                )
                MethodConfig.CheckCall.PostCallInCaller -> getCallBackManager().put(
                    CallerSiteCBImpl.PostCall::class.java,
                    imp,
                    Function2 { p1: CallerSiteCBImpl.PostCall, p2: Continuation<Unit> ->
                        cbObj.model(getIcfg(), p1.hf, p1.env, p1)
                        Unit
                    }
                )
                MethodConfig.CheckCall.PrevCallInCallee -> getCallBackManager().put(
                    CalleeCBImpl.PrevCall::class.java,
                    imp,
                    Function2 { p1: CalleeCBImpl.PrevCall, p2: Continuation<Unit> ->
                        cbObj.model(getIcfg(), p1.hf, p1.env, p1)
                        Unit
                    }
                )
                MethodConfig.CheckCall.EvalCallInCallee -> getCallBackManager().put(
                    CalleeCBImpl.EvalCall::class.java,
                    imp,
                    Function2 { p1: CalleeCBImpl.EvalCall, p2: Continuation<Unit> ->
                        cbObj.model(getIcfg(), p1.hf, p1.env, p1)
                        Unit
                    }
                )
                MethodConfig.CheckCall.PostCallInCallee -> getCallBackManager().put(
                    CalleeCBImpl.PostCall::class.java,
                    imp,
                    Function2 { p1: CalleeCBImpl.PostCall, p2: Continuation<Unit> ->
                        cbObj.model(getIcfg(), p1.hf, p1.env, p1)
                        Unit
                    }
                )
                else -> throw NoWhenBranchMatchedException()
            }
        }

        getLogger().info { "check-points size: ${checkPoints.size}" }

        checkPoints.forEach { checker ->
            val config = MethodConfig(MethodConfig.CheckCall.PostCallInCaller)
            checker.config.invoke(config)
            val cb = CheckCallBack(checker.atMethod, checker)
            val method = checker.atMethod

            when (method.getAt()) {
                MethodConfig.CheckCall.PrevCallInCaller -> getCallBackManager().put(
                    CallerSiteCBImpl.PrevCall::class.java,
                    method,
                    Function2 { p1: CallerSiteCBImpl.PrevCall, p2: Continuation<Unit> ->
                        cb.check(p1.hf, p1.env, p1, getIcfg(), p2)
                        Unit
                    }
                )
                MethodConfig.CheckCall.EvalCallInCaller -> getCallBackManager().put(
                    CallerSiteCBImpl.EvalCall::class.java,
                    method,
                    Function2 { p1: CallerSiteCBImpl.EvalCall, p2: Continuation<Unit> ->
                        cb.check(p1.hf, p1.env, p1, getIcfg(), p2)
                        Unit
                    }
                )
                MethodConfig.CheckCall.PostCallInCaller -> getCallBackManager().put(
                    CallerSiteCBImpl.PostCall::class.java,
                    method,
                    Function2 { p1: CallerSiteCBImpl.PostCall, p2: Continuation<Unit> ->
                        cb.check(p1.hf, p1.env, p1, getIcfg(), p2)
                        Unit
                    }
                )
                MethodConfig.CheckCall.PrevCallInCallee -> getCallBackManager().put(
                    CalleeCBImpl.PrevCall::class.java,
                    method,
                    Function2 { p1: CalleeCBImpl.PrevCall, p2: Continuation<Unit> ->
                        cb.check(p1.hf, p1.env, p1, getIcfg(), p2)
                        Unit
                    }
                )
                MethodConfig.CheckCall.EvalCallInCallee -> getCallBackManager().put(
                    CalleeCBImpl.EvalCall::class.java,
                    method,
                    Function2 { p1: CalleeCBImpl.EvalCall, p2: Continuation<Unit> ->
                        cb.check(p1.hf, p1.env, p1, getIcfg(), p2)
                        Unit
                    }
                )
                MethodConfig.CheckCall.PostCallInCallee -> getCallBackManager().put(
                    CalleeCBImpl.PostCall::class.java,
                    method,
                    Function2 { p1: CalleeCBImpl.PostCall, p2: Continuation<Unit> ->
                        cb.check(p1.hf, p1.env, p1, getIcfg(), p2)
                        Unit
                    }
                )
                else -> throw NoWhenBranchMatchedException()
            }
        }
    }

    override fun addStmt(decl: IJDecl, config: (MethodConfig) -> Unit, stmt: IStmt) {
        if (decl is IMethodDecl) {
            decl.getMatch().forEach { it ->
                synchronized(summaries) {
                    summaries.add(Triple(it, config, stmt))
                }
            }
        } else {
            getLogger().debug { "TODO: decl: $decl not support" }
        }
    }

    override fun check(
        decl: IJDecl,
        config: (MethodConfig) -> Unit,
        expr: IBoolExpr,
        checkType: CheckType,
        env: (Env) -> Unit
    ) {
        GLB += checkType
        if (mainConfig.isEnable(checkType)) {
            if (decl is IMethodDecl) {
                decl.getMatch().forEach { it ->
                    synchronized(checkPoints) {
                        checkPoints.add(Checker(it, config, expr, checkType, env))
                    }
                }
            } else {
                getLogger().error { "TODO: decl: $decl not support" }
            }
        }
    }

    override fun eval(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IExpr, accept: (Any) -> Unit) {
        throw NotImplementedError("An operation is not implemented: Not yet implemented")
    }

    override fun validate() {
    }

    override fun toDecl(target: Any): XDecl {
        return MainConfigKt.interProceduralAnalysisDepends(mainConfig).toDecl(target)
    }

    override infix fun XDecl.dependsOn(dep: XDecl) {
        MainConfigKt.interProceduralAnalysisDepends(mainConfig).dependsOn(this, dep)
    }

    override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
        MainConfigKt.interProceduralAnalysisDepends(mainConfig).dependsOn(this, deps)
    }

    data class Checker(
        val atMethod: SootMethod,
        val config: (MethodConfig) -> Unit,
        val guard: IBoolExpr,
        val report: CheckType,
        val env: (Env) -> Unit
    ) {
        operator fun component1(): SootMethod = atMethod
        operator fun component2(): (MethodConfig) -> Unit = config
        operator fun component3(): IBoolExpr = guard
        operator fun component4(): CheckType = report
        operator fun component5(): (Env) -> Unit = env

        fun copy(
            atMethod: SootMethod = this.atMethod,
            config: (MethodConfig) -> Unit = this.config,
            guard: IBoolExpr = this.guard,
            report: CheckType = this.report,
            env: (Env) -> Unit = this.env
        ) = Checker(atMethod, config, guard, report, env)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Checker) return false

            return atMethod == other.atMethod &&
                config == other.config &&
                guard == other.guard &&
                report == other.report &&
                env == other.env
        }

        override fun hashCode(): Int {
            var result = atMethod.hashCode()
            result = 31 * result + config.hashCode()
            result = 31 * result + guard.hashCode()
            result = 31 * result + report.hashCode()
            result = 31 * result + env.hashCode()
            return result
        }

        override fun toString(): String {
            return "Checker(atMethod=$atMethod, config=$config, guard=$guard, report=$report, env=$env)"
        }
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")
    }
}