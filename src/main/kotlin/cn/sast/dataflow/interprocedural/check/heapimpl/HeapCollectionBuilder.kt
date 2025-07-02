package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public class HeapCollectionBuilder<E>(fields: Builder<Int, IHeapValues<Any>>, unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>?) : HeapDataBuilder(
      fields, unreferenced
   ) {
   public open fun build(): HeapCollection<Any> {
      val var10002: PersistentMap = this.getMap().build();
      val var10003: IHeapValues.Builder = this.getUnreferenced();
      return new HeapCollection<>(var10002, if (var10003 != null) var10003.build() else null);
   }
}
