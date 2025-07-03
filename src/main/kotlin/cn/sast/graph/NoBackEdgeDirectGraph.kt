package cn.sast.graph

import java.util.*

/**
 * 一个保证 **无回边** 的有向图包装器；
 * 在添加边时做循环检测，若会形成回路则拒绝。
 */
class NoBackEdgeDirectGraph<N> {

   private val predView: MutableMap<N, MutableMap<N, MutableSet<N>>> = HashMap()
   private val graph: HashMutableDirectedGraph<N> = HashMutableDirectedGraph()

   /* ---------- 状态 ---------- */

   val isComplete: Boolean
      get() = predView.isEmpty() && graph.size() == 0 &&
              graph.heads.isEmpty() && graph.tails.isEmpty()

   val heads: List<N> get() = synchronized(this) { graph.heads }

   /* ---------- 内部工具 ---------- */

   private fun getPredTasksOf(from: N): MutableSet<N> =
      buildLinkedSet<N> {
         for (pred in graph.getPredsOf(from)) {
            predView[pred]?.get(from)?.let { addAll(it) }
         }
      }

   /* ---------- 边操作 ---------- */

   fun addEdge(from: N, to: N): Boolean {
      if (from == to) return false
      val predTasks = getPredTasksOf(from)
      if (to in predTasks) return false      // would create a back edge

      predTasks += from                      // 自己也要纳入其闭包
      graph.addEdge(from, to)

      /* 维护 predView，传播影响 */
      val work: Queue<N> = LinkedList<N>().apply { add(from) }
      val visited = HashSet<N>().apply { add(from) }

      while (work.isNotEmpty()) {
         val cur = work.poll()
         for (succ in graph.getSuccsOf(cur)) {
            val curView = predView.getOrPut(cur) { HashMap() }
            val set = curView.getOrPut(succ) { HashSet() }

            if (!set.containsAll(predTasks)) {
               if (visited.add(succ)) work.add(succ)
               set.addAll(predTasks)
            }
         }
      }
      return true
   }

   fun removeEdge(from: N, to: N) {
      graph.removeEdge(from, to)
      predView[from]?.apply {
         remove(to)
         if (isEmpty()) predView.remove(from)
      }
   }

   /* ---------- 并发包装 ---------- */

   fun addEdgeSynchronized(from: N, to: N): Boolean =
      synchronized(this) { addEdge(from, to) }

   fun removeEdgeSynchronized(from: N, to: N) =
      synchronized(this) { removeEdge(from, to) }

   fun getPredSize(from: N): Int =
      synchronized(this) { getPredTasksOf(from).size }

   fun cleanUp() {
      predView.clear(); graph.clearAll()
   }

   /* ---------- utils ---------- */
   private inline fun <T> buildLinkedSet(builder: MutableSet<T>.() -> Unit): LinkedHashSet<T> =
      LinkedHashSet<T>().apply(builder)
}
