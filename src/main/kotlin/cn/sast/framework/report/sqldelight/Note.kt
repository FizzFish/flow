package cn.sast.framework.report.sqldelight

public data class Note(
    public val id: Long,
    public val kind: String,
    public val display_hint: String,
    public val __file_id: Long,
    public val _file_abs_path: String,
    public val line: Long,
    public val column: Long?,
    public val message_en: String,
    public val message_zh: String,
    public val __notices_region_id: Long?,
    public val __func_region_id: Long?
) {
    public operator fun component1(): Long = this.id
    public operator fun component2(): String = this.kind
    public operator fun component3(): String = this.display_hint
    public operator fun component4(): Long = this.__file_id
    public operator fun component5(): String = this._file_abs_path
    public operator fun component6(): Long = this.line
    public operator fun component7(): Long? = this.column
    public operator fun component8(): String = this.message_en
    public operator fun component9(): String = this.message_zh
    public operator fun component10(): Long? = this.__notices_region_id
    public operator fun component11(): Long? = this.__func_region_id

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
    ): Note = Note(
        id,
        kind,
        display_hint,
        __file_id,
        _file_abs_path,
        line,
        column,
        message_en,
        message_zh,
        __notices_region_id,
        __func_region_id
    )

    public override fun toString(): String =
        "Note(id=$id, kind=$kind, display_hint=$display_hint, __file_id=$__file_id, _file_abs_path=$_file_abs_path, line=$line, column=$column, message_en=$message_en, message_zh=$message_zh, __notices_region_id=$__notices_region_id, __func_region_id=$__func_region_id)"

    public override fun hashCode(): Int = (
        (
            (
                (
                    (
                        (
                            (
                                (
                                    (
                                        (id.hashCode() * 31 + kind.hashCode()) * 31 + display_hint.hashCode()
                                    ) * 31 + __file_id.hashCode()
                                ) * 31 + _file_abs_path.hashCode()
                            ) * 31 + line.hashCode()
                        ) * 31 + (column?.hashCode() ?: 0)
                    ) * 31 + message_en.hashCode()
                ) * 31 + message_zh.hashCode()
            ) * 31 + (__notices_region_id?.hashCode() ?: 0)
        ) * 31 + (__func_region_id?.hashCode() ?: 0)
    )

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        
        return id == other.id &&
            kind == other.kind &&
            display_hint == other.display_hint &&
            __file_id == other.__file_id &&
            _file_abs_path == other._file_abs_path &&
            line == other.line &&
            column == other.column &&
            message_en == other.message_en &&
            message_zh == other.message_zh &&
            __notices_region_id == other.__notices_region_id &&
            __func_region_id == other.__func_region_id
    }
}