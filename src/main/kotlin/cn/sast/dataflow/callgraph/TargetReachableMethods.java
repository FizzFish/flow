package cn.sast.dataflow.callgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import soot.MethodOrMethodContext;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Filter;
import soot.jimple.toolkits.callgraph.Sources;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

public class TargetReachableMethods {
   protected final ChunkedQueue<MethodOrMethodContext> reachables = new ChunkedQueue();
   protected final Set<MethodOrMethodContext> set = new HashSet<>();
   protected final QueueReader<MethodOrMethodContext> allReachables = this.reachables.reader();
   protected QueueReader<MethodOrMethodContext> unprocessedMethods;
   protected Iterator<Edge> edgeSource;
   protected CallGraph cg;
   protected Filter filter;

   public TargetReachableMethods(CallGraph graph, Iterator<? extends MethodOrMethodContext> lookupPoint, Filter filter) {
      this.filter = filter;
      this.cg = graph;
      this.addMethods(lookupPoint);
      this.unprocessedMethods = this.reachables.reader();
      this.edgeSource = (Iterator<Edge>)(filter == null ? graph.listener() : filter.wrap(graph.listener()));
   }

   public TargetReachableMethods(CallGraph graph, Iterator<? extends MethodOrMethodContext> lookupPoint) {
      this(graph, lookupPoint, null);
   }

   public TargetReachableMethods(CallGraph graph, Collection<? extends MethodOrMethodContext> lookupPoint) {
      this(graph, lookupPoint.iterator());
   }

   protected void addMethods(Iterator<? extends MethodOrMethodContext> methods) {
      while (methods.hasNext()) {
         this.addMethod(methods.next());
      }
   }

   protected void addMethod(MethodOrMethodContext m) {
      if (this.set.add(m)) {
         this.reachables.add(m);
      }
   }

   public void update() {
      while (this.edgeSource.hasNext()) {
         Edge e = this.edgeSource.next();
         if (e != null) {
            MethodOrMethodContext tgtMethod = e.getTgt();
            if (tgtMethod != null && !e.isInvalid() && this.set.contains(tgtMethod)) {
               this.addMethod(e.getSrc());
            }
         }
      }

      while (this.unprocessedMethods.hasNext()) {
         MethodOrMethodContext m = (MethodOrMethodContext)this.unprocessedMethods.next();
         Iterator<Edge> sources = this.cg.edgesInto(m);
         if (this.filter != null) {
            sources = this.filter.wrap(sources);
         }

         this.addMethods(new Sources(sources));
      }
   }

   public QueueReader<MethodOrMethodContext> listener() {
      return this.allReachables.clone();
   }

   public QueueReader<MethodOrMethodContext> newListener() {
      return this.reachables.reader();
   }

   public boolean contains(MethodOrMethodContext m) {
      return this.set.contains(m);
   }

   public int size() {
      return this.set.size();
   }
}
