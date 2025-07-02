package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder

public class OpCalculator(env: HeapValuesEnv, cf: AbstractHeapFactory<IValue>, vararg ops: IHeapValues<IValue>?) : CalculatorBase(env, cf),
   IOpCalculator<IValue> {
   public final val ops: Array<out IHeapValues<IValue>?>

   init {
      this.ops = ops;
   }

   public override fun resolve(fx: (IOpCalculator<IValue>, Builder<IValue>, Array<CompanionV<IValue>>) -> Boolean): IOpCalculator<IValue> {
      val sz: Int = this.ops.length;
      var valueArray: Int = 0;

      val iter: Array<Int>;
      for (iter = new Integer[sz]; valueArray < sz; valueArray++) {
         val var10002: IHeapValues = this.ops[valueArray];
         if (this.ops[valueArray] == null) {
            return this as IOpCalculator<IValue>;
         }

         val op: Int = var10002.getSize();
         if (op.intValue() == 0) {
            return this as IOpCalculator<IValue>;
         }

         iter[valueArray] = op;
      }

      val sizeArray: Array<Int> = iter;
      var var16: Int = 0;

      val counter: Array<Array<CompanionV>>;
      for (counter = new CompanionV[sz][]; var16 < sz; var16++) {
         counter[var16] = new CompanionV[sizeArray[var16]];
      }

      val var15: Array<Array<CompanionV>> = counter;
      var var18: Int = 0;

      val var20: Array<java.util.Iterator>;
      for (var20 = new java.util.Iterator[sz]; var18 < sz; var18++) {
         val var30: IHeapValues = this.ops[var18];
         var20[var18] = var30.iterator();
      }

      val var17: Array<java.util.Iterator> = var20;
      var var21: Int = 0;

      val var23: Array<Int>;
      for (var23 = new Integer[sz]; var21 < sz; var21++) {
         var23[var21] = 0;
      }

      val var19: Array<Int> = var23;
      var var24: Int = 0;

      val var26: Array<CompanionV>;
      for (var26 = new CompanionV[sz]; var24 < sz; var24++) {
         val value: CompanionV = var17[var24].next() as CompanionV;
         var15[var24][0] = value;
         var26[var24] = value;
      }

      val var22: Array<CompanionV> = var26;

      while (this.getCount() < this.getCalculateLimit()) {
         val var25: Boolean = fx.invoke(this, this.getRes(), var22) as java.lang.Boolean;
         this.setCount(this.getCount() + 1);
         if (!var25) {
            this.getUnHandle().add(var22);
         }

         val var28: Int = 0;

         while (true) {
            if (var19[var28] == sizeArray[var28] - 1) {
               var19[var28] = 0;
               val var31: CompanionV = var15[var28][0];
               var22[var28] = var31;
               if (++var28 != sz) {
                  continue;
               }
            } else {
               var19[var28] = var19[var28] + 1;
               val x: Int = var19[var28];
               var var29: CompanionV = var15[var28][x];
               if (var15[var28][x] == null) {
                  var29 = var17[var28].next() as CompanionV;
                  var15[var28][x] = var29;
               }

               var22[var28] = var29;
            }

            if (var28 == sz) {
               return this as IOpCalculator<IValue>;
            }
            break;
         }
      }

      return this as IOpCalculator<IValue>;
   }
}
