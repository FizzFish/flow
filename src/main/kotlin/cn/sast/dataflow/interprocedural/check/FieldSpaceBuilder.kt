package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapBuilder
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder
import soot.RefType

public class FieldSpaceBuilder<V>(
    orig: FieldSpace<Any>,
    clz: RefType,
    fields: Builder<JFieldType, IHeapValues<Any>>,
    unreferenced: cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder<Any>?
) : FieldHeapBuilder(clz, fields, unreferenced) {
    public val orig: FieldSpace<Any> = orig

    public override fun build(): IData<Any> {
        val newMap: PersistentMap<JFieldType, IHeapValues<Any>> = this.getMap().build()
        val var10000: IHeapValues.Builder<Any>? = this.getUnreferenced()
        val newUn: IHeapValues<Any>? = if (var10000 != null) var10000.build() else null
        return if (newMap === this.orig.getMap() && newUn === this.orig.getUnreferenced()) this.orig else FieldSpace(this.getClz(), newMap, newUn)
    }
}