package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class MessageStrings(public val default: Message) {
    public operator fun component1(): Message {
        return this.default
    }

    public fun copy(default: Message = this.default): MessageStrings {
        return MessageStrings(default)
    }

    public override fun toString(): String {
        return "MessageStrings(default=${this.default})"
    }

    public override fun hashCode(): Int {
        return this.default.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is MessageStrings) {
            return false
        } else {
            return this.default == other.default
        }
    }

    public companion object {
        public fun serializer(): KSerializer<MessageStrings> {
            return MessageStrings.serializer()
        }
    }
}