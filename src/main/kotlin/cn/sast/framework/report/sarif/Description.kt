package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Description(text: String) {
   public final val text: String

   init {
      this.text = text;
   }

   public operator fun component1(): String {
      return this.text;
   }

   public fun copy(text: String = this.text): Description {
      return new Description(text);
   }

   public override fun toString(): String {
      return "Description(text=${this.text})";
   }

   public override fun hashCode(): Int {
      return this.text.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Description) {
         return false;
      } else {
         return this.text == (other as Description).text;
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Description> {
         return Description.$serializer.INSTANCE as KSerializer<Description>;
      }
   }
}
