package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.*
import kotlinx.collections.immutable.PersistentList

/**
 * 可变构建器：支持合并、增删、对象重映射。
 */
data class ListSpaceBuilder(
   val list: PersistentList.Builder<IHeapValues<IValue>>,
   var unreferenced: IHeapValues.Builder<IValue>? = null
) : IData.Builder<IValue> {

   /* ---------- union ---------- */

   override fun union(hf: AbstractHeapFactory<IValue>, that: IData<IValue>) {
      require(that is ListSpace)
      when {
         unreferenced == null && that.unreferenced == null && list.size == that.list.size -> {
            that.list.forEachIndexed { i, v -> list[i] = list[i] + v }
         }
         else -> { // 统一转入 unreferenced
            val merged = build().also { list.clear() }   // self → listSpace
            unreferenced = (unreferenced ?: hf.emptyBuilder())
               .apply { add(merged.allElements(hf) + that.allElements(hf)) }
         }
      }
   }

   /* ---------- clone & renew ---------- */

   override fun cloneAndReNewObjects(re: IReNew<IValue>) {
      list.replaceAllIndexed { idx, hv ->
         hv.cloneAndReNewObjects(re.context(ReferenceContext.KVPosition(idx)))
      }
      unreferenced?.cloneAndReNewObjects(re.context(ReferenceContext.KVUnreferenced))
   }

   /* ---------- build ---------- */

   override fun build(): ListSpace =
      ListSpace(list.build(), unreferenced?.takeIf { it.isNotEmpty() }?.build())

   /* ---------- helpers ---------- */

   fun add(value: IHeapValues<IValue>) =
      (unreferenced ?: list).add(value)

   fun remove(hf: AbstractHeapFactory<IValue>, index: Int?): IHeapValues<IValue> =
      when {
         index == null -> {
            val hv = build().allElements(hf)
            list.clear(); unreferenced = null
            hv
         }
         unreferenced == null && index in list.indices -> list.removeAt(index)
         else -> build().allElements(hf)
      }
}
