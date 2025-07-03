package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IIFact
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.util.SootUtilsKt
import cn.sast.idfa.check.ICallerSiteCB
import java.io.Serializable
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.DefinitionStmt
import soot.jimple.InvokeExpr
import soot.jimple.Stmt

@SourceDebugExtension(["SMAP\nCallCallBackImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CallCallBackImpl.kt\ncn/sast/dataflow/interprocedural/check/callback/CallerSiteCBImpl\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n*L\n1#1,164:1\n310#2:165\n*S KotlinDebug\n*F\n+ 1 CallCallBackImpl.kt\ncn/sast/dataflow/interprocedural/check/callback/CallerSiteCBImpl\n*L\n63#1:165\n*E\n"])
class CallerSiteCBImpl(
    val hf: AbstractHeapFactory<IValue>,
    val caller: SootMethod,
    val stmt: Stmt,
    var out: Builder<IValue>,
    private val returnType: Type,
    val env: HookEnv
) : ICallerSiteCBImpl {
    val newEnv: AnyNewExprEnv
        get() = AnyNewExprEnv(caller, stmt as Unit)

    val global: IHeapValues<IValue>
        get() = if (hf.vg.staticFieldTrackingMode != StaticFieldTrackingMode.None)
            hf.push(env, hf.vg.GLOBAL_SITE).popHV()
        else
            hf.empty()

    var `return`: IHeapValues<IValue>

    val `this`: IHeapValues<IValue>
        get() = arg(-1)

    init {
        val definitionStmt = stmt as? DefinitionStmt
        val leftOp = definitionStmt?.leftOp
        `return` = hf.push(
            env,
            hf.newSummaryVal(
                env,
                returnType,
                leftOp ?: "return" as Serializable
            )
        )
            .markSummaryReturnValueInCalleeSite()
            .popHV()
    }

    override fun argToValue(argIndex: Int): Any {
        val iee = stmt.invokeExpr
        val (sootValue, _) = SootUtilsKt.argToOpAndType(iee, argIndex)
        return sootValue
    }

    fun arg(argIndex: Int): IHeapValues<IValue> {
        if (!stmt.containsInvokeExpr()) {
            throw IllegalStateException("env: $env\nstmt = $stmt\nargIndex=$argIndex")
        }
        val iee = stmt.invokeExpr
        val (value, type) = SootUtilsKt.argToOpAndType(iee, argIndex)
        return out.getOfSootValue(env, value, type)
    }

    class EvalCall(private val delegate: CallerSiteCBImpl) : ICallerSiteCB.IEvalCall<IHeapValues<IValue>, Builder<IValue>>, ICallerSiteCBImpl {
        var isEvalAble: Boolean = true
        override val caller: SootMethod get() = delegate.caller
        override val env: HookEnv get() = delegate.env
        override val global: IHeapValues<IValue> get() = delegate.global
        override val hf: AbstractHeapFactory<IValue> get() = delegate.hf
        override val newEnv: AnyNewExprEnv get() = delegate.newEnv
        override var out: Builder<IValue> = delegate.out
        override var `return`: IHeapValues<IValue> = delegate.`return`
        override val stmt: Stmt get() = delegate.stmt
        override val `this`: IHeapValues<IValue> get() = delegate.`this`

        override fun emit(fact: IIFact<IValue>) {}

        override fun arg(argIndex: Int): IHeapValues<IValue> = delegate.arg(argIndex)

        override fun argToValue(argIndex: Int): Any = delegate.argToValue(argIndex)
    }

    class PostCall(private val delegate: CallerSiteCBImpl) : ICallerSiteCB.IPostCall<IHeapValues<IValue>, Builder<IValue>>, ICallerSiteCBImpl {
        override val caller: SootMethod get() = delegate.caller
        override val env: HookEnv get() = delegate.env
        override val global: IHeapValues<IValue> get() = delegate.global
        override val hf: AbstractHeapFactory<IValue> get() = delegate.hf
        override val newEnv: AnyNewExprEnv get() = delegate.newEnv
        override var out: Builder<IValue> = delegate.out
        override var `return`: IHeapValues<IValue> = delegate.`return`
        override val stmt: Stmt get() = delegate.stmt
        override val `this`: IHeapValues<IValue> get() = delegate.`this`

        override fun emit(fact: IIFact<IValue>) {}

        override fun arg(argIndex: Int): IHeapValues<IValue> = delegate.arg(argIndex)

        override fun argToValue(argIndex: Int): Any = delegate.argToValue(argIndex)
    }

    class PrevCall(private val delegate: CallerSiteCBImpl) : ICallerSiteCB.IPrevCall<IHeapValues<IValue>, Builder<IValue>>, ICallerSiteCBImpl {
        override var `return`: IHeapValues<IValue>
            get() = throw IllegalStateException("prev call has no return")
            set(_) = throw IllegalStateException("prev call has no return")

        override val caller: SootMethod get() = delegate.caller
        override val env: HookEnv get() = delegate.env
        override val global: IHeapValues<IValue> get() = delegate.global
        override val hf: AbstractHeapFactory<IValue> get() = delegate.hf
        override val newEnv: AnyNewExprEnv get() = delegate.newEnv
        override var out: Builder<IValue> = delegate.out
        override val stmt: Stmt get() = delegate.stmt
        override val `this`: IHeapValues<IValue> get() = delegate.`this`

        override fun emit(fact: IIFact<IValue>) {}

        override fun arg(argIndex: Int): IHeapValues<IValue> = delegate.arg(argIndex)

        override fun argToValue(argIndex: Int): Any = delegate.argToValue(argIndex)
    }
}