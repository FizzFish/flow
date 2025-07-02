package cn.sast.dataflow.interprocedural.analysis

public data class SymbolicSuccess<V>(value: Any) : MethodResult() {
   public final val value: Any

   init {
      this.value = (V)value;
   }

   public operator fun component1(): Any {
      return this.value;
   }

   public fun copy(value: Any = this.value): SymbolicSuccess<Any> {
      return new SymbolicSuccess<>((V)value);
   }

   public override fun toString(): String {
      return "SymbolicSuccess(value=${this.value})";
   }

   public override fun hashCode(): Int {
      return if (this.value == null) 0 else this.value.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SymbolicSuccess) {
         return false;
      } else {
         return this.value == (other as SymbolicSuccess).value;
      }
   }
}
