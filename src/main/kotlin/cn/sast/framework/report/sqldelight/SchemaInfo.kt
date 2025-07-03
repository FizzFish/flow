package cn.sast.framework.report.sqldelight

public data class SchemaInfo(
    public val key: String,
    public val value_: String
) {
    public operator fun component1(): String = key

    public operator fun component2(): String = value_

    public fun copy(key: String = this.key, value_: String = this.value_): SchemaInfo {
        return SchemaInfo(key, value_)
    }

    public override fun toString(): String {
        return "SchemaInfo(key=$key, value_=$value_)"
    }

    public override fun hashCode(): Int {
        return key.hashCode() * 31 + value_.hashCode()
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SchemaInfo) return false
        return key == other.key && value_ == other.value_
    }
}