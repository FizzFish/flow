package cn.sast.coroutines.caffine.impl

import com.feysh.corax.cache.coroutines.RecCoroutineCache
import com.feysh.corax.cache.coroutines.RecCoroutineLoadingCache
import com.feysh.corax.cache.coroutines.XCache
import com.github.benmanes.caffeine.cache.Cache
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlinx.coroutines.Deferred

internal class RecCoroutineLoadingCacheImpl<K, V>(xCache: XCache<Any, Deferred<Any>>,
      cache: Cache<Any, Deferred<Any>>,
      weakKeyAssociateBy: (Any) -> Array<Any?>,
      mappingFunction: (RecCoroutineLoadingCache<Any, Any>, Any, Continuation<Any>) -> Any?
   ) : RecCoroutineCacheImpl(xCache, cache, weakKeyAssociateBy),
   RecCoroutineLoadingCache<K, V> {
   private final val mappingFunction: (RecCoroutineLoadingCache<Any, Any>, Any, Continuation<Any>) -> Any?

   init {
      this.mappingFunction = mappingFunction;
   }

   public override suspend fun get(key: Any): Deferred<Any>? {
      return this.get((K)key, (new Function3<RecCoroutineCache<K, V>, K, Continuation<? super V>, Object>(this, null) {
         int label;

         {
            super(3, `$completion`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var3: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var var10000: Any;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val it: Any = this.L$0;
                  var10000 = RecCoroutineLoadingCacheImpl.access$getMappingFunction$p(this.this$0);
                  val var10001: RecCoroutineLoadingCacheImpl = this.this$0;
                  this.label = 1;
                  var10000 = (Function3)var10000.invoke(var10001, it, this);
                  if (var10000 === var3) {
                     return var3;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  var10000 = (Function3)`$result`;
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return var10000;
         }

         public final Object invoke(RecCoroutineCache<K, V> p1, K p2, Continuation<? super V> p3) {
            val var4: Function3 = new <anonymous constructor>(this.this$0, p3);
            var4.L$0 = p2;
            return var4.invokeSuspend(Unit.INSTANCE);
         }
      }) as (RecCoroutineCache<K, V>?, K?, Continuation<? super V>?) -> Any, `$completion`);
   }

   public override suspend fun getEntry(key: Any): Deferred<Any>? {
      return this.getEntry((K)key, (new Function3<RecCoroutineCache<K, V>, K, Continuation<? super V>, Object>(this, null) {
         int label;

         {
            super(3, `$completion`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var3: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var var10000: Any;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val it: Any = this.L$0;
                  var10000 = RecCoroutineLoadingCacheImpl.access$getMappingFunction$p(this.this$0);
                  val var10001: RecCoroutineLoadingCacheImpl = this.this$0;
                  this.label = 1;
                  var10000 = (Function3)var10000.invoke(var10001, it, this);
                  if (var10000 === var3) {
                     return var3;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  var10000 = (Function3)`$result`;
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return var10000;
         }

         public final Object invoke(RecCoroutineCache<K, V> p1, K p2, Continuation<? super V> p3) {
            val var4: Function3 = new <anonymous constructor>(this.this$0, p3);
            var4.L$0 = p2;
            return var4.invokeSuspend(Unit.INSTANCE);
         }
      }) as (RecCoroutineCache<K, V>?, K?, Continuation<? super V>?) -> Any, `$completion`);
   }
}
