package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentList
import soot.Type
import soot.Unit
import java.util.LinkedHashMap

/**
 * 在建模阶段产生的“汇摘要”值。
 *
 * @property type    静态类型
 * @property unit    产生该摘要的语句
 * @property special 附加标记（可为 null）
 */
open class SummaryValue private constructor(
   override val type: Type,
   val unit: Unit,
   val special: Any?
) : IValue, IFieldManager<SummaryValue> {

   private val fieldObjects =
      LinkedHashMap<PersistentList<JFieldType>, PhantomField<SummaryValue>>()

   /** 缓存 hashCode（lazy） */
   private var cachedHash: Int? = null

   /* ---------- 工厂 ---------- */

   companion object {
      fun v(ty: Type, unit: Unit, special: Any?): SummaryValue =
         SummaryValue(ty, unit, special)
   }

   /* ---------- IValue ---------- */

   override fun typeIsConcrete() = false
   override fun isNullConstant() = false
   override fun getKind() = IValue.Kind.Summary
   override fun copy(type: Type): IValue = SummaryValue(type, unit, special)

   override fun clone(): SummaryValue = SummaryValue(type, unit, special)

   /* ---------- IFieldManager ---------- */

   override fun getPhantomFieldMap() = fieldObjects

   /* ---------- hash / equals ---------- */

   private fun calcHash(): Int =
      if (!leastExpr) System.identityHashCode(this)
      else 31 * (31 * unit.hashCode() + (special?.hashCode() ?: 0)) + type.hashCode()

   override fun hashCode(): Int =
      cachedHash ?: calcHash().also { cachedHash = it }

   override fun equals(other: Any?): Boolean {
      if (!leastExpr) return this === other
      if (this === other) return true
      if (other !is SummaryValue) return false
      if (cachedHash != null && other.cachedHash != null &&
         cachedHash != other.cachedHash
      ) return false

      return unit == other.unit &&
              special == other.special &&
              type == other.type
   }

   /* ---------- toString ---------- */

   override fun toString(): String =
      "Summary_${type}_${unit}_$special(${hashCode()})"
}
