package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Run(tool: Tool,
   originalUriBaseIds: Map<String, UriBase> = MapsKt.emptyMap(),
   results: List<Result>,
   translations: List<TranslationToolComponent>
) {
   public final val tool: Tool
   public final val originalUriBaseIds: Map<String, UriBase>
   public final val results: List<Result>
   public final val translations: List<TranslationToolComponent>

   init {
      this.tool = tool;
      this.originalUriBaseIds = originalUriBaseIds;
      this.results = results;
      this.translations = translations;
   }

   public operator fun component1(): Tool {
      return this.tool;
   }

   public operator fun component2(): Map<String, UriBase> {
      return this.originalUriBaseIds;
   }

   public operator fun component3(): List<Result> {
      return this.results;
   }

   public operator fun component4(): List<TranslationToolComponent> {
      return this.translations;
   }

   public fun copy(
      tool: Tool = this.tool,
      originalUriBaseIds: Map<String, UriBase> = this.originalUriBaseIds,
      results: List<Result> = this.results,
      translations: List<TranslationToolComponent> = this.translations
   ): Run {
      return new Run(tool, originalUriBaseIds, results, translations);
   }

   public override fun toString(): String {
      return "Run(tool=${this.tool}, originalUriBaseIds=${this.originalUriBaseIds}, results=${this.results}, translations=${this.translations})";
   }

   public override fun hashCode(): Int {
      return ((this.tool.hashCode() * 31 + this.originalUriBaseIds.hashCode()) * 31 + this.results.hashCode()) * 31 + this.translations.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Run) {
         return false;
      } else {
         val var2: Run = other as Run;
         if (!(this.tool == (other as Run).tool)) {
            return false;
         } else if (!(this.originalUriBaseIds == var2.originalUriBaseIds)) {
            return false;
         } else if (!(this.results == var2.results)) {
            return false;
         } else {
            return this.translations == var2.translations;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Run> {
         return Run.$serializer.INSTANCE as KSerializer<Run>;
      }
   }
}
