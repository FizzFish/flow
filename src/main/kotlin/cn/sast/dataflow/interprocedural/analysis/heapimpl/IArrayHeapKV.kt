package cn.sast.dataflow.interprocedural.analysis.heapimpl

import cn.sast.dataflow.interprocedural.analysis.*
import soot.ArrayType

/**
 * K 为索引(Int)，V 为元素类型；子类为真正的数组 KV 映射。
 */
interface IArrayHeapKV<K, V> : IHeapKVData<K, V> {

   val type: ArrayType

   /** 数组长度（可能是区间 / 多值集合） */
   fun getArrayLength(): IHeapValues<Any>

   /** 整体获取元素的并集 */
   fun getElement(hf: AbstractHeapFactory<Any>): IHeapValues<Any>

   /** 尝试解析为实际数组（静态分析很少能做到，默认 null） */
   fun getArray(hf: IHeapValuesFactory<Any>): Array<Any>? = null

   /** 解析为 byte[]，失败返回 null */
   fun getByteArray(hf: IHeapValuesFactory<Any>): ByteArray? = null
}
