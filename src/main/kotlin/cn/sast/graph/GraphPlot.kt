package cn.sast.graph

import java.util.LinkedHashMap
import mu.KLogger
import soot.toolkits.graph.DirectedGraph
import soot.util.dot.DotGraph
import soot.util.dot.DotGraphEdge
import soot.util.dot.DotGraphNode

public abstract class GraphPlot<C, N> {
   public final val cfg: DirectedGraph<Any>

   open fun GraphPlot(cfg: DirectedGraph<N>) {
      this.cfg = cfg;
   }

   public fun plot(graphName: String = "DirectedGraph"): DotGraph {
      val dot: DotGraph = new DotGraph(graphName);
      dot.setGraphAttribute("style", "filled");
      dot.setGraphAttribute("color", "lightgrey");
      dot.setGraphAttribute("rankdir", "LR");
      dot.setGraphAttribute("ranksep", "8");
      dot.setNodeShape("box");
      val map: java.util.Map = new LinkedHashMap();
      val var5: DotGraph = dot.createSubGraph("cluster_start");
      var5.setGraphLabel("START");
      var5.setGraphAttribute("style", "filled");
      var5.setGraphAttribute("color", "lightgrey");
      var5.setNodeShape("box");
      val start: DotGraph = var5;
      val var10000: java.util.Iterator = this.cfg.iterator();
      val var23: java.util.Iterator = var10000;

      while (var23.hasNext()) {
         val node: Any = var23.next();
         val var24: java.lang.String = java.lang.String.valueOf(node);
         val nodeContainer: Any = this.getNodeContainer((N)node);
         var var30: DotGraph = map.get(nodeContainer) as DotGraph;
         if (var30 == null) {
            val succ: GraphPlot = this;
            val succNodeContainer: DotGraph = dot.createSubGraph("cluster_$nodeContainer");
            map.put(nodeContainer, succNodeContainer);
            succNodeContainer.setGraphLabel(java.lang.String.valueOf(nodeContainer));
            succNodeContainer.setGraphAttribute("style", "filled");
            succNodeContainer.setGraphAttribute("color", "lightgrey");
            succNodeContainer.setGraphAttribute("labeljust", "l");
            succNodeContainer.setNodeShape("box");
            var30 = succNodeContainer;
         }

         val fromNode: DotGraphNode = var30.drawNode(var24);
         fromNode.setHTMLLabel(this.getLabel((N)node));
         if (this.cfg.getHeads().contains(node)) {
            fromNode.setAttribute("color", "blue");
            dot.drawEdge(start.getLabel(), var24).setAttribute("color", "green");
         }

         for (Object succ : this.cfg.getSuccsOf(node)) {
            val var27: java.lang.String = java.lang.String.valueOf(var26);
            val var28: Any = this.getNodeContainer((N)var26);
            var var31: DotGraph = map.get(var28) as DotGraph;
            if (var31 == null) {
               val it: GraphPlot = this;
               val var20: DotGraph = dot.createSubGraph("cluster_$var28");
               map.put(var28, var20);
               var20.setGraphLabel(java.lang.String.valueOf(var28));
               var20.setGraphAttribute("style", "filled");
               var20.setGraphAttribute("color", "lightgrey");
               var20.setGraphAttribute("labeljust", "l");
               var20.setNodeShape("box");
               var31 = var20;
            }

            var31.drawNode(var27).setHTMLLabel(this.getLabel((N)var26));
            val edge: DotGraphEdge = dot.drawEdge(var24, var27);
            edge.setAttribute("color", "blue");
            if (nodeContainer == var28) {
               edge.setAttribute("style", "dashed");
            }
         }
      }

      return dot;
   }

   public open fun Any.getLabel(): String {
      return java.lang.String.valueOf(`$this$getLabel`);
   }

   public abstract fun Any.getNodeContainer(): Any {
   }

   @JvmStatic
   fun `logger$lambda$5`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
