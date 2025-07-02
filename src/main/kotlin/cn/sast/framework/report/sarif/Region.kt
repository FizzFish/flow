package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Region(startLine: Int, startColumn: Int = 0, endLine: Int = 0, endColumn: Int = 0) {
   public final val startLine: Int
   public final val startColumn: Int
   public final val endLine: Int
   public final val endColumn: Int

   init {
      this.startLine = startLine;
      this.startColumn = startColumn;
      this.endLine = endLine;
      this.endColumn = endColumn;
   }

   public constructor(region: com.feysh.corax.config.api.report.Region) : this(
         Math.max(region.startLine, 0), Math.max(region.startColumn, 0), Math.max(region.getEndLine(), 0), Math.max(region.getEndColumn() + 1, 0)
      )
   public operator fun component1(): Int {
      return this.startLine;
   }

   public operator fun component2(): Int {
      return this.startColumn;
   }

   public operator fun component3(): Int {
      return this.endLine;
   }

   public operator fun component4(): Int {
      return this.endColumn;
   }

   public fun copy(startLine: Int = this.startLine, startColumn: Int = this.startColumn, endLine: Int = this.endLine, endColumn: Int = this.endColumn): Region {
      return new Region(startLine, startColumn, endLine, endColumn);
   }

   public override fun toString(): String {
      return "Region(startLine=${this.startLine}, startColumn=${this.startColumn}, endLine=${this.endLine}, endColumn=${this.endColumn})";
   }

   public override fun hashCode(): Int {
      return ((Integer.hashCode(this.startLine) * 31 + Integer.hashCode(this.startColumn)) * 31 + Integer.hashCode(this.endLine)) * 31
         + Integer.hashCode(this.endColumn);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Region) {
         return false;
      } else {
         val var2: Region = other as Region;
         if (this.startLine != (other as Region).startLine) {
            return false;
         } else if (this.startColumn != var2.startColumn) {
            return false;
         } else if (this.endLine != var2.endLine) {
            return false;
         } else {
            return this.endColumn == var2.endColumn;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Region> {
         return Region.$serializer.INSTANCE as KSerializer<Region>;
      }
   }
}
