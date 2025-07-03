package cn.sast.framework.report.sqldelight

public data class NoteExt(
    public val __note_id: Long,
    public val attr_name: String,
    public val attr_value: String
) {
    public operator fun component1(): Long {
        return this.__note_id
    }

    public operator fun component2(): String {
        return this.attr_name
    }

    public operator fun component3(): String {
        return this.attr_value
    }

    public fun copy(
        __note_id: Long = this.__note_id,
        attr_name: String = this.attr_name,
        attr_value: String = this.attr_value
    ): NoteExt {
        return NoteExt(__note_id, attr_name, attr_value)
    }

    public override fun toString(): String {
        return "NoteExt(__note_id=${this.__note_id}, attr_name=${this.attr_name}, attr_value=${this.attr_value})"
    }

    public override fun hashCode(): Int {
        return (java.lang.Long.hashCode(this.__note_id) * 31 + this.attr_name.hashCode()) * 31 + this.attr_value.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is NoteExt) {
            return false
        } else {
            val var2: NoteExt = other
            if (this.__note_id != var2.__note_id) {
                return false
            } else if (!(this.attr_name == var2.attr_name)) {
                return false
            } else {
                return this.attr_value == var2.attr_value
            }
        }
    }
}