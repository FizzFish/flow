package cn.sast.framework.report.sqldelight

public data class Rule(
    val name: String,
    val short_description_en: String,
    val short_description_zh: String,
    val severity: String?,
    val priority: String?,
    val language: String,
    val precision: String?,
    val recall: String?,
    val likelihood: String?,
    val impact: String?,
    val technique: String?,
    val analysis_scope: String?,
    val performance: String?,
    val configurable: Long?,
    val implemented: Long?,
    val static_analyzability: String?,
    val c_allocated_target: String?,
    val category_en: String,
    val category_zh: String,
    val rule_sort_number: Long?,
    val chapter_name_1: String?,
    val chapter_name_2: String?,
    val chapter_name_3: String?,
    val chapter_name_4: String?,
    val description_en: String,
    val description_zh: String?,
    val document_en: String,
    val document_zh: String
) {
    public operator fun component1(): String = name

    public operator fun component2(): String = short_description_en

    public operator fun component3(): String = short_description_zh

    public operator fun component4(): String? = severity

    public operator fun component5(): String? = priority

    public operator fun component6(): String = language

    public operator fun component7(): String? = precision

    public operator fun component8(): String? = recall

    public operator fun component9(): String? = likelihood

    public operator fun component10(): String? = impact

    public operator fun component11(): String? = technique

    public operator fun component12(): String? = analysis_scope

    public operator fun component13(): String? = performance

    public operator fun component14(): Long? = configurable

    public operator fun component15(): Long? = implemented

    public operator fun component16(): String? = static_analyzability

    public operator fun component17(): String? = c_allocated_target

    public operator fun component18(): String = category_en

    public operator fun component19(): String = category_zh

    public operator fun component20(): Long? = rule_sort_number

    public operator fun component21(): String? = chapter_name_1

    public operator fun component22(): String? = chapter_name_2

    public operator fun component23(): String? = chapter_name_3

    public operator fun component24(): String? = chapter_name_4

    public operator fun component25(): String = description_en

    public operator fun component26(): String? = description_zh

    public operator fun component27(): String = document_en

    public operator fun component28(): String = document_zh

    public fun copy(
        name: String = this.name,
        short_description_en: String = this.short_description_en,
        short_description_zh: String = this.short_description_zh,
        severity: String? = this.severity,
        priority: String? = this.priority,
        language: String = this.language,
        precision: String? = this.precision,
        recall: String? = this.recall,
        likelihood: String? = this.likelihood,
        impact: String? = this.impact,
        technique: String? = this.technique,
        analysis_scope: String? = this.analysis_scope,
        performance: String? = this.performance,
        configurable: Long? = this.configurable,
        implemented: Long? = this.implemented,
        static_analyzability: String? = this.static_analyzability,
        c_allocated_target: String? = this.c_allocated_target,
        category_en: String = this.category_en,
        category_zh: String = this.category_zh,
        rule_sort_number: Long? = this.rule_sort_number,
        chapter_name_1: String? = this.chapter_name_1,
        chapter_name_2: String? = this.chapter_name_2,
        chapter_name_3: String? = this.chapter_name_3,
        chapter_name_4: String? = this.chapter_name_4,
        description_en: String = this.description_en,
        description_zh: String? = this.description_zh,
        document_en: String = this.document_en,
        document_zh: String = this.document_zh
    ): Rule = Rule(
        name,
        short_description_en,
        short_description_zh,
        severity,
        priority,
        language,
        precision,
        recall,
        likelihood,
        impact,
        technique,
        analysis_scope,
        performance,
        configurable,
        implemented,
        static_analyzability,
        c_allocated_target,
        category_en,
        category_zh,
        rule_sort_number,
        chapter_name_1,
        chapter_name_2,
        chapter_name_3,
        chapter_name_4,
        description_en,
        description_zh,
        document_en,
        document_zh
    )

    override fun toString(): String =
        "Rule(name=$name, short_description_en=$short_description_en, short_description_zh=$short_description_zh, severity=$severity, priority=$priority, language=$language, precision=$precision, recall=$recall, likelihood=$likelihood, impact=$impact, technique=$technique, analysis_scope=$analysis_scope, performance=$performance, configurable=$configurable, implemented=$implemented, static_analyzability=$static_analyzability, c_allocated_target=$c_allocated_target, category_en=$category_en, category_zh=$category_zh, rule_sort_number=$rule_sort_number, chapter_name_1=$chapter_name_1, chapter_name_2=$chapter_name_2, chapter_name_3=$chapter_name_3, chapter_name_4=$chapter_name_4, description_en=$description_en, description_zh=$description_zh, document_en=$document_en, document_zh=$document_zh)"

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + short_description_en.hashCode()
        result = 31 * result + short_description_zh.hashCode()
        result = 31 * result + (severity?.hashCode() ?: 0)
        result = 31 * result + (priority?.hashCode() ?: 0)
        result = 31 * result + language.hashCode()
        result = 31 * result + (precision?.hashCode() ?: 0)
        result = 31 * result + (recall?.hashCode() ?: 0)
        result = 31 * result + (likelihood?.hashCode() ?: 0)
        result = 31 * result + (impact?.hashCode() ?: 0)
        result = 31 * result + (technique?.hashCode() ?: 0)
        result = 31 * result + (analysis_scope?.hashCode() ?: 0)
        result = 31 * result + (performance?.hashCode() ?: 0)
        result = 31 * result + (configurable?.hashCode() ?: 0)
        result = 31 * result + (implemented?.hashCode() ?: 0)
        result = 31 * result + (static_analyzability?.hashCode() ?: 0)
        result = 31 * result + (c_allocated_target?.hashCode() ?: 0)
        result = 31 * result + category_en.hashCode()
        result = 31 * result + category_zh.hashCode()
        result = 31 * result + (rule_sort_number?.hashCode() ?: 0)
        result = 31 * result + (chapter_name_1?.hashCode() ?: 0)
        result = 31 * result + (chapter_name_2?.hashCode() ?: 0)
        result = 31 * result + (chapter_name_3?.hashCode() ?: 0)
        result = 31 * result + (chapter_name_4?.hashCode() ?: 0)
        result = 31 * result + description_en.hashCode()
        result = 31 * result + (description_zh?.hashCode() ?: 0)
        result = 31 * result + document_en.hashCode()
        result = 31 * result + document_zh.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Rule) return false

        if (name != other.name) return false
        if (short_description_en != other.short_description_en) return false
        if (short_description_zh != other.short_description_zh) return false
        if (severity != other.severity) return false
        if (priority != other.priority) return false
        if (language != other.language) return false
        if (precision != other.precision) return false
        if (recall != other.recall) return false
        if (likelihood != other.likelihood) return false
        if (impact != other.impact) return false
        if (technique != other.technique) return false
        if (analysis_scope != other.analysis_scope) return false
        if (performance != other.performance) return false
        if (configurable != other.configurable) return false
        if (implemented != other.implemented) return false
        if (static_analyzability != other.static_analyzability) return false
        if (c_allocated_target != other.c_allocated_target) return false
        if (category_en != other.category_en) return false
        if (category_zh != other.category_zh) return false
        if (rule_sort_number != other.rule_sort_number) return false
        if (chapter_name_1 != other.chapter_name_1) return false
        if (chapter_name_2 != other.chapter_name_2) return false
        if (chapter_name_3 != other.chapter_name_3) return false
        if (chapter_name_4 != other.chapter_name_4) return false
        if (description_en != other.description_en) return false
        if (description_zh != other.description_zh) return false
        if (document_en != other.document_en) return false
        if (document_zh != other.document_zh) return false

        return true
    }
}