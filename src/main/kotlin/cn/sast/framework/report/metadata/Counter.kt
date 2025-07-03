package cn.sast.framework.report.metadata

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
public data class Counter(
    @EncodeDefault(mode = Mode.ALWAYS)
    public val missed: Int,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val covered: Int
) {
    public operator fun component1(): Int {
        return this.missed
    }

    public operator fun component2(): Int {
        return this.covered
    }

    public fun copy(missed: Int = this.missed, covered: Int = this.covered): Counter {
        return Counter(missed, covered)
    }

    public override fun toString(): String {
        return "Counter(missed=${this.missed}, covered=${this.covered})"
    }

    public override fun hashCode(): Int {
        return this.missed.hashCode() * 31 + this.covered.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Counter) {
            return false
        } else {
            if (this.missed != other.missed) {
                return false
            } else {
                return this.covered == other.covered
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Counter> {
            return Counter.serializer()
        }
    }
}