package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.*
import soot.jimple.Constant
import soot.jimple.IntConstant

/**
 * 基础不可变 **堆值集合** 实现。
 *
 * @property map  value → companion  映射
 */
sealed class AbstractHeapValues<V>(
   protected open val map: PersistentMap<V, CompanionV<V>> = persistentHashMapOf()
) : IHeapValues<V> {

   /* 缓存哈希，避免频繁重算 */
   private var _hashCode: Int? = null

   /* ---------- 只读特性 ---------- */

   val size: Int      get() = map.size
   val values: ImmutableSet<V> get() = map.keys.toImmutableSet()
   val valuesCompanion: ImmutableSet<CompanionV<V>> get() =
      map.values.toImmutableSet()

   val single: CompanionV<V>
      get() = when {
         !isSingle() -> error("not single")
         else        -> iterator().next()
      }

   /* ---------- util ---------- */

   private fun computeHash(): Int = map.hashCode()

   /* ---------- IDiffAble ---------- */

   override fun diff(cmp: IDiff<V>, that: IDiffAble<out V>) {
      if (that is AbstractHeapValues<*>) {
         map.keys
            .intersect(that.map.keys)
            .forEach { k ->
               cmp.diff(map[k]!!, that.map[k]!!)
            }
      }
   }

   override fun reference(res: MutableCollection<V>) = res.addAll(map.keys)

   /* ---------- 状态判断 ---------- */

   override fun isEmpty()      = map.isEmpty()
   override fun isNotEmpty()   = map.isNotEmpty()
   override fun isSingle()     = map.size == 1

   /* ---------- 集合运算 ---------- */

   override operator fun plus(rhs: IHeapValues<V>): IHeapValues<V> =
      when {
         rhs.isEmpty() -> this
         this.isEmpty() -> rhs
         else -> builder().apply { add(rhs) }.build()
      }

   override operator fun plus(rhs: CompanionV<V>): IHeapValues<V> =
      builder().apply { add(rhs) }.build()

   /* ----------  map / flatMap  ---------- */

   override fun map(
      c: IHeapValues.Builder<V>,
      transform: (CompanionV<V>) -> CompanionV<V>
   ) = forEach { c.add(transform(it)) }

   override fun flatMap(
      c: IHeapValues.Builder<V>,
      transform: (CompanionV<V>) -> Collection<CompanionV<V>>
   ) = forEach { transform(it).forEach(c::add) }

   /* ---------- int 常量辅助 ---------- */

   override fun getAllIntValue(must: Boolean): MutableSet<Int>? {
      val ints = map.keys.mapNotNull { v ->
         (v as? ConstVal)?.v as? IntConstant
      }.map { it.value }.toMutableSet()
      return when {
         must && ints.size != map.size -> null
         else -> ints
      }
   }

   /* ---------- Iterator ---------- */

   override fun iterator(): Iterator<CompanionV<V>> = map.values.iterator()

   /* ---------- 克隆并重定向 ---------- */

   override fun cloneAndReNewObjects(re: IReNew<V>): IHeapValues<V> =
      builder().apply { cloneAndReNewObjects(re) }.build()

   /* ---------- Object 基础 ---------- */

   override fun hashCode(): Int =
      _hashCode ?: computeHash().also { _hashCode = it }

   override fun equals(other: Any?): Boolean =
      other is AbstractHeapValues<*> && other.map == map

   override fun toString(): String =
      cn.sast.dataflow.util.Printer.nodes2String(map.values)

   /* ---------- 默认实现委托 ---------- */
   override fun getMaxInt(must: Boolean): Int? =
      IHeapValues.DefaultImpls.getMaxInt(this, must)
}
