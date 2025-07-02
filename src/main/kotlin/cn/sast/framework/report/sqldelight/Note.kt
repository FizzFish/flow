package cn.sast.framework.report.sqldelight

public data class Note(id: Long,
   kind: String,
   display_hint: String,
   __file_id: Long,
   _file_abs_path: String,
   line: Long,
   column: Long?,
   message_en: String,
   message_zh: String,
   __notices_region_id: Long?,
   __func_region_id: Long?
) {
   public final val id: Long
   public final val kind: String
   public final val display_hint: String
   public final val __file_id: Long
   public final val _file_abs_path: String
   public final val line: Long
   public final val column: Long?
   public final val message_en: String
   public final val message_zh: String
   public final val __notices_region_id: Long?
   public final val __func_region_id: Long?

   init {
      this.id = id;
      this.kind = kind;
      this.display_hint = display_hint;
      this.__file_id = __file_id;
      this._file_abs_path = _file_abs_path;
      this.line = line;
      this.column = column;
      this.message_en = message_en;
      this.message_zh = message_zh;
      this.__notices_region_id = __notices_region_id;
      this.__func_region_id = __func_region_id;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): String {
      return this.kind;
   }

   public operator fun component3(): String {
      return this.display_hint;
   }

   public operator fun component4(): Long {
      return this.__file_id;
   }

   public operator fun component5(): String {
      return this._file_abs_path;
   }

   public operator fun component6(): Long {
      return this.line;
   }

   public operator fun component7(): Long? {
      return this.column;
   }

   public operator fun component8(): String {
      return this.message_en;
   }

   public operator fun component9(): String {
      return this.message_zh;
   }

   public operator fun component10(): Long? {
      return this.__notices_region_id;
   }

   public operator fun component11(): Long? {
      return this.__func_region_id;
   }

   public fun copy(
      id: Long = this.id,
      kind: String = this.kind,
      display_hint: String = this.display_hint,
      __file_id: Long = this.__file_id,
      _file_abs_path: String = this._file_abs_path,
      line: Long = this.line,
      column: Long? = this.column,
      message_en: String = this.message_en,
      message_zh: String = this.message_zh,
      __notices_region_id: Long? = this.__notices_region_id,
      __func_region_id: Long? = this.__func_region_id
   ): Note {
      return new Note(id, kind, display_hint, __file_id, _file_abs_path, line, column, message_en, message_zh, __notices_region_id, __func_region_id);
   }

   public override fun toString(): String {
      return "Note(id=${this.id}, kind=${this.kind}, display_hint=${this.display_hint}, __file_id=${this.__file_id}, _file_abs_path=${this._file_abs_path}, line=${this.line}, column=${this.column}, message_en=${this.message_en}, message_zh=${this.message_zh}, __notices_region_id=${this.__notices_region_id}, __func_region_id=${this.__func_region_id})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (java.lang.Long.hashCode(this.id) * 31 + this.kind.hashCode()) * 31
                                                                                 + this.display_hint.hashCode()
                                                                           )
                                                                           * 31
                                                                        + java.lang.Long.hashCode(this.__file_id)
                                                                  )
                                                                  * 31
                                                               + this._file_abs_path.hashCode()
                                                         )
                                                         * 31
                                                      + java.lang.Long.hashCode(this.line)
                                                )
                                                * 31
                                             + (if (this.column == null) 0 else this.column.hashCode())
                                       )
                                       * 31
                                    + this.message_en.hashCode()
                              )
                              * 31
                           + this.message_zh.hashCode()
                     )
                     * 31
                  + (if (this.__notices_region_id == null) 0 else this.__notices_region_id.hashCode())
            )
            * 31
         + (if (this.__func_region_id == null) 0 else this.__func_region_id.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Note) {
         return false;
      } else {
         val var2: Note = other as Note;
         if (this.id != (other as Note).id) {
            return false;
         } else if (!(this.kind == var2.kind)) {
            return false;
         } else if (!(this.display_hint == var2.display_hint)) {
            return false;
         } else if (this.__file_id != var2.__file_id) {
            return false;
         } else if (!(this._file_abs_path == var2._file_abs_path)) {
            return false;
         } else if (this.line != var2.line) {
            return false;
         } else if (!(this.column == var2.column)) {
            return false;
         } else if (!(this.message_en == var2.message_en)) {
            return false;
         } else if (!(this.message_zh == var2.message_zh)) {
            return false;
         } else if (!(this.__notices_region_id == var2.__notices_region_id)) {
            return false;
         } else {
            return this.__func_region_id == var2.__func_region_id;
         }
      }
   }
}
