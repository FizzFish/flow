package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.idfa.check.ICalleeCB
import soot.RefType
import soot.Scene
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.jimple.Stmt

class CalleeCBImpl(
    val hf: AbstractHeapFactory<IValue>,
    val callee: SootMethod,
    val stmt: Stmt,
    var out: Builder<IValue>,
    val env: HookEnv
) : ICalleeSiteCBImpl {

    val newEnv: AnyNewExprEnv
        get() = AnyNewExprEnv(callee, stmt as Unit)

    val globalStaticObject: Pair<String, RefType>
        get() = hf.vg.GLOBAL_LOCAL to Scene.v().objectType

    val global: IHeapValues<IValue>
        get() = if (hf.vg.staticFieldTrackingMode != StaticFieldTrackingMode.None)
            hf.push(env, hf.vg.GLOBAL_SITE).popHV()
        else
            hf.empty()

    val `this`: IHeapValues<IValue>
        get() = arg(-1)

    var `return`: IHeapValues<IValue>
        get() {
            val result = out.targetsUnsafe(hf.vg.RETURN_LOCAL)
            if (result != null) {
                return result
            } else {
                val returnType = callee.returnType
                val newVal = hf.push(env, hf.newSummaryVal(env, returnType, hf.vg.RETURN_LOCAL))
                    .markSummaryReturnValueInCalleeSite()
                    .popHV()
                Builder.DefaultImpls.assignNewExpr$default(out, env, hf.vg.RETURN_LOCAL, newVal, false, 8, null)
                return newVal
            }
        }
        set(value) {
            Builder.DefaultImpls.assignNewExpr$default(out, env, hf.vg.RETURN_LOCAL, value, false, 8, null)
        }

    fun arg(argIndex: Int): IHeapValues<IValue> {
        return out.targetsUnsafe(argIndex) ?: hf.empty()
    }

    override fun argToValue(argIndex: Int): Any = argIndex

    class EvalCall(private val delegate: CalleeCBImpl) : ICalleeCB.IEvalCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICalleeSiteCBImpl {
        var isEvalAble: Boolean = true
            internal set

        override val callee: SootMethod get() = delegate.callee
        override val env: HookEnv get() = delegate.env
        override val global: IHeapValues<IValue> get() = delegate.global
        override val hf: AbstractHeapFactory<IValue> get() = delegate.hf
        override val newEnv: AnyNewExprEnv get() = delegate.newEnv
        override var out: Builder<IValue> = delegate.out
            internal set
        override var `return`: IHeapValues<IValue> = delegate.return
            internal set
        override val stmt: Stmt get() = delegate.stmt
        override val `this`: IHeapValues<IValue> get() = delegate.this

        override fun arg(argIndex: Int): IHeapValues<IValue> = delegate.arg(argIndex)
        override fun argToValue(argIndex: Int): Any = delegate.argToValue(argIndex)
    }

    class PostCall(private val delegate: CalleeCBImpl) : ICalleeCB.IPostCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICalleeSiteCBImpl {
        override val callee: SootMethod get() = delegate.callee
        override val env: HookEnv get() = delegate.env
        override val global: IHeapValues<IValue> get() = delegate.global
        override val hf: AbstractHeapFactory<IValue> get() = delegate.hf
        override val newEnv: AnyNewExprEnv get() = delegate.newEnv
        override var out: Builder<IValue> = delegate.out
            internal set
        override var `return`: IHeapValues<IValue> = delegate.return
            internal set
        override val stmt: Stmt get() = delegate.stmt
        override val `this`: IHeapValues<IValue> get() = delegate.this

        override fun arg(argIndex: Int): IHeapValues<IValue> = delegate.arg(argIndex)
        override fun argToValue(argIndex: Int): Any = delegate.argToValue(argIndex)
    }

    class PrevCall(private val delegate: CalleeCBImpl) : ICalleeCB.IPrevCall<IHeapValues<IValue>, IFact.Builder<IValue>>, ICalleeSiteCBImpl {
        override var `return`: IHeapValues<IValue>
            get() = throw IllegalStateException("prev call has no return")
            set(value) = throw IllegalStateException("prev call has no return")

        override val callee: SootMethod get() = delegate.callee
        override val env: HookEnv get() = delegate.env
        override val global: IHeapValues<IValue> get() = delegate.global
        override val hf: AbstractHeapFactory<IValue> get() = delegate.hf
        override val newEnv: AnyNewExprEnv get() = delegate.newEnv
        override var out: Builder<IValue> = delegate.out
            internal set
        override val stmt: Stmt get() = delegate.stmt
        override val `this`: IHeapValues<IValue> get() = delegate.this

        override fun arg(argIndex: Int): IHeapValues<IValue> = delegate.arg(argIndex)
        override fun argToValue(argIndex: Int): Any = delegate.argToValue(argIndex)
    }
}