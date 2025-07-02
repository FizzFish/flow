package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import soot.Type
import soot.jimple.Constant
import soot.jimple.NullConstant

public class ConstVal private constructor(v: Constant, type: Type = v.getType()) : IValue {
   public final val v: Constant
   public open val type: Type

   public final var hash: Int?
      internal set

   init {
      this.v = v;
      this.type = type;
   }

   public override fun toString(): String {
      return "const_${this.getType()}_${this.v}";
   }

   public override fun typeIsConcrete(): Boolean {
      return true;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (other !is ConstVal) {
         return false;
      } else if (!(this.v == (other as ConstVal).v)) {
         return false;
      } else {
         return this.getType() == (other as ConstVal).getType();
      }
   }

   public override fun hashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31 * Integer.valueOf(this.v.hashCode()) + this.getType().hashCode();
         this.hash = result;
      }

      return result;
   }

   public override fun isNullConstant(): Boolean {
      return this.v is NullConstant;
   }

   public override fun getKind(): Kind {
      return IValue.Kind.Normal;
   }

   override fun isNormal(): Boolean {
      return IValue.DefaultImpls.isNormal(this);
   }

   override fun objectEqual(b: IValue): java.lang.Boolean? {
      return IValue.DefaultImpls.objectEqual(this, b);
   }

   override fun clone(): IValue {
      return IValue.DefaultImpls.clone(this);
   }

   override fun copy(type: Type): IValue {
      return IValue.DefaultImpls.copy(this, type);
   }

   public companion object {
      public fun v(v: Constant, ty: Type): ConstVal {
         return new ConstVal(v, ty, null);
      }
   }
}
