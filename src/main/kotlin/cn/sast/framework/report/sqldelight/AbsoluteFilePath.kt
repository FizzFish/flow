package cn.sast.framework.report.sqldelight

public data class AbsoluteFilePath(file_abs_path: String, __file_id: Long) {
   public final val file_abs_path: String
   public final val __file_id: Long

   init {
      this.file_abs_path = file_abs_path;
      this.__file_id = __file_id;
   }

   public operator fun component1(): String {
      return this.file_abs_path;
   }

   public operator fun component2(): Long {
      return this.__file_id;
   }

   public fun copy(file_abs_path: String = this.file_abs_path, __file_id: Long = this.__file_id): AbsoluteFilePath {
      return new AbsoluteFilePath(file_abs_path, __file_id);
   }

   public override fun toString(): String {
      return "AbsoluteFilePath(file_abs_path=${this.file_abs_path}, __file_id=${this.__file_id})";
   }

   public override fun hashCode(): Int {
      return this.file_abs_path.hashCode() * 31 + java.lang.Long.hashCode(this.__file_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AbsoluteFilePath) {
         return false;
      } else {
         val var2: AbsoluteFilePath = other as AbsoluteFilePath;
         if (!(this.file_abs_path == (other as AbsoluteFilePath).file_abs_path)) {
            return false;
         } else {
            return this.__file_id == var2.__file_id;
         }
      }
   }
}
