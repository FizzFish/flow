package cn.sast.dataflow.infoflow.svfa.gcSolver

import cn.sast.common.OS
import cn.sast.dataflow.infoflow.svfa.AP
import cn.sast.dataflow.infoflow.svfa.ILocalDFA
import cn.sast.dataflow.infoflow.svfa.LocalVFA
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import soot.Scene
import soot.Unit
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph
import kotlin.math.max

/**
 * 按方法缓存 [ILocalDFA]（def-use 图），避免重复流敏分析。
 */
class CacheFlowGuide(private val trackControlFlowDependencies: Boolean) {

   /** 依据已加载类数量估算的初始容量 */
   private val initialCapacity: Int
      get() = max(Scene.v().classes.size * 10, 500)

   /** Guava 缓存：UnitGraph → ILocalDFA */
   private val cache: LoadingCache<UnitGraph, ILocalDFA> =
      CacheBuilder.newBuilder()
         .concurrencyLevel(OS.maxThreadNum)
         .initialCapacity(initialCapacity)
         .maximumSize((initialCapacity * 2).toLong())
         .softValues()
         .build(object : CacheLoader<UnitGraph, ILocalDFA>() {
            override fun load(ug: UnitGraph): ILocalDFA =
               LocalVFA(ug as DirectedGraph<Unit>, trackControlFlowDependencies)
         })

   /**
    * 获得 **前向 / 反向** def-use 结果。
    *
    * @param isForward `true` ⇒ *use*；`false` ⇒ *def*
    */
   fun getSuccess(
      isForward: Boolean,
      ap: AP,
      unit: Unit,
      unitGraph: UnitGraph
   ): List<Unit> {
      val dfa = cache.getUnchecked(unitGraph)
      return if (isForward) dfa.getUsesOfAt(ap, unit) else dfa.getDefUsesOfAt(ap, unit)
   }
}
