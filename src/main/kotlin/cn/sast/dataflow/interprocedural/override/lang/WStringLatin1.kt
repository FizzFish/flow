package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.util.SootUtilsKt
import java.util.Arrays
import soot.BooleanType
import soot.G
import soot.IntType
import soot.Type
import soot.jimple.Constant
import soot.jimple.IntConstant

public class WStringLatin1 : SummaryHandlePackage<IValue> {
   public override fun ACheckCallAnalysis.register() {
      `$this$register`.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.lang.StringLatin1: char charAt(byte[],int)>", true));
      `$this$register`.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.lang.StringLatin1: char charAt(byte[],int)>", true));
      `$this$register`.evalCall("<java.lang.StringLatin1: int indexOf(byte[],int,int)>", WStringLatin1::register$lambda$1);
      `$this$register`.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.lang.StringLatin1: java.lang.String newString(byte[],int,int)>", true));
      `$this$register`.evalCall("<java.lang.StringLatin1: boolean equals(byte[],byte[])>", WStringLatin1::register$lambda$3);
      `$this$register`.evalCall("<java.lang.StringLatin1: int hashCode(byte[])>", WStringLatin1::register$lambda$5);
   }

   @JvmStatic
   fun `register$lambda$1$lambda$0`(`$this_ret`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      val value: CompanionV = var3[0];
      val ch: CompanionV = var3[1];
      val fromIndex: CompanionV = var3[2];
      var var10000: Int = FactValuesKt.getIntValue(ch.getValue() as IValue, true);
      if (var10000 != null) {
         val chV: Int = var10000;
         if (!WStringLatin1Kt.canEncode(chV)) {
            val var22: AbstractHeapFactory = `$this_ret`.getHf();
            val var25: HeapValuesEnv = `$this_ret`.getEnv();
            val var28: AbstractHeapFactory = `$this_ret`.getHf();
            val var34: IntConstant = IntConstant.v(-1);
            val var35: Constant = var34 as Constant;
            val var38: IntType = G.v().soot_IntType();
            res.add(var22.push(var25, var28.newConstVal(var35, var38 as Type)).markOfReturnValueOfMethod(`$this_ret`).pop());
            return true;
         } else {
            val max: IData = `$this_ret`.getOut().getValueData((IValue)value.getValue(), BuiltInModelT.Array);
            val var16: IArrayHeapKV = max as? IArrayHeapKV;
            if ((max as? IArrayHeapKV) == null) {
               return false;
            } else {
               val var17: Array<IValue> = var16.getArray(`$this_ret`.getHf());
               if (var17 == null) {
                  return false;
               } else {
                  val arrRaw: Array<IValue> = var17;
                  val var15: Int = var17.length;
                  var10000 = FactValuesKt.getIntValue(fromIndex.getValue() as IValue, true);
                  if (var10000 != null) {
                     var fromIndexV: Int = var10000;
                     if (fromIndexV < 0) {
                        fromIndexV = 0;
                     } else if (fromIndexV >= var15) {
                        val var21: AbstractHeapFactory = `$this_ret`.getHf();
                        val var24: HeapValuesEnv = `$this_ret`.getEnv();
                        val var27: AbstractHeapFactory = `$this_ret`.getHf();
                        val var32: IntConstant = IntConstant.v(-1);
                        val var33: Constant = var32 as Constant;
                        val var37: IntType = G.v().soot_IntType();
                        res.add(var21.push(var24, var27.newConstVal(var33, var37 as Type)).markOfReturnValueOfMethod(`$this_ret`).pop());
                        return true;
                     }

                     if (var15 - fromIndexV > 100) {
                        return false;
                     } else {
                        val c: Byte = (byte)chV;

                        for (int i = fromIndexV; i < max; i++) {
                           val var19: java.lang.Byte = FactValuesKt.getByteValue$default(arrRaw[i], false, 1, null);
                           if (var19 == null) {
                              return false;
                           }

                           if (var19 == c) {
                              val var10001: AbstractHeapFactory = `$this_ret`.getHf();
                              val var10002: HeapValuesEnv = `$this_ret`.getEnv();
                              val var10003: AbstractHeapFactory = `$this_ret`.getHf();
                              val var10004: IntConstant = IntConstant.v(i);
                              val var29: Constant = var10004 as Constant;
                              val var10005: IntType = G.v().soot_IntType();
                              res.add(var10001.push(var10002, var10003.newConstVal(var29, var10005 as Type)).markOfReturnValueOfMethod(`$this_ret`).pop());
                              return true;
                           }
                        }

                        val var20: AbstractHeapFactory = `$this_ret`.getHf();
                        val var23: HeapValuesEnv = `$this_ret`.getEnv();
                        val var26: AbstractHeapFactory = `$this_ret`.getHf();
                        val var30: IntConstant = IntConstant.v(-1);
                        val var31: Constant = var30 as Constant;
                        val var36: IntType = G.v().soot_IntType();
                        res.add(var20.push(var23, var26.newConstVal(var31, var36 as Type)).markOfReturnValueOfMethod(`$this_ret`).pop());
                        return true;
                     }
                  } else {
                     return false;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$1`(): Unit {
      val valueObjectOp: IOpCalculator = `$this$ret`.getHf().resolveOp(`$this$ret`.getEnv(), `$this$ret`.arg(0), `$this$ret`.arg(1), `$this$ret`.arg(2));
      valueObjectOp.resolve(WStringLatin1::register$lambda$1$lambda$0);
      val var6: IntType = G.v().soot_IntType();
      valueObjectOp.putSummaryIfNotConcrete(var6 as Type, "return");
      IFact.Builder.DefaultImpls.assignNewExpr$default(
         `$this$ret`.getOut(), `$this$ret`.getEnv(), `$this$ret`.getHf().getVg().getRETURN_LOCAL(), valueObjectOp.getRes().build(), false, 8, null
      );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$3$lambda$2`(`$this_ret`: CalleeCBImpl.EvalCall, `$this$valueCmp`: IOpCalculator, ret: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      val lop: CompanionV = var3[0];
      val rop: CompanionV = var3[1];
      if (lop.getValue() == var3[1].getValue()) {
         `$this$valueCmp`.getRes().add(`$this_ret`.getHf().push(`$this_ret`.getEnv(), `$this_ret`.getHf().toConstVal(true)).popHV());
         return true;
      } else {
         var var10000: ByteArray = WStringKt.getByteArray(`$this_ret`, lop.getValue() as IValue);
         if (var10000 == null) {
            return false;
         } else {
            var10000 = WStringKt.getByteArray(`$this_ret`, rop.getValue() as IValue);
            if (var10000 == null) {
               return false;
            } else {
               ret.add(`$this_ret`.getHf().push(`$this_ret`.getEnv(), `$this_ret`.getHf().toConstVal(Arrays.equals(var10000, var10000))).pop());
               return true;
            }
         }
      }
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$3`(): Unit {
      val equalsOp: IOpCalculator = `$this$ret`.getHf().resolveOp(`$this$ret`.getEnv(), `$this$ret`.arg(0), `$this$ret`.arg(1));
      equalsOp.resolve(WStringLatin1::register$lambda$3$lambda$2);
      val var5: BooleanType = G.v().soot_BooleanType();
      equalsOp.putSummaryIfNotConcrete(var5 as Type, "return");
      `$this$ret`.setReturn(equalsOp.getRes().build());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$5$lambda$4`(`$this_ret`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      val value: CompanionV = var3[0];
      val var10000: ByteArray = WStringKt.getByteArray(`$this_ret`, var3[0].getValue() as IValue);
      if (var10000 == null) {
         return false;
      } else {
         res.add(
            `$this_ret`.getHf()
               .push(`$this_ret`.getEnv(), `$this_ret`.getHf().toConstVal(new java.lang.String(var10000, Charsets.UTF_8).hashCode()))
               .markOfStringLatin1Hash(value)
               .pop()
         );
         return true;
      }
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$5`(): Unit {
      val valueOp: IOpCalculator = `$this$ret`.getHf().resolveOp(`$this$ret`.getEnv(), `$this$ret`.arg(0));
      valueOp.resolve(WStringLatin1::register$lambda$5$lambda$4);
      valueOp.putSummaryIfNotConcrete(`$this$ret`.getHf().getVg().getBYTE_ARRAY_TYPE() as Type, "return");
      `$this$ret`.setReturn(valueOp.getRes().build());
      return Unit.INSTANCE;
   }

   public companion object {
      public fun v(): WStringLatin1 {
         return new WStringLatin1();
      }
   }
}
