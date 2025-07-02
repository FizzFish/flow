package cn.sast.dataflow.infoflow.svfa

import cn.sast.common.OS
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import soot.Scene
import soot.Unit
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph

/**
 * 使用 Guava 缓存 `ILocalDFA` 结果，避免重复流敏分析。
 */
class CacheFlowGuide(private val trackControlFlowDependencies: Boolean) {

   /** 根据已加载类数量推算的初始容量 */
   private val initialCapacity: Int
      get() = maxOf(Scene.v().classes.size * 10, 500)

   private val flowCacheBuilder: CacheBuilder<Any, Any> =
      CacheBuilder.newBuilder()
         .concurrencyLevel(OS.maxThreadNum)
         .initialCapacity(initialCapacity)
         .maximumSize((initialCapacity * 2).toLong())
         .softValues()

   private val cache: LoadingCache<UnitGraph, ILocalDFA> =
      flowCacheBuilder.build(object : CacheLoader<UnitGraph, ILocalDFA>() {
         override fun load(ug: UnitGraph): ILocalDFA =
            LocalVFA(ug as DirectedGraph<Unit>, trackControlFlowDependencies)
      })

   /**
    * 获取 **def-use / use-def** 邻接单位列表（根据 [isForward] 区分方向）。
    */
   fun getSuccess(
      isForward: Boolean,
      ap: AP,
      unit: Unit,
      unitGraph: UnitGraph
   ): List<Unit> {
      val vfa = cache.getUnchecked(unitGraph)
      return if (isForward) vfa.getUsesOfAt(ap, unit) else vfa.getDefUsesOfAt(ap, unit)
   }
}
