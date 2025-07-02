package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import soot.SootClass
import soot.SootMethod

public open class CustomEntryProvider(entries: Set<SootMethod>) : IEntryPointProvider {
   private final val entries: Set<SootMethod>

   public final val method: Set<SootMethod>
      public final get() {
         if (this.entries.isEmpty()) {
            logger.warn("No entry points");
         }

         logger.info(CustomEntryProvider::_get_method_$lambda$0);
         return this.entries;
      }


   public open val iterator: Flow<AnalyzeTask>

   init {
      this.entries = entries;
      this.iterator = FlowKt.flow((new Function2<FlowCollector<? super IEntryPointProvider.AnalyzeTask>, Continuation<? super Unit>, Object>(this, null) {
         int label;

         {
            super(2, `$completionx`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val `$this$flow`: FlowCollector = this.L$0 as FlowCollector;
                  val var10001: IEntryPointProvider.AnalyzeTask = new IEntryPointProvider.AnalyzeTask(this.this$0) {
                     private final java.util.Set<SootMethod> entries;
                     private final java.util.Set<SootClass> components;

                     {
                        this.entries = `$receiver`.getMethod();
                     }

                     @Override
                     public java.util.Set<SootMethod> getEntries() {
                        return this.entries;
                     }

                     @Override
                     public java.util.Set<SootMethod> getMethodsMustAnalyze() {
                        return this.getEntries();
                     }

                     @Override
                     public java.util.Set<SootClass> getComponents() {
                        return this.components;
                     }

                     @Override
                     public java.lang.String getName() {
                        return "(entries size: ${this.getEntries().size()})";
                     }

                     @Override
                     public void needConstructCallGraph(SootCtx sootCtx) {
                        sootCtx.constructCallGraph();
                     }

                     @Override
                     public java.util.Set<SootMethod> getAdditionalEntries() {
                        return IEntryPointProvider.AnalyzeTask.DefaultImpls.getAdditionalEntries(this);
                     }
                  };
                  val var10002: Continuation = this as Continuation;
                  this.label = 1;
                  if (`$this$flow`.emit(var10001, var10002) === var3x) {
                     return var3x;
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
            val var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         public final Object invoke(FlowCollector<? super IEntryPointProvider.AnalyzeTask> p1, Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   override fun startAnalyse() {
      IEntryPointProvider.DefaultImpls.startAnalyse(this);
   }

   override fun endAnalyse() {
      IEntryPointProvider.DefaultImpls.endAnalyse(this);
   }

   @JvmStatic
   fun `_get_method_$lambda$0`(`this$0`: CustomEntryProvider): Any {
      val var2: Array<Any> = new Object[]{`this$0`.entries.size()};
      val var10000: java.lang.String = java.lang.String.format("custom entry methods num :%d", Arrays.copyOf(var2, var2.length));
      return var10000;
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
