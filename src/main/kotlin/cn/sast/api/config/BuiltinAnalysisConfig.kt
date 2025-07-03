package cn.sast.api.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BuiltinAnalysisConfig")
public data class BuiltinAnalysisConfig(
    @SerialName("unused_detector_soot_sig_pattern_blacklist")
    public val unusedDetectorSootSigPatternBlackList: List<String> = listOf(
        "<.*: void start\\(.*BundleContext\\)>",
        "<.*: void stop\\(.*BundleContext\\)>",
        "<.*\\.R(\\$.*)?: .*>"
    ),
    @SerialName("unused_detector_method_sig_blacklist")
    public val unusedDetectorMethodSigBlackList: List<String> = listOf(
        "org.osgi.framework.BundleActivator: void start(BundleContext context)",
        "org.osgi.framework.BundleActivator: void stop(BundleContext context)"
    ),
    @SerialName("maximum_unused_field_reports_each_class")
    public val maximumUnusedFieldReportsEachClass: Int = 10,
    @SerialName("maximum_unused_method_reports_each_class")
    public val maximumUnusedMethodReportsEachClass: Int = 40
) {
    public operator fun component1(): List<String> {
        return this.unusedDetectorSootSigPatternBlackList
    }

    public operator fun component2(): List<String> {
        return this.unusedDetectorMethodSigBlackList
    }

    public operator fun component3(): Int {
        return this.maximumUnusedFieldReportsEachClass
    }

    public operator fun component4(): Int {
        return this.maximumUnusedMethodReportsEachClass
    }

    public fun copy(
        unusedDetectorSootSigPatternBlackList: List<String> = this.unusedDetectorSootSigPatternBlackList,
        unusedDetectorMethodSigBlackList: List<String> = this.unusedDetectorMethodSigBlackList,
        maximumUnusedFieldReportsEachClass: Int = this.maximumUnusedFieldReportsEachClass,
        maximumUnusedMethodReportsEachClass: Int = this.maximumUnusedMethodReportsEachClass
    ): BuiltinAnalysisConfig {
        return BuiltinAnalysisConfig(
            unusedDetectorSootSigPatternBlackList,
            unusedDetectorMethodSigBlackList,
            maximumUnusedFieldReportsEachClass,
            maximumUnusedMethodReportsEachClass
        )
    }

    public override fun toString(): String {
        return "BuiltinAnalysisConfig(unusedDetectorSootSigPatternBlackList=$unusedDetectorSootSigPatternBlackList, unusedDetectorMethodSigBlackList=$unusedDetectorMethodSigBlackList, maximumUnusedFieldReportsEachClass=$maximumUnusedFieldReportsEachClass, maximumUnusedMethodReportsEachClass=$maximumUnusedMethodReportsEachClass)"
    }

    public override fun hashCode(): Int {
        return (
            (unusedDetectorSootSigPatternBlackList.hashCode() * 31 + unusedDetectorMethodSigBlackList.hashCode()) * 31
                + maximumUnusedFieldReportsEachClass.hashCode()
            ) * 31 + maximumUnusedMethodReportsEachClass.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is BuiltinAnalysisConfig) {
            return false
        }
        return unusedDetectorSootSigPatternBlackList == other.unusedDetectorSootSigPatternBlackList &&
            unusedDetectorMethodSigBlackList == other.unusedDetectorMethodSigBlackList &&
            maximumUnusedFieldReportsEachClass == other.maximumUnusedFieldReportsEachClass &&
            maximumUnusedMethodReportsEachClass == other.maximumUnusedMethodReportsEachClass
    }

    public companion object {
        public fun serializer(): KSerializer<BuiltinAnalysisConfig> {
            return BuiltinAnalysisConfig.serializer()
        }
    }
}