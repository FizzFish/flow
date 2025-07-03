package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.*
import kotlinx.collections.immutable.*

/**
 * 数组/列表抽象：`list` 保存已知索引元素；`unreferenced` 表示未知索引。
 */
data class ListSpace(
   val list: PersistentList<IHeapValues<IValue>> = persistentListOf(),
   val unreferenced: IHeapValues<IValue>? = null
) : IData<IValue> {

   init {
      require(unreferenced?.isNotEmpty() ?: true)
   }

   /* ---------------- IData ---------------- */

   override fun reference(res: MutableCollection<IValue>) {
      list.forEach { it.reference(res) }
      unreferenced?.reference(res)
   }

   override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
      if (this === that || that !is ListSpace) return

      list.zip(that.list).forEach { (l, r) -> l.diff(cmp, r) }
      if (unreferenced != null && that.unreferenced != null)
         unreferenced.diff(cmp, that.unreferenced)
   }

   override fun computeHash(): Int =
      31 * list.hashCode() + (unreferenced?.hashCode() ?: 0) + 1231

   override fun builder(): ListSpaceBuilder =
      ListSpaceBuilder(list.builder(), unreferenced?.builder())

   override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> =
      builder().apply { cloneAndReNewObjects(re) }.build()

   /* ---------------- 业务辅助 ---------------- */

   fun allElements(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> =
      hf.emptyBuilder().apply {
         list.forEach { add(it) }
         unreferenced?.let(::add)
      }.build()

   operator fun get(hf: AbstractHeapFactory<IValue>, index: Int?): IHeapValues<IValue>? =
      when {
         index == null || unreferenced != null -> allElements(hf)
         index in list.indices                 -> list[index]
         else -> null
      }
}
