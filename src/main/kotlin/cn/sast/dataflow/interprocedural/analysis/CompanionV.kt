package cn.sast.dataflow.interprocedural.analysis

public abstract class CompanionV<V> {
   public final val value: Any
   private final var hashCode: Int?

   open fun CompanionV(value: V) {
      this.value = (V)value;
   }

   public abstract fun union(other: CompanionV<Any>): CompanionV<Any> {
   }

   public override fun toString(): String {
      return java.lang.String.valueOf(this.value);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else {
         return other is CompanionV && this.value == (other as CompanionV).value;
      }
   }

   public open fun computeHash(): Int {
      return (if (this.value != null) this.value.hashCode() else 0) + 23342879;
   }

   public override fun hashCode(): Int {
      var h: Int = this.hashCode;
      if (this.hashCode == null) {
         h = this.computeHash();
         this.hashCode = h;
      }

      return h;
   }

   public abstract fun copy(updateValue: Any): CompanionV<Any> {
   }
}
