package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Run(
    public val tool: Tool,
    public val originalUriBaseIds: Map<String, UriBase> = emptyMap(),
    public val results: List<Result>,
    public val translations: List<TranslationToolComponent>
) {
    public operator fun component1(): Tool {
        return this.tool
    }

    public operator fun component2(): Map<String, UriBase> {
        return this.originalUriBaseIds
    }

    public operator fun component3(): List<Result> {
        return this.results
    }

    public operator fun component4(): List<TranslationToolComponent> {
        return this.translations
    }

    public fun copy(
        tool: Tool = this.tool,
        originalUriBaseIds: Map<String, UriBase> = this.originalUriBaseIds,
        results: List<Result> = this.results,
        translations: List<TranslationToolComponent> = this.translations
    ): Run {
        return Run(tool, originalUriBaseIds, results, translations)
    }

    public override fun toString(): String {
        return "Run(tool=${this.tool}, originalUriBaseIds=${this.originalUriBaseIds}, results=${this.results}, translations=${this.translations})"
    }

    public override fun hashCode(): Int {
        return ((this.tool.hashCode() * 31 + this.originalUriBaseIds.hashCode()) * 31 + this.results.hashCode()) * 31 + this.translations.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Run) {
            return false
        } else {
            val var2: Run = other
            if (!(this.tool == other.tool)) {
                return false
            } else if (!(this.originalUriBaseIds == var2.originalUriBaseIds)) {
                return false
            } else if (!(this.results == var2.results)) {
                return false
            } else {
                return this.translations == var2.translations
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Run> {
            return Run.$serializer.INSTANCE as KSerializer<Run>
        }
    }
}