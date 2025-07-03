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
        val size: Int = this.getAbsChain().size
        this.setUnitToSuccs(IdentityHashMap(size * 2 + 1))
        this.setUnitToPreds(IdentityHashMap(size * 2 + 1))
        this.buildUnexceptionalEdges(this.getUnitToPreds(), this.getUnitToSuccs())
        this.buildHeadsAndTails()
    }

    public fun plot(cfg: IInfoflowCFG): DotGraph {
        val dot: DotGraph = DotGraph("AbsGraph")
        dot.setGraphAttribute("style", "filled")
        dot.setGraphAttribute("color", "lightgrey")
        dot.setGraphAttribute("rankdir", "LR")
        dot.setGraphAttribute("ranksep", "8")
        dot.setNodeShape("box")
        val map: MutableMap<SootMethod, DotGraph> = LinkedHashMap()
        val chainIterator = this.getAbsChain().iterator()

        while (chainIterator.hasNext()) {
            val a: Abstraction = chainIterator.next()
            val name: String = System.identityHashCode(a).toString()
            val node: DotGraphNode
            if (a.getCurrentStmt() == null) {
                val sm: DotGraphNode = dot.drawNode(name)
                sm.setHTMLLabel(AbstractionGraphKt.getLabel(a, cfg))
                node = sm
            } else {
                val s: SootMethod = cfg.getMethodOf(a.getCurrentStmt()) as SootMethod
                var subGraph: DotGraph? = map[s] as DotGraph
                if (subGraph == null) {
                    val it: DotGraph = dot.createSubGraph("cluster_${System.identityHashCode(s)}")
                    map[s] = it
                    it.setGraphLabel(s.toString())
                    it.setGraphAttribute("style", "filled")
                    it.setGraphAttribute("color", "lightgrey")
                    it.setNodeShape("box")
                    subGraph = it
                }

                val to: DotGraphNode = subGraph.drawNode(name)
                to.setHTMLLabel(AbstractionGraphKt.getLabel(a, cfg))
                node = to
            }

            for (succ in this.getSuccsOf(a)) {
                val succName: String = System.identityHashCode(succ).toString()
                val succNode: DotGraphNode
                if (succ.getCurrentStmt() == null) {
                    val edge: DotGraphNode = dot.drawNode(succName)
                    edge.setHTMLLabel(AbstractionGraphKt.getLabel(succ, cfg))
                    succNode = edge
                } else {
                    val succMethod: SootMethod = cfg.getMethodOf(succ.getCurrentStmt()) as SootMethod
                    var succSubGraph: DotGraph? = map[succMethod] as DotGraph
                    if (succSubGraph == null) {
                        val newSubGraph: DotGraph = dot.createSubGraph("cluster_${System.identityHashCode(succMethod)}")
                        map[succMethod] = newSubGraph
                        newSubGraph.setGraphLabel(succMethod.toString())
                        newSubGraph.setGraphAttribute("style", "filled")
                        newSubGraph.setGraphAttribute("color", "lightgrey")
                        newSubGraph.setNodeShape("box")
                        succSubGraph = newSubGraph
                    }

                    val succTo: DotGraphNode = succSubGraph.drawNode(succName)
                    succTo.setHTMLLabel(AbstractionGraphKt.getLabel(succ, cfg))
                    succNode = succTo
                }

                val edge: DotGraphEdge = dot.drawEdge(name, succName)
                edge.setAttribute("color", "green")
                if (succ.getCorrespondingCallSite() != succ.getCurrentStmt()) {
                    val callSite: Stmt? = succ.getCorrespondingCallSite()
                    if (callSite != null) {
                        if (callSite == a.getCurrentStmt()) {
                            edge.setAttribute("color", "red")
                        } else {
                            edge.setAttribute("style", "dashed")
                            edge.setAttribute("color", "black")
                        }
                    }
                }

                if (this.isTail(succ)) {
                    succNode.setAttribute("color", "red")
                }
            }

            if (this.isHead(a)) {
                node.setAttribute("color", "blue")
            }
        }

        return dot
    }
}