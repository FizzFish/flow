package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import soot.SootMethod
import soot.Unit

public class InvokeEdgePath private constructor(
    node: Unit,
    interproceduralPathMap: Map<IPath, EntryPath>,
    path: IPath,
    container: SootMethod,
    callee: SootMethod
) : IPath() {
    public open val node: Unit
    public final val path: IPath
    public final val container: SootMethod
    public final val callee: SootMethod
    public final val interproceduralPathMap: Map<IPath, EntryPath>

    public final var hash: Int? = null
        internal set

    init {
        this.node = node
        this.path = path
        this.container = container
        this.callee = callee
        this.interproceduralPathMap = PathCompanionKt.access$getShort(interproceduralPathMap)
    }

    public override fun toString(): String {
        return "${this.container} ${this.getNode()}"
    }

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is InvokeEdgePath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.getNode() != other.getNode()) {
            return false
        } else if (this.path != other.path) {
            return false
        } else if (this.container != other.container) {
            return false
        } else {
            return this.callee == other.callee
        }
    }

    public override fun equivHashCode(): Int {
        var result = this.hash
        if (this.hash == null) {
            result = 31 * (31 * (31 * System.identityHashCode(this.getNode()) + this.path.hashCode()) + this.container.hashCode() + this.callee.hashCode()
            this.hash = result
        }
        return result!!
    }

    public companion object {
        public fun v(
            env: HeapValuesEnv,
            interproceduralPathMap: Map<IPath, EntryPath>,
            path: IPath,
            container: SootMethod,
            callee: SootMethod
        ): InvokeEdgePath {
            return IPath.Companion.getInterner(InvokeEdgePath(env.getNode(), interproceduralPathMap, path, container, callee))
        }
    }
}