package cn.sast.framework.report.sqldelight

public data class AnalyzerResultFile(file_name: String, file_path: String?, __file_id: Long) {
   public final val file_name: String
   public final val file_path: String?
   public final val __file_id: Long

   init {
      this.file_name = file_name;
      this.file_path = file_path;
      this.__file_id = __file_id;
   }

   public operator fun component1(): String {
      return this.file_name;
   }

   public operator fun component2(): String? {
      return this.file_path;
   }

   public operator fun component3(): Long {
      return this.__file_id;
   }

   public fun copy(file_name: String = this.file_name, file_path: String? = this.file_path, __file_id: Long = this.__file_id): AnalyzerResultFile {
      return new AnalyzerResultFile(file_name, file_path, __file_id);
   }

   public override fun toString(): String {
      return "AnalyzerResultFile(file_name=${this.file_name}, file_path=${this.file_path}, __file_id=${this.__file_id})";
   }

   public override fun hashCode(): Int {
      return (this.file_name.hashCode() * 31 + (if (this.file_path == null) 0 else this.file_path.hashCode())) * 31 + java.lang.Long.hashCode(this.__file_id);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AnalyzerResultFile) {
         return false;
      } else {
         val var2: AnalyzerResultFile = other as AnalyzerResultFile;
         if (!(this.file_name == (other as AnalyzerResultFile).file_name)) {
            return false;
         } else if (!(this.file_path == var2.file_path)) {
            return false;
         } else {
            return this.__file_id == var2.__file_id;
         }
      }
   }
}
