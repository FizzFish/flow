package cn.sast.framework.report.sqldelight

public data class NotePath(__note_array_hash_id: Long, note_sequence: Long, note_stack_depth: Long?, note_is_key_event: Long?, __note_id: Long) {
   public final val __note_array_hash_id: Long
   public final val note_sequence: Long
   public final val note_stack_depth: Long?
   public final val note_is_key_event: Long?
   public final val __note_id: Long

   init {
      this.__note_array_hash_id = __note_array_hash_id;
      this.note_sequence = note_sequence;
      this.note_stack_depth = note_stack_depth;
      this.note_is_key_event = note_is_key_event;
      this.__note_id = __note_id;
   }

   public operator fun component1(): Long {
      return this.__note_array_hash_id;
   }

   public operator fun component2(): Long {
      return this.note_sequence;
   }

   public operator fun component3(): Long? {
      return this.note_stack_depth;
   }

   public operator fun component4(): Long? {
      return this.note_is_key_event;
   }

   public operator fun component5(): Long {
      return this.__note_id;
   }

   public fun copy(
      __note_array_hash_id: Long = this.__note_array_hash_id,
      note_sequence: Long = this.note_sequence,
      note_stack_depth: Long? = this.note_stack_depth,
      note_is_key_event: Long? = this.note_is_key_event,
      __note_id: Long = this.__note_id
   ): NotePath {
      return new NotePath(__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id);
   }

   public override fun toString(): String {
      return "NotePath(__note_array_hash_id=${this.__note_array_hash_id}, note_sequence=${this.note_sequence}, note_stack_depth=${this.note_stack_depth}, note_is_key_event=${this.note_is_key_event}, __note_id=${this.__note_id})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (java.lang.Long.hashCode(this.__note_array_hash_id) * 31 + java.lang.Long.hashCode(this.note_sequence)) * 31
                           + (if (this.note_stack_depth == null) 0 else this.note_stack_depth.hashCode())
                     )
                     * 31
                  + (if (this.note_is_key_event == null) 0 else this.note_is_key_event.hashCode())
            )
            * 31
         + java.lang.Long.hashCode(this.__note_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is NotePath) {
         return false;
      } else {
         val var2: NotePath = other as NotePath;
         if (this.__note_array_hash_id != (other as NotePath).__note_array_hash_id) {
            return false;
         } else if (this.note_sequence != var2.note_sequence) {
            return false;
         } else if (!(this.note_stack_depth == var2.note_stack_depth)) {
            return false;
         } else if (!(this.note_is_key_event == var2.note_is_key_event)) {
            return false;
         } else {
            return this.__note_id == var2.__note_id;
         }
      }
   }
}
