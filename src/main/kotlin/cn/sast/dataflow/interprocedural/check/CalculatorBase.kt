package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.ICalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import java.util.LinkedHashSet
import soot.RefType
import soot.Type

public abstract class CalculatorBase : ICalculator<IValue> {
   public final val env: HeapValuesEnv
   public final val cf: AbstractHeapFactory<IValue>

   public final var count: Int
      internal set

   public final val calculateLimit: Int
   public final val unHandle: MutableSet<Any>

   public open var res: Builder<IValue>
      internal final set

   open fun CalculatorBase(env: HeapValuesEnv, cf: AbstractHeapFactory<IValue>) {
      this.env = env;
      this.cf = cf;
      this.calculateLimit = 24;
      this.unHandle = new LinkedHashSet<>();
      this.res = this.cf.empty().builder();
   }

   public override fun putSummaryIfNotConcrete(type: Type, special: Any) {
      if (!this.isFullySimplified()) {
         this.putSummaryValue(type, special);
      }
   }

   public override fun putSummaryValue(type: Type, special: Any) {
      if (type is RefType) {
         this.getRes().add(this.cf.push(this.env, this.cf.getNullConst()).markOfCantCalcAbstractResultValue().pop());
      }

      this.getRes().add(this.cf.push(this.env, this.cf.newSummaryVal(this.env, type, special)).markOfCantCalcAbstractResultValue().pop());
   }

   public override fun isFullySimplified(): Boolean {
      return this.count < this.calculateLimit && this.unHandle.isEmpty();
   }
}
