package cn.sast.api.report

import cn.sast.common.IResFile
import kotlin.enums.EnumEntries

public data class ExpectBugAnnotationData<BugT>(file: IResFile, line: Int, column: Int, bug: Any, kind: cn.sast.api.report.ExpectBugAnnotationData.Kind) {
   public final val file: IResFile
   public final val line: Int
   public final val column: Int
   public final val bug: Any
   public final val kind: cn.sast.api.report.ExpectBugAnnotationData.Kind

   init {
      this.file = file;
      this.line = line;
      this.column = column;
      this.bug = (BugT)bug;
      this.kind = kind;
   }

   public override fun toString(): String {
      return "file: ${this.file}:${this.line}:${this.column} kind: ${this.kind} a bug: ${this.bug}";
   }

   public operator fun component1(): IResFile {
      return this.file;
   }

   public operator fun component2(): Int {
      return this.line;
   }

   public operator fun component3(): Int {
      return this.column;
   }

   public operator fun component4(): Any {
      return this.bug;
   }

   public operator fun component5(): cn.sast.api.report.ExpectBugAnnotationData.Kind {
      return this.kind;
   }

   public fun copy(
      file: IResFile = this.file,
      line: Int = this.line,
      column: Int = this.column,
      bug: Any = this.bug,
      kind: cn.sast.api.report.ExpectBugAnnotationData.Kind = this.kind
   ): ExpectBugAnnotationData<Any> {
      return new ExpectBugAnnotationData<>(file, line, column, (BugT)bug, kind);
   }

   public override fun hashCode(): Int {
      return (
               ((this.file.hashCode() * 31 + Integer.hashCode(this.line)) * 31 + Integer.hashCode(this.column)) * 31
                  + (if (this.bug == null) 0 else this.bug.hashCode())
            )
            * 31
         + this.kind.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ExpectBugAnnotationData) {
         return false;
      } else {
         val var2: ExpectBugAnnotationData = other as ExpectBugAnnotationData;
         if (!(this.file == (other as ExpectBugAnnotationData).file)) {
            return false;
         } else if (this.line != var2.line) {
            return false;
         } else if (this.column != var2.column) {
            return false;
         } else if (!(this.bug == var2.bug)) {
            return false;
         } else {
            return this.kind === var2.kind;
         }
      }
   }

   public enum class Kind {
      Expect,
      Escape
      @JvmStatic
      fun getEntries(): EnumEntries<ExpectBugAnnotationData.Kind> {
         return $ENTRIES;
      }
   }
}
