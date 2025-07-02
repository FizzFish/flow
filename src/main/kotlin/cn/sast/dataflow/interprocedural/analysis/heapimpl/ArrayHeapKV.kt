package cn.sast.dataflow.interprocedural.analysis.heapimpl

import cn.sast.dataflow.interprocedural.analysis.*
import kotlinx.collections.immutable.PersistentMap
import soot.ArrayType

/**
 * 不可变数组堆对象。
 */
open class ArrayHeapKV<V>(
   element: PersistentMap<Int, IHeapValues<V>>,
   unreferenced: IHeapValues<V>?,
   private val allSizeVal: IHeapValues<V>,
   final override val type: ArrayType,
   private val size: Int?,
   private val initializedValue: CompanionV<V>? = null
) : HeapKVData<Int, V>(element, unreferenced), IArrayHeapKV<Int, V> {

   init {
      require(allSizeVal.isNotEmpty()) { "array length value set is empty" }
   }

   /* ---------- IArrayHeapKV ---------- */

   override fun getArrayLength(): IHeapValues<Any> = allSizeVal as IHeapValues<Any>

   override fun getName(): String =
      "${type.elementType}[${size ?: "?"}]"

   fun isValidKey(key: Int?): Boolean? = isValidKey(key, size)

   override fun get(
      hf: IHeapValuesFactory<Any>,
      key: Int?
   ): IHeapValues<Any>? =
      if (isValidKey(key) == false) null else super.get(hf, key)

   /** 单元素读取。若已满 & 有初值，返回初值 */
   fun getValue(
      hf: IHeapValuesFactory<Any>,
      key: Int
   ): IHeapValues<Any>? =
      super.getValue(hf, key) ?: run {
         if (size != null && size == map.size && initializedValue != null) {
            hf.single(initializedValue as CompanionV<Any>)
         } else null
      }

   override fun getArray(hf: IHeapValuesFactory<Any>): Array<Any>? = null
   override fun getByteArray(hf: IHeapValuesFactory<Any>): ByteArray? = null

   /* ---------- Diff ---------- */

   override fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>) {
      if (that is ArrayHeapKV<*>) {
         allSizeVal.diff(cmp, that.allSizeVal as IHeapValues<Any>)
         if (initializedValue != null && that.initializedValue != null)
            cmp.diff(initializedValue, that.initializedValue as CompanionV<Any>)
      }
      super.diff(cmp, that)
   }

   /* ---------- hash/equals ---------- */

   override fun computeHash(): Int = 31 * super.computeHash() + allSizeVal.hashCode()

   override fun equals(other: Any?): Boolean =
      super.equals(other) && other is ArrayHeapKV<*> && other.allSizeVal == allSizeVal

   override fun hashCode(): Int = super.hashCode()
}
