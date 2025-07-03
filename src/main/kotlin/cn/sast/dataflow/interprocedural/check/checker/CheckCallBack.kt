package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.PathCompanionV
import cn.sast.dataflow.interprocedural.check.UnknownPath
import cn.sast.dataflow.interprocedural.check.checker.CheckerModeling.Checker
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.report.Region
import java.util.LinkedHashMap
import kotlin.coroutines.jvm.internal.Boxing
import mu.KLogger
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.jimple.InvokeExpr
import soot.jimple.Stmt

public class CheckCallBack(
    public val atMethod: SootMethod,
    private val define: Checker
) {
    public override fun toString(): String {
        return "check ${this.atMethod}:  ${this.define.getGuard()}"
    }

    public suspend fun check(
        hf: AbstractHeapFactory<IValue>,
        env: HookEnv,
        summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>,
        icfg: InterproceduralCFG
    ): IFact<IValue>? {
        val callStack: CallStackContext = (summaryCtxCalleeSite.getOut() as IFact.Builder).getCallStack()
        val fact: IFact.Builder = summaryCtxCalleeSite.getOut() as IFact.Builder

        for (isBug in SequencesKt.toList(hf.resolve(env, summaryCtxCalleeSite, this.define.getGuard().getExpr()))) {
            val var10000 = isBug as? CompanionV
            if (var10000 != null) {
                val var22 = when (val var21 = var10000 as? PathCompanionV) {
                    null -> UnknownPath.Companion.v(env)
                    else -> var21.getPath() ?: UnknownPath.Companion.v(env)
                }

                val bool = var10000.getValue()
                if (bool is ConstVal && FactValuesKt.getBooleanValue$default(bool, false, 1, null) == Boxing.boxBoolean(true)) {
                    logger.debug { check$lambda$0(this) }
                    val container = icfg.getMethodOf(env.getNode())
                    var var23 = Region.Companion.invoke(env.getNode()) ?: Region.Companion.getERROR()

                    val ctx = object : ProgramStateContext(
                        this, container, var23.getMutable(), ClassResInfo.Companion.of(container), 
                        env.getNode(), this.atMethod, this.define.getGuard().getExpr()
                    ) {
                        private val args = LinkedHashMap<Any, Any>()
                        private var callee: SootMethod = this@CheckCallBack.atMethod
                        private var fileName: String? = null
                        private var invokeExpr: InvokeExpr? = null
                        private var method: SootMethod? = null
                        private var clazz: SootClass? = null
                        private var field: SootField? = null

                        override fun getArgs(): Map<Any, Any> = args
                        override fun getCallee(): SootMethod = callee
                        override fun setCallee(var1: SootMethod) { callee = var1 }
                        override fun getFileName(): String? = fileName
                        override fun setFileName(var1: String?) { fileName = var1 }
                        override fun getInvokeExpr(): InvokeExpr? = invokeExpr
                        override fun setInvokeExpr(var1: InvokeExpr?) { invokeExpr = var1 }
                        override fun getMethod(): SootMethod? = method
                        override fun setMethod(var1: SootMethod?) { method = var1 }
                        override fun getClazz(): SootClass? = clazz
                        override fun setClazz(var1: SootClass?) { clazz = var1 }
                        override fun getField(): SootField? = field
                        override fun setField(var1: SootField?) { field = var1 }
                    }
                    this.define.getEnv().invoke(ctx)
                    env.getCtx().report(var22, ctx, this.define)
                }
            }
        }

        return null
    }

    @JvmStatic
    private fun check$lambda$0(this$0: CheckCallBack): Any {
        return "found a bug at: method: ${this$0.atMethod}. define = ${this$0.define}"
    }

    public companion object {
        private val logger: KLogger
    }
}