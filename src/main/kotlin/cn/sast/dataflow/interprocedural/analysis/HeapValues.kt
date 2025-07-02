package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap

internal class HeapValues(map: PersistentMap<IValue, CompanionV<IValue>> = ExtensionsKt.persistentHashMapOf()) : AbstractHeapValues(map) {
   public open fun builder(): HeapValuesBuilder {
      return new HeapValuesBuilder(this, null, 2, null);
   }

   fun HeapValues() {
      this(null, 1, null);
   }

   public companion object {
      internal fun empty(): HeapValues {
         return new HeapValues(ExtensionsKt.persistentHashMapOf());
      }
   }
}
