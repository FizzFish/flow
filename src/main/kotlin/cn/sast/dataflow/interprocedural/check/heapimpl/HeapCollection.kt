package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapKVData
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import kotlinx.collections.immutable.PersistentMap

public class HeapCollection<E>(
    element: PersistentMap<Int, IHeapValues<Any>>,
    unreferenced: IHeapValues<Any>?
) : HeapKVData<Int, Any>(element, unreferenced) {
    public open fun isValidKey(key: Int?): Boolean? {
        return true
    }

    public override fun getName(): String {
        return "Collection"
    }

    public override fun builder(): IHeapKVData.Builder<Int, Any> {
        val var10002 = this.getMap().builder()
        val var10003 = this.getUnreferenced()
        return HeapCollectionBuilder(var10002, var10003?.builder()) as IHeapKVData.Builder<Int, Any>
    }

    public override fun cloneAndReNewObjects(re: IReNew<Any>): IData<Any> {
        val b = this.builder()
        b.cloneAndReNewObjects(re)
        return b.build()
    }
}