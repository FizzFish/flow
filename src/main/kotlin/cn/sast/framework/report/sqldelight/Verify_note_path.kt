package cn.sast.framework.report.sqldelight

public data class Verify_note_path(
    public val id: Long,
    public val __note_array_hash_id: Long
) {
    public operator fun component1(): Long = id

    public operator fun component2(): Long = __note_array_hash_id

    public fun copy(
        id: Long = this.id,
        __note_array_hash_id: Long = this.__note_array_hash_id
    ): Verify_note_path = Verify_note_path(id, __note_array_hash_id)

    public override fun toString(): String =
        "Verify_note_path(id=$id, __note_array_hash_id=$__note_array_hash_id)"

    public override fun hashCode(): Int =
        id.hashCode() * 31 + __note_array_hash_id.hashCode()

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Verify_note_path) return false
        if (id != other.id) return false
        return __note_array_hash_id == other.__note_array_hash_id
    }
}