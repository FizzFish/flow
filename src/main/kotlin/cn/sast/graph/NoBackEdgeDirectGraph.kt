package cn.sast.graph

import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.Queue
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nNoBackEdgeDirectGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NoBackEdgeDirectGraph.kt\ncn/sast/graph/NoBackEdgeDirectGraph\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,111:1\n1#2:112\n*E\n"])
public class NoBackEdgeDirectGraph<N> {
   private final var predView: MutableMap<Any, MutableMap<Any, MutableSet<Any>>> = (new HashMap()) as java.util.Map
   private final var directedGraph: HashMutableDirectedGraph<Any> = new HashMutableDirectedGraph()

   public final val isComplete: Boolean
      public final get() {
         return this.predView.isEmpty() && this.directedGraph.size() == 0 && this.directedGraph.getHeads().isEmpty() && this.directedGraph.getTails().isEmpty();
      }


   public final val heads: List<Any>
      public final get() {
         synchronized (this) {
            return this.directedGraph.getHeads();
         }
      }


   public fun getPredsTaskOf(from: Any): MutableSet<Any> {
      val predTaskOfFrom: java.util.Set = new LinkedHashSet();

      for (Object pred : this.directedGraph.getPredsOfAsSet((N)from)) {
         val var10000: java.util.Map = this.predView.get(pred);
         if (var10000 != null) {
            val var6: java.util.Set = var10000.get(from) as java.util.Set;
            if (var6 != null) {
               predTaskOfFrom.addAll(var6);
            }
         }
      }

      return predTaskOfFrom;
   }

   public fun addEdge(from: Any, to: Any): Boolean {
      if (from == to) {
         return false;
      } else {
         val predTaskOfFrom: java.util.Set = this.getPredsTaskOf((N)from);
         if (predTaskOfFrom.contains(to)) {
            return false;
         } else {
            predTaskOfFrom.add(from);
            this.directedGraph.addEdge((N)from, (N)to);
            val workList: Queue = new LinkedList();
            workList.add(from);
            val set: HashSet = new HashSet();
            set.add(from);

            while (!workList.isEmpty()) {
               val cur: Any = workList.poll();

               for (Object next : this.directedGraph.getSuccsOfAsSet((N)cur)) {
                  var curView: java.util.Map = this.predView.get(cur);
                  if (curView == null) {
                     curView = new HashMap();
                     this.predView.put((N)cur, curView);
                  }

                  var predTaskOfCur: java.util.Set = curView.get(next) as java.util.Set;
                  if (predTaskOfCur == null) {
                     predTaskOfCur = new HashSet();
                     curView.put(next, predTaskOfCur);
                  }

                  if (!predTaskOfCur.containsAll(predTaskOfFrom)) {
                     if (set.add(next)) {
                        workList.add(next);
                     }

                     predTaskOfCur.addAll(predTaskOfFrom);
                  }
               }
            }

            return true;
         }
      }
   }

   public fun removeEdge(from: Any, to: Any) {
      this.directedGraph.removeEdge((N)from, (N)to);
      var var10000: java.util.Map = this.predView.get(from);
      if (var10000 != null) {
         var10000.remove(to);
         var10000 = var10000;
      } else {
         var10000 = null;
      }

      if (var10000 != null && var10000.isEmpty()) {
         this.predView.remove(from);
      }
   }

   public fun getPredSize(from: Any): Int {
      synchronized (this) {
         return this.getPredsTaskOf((N)from).size();
      }
   }

   public fun addEdgeSynchronized(from: Any, to: Any): Boolean {
      synchronized (this) {
         return this.addEdge((N)from, (N)to);
      }
   }

   public fun removeEdgeSynchronized(from: Any, to: Any) {
      synchronized (this) {
         this.removeEdge((N)from, (N)to);
      }
   }

   public fun cleanUp() {
      this.predView.clear();
      this.directedGraph.clearAll();
   }
}
