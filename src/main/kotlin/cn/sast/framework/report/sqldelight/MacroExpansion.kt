package cn.sast.framework.report.sqldelight

public data class MacroExpansion(__macro_note_set_hash_id: Long, __macro_note_id: Long) {
   public final val __macro_note_set_hash_id: Long
   public final val __macro_note_id: Long

   init {
      this.__macro_note_set_hash_id = __macro_note_set_hash_id;
      this.__macro_note_id = __macro_note_id;
   }

   public operator fun component1(): Long {
      return this.__macro_note_set_hash_id;
   }

   public operator fun component2(): Long {
      return this.__macro_note_id;
   }

   public fun copy(__macro_note_set_hash_id: Long = this.__macro_note_set_hash_id, __macro_note_id: Long = this.__macro_note_id): MacroExpansion {
      return new MacroExpansion(__macro_note_set_hash_id, __macro_note_id);
   }

   public override fun toString(): String {
      return "MacroExpansion(__macro_note_set_hash_id=${this.__macro_note_set_hash_id}, __macro_note_id=${this.__macro_note_id})";
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.__macro_note_set_hash_id) * 31 + java.lang.Long.hashCode(this.__macro_note_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is MacroExpansion) {
         return false;
      } else {
         val var2: MacroExpansion = other as MacroExpansion;
         if (this.__macro_note_set_hash_id != (other as MacroExpansion).__macro_note_set_hash_id) {
            return false;
         } else {
            return this.__macro_note_id == var2.__macro_note_id;
         }
      }
   }
}
