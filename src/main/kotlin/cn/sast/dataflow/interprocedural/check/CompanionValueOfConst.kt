package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.IValue

internal class CompanionValueOfConst(value: IValue, path: IPath, attr: IValue) : CompanionV<IValue>(value), PathCompanionV {
    override val path: IPath
    val attr: IValue

    init {
        this.path = path
        this.attr = attr
        if (value !is ConstVal) {
            throw IllegalStateException(value.toString())
        }
    }

    override fun copy(updateValue: IValue): CompanionV<IValue> {
        return CompanionValueOfConst(updateValue, this.getPath(), this.attr)
    }

    override fun union(other: CompanionV<IValue>): CompanionV<IValue> {
        if (this.getValue() != other.getValue()) {
            throw IllegalArgumentException("Failed requirement.")
        } else {
            return CompanionValueOfConst(
                this.getValue(), 
                MergePath.Companion.v(HeapValuesEnvImpl(this.getPath()), this.getPath(), (other as PathCompanionV).getPath()), 
                this.attr
            )
        }
    }

    override fun toString(): String {
        return "<v=${this.getValue()}, attr=${this.attr}>"
    }

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) {
            return false
        } else {
            return other is CompanionValueOfConst && this.attr == other.attr
        }
    }

    override fun computeHash(): Int {
        return 31 * super.computeHash() + this.attr.hashCode()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}