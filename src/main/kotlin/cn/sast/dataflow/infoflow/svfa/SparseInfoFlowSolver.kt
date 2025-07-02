package cn.sast.dataflow.infoflow.svfa

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootMethod
import soot.Unit
import soot.jimple.IfStmt
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.problems.AbstractInfoflowProblem
import soot.jimple.infoflow.solver.cfg.BackwardsInfoflowCFG
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG.UnitContainer
import soot.jimple.infoflow.solver.executors.InterruptableExecutor
import soot.jimple.infoflow.solver.fastSolver.FastSolverLinkedNode
import soot.jimple.infoflow.solver.fastSolver.InfoflowSolver
import soot.jimple.infoflow.solver.fastSolver.IFDSSolver.ScheduleTarget
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph

@SourceDebugExtension(["SMAP\nSparseInfoflowSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/SparseInfoFlowSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,232:1\n1557#2:233\n1628#2,2:234\n1755#2,3:236\n1630#2:239\n1557#2:240\n1628#2,3:241\n1557#2:244\n1628#2,3:245\n*S KotlinDebug\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/SparseInfoFlowSolver\n*L\n184#1:233\n184#1:234,2\n192#1:236,3\n184#1:239\n200#1:240\n200#1:241,3\n203#1:244\n203#1:245,3\n*E\n"])
public open class SparseInfoFlowSolver(problem: AbstractInfoflowProblem, executor: InterruptableExecutor?) : InfoflowSolver(problem, executor) {
   private final val sparseCache: CacheFlowGuide by LazyKt.lazy(SparseInfoFlowSolver::sparseCache_delegate$lambda$0)
      private final get() {
         return this.sparseCache$delegate.getValue() as CacheFlowGuide;
      }


   private final val isForward: Boolean
      private final get() {
         return this.icfg !is BackwardsInfoflowCFG;
      }


   private final val isBackward: Boolean
      private final get() {
         return this.icfg is BackwardsInfoflowCFG;
      }


   protected open fun propagate(
      sourceVal: Abstraction,
      target: Unit,
      targetVal: Abstraction,
      relatedCallSite: Unit?,
      isUnbalancedReturn: Boolean,
      scheduleTarget: ScheduleTarget
   ) {
      if (targetVal.getAccessPath().getPlainValue() != null && !(targetVal.getAccessPath() == (this.zeroValue as Abstraction).getAccessPath())) {
         val var10000: DirectedGraph = this.icfg.getOrCreateUnitGraph(this.icfg.getMethodOf(target) as SootMethod);
         val unitGraph: UnitGraph = var10000 as UnitGraph;
         val ap: AP = AP.Companion.get(targetVal);
         val uses: java.util.Set = CollectionsKt.toSet(
            if (this.icfg is BackwardsInfoflowCFG)
               this.getSparseCache().getSuccess(false, ap, target, unitGraph)
               else
               this.getSparseCache().getSuccess(true, ap, target, unitGraph)
         );
         val var58: java.util.List;
         if (!targetVal.isAbstractionActive()) {
            if (this.isForward()) {
               val `$this$map$iv`: java.lang.Iterable = uses;
               val toVal: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(uses, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  val it: Unit = newAbs as Unit;
                  var toValx: Abstraction = targetVal;
                  val var54: BiDiInterproceduralCFG = this.icfg;
                  val throughUnits: java.util.Set = SparseInfoflowSolverKt.getGoThrough$default(var54, target, it, null, 4, null);
                  throughUnits.remove(it);
                  if (throughUnits.contains(targetVal.getActivationUnit())) {
                     toValx = targetVal.getActiveCopy();
                  }

                  val var55: AbstractInfoflowProblem = this.getTabulationProblem();
                  val callSites: java.util.Set = SparseInfoflowSolverKt.getActivationUnitsToCallSites(var55).get(targetVal.getActivationUnit()) as java.util.Set;
                  val var57: Boolean;
                  if (callSites == null) {
                     var57 = false;
                  } else {
                     val `$this$any$iv`: java.lang.Iterable = callSites;
                     var var56: Boolean;
                     if (callSites is java.util.Collection && (callSites as java.util.Collection).isEmpty()) {
                        var56 = false;
                     } else {
                        val var26: java.util.Iterator = `$this$any$iv`.iterator();

                        while (true) {
                           if (!var26.hasNext()) {
                              var56 = false;
                              break;
                           }

                           if (throughUnits.contains(var26.next() as Unit)) {
                              var56 = true;
                              break;
                           }
                        }
                     }

                     var57 = var56;
                  }

                  if (var57) {
                     toValx = targetVal.getActiveCopy();
                  }

                  toVal.add(TuplesKt.to(it, toValx));
               }

               var58 = toVal as java.util.List;
            } else {
               val var31: java.lang.Iterable = uses;
               val var37: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(uses, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  var37.add(TuplesKt.to(var47 as Unit, targetVal));
               }

               var58 = var37 as java.util.List;
            }
         } else {
            val var32: java.lang.Iterable = uses;
            val var38: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(uses, 10));

            for (Object item$iv$iv : $this$map$iv) {
               var38.add(TuplesKt.to(var48 as Unit, targetVal));
            }

            var58 = var38 as java.util.List;
         }

         for (Pair var36 : var58) {
            val useUnit: Unit = var36.component1() as Unit;
            val toValx: Abstraction = var36.component2() as Abstraction;
            val var42: Unit = toValx.getTurnUnit();
            if (var42 != null) {
               val var59: BiDiInterproceduralCFG = this.icfg;
               if (!SparseInfoflowSolverKt.getGoThrough(var59, target, useUnit, SetsKt.setOf(var42)).contains(useUnit)
                  && (!this.icfg.isCallStmt(useUnit) || !(sourceVal == targetVal))) {
                  continue;
               }
            }

            if (this.isBackward()) {
               val var60: UnitContainer = this.getTabulationProblem().getManager().getICFG().getDominatorOf(useUnit);
               if (var60.getUnit() != null && var60.getUnit() is IfStmt) {
                  super.propagate(
                     sourceVal as FastSolverLinkedNode,
                     useUnit,
                     toValx.deriveNewAbstractionWithDominator(var60.getUnit()) as FastSolverLinkedNode,
                     relatedCallSite,
                     isUnbalancedReturn,
                     scheduleTarget
                  );
               }
            }

            super.propagate(sourceVal as FastSolverLinkedNode, useUnit, toValx as FastSolverLinkedNode, relatedCallSite, isUnbalancedReturn, scheduleTarget);
         }
      } else {
         super.propagate(sourceVal as FastSolverLinkedNode, target, targetVal as FastSolverLinkedNode, relatedCallSite, isUnbalancedReturn, scheduleTarget);
      }
   }

   public open fun toString(): String {
      return if (this.isForward()) "forward" else "backward";
   }

   @JvmStatic
   fun `sparseCache_delegate$lambda$0`(`this$0`: SparseInfoFlowSolver): CacheFlowGuide {
      return new CacheFlowGuide(`this$0`.getTabulationProblem().getManager().getConfig().getImplicitFlowMode().trackControlFlowDependencies());
   }
}
