package cn.sast.api.report

import soot.SootMethod
import soot.Unit

public data class CoverTaint(
    override val method: SootMethod,
    override val unit: Unit,
    public val value: Any
) : CoverSootCode(method, unit) {

    public operator fun component1(): SootMethod {
        return this.method
    }

    public operator fun component2(): Unit {
        return this.unit
    }

    public operator fun component3(): Any {
        return this.value
    }

    public fun copy(method: SootMethod = this.method, unit: Unit = this.unit, value: Any = this.value): CoverTaint {
        return CoverTaint(method, unit, value)
    }

    public override fun toString(): String {
        return "CoverTaint(method=${this.method}, unit=${this.unit}, value=${this.value})"
    }

    public override fun hashCode(): Int {
        return (this.method.hashCode() * 31 + this.unit.hashCode()) * 31 + this.value.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is CoverTaint) {
            return false
        } else {
            val var2: CoverTaint = other
            if (!(this.method == other.method)) {
                return false
            } else if (!(this.unit == var2.unit)) {
                return false
            } else {
                return this.value == var2.value
            }
        }
    }
}