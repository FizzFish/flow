package cn.sast.framework.metrics

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
internal data class ReportKey(category: String? = null, type: String, size: Int = -1) {
   public final val category: String?
   public final val type: String

   public final var size: Int
      internal set

   init {
      this.category = category;
      this.type = type;
      this.size = size;
   }

   public operator fun component1(): String? {
      return this.category;
   }

   public operator fun component2(): String {
      return this.type;
   }

   public operator fun component3(): Int {
      return this.size;
   }

   public fun copy(category: String? = this.category, type: String = this.type, size: Int = this.size): ReportKey {
      return new ReportKey(category, type, size);
   }

   public override fun toString(): String {
      return "ReportKey(category=${this.category}, type=${this.type}, size=${this.size})";
   }

   public override fun hashCode(): Int {
      return ((if (this.category == null) 0 else this.category.hashCode()) * 31 + this.type.hashCode()) * 31 + Integer.hashCode(this.size);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ReportKey) {
         return false;
      } else {
         val var2: ReportKey = other as ReportKey;
         if (!(this.category == (other as ReportKey).category)) {
            return false;
         } else if (!(this.type == var2.type)) {
            return false;
         } else {
            return this.size == var2.size;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<ReportKey> {
         return ReportKey.$serializer.INSTANCE as KSerializer<ReportKey>;
      }
   }
}
