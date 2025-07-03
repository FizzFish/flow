package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import com.feysh.corax.config.api.IIexConst
import java.util.ArrayList
import java.util.HashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.PrimType
import soot.RefLikeType
import soot.SootMethod
import soot.jimple.AnyNewExpr
import soot.jimple.BinopExpr
import soot.jimple.Constant
import soot.jimple.NegExpr

@SourceDebugExtension(["SMAP\nOperatorPathFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorPathFactory.kt\ncn/sast/dataflow/interprocedural/check/OperatorPathFactory\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,201:1\n1628#2,3:202\n1454#2,2:205\n1368#2:207\n1557#2:208\n1628#2,3:209\n1456#2,3:212\n*S KotlinDebug\n*F\n+ 1 OperatorPathFactory.kt\ncn/sast/dataflow/interprocedural/check/OperatorPathFactory\n*L\n73#1:202,3\n77#1:205,2\n77#1:207\n77#1:208\n77#1:209,3\n77#1:212,3\n*E\n"])
internal data class OperatorPathFactory(
    val heapFactory: AbstractHeapFactory<IValue>,
    val env: HeapValuesEnv,
    private val alloc: IValue,
    val path: IPath? = null
) : JOperatorV<IValue> {

    override fun pop(): CompanionV<IValue> {
        val pathValue = path ?: UnknownPath.Companion.v(env)
        return CompanionValueImpl1(alloc, pathValue)
    }

    override fun popHV(): IHeapValues<IValue> {
        return heapFactory.empty().plus(pop())
    }

    override fun markOfConstant(c: Constant, info: Any?): JOperatorV<IValue> {
        return copy(path = LiteralPath.Companion.v(env, c, info))
    }

    override fun markOfConstant(c: IIexConst): JOperatorV<IValue> {
        return copy(path = LiteralPath.Companion.v(env, c))
    }

    override fun markOfNewExpr(expr: AnyNewExpr): JOperatorV<IValue> {
        return this
    }

    override fun markSummaryValueFromArrayGet(array: CompanionV<IValue>, info: Any?): JOperatorV<IValue> {
        return copy(path = (array as PathCompanionV).getPath())
    }

    override fun markSummaryValueInCaughtExceptionRhs(): JOperatorV<IValue> {
        return this
    }

    override fun markSummaryReturnValueFailedInHook(): JOperatorV<IValue> {
        return this
    }

    override fun markSummaryReturnValueInCalleeSite(): JOperatorV<IValue> {
        return this
    }

    override fun markOfCantCalcAbstractResultValue(): JOperatorV<IValue> {
        return this
    }

    override fun markOfEntryMethodParam(entryPoint: SootMethod): JOperatorV<IValue> {
        return this
    }

    override fun markSootBinOp(expr: BinopExpr, clop: CompanionV<IValue>, crop: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = MergePath.Companion.v(env, (clop as PathCompanionV).getPath(), (crop as PathCompanionV).getPath()))
    }

    override fun markOfOp(op: Any, op1: CompanionV<IValue>, vararg ops: CompanionV<IValue>): JOperatorV<IValue> {
        val paths = (listOf(op1) + ops).map { (it as PathCompanionV).getPath() }.toMutableSet()
        return copy(path = MergePath.Companion.v(env, paths))
    }

    override fun markOfOp(op: Any, op1: ImmutableElementSet<Any>, vararg ops: ImmutableElementSet<Any>): JOperatorV<IValue> {
        val paths = (listOf(op1) + ops).flatMap { set ->
            set.getMap().values().flatMap { heapValues ->
                (heapValues as IHeapValues).getValuesCompanion().map { (it as PathCompanionV).getPath() }
            }
        }.toMutableSet()
        return copy(path = MergePath.Companion.v(env, paths))
    }

    override fun markOfNegExpr(expr: NegExpr, cop: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (cop as PathCompanionV).getPath())
    }

    override fun markOfCastTo(toPrimType: PrimType): JOperatorV<IValue> {
        return this
    }

    override fun markOfCastTo(toRefType: RefLikeType): JOperatorV<IValue> {
        return this
    }

    override fun markOfInstanceOf(): JOperatorV<IValue> {
        return this
    }

    override fun markOfArrayContentEqualsBoolResult(): JOperatorV<IValue> {
        return this
    }

    override fun markOfParseString(hint: String, str: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (str as PathCompanionV).getPath())
    }

    override fun markSummaryReturnValueFailedGetKeyFromKey(src: CompanionV<IValue>, mt: Any, key: Any?): JOperatorV<IValue> {
        return this
    }

    override fun dataGetElementFromSequence(sourceSequence: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (sourceSequence as PathCompanionV).getPath())
    }

    override fun markSummaryArraySize(allocSite: IHeapValues<IValue>): JOperatorV<IValue> {
        return this
    }

    override fun markOfGetClass(cop: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (cop as PathCompanionV).getPath())
    }

    override fun markOfObjectEqualsResult(th1s: CompanionV<IValue>, that: CompanionV<IValue>): JOperatorV<IValue> {
        return this
    }

    override fun markOfReturnValueOfMethod(ctx: EvalCall): JOperatorV<IValue> {
        return this
    }

    override fun dataSequenceToSeq(sourceSequence: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (sourceSequence as PathCompanionV).getPath())
    }

    override fun markArraySizeOf(array: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (array as PathCompanionV).getPath())
    }

    override fun markOfTaint(): JOperatorV<IValue> {
        return this
    }

    override fun markOfStringLatin1Hash(byteArray: CompanionV<IValue>): JOperatorV<IValue> {
        return copy(path = (byteArray as PathCompanionV).getPath())
    }

    override fun markOfWideningSummary(): JOperatorV<IValue> {
        return this
    }

    private fun component3(): IValue = alloc
}