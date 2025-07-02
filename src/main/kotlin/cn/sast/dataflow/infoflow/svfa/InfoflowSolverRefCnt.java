package cn.sast.dataflow.infoflow.svfa;

import heros.solver.PathEdge;
import soot.Unit;
import soot.jimple.infoflow.collect.MyConcurrentHashMap;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.problems.AbstractInfoflowProblem;
import soot.jimple.infoflow.solver.executors.InterruptableExecutor;
import soot.jimple.infoflow.solver.fastSolver.InfoflowSolver;

public class InfoflowSolverRefCnt extends InfoflowSolver {
   public InfoflowSolverRefCnt(AbstractInfoflowProblem problem, InterruptableExecutor executor) {
      super(problem, executor);
   }

   public void cleanup() {
      this.jumpFunctions = new MyConcurrentHashMap();
      this.incoming.clear();
      this.endSummary.clear();
      if (this.ffCache != null) {
         this.ffCache.invalidate();
      }
   }

   public Abstraction addFunction(PathEdge<Unit, Abstraction> edge) {
      return (Abstraction)this.jumpFunctions.putIfAbsent(edge, (Abstraction)edge.factAtTarget());
   }
}
