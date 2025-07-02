package cn.sast.framework.report.metadata

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
public data class Counter(missed: Int, covered: Int) {
   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final var missed: Int

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final var covered: Int

   init {
      this.missed = missed;
      this.covered = covered;
   }

   public operator fun component1(): Int {
      return this.missed;
   }

   public operator fun component2(): Int {
      return this.covered;
   }

   public fun copy(missed: Int = this.missed, covered: Int = this.covered): Counter {
      return new Counter(missed, covered);
   }

   public override fun toString(): String {
      return "Counter(missed=${this.missed}, covered=${this.covered})";
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.missed) * 31 + Integer.hashCode(this.covered);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Counter) {
         return false;
      } else {
         val var2: Counter = other as Counter;
         if (this.missed != (other as Counter).missed) {
            return false;
         } else {
            return this.covered == var2.covered;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Counter> {
         return Counter.$serializer.INSTANCE as KSerializer<Counter>;
      }
   }
}
