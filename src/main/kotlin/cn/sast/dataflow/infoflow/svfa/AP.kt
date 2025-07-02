package cn.sast.dataflow.infoflow.svfa

import soot.Local
import soot.SootField
import soot.Value
import soot.jimple.ArrayRef
import soot.jimple.FieldRef
import soot.jimple.InstanceFieldRef
import soot.jimple.LengthExpr
import soot.jimple.NewArrayExpr
import soot.jimple.StaticFieldRef
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.data.AccessPathFragment

public data class AP(value: Value, field: SootField? = null) {
   public final val value: Value
   public final val field: SootField?

   init {
      this.value = value;
      this.field = field;
   }

   public fun base(): AP {
      return if (this.field == null) this else new AP(this.value, null, 2, null);
   }

   public override fun toString(): String {
      return if (this.field != null) "${this.value}.${this.field.getName()}" else java.lang.String.valueOf(this.value);
   }

   public operator fun component1(): Value {
      return this.value;
   }

   public operator fun component2(): SootField? {
      return this.field;
   }

   public fun copy(value: Value = this.value, field: SootField? = this.field): AP {
      return new AP(value, field);
   }

   public override fun hashCode(): Int {
      return this.value.hashCode() * 31 + (if (this.field == null) 0 else this.field.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AP) {
         return false;
      } else {
         val var2: AP = other as AP;
         if (!(this.value == (other as AP).value)) {
            return false;
         } else {
            return this.field == var2.field;
         }
      }
   }

   public companion object {
      private final val staticValue: Value

      public operator fun get(value: Value): AP? {
         var base: Value = null;
         var field: SootField = null;
         if (value is StaticFieldRef) {
            base = AP.access$getStaticValue$cp();
            field = (value as StaticFieldRef).getField();
         } else if (value is Local) {
            base = value;
         } else if (value is FieldRef) {
            if (value is InstanceFieldRef) {
               base = (value as InstanceFieldRef).getBase();
               field = (value as InstanceFieldRef).getField();
            }
         } else if (value is ArrayRef) {
            val var10000: Value = (value as ArrayRef).getBase();
            base = (var10000 as Local) as Value;
         } else if (value is LengthExpr) {
            base = (value as LengthExpr).getOp();
         } else if (value is NewArrayExpr) {
            base = (value as NewArrayExpr).getSize();
         }

         return if (base != null) new AP(base, field) else null;
      }

      public operator fun get(value: Abstraction): AP {
         val var10002: Local = value.getAccessPath().getPlainValue();
         val var2: Value = var10002 as Value;
         val var10003: AccessPathFragment = value.getAccessPath().getFirstFragment();
         return new AP(var2, if (var10003 != null) var10003.getField() else null);
      }
   }
}
