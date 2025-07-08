package cn.sast.coroutines.caffeine.impl

import com.feysh.corax.cache.coroutines.RecCoroutineLoadingCache
import com.feysh.corax.cache.coroutines.XCache
import com.github.benmanes.caffeine.cache.Cache
import kotlinx.coroutines.Deferred

/**
 * 在 [RecCoroutineCacheImpl] 基础上增加 *loading* 能力。
 */
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
