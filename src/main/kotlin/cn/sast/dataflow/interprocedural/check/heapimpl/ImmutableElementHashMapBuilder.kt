package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public open class ImmutableElementHashMapBuilder<K, V>(
    fields: Builder<Any, IHeapValues<Any>>,
    unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>?
) : HeapDataBuilder(fields, unreferenced) {
    public open fun build(): ImmutableElementHashMap<Any, Any> {
        val map: PersistentMap<Any, IHeapValues<Any>> = this.getMap().build()
        val unreferenced: IHeapValues<Any>? = this.getUnreferenced()?.build()
        return ImmutableElementHashMap(map, unreferenced)
    }
}