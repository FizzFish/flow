package cn.sast.framework.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.Orderer;

public class PseudoTopologicalOrderer<N> implements Orderer<N> {
   public static final boolean REVERSE = true;
   private boolean mIsReversed = false;

   public PseudoTopologicalOrderer() {
   }

   public List<N> newList(DirectedGraph<N> g, boolean reverse) {
      this.mIsReversed = reverse;
      return new PseudoTopologicalOrderer.ReverseOrderBuilder<N>(g).computeOrder(!reverse);
   }

   @Deprecated
   public PseudoTopologicalOrderer(boolean isReversed) {
      this.mIsReversed = isReversed;
   }

   @Deprecated
   public List<N> newList(DirectedGraph<N> g) {
      return new PseudoTopologicalOrderer.ReverseOrderBuilder<N>(g).computeOrder(!this.mIsReversed);
   }

   @Deprecated
   public void setReverseOrder(boolean isReversed) {
      this.mIsReversed = isReversed;
   }

   @Deprecated
   public boolean isReverseOrder() {
      return this.mIsReversed;
   }

   private static class ReverseOrderBuilder<N> {
      private final DirectedGraph<N> graph;
      private final int graphSize;
      private final int[] indexStack;
      private final N[] stmtStack;
      private final Set<N> visited;
      private final N[] order;
      private int orderLength;

      public ReverseOrderBuilder(DirectedGraph<N> g) {
         this.graph = g;
         int n = g.size();
         this.graphSize = n;
         this.visited = Collections.newSetFromMap(new IdentityHashMap<>(n * 2 + 1));
         this.indexStack = new int[n];
         N[] tempStmtStack = (N[])(new Object[n]);
         this.stmtStack = tempStmtStack;
         N[] tempOrder = (N[])(new Object[n]);
         this.order = tempOrder;
         this.orderLength = 0;
      }

      public List<N> computeOrder(boolean reverse) {
         for (N s : this.graph) {
            if (this.visited.add(s)) {
               this.visitNode(s);
            }

            if (this.orderLength == this.graphSize) {
               break;
            }
         }

         if (reverse) {
            reverseArray(this.order);
         }

         return Arrays.asList(this.order);
      }

      private void visitNode(N startStmt) {
         int last = 0;
         this.stmtStack[last] = startStmt;
         this.indexStack[last++] = -1;

         while (last > 0) {
            int toVisitIndex = ++this.indexStack[last - 1];
            N toVisitNode = this.stmtStack[last - 1];
            List<N> succs = this.graph.getSuccsOf(toVisitNode);
            if (toVisitIndex >= succs.size()) {
               this.order[this.orderLength++] = toVisitNode;
               last--;
            } else {
               N childNode = succs.get(toVisitIndex);
               if (this.visited.add(childNode)) {
                  this.stmtStack[last] = childNode;
                  this.indexStack[last++] = -1;
               }
            }
         }
      }

      private static <T> void reverseArray(T[] array) {
         int max = array.length >> 1;
         int i = 0;

         for (int j = array.length - 1; i < max; j--) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            i++;
         }
      }
   }
}
