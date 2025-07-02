package cn.sast.framework.report.sqldelight

public data class Region(id: Long, __file_id: Long, start_line: Long, start_column: Long?, end_line: Long?, end_column: Long?) {
   public final val id: Long
   public final val __file_id: Long
   public final val start_line: Long
   public final val start_column: Long?
   public final val end_line: Long?
   public final val end_column: Long?

   init {
      this.id = id;
      this.__file_id = __file_id;
      this.start_line = start_line;
      this.start_column = start_column;
      this.end_line = end_line;
      this.end_column = end_column;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): Long {
      return this.__file_id;
   }

   public operator fun component3(): Long {
      return this.start_line;
   }

   public operator fun component4(): Long? {
      return this.start_column;
   }

   public operator fun component5(): Long? {
      return this.end_line;
   }

   public operator fun component6(): Long? {
      return this.end_column;
   }

   public fun copy(
      id: Long = this.id,
      __file_id: Long = this.__file_id,
      start_line: Long = this.start_line,
      start_column: Long? = this.start_column,
      end_line: Long? = this.end_line,
      end_column: Long? = this.end_column
   ): Region {
      return new Region(id, __file_id, start_line, start_column, end_line, end_column);
   }

   public override fun toString(): String {
      return "Region(id=${this.id}, __file_id=${this.__file_id}, start_line=${this.start_line}, start_column=${this.start_column}, end_line=${this.end_line}, end_column=${this.end_column})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        ((java.lang.Long.hashCode(this.id) * 31 + java.lang.Long.hashCode(this.__file_id)) * 31 + java.lang.Long.hashCode(this.start_line))
                              * 31
                           + (if (this.start_column == null) 0 else this.start_column.hashCode())
                     )
                     * 31
                  + (if (this.end_line == null) 0 else this.end_line.hashCode())
            )
            * 31
         + (if (this.end_column == null) 0 else this.end_column.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Region) {
         return false;
      } else {
         val var2: Region = other as Region;
         if (this.id != (other as Region).id) {
            return false;
         } else if (this.__file_id != var2.__file_id) {
            return false;
         } else if (this.start_line != var2.start_line) {
            return false;
         } else if (!(this.start_column == var2.start_column)) {
            return false;
         } else if (!(this.end_line == var2.end_line)) {
            return false;
         } else {
            return this.end_column == var2.end_column;
         }
      }
   }
}
