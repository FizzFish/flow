package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.util.SootUtilsKt
import java.util.Arrays
import soot.BooleanType
import soot.G
import soot.Type

public class WArrays : SummaryHandlePackage<IValue> {
   public override fun ACheckCallAnalysis.register() {
      `$this$register`.evalCall("<java.util.Arrays: boolean equals(byte[],byte[])>", WArrays::register$lambda$1);
      `$this$register`.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.util.Arrays: byte[] copyOfRange(byte[],int,int)>", true));
   }

   @JvmStatic
   fun `register$lambda$1$contentEqual`(`$this_evalCall`: CalleeCBImpl.EvalCall, arrayA: IArrayHeapKV<Integer, IValue>, arrayB: IArrayHeapKV<Integer, IValue>): java.lang.Boolean {
      val var10000: ByteArray = arrayA.getByteArray(`$this_evalCall`.getHf());
      label11:
      if (var10000 == null) {
         return null;
      } else {
         val var5: ByteArray = arrayB.getByteArray(`$this_evalCall`.getHf());
         return if (var5 == null) null else Arrays.equals(var10000, var5);
      }
   }

   @JvmStatic
   fun `register$lambda$1$lambda$0`(`$this_evalCall`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      val ca1: CompanionV = var3[0];
      val ca2: CompanionV = var3[1];
      val a1: IValue = ca1.getValue() as IValue;
      val a2: IValue = ca2.getValue() as IValue;
      if (!a1.isNullConstant() && !a2.isNullConstant()) {
         val arrayA1: IArrayHeapKV = `$this_evalCall`.getOut().getArray(a1);
         val arrayA2: IArrayHeapKV = `$this_evalCall`.getOut().getArray(a2);
         if (arrayA1 != null && arrayA2 != null) {
            val var10000: java.lang.Boolean = register$lambda$1$contentEqual(`$this_evalCall`, arrayA1, arrayA2);
            if (var10000 != null) {
               res.add(
                  `$this_evalCall`.getHf()
                     .push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(var10000))
                     .markOfArrayContentEqualsBoolResult()
                     .pop()
               );
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         res.add(
            `$this_evalCall`.getHf().push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(false)).markOfArrayContentEqualsBoolResult().pop()
         );
         return true;
      }
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$1`(): Unit {
      val binop: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(0), `$this$evalCall`.arg(1));
      binop.resolve(WArrays::register$lambda$1$lambda$0);
      val var5: BooleanType = G.v().soot_BooleanType();
      binop.putSummaryIfNotConcrete(var5 as Type, "return");
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         `$this$evalCall`.getOut(), `$this$evalCall`.getEnv(), `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(), binop.getRes().build(), false, 8, null
      );
      return Unit.INSTANCE;
   }

   public companion object {
      public fun v(): WArrays {
         return new WArrays();
      }
   }
}
