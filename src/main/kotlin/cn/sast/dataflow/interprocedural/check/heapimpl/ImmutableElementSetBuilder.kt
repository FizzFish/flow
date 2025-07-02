package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public class ImmutableElementSetBuilder<E>(fields: Builder<Any, IHeapValues<IValue>>,
   unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>?
) : ImmutableElementHashMapBuilder(fields, unreferenced) {
   public open fun build(): ImmutableElementSet<Any> {
      val var10002: PersistentMap = this.getMap().build();
      val var10003: IHeapValues.Builder = this.getUnreferenced();
      return new ImmutableElementSet<>(var10002, if (var10003 != null) var10003.build() else null);
   }
}
