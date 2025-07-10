package cn.sast.coroutines.caffeine.impl

import cn.sast.graph.NoBackEdgeDirectGraph
import com.feysh.corax.cache.coroutines.RecCoroutineCache
import com.feysh.corax.cache.coroutines.RecCoroutineLoadingCache
import com.feysh.corax.cache.coroutines.XCache
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Interner
import com.github.benmanes.caffeine.cache.stats.CacheStats
import kotlinx.coroutines.*
import mu.KLogger
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt
import kotlin.jvm.internal.Ref

/**
 * 递归安全的协程缓存实现（不带 loading）。
 *
 * - **弱键、弱值**，防止内存泄漏
 * - **No-back-edge 图** 防递归
 * - 支持 *entry* 与普通结果拆分
 */
internal open class RecCoroutineCacheImpl<K, V>(
   private val xCache: XCache<K, Deferred<V>>,
   private val cache: Cache<K, Deferred<V>>,
   private val weakKeyAssociateByValue: (V) -> Array<Any?>
) : RecCoroutineCache<K, V> {

   private val lruCache: Cache<K, Deferred<V>> =
      Caffeine.newBuilder().initialCapacity(2000).maximumSize(10000).build()

   private val scope: CoroutineScope = xCache.defaultScope.value
   private val interner: Interner<K> = Interner.newWeakInterner()
   private val noBackEdgeDirectGraph = NoBackEdgeDirectGraph<Any>()
   private val weakHolder = WeakEntryHolder<K, Any>()

   private var _x = 0

   companion object {
      private val logger: KLogger = KotlinLogging.logger {}
   }

   private fun CoroutineContext.recursiveId(): Any {
      val ctx = this[RecursiveContext] ?: error("No RecursiveContext")
      return ctx.id
   }
   open class RecID {
      override fun toString(): String = "RecID-${System.identityHashCode(this)}"
   }

   class EntryRecID : RecID() {
      override fun toString(): String = "Entry-${super.toString()}"
   }
   private class RecursiveContext(val id: Any) :
      AbstractCoroutineContextElement(Key) {
      companion object Key : CoroutineContext.Key<RecursiveContext>
   }

   /* ---------- 公开状态 ---------- */

   override val cacheStats: CacheStats get() = cache.stats()
   override val size: Long            get() = cache.estimatedSize()

   /* ---------- 核心 API ---------- */

   override suspend fun get(
      key: K,
      mappingFunction: suspend RecCoroutineCache<K, V>.(K) -> V
   ): Deferred<V> {
      val parentJob = coroutineContext[Job] ?: SupervisorJob()
      val src = RecID()

      return get(src, parentJob, key, mappingFunction)
   }

   override suspend fun getEntry(
      key: K,
      mappingFunction: suspend RecCoroutineCache<K, V>.(K) -> V
   ): Deferred<V> {
      val parentJob = coroutineContext[Job] ?: SupervisorJob()
      val src = EntryRecID()

      return get(src, parentJob, key, mappingFunction)
   }

   private fun get(
      src: Any,
      parentJob: Job,
      key: K,
      mappingFunction: suspend RecCoroutineCache<K, V>.(K) -> V
   ): Deferred<V> {
      // 对 key 去重，保证同一个 key 只对应一个 entry
      val canonicalKey = interner.intern(key)
      val mapped = kotlin.jvm.internal.Ref.BooleanRef().apply { element = false }

      // 创建 Deferred (可能命中 cache，可能触发 mapping)
      var createdContext: CoroutineContext? = null

      val deferred = cache.get(canonicalKey) {
         if (mapped.element) error("Already mapped once.")
         mapped.element = true

         // 为这次执行分配唯一 ID
         val id = RecID()

         // 新建边：父节点 src -> 当前新建 id
         if (!noBackEdgeDirectGraph.addEdgeSynchronized(src, id)) {
            error("Cycle detected!")
         }

         // 创建包含 RecursiveContext + 父 Job 的上下文
         val ctx = RecursiveContext(id) + parentJob
         createdContext = ctx

         // 真正异步执行
         scope.async(ctx) {
            val value = mappingFunction(this@RecCoroutineCacheImpl, key)

            // 把当前 Job 和 value 里关联到的弱引用放进 holder
            weakHolder.put(key, coroutineContext[Job]!!)
            weakKeyAssociateByValue(value).forEach { ref ->
               ref?.let { weakHolder.put(key, it) }
            }

            value
         }.also { d ->
            // 当完成时，从图里移除边
            d.invokeOnCompletion {
               noBackEdgeDirectGraph.removeEdgeSynchronized(src, id)
            }
            // 放到 LRU 缓存里
            lruCache.put(key, d)
         }
      }

      // ✅ 不再从 deferred 反查上下文，而是直接用刚才构造的
      val tgt = createdContext?.recursiveId()
         ?: error("Created context is null — this should not happen.")

      // 如果 deferred 是从 cache 命中的，并且还活跃，那么再尝试建边
      if (!mapped.element && deferred.isActive) {
         if (!noBackEdgeDirectGraph.addEdgeSynchronized(src, tgt)) {
            return deferred
         }
      }

      // 完成时：更新一些 debug 索引 + 从图里移除边
      deferred.invokeOnCompletion {
         _x += System.identityHashCode(deferred) + System.identityHashCode(canonicalKey)
         noBackEdgeDirectGraph.removeEdgeSynchronized(src, tgt)
      }

      return deferred
   }


   override fun validateAfterFinished(): Boolean {
      val ok = noBackEdgeDirectGraph.isComplete
      if (!ok) logger.error { "${noBackEdgeDirectGraph} is not complete" }
      return ok
   }

   override suspend fun getPredSize(): Int {
      val src = coroutineContext.recursiveId()
      return noBackEdgeDirectGraph.getPredSize(src)
   }

   override fun cleanUp() {
      cache.cleanUp()
      lruCache.cleanUp()
      noBackEdgeDirectGraph.cleanUp()
      weakHolder.clean()
   }
}

internal class RecCoroutineLoadingCacheImpl<K, V>(
   xCache: XCache<K, Deferred<V>>,
   cache: Cache<K, Deferred<V>>,
   weakKeyAssociateByValue: (V) -> Array<Any?>,
   private val mapping: suspend RecCoroutineLoadingCache<K, V>.(K) -> V
) : RecCoroutineCacheImpl<K, V>(xCache, cache, weakKeyAssociateByValue),
   RecCoroutineLoadingCache<K, V> {

   override suspend fun get(key: K): Deferred<V>? =
      super.get(key) { mapping(this@RecCoroutineLoadingCacheImpl, it) }

   override suspend fun getEntry(key: K): Deferred<V>? =
      super.getEntry(key) { mapping(this@RecCoroutineLoadingCacheImpl, it) }
}


/** 人类可读格式 */
fun CacheStats.pp(): String = buildString {
   fun Double.f() = (this * 100).roundToInt() / 100.0
   append("hit:${hitRate().f()} ")
   append("miss:${missRate().f()} ")
   append("penalty:${averageLoadPenalty().f()} ")
   append("failure:${loadFailureRate().f()} ")
   append(this@pp)
}


