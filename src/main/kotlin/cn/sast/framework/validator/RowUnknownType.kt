package cn.sast.framework.validator

internal data class RowUnknownType(val type: String) : RowType() {

    override fun toString(): String {
        return this.type
    }

    operator fun component1(): String {
        return this.type
    }

    fun copy(type: String = this.type): RowUnknownType {
        return RowUnknownType(type)
    }

    override fun hashCode(): Int {
        return this.type.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is RowUnknownType) {
            return false
        } else {
            return this.type == other.type
        }
    }
}