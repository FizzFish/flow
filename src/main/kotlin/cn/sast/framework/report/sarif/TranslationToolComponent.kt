package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class TranslationToolComponent(language: String, name: String, rules: List<Rule>) {
   public final val language: String
   public final val name: String
   public final val rules: List<Rule>

   init {
      this.language = language;
      this.name = name;
      this.rules = rules;
   }

   public operator fun component1(): String {
      return this.language;
   }

   public operator fun component2(): String {
      return this.name;
   }

   public operator fun component3(): List<Rule> {
      return this.rules;
   }

   public fun copy(language: String = this.language, name: String = this.name, rules: List<Rule> = this.rules): TranslationToolComponent {
      return new TranslationToolComponent(language, name, rules);
   }

   public override fun toString(): String {
      return "TranslationToolComponent(language=${this.language}, name=${this.name}, rules=${this.rules})";
   }

   public override fun hashCode(): Int {
      return (this.language.hashCode() * 31 + this.name.hashCode()) * 31 + this.rules.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is TranslationToolComponent) {
         return false;
      } else {
         val var2: TranslationToolComponent = other as TranslationToolComponent;
         if (!(this.language == (other as TranslationToolComponent).language)) {
            return false;
         } else if (!(this.name == var2.name)) {
            return false;
         } else {
            return this.rules == var2.rules;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<TranslationToolComponent> {
         return TranslationToolComponent.$serializer.INSTANCE as KSerializer<TranslationToolComponent>;
      }
   }
}
