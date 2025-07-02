package cn.sast.dataflow.infoflow.svfa

import java.util.Objects
import soot.Unit
import soot.Value

internal class VFNode(ap: Value, stmt: Unit) {
   public final val ap: Value
   public final val stmt: Unit

   init {
      this.ap = ap;
      this.stmt = stmt;
   }

   public override fun hashCode(): Int {
      return Objects.hash(this.ap, this.stmt);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is VFNode) {
         return false;
      } else {
         return this.ap == (other as VFNode).ap && this.stmt == (other as VFNode).stmt;
      }
   }

   public override fun toString(): String {
      return "${this.ap} ${this.stmt}";
   }
}
