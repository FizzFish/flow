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
      val `$this$associateWith$iv`: java.lang.Iterable = heads;
      val `result$iv`: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(heads, 10)), 16));

      for (Object element$iv$iv : $this$associateWith$iv) {
         val var19: java.util.Map = `result$iv`;
         val pQueue: LinkedList = new LinkedList();
         pQueue.add(`element$iv$iv`);
         val event: java.util.List = new ArrayList();
         val visit: java.util.Set = new LinkedHashSet();

         while (!pQueue.isEmpty()) {
            val var10000: Any = pQueue.remove(0);
            event.add(var10000);

            for (Object to : g.getSuccsOf(var10000)) {
               event.add(to);
               if (visit.add(to)) {
                  pQueue.add(to);
                  break;
               }
            }
         }

         var19.put(`element$iv$iv`, PathGeneratorKt.getRemoveAdjacentDuplicates(event));
      }

      return `result$iv`;
   }

   public fun getHeads(sink: Any, g: MutableDirectedGraph<Any>? = null): Set<Any> {
      val heads: java.util.Set = new LinkedHashSet();
      val pQueue: LinkedList = new LinkedList();
      pQueue.add(sink);
      val visit: java.util.Set = new LinkedHashSet();

      while (!pQueue.isEmpty()) {
         val var10000: Any = pQueue.remove(0);
         val node: Any = var10000;
         if (this.getShouldExplore((P)var10000)) {
            if (g != null && !g.containsNode(var10000)) {
               g.addNode(var10000);
            }

            val preds: java.util.Collection = this.getPreds((P)var10000);
            if (preds.isEmpty()) {
               heads.add(var10000);
            }

            for (Object pred : preds) {
               if (this.getShouldExplore((P)pred)) {
                  if (g != null) {
                     if (!g.containsNode(pred)) {
                        g.addNode(pred);
                     }

                     g.addEdge(pred, node);
                  }

                  if (visit.add(pred)) {
                     pQueue.add(pred);
                  }
               }
            }
         }
      }

      return heads;
   }
}
