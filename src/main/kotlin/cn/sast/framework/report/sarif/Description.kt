package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Description(public val text: String) {
    public operator fun component1(): String {
        return this.text
    }

    public fun copy(text: String = this.text): Description {
        return Description(text)
    }

    public override fun toString(): String {
        return "Description(text=${this.text})"
    }

    public override fun hashCode(): Int {
        return this.text.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Description) {
            return false
        } else {
            return this.text == other.text
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Description> {
            return Description.serializer()
        }
    }
}