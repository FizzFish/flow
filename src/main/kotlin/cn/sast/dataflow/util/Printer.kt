package cn.sast.dataflow.util

import cn.sast.dataflow.interprocedural.analysis.CompanionV

/**
 * 打印调试用的辅助工具。
 */
object Printer {

   /** 单节点转字符串（默认 `toString()`） */
   fun <V> node2String(node: V): String = node.toString()

   /** 多节点打印成 `{ a, b, c }` 或 `empty` / `!null!` */
   fun <V> nodes2String(nodes: Collection<CompanionV<V>>?): String =
      when {
         nodes == null        -> "!null!"
         nodes.isEmpty()      -> "empty"
         else -> buildString {
            append("{ ")
            nodes.joinTo(
               this,
               separator = ", "
            ) { node2String(it) }
            append(" }")
         }
      }
}
