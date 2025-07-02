package cn.sast.dataflow.interprocedural.analysis.heapimpl

import cn.sast.dataflow.interprocedural.analysis.*
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import soot.ArrayType

/**
 * 可变数组堆对象，用于构建 / 更新 ArrayHeapKV。
 *
 * @param map          数组元素映射  index → values
 * @param unreferenced 未知索引的元素集合
 * @param type         数组类型
 * @param allSize      长度值集合（不能为空）
 * @param size         具体长度（已知时）
 * @param initialized  全初始化值（若数组元素全部相同）
 */
open class ArrayHeapBuilder<V>(
   map: PersistentMap<Int, IHeapValues<V>>,
   unreferenced: IHeapValues<V>?,
   val type: ArrayType,
   val allSize: IHeapValues<V>,
   val size: Int?,
   var initializedValue: CompanionV<V>?
) : HeapDataBuilder<Int, V>(map.toBuilder(), unreferenced) {

   init {
      require(allSize.isNotEmpty()) { "allSize must not be empty" }
   }

   /** 单元素读取，若未写入且满足初始化条件则返回初始化值 */
   fun getValue(
      hf: IHeapValuesFactory<V>,
      key: Int
   ): IHeapValues<V>? =
      super.getValue(hf, key) ?: run {
         if (size != null && size == map.size && initializedValue != null) {
            hf.single(initializedValue!!)
         } else null
      }

   /** 写元素：append=true 表示集合并，否则覆盖 */
   fun set(
      hf: IHeapValuesFactory<V>,
      env: HeapValuesEnv,
      key: Int?,
      update: IHeapValues<V>?,
      append: Boolean
   ) {
      if (isValidKey(key, size) != false) {
         super.set(hf, env, key, update, append)
      }
   }

   /* ---------- 重写：克隆 + 对象重映射 ---------- */

   override fun cloneAndReNewObjects(re: IReNew<V>) {
      super.cloneAndReNewObjects(re.context(ReferenceContext.ArrayElement))
      allSizeBuilder.cloneAndReNewObjects(re.context(ReferenceContext.ArraySize))

      initializedValue?.let { initV ->
         val newV = re.context(ReferenceContext.ArrayInitialized)
            .checkNeedReplace(initV) ?: initV
         initializedValue = if (newV.value === newV) newV else newV.copy(newV.value)
      }
   }

   /* ---------- 其它辅助 ---------- */

   fun clearAllIndex() {
      map.clear()
   }
}
