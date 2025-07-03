package cn.sast.framework.report.sqldelight

import java.util.Arrays

data class File(
    val id: Long,
    val file_raw_content_hash: String,
    val relative_path: String,
    val lines: Long,
    val encoding: String?,
    val file_raw_content_size: Long,
    val file_raw_content: ByteArray
) {
    operator fun component1(): Long = id

    operator fun component2(): String = file_raw_content_hash

    operator fun component3(): String = relative_path

    operator fun component4(): Long = lines

    operator fun component5(): String? = encoding

    operator fun component6(): Long = file_raw_content_size

    operator fun component7(): ByteArray = file_raw_content

    fun copy(
        id: Long = this.id,
        file_raw_content_hash: String = this.file_raw_content_hash,
        relative_path: String = this.relative_path,
        lines: Long = this.lines,
        encoding: String? = this.encoding,
        file_raw_content_size: Long = this.file_raw_content_size,
        file_raw_content: ByteArray = this.file_raw_content
    ): File {
        return File(id, file_raw_content_hash, relative_path, lines, encoding, file_raw_content_size, file_raw_content)
    }

    override fun toString(): String {
        return "File(id=$id, file_raw_content_hash=$file_raw_content_hash, relative_path=$relative_path, lines=$lines, encoding=$encoding, file_raw_content_size=$file_raw_content_size, file_raw_content=${Arrays.toString(file_raw_content)})"
    }

    override fun hashCode(): Int {
        return (
            (
                (
                    (
                        ((id.hashCode() * 31 + file_raw_content_hash.hashCode()) * 31 + relative_path.hashCode()) * 31
                            + lines.hashCode()
                    ) * 31
                    + (encoding?.hashCode() ?: 0)
                ) * 31
                + file_raw_content_size.hashCode()
            ) * 31
            + Arrays.hashCode(file_raw_content)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is File) return false

        if (id != other.id) return false
        if (file_raw_content_hash != other.file_raw_content_hash) return false
        if (relative_path != other.relative_path) return false
        if (lines != other.lines) return false
        if (encoding != other.encoding) return false
        if (file_raw_content_size != other.file_raw_content_size) return false
        if (!file_raw_content.contentEquals(other.file_raw_content)) return false

        return true
    }
}