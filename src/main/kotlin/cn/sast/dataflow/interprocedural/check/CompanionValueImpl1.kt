package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.IValue

internal class CompanionValueImpl1(value: IValue, override val path: IPath) : CompanionV(value), PathCompanionV {

    override fun union(other: CompanionV<IValue>): CompanionV<IValue> {
        if (this.getValue() != other.getValue()) {
            throw IllegalArgumentException("Failed requirement.")
        } else {
            return CompanionValueImpl1(
                this.getValue(),
                MergePath.Companion.v(HeapValuesEnvImpl(this.getPath()), this.getPath(), (other as PathCompanionV).getPath())
            )
        }
    }

    override fun toString(): String {
        return "<${this.getValue()}>"
    }

    fun copy(updateValue: IValue): CompanionV<IValue> {
        return CompanionValueImpl1(updateValue, this.getPath())
    }
}