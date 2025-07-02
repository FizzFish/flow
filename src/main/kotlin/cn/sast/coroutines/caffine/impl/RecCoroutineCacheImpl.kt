package cn.sast.coroutines.caffine.impl

import cn.sast.graph.NoBackEdgeDirectGraph
import com.feysh.corax.cache.coroutines.RecCoroutineCache
import com.feysh.corax.cache.coroutines.XCache
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Interner
import com.github.benmanes.caffeine.cache.stats.CacheStats
import java.util.function.Function
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import mu.KLogger

@SourceDebugExtension(["SMAP\nRecCoroutineCacheImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RecCoroutineCacheImpl.kt\ncn/sast/coroutines/caffine/impl/RecCoroutineCacheImpl\n+ 2 CoroutineScope.kt\nkotlinx/coroutines/CoroutineScopeKt\n*L\n1#1,181:1\n326#2:182\n*S KotlinDebug\n*F\n+ 1 RecCoroutineCacheImpl.kt\ncn/sast/coroutines/caffine/impl/RecCoroutineCacheImpl\n*L\n154#1:182\n*E\n"])
internal open class RecCoroutineCacheImpl<K, V>(xCache: XCache<Any, Deferred<Any>>,
      cache: Cache<Any, Deferred<Any>>,
      weakKeyAssociateByValue: (Any) -> Array<Any?>
   ) :
   RecCoroutineCache<K, V> {
   private final val xCache: XCache<Any, Deferred<Any>>
   private final val cache: Cache<Any, Deferred<Any>>
   private final val weakKeyAssociateByValue: (Any) -> Array<Any?>
   private final val lruCache: Cache<Any, Deferred<Any>>
   public final val scope: CoroutineScope

   public open val cacheStats: CacheStats
      public open get() {
         val var10000: CacheStats = this.cache.stats();
         return var10000;
      }


   private final val interner: Interner<Any>
   private final val noBackEdgeDirectGraph: NoBackEdgeDirectGraph<Any>
   private final val weakHolder: WeakEntryHolder<Any, Any>
   private final var _x: Int

   public open val size: Long
      public open get() {
         return this.cache.estimatedSize();
      }


   init {
      this.xCache = xCache;
      this.cache = cache;
      this.weakKeyAssociateByValue = weakKeyAssociateByValue;
      this.lruCache = Caffeine.newBuilder().initialCapacity(2000).maximumSize(10000L).build();
      this.scope = this.xCache.getDefaultScope().getValue() as CoroutineScope;
      val var10001: Interner = Interner.newWeakInterner();
      this.interner = var10001;
      this.noBackEdgeDirectGraph = new NoBackEdgeDirectGraph<>();
      this.weakHolder = new WeakEntryHolder<>();
   }

   private fun CoroutineContext.recursiveId(): Any {
      val var10000: RecCoroutineCacheImpl.RecursiveContext = `$this$recursiveId`.get(RecCoroutineCacheImpl.RecursiveContext.Key) as RecCoroutineCacheImpl.RecursiveContext;
      if (var10000 != null) {
         val var2: Any = var10000.getId();
         if (var2 != null) {
            return var2;
         }
      }

      throw new IllegalStateException("current coroutine context has no RecursiveContext".toString());
   }

   public override suspend fun get(key: Any, mappingFunction: (RecCoroutineCache<Any, Any>, Any, Continuation<Any>) -> Any?): Deferred<Any>? {
      return get$suspendImpl(this, (K)key, mappingFunction, `$completion`);
   }

   private fun get(src: Any, parentJob: Job, key: Any, mappingFunction: (RecCoroutineCache<Any, Any>, Any, Continuation<Any>) -> Any?): Deferred<Any>? {
      val canonicalKey: Any = this.interner.intern(key);
      val mapped: BooleanRef = new BooleanRef();
      val mapping: Function1 = RecCoroutineCacheImpl::get$lambda$0;
      val deferred: ObjectRef = new ObjectRef();
      deferred.element = this.cache.get(canonicalKey, new Function(mapping) {
         {
            this.function = function;
         }
      });
      if ((deferred.element as Deferred).isCancelled() && !parentJob.isCancelled()) {
         val ignore: Deferred = this.cache.asMap().remove(canonicalKey) as Deferred;
         mapped.element = false;
         deferred.element = this.cache.get(canonicalKey, new Function(mapping) {
            {
               this.function = function;
            }
         });
      }

      val var12: Deferred = this.lruCache.get(canonicalKey, new Function(RecCoroutineCacheImpl::get$lambda$1) {
         {
            this.function = function;
         }
      }) as Deferred;
      val var10001: Any = deferred.element;
      val var13: Any = this.recursiveId((var10001 as CoroutineScope).getCoroutineContext());
      if (!mapped.element) {
         if ((deferred.element as Job).isCompleted()) {
            return deferred.element as Deferred<V>;
         }

         if (!this.noBackEdgeDirectGraph.addEdgeSynchronized(src, var13)) {
            return null;
         }
      }

      (deferred.element as Job).invokeOnCompletion(RecCoroutineCacheImpl::get$lambda$2);
      return deferred.element as Deferred<V>;
   }

   public override suspend fun getEntry(key: Any, mappingFunction: (RecCoroutineCache<Any, Any>, Any, Continuation<Any>) -> Any?): Deferred<Any>? {
      return getEntry$suspendImpl(this, (K)key, mappingFunction, `$completion`);
   }

   public override fun validateAfterFinished(): Boolean {
      if (!this.noBackEdgeDirectGraph.isComplete()) {
         logger.error(RecCoroutineCacheImpl::validateAfterFinished$lambda$3);
         return false;
      } else {
         return true;
      }
   }

   public override fun cleanUp() {
      this.cache.cleanUp();
      this.noBackEdgeDirectGraph.cleanUp();
      this.weakHolder.clean();
      this.lruCache.cleanUp();
   }

   public override suspend fun getPredSize(): Int {
      return getPredSize$suspendImpl(this, `$completion`);
   }

   @JvmStatic
   fun `get$lambda$0`(`$mapped`: BooleanRef, `this$0`: RecCoroutineCacheImpl, `$src`: Any, `$parentJob`: Job, `$mappingFunction`: Function3, k: Any): Deferred {
      if (`$mapped`.element) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         `$mapped`.element = true;
         val id: RecCoroutineCacheImpl.RecID = new RecCoroutineCacheImpl.RecID();
         if (!`this$0`.noBackEdgeDirectGraph.addEdgeSynchronized(`$src`, id)) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else {
            return BuildersKt.async(
               `this$0`.scope,
               new RecCoroutineCacheImpl.RecursiveContext(id).plus(`$parentJob` as CoroutineContext),
               CoroutineStart.DEFAULT,
               (new Function2<CoroutineScope, Continuation<? super V>, Object>(`this$0`, id, `$mappingFunction`, k, null) {
                  int label;

                  {
                     super(2, `$completionx`);
                     this.this$0 = `$receiver`;
                     this.$id = `$id`;
                     this.$mappingFunction = `$mappingFunction`;
                     this.$k = (K)`$k`;
                  }

                  public final Object invokeSuspend(Object $result) {
                     val var13: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     var job: CoroutineScope;
                     var var10000: Any;
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           val `$this$async`: CoroutineScope = this.L$0 as CoroutineScope;
                           job = this.L$0 as CoroutineScope;
                           if (!(RecCoroutineCacheImpl.access$recursiveId(this.this$0, `$this$async`.getCoroutineContext()) == this.$id)) {
                              throw new IllegalArgumentException("Failed requirement.".toString());
                           }

                           var10000 = this.$mappingFunction;
                           val var10001: RecCoroutineCacheImpl = this.this$0;
                           val var10002: Any = this.$k;
                           this.L$0 = `$this$async`;
                           this.label = 1;
                           var10000 = (Function3)var10000.invoke(var10001, var10002, this);
                           if (var10000 === var13) {
                              return var13;
                           }
                           break;
                        case 1:
                           job = this.L$0 as CoroutineScope;
                           ResultKt.throwOnFailure(`$result`);
                           var10000 = (Function3)`$result`;
                           break;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }

                     val var5: RecCoroutineCacheImpl = this.this$0;
                     val var6: Any = this.$k;

                     for (Object ref : (Object[])RecCoroutineCacheImpl.access$getWeakKeyAssociateByValue$p(this.this$0).invoke(var10000)) {
                        val var16: WeakEntryHolder = RecCoroutineCacheImpl.access$getWeakHolder$p(var5);
                        if (ref != null) {
                           var16.put(var6, ref);
                        }
                     }

                     RecCoroutineCacheImpl.access$getWeakHolder$p(var5).put(var6, job);
                     return var10000;
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     val var3: Function2 = new <anonymous constructor>(this.this$0, this.$id, this.$mappingFunction, this.$k, `$completion`);
                     var3.L$0 = value;
                     return var3 as Continuation<Unit>;
                  }

                  public final Object invoke(CoroutineScope p1, Continuation<? super V> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }
               }) as Function2
            );
         }
      }
   }

   @JvmStatic
   fun `get$lambda$1`(`$deferred`: ObjectRef, it: Any): Deferred {
      return `$deferred`.element as Deferred;
   }

   @JvmStatic
   fun `get$lambda$2`(`this$0`: RecCoroutineCacheImpl, `$deferred`: ObjectRef, `$canonicalKey`: Any, `$src`: Any, `$tgt`: Any, cause: java.lang.Throwable): Unit {
      `this$0`._x = `this$0`._x + System.identityHashCode(`$deferred`.element) + System.identityHashCode(`$canonicalKey`);
      `this$0`.noBackEdgeDirectGraph.removeEdgeSynchronized(`$src`, `$tgt`);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `validateAfterFinished$lambda$3`(`this$0`: RecCoroutineCacheImpl): Any {
      return "${`this$0`.noBackEdgeDirectGraph} is not complete";
   }

   @JvmStatic
   fun `logger$lambda$4`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final var logger: KLogger
   }

   public open class EntryRecID : RecCoroutineCacheImpl.RecID {
      public override fun toString(): String {
         return "Entry-${super.toString()}";
      }
   }

   public open class RecID {
      public override fun toString(): String {
         return "RecID-${System.identityHashCode(this)}";
      }
   }

   public class RecursiveContext(id: Any) : AbstractCoroutineContextElement(Key) {
      public final val id: Any

      init {
         this.id = id;
      }

      public companion object Key : kotlin.coroutines.CoroutineContext.Key<RecCoroutineCacheImpl.RecursiveContext>
   }
}
