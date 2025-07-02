package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import soot.ArrayType
import soot.G
import soot.IntType
import soot.PrimType
import soot.RefType
import soot.Type
import soot.jimple.ClassConstant
import soot.jimple.Constant

public class WObject : SummaryHandlePackage<IValue> {
   public final val jClassType: RefType = RefType.v("java.lang.Class")

   public override fun ACheckCallAnalysis.register() {
      `$this$register`.evalCall(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), WObject::register$lambda$1);
      `$this$register`.evalCall(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), WObject::register$lambda$3);
      `$this$register`.evalCall(UtilsKt.getSootSignature(<unrepresentable>.INSTANCE as KCallable<?>), WObject::register$lambda$5);
   }

   @JvmStatic
   fun `register$lambda$1$lambda$0`(
      `$this_evalCall`: CalleeCBImpl.EvalCall, `this$0`: WObject, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var4: Array<CompanionV>
   ): Boolean {
      val cop: CompanionV = var4[0];
      val op: IValue = var4[0].getValue() as IValue;
      if (!op.typeIsConcrete()) {
         return false;
      } else {
         val type: Type = op.getType();
         val var10000: ClassConstant = if (type !is RefType && type !is ArrayType && type !is PrimType) null else ClassConstant.fromType(type);
         if (var10000 == null) {
            return false;
         } else {
            val var10001: AbstractHeapFactory = `$this_evalCall`.getHf();
            val var10002: HeapValuesEnv = `$this_evalCall`.getEnv();
            val var10003: AbstractHeapFactory = `$this_evalCall`.getHf();
            val var10004: Constant = var10000 as Constant;
            val var10005: RefType = `this$0`.jClassType;
            res.add(var10001.push(var10002, var10003.newConstVal(var10004, var10005 as Type)).markOfGetClass(cop).pop());
            return true;
         }
      }
   }

   @JvmStatic
   fun `register$lambda$1`(`this$0`: WObject, `$this$evalCall`: CalleeCBImpl.EvalCall): Unit {
      val unop: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(-1));
      unop.resolve(WObject::register$lambda$1$lambda$0);
      val var5: RefType = `this$0`.jClassType;
      unop.putSummaryIfNotConcrete(var5 as Type, "return");
      `$this$evalCall`.setReturn(unop.getRes().build());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$3$lambda$2`(`$this_evalCall`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      val th1s: CompanionV = var3[0];
      val that: CompanionV = var3[1];
      if ((var3[1].getValue() as IValue).isNullConstant()) {
         res.add(
            `$this_evalCall`.getHf().push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(false)).markOfObjectEqualsResult(th1s, that).pop()
         );
      } else {
         res.add(
            `$this_evalCall`.getHf()
               .push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(th1s.getValue() == that.getValue()))
               .markOfObjectEqualsResult(th1s, that)
               .pop()
         );
      }

      return true;
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$3`(): Unit {
      val strObjectOp: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(-1), `$this$evalCall`.arg(0));
      strObjectOp.resolve(WObject::register$lambda$3$lambda$2);
      `$this$evalCall`.setReturn(strObjectOp.getRes().build());
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `register$lambda$5$lambda$4`(`$this_evalCall`: CalleeCBImpl.EvalCall, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var3: Array<CompanionV>): Boolean {
      res.add(
         `$this_evalCall`.getHf()
            .push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(var3[0].hashCode()))
            .markOfReturnValueOfMethod(`$this_evalCall`)
            .pop()
      );
      return true;
   }

   @JvmStatic
   fun CalleeCBImpl.EvalCall.`register$lambda$5`(): Unit {
      val unop: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(0));
      unop.resolve(WObject::register$lambda$5$lambda$4);
      val var4: IntType = G.v().soot_IntType();
      unop.putSummaryIfNotConcrete(var4 as Type, "return");
      `$this$evalCall`.setReturn(unop.getRes().build());
      return Unit.INSTANCE;
   }

   public companion object {
      public fun v(): WObject {
         return new WObject();
      }
   }
}
