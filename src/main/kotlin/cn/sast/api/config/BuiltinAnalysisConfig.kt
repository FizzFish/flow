package cn.sast.api.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 内置分析器全局配置。
 *
 * > Kotlin `data class` 会自动合成：
 * > - `componentN()` / `copy()` / `toString()` / `hashCode()` / `equals()`
 * > - 以及由 `@Serializable` 注入的 `Companion.serializer()`
 */
@Serializable
@SerialName("BuiltinAnalysisConfig")
data class BuiltinAnalysisConfig(

    /** Soot Signature 正则黑名单（UnusedDetector 用） */
    @SerialName("unused_detector_soot_sig_pattern_blacklist")
    val unusedDetectorSootSigPatternBlackList: List<String> = listOf(
        "<.*: void start\\(.*BundleContext\\)>",
        "<.*: void stop\\(.*BundleContext\\)>",
        "<.*\\.R(\\$.*)?: .*>"
    ),

    /** 方法签名黑名单（UnusedDetector 用） */
    @SerialName("unused_detector_method_sig_blacklist")
    val unusedDetectorMethodSigBlackList: List<String> = listOf(
        "org.osgi.framework.BundleActivator: void start(BundleContext context)",
        "org.osgi.framework.BundleActivator: void stop(BundleContext context)"
    ),

    /** 单个类最多报告多少条未使用字段 */
    @SerialName("maximum_unused_field_reports_each_class")
    val maximumUnusedFieldReportsEachClass: Int = 10,

    /** 单个类最多报告多少条未使用方法 */
    @SerialName("maximum_unused_method_reports_each_class")
    val maximumUnusedMethodReportsEachClass: Int = 40
)
