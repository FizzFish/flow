package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import soot.ArrayType
import soot.RefType
import soot.Type
import soot.Unit
import soot.jimple.AnyNewExpr

public open class AnyNewValue(u: Unit, method: String, newExpr: AnyNewExpr) : IValue {
   public final val u: Unit
   public final val method: String
   public final val newExpr: AnyNewExpr
   public open val type: Type

   public final var hashCode: Int?
      internal set

   init {
      this.u = u;
      this.method = method;
      this.newExpr = newExpr;
      if (this.newExpr.getType() !is RefType && this.newExpr.getType() !is ArrayType) {
         throw new IllegalStateException(java.lang.String.valueOf(this.newExpr).toString());
      } else {
         val var10001: Type = this.newExpr.getType();
         this.type = var10001;
      }
   }

   public override fun clone(): IValue {
      return new AnyNewValue(this.u, this.method, this.newExpr);
   }

   public override fun toString(): String {
      return "${this.newExpr} *${this.u.getJavaSourceStartLineNumber()} (${this.hashCode()})";
   }

   public override fun typeIsConcrete(): Boolean {
      return true;
   }

   public override fun isNullConstant(): Boolean {
      return false;
   }

   public override fun getKind(): Kind {
      return IValue.Kind.Normal;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!FactValuesKt.getLeastExpr()) {
         return this === other;
      } else if (this === other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (other !is AnyNewValue) {
         return false;
      } else if (this.hashCode != null && (other as AnyNewValue).hashCode != null && !(this.hashCode == (other as AnyNewValue).hashCode)) {
         return false;
      } else if (!(this.method == (other as AnyNewValue).method)) {
         return false;
      } else if (!(this.newExpr == (other as AnyNewValue).newExpr)) {
         return false;
      } else {
         return this.getType() == (other as AnyNewValue).getType();
      }
   }

   public fun hash(): Int {
      return if (!FactValuesKt.getLeastExpr())
         System.identityHashCode(this)
         else
         31 * (31 * (31 * 1 + this.method.hashCode()) + this.newExpr.hashCode()) + this.getType().hashCode();
   }

   public override fun hashCode(): Int {
      var h: Int = this.hashCode;
      if (this.hashCode == null) {
         h = this.hash();
         this.hashCode = h;
      }

      return h;
   }

   override fun isNormal(): Boolean {
      return IValue.DefaultImpls.isNormal(this);
   }

   override fun objectEqual(b: IValue): java.lang.Boolean? {
      return IValue.DefaultImpls.objectEqual(this, b);
   }

   override fun copy(type: Type): IValue {
      return IValue.DefaultImpls.copy(this, type);
   }
}
