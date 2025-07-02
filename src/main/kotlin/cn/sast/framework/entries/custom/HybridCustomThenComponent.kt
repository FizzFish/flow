package cn.sast.framework.entries.custom

import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import cn.sast.framework.entries.component.ComponentEntryProvider
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.SootClass
import soot.SootMethod

public class HybridCustomThenComponent(ctx: SootCtx, entries: Set<SootMethod>) : CustomEntryProvider(entries) {
   public open val iterator: Flow<AnalyzeTask>

   init {
      this.iterator = FlowKt.flow(
         (
            new Function2<FlowCollector<? super IEntryPointProvider.AnalyzeTask>, Continuation<? super Unit>, Object>(this, ctx, entries, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
                  this.$ctx = `$ctx`;
                  this.$entries = `$entries`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var18: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        val `$this$flow`: FlowCollector = this.L$0 as FlowCollector;
                        val iterator: java.lang.Iterable = this.this$0.getMethod();
                        val var14: SootCtx = this.$ctx;
                        val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterator, 10));

                        for (Object item$iv$iv : iterator) {
                           `destination$iv$iv`.add((`item$iv$iv` as SootMethod).getSignature());
                        }

                        val component: ComponentEntryProvider = new ComponentEntryProvider(var14, `destination$iv$iv`);
                        val var19: Flow = FlowKt.flow(
                           (
                              new Function2<FlowCollector<? super IEntryPointProvider.AnalyzeTask>, Continuation<? super Unit>, Object>(
                                 component, this.$entries, null
                              ) {
                                 int label;

                                 {
                                    super(2, `$completionx`);
                                    this.$component = `$component`;
                                    this.$entries = `$entries`;
                                 }

                                 public final Object invokeSuspend(Object $result) {
                                    val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                       case 0:
                                          ResultKt.throwOnFailure(`$result`);
                                          val `$this$flow`: FlowCollector = this.L$0 as FlowCollector;
                                          val var10001: IEntryPointProvider.AnalyzeTask = new IEntryPointProvider.AnalyzeTask(this.$component, this.$entries) {
                                             private final java.util.Set<SootMethod> entries;
                                             private final java.util.Set<SootClass> components;

                                             {
                                                this.$entries = `$entries`;
                                                this.entries = `$component`.getMethod();
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
                                             public java.util.Set<SootMethod> getMethodsMustAnalyze() {
                                                return this.$entries;
                                             }

                                             @Override
                                             public java.lang.String getName() {
                                                return "(entries size: ${this.$entries.size()})";
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
                                    val var3: Function2 = new <anonymous constructor>(this.$component, this.$entries, `$completion`);
                                    var3.L$0 = value;
                                    return var3 as Continuation<Unit>;
                                 }

                                 public final Object invoke(FlowCollector<? super IEntryPointProvider.AnalyzeTask> p1, Continuation<? super Unit> p2) {
                                    return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                 }
                              }
                           ) as Function2
                        );
                        val var10002: Continuation = this as Continuation;
                        this.label = 1;
                        if (FlowKt.emitAll(`$this$flow`, var19, var10002) === var18) {
                           return var18;
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
                  val var3: Function2 = new <anonymous constructor>(this.this$0, this.$ctx, this.$entries, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               public final Object invoke(FlowCollector<? super IEntryPointProvider.AnalyzeTask> p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2
      );
   }
}
