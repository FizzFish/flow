package cn.sast.coroutines.caffeine.impl

import com.feysh.corax.cache.coroutines.*
import com.github.benmanes.caffeine.cache.stats.ConcurrentStatsCounter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

/** 整个框架唯一入口 —— 快速创建各种 Rec-Cache */
object FastCacheImpl : FastCache {

   /* ---------- 内部辅助 ---------- */

   private fun <K, V> newRecXCache(scope: CoroutineScope): XCache<K, Deferred<V>> =
      XCache.cacheBuilder<K, Deferred<V>> {

         /* -------- 基础参数 -------- */
         this.scope           = scope       // ← 关键：用 defaultScope
         weakKeys            = true
         weakValues          = true
         useCallingContext   = true

         /* -------- 统计 -------- */
         statsCounter        = ConcurrentStatsCounter()
      }


   /* ---------- FastCache 接口实现 ---------- */

   override fun <K, V> buildRecCoroutineCache(
      scope: CoroutineScope,
      weakKeyAssociateByValue: (V) -> Array<Any?>
   ): RecCoroutineCache<K, V> {
      val x = newRecXCache<K, V>(scope)
      val caffeineCache = x.build()
      return RecCoroutineCacheImpl(x, caffeineCache, weakKeyAssociateByValue)
   }

   override fun <K, V> buildRecCoroutineLoadingCache(
      scope: CoroutineScope,
      weakKeyAssociateByValue: (V) -> Array<Any?>,
      mappingFunction: suspend RecCoroutineLoadingCache<K, V>.(K) -> V
   ): RecCoroutineLoadingCache<K, V> {
      val x = newRecXCache<K, V>(scope)
      val caffeineCache = x.build()

      return RecCoroutineLoadingCacheImpl(x, x.build(), weakKeyAssociateByValue, mappingFunction)
   }
}
