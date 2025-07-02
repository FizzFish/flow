package cn.sast.dataflow.interprocedural.analysis

import soot.SootField
import soot.Type

public class JSootFieldType(sootField: SootField) : JFieldType() {
   public final val sootField: SootField

   public open val type: Type
      public open get() {
         val var10000: Type = this.sootField.getType();
         return var10000;
      }


   public open val name: String
      public open get() {
         val var10000: java.lang.String = this.sootField.getName();
         return var10000;
      }


   init {
      this.sootField = sootField;
   }

   public override fun hashCode(): Int {
      return this.getName().hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      return this.getName() == (if ((other as? JFieldType) != null) (other as? JFieldType).getName() else null);
   }

   public override fun toString(): String {
      return this.getName();
   }
}
