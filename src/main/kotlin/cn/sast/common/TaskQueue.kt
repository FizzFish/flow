package cn.sast.common

import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ChannelKt
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.ChannelsKt
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.SendChannel.DefaultImpls

/** @deprecated */
@Deprecated(message = "Use MultiWorkerQueue instead.", replaceWith = @ReplaceWith(expression = "MultiWorkerQueue", imports = []))
public class TaskQueue<TaskData>(name: String,
   numberThreads: Int = Math.max(OS.INSTANCE.getMaxThreadNum() - 1, 1),
   action: (Any, Int, Continuation<Unit>) -> Any?
) {
   private final val name: String
   private final val numberThreads: Int
   private final val action: (Any, Int, Continuation<Unit>) -> Any?
   private final val queue: Channel<Any>

   init {
      this.name = name;
      this.numberThreads = numberThreads;
      this.action = action;
      this.queue = ChannelKt.Channel$default(this.numberThreads * 2, null, null, 6, null);
   }

   public fun addTask(taskData: Any, isLast: Boolean = false) {
      ChannelResult.getOrThrow-impl(ChannelsKt.trySendBlocking(this.queue as SendChannel, taskData));
      if (isLast) {
         DefaultImpls.close$default(this.queue as SendChannel, null, 1, null);
      }
   }

   public fun addTask(taskData: Iterable<Any>) {
      for (Object t : taskData) {
         this.addTask((TaskData)t, false);
      }
   }

   public fun addTaskFinished() {
      DefaultImpls.close$default(this.queue as SendChannel, null, 1, null);
   }

   public fun runTask(): Job {
      val scope: CoroutineScope = CoroutineScopeKt.CoroutineScope(Dispatchers.getDefault() as CoroutineContext);
      val jobs: ArrayList = new ArrayList();
      var i: Int = 0;

      for (int var4 = this.numberThreads; i < var4; i++) {
         jobs.add(
            BuildersKt.launch$default(
               scope,
               (new CoroutineName("${this.name}-$i")) as CoroutineContext,
               null,
               (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, i, null) {
                  Object L$0;
                  int label;

                  {
                     super(2, `$completionx`);
                     this.this$0 = `$receiver`;
                     this.$i = `$i`;
                  }

                  // $VF: Irreducible bytecode was duplicated to produce valid code
                  public final Object invokeSuspend(Object $result) {
                     val var4: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     var var2: ChannelIterator;
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           var2 = TaskQueue.access$getQueue$p(this.this$0).iterator();
                           break;
                        case 1:
                           var2 = this.L$0 as ChannelIterator;
                           ResultKt.throwOnFailure(`$result`);
                           if (!`$result` as java.lang.Boolean) {
                              return Unit.INSTANCE;
                           }

                           val taskData: Any = var2.next();
                           val var10000: Function3 = TaskQueue.access$getAction$p(this.this$0);
                           val var10002: Int = Boxing.boxInt(this.$i);
                           this.L$0 = var2;
                           this.label = 2;
                           if (var10000.invoke(taskData, var10002, this) === var4) {
                              return var4;
                           }
                           break;
                        case 2:
                           var2 = this.L$0 as ChannelIterator;
                           ResultKt.throwOnFailure(`$result`);
                           break;
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }

                     val var5: Any;
                     var var7: Function3;
                     val var8: Int;
                     do {
                        val var10001: Continuation = this as Continuation;
                        this.L$0 = var2;
                        this.label = 1;
                        var7 = (Function3)var2.hasNext(var10001);
                        if (var7 === var4) {
                           return var4;
                        }

                        if (!var7 as java.lang.Boolean) {
                           return Unit.INSTANCE;
                        }

                        var5 = var2.next();
                        var7 = TaskQueue.access$getAction$p(this.this$0);
                        var8 = Boxing.boxInt(this.$i);
                        this.L$0 = var2;
                        this.label = 2;
                     } while (var7.invoke(taskData, var8, this) != var4);

                     return var4;
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     return (new <anonymous constructor>(this.this$0, this.$i, `$completion`)) as Continuation<Unit>;
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

      return BuildersKt.launch$default(
         scope,
         (new CoroutineName("${this.name}-joinAll")) as CoroutineContext,
         null,
         (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(jobs, null) {
            int label;

            {
               super(2, `$completionx`);
               this.$jobs = `$jobs`;
            }

            public final Object invokeSuspend(Object $result) {
               val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     val var10000: java.util.Collection = this.$jobs;
                     val var10001: Continuation = this as Continuation;
                     this.label = 1;
                     if (AwaitKt.joinAll(var10000, var10001) === var2) {
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
               return (new <anonymous constructor>(this.$jobs, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2,
         2,
         null
      );
   }

   public companion object
}
