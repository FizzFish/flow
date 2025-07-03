package cn.sast.framework.report.sqldelight

data class Verify_macro(
    val id: Long,
    val __macro_note_set_hash_id: Long
) {
    operator fun component1(): Long = id

    operator fun component2(): Long = __macro_note_set_hash_id

    fun copy(
        id: Long = this.id,
        __macro_note_set_hash_id: Long = this.__macro_note_set_hash_id
    ): Verify_macro = Verify_macro(id, __macro_note_set_hash_id)

    override fun toString(): String =
        "Verify_macro(id=$id, __macro_note_set_hash_id=$__macro_note_set_hash_id)"

    override fun hashCode(): Int =
        id.hashCode() * 31 + __macro_note_set_hash_id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Verify_macro) return false
        if (id != other.id) return false
        return __macro_note_set_hash_id == other.__macro_note_set_hash_id
    }
}