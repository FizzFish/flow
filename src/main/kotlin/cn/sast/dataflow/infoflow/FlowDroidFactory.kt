package cn.sast.dataflow.infoflow

import cn.sast.dataflow.infoflow.svfa.SparseInfoFlowSolver
import mu.KLogger
import mu.KotlinLogging
import soot.SootMethod
import soot.jimple.infoflow.*
import soot.jimple.infoflow.InfoflowConfiguration.*
import soot.jimple.infoflow.cfg.BiDirICFGFactory
import soot.jimple.infoflow.problems.*
import soot.jimple.infoflow.solver.IInfoflowSolver
import soot.jimple.infoflow.solver.executors.InterruptableExecutor
import java.io.File

/**
 * 为项目统一创建 Forward / Backward 信息流实例，并可选启用 Sparse 优化。
 */
object FlowDroidFactory {

   private val logger: KLogger = KotlinLogging.logger {}

   @JvmStatic
   fun createInfoFlow(
      dataFlowDirection: InfoflowConfiguration.DataFlowDirection,
      androidPlatformDir: String? = null,
      forceAndroidJar: Boolean? = null,
      lifecycleMethods: Collection<SootMethod>? = null,
      cfgFactory: BiDirICFGFactory? = null,
      useSparseOpt: Boolean = true,
      resultAddedHandlers: Set<TaintPropagationResults.OnTaintPropagationResultAdded> = emptySet()
   ): AbstractInfoflow {

      /* ---------- 参数合法性检查 ---------- */
      require((androidPlatformDir == null) == (forceAndroidJar == null)) {
         "androidPlatformDir 与 forceAndroidJar 必须同时给出或同时不给"
      }
      androidPlatformDir?.let {
         val ok = if (forceAndroidJar == true)
            it.endsWith(".jar", ignoreCase = true)
         else File(it).isDirectory
         require(ok) { "非法的 androidPlatformDir: $it 与 forceAndroidJar: $forceAndroidJar" }
      }

      /* ---------- 创建 Forward / Backward 基类 ---------- */
      return when (dataFlowDirection) {
         InfoflowConfiguration.DataFlowDirection.Forwards ->
            object : Infoflow(
               lifecycleMethods, androidPlatformDir, cfgFactory,
               resultAddedHandlers, useSparseOpt, forceAndroidJar == true
            ) {
               override fun createInfoflowProblem(zeroValue: Abstraction): InfoflowProblem {
                  return super.createInfoflowProblem(zeroValue)
                     .also { p -> resultAddedHandlers.forEach(p.results::addResultAvailableHandler) }
               }

               override fun createDataFlowSolver(
                  executor: InterruptableExecutor?,
                  problem: AbstractInfoflowProblem,
                  solverConfig: InfoflowConfiguration.SolverConfiguration
               ): IInfoflowSolver {
                  if (!useSparseOpt) {
                     logger.info { "Using forward solver with no sparse opt" }
                     return super.createDataFlowSolver(executor, problem, solverConfig)
                  }

                  return when (solverConfig.dataFlowSolver) {
                     DataFlowSolver.ContextAndFlowSensitive -> {
                        logger.info { "Using context- and flow-sensitive solver with sparse opt" }
                        SparseInfoFlowSolver(problem, executor)
                     }

                     DataFlowSolver.GarbageCollecting -> {
                        logger.info { "Using garbage-collecting solver with sparse opt" }
                        cn.sast.dataflow.infoflow.svfa.gcSolver.SparseInfoFlowSolver(problem, executor)
                           .also { solverPeerGroup.addSolver(it); it.peerGroup = solverPeerGroup }
                     }

                     else -> error("Sparse opt not yet supported for ${solverConfig.dataFlowSolver}")
                  }
               }
            }

         InfoflowConfiguration.DataFlowDirection.Backwards ->
            object : BackwardsInfoflow(
               lifecycleMethods, androidPlatformDir, cfgFactory,
               resultAddedHandlers, useSparseOpt, forceAndroidJar == true
            ) {
               override fun createInfoflowProblem(zeroValue: Abstraction): BackwardsInfoflowProblem {
                  return super.createInfoflowProblem(zeroValue)
                     .also { p -> resultAddedHandlers.forEach(p.results::addResultAvailableHandler) }
               }

               override fun createDataFlowSolver(
                  executor: InterruptableExecutor?,
                  problem: AbstractInfoflowProblem,
                  solverConfig: InfoflowConfiguration.SolverConfiguration
               ): IInfoflowSolver {
                  if (!useSparseOpt) {
                     logger.info { "Using backward solver with no sparse opt" }
                     return super.createDataFlowSolver(executor, problem, solverConfig)
                  }

                  return when (solverConfig.dataFlowSolver) {
                     DataFlowSolver.ContextAndFlowSensitive -> {
                        logger.info { "Using context- and flow-sensitive solver with sparse opt" }
                        SparseInfoFlowSolver(problem, executor)
                     }

                     DataFlowSolver.GarbageCollecting -> {
                        logger.info { "Using garbage-collecting solver with sparse opt" }
                        cn.sast.dataflow.infoflow.svfa.gcSolver.SparseInfoFlowSolver(problem, executor)
                           .also { solverPeerGroup.addSolver(it); it.peerGroup = solverPeerGroup }
                     }

                     else -> error("Sparse opt not yet supported for ${solverConfig.dataFlowSolver}")
                  }
               }
            }
      }
   }
}
