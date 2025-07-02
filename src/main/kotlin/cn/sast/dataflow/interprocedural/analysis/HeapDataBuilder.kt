package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import mu.KotlinLogging

/**
 * 可变 KV-堆对象的通用构建器。
 */
abstract class HeapDataBuilder<K, V>(
   protected val map: PersistentMap<K, IHeapValues<V>>.Builder,
   protected var unreferenced: IHeapValuesBuilder<V>? = null
) : IHeapKVData.Builder<K, V> {

   /* ---------- 读取 ---------- */

   protected fun getValue(key: K): IHeapValues<V>? = map[key]

   /* ---------- 写入 ---------- */

   override fun set(
      hf: IHeapValuesFactory<Any>,
      env: HeapValuesEnv,
      key: K?,
      update: IHeapValues<Any>?,
      append: Boolean
   ) {
      if (update == null || update.isEmpty()) {
         logger.debug { "ignore update is $update" }
         return
      }

      if (key == null) {
         unreferenced = (unreferenced ?: hf.emptyBuilder()).apply { add(update) }
         return
      }

      val exist = getValue(key)
      map[key] = if (append && exist != null) update.plus(exist) else update as IHeapValues<V>
   }

   /* ---------- 合并 ---------- */

   override fun union(hf: AbstractHeapFactory<Any>, that: IData<Any>) {
      require(that is HeapKVData<*, *>) { "Failed requirement." }

      // unreferenced
      that.unreferenced?.let { v ->
         unreferenced = (unreferenced ?: v.builder()).apply { add(v) }
      }

      // map
      if (map !== that.map) {
         that.map.forEach { (k, v) ->
            val exist = map[k]
            map[k] = if (exist == null) v as IHeapValues<V> else (v as IHeapValues<V>).plus(exist)
         }
      }
   }

   /* ---------- toString ---------- */
   override fun toString(): String = build().toString()

   companion object {
      private val logger = KotlinLogging.logger {}
   }
}
