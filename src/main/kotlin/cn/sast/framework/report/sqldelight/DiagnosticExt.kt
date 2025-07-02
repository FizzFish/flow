package cn.sast.framework.report.sqldelight

public data class DiagnosticExt(__diagnostic_id: Long, attr_name: String, attr_value: String) {
   public final val __diagnostic_id: Long
   public final val attr_name: String
   public final val attr_value: String

   init {
      this.__diagnostic_id = __diagnostic_id;
      this.attr_name = attr_name;
      this.attr_value = attr_value;
   }

   public operator fun component1(): Long {
      return this.__diagnostic_id;
   }

   public operator fun component2(): String {
      return this.attr_name;
   }

   public operator fun component3(): String {
      return this.attr_value;
   }

   public fun copy(__diagnostic_id: Long = this.__diagnostic_id, attr_name: String = this.attr_name, attr_value: String = this.attr_value): DiagnosticExt {
      return new DiagnosticExt(__diagnostic_id, attr_name, attr_value);
   }

   public override fun toString(): String {
      return "DiagnosticExt(__diagnostic_id=${this.__diagnostic_id}, attr_name=${this.attr_name}, attr_value=${this.attr_value})";
   }

   public override fun hashCode(): Int {
      return (java.lang.Long.hashCode(this.__diagnostic_id) * 31 + this.attr_name.hashCode()) * 31 + this.attr_value.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is DiagnosticExt) {
         return false;
      } else {
         val var2: DiagnosticExt = other as DiagnosticExt;
         if (this.__diagnostic_id != (other as DiagnosticExt).__diagnostic_id) {
            return false;
         } else if (!(this.attr_name == var2.attr_name)) {
            return false;
         } else {
            return this.attr_value == var2.attr_value;
         }
      }
   }
}
