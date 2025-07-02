package cn.sast.api.report

import cn.sast.api.util.ComparatorUtilsKt
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/MacroExpansion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public data class MacroExpansion(message: String, classname: IBugResInfo, line: Int, column: Int, range: Range? = null) : java.lang.Comparable<MacroExpansion> {
   public final val message: String
   public final val classname: IBugResInfo
   public final val line: Int
   public final val column: Int
   public final val range: Range?

   init {
      this.message = message;
      this.classname = classname;
      this.line = line;
      this.column = column;
      this.range = range;
   }

   public open operator fun compareTo(other: MacroExpansion): Int {
      var var3: Int = this.message.compareTo(other.message);
      var var2: Int = if (var3.intValue() != 0) var3 else null;
      if (var2 != null) {
         return var2.intValue();
      } else {
         var3 = this.classname.compareTo(other.classname);
         var2 = if (var3.intValue() != 0) var3 else null;
         if (var2 != null) {
            return var2.intValue();
         } else {
            var3 = Intrinsics.compare(this.line, other.line);
            var2 = if (var3.intValue() != 0) var3 else null;
            if (var2 != null) {
               return var2.intValue();
            } else {
               var3 = Intrinsics.compare(this.column, other.column);
               var2 = if (var3.intValue() != 0) var3 else null;
               label51:
               if (var2 != null) {
                  return var2.intValue();
               } else {
                  var3 = ComparatorUtilsKt.compareToNullable(this.range, other.range);
                  var2 = if (var3.intValue() != 0) var3 else null;
                  return if (var2 != null) var2.intValue() else 0;
               }
            }
         }
      }
   }

   public operator fun component1(): String {
      return this.message;
   }

   public operator fun component2(): IBugResInfo {
      return this.classname;
   }

   public operator fun component3(): Int {
      return this.line;
   }

   public operator fun component4(): Int {
      return this.column;
   }

   public operator fun component5(): Range? {
      return this.range;
   }

   public fun copy(
      message: String = this.message,
      classname: IBugResInfo = this.classname,
      line: Int = this.line,
      column: Int = this.column,
      range: Range? = this.range
   ): MacroExpansion {
      return new MacroExpansion(message, classname, line, column, range);
   }

   public override fun toString(): String {
      return "MacroExpansion(message=${this.message}, classname=${this.classname}, line=${this.line}, column=${this.column}, range=${this.range})";
   }

   public override fun hashCode(): Int {
      return (((this.message.hashCode() * 31 + this.classname.hashCode()) * 31 + Integer.hashCode(this.line)) * 31 + Integer.hashCode(this.column)) * 31
         + (if (this.range == null) 0 else this.range.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is MacroExpansion) {
         return false;
      } else {
         val var2: MacroExpansion = other as MacroExpansion;
         if (!(this.message == (other as MacroExpansion).message)) {
            return false;
         } else if (!(this.classname == var2.classname)) {
            return false;
         } else if (this.line != var2.line) {
            return false;
         } else if (this.column != var2.column) {
            return false;
         } else {
            return this.range == var2.range;
         }
      }
   }
}
