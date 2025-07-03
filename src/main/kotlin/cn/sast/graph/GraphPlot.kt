package cn.sast.graph

import mu.KotlinLogging
import soot.toolkits.graph.DirectedGraph
import soot.util.dot.DotGraph
import soot.util.dot.DotGraphEdge
import soot.util.dot.DotGraphNode
import java.util.*

/**
 * 将 [cfg] 绘制成 DOT 图的抽象基类。
 *
 * @param C  “子图容器” 的泛型；由 [getNodeContainer] 决定
 * @param N  节点类型（与 Soot CFG 保持一致）
 */
abstract class GraphPlot<C, N>(
   val cfg: DirectedGraph<N>
) {

   private val logger = KotlinLogging.logger {}

   /**
    * 返回节点 *所属子图*；相同子图的节点会放在同一 cluster 内。
    */
   protected abstract fun getNodeContainer(node: N): C

   /** 返回节点在 DOT 图里的 label（可含 HTML） */
   protected open fun getLabel(node: N): String = node.toString()

   /**
    * 生成并返回 [DotGraph]；可调用 `render()` 再落盘。
    */
   fun plot(graphName: String = "DirectedGraph"): DotGraph {
      val dot = DotGraph(graphName).apply {
         setGraphAttribute("style", "filled")
         setGraphAttribute("color", "lightgrey")
         setGraphAttribute("rankdir", "LR")
         setGraphAttribute("ranksep", "8")
         setNodeShape("box")
      }

      /* cluster 抽象容器 */
      val subGraphs = LinkedHashMap<C, DotGraph>()

      /* start 节点 */
      val startCluster = dot.createSubGraph("cluster_start").apply {
         setGraphLabel("START")
         setGraphAttribute("style", "filled")
         setGraphAttribute("color", "lightgrey")
         setNodeShape("box")
      }

      /* ----- 遍历所有节点 ----- */
      for (node in cfg) {
         val nodeId = node.toString()
         val container = getNodeContainer(node)
         val sub = subGraphs.getOrPut(container) {
            dot.createSubGraph("cluster_$container").apply {
               setGraphLabel(container.toString())
               setGraphAttribute("style", "filled")
               setGraphAttribute("color", "lightgrey")
               setGraphAttribute("labeljust", "l")
               setNodeShape("box")
            }
         }

         /* 绘制节点本身 */
         val fromNode: DotGraphNode = sub.drawNode(nodeId).apply {
            setHTMLLabel(getLabel(node))
         }

         /* 起点额外标记 */
         if (cfg.heads.contains(node)) {
            fromNode.setAttribute("color", "blue")
            dot.drawEdge(startCluster.label, nodeId).setAttribute("color", "green")
         }

         /* 遍历后继 */
         for (succ in cfg.getSuccsOf(node)) {
            val succId = succ.toString()
            val succContainer = getNodeContainer(succ)
            val succSub = subGraphs.getOrPut(succContainer) {
               dot.createSubGraph("cluster_$succContainer").apply {
                  setGraphLabel(succContainer.toString())
                  setGraphAttribute("style", "filled")
                  setGraphAttribute("color", "lightgrey")
                  setGraphAttribute("labeljust", "l")
                  setNodeShape("box")
               }
            }
            succSub.drawNode(succId).setHTMLLabel(getLabel(succ))

            val edge: DotGraphEdge = dot.drawEdge(nodeId, succId).apply {
               setAttribute("color", "blue")
               if (container == succContainer) setAttribute("style", "dashed")
            }
         }
      }
      return dot
   }
}
