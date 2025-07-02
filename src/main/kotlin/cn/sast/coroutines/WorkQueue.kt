package cn.sast.coroutines

import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.DelayKt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelKt
import kotlinx.coroutines.channels.ChannelResult

public class WorkQueue<T>(dispatcher: CoroutineDispatcher, doWork: (WorkQueue<Any>, Any, Continuation<Unit>) -> Any?) {
   private final val dispatcher: CoroutineDispatcher
   private final val doWork: (WorkQueue<Any>, Any, Continuation<Unit>) -> Any?
   public final val channel: Channel<Any>

   init {
      this.dispatcher = dispatcher;
      this.doWork = doWork;
      this.channel = ChannelKt.Channel$default(Integer.MAX_VALUE, null, null, 6, null);
   }

   public suspend fun start(numberOfCoroutines: Int) {
      val idleCount: AtomicInteger = new AtomicInteger(0);
      val var10000: Any = CoroutineScopeKt.coroutineScope(
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(numberOfCoroutines, this, idleCount, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.$numberOfCoroutines = `$numberOfCoroutines`;
                  this.this$0 = `$receiver`;
                  this.$idleCount = `$idleCount`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var15: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        val `$this$coroutineScope`: CoroutineScope = this.L$0 as CoroutineScope;
                        val `$this$map$iv`: java.lang.Iterable = RangesKt.until(0, this.$numberOfCoroutines) as java.lang.Iterable;
                        val var4: WorkQueue = this.this$0;
                        val var5: AtomicInteger = this.$idleCount;
                        val var6: Int = this.$numberOfCoroutines;
                        val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));
                        val var11: java.util.Iterator = `$this$map$iv`.iterator();

                        while (var11.hasNext()) {
                           val `item$iv$iv`: Int = (var11 as IntIterator).nextInt();
                           `destination$iv$iv`.add(
                              BuildersKt.launch$default(
                                 `$this$coroutineScope`,
                                 WorkQueue.access$getDispatcher$p(var4) as CoroutineContext,
                                 null,
                                 (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(var4, var5, var6, null) {
                                    int I$0;
                                    int label;

                                    {
                                       super(2, `$completionx`);
                                       this.this$0 = `$receiver`;
                                       this.$idleCount = `$idleCount`;
                                       this.$numberOfCoroutines = `$numberOfCoroutines`;
                                    }

                                    // $VF: Irreducible bytecode was duplicated to produce valid code
                                    public final Object invokeSuspend(Object $result) {
                                       val var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                       var `$this$launch`: CoroutineScope;
                                       var didReportIdle: Boolean;
                                       switch (this.label) {
                                          case 0:
                                             ResultKt.throwOnFailure(`$result`);
                                             `$this$launch` = this.L$0 as CoroutineScope;
                                             didReportIdle = false;
                                             break;
                                          case 1:
                                             didReportIdle = (boolean)this.I$0;
                                             `$this$launch` = this.L$0 as CoroutineScope;
                                             ResultKt.throwOnFailure(`$result`);
                                             break;
                                          case 2:
                                             didReportIdle = (boolean)this.I$0;
                                             `$this$launch` = this.L$0 as CoroutineScope;
                                             ResultKt.throwOnFailure(`$result`);
                                             break;
                                          default:
                                             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                       }

                                       while (CoroutineScopeKt.isActive($this$launch)) {
                                          val nextItem: Any = ChannelResult.getOrNull-impl(this.this$0.getChannel().tryReceive-PtdJZtk());
                                          if (nextItem == null) {
                                             val var10000: Int;
                                             if (didReportIdle == 0) {
                                                didReportIdle = true;
                                                var10000 = this.$idleCount.incrementAndGet();
                                             } else {
                                                var10000 = this.$idleCount.get();
                                             }

                                             if (var10000 == this.$numberOfCoroutines) {
                                                break;
                                             }

                                             val var10001: Continuation = this as Continuation;
                                             this.L$0 = `$this$launch`;
                                             this.I$0 = didReportIdle;
                                             this.label = 1;
                                             if (DelayKt.delay(10L, var10001) === var6) {
                                                return var6;
                                             }
                                          } else {
                                             val var7: Function3 = WorkQueue.access$getDoWork$p(this.this$0);
                                             val var8: WorkQueue = this.this$0;
                                             this.L$0 = `$this$launch`;
                                             this.I$0 = didReportIdle;
                                             this.label = 2;
                                             if (var7.invoke(var8, nextItem, this) === var6) {
                                                return var6;
                                             }
                                          }
                                       }

                                       return Unit.INSTANCE;
                                    }

                                    public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                       val var3: Function2 = new <anonymous constructor>(this.this$0, this.$idleCount, this.$numberOfCoroutines, `$completion`);
                                       var3.L$0 = value;
                                       return var3 as Continuation<Unit>;
                                    }

                                    public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                       return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                    }
                                 }) as Function2,
                                 2,
                                 null
                              )
                           );
                        }

                        val var10000: java.util.Collection = `destination$iv$iv` as java.util.List;
                        val var10001: Continuation = this as Continuation;
                        this.label = 1;
                        if (AwaitKt.joinAll(var10000, var10001) === var15) {
                           return var15;
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
                  val var3: Function2 = new <anonymous constructor>(this.$numberOfCoroutines, this.this$0, this.$idleCount, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2,
         `$completion`
      );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }
}
