package cn.sast.framework.report.sarif

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
public data class IndexedMessage(id: String = "default") {
   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val id: String

   init {
      this.id = id;
   }

   public operator fun component1(): String {
      return this.id;
   }

   public fun copy(id: String = this.id): IndexedMessage {
      return new IndexedMessage(id);
   }

   public override fun toString(): String {
      return "IndexedMessage(id=${this.id})";
   }

   public override fun hashCode(): Int {
      return this.id.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is IndexedMessage) {
         return false;
      } else {
         return this.id == (other as IndexedMessage).id;
      }
   }

   fun IndexedMessage() {
      this(null, 1, null);
   }

   public companion object {
      public fun serializer(): KSerializer<IndexedMessage> {
         return IndexedMessage.$serializer.INSTANCE as KSerializer<IndexedMessage>;
      }
   }
}
