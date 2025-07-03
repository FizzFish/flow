package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapKV
import kotlinx.collections.immutable.PersistentMap
import soot.RefType

public class FieldSpace<V>(
    clz: RefType,
    fields: PersistentMap<JFieldType, IHeapValues<Any>>,
    unreferenced: IHeapValues<Any>?
) : FieldHeapKV(clz, fields, unreferenced) {
    public override fun builder(): IHeapKVData.Builder<JFieldType, Any> {
        val var10003: RefType = this.getClz()
        val var10004 = this.getMap().builder()
        val var10005: IHeapValues<Any>? = this.getUnreferenced()
        return FieldSpaceBuilder<Any>(this, var10003, var10004, var10005?.builder()) as IHeapKVData.Builder<JFieldType, Any>
    }

    public override fun getName(): String {
        return "field(${this.getClz()})"
    }

    public override fun cloneAndReNewObjects(re: IReNew<Any>): IData<Any> {
        val b = this.builder()
        b.cloneAndReNewObjects(re)
        return b.build()
    }
}