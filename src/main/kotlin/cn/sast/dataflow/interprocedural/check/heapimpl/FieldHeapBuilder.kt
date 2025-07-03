package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import kotlinx.collections.immutable.PersistentMap.Builder
import soot.RefType

public abstract class FieldHeapBuilder<V> : HeapDataBuilder<JFieldType, V> {
    public val clz: RefType

    constructor(clz: RefType, fields: Builder<JFieldType, IHeapValues<V>>, unreferenced: IHeapValuesBuilder<V>?) : super(fields, unreferenced) {
        this.clz = clz
    }
}