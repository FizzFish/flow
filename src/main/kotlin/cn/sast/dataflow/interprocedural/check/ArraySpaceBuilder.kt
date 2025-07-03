package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapBuilder
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder

public open class ArraySpaceBuilder internal constructor(
    orig: ArraySpace,
    element: Builder<Int, IHeapValues<IValue>>,
    unreferenced: IHeapValues.Builder<IValue>?
) : ArrayHeapBuilder(
    element,
    unreferenced,
    orig.getType(),
    orig.getAllSize().builder(),
    orig.getSize(),
    orig.getInitializedValue()
) {
    public val orig: ArraySpace = orig

    public override fun build(): IData<IValue> {
        val newMap = getMap().build()
        val var10000 = getUnreferenced()
        val newUn = var10000?.build()
        val newAllSize = getAllSize().build()
        return if (newMap === orig.getMap() &&
            newUn === orig.getUnreferenced() &&
            newAllSize === orig.getAllSize() &&
            getInitializedValue() === orig.getInitializedValue()
        ) {
            orig
        } else {
            ArraySpace(
                newMap,
                newUn,
                getType(),
                newAllSize,
                getSize(),
                getInitializedValue()
            )
        }
    }
}