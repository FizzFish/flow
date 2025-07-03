package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import soot.Unit

public class AssignLocalPath private constructor(
    node: Unit,
    val lhs: Any,
    val rhsValue: String,
    val rhsValePath: IPath
) : IPath() {
    public val node: Unit = node
    public var hash: Int? = null
        internal set

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is AssignLocalPath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.getNode() != other.getNode()) {
            return false
        } else if (this.lhs != other.lhs) {
            return false
        } else if (this.rhsValue != other.rhsValue) {
            return false
        } else {
            return this.rhsValePath === other.rhsValePath
        }
    }

    public override fun equivHashCode(): Int {
        var result: Int = this.hash ?: run {
            val temp = 31 * (31 * (31 * System.identityHashCode(this.getNode()) + lhs.hashCode()) + rhsValue.hashCode()) + rhsValePath.hashCode()
            hash = temp
            temp
        }
        return result
    }

    public companion object {
        public fun v(env: HeapValuesEnv, lhs: Any, rhsValue: IValue, rhsValePath: IPath): AssignLocalPath {
            return IPath.Companion.getInterner(AssignLocalPath(env.getNode(), lhs, rhsValue.toString(), rhsValePath))
        }
    }
}