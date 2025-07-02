package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue

public class ObjectValues(values: IHeapValues<IValue>) : IData<IValue> {
   public final val values: IHeapValues<IValue>
   private final var hashCode: Int?

   init {
      this.values = values;
   }

   public override fun reference(res: MutableCollection<IValue>) {
      this.values.reference(res);
   }

   public open fun builder(): ObjectValuesBuilder {
      return new ObjectValuesBuilder(this, this.values.builder());
   }

   public override fun computeHash(): Int {
      return this.values.hashCode();
   }

   public override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
      if (that is ObjectValues) {
         this.values.diff(cmp, (that as ObjectValues).values);
      }
   }

   public override fun hashCode(): Int {
      var hash: Int = this.hashCode;
      if (this.hashCode == null) {
         hash = this.computeHash();
         this.hashCode = hash;
      }

      return hash;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ObjectValues) {
         return false;
      } else {
         return this.hashCode() == (other as ObjectValues).hashCode() && this.values == (other as ObjectValues).values;
      }
   }

   public override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
      val b: ObjectValuesBuilder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }

   public override fun toString(): String {
      return java.lang.String.valueOf(this.values);
   }
}
