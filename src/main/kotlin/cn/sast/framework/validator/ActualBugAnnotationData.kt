package cn.sast.framework.validator

import cn.sast.api.report.ReportKt
import cn.sast.common.IResFile
import com.feysh.corax.config.api.CheckType

internal data class ActualBugAnnotationData(file: IResFile, line: Int, checkType: CheckType) {
   public final val file: IResFile
   public final val line: Int
   public final val checkType: CheckType

   init {
      this.file = file;
      this.line = line;
      this.checkType = checkType;
   }

   public override fun toString(): String {
      this.checkType.getBugMessage();
      return "${this.file}:${this.line} report a [${ReportKt.getPerfectName(this.checkType)}]";
   }

   public operator fun component1(): IResFile {
      return this.file;
   }

   public operator fun component2(): Int {
      return this.line;
   }

   public operator fun component3(): CheckType {
      return this.checkType;
   }

   public fun copy(file: IResFile = this.file, line: Int = this.line, checkType: CheckType = this.checkType): ActualBugAnnotationData {
      return new ActualBugAnnotationData(file, line, checkType);
   }

   public override fun hashCode(): Int {
      return (this.file.hashCode() * 31 + Integer.hashCode(this.line)) * 31 + this.checkType.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ActualBugAnnotationData) {
         return false;
      } else {
         val var2: ActualBugAnnotationData = other as ActualBugAnnotationData;
         if (!(this.file == (other as ActualBugAnnotationData).file)) {
            return false;
         } else if (this.line != var2.line) {
            return false;
         } else {
            return this.checkType == var2.checkType;
         }
      }
   }
}
