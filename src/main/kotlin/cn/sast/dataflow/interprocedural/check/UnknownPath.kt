package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import soot.Unit

public class UnknownPath private constructor(node: Unit) : IPath() {
    public override val node: Unit

    public final var hash: Int? = null
        internal set

    init {
        this.node = node
    }

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is UnknownPath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else {
            return this.getNode() === other.getNode()
        }
    }

    public override fun equivHashCode(): Int {
        var result = this.hash
        if (this.hash == null) {
            result = System.identityHashCode(this.getNode())
            this.hash = result
        }

        return result ?: 0
    }

    public companion object {
        public fun v(env: HeapValuesEnv): UnknownPath {
            return IPath.Companion.getInterner(UnknownPath(env.getNode()))
        }
    }
}