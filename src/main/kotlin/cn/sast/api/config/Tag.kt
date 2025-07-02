package cn.sast.api.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Tag(standard: String, rule: String) {
   public final val standard: String
   public final val rule: String

   init {
      this.standard = standard;
      this.rule = rule;
   }

   public operator fun component1(): String {
      return this.standard;
   }

   public operator fun component2(): String {
      return this.rule;
   }

   public fun copy(standard: String = this.standard, rule: String = this.rule): Tag {
      return new Tag(standard, rule);
   }

   public override fun toString(): String {
      return "Tag(standard=${this.standard}, rule=${this.rule})";
   }

   public override fun hashCode(): Int {
      return this.standard.hashCode() * 31 + this.rule.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Tag) {
         return false;
      } else {
         val var2: Tag = other as Tag;
         if (!(this.standard == (other as Tag).standard)) {
            return false;
         } else {
            return this.rule == var2.rule;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Tag> {
         return Tag.$serializer.INSTANCE as KSerializer<Tag>;
      }
   }
}
