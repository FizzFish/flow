package cn.sast.dataflow.interprocedural.analysis

import soot.Type

public class JFieldNameType(fieldName: String, type: Type) : JFieldType() {
   public final val fieldName: String
   public open val type: Type

   public open val name: String
      public open get() {
         return this.fieldName;
      }


   init {
      this.fieldName = fieldName;
      this.type = type;
   }

   public override fun toString(): String {
      return this.getName();
   }

   public override fun hashCode(): Int {
      return this.getName().hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      return this.getName() == (if ((other as? JFieldType) != null) (other as? JFieldType).getName() else null);
   }
}
