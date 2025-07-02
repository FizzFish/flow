package cn.sast.framework.report.sqldelight.note

public data class Verify_file(id: Long, __file_id: Long) {
   public final val id: Long
   public final val __file_id: Long

   init {
      this.id = id;
      this.__file_id = __file_id;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): Long {
      return this.__file_id;
   }

   public fun copy(id: Long = this.id, __file_id: Long = this.__file_id): Verify_file {
      return new Verify_file(id, __file_id);
   }

   public override fun toString(): String {
      return "Verify_file(id=${this.id}, __file_id=${this.__file_id})";
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.id) * 31 + java.lang.Long.hashCode(this.__file_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Verify_file) {
         return false;
      } else {
         val var2: Verify_file = other as Verify_file;
         if (this.id != (other as Verify_file).id) {
            return false;
         } else {
            return this.__file_id == var2.__file_id;
         }
      }
   }
}
