package cn.sast.dataflow.interprocedural.check

import java.util.IdentityHashMap
import java.util.LinkedHashMap
import soot.SootMethod
import soot.jimple.Stmt
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG
import soot.util.dot.DotGraph
import soot.util.dot.DotGraphEdge
import soot.util.dot.DotGraphNode

public class DefaultAbstractionGraph(sink: Abstraction) : AbstractionGraph(sink) {
   init {
      val size: Int = this.getAbsChain().size();
      this.setUnitToSuccs(new IdentityHashMap<>(size * 2 + 1));
      this.setUnitToPreds(new IdentityHashMap<>(size * 2 + 1));
      this.buildUnexceptionalEdges(this.getUnitToPreds(), this.getUnitToSuccs());
      this.buildHeadsAndTails();
   }

   public fun plot(cfg: IInfoflowCFG): DotGraph {
      val dot: DotGraph = new DotGraph("AbsGraph");
      dot.setGraphAttribute("style", "filled");
      dot.setGraphAttribute("color", "lightgrey");
      dot.setGraphAttribute("rankdir", "LR");
      dot.setGraphAttribute("ranksep", "8");
      dot.setNodeShape("box");
      val map: java.util.Map = new LinkedHashMap();
      var var10000: java.util.Iterator = this.getAbsChain().iterator();
      val var4: java.util.Iterator = var10000;

      while (var4.hasNext()) {
         var10000 = (java.util.Iterator)var4.next();
         val a: Abstraction = var10000 as Abstraction;
         val name: java.lang.String = java.lang.String.valueOf(System.identityHashCode(var10000 as Abstraction));
         val var34: DotGraphNode;
         if (a.getCurrentStmt() == null) {
            val sm: DotGraphNode = dot.drawNode(name);
            sm.setHTMLLabel(AbstractionGraphKt.getLabel(a, cfg));
            var34 = sm;
         } else {
            val var21: SootMethod = cfg.getMethodOf(a.getCurrentStmt()) as SootMethod;
            val s: SootMethod = cfg.getMethodOf(a.getCurrentStmt()) as SootMethod;
            var var35: DotGraph = map.get(s) as DotGraph;
            if (var35 == null) {
               val sub2: DefaultAbstractionGraph = this;
               val it: DotGraph = dot.createSubGraph("cluster_${System.identityHashCode(s)}");
               map.put(s, it);
               it.setGraphLabel(s.toString());
               it.setGraphAttribute("style", "filled");
               it.setGraphAttribute("color", "lightgrey");
               it.setNodeShape("box");
               var35 = it;
            }

            val to: DotGraphNode = var35.drawNode(name);
            to.setHTMLLabel(AbstractionGraphKt.getLabel(a, cfg));
            var34 = to;
         }

         for (Abstraction s : this.getSuccsOf(a)) {
            val var24: java.lang.String = java.lang.String.valueOf(System.identityHashCode(var23));
            val var36: DotGraphNode;
            if (var23.getCurrentStmt() == null) {
               val edge: DotGraphNode = dot.drawNode(var24);
               edge.setHTMLLabel(AbstractionGraphKt.getLabel(var23, cfg));
               var36 = edge;
            } else {
               val var26: SootMethod = cfg.getMethodOf(var23.getCurrentStmt()) as SootMethod;
               var var37: DotGraph = map.get(var26) as DotGraph;
               if (var37 == null) {
                  val var16: DefaultAbstractionGraph = this;
                  val var18: DotGraph = dot.createSubGraph("cluster_${System.identityHashCode(var26)}");
                  map.put(var26, var18);
                  var18.setGraphLabel(var26.toString());
                  var18.setGraphAttribute("style", "filled");
                  var18.setGraphAttribute("color", "lightgrey");
                  var18.setNodeShape("box");
                  var37 = var18;
               }

               val var29: DotGraphNode = var37.drawNode(var24);
               var29.setHTMLLabel(AbstractionGraphKt.getLabel(var23, cfg));
               var36 = var29;
            }

            val var27: DotGraphEdge = dot.drawEdge(name, var24);
            var27.setAttribute("color", "green");
            if (!(var23.getCorrespondingCallSite() == var23.getCurrentStmt())) {
               val var38: Stmt = var23.getCorrespondingCallSite();
               if (var38 != null) {
                  if (var38 == a.getCurrentStmt()) {
                     var27.setAttribute("color", "red");
                  } else {
                     var27.setAttribute("style", "dashed");
                     var27.setAttribute("color", "black");
                  }
               }
            }

            if (this.isTail(var23)) {
               var36.setAttribute("color", "red");
            }
         }

         if (this.isHead(a)) {
            var34.setAttribute("color", "blue");
         }
      }

      return dot;
   }
}
