package cn.sast.api.config

data class ChapterFlat(
    val category: String,
    val severity: String,
    val ruleId: String
) {
    operator fun component1(): String = category

    operator fun component2(): String = severity

    operator fun component3(): String = ruleId

    fun copy(
        category: String = this.category,
        severity: String = this.severity,
        ruleId: String = this.ruleId
    ): ChapterFlat = ChapterFlat(category, severity, ruleId)

    override fun toString(): String =
        "ChapterFlat(category=$category, severity=$severity, ruleId=$ruleId)"

    override fun hashCode(): Int =
        (category.hashCode() * 31 + severity.hashCode()) * 31 + ruleId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChapterFlat) return false
        return category == other.category &&
                severity == other.severity &&
                ruleId == other.ruleId
    }
}