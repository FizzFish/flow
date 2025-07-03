package cn.sast.framework.report.sqldelight

public data class Diagnostic(
    val id: Long,
    val rule_name: String,
    val _rule_short_description_zh: String?,
    val __file_id: Long?,
    val _file_abs_path: String,
    val _line: Long,
    val _column: Long,
    val _message_en: String,
    val _message_zh: String,
    val severity: String?,
    val precision: String?,
    val likelihood: String?,
    val impact: String?,
    val technique: String?,
    val analysis_scope: String?,
    val line_content: String?,
    val __note_array_hash_id: Long,
    val __control_flow_array_hash_id: Long?,
    val __macro_note_set_hash_id: Long?
) {
    public operator fun component1(): Long = id

    public operator fun component2(): String = rule_name

    public operator fun component3(): String? = _rule_short_description_zh

    public operator fun component4(): Long? = __file_id

    public operator fun component5(): String = _file_abs_path

    public operator fun component6(): Long = _line

    public operator fun component7(): Long = _column

    public operator fun component8(): String = _message_en

    public operator fun component9(): String = _message_zh

    public operator fun component10(): String? = severity

    public operator fun component11(): String? = precision

    public operator fun component12(): String? = likelihood

    public operator fun component13(): String? = impact

    public operator fun component14(): String? = technique

    public operator fun component15(): String? = analysis_scope

    public operator fun component16(): String? = line_content

    public operator fun component17(): Long = __note_array_hash_id

    public operator fun component18(): Long? = __control_flow_array_hash_id

    public operator fun component19(): Long? = __macro_note_set_hash_id

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
    ): Diagnostic = Diagnostic(
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
    )

    override fun toString(): String =
        "Diagnostic(id=$id, rule_name=$rule_name, _rule_short_description_zh=$_rule_short_description_zh, __file_id=$__file_id, _file_abs_path=$_file_abs_path, _line=$_line, _column=$_column, _message_en=$_message_en, _message_zh=$_message_zh, severity=$severity, precision=$precision, likelihood=$likelihood, impact=$impact, technique=$technique, analysis_scope=$analysis_scope, line_content=$line_content, __note_array_hash_id=$__note_array_hash_id, __control_flow_array_hash_id=$__control_flow_array_hash_id, __macro_note_set_hash_id=$__macro_note_set_hash_id)"

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + rule_name.hashCode()
        result = 31 * result + (_rule_short_description_zh?.hashCode() ?: 0)
        result = 31 * result + (__file_id?.hashCode() ?: 0)
        result = 31 * result + _file_abs_path.hashCode()
        result = 31 * result + _line.hashCode()
        result = 31 * result + _column.hashCode()
        result = 31 * result + _message_en.hashCode()
        result = 31 * result + _message_zh.hashCode()
        result = 31 * result + (severity?.hashCode() ?: 0)
        result = 31 * result + (precision?.hashCode() ?: 0)
        result = 31 * result + (likelihood?.hashCode() ?: 0)
        result = 31 * result + (impact?.hashCode() ?: 0)
        result = 31 * result + (technique?.hashCode() ?: 0)
        result = 31 * result + (analysis_scope?.hashCode() ?: 0)
        result = 31 * result + (line_content?.hashCode() ?: 0)
        result = 31 * result + __note_array_hash_id.hashCode()
        result = 31 * result + (__control_flow_array_hash_id?.hashCode() ?: 0)
        result = 31 * result + (__macro_note_set_hash_id?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Diagnostic) return false

        if (id != other.id) return false
        if (rule_name != other.rule_name) return false
        if (_rule_short_description_zh != other._rule_short_description_zh) return false
        if (__file_id != other.__file_id) return false
        if (_file_abs_path != other._file_abs_path) return false
        if (_line != other._line) return false
        if (_column != other._column) return false
        if (_message_en != other._message_en) return false
        if (_message_zh != other._message_zh) return false
        if (severity != other.severity) return false
        if (precision != other.precision) return false
        if (likelihood != other.likelihood) return false
        if (impact != other.impact) return false
        if (technique != other.technique) return false
        if (analysis_scope != other.analysis_scope) return false
        if (line_content != other.line_content) return false
        if (__note_array_hash_id != other.__note_array_hash_id) return false
        if (__control_flow_array_hash_id != other.__control_flow_array_hash_id) return false
        if (__macro_note_set_hash_id != other.__macro_note_set_hash_id) return false

        return true
    }
}