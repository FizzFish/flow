@file:Suppress("UNCHECKED_CAST", "UNUSED_VARIABLE")

package cn.sast.dataflow.infoflow.svfa

import cn.sast.dataflow.infoflow.svfa.activationUnitsToCallSites
import soot.SootMethod
import soot.Unit as SootUnit
import soot.jimple.IfStmt
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.problems.AbstractInfoflowProblem
import soot.jimple.infoflow.solver.cfg.BackwardsInfoflowCFG
import soot.jimple.infoflow.solver.executors.InterruptableExecutor
import soot.jimple.infoflow.solver.fastSolver.FastSolverLinkedNode
import soot.jimple.infoflow.solver.fastSolver.InfoflowSolver
import soot.jimple.infoflow.solver.fastSolver.IFDSSolver.ScheduleTarget
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG
import soot.toolkits.graph.UnitGraph

/**
 * 在标准 InfoflowSolver 基础上引入 **Sparse** 优化：
 * - 使用 [CacheFlowGuide] 加速 def-use 查询
 * - 对前向 / 后向分析分别处理
 */
open class SparseInfoFlowSolver(
   problem: AbstractInfoflowProblem<*, *, *, *, *, *>,
   executor: InterruptableExecutor? = null
) : InfoflowSolver<Any?, Any?, Any?, Any?, Any?, Any?>(problem, executor) {

   /** def-use 缓存（按配置决定是否跟踪控制依赖） */
   private val sparseCache by lazy {
      CacheFlowGuide(
         problem.manager.config.implicitFlowMode.trackControlFlowDependencies
      )
   }

   private val isForward get() = icfg !is BackwardsInfoflowCFG<*, *>
   private val isBackward get() = icfg  is BackwardsInfoflowCFG<*, *>

   /* ------------------------------------------------------------------ */
   /*  重写核心 propagate                                                  */
   /* ------------------------------------------------------------------ */
   override fun propagate(
      sourceVal: Abstraction,
      target: SootUnit,
      targetVal: Abstraction,
      relatedCallSite: SootUnit?,
      isUnbalancedReturn: Boolean,
      scheduleTarget: ScheduleTarget?
   ) {
      // 若目标是 zero-value 或 plainValue 为 null，直接走父类
      if (targetVal.accessPath.plainValue == null ||
         targetVal.accessPath == (zeroValue as Abstraction).accessPath
      ) {
         super.propagate(
            sourceVal as FastSolverLinkedNode<*>,
            target,
            targetVal as FastSolverLinkedNode<*>,
            relatedCallSite,
            isUnbalancedReturn,
            scheduleTarget
         )
         return
      }

      /* ---------- 1. 取 def-use 关系 ---------- */
      val method: SootMethod = icfg.getMethodOf(target)
      val unitGraph: UnitGraph = icfg.getOrCreateUnitGraph(method)

      val ap      = AP[targetVal]
      val useSet: Set<SootUnit> =
         if (isBackward)
            sparseCache.getSuccess(false, ap, target, unitGraph)
         else
            sparseCache.getSuccess(true, ap, target, unitGraph)

      /* ---------- 2. 生成 (useUnit, toVal) 列表 ---------- */
      val pairs = ArrayList<Pair<SootUnit, Abstraction>>()

      if (!targetVal.isAbstractionActive) {
         useSet.forEach { useUnit ->
            var toVal = targetVal
            // 路径 / Dominator / Activation 处理
            val through = icfg.getGoThrough(target, useUnit)
               .apply { remove(useUnit) }

            if (through.contains(targetVal.activationUnit)) {
               toVal = targetVal.activeCopy
            }

            val callSites =
               problem.activationUnitsToCallSites[targetVal.activationUnit] ?: emptySet()

            if (callSites.any { it in through }) {
               toVal = targetVal.activeCopy
            }
            pairs += useUnit to toVal
         }
      } else {
         useSet.forEach { pairs += it to targetVal }
      }

      /* ---------- 3. 真正向流程图中插入边 ---------- */
      for ((useUnit, toVal) in pairs) {

         // 若 turnUnit 被挡路，则跳过
         toVal.turnUnit?.let { turn ->
            val mustPass = icfg.getGoThrough(target, useUnit, setOf(turn))
            if (useUnit !in mustPass && (icfg.isCallStmt(useUnit) && sourceVal == targetVal))
               return@for
         }

         // 回向分析时带 dominator 优化
         if (isBackward) {
            icfg.getDominatorOf(useUnit).unit?.takeIf { it is IfStmt }?.let { domUnit ->
               super.propagate(
                  sourceVal as FastSolverLinkedNode<*>,
                  useUnit,
                  toVal.deriveNewAbstractionWithDominator(domUnit)
                          as FastSolverLinkedNode<*>,
                  relatedCallSite,
                  isUnbalancedReturn,
                  scheduleTarget
               )
               return@for
            }
         }

         // 默认分支
         super.propagate(
            sourceVal as FastSolverLinkedNode<*>,
            useUnit,
            toVal as FastSolverLinkedNode<*>,
            relatedCallSite,
            isUnbalancedReturn,
            scheduleTarget
         )
      }
   }

   override fun toString(): String = if (isForward) "forward" else "backward"
}
