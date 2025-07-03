package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.StmtModelingEnv
import soot.Unit

public class ModelBind private constructor(
    node: Unit,
    obj: IValue,
    mt: Any,
    data: Any,
    info: ModelingStmtInfo?,
    prev: IPath
) : IPath() {
    public open val node: Unit = node
    public final val obj: IValue = obj
    public final val mt: Any = mt
    public final val data: Any = data
    public final val info: ModelingStmtInfo? = info
    public final val prev: IPath = prev

    public final var hash: Int? = null
        internal set

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is ModelBind) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.getNode() != other.getNode()) {
            return false
        } else if (this.obj != other.obj) {
            return false
        } else if (this.mt != other.mt) {
            return false
        } else if (this.data != other.data) {
            return false
        } else if (this.info != other.info) {
            return false
        } else {
            return this.prev === other.prev
        }
    }

    public override fun equivHashCode(): Int {
        var result = this.hash
        if (result == null) {
            result = 31 * (31 * (31 * (31 * (31 * System.identityHashCode(this.getNode()) + obj.hashCode()) + mt.hashCode()) + data.hashCode()) + (info?.hashCode() ?: 0)) + prev.hashCode()
            this.hash = result
        }
        return result
    }

    public companion object {
        public fun v(env: HeapValuesEnv, obj: IValue, mt: Any, data: IData<IValue>, prev: IPath): ModelBind {
            return IPath.Companion.getInterner(
                ModelBind(
                    env.getNode(),
                    obj,
                    mt,
                    mt,
                    (env as? StmtModelingEnv)?.getInfo(),
                    prev
                )
            )
        }
    }
}