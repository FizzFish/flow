package cn.sast.dataflow.analysis.constant

import java.util.Objects

public open class CPValue {
   private final var value: Int


   private constructor(`val`: Int)  {
      this.value = `val`;
   }

   public fun value(): Int {
      return this.value;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other == null || !(this.getClass() == other.getClass())) {
         return false;
      } else if (this === undef && other === nac) {
         return false;
      } else if (this === nac && other === undef) {
         return false;
      } else {
         return this.value == (other as CPValue).value;
      }
   }

   public override fun hashCode(): Int {
      return Objects.hash(this.value);
   }

   public override fun toString(): String {
      if (this === nac) {
         return "NAC";
      } else {
         return if (this === undef) "UNDEF" else java.lang.String.valueOf(this.value);
      }
   }

   public companion object {
      public final val nac: CPValue
      public final val undef: CPValue

      public fun makeConstant(value: Int): CPValue {
         return new CPValue(value, null);
      }

      public fun makeConstant(`val`: Boolean): CPValue {
         return if (`val`) this.makeConstant(1) else this.makeConstant(0);
      }

      public fun meetValue(value1: CPValue, value2: CPValue): CPValue {
         if (value1 === this.getUndef()) {
            return value2;
         } else if (value2 === this.getUndef()) {
            return value1;
         } else if (value1 != this.getNac() && value2 != this.getNac()) {
            return if (value1.value() == value2.value()) this.makeConstant(value1.value()) else this.getNac();
         } else {
            return this.getNac();
         }
      }
   }
}
