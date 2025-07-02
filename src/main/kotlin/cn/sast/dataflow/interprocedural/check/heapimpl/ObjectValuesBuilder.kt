package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder

public class ObjectValuesBuilder(orig: ObjectValues, values: Builder<IValue>) : IData.Builder<IValue> {
   private final val orig: ObjectValues
   public final val values: Builder<IValue>

   init {
      this.orig = orig;
      this.values = values;
   }

   public override fun union(hf: AbstractHeapFactory<IValue>, that: IData<IValue>) {
      if (that !is ObjectValues) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         this.values.add((that as ObjectValues).getValues());
      }
   }

   public override fun cloneAndReNewObjects(re: IReNew<IValue>) {
      this.values.cloneAndReNewObjects(re);
   }

   public fun addAll(values: IHeapValues<IValue>) {
      this.values.add(values);
   }

   public open fun build(): ObjectValues {
      val var1: IHeapValues = this.values.build();
      return if (this.orig.getValues() == var1) this.orig else new ObjectValues(var1);
   }

   public override fun toString(): String {
      return java.lang.String.valueOf(this.values);
   }
}
