package cn.sast.framework.entries.allmethods

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.Scene
import soot.SootClass
import soot.SootMethod

public class AllMethodsProvider(classes: Collection<SootClass> = Scene.v().getApplicationClasses() as java.util.Collection) : IEntryPointProvider {
   public final val classes: Collection<SootClass>
   public open val iterator: Flow<AnalyzeTask>

   init {
      this.classes = classes;
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
                     private final Void components;
                     private final java.lang.String name;

                     {
                        var `$this$flatMap$iv`: java.lang.Iterable = `$receiver`.getClasses();
                        var `destination$iv$iv`: java.util.Collection = new ArrayList();

                        for (Object element$iv$iv : $this$flatMap$iv) {
                           if ((`element$iv$iv` as SootClass).isInScene()) {
                              `destination$iv$iv`.add(`element$iv$iv`);
                           }
                        }

                        `$this$flatMap$iv` = `destination$iv$iv` as java.util.List;
                        `destination$iv$iv` = new ArrayList();

                        for (Object element$iv$ivx : $this$flatMap$iv) {
                           val var10000: java.util.List = (`element$iv$ivx` as SootClass).getMethods();
                           CollectionsKt.addAll(`destination$iv$iv`, var10000);
                        }

                        this.entries = CollectionsKt.toSet(`destination$iv$iv` as java.util.List);
                        this.name = "(entries size: ${this.getEntries().size()})";
                     }

                     @Override
                     public java.util.Set<SootMethod> getEntries() {
                        return this.entries;
                     }

                     public Void getComponents() {
                        return this.components;
                     }

                     @Override
                     public java.lang.String getName() {
                        return this.name;
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

   fun AllMethodsProvider() {
      this(null, 1, null);
   }
}
