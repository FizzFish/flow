package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AnalyzerStatistics(
    public val failed: Int,
    @SerialName("failed_sources")
    public val failedSources: List<String>,
    public val successful: Int,
    @SerialName("successful_sources")
    public val successfulSources: List<String>,
    public val version: String
) {
    public operator fun component1(): Int = this.failed

    public operator fun component2(): List<String> = this.failedSources

    public operator fun component3(): Int = this.successful

    public operator fun component4(): List<String> = this.successfulSources

    public operator fun component5(): String = this.version

    public fun copy(
        failed: Int = this.failed,
        failedSources: List<String> = this.failedSources,
        successful: Int = this.successful,
        successfulSources: List<String> = this.successfulSources,
        version: String = this.version
    ): AnalyzerStatistics {
        return AnalyzerStatistics(failed, failedSources, successful, successfulSources, version)
    }

    public override fun toString(): String {
        return "AnalyzerStatistics(failed=${this.failed}, failedSources=${this.failedSources}, successful=${this.successful}, successfulSources=${this.successfulSources}, version=${this.version})"
    }

    public override fun hashCode(): Int {
        return (
                ((Integer.hashCode(this.failed) * 31 + this.failedSources.hashCode()) * 31 + Integer.hashCode(this.successful)) * 31
                + this.successfulSources.hashCode()
            )
            * 31
            + this.version.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is AnalyzerStatistics) {
            return false
        } else {
            val var2: AnalyzerStatistics = other
            if (this.failed != other.failed) {
                return false
            } else if (!(this.failedSources == var2.failedSources)) {
                return false
            } else if (this.successful != var2.successful) {
                return false
            } else if (!(this.successfulSources == var2.successfulSources)) {
                return false
            } else {
                return this.version == var2.version
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<AnalyzerStatistics> {
            return AnalyzerStatistics.serializer()
        }
    }
}