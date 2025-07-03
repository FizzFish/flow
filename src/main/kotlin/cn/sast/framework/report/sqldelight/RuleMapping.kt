package cn.sast.framework.report.sqldelight

public data class RuleMapping(
    public val rule_name: String,
    public val standard_name: String?,
    public val standard_rule: String?
) {
    public operator fun component1(): String {
        return this.rule_name
    }

    public operator fun component2(): String? {
        return this.standard_name
    }

    public operator fun component3(): String? {
        return this.standard_rule
    }

    public fun copy(
        rule_name: String = this.rule_name,
        standard_name: String? = this.standard_name,
        standard_rule: String? = this.standard_rule
    ): RuleMapping {
        return RuleMapping(rule_name, standard_name, standard_rule)
    }

    public override fun toString(): String {
        return "RuleMapping(rule_name=${this.rule_name}, standard_name=${this.standard_name}, standard_rule=${this.standard_rule})"
    }

    public override fun hashCode(): Int {
        return (this.rule_name.hashCode() * 31 + (this.standard_name?.hashCode() ?: 0)) * 31 +
                (this.standard_rule?.hashCode() ?: 0)
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is RuleMapping) {
            return false
        }
        return rule_name == other.rule_name &&
                standard_name == other.standard_name &&
                standard_rule == other.standard_rule
    }
}