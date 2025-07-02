package cn.sast.framework.report.sqldelight

public data class SchemaInfo(key: String, value_: String) {
   public final val key: String
   public final val value_: String

   init {
      this.key = key;
      this.value_ = value_;
   }

   public operator fun component1(): String {
      return this.key;
   }

   public operator fun component2(): String {
      return this.value_;
   }

   public fun copy(key: String = this.key, value_: String = this.value_): SchemaInfo {
      return new SchemaInfo(key, value_);
   }

   public override fun toString(): String {
      return "SchemaInfo(key=${this.key}, value_=${this.value_})";
   }

   public override fun hashCode(): Int {
      return this.key.hashCode() * 31 + this.value_.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SchemaInfo) {
         return false;
      } else {
         val var2: SchemaInfo = other as SchemaInfo;
         if (!(this.key == (other as SchemaInfo).key)) {
            return false;
         } else {
            return this.value_ == var2.value_;
         }
      }
   }
}
