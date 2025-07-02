package cn.sast.framework.validator

internal data class RowUnknownType(type: String) : RowType() {
   public open val type: String

   init {
      this.type = type;
   }

   public override fun toString(): String {
      return this.getType();
   }

   public operator fun component1(): String {
      return this.type;
   }

   public fun copy(type: String = this.type): RowUnknownType {
      return new RowUnknownType(type);
   }

   public override fun hashCode(): Int {
      return this.type.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is RowUnknownType) {
         return false;
      } else {
         return this.type == (other as RowUnknownType).type;
      }
   }
}
