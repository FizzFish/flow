package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/BugPathPosition\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public data class BugPathPosition(classname: IBugResInfo, region: Region?) : java.lang.Comparable<BugPathPosition> {
   public final val classname: IBugResInfo
   public final val region: Region?

   init {
      this.classname = classname;
      this.region = region;
   }

   public open operator fun compareTo(other: BugPathPosition): Int {
      var var3: Int = this.classname.compareTo(other.classname);
      var var2: Int = if (var3.intValue() != 0) var3 else null;
      label27:
      if (var2 != null) {
         return var2.intValue();
      } else {
         var3 = ComparatorUtilsKt.compareToNullable(this.region, other.region);
         var2 = if (var3.intValue() != 0) var3 else null;
         return if (var2 != null) var2.intValue() else 0;
      }
   }

   public override fun toString(): String {
      return "${this.classname} ${this.region}";
   }

   public operator fun component1(): IBugResInfo {
      return this.classname;
   }

   public operator fun component2(): Region? {
      return this.region;
   }

   public fun copy(classname: IBugResInfo = this.classname, region: Region? = this.region): BugPathPosition {
      return new BugPathPosition(classname, region);
   }

   public override fun hashCode(): Int {
      return this.classname.hashCode() * 31 + (if (this.region == null) 0 else this.region.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is BugPathPosition) {
         return false;
      } else {
         val var2: BugPathPosition = other as BugPathPosition;
         if (!(this.classname == (other as BugPathPosition).classname)) {
            return false;
         } else {
            return this.region == var2.region;
         }
      }
   }
}
