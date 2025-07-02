package cn.sast.common

import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.JobKt
import mu.KLogger
import mu.KotlinLogging
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

private final val logger: KLogger = KotlinLogging.INSTANCE.logger(TaskQueueKt::logger$lambda$0)

public suspend fun runInMilliSeconds(job: Job, milliSeconds: Long, name: String, timeoutAction: () -> Unit) {
   var `$continuation`: Continuation;
   label24: {
      if (`$completion` is <unrepresentable>) {
         `$continuation` = `$completion` as <unrepresentable>;
         if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE;
            break label24;
         }
      }

      `$continuation` = new ContinuationImpl(`$completion`) {
         long J$0;
         long J$1;
         Object L$0;
         Object L$1;
         int label;

         {
            super(`$completion`);
         }

         @Nullable
         public final Object invokeSuspend(@NotNull Object $result) {
            this.result = `$result`;
            this.label |= Integer.MIN_VALUE;
            return TaskQueueKt.runInMilliSeconds(null, 0L, null, null, this as Continuation<? super Unit>);
         }
      };
   }

   val `$result`: Any = `$continuation`.result;
   val var13: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
   var start: Long;
   var timer: Timer;
   switch ($continuation.label) {
      case 0:
         ResultKt.throwOnFailure(`$result`);
         start = System.currentTimeMillis();
         timer = new Timer();
         timer.schedule(
            new TimerTask(job, timeoutAction, name) {
               {
                  this.$job = `$job`;
                  this.$timeoutAction = `$timeoutAction`;
                  this.$name = `$name`;
               }

               @Override
               public void run() {
                  BuildersKt.runBlocking$default(
                     null, (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.$job, this.$timeoutAction, this.$name, null) {
                        long J$0;
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$job = `$job`;
                           this.$timeoutAction = `$timeoutAction`;
                           this.$name = `$name`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           var cancelStart: Long;
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 if (!this.$job.isActive()) {
                                    return Unit.INSTANCE;
                                 }

                                 TaskQueueKt.access$getLogger$p().warn(<unrepresentable>::invokeSuspend$lambda$0);
                                 cancelStart = System.currentTimeMillis();
                                 val var10000: Job = this.$job;
                                 val var10001: Continuation = this as Continuation;
                                 this.J$0 = cancelStart;
                                 this.label = 1;
                                 if (JobKt.cancelAndJoin(var10000, var10001) === var6) {
                                    return var6;
                                 }
                                 break;
                              case 1:
                                 cancelStart = this.J$0;
                                 ResultKt.throwOnFailure(`$result`);
                                 break;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }

                           val cancelEnd: Long = System.currentTimeMillis();
                           if (cancelEnd - cancelStart > 1000L) {
                              TaskQueueKt.access$getLogger$p().warn(<unrepresentable>::invokeSuspend$lambda$1);
                           }

                           this.$timeoutAction.invoke();
                           return Unit.INSTANCE;
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           return (new <anonymous constructor>(this.$job, this.$timeoutAction, this.$name, `$completion`)) as Continuation<Unit>;
                        }

                        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }

                        private static final Object invokeSuspend$lambda$0(java.lang.String $name) {
                           return "$`$name` runInMilliSeconds timeout";
                        }

                        private static final Object invokeSuspend$lambda$1(java.lang.String $name, long $cancelEnd, long $cancelStart) {
                           return "$`$name` runInMilliSeconds cancelAndJoin takes ${`$cancelEnd` - `$cancelStart`}";
                        }
                     }) as Function2, 1, null
                  );
               }
            },
            milliSeconds
         );
         `$continuation`.L$0 = name;
         `$continuation`.L$1 = timer;
         `$continuation`.J$0 = milliSeconds;
         `$continuation`.J$1 = start;
         `$continuation`.label = 1;
         if (job.join(`$continuation`) === var13) {
            return var13;
         }
         break;
      case 1:
         start = `$continuation`.J$1;
         milliSeconds = `$continuation`.J$0;
         timer = `$continuation`.L$1 as Timer;
         name = `$continuation`.L$0 as java.lang.String;
         ResultKt.throwOnFailure(`$result`);
         break;
      default:
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
   }

   timer.cancel();
   val end: Long = System.currentTimeMillis();
   if ((double)(end - start - milliSeconds) / milliSeconds > 0.1) {
      logger.warn(TaskQueueKt::runInMilliSeconds$lambda$1);
   }

   return Unit.INSTANCE;
}

fun `logger$lambda$0`(): Unit {
   return Unit.INSTANCE;
}

fun `runInMilliSeconds$lambda$1`(`$name`: java.lang.String, `$milliSeconds`: Long, `$end`: Long, `$start`: Long): Any {
   return "$`$name` runInMilliSeconds cost more than expected expect=$`$milliSeconds`, actual=${`$end` - `$start`}";
}

@JvmSynthetic
fun `access$getLogger$p`(): KLogger {
   return logger;
}
