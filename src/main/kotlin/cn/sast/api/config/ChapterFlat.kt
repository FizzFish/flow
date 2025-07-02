package cn.sast.api.config

public data class ChapterFlat(category: String, severity: String, ruleId: String) {
   public final val category: String
   public final val severity: String
   public final val ruleId: String

   init {
      this.category = category;
      this.severity = severity;
      this.ruleId = ruleId;
   }

   public operator fun component1(): String {
      return this.category;
   }

   public operator fun component2(): String {
      return this.severity;
   }

   public operator fun component3(): String {
      return this.ruleId;
   }

   public fun copy(category: String = this.category, severity: String = this.severity, ruleId: String = this.ruleId): ChapterFlat {
      return new ChapterFlat(category, severity, ruleId);
   }

   public override fun toString(): String {
      return "ChapterFlat(category=${this.category}, severity=${this.severity}, ruleId=${this.ruleId})";
   }

   public override fun hashCode(): Int {
      return (this.category.hashCode() * 31 + this.severity.hashCode()) * 31 + this.ruleId.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ChapterFlat) {
         return false;
      } else {
         val var2: ChapterFlat = other as ChapterFlat;
         if (!(this.category == (other as ChapterFlat).category)) {
            return false;
         } else if (!(this.severity == var2.severity)) {
            return false;
         } else {
            return this.ruleId == var2.ruleId;
         }
      }
   }
}
