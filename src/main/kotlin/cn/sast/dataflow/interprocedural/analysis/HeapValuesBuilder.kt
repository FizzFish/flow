package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentMap

/** 具体实现的可变 Builder */
class HeapValuesBuilder(
   val orig: HeapValues,
   map: PersistentMap<IValue, CompanionV<IValue>>.Builder
) : AbstractHeapValuesBuilder<IValue>(orig, map) {

   override fun build(): IHeapValues<IValue> =
      if (map.build() === orig.map) orig else HeapValues(map.build())
}
