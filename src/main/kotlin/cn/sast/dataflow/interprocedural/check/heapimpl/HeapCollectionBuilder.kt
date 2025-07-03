package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public class HeapCollectionBuilder<E>(
    fields: Builder<Int, IHeapValues<Any>>,
    unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>?
) : HeapDataBuilder(fields, unreferenced) {
    public open fun build(): HeapCollection<Any> {
        val map: PersistentMap<Int, IHeapValues<Any>> = this.getMap().build()
        val unreferencedBuilder: IHeapValues.Builder<Any>? = this.getUnreferenced()
        return HeapCollection(
            map,
            unreferencedBuilder?.build()
        )
    }
}