package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapBuilder
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public open class ArraySpaceBuilder internal constructor(orig: ArraySpace,
   element: Builder<Int, IHeapValues<IValue>>,
   unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>?
) : ArrayHeapBuilder(element, unreferenced, orig.getType(), orig.getAllSize().builder(), orig.getSize(), orig.getInitializedValue()) {
   public final val orig: ArraySpace

   init {
      this.orig = orig;
   }

   public override fun build(): IData<IValue> {
      val newMap: PersistentMap = this.getMap().build();
      val var10000: IHeapValues.Builder = this.getUnreferenced();
      val newUn: IHeapValues = if (var10000 != null) var10000.build() else null;
      val newAllSize: IHeapValues = this.getAllSize().build();
      return if (newMap === this.orig.getMap()
            && newUn === this.orig.getUnreferenced()
            && newAllSize === this.orig.getAllSize()
            && this.getInitializedValue() === this.orig.getInitializedValue())
         this.orig
         else
         new ArraySpace(newMap, newUn, this.getType(), newAllSize, this.getSize(), this.getInitializedValue());
   }
}
