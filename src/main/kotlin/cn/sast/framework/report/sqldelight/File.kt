package cn.sast.framework.report.sqldelight

import java.util.Arrays

public data class File(id: Long,
   file_raw_content_hash: String,
   relative_path: String,
   lines: Long,
   encoding: String?,
   file_raw_content_size: Long,
   file_raw_content: ByteArray
) {
   public final val id: Long
   public final val file_raw_content_hash: String
   public final val relative_path: String
   public final val lines: Long
   public final val encoding: String?
   public final val file_raw_content_size: Long
   public final val file_raw_content: ByteArray

   init {
      this.id = id;
      this.file_raw_content_hash = file_raw_content_hash;
      this.relative_path = relative_path;
      this.lines = lines;
      this.encoding = encoding;
      this.file_raw_content_size = file_raw_content_size;
      this.file_raw_content = file_raw_content;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): String {
      return this.file_raw_content_hash;
   }

   public operator fun component3(): String {
      return this.relative_path;
   }

   public operator fun component4(): Long {
      return this.lines;
   }

   public operator fun component5(): String? {
      return this.encoding;
   }

   public operator fun component6(): Long {
      return this.file_raw_content_size;
   }

   public operator fun component7(): ByteArray {
      return this.file_raw_content;
   }

   public fun copy(
      id: Long = this.id,
      file_raw_content_hash: String = this.file_raw_content_hash,
      relative_path: String = this.relative_path,
      lines: Long = this.lines,
      encoding: String? = this.encoding,
      file_raw_content_size: Long = this.file_raw_content_size,
      file_raw_content: ByteArray = this.file_raw_content
   ): File {
      return new File(id, file_raw_content_hash, relative_path, lines, encoding, file_raw_content_size, file_raw_content);
   }

   public override fun toString(): String {
      return "File(id=${this.id}, file_raw_content_hash=${this.file_raw_content_hash}, relative_path=${this.relative_path}, lines=${this.lines}, encoding=${this.encoding}, file_raw_content_size=${this.file_raw_content_size}, file_raw_content=${Arrays.toString(
         this.file_raw_content
      )})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 ((java.lang.Long.hashCode(this.id) * 31 + this.file_raw_content_hash.hashCode()) * 31 + this.relative_path.hashCode()) * 31
                                    + java.lang.Long.hashCode(this.lines)
                              )
                              * 31
                           + (if (this.encoding == null) 0 else this.encoding.hashCode())
                     )
                     * 31
                  + java.lang.Long.hashCode(this.file_raw_content_size)
            )
            * 31
         + Arrays.hashCode(this.file_raw_content);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is File) {
         return false;
      } else {
         val var2: File = other as File;
         if (this.id != (other as File).id) {
            return false;
         } else if (!(this.file_raw_content_hash == var2.file_raw_content_hash)) {
            return false;
         } else if (!(this.relative_path == var2.relative_path)) {
            return false;
         } else if (this.lines != var2.lines) {
            return false;
         } else if (!(this.encoding == var2.encoding)) {
            return false;
         } else if (this.file_raw_content_size != var2.file_raw_content_size) {
            return false;
         } else {
            return this.file_raw_content == var2.file_raw_content;
         }
      }
   }
}
