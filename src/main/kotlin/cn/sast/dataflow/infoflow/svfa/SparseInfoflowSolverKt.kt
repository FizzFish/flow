@file:Suppress("UNCHECKED_CAST")

package cn.sast.dataflow.infoflow.svfa

import cn.sast.dataflow.infoflow.svfa.RefCntUnit
import kotlin.reflect.jvm.isAccessible
import soot.Unit as SootUnit
import soot.SootMethod
import soot.jimple.infoflow.collect.MyConcurrentHashMap
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/* ------------------------------------------------------------------ */
/*  1. 反射访问 activationUnitsToCallSites                            */
/* ------------------------------------------------------------------ */

/**
 * 通过反射拿到 [`AbstractInfoflowProblem.activationUnitsToCallSites`]
 *（因其通常为 `private static` 字段）。
 */
val <P : Any> P.activationUnitsToCallSites: MyConcurrentHashMap<SootUnit, Set<SootUnit>>
   get() {
      val field = javaClass.superclass
         .getDeclaredField("activationUnitsToCallSites")
         .apply { isAccessible = true }
      @Suppress("UNCHECKED_CAST")
      return field.get(this) as MyConcurrentHashMap<SootUnit, Set<SootUnit>>
   }

/* ------------------------------------------------------------------ */
/*  2. 计算 from→to 的“必经节点”集合                                   */
/* ------------------------------------------------------------------ */

fun <N, M> BiDiInterproceduralCFG<N, M>.getGoThrough(
   from: N,
   to: N,
   skipNodes: Set<N> = emptySet()
): MutableSet<N> {

   if (from == to) return mutableSetOf(from)

   val workList: Queue<RefCntUnit<N>> = LinkedList()
   val start  = RefCntUnit(from, 1)
   workList += start

   /** 已遍历过的节点 → 包装对象 */
   val visited: MutableMap<N, RefCntUnit<N>> = ConcurrentHashMap()
   visited[from] = start

   while (workList.isNotEmpty()) {
      val curPack = workList.poll()

      if (curPack.u in skipNodes) {
         curPack.dec()
         continue
      }

      for (succ in getSuccsOf(curPack.u)) {
         val pack = visited.computeIfAbsent(succ) { RefCntUnit(it, 1) }
         if (succ != to && pack === visited[succ]) {  // 首次见到
            workList += pack
         }
         pack.add(curPack)
      }
      curPack.dec()
   }

   // 过滤出“计数不为 0”的节点（即所有路径都经过的节点）
   return visited.values
      .filter { it.cnt != 0 }
      .mapTo(HashSet()) { it.u }
}

/* 默认参数合成函数（保持与原反编译接口一致） */
@Suppress("FunctionName")
fun <N, M> BiDiInterproceduralCFG<N, M>.getGoThrough$default(
from: N,
to: N,
skipNodes: Set<N>? = null
): Set<N> = getGoThrough(from, to, skipNodes ?: emptySet())

/* ------------------------------------------------------------------ */
/*  3. 以 cfg 的后继边 BFS 形成可达集合                               */
/* ------------------------------------------------------------------ */

fun getReachSet(
   icfg: BiDiInterproceduralCFG<SootUnit, SootMethod>,
   target: SootUnit
): Set<SootUnit> {
   val reachSet = HashSet<SootUnit>().apply { add(target) }
   val queue: Queue<SootUnit> = LinkedList<SootUnit>().apply { add(target) }

   while (queue.isNotEmpty()) {
      val cur = queue.poll()
      icfg.getSuccsOf(cur).forEach {
         if (reachSet.add(it)) queue.add(it)
      }
   }
   return reachSet
}
