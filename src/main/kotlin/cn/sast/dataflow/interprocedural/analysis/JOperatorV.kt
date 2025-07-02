package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import com.feysh.corax.config.api.IIexConst
import soot.PrimType
import soot.RefLikeType
import soot.SootMethod
import soot.jimple.AnyNewExpr
import soot.jimple.BinopExpr
import soot.jimple.Constant
import soot.jimple.NegExpr

public interface JOperatorV<V> : JOperator<V> {
   public abstract fun markOfEntryMethodParam(entryPoint: SootMethod): JOperatorV<Any> {
   }

   public abstract fun markOfTaint(): JOperatorV<Any> {
   }

   public abstract fun markOfConstant(c: Constant, info: Any? = ...): JOperatorV<Any> {
   }

   public abstract fun markOfConstant(c: IIexConst): JOperatorV<Any> {
   }

   public abstract fun markOfNewExpr(expr: AnyNewExpr): JOperatorV<Any> {
   }

   public abstract fun markSummaryValueFromArrayGet(array: CompanionV<Any>, info: Any? = ...): JOperatorV<Any> {
   }

   public abstract fun markSummaryValueInCaughtExceptionRhs(): JOperatorV<Any> {
   }

   public abstract fun markSummaryReturnValueFailedInHook(): JOperatorV<Any> {
   }

   public abstract fun markSummaryReturnValueInCalleeSite(): JOperatorV<Any> {
   }

   public abstract fun markSummaryReturnValueFailedGetKeyFromKey(src: CompanionV<Any>, mt: Any, key: Any?): JOperatorV<Any> {
   }

   public abstract fun dataGetElementFromSequence(sourceSequence: CompanionV<Any>): JOperatorV<Any> {
   }

   public abstract fun dataSequenceToSeq(sourceSequence: CompanionV<Any>): JOperatorV<Any> {
   }

   public abstract fun markSummaryArraySize(allocSite: IHeapValues<Any>): JOperatorV<Any> {
   }

   public abstract fun markArraySizeOf(array: CompanionV<Any>): JOperatorV<Any> {
   }

   public abstract fun markOfCantCalcAbstractResultValue(): JOperatorV<Any> {
   }

   public abstract fun markSootBinOp(expr: BinopExpr, clop: CompanionV<IValue>, crop: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfOp(op: Any, op1: CompanionV<IValue>, vararg ops: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfOp(op: Any, op1: ImmutableElementSet<Any>, vararg ops: ImmutableElementSet<Any>): JOperatorV<Any> {
   }

   public abstract fun markOfNegExpr(expr: NegExpr, cop: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfCastTo(toPrimType: PrimType): JOperatorV<Any> {
   }

   public abstract fun markOfCastTo(toRefType: RefLikeType): JOperatorV<Any> {
   }

   public abstract fun markOfInstanceOf(): JOperatorV<Any> {
   }

   public abstract fun markOfArrayContentEqualsBoolResult(): JOperatorV<Any> {
   }

   public abstract fun markOfParseString(hint: String, str: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfGetClass(cop: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfObjectEqualsResult(th1s: CompanionV<IValue>, that: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfReturnValueOfMethod(ctx: EvalCall): JOperatorV<Any> {
   }

   public abstract fun markOfStringLatin1Hash(byteArray: CompanionV<IValue>): JOperatorV<Any> {
   }

   public abstract fun markOfWideningSummary(): JOperatorV<Any> {
   }

   public abstract fun pop(): CompanionV<Any> {
   }

   public abstract fun popHV(): IHeapValues<Any> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls
}
