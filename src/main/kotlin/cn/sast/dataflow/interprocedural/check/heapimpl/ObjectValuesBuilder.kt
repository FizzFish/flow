package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder

public class ObjectValuesBuilder(
    private val orig: ObjectValues,
    public val values: Builder<IValue>
) : IData.Builder<IValue> {

    public override fun union(hf: AbstractHeapFactory<IValue>, that: IData<IValue>) {
        if (that !is ObjectValues) {
            throw IllegalArgumentException("Failed requirement.")
        } else {
            this.values.add((that as ObjectValues).getValues())
        }
    }

    public override fun cloneAndReNewObjects(re: IReNew<IValue>) {
        this.values.cloneAndReNewObjects(re)
    }

    public fun addAll(values: IHeapValues<IValue>) {
        this.values.add(values)
    }

    public open fun build(): ObjectValues {
        val var1: IHeapValues<IValue> = this.values.build()
        return if (this.orig.getValues() == var1) this.orig else ObjectValues(var1)
    }

    public override fun toString(): String {
        return this.values.toString()
    }
}