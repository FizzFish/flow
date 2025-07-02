package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class UriBase(uri: String, description: Description? = null) {
   public final val uri: String
   public final val description: Description?

   init {
      this.uri = uri;
      this.description = description;
   }

   public operator fun component1(): String {
      return this.uri;
   }

   public operator fun component2(): Description? {
      return this.description;
   }

   public fun copy(uri: String = this.uri, description: Description? = this.description): UriBase {
      return new UriBase(uri, description);
   }

   public override fun toString(): String {
      return "UriBase(uri=${this.uri}, description=${this.description})";
   }

   public override fun hashCode(): Int {
      return this.uri.hashCode() * 31 + (if (this.description == null) 0 else this.description.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is UriBase) {
         return false;
      } else {
         val var2: UriBase = other as UriBase;
         if (!(this.uri == (other as UriBase).uri)) {
            return false;
         } else {
            return this.description == var2.description;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<UriBase> {
         return UriBase.$serializer.INSTANCE as KSerializer<UriBase>;
      }
   }
}
