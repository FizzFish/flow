package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class UriBase(
    public val uri: String,
    public val description: Description? = null
) {
    public operator fun component1(): String {
        return this.uri
    }

    public operator fun component2(): Description? {
        return this.description
    }

    public fun copy(uri: String = this.uri, description: Description? = this.description): UriBase {
        return UriBase(uri, description)
    }

    public override fun toString(): String {
        return "UriBase(uri=${this.uri}, description=${this.description})"
    }

    public override fun hashCode(): Int {
        return this.uri.hashCode() * 31 + (this.description?.hashCode() ?: 0)
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is UriBase) {
            return false
        }
        return this.uri == other.uri && this.description == other.description
    }

    public companion object {
        public fun serializer(): KSerializer<UriBase> {
            return UriBase.serializer()
        }
    }
}