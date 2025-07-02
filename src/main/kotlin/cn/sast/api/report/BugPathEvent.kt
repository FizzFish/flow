package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/BugPathEvent\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public data class BugPathEvent(message: Map<Language, String>, classname: IBugResInfo, region: Region, stackDepth: Int? = null) :
   java.lang.Comparable<BugPathEvent>,
   IReportHashAble {
   public final val message: Map<Language, String>
   public final val classname: IBugResInfo
   public final val region: Region
   public final val stackDepth: Int?

   init {
      this.message = message;
      this.classname = classname;
      this.region = region;
      this.stackDepth = stackDepth;
   }

   public open operator fun compareTo(other: BugPathEvent): Int {
      var var3: Int = ComparatorUtilsKt.compareToMap(this.message, other.message);
      var var2: Int = if (var3.intValue() != 0) var3 else null;
      if (var2 != null) {
         return var2.intValue();
      } else {
         var3 = this.classname.compareTo(other.classname);
         var2 = if (var3.intValue() != 0) var3 else null;
         label35:
         if (var2 != null) {
            return var2.intValue();
         } else {
            var3 = this.region.compareTo(other.region);
            var2 = if (var3.intValue() != 0) var3 else null;
            return if (var2 != null) var2.intValue() else 0;
         }
      }
   }

   public override fun reportHash(c: IReportHashCalculator): String {
      return "${this.classname.reportHash(c)}:${this.region}";
   }

   public fun reportHashWithMessage(c: IReportHashCalculator): String {
      return "${this.reportHash(c)} ${CollectionsKt.toSortedSet(this.message.values())}";
   }

   public override fun toString(): String {
      return "${this.classname} at ${this.region} ${this.message}";
   }

   public operator fun component1(): Map<Language, String> {
      return this.message;
   }

   public operator fun component2(): IBugResInfo {
      return this.classname;
   }

   public operator fun component3(): Region {
      return this.region;
   }

   public operator fun component4(): Int? {
      return this.stackDepth;
   }

   public fun copy(
      message: Map<Language, String> = this.message,
      classname: IBugResInfo = this.classname,
      region: Region = this.region,
      stackDepth: Int? = this.stackDepth
   ): BugPathEvent {
      return new BugPathEvent(message, classname, region, stackDepth);
   }

   public override fun hashCode(): Int {
      return ((this.message.hashCode() * 31 + this.classname.hashCode()) * 31 + this.region.hashCode()) * 31
         + (if (this.stackDepth == null) 0 else this.stackDepth.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is BugPathEvent) {
         return false;
      } else {
         val var2: BugPathEvent = other as BugPathEvent;
         if (!(this.message == (other as BugPathEvent).message)) {
            return false;
         } else if (!(this.classname == var2.classname)) {
            return false;
         } else if (!(this.region == var2.region)) {
            return false;
         } else {
            return this.stackDepth == var2.stackDepth;
         }
      }
   }
}
