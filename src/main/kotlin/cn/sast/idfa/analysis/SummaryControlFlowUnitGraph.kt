package cn.sast.idfa.analysis

import soot.*
import soot.jimple.*
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.ExceptionalUnitGraphFactory
import soot.toolkits.graph.UnitGraph

/**
 * 给 **无方法体** 的 phantom-method 动态生成最小可用的 CFG，
 * 结构固定为：`new Error -> <init> -> throw`.
 *
 * 生成后立即交由 [InterproceduralCFG] 记录 owner 信息。
 */
class SummaryControlFlowUnitGraph(
   val method: SootMethod,
   private val icfg: InterproceduralCFG,
) : DirectedGraph<Unit> {

   private val j    = Jimple.v()
   private val body = j.newBody(method)
   private val graph: UnitGraph

   init {
      require(!method.hasActiveBody()) { "$method hasActiveBody" }

      /* 1. 构造简单的异常抛出方法体 */
      val units = body.units
      val lg    = Scene.v().createLocalGenerator(body)
      val errTy = RefType.v("java.lang.Error")
      val exLoc = lg.generateLocal(errTy)

      val assign = j.newAssignStmt(exLoc, j.newNewExpr(errTy))
      units.add(assign)

      val init = errTy.sootClass.getMethod("<init>", listOf(RefType.v("java.lang.String")))
      val invoke = j.newInvokeStmt(
         j.newSpecialInvokeExpr(exLoc, init.makeRef(), StringConstant.v("phantom method body"))
      )
      units.insertAfter(invoke, assign)
      units.insertAfter(j.newThrowStmt(exLoc), invoke)

      /* 2. 告诉 ICFG 这些 Unit 属于哪一个方法 */
      body.units.forEach { icfg.setOwnerStatement(it, method) }

      /* 3. 用 Soot 工厂创建可处理异常边的 CFG */
      graph = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body)
   }

   /* ---------------- DirectedGraph 接口简单委托 ---------------- */

   override fun iterator(): MutableIterator<Unit> = graph.iterator()
   override fun getHeads(): List<Unit>          = graph.heads
   override fun getTails(): List<Unit>          = graph.tails
   override fun getPredsOf(s: Unit): List<Unit> = graph.getPredsOf(s)
   override fun getSuccsOf(s: Unit): List<Unit> = graph.getSuccsOf(s)
   override fun size(): Int                     = graph.size
}
