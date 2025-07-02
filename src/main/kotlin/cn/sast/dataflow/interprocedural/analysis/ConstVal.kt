package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import soot.Type
import soot.jimple.Constant
import soot.jimple.NullConstant

/**
 * 常量值包装。
 */
class ConstVal private constructor(
   val v: Constant,
   override val type: Type = v.type
) : IValue {

   /** 懒缓存的 hashCode */
   private var _hashCode: Int? = null

   /* ---------- IValue ---------- */

   override fun toString(): String = "const_${type}_${v}"

   override fun typeIsConcrete(): Boolean = true
   override fun isNullConstant(): Boolean = v is NullConstant
   override fun getKind(): Kind = IValue.Kind.Normal

   /* ---------- equals/hash ---------- */

   override fun equals(other: Any?): Boolean =
      other is ConstVal && v == other.v && type == other.type

   override fun hashCode(): Int =
      _hashCode ?: (31 * v.hashCode() + type.hashCode()).also { _hashCode = it }

   /* ---------- default delegates ---------- */

   override fun isNormal()               = IValue.DefaultImpls.isNormal(this)
   override fun objectEqual(b: IValue)   = IValue.DefaultImpls.objectEqual(this, b)
   override fun clone(): IValue          = IValue.DefaultImpls.clone(this)
   override fun copy(type: Type): IValue = IValue.DefaultImpls.copy(this, type)

   /* ---------- 工厂 ---------- */
   companion object {
      fun v(c: Constant, ty: Type = c.type): ConstVal = ConstVal(c, ty)
   }
}
