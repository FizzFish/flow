package cn.sast.dataflow.infoflow.svfa

import java.util.*

/**
 * 带引用计数的包装，用于 Sparse 流程中的节点活跃性管理。
 */
class RefCntUnit<N>(
   val u: N,
   cnt: Int = 1
) {
   var cnt: Int = cnt
      internal set

   /** 被当前节点引用的 *前驱* */
   private val ref: Queue<RefCntUnit<*>> = LinkedList()

   /* ---------- 引用计数操作 ---------- */

   fun add(prev: RefCntUnit<*>) {
      ref += prev
      prev.cnt++
   }

   fun dec() {
      cnt--
      if (cnt == 0) ref.forEach { it.dec() }
      require(cnt >= 0) { "Reference count under-flow" }
   }

   /* ---------- Object 基础 ---------- */

   override fun hashCode(): Int = u?.hashCode() ?: 0

   override fun equals(other: Any?): Boolean =
      other is RefCntUnit<*> && other.u == u

   override fun toString(): String = "$u $cnt"
}
