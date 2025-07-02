package cn.sast.framework.report.sqldelight

public data class Verify_note_path(id: Long, __note_array_hash_id: Long) {
   public final val id: Long
   public final val __note_array_hash_id: Long

   init {
      this.id = id;
      this.__note_array_hash_id = __note_array_hash_id;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): Long {
      return this.__note_array_hash_id;
   }

   public fun copy(id: Long = this.id, __note_array_hash_id: Long = this.__note_array_hash_id): Verify_note_path {
      return new Verify_note_path(id, __note_array_hash_id);
   }

   public override fun toString(): String {
      return "Verify_note_path(id=${this.id}, __note_array_hash_id=${this.__note_array_hash_id})";
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.id) * 31 + java.lang.Long.hashCode(this.__note_array_hash_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Verify_note_path) {
         return false;
      } else {
         val var2: Verify_note_path = other as Verify_note_path;
         if (this.id != (other as Verify_note_path).id) {
            return false;
         } else {
            return this.__note_array_hash_id == var2.__note_array_hash_id;
         }
      }
   }
}
