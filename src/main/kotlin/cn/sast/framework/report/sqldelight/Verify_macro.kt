package cn.sast.framework.report.sqldelight

public data class Verify_macro(id: Long, __macro_note_set_hash_id: Long) {
   public final val id: Long
   public final val __macro_note_set_hash_id: Long

   init {
      this.id = id;
      this.__macro_note_set_hash_id = __macro_note_set_hash_id;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): Long {
      return this.__macro_note_set_hash_id;
   }

   public fun copy(id: Long = this.id, __macro_note_set_hash_id: Long = this.__macro_note_set_hash_id): Verify_macro {
      return new Verify_macro(id, __macro_note_set_hash_id);
   }

   public override fun toString(): String {
      return "Verify_macro(id=${this.id}, __macro_note_set_hash_id=${this.__macro_note_set_hash_id})";
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.id) * 31 + java.lang.Long.hashCode(this.__macro_note_set_hash_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Verify_macro) {
         return false;
      } else {
         val var2: Verify_macro = other as Verify_macro;
         if (this.id != (other as Verify_macro).id) {
            return false;
         } else {
            return this.__macro_note_set_hash_id == var2.__macro_note_set_hash_id;
         }
      }
   }
}
