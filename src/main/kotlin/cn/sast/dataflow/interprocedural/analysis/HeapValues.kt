package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf

/** 具体实现的不可变 [AbstractHeapValues] */
class HeapValues(
   map: PersistentMap<IValue, CompanionV<IValue>> = persistentHashMapOf()
) : AbstractHeapValues<IValue>(map) {

   override fun builder(): HeapValuesBuilder =
      HeapValuesBuilder(this, map.builder())

   companion object {
      internal fun empty(): HeapValues = HeapValues(persistentHashMapOf())
   }
}
