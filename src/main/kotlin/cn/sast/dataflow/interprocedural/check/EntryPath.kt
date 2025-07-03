package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import kotlinx.collections.immutable.PersistentList
import soot.SootMethod
import soot.Unit

public class EntryPath private constructor(special: PersistentList<ReferenceContext>, node: Unit) : IPath() {
    public open val node: Unit
    public final val special: PersistentList<ReferenceContext>

    public final var hash: Int? = null
        internal set

    init {
        this.node = node
        this.special = IPath.Companion.specialInterner(special)
    }

    public override fun toString(): String {
        return "${this.special.joinToString(":")} ${this.getNode()}"
    }

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is EntryPath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.getNode() != other.getNode()) {
            return false
        } else {
            return this.special === other.special
        }
    }

    public override fun equivHashCode(): Int {
        var result = this.hash
        if (result == null) {
            result = 31 * System.identityHashCode(this.getNode()) + System.identityHashCode(this.special)
            this.hash = result
        }
        return result
    }

    public companion object {
        public fun v(special: PersistentList<ReferenceContext>, method: SootMethod, env: HeapValuesEnv): EntryPath {
            return IPath.Companion.getInterner(EntryPath(special, env.getNode()))
        }
    }
}