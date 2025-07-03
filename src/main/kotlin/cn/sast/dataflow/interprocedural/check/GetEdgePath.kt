package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import soot.Unit

public class GetEdgePath private constructor(
    node: Unit,
    heapObject: IValue,
    heapObjectPath: IPath,
    mt: Any,
    key: Any?,
    value: IValue,
    valuePath: IPath,
    info: Any?
) : IPath() {
    public open val node: Unit = node
    public final val heapObject: IValue = heapObject
    public final val heapObjectPath: IPath = heapObjectPath
    public final val mt: Any = mt
    public final val key: Any? = key
    public final val value: IValue = value
    public final val valuePath: IPath = valuePath
    public final val info: Any? = info

    public final var hash: Int? = null
        internal set

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is GetEdgePath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.getNode() != other.getNode()) {
            return false
        } else if (!(this.heapObject == other.heapObject)) {
            return false
        } else if (this.heapObjectPath != other.heapObjectPath) {
            return false
        } else if (!(this.mt == other.mt)) {
            return false
        } else if (!(this.key == other.key)) {
            return false
        } else if (!(this.value == other.value)) {
            return false
        } else if (this.valuePath != other.valuePath) {
            return false
        } else {
            return this.info == other.info
        }
    }

    public override fun equivHashCode(): Int {
        var result: Int = this.hash ?: run {
            val temp = 31 * (31 * (31 * (31 * (31 * (31 * (31 * System.identityHashCode(this.getNode()) + heapObject.hashCode()) + heapObjectPath.hashCode()) + mt.hashCode()) + (key?.hashCode() ?: 0)) + value.hashCode()) + valuePath.hashCode()
            (if (info != null) temp + info.hashCode() else temp).also { hash = it }
        }
        return result
    }

    public companion object {
        public fun v(
            env: HeapValuesEnv,
            heapObject: IValue,
            heapObjectPath: IPath,
            mt: Any,
            key: Any?,
            v: IValue,
            value: IPath,
            info: Any? = null
        ): GetEdgePath {
            return IPath.Companion.getInterner(GetEdgePath(env.getNode(), heapObject, heapObjectPath, mt, key, v, value, info))
        }
    }
}