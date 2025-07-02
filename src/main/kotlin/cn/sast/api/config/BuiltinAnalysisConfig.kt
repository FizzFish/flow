package cn.sast.api.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BuiltinAnalysisConfig")
public data class BuiltinAnalysisConfig(unusedDetectorSootSigPatternBlackList: List<String> = CollectionsKt.listOf(
         new java.lang.String[]{"<.*: void start\\(.*BundleContext\\)>", "<.*: void stop\\(.*BundleContext\\)>", "<.*\\.R(\\$.*)?: .*>"}
      ),
   unusedDetectorMethodSigBlackList: List<String> = CollectionsKt.listOf(
         new java.lang.String[]{
            "org.osgi.framework.BundleActivator: void start(BundleContext context)", "org.osgi.framework.BundleActivator: void stop(BundleContext context)"
         }
      ),
   maximumUnusedFieldReportsEachClass: Int = 10,
   maximumUnusedMethodReportsEachClass: Int = 40
) {
   @SerialName("unused_detector_soot_sig_pattern_blacklist")
   public final val unusedDetectorSootSigPatternBlackList: List<String>

   @SerialName("unused_detector_method_sig_blacklist")
   public final val unusedDetectorMethodSigBlackList: List<String>

   @SerialName("maximum_unused_field_reports_each_class")
   public final val maximumUnusedFieldReportsEachClass: Int

   @SerialName("maximum_unused_method_reports_each_class")
   public final val maximumUnusedMethodReportsEachClass: Int

   init {
      this.unusedDetectorSootSigPatternBlackList = unusedDetectorSootSigPatternBlackList;
      this.unusedDetectorMethodSigBlackList = unusedDetectorMethodSigBlackList;
      this.maximumUnusedFieldReportsEachClass = maximumUnusedFieldReportsEachClass;
      this.maximumUnusedMethodReportsEachClass = maximumUnusedMethodReportsEachClass;
   }

   public operator fun component1(): List<String> {
      return this.unusedDetectorSootSigPatternBlackList;
   }

   public operator fun component2(): List<String> {
      return this.unusedDetectorMethodSigBlackList;
   }

   public operator fun component3(): Int {
      return this.maximumUnusedFieldReportsEachClass;
   }

   public operator fun component4(): Int {
      return this.maximumUnusedMethodReportsEachClass;
   }

   public fun copy(
      unusedDetectorSootSigPatternBlackList: List<String> = this.unusedDetectorSootSigPatternBlackList,
      unusedDetectorMethodSigBlackList: List<String> = this.unusedDetectorMethodSigBlackList,
      maximumUnusedFieldReportsEachClass: Int = this.maximumUnusedFieldReportsEachClass,
      maximumUnusedMethodReportsEachClass: Int = this.maximumUnusedMethodReportsEachClass
   ): BuiltinAnalysisConfig {
      return new BuiltinAnalysisConfig(
         unusedDetectorSootSigPatternBlackList, unusedDetectorMethodSigBlackList, maximumUnusedFieldReportsEachClass, maximumUnusedMethodReportsEachClass
      );
   }

   public override fun toString(): String {
      return "BuiltinAnalysisConfig(unusedDetectorSootSigPatternBlackList=${this.unusedDetectorSootSigPatternBlackList}, unusedDetectorMethodSigBlackList=${this.unusedDetectorMethodSigBlackList}, maximumUnusedFieldReportsEachClass=${this.maximumUnusedFieldReportsEachClass}, maximumUnusedMethodReportsEachClass=${this.maximumUnusedMethodReportsEachClass})";
   }

   public override fun hashCode(): Int {
      return (
               (this.unusedDetectorSootSigPatternBlackList.hashCode() * 31 + this.unusedDetectorMethodSigBlackList.hashCode()) * 31
                  + Integer.hashCode(this.maximumUnusedFieldReportsEachClass)
            )
            * 31
         + Integer.hashCode(this.maximumUnusedMethodReportsEachClass);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is BuiltinAnalysisConfig) {
         return false;
      } else {
         val var2: BuiltinAnalysisConfig = other as BuiltinAnalysisConfig;
         if (!(this.unusedDetectorSootSigPatternBlackList == (other as BuiltinAnalysisConfig).unusedDetectorSootSigPatternBlackList)) {
            return false;
         } else if (!(this.unusedDetectorMethodSigBlackList == var2.unusedDetectorMethodSigBlackList)) {
            return false;
         } else if (this.maximumUnusedFieldReportsEachClass != var2.maximumUnusedFieldReportsEachClass) {
            return false;
         } else {
            return this.maximumUnusedMethodReportsEachClass == var2.maximumUnusedMethodReportsEachClass;
         }
      }
   }

   fun BuiltinAnalysisConfig() {
      this(null, null, 0, 0, 15, null);
   }

   public companion object {
      public fun serializer(): KSerializer<BuiltinAnalysisConfig> {
         return BuiltinAnalysisConfig.$serializer.INSTANCE as KSerializer<BuiltinAnalysisConfig>;
      }
   }
}
