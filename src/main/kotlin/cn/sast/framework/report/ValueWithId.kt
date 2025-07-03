package cn.sast.framework.report

private data class ValueWithId<T>(val id: Long, val value: T) {
    public operator fun component1(): Long {
        return this.id
    }

    public operator fun component2(): T {
        return this.value
    }

    public fun copy(id: Long = this.id, value: T = this.value): ValueWithId<T> {
        return ValueWithId(id, value)
    }

    public override fun toString(): String {
        return "ValueWithId(id=${this.id}, value=${this.value})"
    }

    public override fun hashCode(): Int {
        return java.lang.Long.hashCode(this.id) * 31 + (this.value?.hashCode() ?: 0)
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is ValueWithId<*>) {
            return false
        }
        if (this.id != other.id) {
            return false
        }
        return this.value == other.value
    }
}