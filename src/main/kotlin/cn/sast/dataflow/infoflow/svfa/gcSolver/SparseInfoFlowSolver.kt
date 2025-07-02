package cn.sast.dataflow.infoflow.svfa.gcSolver

import cn.sast.dataflow.infoflow.svfa.AP
import cn.sast.dataflow.infoflow.svfa.SparseInfoflowSolverKt
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
import soot.jimple.infoflow.solver.gcSolver.InfoflowSolver
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph

@SourceDebugExtension(["SMAP\nSparseInfoflowSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/gcSolver/SparseInfoFlowSolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,140:1\n1557#2:141\n1628#2,2:142\n1755#2,3:144\n1630#2:147\n1557#2:148\n1628#2,3:149\n1557#2:152\n1628#2,3:153\n*S KotlinDebug\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/gcSolver/SparseInfoFlowSolver\n*L\n92#1:141\n92#1:142,2\n100#1:144,3\n92#1:147\n108#1:148\n108#1:149,3\n111#1:152\n111#1:153,3\n*E\n"])
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


   protected open fun propagate(sourceVal: Abstraction, target: Unit, targetVal: Abstraction, relatedCallSite: Unit, isUnbalancedReturn: Boolean) {
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
         val var57: java.util.List;
         if (!targetVal.isAbstractionActive()) {
            if (this.isForward()) {
               val `$this$map$iv`: java.lang.Iterable = uses;
               val toVal: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(uses, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  val it: Unit = newAbs as Unit;
                  var toValx: Abstraction = targetVal;
                  val var53: BiDiInterproceduralCFG = this.icfg;
                  val throughUnits: java.util.Set = SparseInfoflowSolverKt.getGoThrough$default(var53, target, it, null, 4, null);
                  throughUnits.remove(it);
                  if (throughUnits.contains(targetVal.getActivationUnit())) {
                     toValx = targetVal.getActiveCopy();
                  }

                  val var54: AbstractInfoflowProblem = this.getTabulationProblem();
                  val callSites: java.util.Set = SparseInfoflowSolverKt.getActivationUnitsToCallSites(var54).get(targetVal.getActivationUnit()) as java.util.Set;
                  val var56: Boolean;
                  if (callSites == null) {
                     var56 = false;
                  } else {
                     val `$this$any$iv`: java.lang.Iterable = callSites;
                     var var55: Boolean;
                     if (callSites is java.util.Collection && (callSites as java.util.Collection).isEmpty()) {
                        var55 = false;
                     } else {
                        val var25: java.util.Iterator = `$this$any$iv`.iterator();

                        while (true) {
                           if (!var25.hasNext()) {
                              var55 = false;
                              break;
                           }

                           if (throughUnits.contains(var25.next() as Unit)) {
                              var55 = true;
                              break;
                           }
                        }
                     }

                     var56 = var55;
                  }

                  if (var56) {
                     toValx = targetVal.getActiveCopy();
                  }

                  toVal.add(TuplesKt.to(it, toValx));
               }

               var57 = toVal as java.util.List;
            } else {
               val var30: java.lang.Iterable = uses;
               val var36: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(uses, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  var36.add(TuplesKt.to(var46 as Unit, targetVal));
               }

               var57 = var36 as java.util.List;
            }
         } else {
            val var31: java.lang.Iterable = uses;
            val var37: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(uses, 10));

            for (Object item$iv$iv : $this$map$iv) {
               var37.add(TuplesKt.to(var47 as Unit, targetVal));
            }

            var57 = var37 as java.util.List;
         }

         for (Pair var35 : var57) {
            val useUnit: Unit = var35.component1() as Unit;
            val toValx: Abstraction = var35.component2() as Abstraction;
            val var41: Unit = toValx.getTurnUnit();
            if (var41 != null) {
               val var58: BiDiInterproceduralCFG = this.icfg;
               if (!SparseInfoflowSolverKt.getGoThrough(var58, target, useUnit, SetsKt.setOf(var41)).contains(useUnit)
                  && (!this.icfg.isCallStmt(useUnit) || !(sourceVal == targetVal))) {
                  continue;
               }
            }

            if (this.isBackward()) {
               val var59: UnitContainer = this.getTabulationProblem().getManager().getICFG().getDominatorOf(useUnit);
               if (var59.getUnit() != null && var59.getUnit() is IfStmt) {
                  super.propagate(
                     sourceVal as FastSolverLinkedNode,
                     useUnit,
                     toValx.deriveNewAbstractionWithDominator(var59.getUnit()) as FastSolverLinkedNode,
                     relatedCallSite,
                     isUnbalancedReturn
                  );
               }
            }

            super.propagate(sourceVal as FastSolverLinkedNode, useUnit, toValx as FastSolverLinkedNode, relatedCallSite, isUnbalancedReturn);
         }
      } else {
         super.propagate(sourceVal as FastSolverLinkedNode, target, targetVal as FastSolverLinkedNode, relatedCallSite, isUnbalancedReturn);
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
