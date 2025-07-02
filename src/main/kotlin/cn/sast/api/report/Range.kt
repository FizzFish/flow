package cn.sast.api.report

import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/Range\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public data class Range(start_line: Int, start_col: Int, end_line: Int, end_col: Int) : java.lang.Comparable<Range>, IReportHashAble {
   public final val start_line: Int
   public final val start_col: Int
   public final val end_line: Int
   public final val end_col: Int

   init {
      this.start_line = start_line;
      this.start_col = start_col;
      this.end_line = end_line;
      this.end_col = end_col;
   }

   public open operator fun compareTo(other: Range): Int {
      var var3: Int = Intrinsics.compare(this.start_line, other.start_line);
      var var2: Int = if (var3.intValue() != 0) var3 else null;
      if (var2 != null) {
         return var2.intValue();
      } else {
         var3 = Intrinsics.compare(this.start_col, other.start_col);
         var2 = if (var3.intValue() != 0) var3 else null;
         if (var2 != null) {
            return var2.intValue();
         } else {
            var3 = Intrinsics.compare(this.end_line, other.end_line);
            var2 = if (var3.intValue() != 0) var3 else null;
            label43:
            if (var2 != null) {
               return var2.intValue();
            } else {
               var3 = Intrinsics.compare(this.end_col, other.end_col);
               var2 = if (var3.intValue() != 0) var3 else null;
               return if (var2 != null) var2.intValue() else 0;
            }
         }
      }
   }

   public override fun reportHash(c: IReportHashCalculator): String {
      return "${this.start_line},${this.start_col},${this.end_line},${this.end_col}";
   }

   public override fun toString(): String {
      return "[${this.start_line}:${this.start_col},${this.end_line}:${this.end_col}]";
   }

   public operator fun component1(): Int {
      return this.start_line;
   }

   public operator fun component2(): Int {
      return this.start_col;
   }

   public operator fun component3(): Int {
      return this.end_line;
   }

   public operator fun component4(): Int {
      return this.end_col;
   }

   public fun copy(start_line: Int = this.start_line, start_col: Int = this.start_col, end_line: Int = this.end_line, end_col: Int = this.end_col): Range {
      return new Range(start_line, start_col, end_line, end_col);
   }

   public override fun hashCode(): Int {
      return ((Integer.hashCode(this.start_line) * 31 + Integer.hashCode(this.start_col)) * 31 + Integer.hashCode(this.end_line)) * 31
         + Integer.hashCode(this.end_col);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Range) {
         return false;
      } else {
         val var2: Range = other as Range;
         if (this.start_line != (other as Range).start_line) {
            return false;
         } else if (this.start_col != var2.start_col) {
            return false;
         } else if (this.end_line != var2.end_line) {
            return false;
         } else {
            return this.end_col == var2.end_col;
         }
      }
   }
}
