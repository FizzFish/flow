package cn.sast.framework.report.sqldelight

public data class ControlFlowPath(
    val __control_flow_array_hash_id: Long,
    val control_flow_sequence: Long,
    val __control_flow_id: Long
) {
    public operator fun component1(): Long {
        return this.__control_flow_array_hash_id
    }

    public operator fun component2(): Long {
        return this.control_flow_sequence
    }

    public operator fun component3(): Long {
        return this.__control_flow_id
    }

    public fun copy(
        __control_flow_array_hash_id: Long = this.__control_flow_array_hash_id,
        control_flow_sequence: Long = this.control_flow_sequence,
        __control_flow_id: Long = this.__control_flow_id
    ): ControlFlowPath {
        return ControlFlowPath(__control_flow_array_hash_id, control_flow_sequence, __control_flow_id)
    }

    public override fun toString(): String {
        return "ControlFlowPath(__control_flow_array_hash_id=${this.__control_flow_array_hash_id}, control_flow_sequence=${this.control_flow_sequence}, __control_flow_id=${this.__control_flow_id})"
    }

    public override fun hashCode(): Int {
        return (java.lang.Long.hashCode(this.__control_flow_array_hash_id) * 31 + java.lang.Long.hashCode(this.control_flow_sequence)) * 31 +
            java.lang.Long.hashCode(this.__control_flow_id)
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is ControlFlowPath) {
            return false
        } else {
            val var2: ControlFlowPath = other
            if (this.__control_flow_array_hash_id != other.__control_flow_array_hash_id) {
                return false
            } else if (this.control_flow_sequence != var2.control_flow_sequence) {
                return false
            } else {
                return this.__control_flow_id == var2.__control_flow_id
            }
        }
    }
}