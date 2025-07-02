package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

/**
 * 不可变 KV-堆对象基类。
 */
abstract class HeapKVData<K, V>(
   protected val map: PersistentMap<K, IHeapValues<V>>,
   protected val unreferenced: IHeapValues<V>? = null
) : IHeapKVData<K, V> {

   private var _hashCode: Int? = null

   /* ---------- 引用收集 ---------- */

   override fun reference(res: MutableCollection<Any>) {
      map.values.forEach { it.reference(res) }
      unreferenced?.reference(res)
   }

   /* ---------- Diff ---------- */

   override fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>) {
      if (that !is HeapKVData<*, *>) return

      map.keys.intersect(that.map.keys).forEach { k ->
         map[k]!!.diff(cmp, that.map[k]!!)
      }
      if (unreferenced != null && that.unreferenced != null)
         unreferenced.diff(cmp, that.unreferenced)
   }

   /* ---------- equals / hash ---------- */

   override fun equals(other: Any?): Boolean =
      other is HeapKVData<*, *> &&
              hashCode() == other.hashCode() &&
              map == other.map &&
              unreferenced == other.unreferenced

   override fun computeHash(): Int =
      31 * map.hashCode() + (unreferenced?.hashCode() ?: 0)

   override fun hashCode(): Int =
      _hashCode ?: computeHash().also { _hashCode = it }

   /* ---------- 读取 ---------- */

   protected abstract fun isValidKey(key: K?): Boolean?

   protected open fun fromNullKey(hf: IHeapValuesFactory<Any>): IHeapValues<Any> {
      val b = hf.emptyBuilder()
      map.values.forEach { b.add(it as IHeapValues<Any>) }
      unreferenced?.let { b.add(it as IHeapValues<Any>) }
      return b.build()
   }

   override fun get(hf: IHeapValuesFactory<V>, key: K?): Iterable<CompanionV<out Any?>>? {
      return if (key != null) {
         val exist = map[key] as IHeapValues<Any>?
         when {
            exist != null && unreferenced != null -> exist.plus(unreferenced)
            exist != null -> exist
            else -> unreferenced
         }
      } else fromNullKey(hf)
   }

   /* ---------- pretty print ---------- */

   protected open fun ppKey(key: K): String = key.toString()
   protected open fun ppValue(v: IHeapValues<Any>): String = v.toString()

   override fun toString(): String = buildString {
      append(getName()).append(' ')
      if (map.isEmpty()) append("unreferenced: $unreferenced")
      else map.forEach { (k, v) ->
         val merged = if (unreferenced != null) v.plus(unreferenced) else v
         append(ppKey(k)).append("->").append(ppValue(merged as IHeapValues<Any>)).append(" ; ")
      }
   }
}
