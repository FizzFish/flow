package cn.sast.framework.report.sqldelight

public data class NotePath(
    val __note_array_hash_id: Long,
    val note_sequence: Long,
    val note_stack_depth: Long?,
    val note_is_key_event: Long?,
    val __note_id: Long
) {
    public operator fun component1(): Long = this.__note_array_hash_id

    public operator fun component2(): Long = this.note_sequence

    public operator fun component3(): Long? = this.note_stack_depth

    public operator fun component4(): Long? = this.note_is_key_event

    public operator fun component5(): Long = this.__note_id

    public fun copy(
        __note_array_hash_id: Long = this.__note_array_hash_id,
        note_sequence: Long = this.note_sequence,
        note_stack_depth: Long? = this.note_stack_depth,
        note_is_key_event: Long? = this.note_is_key_event,
        __note_id: Long = this.__note_id
    ): NotePath {
        return NotePath(__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id)
    }

    public override fun toString(): String {
        return "NotePath(__note_array_hash_id=$__note_array_hash_id, note_sequence=$note_sequence, note_stack_depth=$note_stack_depth, note_is_key_event=$note_is_key_event, __note_id=$__note_id)"
    }

    public override fun hashCode(): Int {
        return (
            (
                (
                    (__note_array_hash_id.hashCode() * 31 + note_sequence.hashCode()) * 31
                        + (note_stack_depth?.hashCode() ?: 0)
                ) * 31
                + (note_is_key_event?.hashCode() ?: 0)
            ) * 31
            + __note_id.hashCode()
        )
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is NotePath) {
            return false
        }
        return __note_array_hash_id == other.__note_array_hash_id &&
            note_sequence == other.note_sequence &&
            note_stack_depth == other.note_stack_depth &&
            note_is_key_event == other.note_is_key_event &&
            __note_id == other.__note_id
    }
}