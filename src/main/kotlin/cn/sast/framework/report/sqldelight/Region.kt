package cn.sast.framework.report.sqldelight

public data class Region(
    val id: Long,
    val __file_id: Long,
    val start_line: Long,
    val start_column: Long?,
    val end_line: Long?,
    val end_column: Long?
) {
    public operator fun component1(): Long = this.id

    public operator fun component2(): Long = this.__file_id

    public operator fun component3(): Long = this.start_line

    public operator fun component4(): Long? = this.start_column

    public operator fun component5(): Long? = this.end_line

    public operator fun component6(): Long? = this.end_column

    public fun copy(
        id: Long = this.id,
        __file_id: Long = this.__file_id,
        start_line: Long = this.start_line,
        start_column: Long? = this.start_column,
        end_line: Long? = this.end_line,
        end_column: Long? = this.end_column
    ): Region {
        return Region(id, __file_id, start_line, start_column, end_line, end_column)
    }

    public override fun toString(): String {
        return "Region(id=${this.id}, __file_id=${this.__file_id}, start_line=${this.start_line}, start_column=${this.start_column}, end_line=${this.end_line}, end_column=${this.end_column})"
    }

    public override fun hashCode(): Int {
        return (
            (
                (
                    ((java.lang.Long.hashCode(this.id) * 31 + java.lang.Long.hashCode(this.__file_id)) * 31 + java.lang.Long.hashCode(this.start_line))
                        * 31
                        + (this.start_column?.hashCode() ?: 0)
                )
                    * 31
                    + (this.end_line?.hashCode() ?: 0)
            )
                * 31
                + (this.end_column?.hashCode() ?: 0)
        )
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Region) {
            return false
        } else {
            if (this.id != other.id) {
                return false
            } else if (this.__file_id != other.__file_id) {
                return false
            } else if (this.start_line != other.start_line) {
                return false
            } else if (this.start_column != other.start_column) {
                return false
            } else if (this.end_line != other.end_line) {
                return false
            } else {
                return this.end_column == other.end_column
            }
        }
    }
}