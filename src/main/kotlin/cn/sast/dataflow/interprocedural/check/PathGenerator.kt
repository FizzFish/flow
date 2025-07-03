package cn.sast.dataflow.interprocedural.check

import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.LinkedList
import kotlin.jvm.internal.SourceDebugExtension
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.MutableDirectedGraph

@SourceDebugExtension(["SMAP\nPathGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGenerator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,213:1\n1279#2,2:214\n1293#2,4:216\n*S KotlinDebug\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGenerator\n*L\n31#1:214,2\n31#1:216,4\n*E\n"])
public abstract class PathGenerator<P> {
    public abstract val shouldExplore: Boolean
    public abstract val preds: Collection<Any>

    public fun flush(g: DirectedGraph<Any>, heads: Set<Any>): Map<Any, List<Any>> {
        val result = LinkedHashMap<Any, List<Any>>(kotlin.math.max(kotlin.collections.mapCapacity(heads.size), 16))

        for (element in heads) {
            val pQueue = LinkedList<Any>()
            pQueue.add(element)
            val event = ArrayList<Any>()
            val visit = LinkedHashSet<Any>()

            while (pQueue.isNotEmpty()) {
                val current = pQueue.removeAt(0)
                event.add(current)

                for (to in g.getSuccsOf(current)) {
                    event.add(to)
                    if (visit.add(to)) {
                        pQueue.add(to)
                        break
                    }
                }
            }

            result[element] = PathGeneratorKt.getRemoveAdjacentDuplicates(event)
        }

        return result
    }

    public fun getHeads(sink: Any, g: MutableDirectedGraph<Any>? = null): Set<Any> {
        val heads = LinkedHashSet<Any>()
        val pQueue = LinkedList<Any>()
        pQueue.add(sink)
        val visit = LinkedHashSet<Any>()

        while (pQueue.isNotEmpty()) {
            val node = pQueue.removeAt(0)
            if (shouldExplore) {
                if (g != null && !g.containsNode(node)) {
                    g.addNode(node)
                }

                val preds = preds
                if (preds.isEmpty()) {
                    heads.add(node)
                }

                for (pred in preds) {
                    if (shouldExplore) {
                        if (g != null) {
                            if (!g.containsNode(pred)) {
                                g.addNode(pred)
                            }
                            g.addEdge(pred, node)
                        }

                        if (visit.add(pred)) {
                            pQueue.add(pred)
                        }
                    }
                }
            }
        }

        return heads
    }
}