package cn.sast.dataflow.interprocedural.analysis

public data class SymbolicFailure<V>(symbolic: Any, concrete: Throwable?, explicit: Boolean, inNestedMethod: Boolean) : MethodResult() {
   public final val symbolic: Any
   public final val concrete: Throwable?
   public final val explicit: Boolean
   public final val inNestedMethod: Boolean

   init {
      this.symbolic = (V)symbolic;
      this.concrete = concrete;
      this.explicit = explicit;
      this.inNestedMethod = inNestedMethod;
   }

   public operator fun component1(): Any {
      return this.symbolic;
   }

   public operator fun component2(): Throwable? {
      return this.concrete;
   }

   public operator fun component3(): Boolean {
      return this.explicit;
   }

   public operator fun component4(): Boolean {
      return this.inNestedMethod;
   }

   public fun copy(
      symbolic: Any = this.symbolic,
      concrete: Throwable? = this.concrete,
      explicit: Boolean = this.explicit,
      inNestedMethod: Boolean = this.inNestedMethod
   ): SymbolicFailure<Any> {
      return new SymbolicFailure<>((V)symbolic, concrete, explicit, inNestedMethod);
   }

   public override fun toString(): String {
      return "SymbolicFailure(symbolic=${this.symbolic}, concrete=${this.concrete}, explicit=${this.explicit}, inNestedMethod=${this.inNestedMethod})";
   }

   public override fun hashCode(): Int {
      return (
               ((if (this.symbolic == null) 0 else this.symbolic.hashCode()) * 31 + (if (this.concrete == null) 0 else this.concrete.hashCode())) * 31
                  + java.lang.Boolean.hashCode(this.explicit)
            )
            * 31
         + java.lang.Boolean.hashCode(this.inNestedMethod);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SymbolicFailure) {
         return false;
      } else {
         val var2: SymbolicFailure = other as SymbolicFailure;
         if (!(this.symbolic == (other as SymbolicFailure).symbolic)) {
            return false;
         } else if (!(this.concrete == var2.concrete)) {
            return false;
         } else if (this.explicit != var2.explicit) {
            return false;
         } else {
            return this.inNestedMethod == var2.inNestedMethod;
         }
      }
   }
}
