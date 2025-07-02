package cn.sast.coroutines

import cn.sast.common.OS
import java.io.Closeable
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.ThreadsKt
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.atomicfu.AtomicFU
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.AtomicRef
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellableContinuation.DefaultImpls
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelKt
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.future.FutureKt

@SourceDebugExtension(["SMAP\nMultiWorkerQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultiWorkerQueue.kt\ncn/sast/coroutines/MultiWorkerQueue\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 AtomicFU.common.kt\nkotlinx/atomicfu/AtomicFU_commonKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,186:1\n68#1:199\n70#1:201\n68#1:207\n70#1:212\n70#1:214\n1#2:187\n498#3,4:188\n174#3,4:192\n487#3,3:196\n490#3:200\n487#3,3:204\n490#3:208\n487#3,3:209\n490#3:213\n174#3,4:217\n174#3,4:221\n174#3,4:225\n1863#4,2:202\n1863#4,2:215\n*S KotlinDebug\n*F\n+ 1 MultiWorkerQueue.kt\ncn/sast/coroutines/MultiWorkerQueue\n*L\n119#1:199\n124#1:201\n140#1:207\n145#1:212\n148#1:214\n75#1:188,4\n77#1:192,4\n118#1:196,3\n118#1:200\n140#1:204,3\n140#1:208\n144#1:209,3\n144#1:213\n157#1:217,4\n162#1:221,4\n168#1:225,4\n135#1:202,2\n156#1:215,2\n*E\n"])
public open class MultiWorkerQueue<T>(name: String,
      workersCount: Int = Math.max(OS.INSTANCE.getMaxThreadNum() - 1, 1),
      action: (Any, Continuation<Unit>) -> Any?
   ) :
   Cloneable,
   Closeable {
   private final val name: String
   private final val workersCount: Int
   private final val action: (Any, Continuation<Unit>) -> Any?
   private final val tasksQueue: Channel<Any>
   private final val availableWorkers: Channel<CancellableContinuation<Any>>
   private final val futureAtomicRef: AtomicRef<CompletableFuture<Unit>?>
   private final val workerPool: OnDemandAllocatingPool<Thread>
   private final val tasksAndWorkersCounter: AtomicLong
   private final val activeCounter: AtomicLong

   init {
      this.name = name;
      this.workersCount = workersCount;
      this.action = action;
      if (this.workersCount <= 0) {
         throw new IllegalStateException(("workersCount: ${this.workersCount} must be greater than zero").toString());
      } else {
         this.tasksQueue = ChannelKt.Channel$default(Integer.MAX_VALUE, null, null, 6, null);
         this.availableWorkers = ChannelKt.Channel$default(Integer.MAX_VALUE, null, null, 6, null);
         this.futureAtomicRef = AtomicFU.atomic(null);
         this.workerPool = new OnDemandAllocatingPool<>(this.workersCount, MultiWorkerQueue::workerPool$lambda$2);
         this.tasksAndWorkersCounter = AtomicFU.atomic(0L);
         this.activeCounter = AtomicFU.atomic(0L);
      }
   }

   public open suspend fun beforeExecute(task: Any) {
      return beforeExecute$suspendImpl(this, (T)task, `$completion`);
   }

   public open suspend fun process(task: Any) {
      return process$suspendImpl(this, (T)task, `$completion`);
   }

   public open suspend fun afterExecute(task: Any, t: Throwable?) {
      return afterExecute$suspendImpl(this, (T)task, t, `$completion`);
   }

   private inline fun Long.isClosed(): Boolean {
      return (`$this$isClosed` and 1L) == 1L;
   }

   private inline fun Long.hasTasks(): Boolean {
      return `$this$hasTasks` >= 2L;
   }

   private inline fun Long.hasWorkers(): Boolean {
      return `$this$hasWorkers` < 0L;
   }

   private fun resumeJoinCoroutineAndUpdate(dec: Long) {
      val `$this$updateAndGet$iv`: AtomicLong = this.activeCounter;

      val `$i$f$getAndUpdate`: Long;
      do {
         `$i$f$getAndUpdate` = `$this$updateAndGet$iv`.getValue();
      } while (!$this$updateAndGet$iv.compareAndSet(cur$iv, cur$iv - dec));

      if (`$i$f$getAndUpdate` - dec == 0L) {
         val var12: AtomicRef = this.futureAtomicRef;

         val `cur$ivx`: Any;
         do {
            `cur$ivx` = var12.getValue();
            val var14: CompletableFuture = `cur$ivx` as CompletableFuture;
         } while (!$this$getAndUpdate$iv.compareAndSet(cur$ivx, null));

         val var10000: CompletableFuture = `cur$ivx` as CompletableFuture;
         if (`cur$ivx` as CompletableFuture != null) {
            var10000.complete(Unit.INSTANCE);
         }
      } else if (`$i$f$getAndUpdate` - dec < 0L) {
         throw new IllegalStateException("Internal invariants of $this were violated, please file this bug to us. activeCount: ${`$i$f$getAndUpdate` - dec}");
      }
   }

   private fun workerRunLoop() {
      BuildersKt.runBlocking$default(
         null,
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
         ) as Function2,
         1,
         null
      );
   }

   private fun obtainWorker(): CancellableContinuation<Any> {
      var var10000: CancellableContinuation = ChannelResult.getOrNull-impl(this.availableWorkers.tryReceive-PtdJZtk()) as CancellableContinuation;
      if (var10000 == null) {
         var10000 = BuildersKt.runBlocking$default(
            null, (new Function2<CoroutineScope, Continuation<? super CancellableContinuation<? super T>>, Object>(this, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var var10000: Any;
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = MultiWorkerQueue.access$getAvailableWorkers$p(this.this$0);
                        val var10001: Continuation = this as Continuation;
                        this.label = 1;
                        var10000 = (Channel)var10000.receive(var10001);
                        if (var10000 === var2) {
                           return var2;
                        }
                        break;
                     case 1:
                        ResultKt.throwOnFailure(`$result`);
                        var10000 = (Channel)`$result`;
                        break;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  return var10000;
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super CancellableContinuation<? super T>> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }) as Function2, 1, null
         ) as CancellableContinuation;
      }

      return var10000;
   }

   public fun dispatch(data: Any) {
      val result: AtomicLong = this.tasksAndWorkersCounter;

      val `cur$iv`: Long;
      do {
         `cur$iv` = result.getValue();
         if ((`cur$iv` and 1L) == 1L) {
            throw new IllegalStateException("Dispatcher ${this.name} was closed, attempted to schedule: $data");
         }
      } while (!$this$getAndUpdate$iv.compareAndSet(cur$iv, cur$iv + 2));

      this.activeCounter.incrementAndGet();
      if (`cur$iv` < 0L) {
         (this.obtainWorker() as Continuation).resumeWith(Result.constructor-impl(data));
      } else {
         this.workerPool.allocate();
         this.checkChannelResult-rs8usWo(this.tasksQueue.trySend-JP2dKIU(data));
      }
   }

   public fun dispatch(data: Collection<Any>) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         this.dispatch((T)`element$iv`);
      }
   }

   public override fun close() {
      val workers: AtomicLong = this.tasksAndWorkersCounter;

      val `$i$f$getAndUpdate`: Long;
      do {
         `$i$f$getAndUpdate` = workers.getValue();
      } while (!$this$getAndUpdate$iv.compareAndSet(cur$iv, (cur$iv & 1L) == 1L ? cur$iv : cur$iv | 1L));

      while (true) {
         val `$this$getAndUpdate$ivx`: AtomicLong = this.tasksAndWorkersCounter;

         val `cur$ivx`: Long;
         do {
            `cur$ivx` = `$this$getAndUpdate$ivx`.getValue();
         } while (!$this$getAndUpdate$ivx.compareAndSet(cur$ivx, cur$ivx < 0L ? cur$ivx + 2 : cur$ivx));

         this.activeCounter.incrementAndGet();
         if (`cur$ivx` >= 0L) {
            val var16: java.lang.Iterable;
            for (Object element$iv : var16) {
               (var23 as Thread).join();
            }

            val `$this$getAndUpdate$ivxx`: AtomicRef = this.futureAtomicRef;

            do {
               var21 = `$this$getAndUpdate$ivxx`.getValue();
               val var24: CompletableFuture = var21 as CompletableFuture;
            } while (!$this$getAndUpdate$ivxx.compareAndSet(cur$iv, null));

            return;
         }

         DefaultImpls.cancel$default(this.obtainWorker(), null, 1, null);
      }
   }

   public suspend fun join() {
      val `$this$getAndUpdate$iv`: AtomicRef = this.futureAtomicRef;

      val `$i$f$getAndUpdate`: Any;
      var var10000: CompletableFuture;
      do {
         `$i$f$getAndUpdate` = `$this$getAndUpdate$iv`.getValue();
         val `cur$iv`: CompletableFuture = `$i$f$getAndUpdate` as CompletableFuture;
         var10000 = `$i$f$getAndUpdate` as CompletableFuture;
         if (`cur$iv` == null) {
            var10000 = new CompletableFuture();
         }
      } while (!$this$getAndUpdate$iv.compareAndSet(cur$iv, var10000));

      val old: CompletableFuture = `$i$f$getAndUpdate` as CompletableFuture;
      if (`$i$f$getAndUpdate` as CompletableFuture != null) {
         return FutureKt.await(old, `$completion`);
      } else {
         this.resumeJoinCoroutineAndUpdate(0L);
         val `$this$getAndUpdate$ivx`: AtomicRef = this.futureAtomicRef;

         val `cur$ivx`: Any;
         do {
            `cur$ivx` = `$this$getAndUpdate$ivx`.getValue();
         } while (!$this$getAndUpdate$ivx.compareAndSet(cur$ivx, (CompletableFuture)cur$ivx));

         return if (`cur$ivx` as CompletableFuture != null) FutureKt.await(`cur$ivx` as CompletableFuture, `$completion`) else Unit.INSTANCE;
      }
   }

   @JvmName(name = "waitAndBlock")
   public fun wait() {
      BuildersKt.runBlocking$default(null, (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
         int label;

         {
            super(2, `$completionx`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val var10000: MultiWorkerQueue = this.this$0;
                  val var10001: Continuation = this as Continuation;
                  this.label = 1;
                  if (var10000.join(var10001) === var2) {
                     return var2;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return Unit.INSTANCE;
         }

         public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
            return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
         }

         public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2, 1, null);
   }

   private fun checkChannelResult(result: ChannelResult<*>) {
      if (!ChannelResult.isSuccess-impl(result)) {
         throw new IllegalStateException("Internal invariants of $this were violated, please file this bug to us", ChannelResult.exceptionOrNull-impl(result));
      }
   }

   override fun clone(): Any {
      return super.clone();
   }

   @JvmStatic
   fun `workerPool$lambda$2$lambda$1`(`this$0`: MultiWorkerQueue): Unit {
      `this$0`.workerRunLoop();
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `workerPool$lambda$2`(`this$0`: MultiWorkerQueue, ctl: Int): Thread {
      return ThreadsKt.thread$default(false, false, null, "${`this$0`.name}-$ctl", 0, MultiWorkerQueue::workerPool$lambda$2$lambda$1, 23, null);
   }
}
