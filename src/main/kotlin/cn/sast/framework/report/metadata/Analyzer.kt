package cn.sast.framework.report.metadata

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Analyzer(analyzerStatistics: AnalyzerStatistics, checkers: Map<String, String> = MapsKt.emptyMap()) {
   @SerialName("analyzer_statistics")
   public final val analyzerStatistics: AnalyzerStatistics

   public final val checkers: Map<String, String>

   init {
      this.analyzerStatistics = analyzerStatistics;
      this.checkers = checkers;
   }

   public operator fun component1(): AnalyzerStatistics {
      return this.analyzerStatistics;
   }

   public operator fun component2(): Map<String, String> {
      return this.checkers;
   }

   public fun copy(analyzerStatistics: AnalyzerStatistics = this.analyzerStatistics, checkers: Map<String, String> = this.checkers): Analyzer {
      return new Analyzer(analyzerStatistics, checkers);
   }

   public override fun toString(): String {
      return "Analyzer(analyzerStatistics=${this.analyzerStatistics}, checkers=${this.checkers})";
   }

   public override fun hashCode(): Int {
      return this.analyzerStatistics.hashCode() * 31 + this.checkers.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Analyzer) {
         return false;
      } else {
         val var2: Analyzer = other as Analyzer;
         if (!(this.analyzerStatistics == (other as Analyzer).analyzerStatistics)) {
            return false;
         } else {
            return this.checkers == var2.checkers;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Analyzer> {
         return Analyzer.$serializer.INSTANCE as KSerializer<Analyzer>;
      }
   }
}
