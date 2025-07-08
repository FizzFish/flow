package cn.sast.coroutines.caffeine.impl

import cn.sast.graph.NoBackEdgeDirectGraph
import com.feysh.corax.cache.coroutines.RecCoroutineCache
import com.feysh.corax.cache.coroutines.XCache
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Interner
import com.github.benmanes.caffeine.cache.stats.CacheStats
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

/**
 * 递归安全的协程缓存实现（不带 loading）。
 *
 * - **弱键、弱值**，防止内存泄漏
 * - **No-back-edge 图** 防递归
 * - 支持 *entry* 与普通结果拆分
 */

private val logger = KotlinLogging.logger {}

internal open class RecCoroutineCacheImpl<K, V> (
   private val xCache: XCache<K, Deferred<V>>,
   private val cache: Cache<K, Deferred<V>>,
   private val weakKeyAssociateByValue: (V) -> Array<Any?>
) : RecCoroutineCache<K, V> {

   /* ---------- 内部结构 ---------- */

   private val scope: CoroutineScope = xCache.defaultScope.value
   private val lruCache             = Caffeine.newBuilder()
      .initialCapacity(2_000)
      .maximumSize(10_000)
      .build<K, Deferred<V>>()

   private val interner            : Interner<K> = Interner.newWeakInterner()
   private val graph               = NoBackEdgeDirectGraph<Any>()
   private val weakHolder          = WeakEntryHolder<K, V>()
   private val recursiveIds        = ConcurrentHashMap<CoroutineContext, Any>()

   /* ---------- 公开状态 ---------- */

   override val cacheStats: CacheStats get() = cache.stats()
   override val size: Long            get() = cache.estimatedSize()

   /* ---------- 核心 API ---------- */

   @Suppress("UNCHECKED_CAST")
   override suspend fun get(
      key: K,
      mapping: suspend RecCoroutineCache<K, V>.(K) -> V
   ): Deferred<V>? {
      val canonical = interner.intern(key)
      val srcId     = currentRecursiveId() ?: Any()

      /* 缓存命中 or 异步创建 */
      val deferred = cache.get(canonical) { _ ->
         asyncCreate(srcId, canonical, mapping)
      }

      /* 递归检测 */
      val tgtId = deferred.contextRecursiveId
      if (tgtId != null && !graph.addEdgeSynchronized(srcId, tgtId)) {
         return null              // 递归冲突，返回 null
      }
      return deferred
   }

   override suspend fun getEntry(
      key: K,
      mapping: suspend RecCoroutineCache<K, V>.(K) -> V
   ): Deferred<V>? = get(key, mapping) // 简版，同 get

   override fun validateAfterFinished(): Boolean {
      val ok = graph.isComplete()
      if (!ok) logger.error { "${graph} is not complete" }
      return ok
   }

   override fun cleanUp() {
      cache.cleanUp()
      lruCache.cleanUp()
      graph.cleanUp()
      weakHolder.clean()
   }

   override suspend fun getPredSize(): Int = graph.nodeSize

   /* ---------- 实现细节 ---------- */

   private fun asyncCreate(
      srcId: Any,
      key: K,
      mapping: suspend RecCoroutineCache<K, V>.(K) -> V
   ): Deferred<V> = scope.async(RecursiveContext(srcId)) {
      val value = mapping(this@RecCoroutineCacheImpl as RecCoroutineCache<K, V>, key)
      weakHolder.put(key, coroutineContext[Job]!!)
      weakKeyAssociateByValue(value).forEach { ref -> weakHolder.put(key, ref ?: return@forEach) }
      value
   }.also { d ->
      // 当完成时从图里移除边
      d.invokeOnCompletion { graph.removeEdgeSynchronized(srcId, d.contextRecursiveId) }
   }.also { d ->
      // 放到 LRU 用于 keep-alive
      lruCache.put(key, d)
   }

   private val Deferred<*>.contextRecursiveId: Any?
      get() = recursiveIds[coroutineContext]

   private fun currentRecursiveId(): Any? =
      coroutineContext[RecursiveContext]?.id

   /* ---------- 内部 Context ---------- */

   private class RecursiveContext(val id: Any) :
      AbstractCoroutineContextElement(Key) {
      companion object Key : CoroutineContext.Key<RecursiveContext>
   }
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

