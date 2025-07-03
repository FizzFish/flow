package cn.sast.framework.report.sqldelight

public data class AnalyzerResultFile(
    public val file_name: String,
    public val file_path: String?,
    public val __file_id: Long
) {
    public operator fun component1(): String {
        return this.file_name
    }

    public operator fun component2(): String? {
        return this.file_path
    }

    public operator fun component3(): Long {
        return this.__file_id
    }

    public fun copy(
        file_name: String = this.file_name,
        file_path: String? = this.file_path,
        __file_id: Long = this.__file_id
    ): AnalyzerResultFile {
        return AnalyzerResultFile(file_name, file_path, __file_id)
    }

    public override fun toString(): String {
        return "AnalyzerResultFile(file_name=${this.file_name}, file_path=${this.file_path}, __file_id=${this.__file_id})"
    }

    public override fun hashCode(): Int {
        return (this.file_name.hashCode() * 31 + (this.file_path?.hashCode() ?: 0)) * 31 + this.__file_id.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is AnalyzerResultFile) {
            return false
        }
        return this.file_name == other.file_name &&
            this.file_path == other.file_path &&
            this.__file_id == other.__file_id
    }
}