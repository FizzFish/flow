package cn.sast.framework.entries.component

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import soot.Scene
import soot.SootClass
import soot.SootMethod

public class ComponentEntryProvider(ctx: SootCtx, entries: Collection<String>) : IEntryPointProvider {
   private final val ctx: SootCtx
   private final val entries: Collection<String>

   public final val method: Set<SootMethod>
      public final get() {
         val var10000: SootMethod = new ComponentEntryPointCreator(this.entries).createDummyMain();
         if (!var10000.getDeclaringClass().isInScene()) {
            Scene.v().addClass(var10000.getDeclaringClass());
         }

         var10000.getDeclaringClass().setApplicationClass();
         return SetsKt.setOf(var10000);
      }


   public open val iterator: Flow<AnalyzeTask>

   init {
      this.ctx = ctx;
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
                     public java.util.Set<SootMethod> getMethodsMustAnalyze() {
                        return IEntryPointProvider.AnalyzeTask.DefaultImpls.getMethodsMustAnalyze(this);
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
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
