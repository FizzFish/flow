package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AnalyzerStatistics(failed: Int, failedSources: List<String>, successful: Int, successfulSources: List<String>, version: String) {
   public final val failed: Int

   @SerialName("failed_sources")
   public final val failedSources: List<String>

   public final val successful: Int

   @SerialName("successful_sources")
   public final val successfulSources: List<String>

   public final val version: String

   init {
      this.failed = failed;
      this.failedSources = failedSources;
      this.successful = successful;
      this.successfulSources = successfulSources;
      this.version = version;
   }

   public operator fun component1(): Int {
      return this.failed;
   }

   public operator fun component2(): List<String> {
      return this.failedSources;
   }

   public operator fun component3(): Int {
      return this.successful;
   }

   public operator fun component4(): List<String> {
      return this.successfulSources;
   }

   public operator fun component5(): String {
      return this.version;
   }

   public fun copy(
      failed: Int = this.failed,
      failedSources: List<String> = this.failedSources,
      successful: Int = this.successful,
      successfulSources: List<String> = this.successfulSources,
      version: String = this.version
   ): AnalyzerStatistics {
      return new AnalyzerStatistics(failed, failedSources, successful, successfulSources, version);
   }

   public override fun toString(): String {
      return "AnalyzerStatistics(failed=${this.failed}, failedSources=${this.failedSources}, successful=${this.successful}, successfulSources=${this.successfulSources}, version=${this.version})";
   }

   public override fun hashCode(): Int {
      return (
               ((Integer.hashCode(this.failed) * 31 + this.failedSources.hashCode()) * 31 + Integer.hashCode(this.successful)) * 31
                  + this.successfulSources.hashCode()
            )
            * 31
         + this.version.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AnalyzerStatistics) {
         return false;
      } else {
         val var2: AnalyzerStatistics = other as AnalyzerStatistics;
         if (this.failed != (other as AnalyzerStatistics).failed) {
            return false;
         } else if (!(this.failedSources == var2.failedSources)) {
            return false;
         } else if (this.successful != var2.successful) {
            return false;
         } else if (!(this.successfulSources == var2.successfulSources)) {
            return false;
         } else {
            return this.version == var2.version;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<AnalyzerStatistics> {
         return AnalyzerStatistics.$serializer.INSTANCE as KSerializer<AnalyzerStatistics>;
      }
   }
}
