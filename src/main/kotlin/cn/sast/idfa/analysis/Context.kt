package cn.sast.idfa.analysis

import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.PseudoTopologicalOrderer
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 可复用的单方法分析上下文。
 *
 * @param M  方法（如 `soot.SootMethod`）
 * @param N  CFG 节点（如 `soot.Unit`）
 * @param A  数据流格值
 */
open class Context<M, N, A>(
   val method: M,
   val cfg: DirectedGraph<N>,
   reverse: Boolean,
   var isAnalyzable: Boolean = true
) : soot.Context, Comparable<Context<M, N, A>> {

   /** 本方法是否已到达不动点 */
   var isAnalysed: Boolean = false
      private set

   /** 若为 true，外部调度器将跳过此方法 */
   var skipAnalysis: Boolean = false

   /** 是否启用路径敏感分析 */
   var pathSensitiveEnabled: Boolean = true

   /** 每个节点的迭代次数（调试用） */
   var iterationCount: MutableMap<N, Int> = HashMap()

   /** 已执行过 widening 的边 */
   var wideningEdges: MutableSet<Pair<N, N>> = HashSet()

   /** 入口 / 退出格值（由分析驱动代码设置） */
   var entryValue: A? = null
      private set
   var exitValue: A? = null
      private set

   /** 全局唯一 ID，可用来排序 */
   val id: Int = count.getAndIncrement()

   /** 格的 ⊥ 元素（先由框架注入，再初始化 worklist 时使用） */
   var bottomValue: A? = null

   /* ---------- 内部结构，分析完毕后可回收 ---------- */

   private val orderedNodes: List<N>
   private var inValues: MutableMap<N, A> = LinkedHashMap()
   private var edgeValues: MutableMap<Pair<N, N>, A> = LinkedHashMap()
   private var callEdgeValues: MutableMap<Triple<N, M, A>, A> = LinkedHashMap()

   private var callSite: N? = null

   /** worklist 采用伪拓扑序号做优先级 */
   val worklist: NavigableSet<Pair<N, N>>

   init {
      /* ---------- 计算伪拓扑序 & worklist 比较器 ---------- */
      val orderer = PseudoTopologicalOrderer<N>()
      orderedNodes = orderer.newList(cfg, reverse)

      val edgeNumber = HashMap<Pair<N, N>, Int>()
      var num = 1
      for (node in orderedNodes) {
         val succs = cfg.getSuccsOf(node)
         if (succs.isEmpty()) {
            edgeNumber[node to node] = num++
         } else {
            for (succ in succs) edgeNumber[node to succ] = num++
         }
      }

      worklist = TreeSet { e1, e2 ->
         (edgeNumber[e1] ?: 0) - (edgeNumber[e2] ?: 0)
      }
   }

   /* ---------- 小工具函数 ---------- */

   override fun compareTo(other: Context<M, N, A>): Int = id - other.id

   fun edgeValue(from: N, to: N): A? = edgeValues[from to to] ?: bottomValue

   fun setEdgeValue(from: N, to: N, value: A) {
      edgeValues[from to to] = value
   }

   fun valueBefore(node: N): A? = inValues[node]

   fun setValueBefore(node: N, value: A) {
      inValues[node] = value
   }

   fun setEntryValue(value: A) {
      entryValue = value
   }

   fun setExitValue(value: A) {
      exitValue = value
   }

   /** 标记已分析完毕并清理大对象，方便 GC */
   fun markAnalysed() {
      isAnalysed = true
      callEdgeValues.clear()
      edgeValues.clear()
      inValues.clear()
      iterationCount.clear()
      wideningEdges.clear()
   }

   /* ---------- worklist 相关 ---------- */

   /** 首次 / 重新开始分析时调用 */
   fun initWorklist() {
      isAnalysed = false
      for (node in orderedNodes) {
         val succs = cfg.getSuccsOf(node)
         if (succs.isEmpty()) {
            worklist += node to node
            bottomValue?.let { setEdgeValue(node, node, it) }
         } else {
            for (succ in succs) {
               worklist += node to succ
               bottomValue?.let { setEdgeValue(node, succ, it) }
            }
         }
      }
   }

   fun clearWorklist() = worklist.clear()

   fun addToWorklist(node: N) {
      val succs = cfg.getSuccsOf(node)
      if (succs.isEmpty()) {
         worklist += node to node
      } else {
         for (succ in succs) worklist += node to succ
      }
   }

   /* ---------- 过程间信息 ---------- */

   fun setCallSite(node: N) {
      callSite = node
   }

   fun getCallSite(): N? = callSite

   fun hasCallSite(): Boolean = callSite != null

   fun getCallEdgeValue(node: N, callee: M, entry: A): A? =
      callEdgeValues[Triple(node, callee, entry)]

   fun setCallEdgeValue(node: N, callee: M, entry: A, out: A) {
      callEdgeValues[Triple(node, callee, entry)] = out
   }

   /* ---------- 调试输出 ---------- */

   override fun toString(): String = buildString {
      if (!isAnalyzable) append("NN ")
      append("$id : $method")
   }

   companion object {
      val count: AtomicInteger = AtomicInteger()

      fun reset() = count.set(0)
   }
}
