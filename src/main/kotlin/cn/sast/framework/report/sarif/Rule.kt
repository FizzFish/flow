package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Rule(
    public val id: String,
    public val name: String,
    public val messageStrings: MessageStrings
) {
    public operator fun component1(): String {
        return this.id
    }

    public operator fun component2(): String {
        return this.name
    }

    public operator fun component3(): MessageStrings {
        return this.messageStrings
    }

    public fun copy(
        id: String = this.id,
        name: String = this.name,
        messageStrings: MessageStrings = this.messageStrings
    ): Rule {
        return Rule(id, name, messageStrings)
    }

    public override fun toString(): String {
        return "Rule(id=${this.id}, name=${this.name}, messageStrings=${this.messageStrings})"
    }

    public override fun hashCode(): Int {
        return (this.id.hashCode() * 31 + this.name.hashCode()) * 31 + this.messageStrings.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Rule) {
            return false
        } else {
            val var2: Rule = other
            if (!(this.id == other.id)) {
                return false
            } else if (!(this.name == var2.name)) {
                return false
            } else {
                return this.messageStrings == var2.messageStrings
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Rule> {
            return Rule.serializer()
        }
    }
}