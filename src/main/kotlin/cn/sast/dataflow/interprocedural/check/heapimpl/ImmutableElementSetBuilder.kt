package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public class ImmutableElementSetBuilder<E>(
    fields: Builder<Any, IHeapValues<IValue>>,
    unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<IValue>?
) : ImmutableElementHashMapBuilder(fields, unreferenced) {
    public open fun build(): ImmutableElementSet<Any> {
        val map: PersistentMap<Any, IHeapValues<IValue>> = this.getMap().build()
        val unreferencedBuilder: IHeapValues.Builder<IValue>? = this.getUnreferenced()
        return ImmutableElementSet(
            map,
            unreferencedBuilder?.build()
        )
    }
}