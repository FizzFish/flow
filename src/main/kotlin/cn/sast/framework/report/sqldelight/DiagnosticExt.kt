package cn.sast.framework.report.sqldelight

public data class DiagnosticExt(
    public val __diagnostic_id: Long,
    public val attr_name: String,
    public val attr_value: String
) {
    public operator fun component1(): Long = this.__diagnostic_id

    public operator fun component2(): String = this.attr_name

    public operator fun component3(): String = this.attr_value

    public fun copy(
        __diagnostic_id: Long = this.__diagnostic_id,
        attr_name: String = this.attr_name,
        attr_value: String = this.attr_value
    ): DiagnosticExt = DiagnosticExt(__diagnostic_id, attr_name, attr_value)

    public override fun toString(): String =
        "DiagnosticExt(__diagnostic_id=${this.__diagnostic_id}, attr_name=${this.attr_name}, attr_value=${this.attr_value})"

    public override fun hashCode(): Int =
        (java.lang.Long.hashCode(this.__diagnostic_id) * 31 + this.attr_name.hashCode()) * 31 + this.attr_value.hashCode()

    public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiagnosticExt) return false
        return __diagnostic_id == other.__diagnostic_id &&
                attr_name == other.attr_name &&
                attr_value == other.attr_value
    }
}