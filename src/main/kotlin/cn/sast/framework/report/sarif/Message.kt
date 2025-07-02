package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Message(text: String, markdown: String = "") {
   public final val text: String
   public final val markdown: String

   init {
      this.text = text;
      this.markdown = markdown;
   }

   public operator fun component1(): String {
      return this.text;
   }

   public operator fun component2(): String {
      return this.markdown;
   }

   public fun copy(text: String = this.text, markdown: String = this.markdown): Message {
      return new Message(text, markdown);
   }

   public override fun toString(): String {
      return "Message(text=${this.text}, markdown=${this.markdown})";
   }

   public override fun hashCode(): Int {
      return this.text.hashCode() * 31 + this.markdown.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Message) {
         return false;
      } else {
         val var2: Message = other as Message;
         if (!(this.text == (other as Message).text)) {
            return false;
         } else {
            return this.markdown == var2.markdown;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Message> {
         return Message.$serializer.INSTANCE as KSerializer<Message>;
      }
   }
}
