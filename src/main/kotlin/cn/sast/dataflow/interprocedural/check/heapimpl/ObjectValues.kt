package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue

public class ObjectValues(values: IHeapValues<IValue>) : IData<IValue> {
    public val values: IHeapValues<IValue>
    private var hashCode: Int? = null

    init {
        this.values = values
    }

    override fun reference(res: MutableCollection<IValue>) {
        this.values.reference(res)
    }

    open fun builder(): ObjectValuesBuilder {
        return ObjectValuesBuilder(this, this.values.builder())
    }

    override fun computeHash(): Int {
        return this.values.hashCode()
    }

    override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
        if (that is ObjectValues) {
            this.values.diff(cmp, that.values)
        }
    }

    override fun hashCode(): Int {
        var hash = this.hashCode
        if (hash == null) {
            hash = this.computeHash()
            this.hashCode = hash
        }
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is ObjectValues) {
            return false
        } else {
            return this.hashCode() == other.hashCode() && this.values == other.values
        }
    }

    override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
        val b = this.builder()
        b.cloneAndReNewObjects(re)
        return b.build()
    }

    override fun toString(): String {
        return this.values.toString()
    }
}