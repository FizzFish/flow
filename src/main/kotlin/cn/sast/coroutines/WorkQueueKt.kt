package cn.sast.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

public suspend fun <T> runWork(
   dispatcher: CoroutineDispatcher,
   numberOfCoroutines: Int,
   initialWorkload: Collection<T>,
   doWork: (WorkQueue<T>, T, Continuation<Unit>) -> Any?
) {
   var `$continuation`: Continuation;
   label39: {
      if (`$completion` is <unrepresentable>) {
         `$continuation` = `$completion` as <unrepresentable>;
         if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE;
            break label39;
         }
      }

      `$continuation` = new ContinuationImpl(`$completion`) {
         int I$0;
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
            return WorkQueueKt.runWork(null, 0, null, null, this as Continuation<? super Unit>);
         }
      };
   }

   val `$result`: Any = `$continuation`.result;
   val var10: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
   var queue: WorkQueue;
   var var6: java.util.Iterator;
   switch ($continuation.label) {
      case 0:
         ResultKt.throwOnFailure(`$result`);
         queue = new WorkQueue(dispatcher, doWork);
         var6 = initialWorkload.iterator();
         break;
      case 1:
         numberOfCoroutines = `$continuation`.I$0;
         var6 = `$continuation`.L$1 as java.util.Iterator;
         queue = `$continuation`.L$0 as WorkQueue;
         ResultKt.throwOnFailure(`$result`);
         break;
      case 2:
         ResultKt.throwOnFailure(`$result`);
         return Unit.INSTANCE;
      default:
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
   }

   while (var6.hasNext()) {
      val item: Any = var6.next();
      val var10000: Channel = queue.getChannel();
      `$continuation`.L$0 = queue;
      `$continuation`.L$1 = var6;
      `$continuation`.I$0 = numberOfCoroutines;
      `$continuation`.label = 1;
      if (var10000.send(item, `$continuation`) === var10) {
         return var10;
      }
   }

   `$continuation`.L$0 = null;
   `$continuation`.L$1 = null;
   `$continuation`.label = 2;
   return if (queue.start(numberOfCoroutines, `$continuation`) === var10) var10 else Unit.INSTANCE;
}
