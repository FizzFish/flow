package cn.sast.api.report

import soot.SootMethod
import soot.Unit

public data class CoverInst(method: SootMethod, unit: Unit) : CoverSootCode(method, unit) {
   public open val method: SootMethod
   public open val unit: Unit

   init {
      this.method = method;
      this.unit = unit;
   }

   public operator fun component1(): SootMethod {
      return this.method;
   }

   public operator fun component2(): Unit {
      return this.unit;
   }

   public fun copy(method: SootMethod = this.method, unit: Unit = this.unit): CoverInst {
      return new CoverInst(method, unit);
   }

   public override fun toString(): String {
      return "CoverInst(method=${this.method}, unit=${this.unit})";
   }

   public override fun hashCode(): Int {
      return this.method.hashCode() * 31 + this.unit.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CoverInst) {
         return false;
      } else {
         val var2: CoverInst = other as CoverInst;
         if (!(this.method == (other as CoverInst).method)) {
            return false;
         } else {
            return this.unit == var2.unit;
         }
      }
   }
}
