package cn.sast.framework.validator

import cn.sast.api.report.ReportKt
import com.feysh.corax.config.api.CheckType

internal data class RowCheckType(type: CheckType) : RowType() {
   public open val type: CheckType

   init {
      this.type = type;
   }

   public override fun toString(): String {
      return ReportKt.getPerfectName(this.getType());
   }

   public operator fun component1(): CheckType {
      return this.type;
   }

   public fun copy(type: CheckType = this.type): RowCheckType {
      return new RowCheckType(type);
   }

   public override fun hashCode(): Int {
      return this.type.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is RowCheckType) {
         return false;
      } else {
         return this.type == (other as RowCheckType).type;
      }
   }
}
