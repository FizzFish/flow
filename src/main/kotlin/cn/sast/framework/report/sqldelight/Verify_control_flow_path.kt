package cn.sast.framework.report.sqldelight

public data class Verify_control_flow_path(
    public val id: Long,
    public val __control_flow_array_hash_id: Long
) {
    public operator fun component1(): Long = id

    public operator fun component2(): Long = __control_flow_array_hash_id

    public fun copy(
        id: Long = this.id,
        __control_flow_array_hash_id: Long = this.__control_flow_array_hash_id
    ): Verify_control_flow_path = Verify_control_flow_path(id, __control_flow_array_hash_id)

    public override fun toString(): String =
        "Verify_control_flow_path(id=$id, __control_flow_array_hash_id=$__control_flow_array_hash_id)"

    public override fun hashCode(): Int =
        id.hashCode() * 31 + __control_flow_array_hash_id.hashCode()

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Verify_control_flow_path) return false
        return id == other.id && __control_flow_array_hash_id == other.__control_flow_array_hash_id
    }
}