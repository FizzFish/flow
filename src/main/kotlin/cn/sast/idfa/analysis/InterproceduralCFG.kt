package cn.sast.idfa.analysis

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import soot.*
import soot.jimple.IdentityStmt
import soot.jimple.LookupSwitchStmt
import soot.jimple.ThisRef
import soot.jimple.toolkits.ide.icfg.AbstractJimpleBasedICFG
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG
import soot.toolkits.graph.DirectedBodyGraph
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.LiveLocals
import soot.toolkits.scalar.SimpleLiveLocals
import java.util.concurrent.ConcurrentHashMap

/**
 * 基于 Soot 的跨过程控制流图 (ICFG) 轻量封装，
 * 负责缓存 UnitGraph / SummaryGraph，并提供常用查询辅助。
 *
 * - **cfgCacheSummary**：方法级摘要图
 * - **cfgCache**：完整版 UnitGraph
 * - **liveLocalCache**：`LiveLocals` 结果缓存
 *
 * @author fizz
 */
class InterproceduralCFG : ProgramRepresentation<SootMethod, Unit> {

   /** 指令节点所属方法缓存（手动注入，可覆盖 Soot 默认查询以提升速度） */
   private val unitToOwner = ConcurrentHashMap<Unit, SootMethod>()

   /** 懒加载的 Jimple ICFG（Soot 官方实现） */
   private val delegateICFG: AbstractJimpleBasedICFG by lazy {
      JimpleBasedInterproceduralCFG(true)
   }

   /** 方法摘要图缓存 */
   private val cfgCacheSummary: LoadingCache<SootMethod, DirectedGraph<Unit>> =
      Caffeine.newBuilder().build { key ->
         SummaryControlFlowUnitGraph(key, this)
      }

   /** 完整 UnitGraph 缓存 */
   private val cfgCache: LoadingCache<SootMethod, DirectedGraph<Unit>> =
      Caffeine.newBuilder().build { key ->
         delegateICFG.getOrCreateUnitGraph(key.activeBody)
      }

   /** LiveLocals 结果缓存 */
   private val liveLocalCache: LoadingCache<DirectedBodyGraph<Unit>, LiveLocals> =
      Caffeine.newBuilder().build { g -> SimpleLiveLocals(g) }

   // ------------------------------------------------------------------ //
   // 对外查询 API
   // ------------------------------------------------------------------ //

   fun getControlFlowGraph(method: SootMethod): DirectedGraph<Unit> =
      cfgCache[method]

   fun getSummaryControlFlowGraph(method: SootMethod): DirectedGraph<Unit> =
      cfgCacheSummary[method]

   fun isCall(node: Unit): Boolean =
      (node as? Stmt)?.containsInvokeExpr() ?: false

   fun getCalleesOfCallAt(callerMethod: SootMethod, callNode: Unit): Set<SootMethod> =
      delegateICFG.getCalleesOfCallAt(callNode).toSet()

   fun getMethodOf(node: Unit): SootMethod =
      unitToOwner[node] ?: delegateICFG.getMethodOf(node)

   fun setOwnerStatement(u: Unit, owner: SootMethod) {
      unitToOwner[u] = owner
   }

   fun setOwnerStatement(units: Iterable<Unit>, owner: SootMethod) {
      units.forEach { unitToOwner[it] = owner }
   }

   fun isAnalyzable(method: SootMethod): Boolean =
      method.hasActiveBody()

   fun isFallThroughSuccessor(unit: Unit, succ: Unit): Boolean =
      delegateICFG.isFallThroughSuccessor(unit, succ)

   fun isCallStmt(unit: Unit): Boolean =
      delegateICFG.isCallStmt(unit)

   fun getCalleesOfCallAt(unit: Unit): Collection<SootMethod> =
      delegateICFG.getCalleesOfCallAt(unit)

   fun getPredsOf(unit: Unit): List<Unit> =
      delegateICFG.getPredsOf(unit)

   fun hasPredAsLookupSwitchStmt(unit: Unit): Boolean =
      delegateICFG.getPredsOf(unit).any { it is LookupSwitchStmt }

   fun getPredAsLookupSwitchStmt(unit: Unit): Unit? =
      delegateICFG.getPredsOf(unit).firstOrNull { it is LookupSwitchStmt }

   /**
    * 返回方法首条 `this := @this` 形式的 [IdentityStmt]。
    * 若未找到则抛异常。
    */
   fun getIdentityStmt(method: SootMethod): IdentityStmt =
      method.activeBody.units
         .filterIsInstance<IdentityStmt>()
         .firstOrNull { it.rightOp is ThisRef }
         ?: error("Couldn't find identity ref in $method")

   /**
    * 判断调用是否应被跳过（可按项目需要扩展）。
    */
   fun isSkipCall(node: Unit): Boolean {
      // TODO: 按需实现更复杂的过滤逻辑
      return false
   }

   /**
    * 计算一个指令处所有 **非活跃** 的 [Local] 变量。
    */
   fun getNonLiveLocals(
      ug: DirectedBodyGraph<Unit>,
      unit: Unit
   ): List<Local> {
      val liveLocals = liveLocalCache[ug].getLiveLocalsAfter(unit)
      return unit.useBoxes
         .mapNotNull { it.value as? Local }
         .filterNot { it in liveLocals }
   }
}
