package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.emptyMap

@Serializable
public data class Analyzer(
    @SerialName("analyzer_statistics")
    public val analyzerStatistics: AnalyzerStatistics,
    public val checkers: Map<String, String> = emptyMap()
) {
    public operator fun component1(): AnalyzerStatistics {
        return this.analyzerStatistics
    }

    public operator fun component2(): Map<String, String> {
        return this.checkers
    }

    public fun copy(
        analyzerStatistics: AnalyzerStatistics = this.analyzerStatistics,
        checkers: Map<String, String> = this.checkers
    ): Analyzer {
        return Analyzer(analyzerStatistics, checkers)
    }

    public override fun toString(): String {
        return "Analyzer(analyzerStatistics=${this.analyzerStatistics}, checkers=${this.checkers})"
    }

    public override fun hashCode(): Int {
        return this.analyzerStatistics.hashCode() * 31 + this.checkers.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Analyzer) {
            return false
        } else {
            val var2: Analyzer = other
            if (!(this.analyzerStatistics == other.analyzerStatistics)) {
                return false
            } else {
                return this.checkers == var2.checkers
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Analyzer> {
            return Analyzer.serializer()
        }
    }
}