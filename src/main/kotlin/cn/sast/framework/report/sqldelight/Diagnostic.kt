package cn.sast.framework.report.sqldelight

public data class Diagnostic(id: Long,
   rule_name: String,
   _rule_short_description_zh: String?,
   __file_id: Long?,
   _file_abs_path: String,
   _line: Long,
   _column: Long,
   _message_en: String,
   _message_zh: String,
   severity: String?,
   precision: String?,
   likelihood: String?,
   impact: String?,
   technique: String?,
   analysis_scope: String?,
   line_content: String?,
   __note_array_hash_id: Long,
   __control_flow_array_hash_id: Long?,
   __macro_note_set_hash_id: Long?
) {
   public final val id: Long
   public final val rule_name: String
   public final val _rule_short_description_zh: String?
   public final val __file_id: Long?
   public final val _file_abs_path: String
   public final val _line: Long
   public final val _column: Long
   public final val _message_en: String
   public final val _message_zh: String
   public final val severity: String?
   public final val precision: String?
   public final val likelihood: String?
   public final val impact: String?
   public final val technique: String?
   public final val analysis_scope: String?
   public final val line_content: String?
   public final val __note_array_hash_id: Long
   public final val __control_flow_array_hash_id: Long?
   public final val __macro_note_set_hash_id: Long?

   init {
      this.id = id;
      this.rule_name = rule_name;
      this._rule_short_description_zh = _rule_short_description_zh;
      this.__file_id = __file_id;
      this._file_abs_path = _file_abs_path;
      this._line = _line;
      this._column = _column;
      this._message_en = _message_en;
      this._message_zh = _message_zh;
      this.severity = severity;
      this.precision = precision;
      this.likelihood = likelihood;
      this.impact = impact;
      this.technique = technique;
      this.analysis_scope = analysis_scope;
      this.line_content = line_content;
      this.__note_array_hash_id = __note_array_hash_id;
      this.__control_flow_array_hash_id = __control_flow_array_hash_id;
      this.__macro_note_set_hash_id = __macro_note_set_hash_id;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): String {
      return this.rule_name;
   }

   public operator fun component3(): String? {
      return this._rule_short_description_zh;
   }

   public operator fun component4(): Long? {
      return this.__file_id;
   }

   public operator fun component5(): String {
      return this._file_abs_path;
   }

   public operator fun component6(): Long {
      return this._line;
   }

   public operator fun component7(): Long {
      return this._column;
   }

   public operator fun component8(): String {
      return this._message_en;
   }

   public operator fun component9(): String {
      return this._message_zh;
   }

   public operator fun component10(): String? {
      return this.severity;
   }

   public operator fun component11(): String? {
      return this.precision;
   }

   public operator fun component12(): String? {
      return this.likelihood;
   }

   public operator fun component13(): String? {
      return this.impact;
   }

   public operator fun component14(): String? {
      return this.technique;
   }

   public operator fun component15(): String? {
      return this.analysis_scope;
   }

   public operator fun component16(): String? {
      return this.line_content;
   }

   public operator fun component17(): Long {
      return this.__note_array_hash_id;
   }

   public operator fun component18(): Long? {
      return this.__control_flow_array_hash_id;
   }

   public operator fun component19(): Long? {
      return this.__macro_note_set_hash_id;
   }

   public fun copy(
      id: Long = this.id,
      rule_name: String = this.rule_name,
      _rule_short_description_zh: String? = this._rule_short_description_zh,
      __file_id: Long? = this.__file_id,
      _file_abs_path: String = this._file_abs_path,
      _line: Long = this._line,
      _column: Long = this._column,
      _message_en: String = this._message_en,
      _message_zh: String = this._message_zh,
      severity: String? = this.severity,
      precision: String? = this.precision,
      likelihood: String? = this.likelihood,
      impact: String? = this.impact,
      technique: String? = this.technique,
      analysis_scope: String? = this.analysis_scope,
      line_content: String? = this.line_content,
      __note_array_hash_id: Long = this.__note_array_hash_id,
      __control_flow_array_hash_id: Long? = this.__control_flow_array_hash_id,
      __macro_note_set_hash_id: Long? = this.__macro_note_set_hash_id
   ): Diagnostic {
      return new Diagnostic(
         id,
         rule_name,
         _rule_short_description_zh,
         __file_id,
         _file_abs_path,
         _line,
         _column,
         _message_en,
         _message_zh,
         severity,
         precision,
         likelihood,
         impact,
         technique,
         analysis_scope,
         line_content,
         __note_array_hash_id,
         __control_flow_array_hash_id,
         __macro_note_set_hash_id
      );
   }

   public override fun toString(): String {
      return "Diagnostic(id=${this.id}, rule_name=${this.rule_name}, _rule_short_description_zh=${this._rule_short_description_zh}, __file_id=${this.__file_id}, _file_abs_path=${this._file_abs_path}, _line=${this._line}, _column=${this._column}, _message_en=${this._message_en}, _message_zh=${this._message_zh}, severity=${this.severity}, precision=${this.precision}, likelihood=${this.likelihood}, impact=${this.impact}, technique=${this.technique}, analysis_scope=${this.analysis_scope}, line_content=${this.line_content}, __note_array_hash_id=${this.__note_array_hash_id}, __control_flow_array_hash_id=${this.__control_flow_array_hash_id}, __macro_note_set_hash_id=${this.__macro_note_set_hash_id})";
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
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    (
                                                                                                                                             (
                                                                                                                                                      (
                                                                                                                                                               java.lang.Long.hashCode(
                                                                                                                                                                        this.id
                                                                                                                                                                     )
                                                                                                                                                                     * 31
                                                                                                                                                                  + this.rule_name
                                                                                                                                                                     .hashCode()
                                                                                                                                                            )
                                                                                                                                                            * 31
                                                                                                                                                         + (
                                                                                                                                                            if (this._rule_short_description_zh
                                                                                                                                                                  == null)
                                                                                                                                                               0
                                                                                                                                                               else
                                                                                                                                                               this._rule_short_description_zh
                                                                                                                                                                  .hashCode()
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + (
                                                                                                                                                   if (this.__file_id
                                                                                                                                                         == null)
                                                                                                                                                      0
                                                                                                                                                      else
                                                                                                                                                      this.__file_id
                                                                                                                                                         .hashCode()
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + this._file_abs_path
                                                                                                                                          .hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + java.lang.Long.hashCode(
                                                                                                                                 this._line
                                                                                                                              )
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + java.lang.Long.hashCode(this._column)
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this._message_en.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this._message_zh.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + (if (this.severity == null) 0 else this.severity.hashCode())
                                                                                    )
                                                                                    * 31
                                                                                 + (if (this.precision == null) 0 else this.precision.hashCode())
                                                                           )
                                                                           * 31
                                                                        + (if (this.likelihood == null) 0 else this.likelihood.hashCode())
                                                                  )
                                                                  * 31
                                                               + (if (this.impact == null) 0 else this.impact.hashCode())
                                                         )
                                                         * 31
                                                      + (if (this.technique == null) 0 else this.technique.hashCode())
                                                )
                                                * 31
                                             + (if (this.analysis_scope == null) 0 else this.analysis_scope.hashCode())
                                       )
                                       * 31
                                    + (if (this.line_content == null) 0 else this.line_content.hashCode())
                              )
                              * 31
                           + java.lang.Long.hashCode(this.__note_array_hash_id)
                     )
                     * 31
                  + (if (this.__control_flow_array_hash_id == null) 0 else this.__control_flow_array_hash_id.hashCode())
            )
            * 31
         + (if (this.__macro_note_set_hash_id == null) 0 else this.__macro_note_set_hash_id.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Diagnostic) {
         return false;
      } else {
         val var2: Diagnostic = other as Diagnostic;
         if (this.id != (other as Diagnostic).id) {
            return false;
         } else if (!(this.rule_name == var2.rule_name)) {
            return false;
         } else if (!(this._rule_short_description_zh == var2._rule_short_description_zh)) {
            return false;
         } else if (!(this.__file_id == var2.__file_id)) {
            return false;
         } else if (!(this._file_abs_path == var2._file_abs_path)) {
            return false;
         } else if (this._line != var2._line) {
            return false;
         } else if (this._column != var2._column) {
            return false;
         } else if (!(this._message_en == var2._message_en)) {
            return false;
         } else if (!(this._message_zh == var2._message_zh)) {
            return false;
         } else if (!(this.severity == var2.severity)) {
            return false;
         } else if (!(this.precision == var2.precision)) {
            return false;
         } else if (!(this.likelihood == var2.likelihood)) {
            return false;
         } else if (!(this.impact == var2.impact)) {
            return false;
         } else if (!(this.technique == var2.technique)) {
            return false;
         } else if (!(this.analysis_scope == var2.analysis_scope)) {
            return false;
         } else if (!(this.line_content == var2.line_content)) {
            return false;
         } else if (this.__note_array_hash_id != var2.__note_array_hash_id) {
            return false;
         } else if (!(this.__control_flow_array_hash_id == var2.__control_flow_array_hash_id)) {
            return false;
         } else {
            return this.__macro_note_set_hash_id == var2.__macro_note_set_hash_id;
         }
      }
   }
}
