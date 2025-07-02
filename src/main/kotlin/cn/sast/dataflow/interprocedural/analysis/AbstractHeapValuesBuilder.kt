package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.toPersistentMap

/**
 * 可变构建器，实现 **增量合并 / 克隆重建**。
 */
sealed class AbstractHeapValuesBuilder<V>(
   protected val orig: AbstractHeapValues<V>,
   protected val map: PersistentMap<V, CompanionV<V>>.Builder =
      orig.map.builder()
) : IHeapValues.Builder<V> {

   override fun isEmpty()    = map.isEmpty()
   override fun isNotEmpty() = map.isNotEmpty()

   /* ---------- 添加 ---------- */

   override fun add(elements: IHeapValues<V>): IHeapValues.Builder<V> {
      if (elements is AbstractHeapValues) elements.forEach(::add)
      return this
   }

   override fun add(element: CompanionV<V>): IHeapValues.Builder<V> {
      val k = element.value
      val exist = map[k]
      map[k] = exist?.union(element) ?: element
      return this
   }

   /* ---------- 克隆并替换 ---------- */

   override fun cloneAndReNewObjects(re: IReNew<V>) {
      val snapshot = map.toPersistentMap()
      snapshot.forEach { (k, v) ->
         val newK = re.checkNeedReplace(k) ?: k
         val newV = re.context(ReferenceContext.ObjectValues(k)).checkNeedReplace(v) ?: v
         if (newK != k || newV != v) {
            map.remove(k)
            map[newK] = if (newV.value === newV) newV else newV.copy(newV.value)
         }
      }
   }

   /* ---------- String ---------- */

   override fun toString(): String =
      cn.sast.dataflow.util.Printer.nodes2String(map.values)
}
