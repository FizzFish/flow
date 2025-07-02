package cn.sast.dataflow.infoflow.svfa

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.extensions.*
import soot.Value

/**
 * 数据流分析中的单个流事实：
 * `Value → { VFNode… }`
 */
internal class FlowFact(
   var data: PersistentMap<Value, PersistentSet<VFNode>> =
      persistentHashMapOf()
) {

   /* ---------- Object 基础 ---------- */

   override fun toString(): String =
      buildString {
         data.values.flatten().joinTo(this, separator = "\n", prefix = "\n")
      }

   override fun hashCode(): Int = data.hashCode()

   override fun equals(other: Any?): Boolean =
      other is FlowFact && other.data == data
}
