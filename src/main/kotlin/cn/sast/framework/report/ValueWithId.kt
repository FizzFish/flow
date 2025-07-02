package cn.sast.framework.report

private data class ValueWithId<T>(id: Long, value: Any) {
   public final val id: Long
   public final val value: Any

   init {
      this.id = id;
      this.value = (T)value;
   }

   public operator fun component1(): Long {
      return this.id;
   }

   public operator fun component2(): Any {
      return this.value;
   }

   public fun copy(id: Long = this.id, value: Any = this.value): ValueWithId<Any> {
      return new ValueWithId<>(id, (T)value);
   }

   public override fun toString(): String {
      return "ValueWithId(id=${this.id}, value=${this.value})";
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.id) * 31 + (if (this.value == null) 0 else this.value.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ValueWithId) {
         return false;
      } else {
         val var2: ValueWithId = other as ValueWithId;
         if (this.id != (other as ValueWithId).id) {
            return false;
         } else {
            return this.value == var2.value;
         }
      }
   }
}
