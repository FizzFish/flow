package cn.sast.graph

import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import soot.toolkits.graph.MutableDirectedGraph
import java.util.*

/**
 * 使用 HashMap / HashSet 实现的可变有向图。
 */
@Serializable
class HashMutableDirectedGraph<N> : MutableDirectedGraph<N>, Cloneable {

   /* ---------- 内部结构 ---------- */

   private val nodeToPreds: MutableMap<N, MutableSet<N>> = HashMap()
   private val nodeToSuccs: MutableMap<N, MutableSet<N>> = HashMap()
   private val heads: MutableSet<N> = LinkedHashSet()
   private val tails: MutableSet<N> = LinkedHashSet()

   /* ---------- 构造 ---------- */

   constructor()
   constructor(orig: HashMutableDirectedGraph<N>) {
      nodeToPreds.putAll(orig.nodeToPreds.mapValues { HashSet(it.value) })
      nodeToSuccs.putAll(orig.nodeToSuccs.mapValues { HashSet(it.value) })
      heads += orig.heads
      tails += orig.tails
   }

   /* ---------- MutableDirectedGraph ---------- */

   override fun size(): Int = nodeToPreds.size
   override fun getNodes(): MutableList<N> {
      return nodeToPreds.keys.toMutableList()
   }

   override fun iterator(): MutableIterator<N> = nodeToPreds.keys.iterator()

   override fun getHeads(): MutableList<N> = heads.toMutableList()
   override fun getTails(): MutableList<N> = tails.toMutableList()

   override fun getPredsOf(s: N): MutableList<N> =
      nodeToPreds[s]?.toMutableList() ?: mutableListOf()

   override fun getSuccsOf(s: N): MutableList<N> =
      nodeToSuccs[s]?.toMutableList() ?: mutableListOf()

   /* ---------- 基本操作 ---------- */

   fun clearAll() {
      nodeToPreds.clear(); nodeToSuccs.clear(); heads.clear(); tails.clear()
   }

   override fun addNode(node: N) {
      if (node !in nodeToPreds) {
         nodeToPreds[node] = LinkedHashSet()
         nodeToSuccs[node] = LinkedHashSet()
         heads += node; tails += node
      }
   }

   override fun removeNode(node: N) {
      nodeToSuccs[node]?.toList()?.forEach { removeEdge(node, it) }
      nodeToPreds[node]?.toList()?.forEach { removeEdge(it, node) }
      nodeToSuccs.remove(node)
      nodeToPreds.remove(node)
      heads.remove(node); tails.remove(node)
   }

   override fun containsNode(node: N): Boolean = node in nodeToPreds

   override fun containsEdge(from: N, to: N): Boolean =
      nodeToSuccs[from]?.contains(to) == true

   override fun addEdge(from: N, to: N) {
      if (from == to || containsEdge(from, to)) return
      addNode(from); addNode(to)

      nodeToSuccs[from]!!.add(to)
      nodeToPreds[to]!!.add(from)

      heads.remove(to); tails.remove(from)
   }

   override fun removeEdge(from: N, to: N) {
      if (!containsEdge(from, to)) return
      nodeToSuccs[from]!!.remove(to)
      nodeToPreds[to]!!.remove(from)

      if (nodeToSuccs[from]!!.isEmpty()) tails += from
      if (nodeToPreds[to]!!.isEmpty()) heads += to
   }

   /* ---------- 打印 ---------- */

   fun printGraph() {
      for (n in this) {
         logger.debug("Node = $n")
         logger.debug("  Preds: ${getPredsOf(n)}")
         logger.debug("  Succs: ${getSuccsOf(n)}")
      }
   }

   /* ---------- 克隆 ---------- */

   public override fun clone(): HashMutableDirectedGraph<N> = HashMutableDirectedGraph(this)

   companion object {
      private val logger = LoggerFactory.getLogger("HashMutableDirectedGraph")
   }
}
