package cn.sast.dataflow.callgraph

import cn.sast.common.OS
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import soot.*
import soot.jimple.FieldRef
import soot.jimple.StaticFieldRef
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Targets
import soot.util.queue.ChunkedQueue
import soot.util.queue.QueueReader
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

class StaticFiledAccessCache(val cg: CallGraph) {

   /** 计算初始容量：类数 * 10 或 +1000，取最大 */
   private val initialCapacity: Int
      get() = Scene.v().classes.size.let { size -> maxOf(size * 10, size + 1000) }

   /** Reachability 缓存 */
   private val cache: LoadingCache<Pair<SootMethod, SootMethod>, Boolean> = CacheBuilder.newBuilder()
      .concurrencyLevel(OS.INSTANCE.maxThreadNum)
      .initialCapacity(initialCapacity)
      .maximumSize(initialCapacity * 2L)
      .softValues()
      .build(object : CacheLoader<Pair<SootMethod, SootMethod>, Boolean>() {
         override fun load(key: Pair<SootMethod, SootMethod>): Boolean {
            val (src, target) = key
            val reachable = HashSet<SootMethod>()
            val queue = ChunkedQueue<SootMethod>().also { it.add(src) }
            val reader: QueueReader<SootMethod> = queue.reader()

            while (reader.hasNext()) {
               val method = reader.next()
               val targets = Targets(cg.edgesOutOf(method as MethodOrMethodContext))
               while (targets.hasNext()) {
                  val tgtMethod = targets.next() as SootMethod
                  if (reachable.add(tgtMethod)) {
                     if (tgtMethod == target) return true
                     queue.add(tgtMethod)
                  }
               }
            }
            return false
         }
      })

   /** 每个 staticFieldRef 被哪些 SootMethod 使用 */
   private val staticFieldRefToSootMethod: Map<StaticFieldRef, Set<SootMethod>> by lazy {
      val result = LinkedHashMap<StaticFieldRef, MutableSet<SootMethod>>()
      for (sc in Scene.v().classes) {
         for (method in sc.methods) {
            if (method.hasActiveBody()) {
               for (unit in method.activeBody.units) {
                  if (unit is Stmt && unit.containsFieldRef() && unit.fieldRef is StaticFieldRef) {
                     val sf = unit.fieldRef as StaticFieldRef
                     result.computeIfAbsent(sf) { LinkedHashSet() }.add(method)
                  }
               }
            }
         }
      }
      result
   }

   /** 检查入口方法是否能访问某个 StaticFieldRef */
   fun isAccessible(entry: SootMethod, fieldRef: StaticFieldRef): Boolean {
      val methodSet = staticFieldRefToSootMethod[fieldRef] ?: return false
      return methodSet.any { callee -> cache.get(entry to callee) }
   }
}
