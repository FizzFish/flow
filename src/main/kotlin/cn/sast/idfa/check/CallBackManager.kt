package cn.sast.idfa.check

import cn.sast.api.util.SootUtilsKt
import soot.SootClass
import soot.SootMethod
import soot.Unit
import soot.toolkits.graph.UnitGraph
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet
import kotlin.coroutines.Continuation

/**
 * 统一管理各种回调的注册 / 查询 / 统计。
 *
 * - 以 **方法** 或 **Unit** 为键
 * - 支持查询命中 / 未命中统计
 * - 支持替换 `UnitGraph`（当想用手写 CFG 覆盖 Soot 生成的结果时）
 */
class CallBackManager {

   private val callBacksOfMethod: MutableMap<Class<out IStmtCB>,
           MutableMap<SootMethod, MutableList<Any>>> = LinkedHashMap()

   private val callBacksOfUnit: MutableMap<Class<out IStmtCB>,
           MutableMap<Unit, MutableList<Any>>> = LinkedHashMap()

   private val jimpleOverride: MutableMap<SootMethod, UnitGraph> = LinkedHashMap()

   private val miss: MutableMap<Class<out IStmtCB>, MutableSet<SootMethod>> = LinkedHashMap()
   private val hit : MutableMap<Class<out IStmtCB>, MutableSet<Pair<SootMethod, SootMethod>>> =
      LinkedHashMap()

   /* ---------- 以  Method  为键 ---------- */

   /**
    * 注册以 **方法** 为键的回调
    * `cb` 一般是 `suspend (T) -> Any?` 之类的挂起 lambda，
    * 这里为了保持通用，用 `Any` 保存。
    */
   fun <T> put(
      cbType: Class<out IStmtCB>,
      key: SootMethod,
      cb: (T, Continuation<Unit>) -> Any?
   ) {
      val list = callBacksOfMethod
         .getOrPut(cbType) { LinkedHashMap() }
         .getOrPut(key) { ArrayList() }
      list += cb
   }

   /**
    * 查询方法回调（携带命中 / 未命中统计）
    */
   @Suppress("UNCHECKED_CAST")
   fun <T> get(
      cbType: Class<out IStmtCB>,
      method: SootMethod
   ): List<(T, Continuation<Unit>) -> Any?>? {

      val subSig = method.subSignature
      val target: SootMethod? =
         SootUtilsKt.findMethodOrNull(method.declaringClass, subSig)
            .firstOrNull { getRaw<T>(cbType, it) != null }

      if (target != null && (target == method || !target.isConcrete)) {
         val list = getRaw<T>(cbType, target)
         if (list != null) {
            hit.getOrPut(cbType) { LinkedHashSet() } += method to target
            return list
         }
      }

      miss.getOrPut(cbType) { LinkedHashSet() } += method
      return null
   }

   @Suppress("UNCHECKED_CAST")
   fun <T> getRaw(
      cbType: Class<out IStmtCB>,
      key: SootMethod
   ): List<(T, Continuation<Unit>) -> Any?>? =
      callBacksOfMethod[cbType]?.get(key) as? List<(T, Continuation<Unit>) -> Any?>

   /* ---------- 以  Unit  为键 ---------- */

   fun put(
      cbType: Class<out IStmtCB>,
      key: Unit,
      cb: (Any?) -> Unit
   ) {
      val list = callBacksOfUnit
         .getOrPut(cbType) { LinkedHashMap() }
         .getOrPut(key) { ArrayList() }
      list += cb
   }

   @Suppress("UNCHECKED_CAST")
   fun <T> get(
      cbType: Class<out IStmtCB>,
      key: Unit
   ): List<T>? = callBacksOfUnit[cbType]?.get(key) as? List<T>

   /* ---------- CFG 覆盖 ---------- */

   fun putUnitGraphOverride(
      key: SootMethod,
      override: UnitGraph
   ): UnitGraph? = jimpleOverride.put(key, override)

   fun getUnitGraphOverride(key: SootMethod): UnitGraph? = jimpleOverride[key]

   /* ---------- 统计数据 ---------- */

   /**
    * 把 “真正缺少总结方法” 的列表交给 `reportMissingMethod`
    */
   fun reportMissSummaryMethod(reportMissingMethod: (SootMethod) -> Unit) {
      val matched: Set<SootMethod> = hit.values
         .flatMap { it }
         .mapTo(LinkedHashSet()) { it.first }

      val missed: Set<SootMethod> = miss.values
         .flattenTo(LinkedHashSet())

      (missed - matched).forEach(reportMissingMethod)
   }
}
