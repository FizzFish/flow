package cn.sast.framework.util

import cn.sast.framework.engine.PreAnalysisImpl
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ICheckPoint
import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nSafeAnalyzeUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeAnalyzeUtil.kt\ncn/sast/framework/util/SafeAnalyzeUtil\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,57:1\n1#2:58\n*E\n"])
public class SafeAnalyzeUtil(errorLimitCount: Int, errorCount: Int = 0) {
   private final val errorLimitCount: Int
   private final var errorCount: Int

   init {
      this.errorLimitCount = errorLimitCount;
      this.errorCount = errorCount;
   }

   context(CoroutineScope)
   public suspend fun <C : ICheckPoint, T> safeAnalyze(point: C, block: (C, Continuation<T>) -> Any?): T? {
      var `$continuation`: Continuation;
      label50: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label50;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            Object L$2;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.safeAnalyze(null, null, null, this as Continuation<? super T>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var10: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);

            try {
               `$continuation`.L$0 = this;
               `$continuation`.L$1 = `$context_receiver_0`;
               `$continuation`.L$2 = point;
               `$continuation`.label = 1;
               var10000 = block.invoke(point, `$continuation`);
            } catch (var12: java.lang.Throwable) {
               this.onCheckerError(var12, SafeAnalyzeUtil::safeAnalyze$lambda$2);
               return null;
            }

            if (var10000 === var10) {
               return var10;
            }
            break;
         case 1:
            point = `$continuation`.L$2 as ICheckPoint;
            `$context_receiver_0` = `$continuation`.L$1 as CoroutineScope;
            this = `$continuation`.L$0 as SafeAnalyzeUtil;

            try {
               ResultKt.throwOnFailure(`$result`);
               var10000 = `$result`;
               break;
            } catch (var13: java.lang.Throwable) {
               (`$continuation`.L$0 as SafeAnalyzeUtil).onCheckerError(var13, SafeAnalyzeUtil::safeAnalyze$lambda$2);
               return null;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      var var5: Any;
      try {
         var5 = if (!(var10000 == Unit.INSTANCE)) var10000 else null;
      } catch (var11: java.lang.Throwable) {
         this.onCheckerError(var11, SafeAnalyzeUtil::safeAnalyze$lambda$2);
         var5 = null;
      }

      return var5;
   }

   context(CheckerUnit)
   public suspend fun <T> safeRunInSceneAsync(block: (Continuation<T>) -> Any?): T? {
      var `$continuation`: Continuation;
      label35: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label35;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            Object L$1;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.safeRunInSceneAsync(null, null, this as Continuation<? super T>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);

            try {
               `$continuation`.L$0 = this;
               `$continuation`.L$1 = `$context_receiver_0`;
               `$continuation`.label = 1;
               var10000 = block.invoke(`$continuation`);
            } catch (var10: Exception) {
               this.onCheckerError(var10, SafeAnalyzeUtil::safeRunInSceneAsync$lambda$4);
               return null;
            }

            if (var10000 === var7) {
               return var7;
            }
            break;
         case 1:
            `$context_receiver_0` = `$continuation`.L$1 as CheckerUnit;
            this = `$continuation`.L$0 as SafeAnalyzeUtil;

            try {
               ResultKt.throwOnFailure(`$result`);
               var10000 = `$result`;
               break;
            } catch (var9: Exception) {
               (`$continuation`.L$0 as SafeAnalyzeUtil).onCheckerError(var9, SafeAnalyzeUtil::safeRunInSceneAsync$lambda$4);
               return null;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         return var10000;
      } catch (var8: Exception) {
         this.onCheckerError(var8, SafeAnalyzeUtil::safeRunInSceneAsync$lambda$4);
         return null;
      }
   }

   private fun onCheckerError(t: Throwable, onCheckerError: () -> Unit) {
      if (t is CancellationException) {
         throw t;
      } else if (t !is Exception && t !is NotImplementedError) {
         throw t;
      } else {
         onCheckerError.invoke();
      }
   }

   @JvmStatic
   fun `safeAnalyze$lambda$2$lambda$1`(`$point`: ICheckPoint): Any {
      return "When analyzing this location: $`$point`, please file this bug to us";
   }

   @JvmStatic
   fun `safeAnalyze$lambda$2`(`$t`: java.lang.Throwable, `this$0`: SafeAnalyzeUtil, `$$context_receiver_0`: CoroutineScope, `$point`: ICheckPoint): Unit {
      PreAnalysisImpl.Companion.getKLogger().error(`$t`, SafeAnalyzeUtil::safeAnalyze$lambda$2$lambda$1);
      `this$0`.errorCount++;
      if (`this$0`.errorCount > `this$0`.errorLimitCount) {
         CoroutineScopeKt.cancel$default(`$$context_receiver_0`, null, 1, null);
      }

      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `safeRunInSceneAsync$lambda$4$lambda$3`(`$$context_receiver_0`: CheckerUnit): Any {
      return "Occur a exception while call $`$$context_receiver_0`:runInSceneAsync, please file this bug to us";
   }

   @JvmStatic
   fun `safeRunInSceneAsync$lambda$4`(`$t`: Exception, `$$context_receiver_0`: CheckerUnit): Unit {
      PreAnalysisImpl.Companion.getKLogger().error(`$t`, SafeAnalyzeUtil::safeRunInSceneAsync$lambda$4$lambda$3);
      return Unit.INSTANCE;
   }
}
