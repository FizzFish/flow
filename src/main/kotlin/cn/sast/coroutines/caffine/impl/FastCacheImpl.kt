package cn.sast.coroutines.caffine.impl

import com.feysh.corax.cache.coroutines.Configuration
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.cache.coroutines.RecCoroutineCache
import com.feysh.corax.cache.coroutines.RecCoroutineLoadingCache
import com.feysh.corax.cache.coroutines.XCache
import com.github.benmanes.caffeine.cache.stats.ConcurrentStatsCounter
import com.github.benmanes.caffeine.cache.stats.StatsCounter
import kotlin.coroutines.Continuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

public object FastCacheImpl : FastCache {
   private fun <K, V> newRecXCache(scope: CoroutineScope): XCache<K, Deferred<V>> {
      return XCache.Companion.cacheBuilder(FastCacheImpl::newRecXCache$lambda$0);
   }

   public override fun <K, V> buildRecCoroutineCache(scope: CoroutineScope, weakKeyAssociateByValue: (V) -> Array<Any?>): RecCoroutineCache<K, V> {
      val xCache: XCache = this.newRecXCache(scope);
      return new RecCoroutineCacheImpl(xCache, xCache.build(), weakKeyAssociateByValue);
   }

   public override fun <K, V> buildRecCoroutineLoadingCache(
      scope: CoroutineScope,
      weakKeyAssociateByValue: (V) -> Array<Any?>,
      mappingFunction: (RecCoroutineLoadingCache<K, V>, K, Continuation<V>) -> Any?
   ): RecCoroutineLoadingCache<K, V> {
      val xCache: XCache = this.newRecXCache(scope);
      return new RecCoroutineLoadingCacheImpl(xCache, xCache.build(), weakKeyAssociateByValue, mappingFunction);
   }

   @JvmStatic
   fun `newRecXCache$lambda$0`(`$scope`: CoroutineScope, `$this$cacheBuilder`: Configuration): Unit {
      `$this$cacheBuilder`.setScope(`$scope`);
      `$this$cacheBuilder`.setWeakKeys(true);
      `$this$cacheBuilder`.setWeakValues(true);
      `$this$cacheBuilder`.setUseCallingContext(true);
      `$this$cacheBuilder`.setStatsCounter((new ConcurrentStatsCounter()) as StatsCounter);
      return Unit.INSTANCE;
   }
}
