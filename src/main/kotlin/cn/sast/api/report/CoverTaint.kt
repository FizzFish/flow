package cn.sast.api.report

import soot.SootMethod
import soot.Unit

public data class CoverTaint(method: SootMethod, unit: Unit, value: Any) : CoverSootCode(method, unit) {
   public open val method: SootMethod
   public open val unit: Unit
   public final val value: Any

   init {
      this.method = method;
      this.unit = unit;
      this.value = value;
   }

   public operator fun component1(): SootMethod {
      return this.method;
   }

   public operator fun component2(): Unit {
      return this.unit;
   }

   public operator fun component3(): Any {
      return this.value;
   }

   public fun copy(method: SootMethod = this.method, unit: Unit = this.unit, value: Any = this.value): CoverTaint {
      return new CoverTaint(method, unit, value);
   }

   public override fun toString(): String {
      return "CoverTaint(method=${this.method}, unit=${this.unit}, value=${this.value})";
   }

   public override fun hashCode(): Int {
      return (this.method.hashCode() * 31 + this.unit.hashCode()) * 31 + this.value.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CoverTaint) {
         return false;
      } else {
         val var2: CoverTaint = other as CoverTaint;
         if (!(this.method == (other as CoverTaint).method)) {
            return false;
         } else if (!(this.unit == var2.unit)) {
            return false;
         } else {
            return this.value == var2.value;
         }
      }
   }
}
