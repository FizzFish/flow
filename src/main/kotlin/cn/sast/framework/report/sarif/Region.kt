package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Region(
    public val startLine: Int,
    public val startColumn: Int = 0,
    public val endLine: Int = 0,
    public val endColumn: Int = 0
) {
    public constructor(region: com.feysh.corax.config.api.report.Region) : this(
        Math.max(region.startLine, 0),
        Math.max(region.startColumn, 0),
        Math.max(region.getEndLine(), 0),
        Math.max(region.getEndColumn() + 1, 0)
    )

    public operator fun component1(): Int = this.startLine

    public operator fun component2(): Int = this.startColumn

    public operator fun component3(): Int = this.endLine

    public operator fun component4(): Int = this.endColumn

    public fun copy(
        startLine: Int = this.startLine,
        startColumn: Int = this.startColumn,
        endLine: Int = this.endLine,
        endColumn: Int = this.endColumn
    ): Region = Region(startLine, startColumn, endLine, endColumn)

    public override fun toString(): String =
        "Region(startLine=${this.startLine}, startColumn=${this.startColumn}, endLine=${this.endLine}, endColumn=${this.endColumn})"

    public override fun hashCode(): Int =
        ((Integer.hashCode(this.startLine) * 31 + Integer.hashCode(this.startColumn)) * 31 + Integer.hashCode(
            this.endLine
        )) * 31 + Integer.hashCode(this.endColumn)

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Region) {
            return false
        } else {
            if (this.startLine != other.startLine) {
                return false
            } else if (this.startColumn != other.startColumn) {
                return false
            } else if (this.endLine != other.endLine) {
                return false
            } else {
                return this.endColumn == other.endColumn
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Region> = Region.serializer()
    }
}