package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import com.feysh.corax.config.api.IIexConst
import soot.*
import soot.jimple.*

/** CompanionV 栈顶返回型 Operator */
interface JOperatorV<V> : JOperator<V> {

   /* ---------- 标记 ---------- */

   fun markOfEntryMethodParam(entryPoint: SootMethod): JOperatorV<V>
   fun markOfTaint(): JOperatorV<V>
   fun markOfConstant(c: Constant, info: Any? = null): JOperatorV<V>
   fun markOfConstant(c: IIexConst): JOperatorV<V>
   fun markOfNewExpr(expr: AnyNewExpr): JOperatorV<V>
   fun markSummaryValueFromArrayGet(array: CompanionV<V>, info: Any? = null): JOperatorV<V>
   fun markSummaryValueInCaughtExceptionRhs(): JOperatorV<V>
   fun markSummaryReturnValueFailedInHook(): JOperatorV<V>
   fun markSummaryReturnValueInCalleeSite(): JOperatorV<V>
   fun markSummaryReturnValueFailedGetKeyFromKey(
      src: CompanionV<V>, mt: Any, key: Any?
   ): JOperatorV<V>

   /* ---------- Data operation ---------- */

   fun dataGetElementFromSequence(sourceSequence: CompanionV<V>): JOperatorV<V>
   fun dataSequenceToSeq(sourceSequence: CompanionV<V>): JOperatorV<V>

   /* ---------- 数组长度 ---------- */

   fun markSummaryArraySize(allocSite: IHeapValues<V>): JOperatorV<V>
   fun markArraySizeOf(array: CompanionV<V>): JOperatorV<V>

   /* ---------- 算术 / 逻辑 ---------- */

   fun markOfCantCalcAbstractResultValue(): JOperatorV<V>
   fun markSootBinOp(expr: BinopExpr, clop: CompanionV<IValue>, crop: CompanionV<IValue>): JOperatorV<V>

   fun markOfOp(op: Any, op1: CompanionV<IValue>, vararg ops: CompanionV<IValue>): JOperatorV<V>
   fun markOfOp(op: Any, op1: ImmutableElementSet<Any>, vararg ops: ImmutableElementSet<Any>): JOperatorV<V>
   fun markOfNegExpr(expr: NegExpr, cop: CompanionV<IValue>): JOperatorV<V>

   fun markOfCastTo(toPrimType: PrimType): JOperatorV<V>
   fun markOfCastTo(toRefType: RefLikeType): JOperatorV<V>
   fun markOfInstanceOf(): JOperatorV<V>
   fun markOfArrayContentEqualsBoolResult(): JOperatorV<V>
   fun markOfParseString(hint: String, str: CompanionV<IValue>): JOperatorV<V>
   fun markOfGetClass(cop: CompanionV<IValue>): JOperatorV<V>
   fun markOfObjectEqualsResult(th1s: CompanionV<IValue>, that: CompanionV<IValue>): JOperatorV<V>
   fun markOfReturnValueOfMethod(ctx: EvalCall): JOperatorV<V>
   fun markOfStringLatin1Hash(byteArray: CompanionV<IValue>): JOperatorV<V>
   fun markOfWideningSummary(): JOperatorV<V>

   /* ---------- 栈弹出 ---------- */

   fun pop(): CompanionV<V>
   fun popHV(): IHeapValues<V>
}
