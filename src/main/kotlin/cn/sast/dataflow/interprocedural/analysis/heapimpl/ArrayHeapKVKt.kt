package cn.sast.dataflow.interprocedural.analysis.heapimpl

/** 判断数组下标是否合法；`null` 表示“未知”。 */
internal fun isValidKey(key: Int?, size: Int?): Boolean? = when {
   key == null       -> null                     // 未知
   key < 0           -> false
   size != null && key >= size -> false
   else              -> true
}
