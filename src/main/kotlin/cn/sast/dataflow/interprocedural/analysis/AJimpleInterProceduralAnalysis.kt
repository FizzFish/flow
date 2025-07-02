@file:Suppress("UNCHECKED_CAST")

package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.api.util.OthersKt
import cn.sast.idfa.analysis.*
import kotlinx.coroutines.runBlocking
import mu.KLogger
import mu.KotlinLogging
import soot.*
import soot.jimple.*
import soot.jimple.internal.JEqExpr
import soot.jimple.internal.JimpleLocal
import soot.tagkit.Tag
import soot.toolkits.graph.DirectedGraph
import java.util.concurrent.atomic.AtomicBoolean

/**
 * **Jimple** 版跨过程数据流基础框架；负责把 JVM 语义映射到堆接口。
 *
 * @param V   语义值类型（由 `AbstractHeapFactory` 决定）
 * @param CTX 运行上下文类型（`AIContext`）
 */
abstract class AJimpleInterProceduralAnalysis<
        V,
        CTX : Context<SootMethod, Unit, IFact<V>>
        >(
   val hf: AbstractHeapFactory<V>,
   val icfg: InterproceduralCFG
) : ForwardInterProceduralAnalysis<SootMethod, Unit, IFact<V>, IHeapValues<V>, CTX>() {

   /** 是否分析三方库类 */
   var analyzeLibraryClasses: Boolean = true

   /** 外部可注入额外 “是否需要分析” 逻辑 */
   var needAnalyze: ((SootMethod) -> Boolean)? = null

   /* ------------------------------------------------------------------ */
   /*  InterproceduralCFG                                                */
   /* ------------------------------------------------------------------ */

   override fun programRepresentation(): InterproceduralCFG = icfg

   /* ------------------------------------------------------------------ */
   /*  —— 抽象接口 —— 子类负责实现                                        */
   /* ------------------------------------------------------------------ */

   abstract fun newExprEnv(
      context: CTX,
      node: Unit,
      inValue: IFact<V>
   ): AnyNewExprEnv

   abstract fun resolveTargets(
      callerMethod: SootMethod,
      ie: InvokeExpr,
      node: Unit,
      inValue: IFact<V>
   ): Set<SootMethod>

   /* ------------------------------------------------------------------ */
   /*  控制点：正常流 / 调用 / 递归 / 失败 / 收敛                          */
   /* ------------------------------------------------------------------ */

   abstract suspend fun normalFlowFunction(
      context: CTX,
      node: Unit,
      succ: Unit,
      inValue: IFact<V>,
      isNegativeBranch: AtomicBoolean
   ): IFact<V>

   open fun normalFlowUnAccessibleFunction(
      ctx: CTX,
      node: Unit,
      succ: Unit,
      inValue: IFact<V>
   ): IFact<V> = inValue // 默认什么都不做

   /* 其余 call/return/widening 等钩子沿用父类默认实现或由子类覆写 */

   /* ------------------------------------------------------------------ */
   /*  CFG & analyzable 判定                                             */
   /* ------------------------------------------------------------------ */

   override fun getCfg(method: SootMethod, isAnalyzable: Boolean): DirectedGraph<Unit> =
      if (method.hasActiveBody()) icfg.getControlFlowGraph(method)
      else icfg.getSummaryControlFlowGraph(method)

   override fun isAnalyzable(callee: SootMethod, in1: IFact<V>): Boolean {
      if (!icfg.isAnalyzable(callee)) return false
      needAnalyze?.let { if (!it(callee)) return false }

      // 库调用分析开关
      if (!analyzeLibraryClasses && !callee.declaringClass.isApplicationClass) return false

      // 递归深度 / 路径敏感限幅
      val libLimit = ExtSettings.calleeDepChainMaxNumForLibClassesInInterProceduraldataFlow
      if (libLimit >= 0) {
         val cs = in1.callStack
         val libs = generateSequence(cs) { it.caller }
            .map { it.method }
            .count { !it.declaringClass.isApplicationClass }
         if (libs > libLimit) return false
      }

      // FlowDroid TAG 优先
      if (callee.tags.any { it is FlowDroidEssentialMethodTag }) return true

      return !Scene.v().isExcluded(callee.declaringClass)
   }

   /* ------------------------------------------------------------------ */
   /*  日志                                                              */
   /* ------------------------------------------------------------------ */

   companion object {
      val logger: KLogger = KotlinLogging.logger {}
   }
}
