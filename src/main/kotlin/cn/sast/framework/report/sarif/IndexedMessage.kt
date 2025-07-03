package cn.sast.framework.report.sarif

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
public data class IndexedMessage(public val id: String = "default") {
    @EncodeDefault(
        mode = Mode.ALWAYS
    )
    public val id: String = id

    public operator fun component1(): String {
        return this.id
    }

    public fun copy(id: String = this.id): IndexedMessage {
        return IndexedMessage(id)
    }

    public override fun toString(): String {
        return "IndexedMessage(id=${this.id})"
    }

    public override fun hashCode(): Int {
        return this.id.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is IndexedMessage) {
            return false
        } else {
            return this.id == other.id
        }
    }

    public companion object {
        public fun serializer(): KSerializer<IndexedMessage> {
            return IndexedMessage.serializer()
        }
    }
}