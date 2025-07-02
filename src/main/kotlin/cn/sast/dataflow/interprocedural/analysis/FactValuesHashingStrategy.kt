package cn.sast.dataflow.interprocedural.analysis

import gnu.trove.strategy.HashingStrategy
import java.util.Objects

/**
 * 针对 [IValue] 的 *自定义哈希/相等* 规则，供 Trove 集合使用。
 */
class FactValuesHashingStrategy : HashingStrategy<IValue> {

   /* ---------- Hash ---------- */

   override fun computeHashCode(v: IValue?): Int {
      requireNotNull(v)
      return when (v) {
         is AnyNewValue     -> Objects.hash(v.newExpr)
         is ConstVal        -> Objects.hash(v.v)
         is SummaryValue    -> Objects.hash(v.type, v.unit, v.special)
         is EntryParam      -> Objects.hash(v.method, v.paramIndex)
         is GlobalStaticObject -> v.hashCode()
         is PhantomField<*> -> Objects.hash(computeHashCode(v.base), v.accessPath)
         else -> error("Unsupported IValue type: ${v.javaClass}: $v")
      }
   }

   /* ---------- Equals ---------- */

   override fun equals(a: IValue?, b: IValue?): Boolean {
      if (a === b) return true
      if (a == null || b == null) return false
      if (computeHashCode(a) != computeHashCode(b)) return false

      return when {
         a is AnyNewValue && b is AnyNewValue ->
            a.newExpr == b.newExpr

         a is ConstVal && b is ConstVal ->
            a.v == b.v

         a is SummaryValue && b is SummaryValue ->
            a.type == b.type && a.unit == b.unit && a.special == b.special

         a is EntryParam && b is EntryParam ->
            a.method == b.method && a.paramIndex == b.paramIndex

         a is GlobalStaticObject && b is GlobalStaticObject ->
            true

         a is PhantomField<*> && b is PhantomField<*> ->
            equals(a.base, b.base) && a.accessPath == b.accessPath

         else -> error(
            "Type mismatch when comparing IValue:\na: ${a.javaClass}: $a\nb: ${b.javaClass}: $b"
         )
      }
   }

   companion object {
      @JvmField
      val INSTANCE: FactValuesHashingStrategy = FactValuesHashingStrategy()
   }
}
