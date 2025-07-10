package cn.sast.idfa.analysis

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import soot.*
import soot.Unit
import soot.jimple.IdentityStmt
import soot.jimple.LookupSwitchStmt
import soot.jimple.Stmt
import soot.jimple.ThisRef
import soot.jimple.toolkits.ide.icfg.AbstractJimpleBasedICFG
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG
import soot.toolkits.graph.DirectedBodyGraph
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.scalar.LiveLocals
import soot.toolkits.scalar.SimpleLiveLocals
import java.util.concurrent.ConcurrentHashMap
import soot.Unit as SootUnit

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
class InterproceduralCFG : ProgramRepresentation<SootMethod, SootUnit> {

   /** 指令节点所属方法缓存（手动注入，可覆盖 Soot 默认查询以提升速度） */
   private val unitToOwner = ConcurrentHashMap<SootUnit, SootMethod>()

   /** 懒加载的 Jimple ICFG（Soot 官方实现） */
   private val delegateICFG: AbstractJimpleBasedICFG by lazy {
      JimpleBasedInterproceduralCFG(true)
   }

   /** 方法摘要图缓存 */
   private val cfgCacheSummary: LoadingCache<SootMethod, DirectedGraph<SootUnit>> =
      Caffeine.newBuilder().build { key ->
         SummaryControlFlowUnitGraph(key, this)
      }

   /** 完整 UnitGraph 缓存 */
   private val cfgCache: LoadingCache<SootMethod, DirectedGraph<SootUnit>> =
      Caffeine.newBuilder().build { key ->
         delegateICFG.getOrCreateUnitGraph(key.activeBody)
      }

   /** LiveLocals 结果缓存 */
   private val liveLocalCache: LoadingCache<DirectedBodyGraph<SootUnit>, LiveLocals> =
      Caffeine.newBuilder().build { g -> SimpleLiveLocals(g) }

   // ------------------------------------------------------------------ //
   // 对外查询 API
   // ------------------------------------------------------------------ //

   override fun getControlFlowGraph(method: SootMethod): DirectedGraph<SootUnit> =
      cfgCache[method]

   override fun getSummaryControlFlowGraph(method: SootMethod): DirectedGraph<SootUnit> =
      cfgCacheSummary[method]

   override fun isCall(node: SootUnit): Boolean =
      (node as? Stmt)?.containsInvokeExpr() ?: false

   override fun getCalleesOfCallAt(callerMethod: SootMethod, callNode: SootUnit): Set<SootMethod> =
      delegateICFG.getCalleesOfCallAt(callNode).toSet()

   fun getMethodOf(node: SootUnit): SootMethod =
      unitToOwner[node] ?: delegateICFG.getMethodOf(node)

   override fun setOwnerStatement(u: SootUnit, owner: SootMethod) {
      unitToOwner[u] = owner
   }

   override fun setOwnerStatement(units: Iterable<SootUnit>, owner: SootMethod) {
      units.forEach { unitToOwner[it] = owner }
   }

   override fun isAnalyzable(method: SootMethod): Boolean =
      method.hasActiveBody()

   fun isFallThroughSuccessor(unit: SootUnit, succ: SootUnit): Boolean =
      delegateICFG.isFallThroughSuccessor(unit, succ)

   fun isCallStmt(unit: SootUnit): Boolean =
      delegateICFG.isCallStmt(unit)

   fun getCalleesOfCallAt(unit: SootUnit): Collection<SootMethod> =
      delegateICFG.getCalleesOfCallAt(unit)

   fun getPredsOf(unit: SootUnit): List<SootUnit> =
      delegateICFG.getPredsOf(unit)

   fun hasPredAsLookupSwitchStmt(unit: SootUnit): Boolean =
      delegateICFG.getPredsOf(unit).any { it is LookupSwitchStmt }

   fun getPredAsLookupSwitchStmt(unit: SootUnit): SootUnit? =
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
   override fun isSkipCall(node: SootUnit): Boolean {
      // TODO: 按需实现更复杂的过滤逻辑
      return false
   }

   /**
    * 计算一个指令处所有 **非活跃** 的 [Local] 变量。
    */
   fun getNonLiveLocals(
      ug: DirectedBodyGraph<SootUnit>,
      unit: SootUnit
   ): List<Local> {
      val liveLocals = liveLocalCache[ug].getLiveLocalsAfter(unit)
      return unit.useBoxes
         .mapNotNull { it.value as? Local }
         .filterNot { it in liveLocals }
   }

}
