package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import com.feysh.corax.config.api.IIexConst
import soot.Unit
import soot.jimple.Constant

internal class LiteralPath private constructor(node: Unit, const: Any) : IPath() {
    override val node: Unit
    val const: Any

    var hash: Int? = null
        internal set

    init {
        this.node = node
        this.const = const
    }

    override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is LiteralPath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.node != other.node) {
            return false
        } else {
            return this.const == other.const
        }
    }

    override fun equivHashCode(): Int {
        var result = this.hash
        if (this.hash == null) {
            result = 31 * System.identityHashCode(this.node) + this.const.hashCode()
            this.hash = result
        }
        return result!!
    }

    companion object {
        fun v(env: HeapValuesEnv, const: Constant, info: Any?): LiteralPath {
            return IPath.Companion.getInterner(LiteralPath(env.getNode(), const))
        }

        fun v(env: HeapValuesEnv, constIex: IIexConst): LiteralPath {
            return IPath.Companion.getInterner(LiteralPath(env.getNode(), constIex))
        }
    }
}