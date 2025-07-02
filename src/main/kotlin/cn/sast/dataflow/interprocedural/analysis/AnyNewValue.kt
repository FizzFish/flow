package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import soot.*
import soot.jimple.AnyNewExpr

/**
 * 表示 `new A()` / `new int[] {}` 在语义堆中的抽象对象。
 *
 * @property u        发生 `new` 的具体单元
 * @property method   方法签名（字符串）—— 用于跨方法比较
 * @property newExpr  对应的 Jimple `AnyNewExpr`
 */
open class AnyNewValue(
   val u: Unit,
   val method: String,
   val newExpr: AnyNewExpr
) : IValue {

   override val type: Type = newExpr.type.also {
      require(it is RefType || it is ArrayType) { newExpr.toString() }
   }

   /** 懒缓存的 hashCode（与 equals 中逻辑保持一致） */
   private var _hashCode: Int? = null

   /* ---------- IValue ---------- */

   override fun clone(): IValue = AnyNewValue(u, method, newExpr)

   override fun toString(): String =
      "$newExpr *${u.javaSourceStartLineNumber} (${hashCode()})"

   override fun typeIsConcrete(): Boolean = true
   override fun isNullConstant(): Boolean = false
   override fun getKind(): Kind = IValue.Kind.Normal

   /* ---------- equals/hash ---------- */

   override fun equals(other: Any?): Boolean {
      // “最小表达式” 优化：直接用引用等价
      if (!FactValuesKt.getLeastExpr()) return this === other
      if (this === other) return true
      if (other !is AnyNewValue) return false

      // 若双方均已缓存 hashCode 且不同，可快速返回
      if (_hashCode != null && other._hashCode != null &&
         _hashCode != other._hashCode
      ) return false

      return method == other.method &&
              newExpr == other.newExpr &&
              type == other.type
   }

   private fun calcHash(): Int =
      if (!FactValuesKt.getLeastExpr())
         System.identityHashCode(this)
      else
         31 * (31 * method.hashCode() + newExpr.hashCode()) + type.hashCode()

   override fun hashCode(): Int =
      _hashCode ?: calcHash().also { _hashCode = it }

   /* ---------- default-method delegates ---------- */

   override fun isNormal()              = IValue.DefaultImpls.isNormal(this)
   override fun objectEqual(b: IValue)  = IValue.DefaultImpls.objectEqual(this, b)
   override fun copy(type: Type): IValue = IValue.DefaultImpls.copy(this, type)
}
