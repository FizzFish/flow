package cn.sast.framework.report.sqldelight

public data class Verify_control_flow_path(id: Long, __control_flow_array_hash_id: Long) {
   public final val id: Long
   public final val __control_flow_array_hash_id: Long

   init {
      this.id = id;
      this.__control_flow_array_hash_id = __control_flow_array_hash_id;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): Long {
      return this.__control_flow_array_hash_id;
   }

   public fun copy(id: Long = this.id, __control_flow_array_hash_id: Long = this.__control_flow_array_hash_id): Verify_control_flow_path {
      return new Verify_control_flow_path(id, __control_flow_array_hash_id);
   }

   public override fun toString(): String {
      return "Verify_control_flow_path(id=${this.id}, __control_flow_array_hash_id=${this.__control_flow_array_hash_id})";
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.id) * 31 + java.lang.Long.hashCode(this.__control_flow_array_hash_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Verify_control_flow_path) {
         return false;
      } else {
         val var2: Verify_control_flow_path = other as Verify_control_flow_path;
         if (this.id != (other as Verify_control_flow_path).id) {
            return false;
         } else {
            return this.__control_flow_array_hash_id == var2.__control_flow_array_hash_id;
         }
      }
   }
}
