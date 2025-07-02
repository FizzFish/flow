@file:Suppress("UNCHECKED_CAST")

package cn.sast.dataflow.infoflow.svfa.gcSolver

import cn.sast.dataflow.infoflow.svfa.AP
import cn.sast.dataflow.infoflow.svfa.activationUnitsToCallSites
import cn.sast.dataflow.infoflow.svfa.getGoThrough
import cn.sast.dataflow.infoflow.svfa.getReachSet
import soot.SootMethod
import soot.Unit as SootUnit
import soot.jimple.IfStmt
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.problems.AbstractInfoflowProblem
import soot.jimple.infoflow.solver.cfg.BackwardsInfoflowCFG
import soot.jimple.infoflow.solver.executors.InterruptableExecutor
import soot.jimple.infoflow.solver.fastSolver.FastSolverLinkedNode
import soot.jimple.infoflow.solver.gcSolver.InfoflowSolver
import soot.toolkits.graph.UnitGraph

/**
 * 将 Sparse 优化接入 GC-Solver 版本。
 */
open class SparseInfoFlowSolver(
   problem: AbstractInfoflowProblem<*, *, *, *, *, *>,
   executor: InterruptableExecutor? = null
) : InfoflowSolver<Any?, Any?, Any?, Any?, Any?, Any?>(problem, executor) {

   private val sparseCache by lazy {
      CacheFlowGuide(problem.manager.config.implicitFlowMode.trackControlFlowDependencies)
   }

   private val isForward get() = icfg !is BackwardsInfoflowCFG<*, *>
   private val isBackward get() = icfg  is BackwardsInfoflowCFG<*, *>

   /* ------------------------------------------------------------------ */
   /*  关键：在此按 def-use 关系稀疏传播                                 */
   /* ------------------------------------------------------------------ */
   override fun propagate(
      sourceVal: Abstraction,
      target: SootUnit,
      targetVal: Abstraction,
      relatedCallSite: SootUnit?,
      isUnbalancedReturn: Boolean
   ) {
      // 对 zero-value 或无具体 Value 的情况直接使用父类逻辑
      if (targetVal.accessPath.plainValue == null ||
         targetVal.accessPath == (zeroValue as Abstraction).accessPath
      ) {
         super.propagate(
            sourceVal as FastSolverLinkedNode<*>,
            target,
            targetVal as FastSolverLinkedNode<*>,
            relatedCallSite,
            isUnbalancedReturn
         )
         return
      }

      /* ---------- 1. def-use 获取 ---------- */
      val method: SootMethod = icfg.getMethodOf(target)
      val unitGraph: UnitGraph = icfg.getOrCreateUnitGraph(method)
      val ap = AP[targetVal]

      val succs: List<SootUnit> =
         if (isBackward)
            sparseCache.getSuccess(false, ap, target, unitGraph)
         else
            sparseCache.getSuccess(true, ap, target, unitGraph)

      /* ---------- 2. 对每个 use 产生新的 Abstraction ---------- */
      val pairs = buildList {
         succs.forEach { use ->
            val toVal = if (!targetVal.isAbstractionActive) {
               // 判断是否需要激活
               val through = icfg.getGoThrough(target, use)
               val needActive = through.contains(targetVal.activationUnit) ||
                       problem.activationUnitsToCallSites[targetVal.activationUnit]
                          ?.any { it in through } == true
               if (needActive) targetVal.activeCopy else targetVal
            } else targetVal
            add(use to toVal)
         }
      }

      /* ---------- 3. 真正向求解器发送 ---------- */
      for ((useUnit, toVal) in pairs) {
         toVal.turnUnit?.let { turn ->
            val mustPass = icfg.getGoThrough(target, useUnit, setOf(turn))
            if (useUnit !in mustPass &&
               (icfg.isCallStmt(useUnit) && sourceVal == targetVal)
            ) continue
         }

         // backward 模式下 dominator 优化
         if (isBackward) {
            val domContainer = problem.manager.icfg.getDominatorOf(useUnit)
            domContainer.unit?.takeIf { it is IfStmt }?.let { dom ->
               super.propagate(
                  sourceVal as FastSolverLinkedNode<*>,
                  useUnit,
                  toVal.deriveNewAbstractionWithDominator(dom) as FastSolverLinkedNode<*>,
                  relatedCallSite,
                  isUnbalancedReturn
               )
               continue
            }
         }

         super.propagate(
            sourceVal as FastSolverLinkedNode<*>,
            useUnit,
            toVal as FastSolverLinkedNode<*>,
            relatedCallSite,
            isUnbalancedReturn
         )
      }
   }

   override fun toString(): String = if (isForward) "forward" else "backward"
}
