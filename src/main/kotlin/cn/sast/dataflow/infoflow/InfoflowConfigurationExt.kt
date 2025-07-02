package cn.sast.dataflow.infoflow

public data class InfoflowConfigurationExt(useSparseOpt: Boolean = true, missing_summaries_file: String? = null) {
   public final var useSparseOpt: Boolean
      internal set

   public final var missing_summaries_file: String?
      internal set

   init {
      this.useSparseOpt = useSparseOpt;
      this.missing_summaries_file = missing_summaries_file;
   }

   public operator fun component1(): Boolean {
      return this.useSparseOpt;
   }

   public operator fun component2(): String? {
      return this.missing_summaries_file;
   }

   public fun copy(useSparseOpt: Boolean = this.useSparseOpt, missing_summaries_file: String? = this.missing_summaries_file): InfoflowConfigurationExt {
      return new InfoflowConfigurationExt(useSparseOpt, missing_summaries_file);
   }

   public override fun toString(): String {
      return "InfoflowConfigurationExt(useSparseOpt=${this.useSparseOpt}, missing_summaries_file=${this.missing_summaries_file})";
   }

   public override fun hashCode(): Int {
      return java.lang.Boolean.hashCode(this.useSparseOpt) * 31 + (if (this.missing_summaries_file == null) 0 else this.missing_summaries_file.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is InfoflowConfigurationExt) {
         return false;
      } else {
         val var2: InfoflowConfigurationExt = other as InfoflowConfigurationExt;
         if (this.useSparseOpt != (other as InfoflowConfigurationExt).useSparseOpt) {
            return false;
         } else {
            return this.missing_summaries_file == var2.missing_summaries_file;
         }
      }
   }

   fun InfoflowConfigurationExt() {
      this(false, null, 3, null);
   }
}
