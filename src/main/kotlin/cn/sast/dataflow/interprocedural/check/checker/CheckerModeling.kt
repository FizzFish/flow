package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.common.GLB
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.IBoolExpr
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IJDecl
import com.feysh.corax.config.api.IMethodDecl
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MethodConfig
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import mu.KLogger
import soot.SootMethod

@SourceDebugExtension(["SMAP\nCheckerModeling.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/CheckerModeling\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CheckerManager.kt\ncn/sast/idfa/check/CallBackManager\n*L\n1#1,565:1\n1863#2:566\n1864#2:585\n1863#2:586\n1864#2:605\n1863#2,2:606\n1863#2,2:608\n83#3,3:567\n83#3,3:570\n83#3,3:573\n83#3,3:576\n83#3,3:579\n83#3,3:582\n83#3,3:587\n83#3,3:590\n83#3,3:593\n83#3,3:596\n83#3,3:599\n83#3,3:602\n*S KotlinDebug\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/CheckerModeling\n*L\n392#1:566\n392#1:585\n436#1:586\n436#1:605\n500#1:606,2\n526#1:608,2\n399#1:567,3\n405#1:570,3\n411#1:573,3\n416#1:576,3\n422#1:579,3\n427#1:582,3\n443#1:587,3\n449#1:590,3\n455#1:593,3\n460#1:596,3\n466#1:599,3\n471#1:602,3\n*E\n"])
public class CheckerModeling(mainConfig: MainConfig, icfg: InterproceduralCFG, preAnalysis: PreAnalysisApi)
   : AIAnalysisBaseImpl,
   SummaryHandlePackage<IValue>,
   IAnalysisDepends {
   public final val mainConfig: MainConfig
   public final val icfg: InterproceduralCFG
   public open val preAnalysis: PreAnalysisApi

   public open val fastCache: FastCache
      public open get() {
         return FastCacheImpl.INSTANCE;
      }


   public open val scope: CoroutineScope
      public open get() {
         return GlobalScope.INSTANCE as CoroutineScope;
      }


   private final val summaries: MutableList<Triple<SootMethod, (MethodConfig) -> Unit, IStmt>>
   private final val checkPoints: MutableList<cn.sast.dataflow.interprocedural.check.checker.CheckerModeling.Checker>
   public open val error: ConfigInfoLogger

   init {
      this.$$delegate_0 = MainConfigKt.interProceduralAnalysisDepends(mainConfig);
      this.mainConfig = mainConfig;
      this.icfg = icfg;
      this.preAnalysis = preAnalysis;
      this.summaries = new ArrayList<>();
      this.checkPoints = new ArrayList<>();
      this.error = new ConfigInfoLogger();
   }

   public override fun ACheckCallAnalysis.register() {
      this.getLogger().info(CheckerModeling::register$lambda$0);

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val checker: Triple = `element$iv` as Triple;
         val imp: SootMethod = (`element$iv` as Triple).component1() as SootMethod;
         val cb: Function1 = (`element$iv` as Triple).component2() as Function1;
         val method: IStmt = checker.component3() as IStmt;
         val `this_$iv`: MethodConfig = new MethodConfig(MethodConfig.CheckCall.PostCallInCaller);
         cb.invoke(`this_$iv`);
         val `cb$iv`: ModelingCallBack = new ModelingCallBack(imp, method);
         switch (CheckerModeling.WhenMappings.$EnumSwitchMapping$0[imp.getAt().ordinal()]) {
            case 1:
               `$this$register`.getCallBackManager()
                  .put(
                     CallerSiteCBImpl.PrevCall::class.java,
                     imp,
                     (new Function2<CallerSiteCBImpl.PrevCall, Continuation<? super Unit>, Object>(`cb$iv`, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CallerSiteCBImpl.PrevCall = this.L$0 as CallerSiteCBImpl.PrevCall;
                                 this.$cb.model(this.$this_register.getIcfg(), `$this$put`.getHf(), `$this$put`.getEnv(), `$this$put`);
                                 return Unit.INSTANCE;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CallerSiteCBImpl.PrevCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 2:
               `$this$register`.getCallBackManager()
                  .put(
                     CallerSiteCBImpl.EvalCall::class.java,
                     imp,
                     (new Function2<CallerSiteCBImpl.EvalCall, Continuation<? super Unit>, Object>(`cb$iv`, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CallerSiteCBImpl.EvalCall = this.L$0 as CallerSiteCBImpl.EvalCall;
                                 this.$cb.model(this.$this_register.getIcfg(), `$this$put`.getHf(), `$this$put`.getEnv(), `$this$put`);
                                 return Unit.INSTANCE;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CallerSiteCBImpl.EvalCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 3:
               `$this$register`.getCallBackManager()
                  .put(
                     CallerSiteCBImpl.PostCall::class.java,
                     imp,
                     (new Function2<CallerSiteCBImpl.PostCall, Continuation<? super Unit>, Object>(`cb$iv`, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CallerSiteCBImpl.PostCall = this.L$0 as CallerSiteCBImpl.PostCall;
                                 this.$cb.model(this.$this_register.getIcfg(), `$this$put`.getHf(), `$this$put`.getEnv(), `$this$put`);
                                 return Unit.INSTANCE;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CallerSiteCBImpl.PostCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 4:
               `$this$register`.getCallBackManager()
                  .put(
                     CalleeCBImpl.PrevCall::class.java,
                     imp,
                     (new Function2<CalleeCBImpl.PrevCall, Continuation<? super Unit>, Object>(`cb$iv`, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CalleeCBImpl.PrevCall = this.L$0 as CalleeCBImpl.PrevCall;
                                 this.$cb.model(this.$this_register.getIcfg(), `$this$put`.getHf(), `$this$put`.getEnv(), `$this$put`);
                                 return Unit.INSTANCE;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CalleeCBImpl.PrevCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 5:
               `$this$register`.getCallBackManager()
                  .put(
                     CalleeCBImpl.EvalCall::class.java,
                     imp,
                     (new Function2<CalleeCBImpl.EvalCall, Continuation<? super Unit>, Object>(`cb$iv`, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CalleeCBImpl.EvalCall = this.L$0 as CalleeCBImpl.EvalCall;
                                 this.$cb.model(this.$this_register.getIcfg(), `$this$put`.getHf(), `$this$put`.getEnv(), `$this$put`);
                                 return Unit.INSTANCE;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CalleeCBImpl.EvalCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 6:
               `$this$register`.getCallBackManager()
                  .put(
                     CalleeCBImpl.PostCall::class.java,
                     imp,
                     (new Function2<CalleeCBImpl.PostCall, Continuation<? super Unit>, Object>(`cb$iv`, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CalleeCBImpl.PostCall = this.L$0 as CalleeCBImpl.PostCall;
                                 this.$cb.model(this.$this_register.getIcfg(), `$this$put`.getHf(), `$this$put`.getEnv(), `$this$put`);
                                 return Unit.INSTANCE;
                              default:
                                 throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                           }
                        }

                        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CalleeCBImpl.PostCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }

      this.getLogger().info(CheckerModeling::register$lambda$2);

      for (Object element$iv : $this$forEach$iv) {
         val var21: CheckerModeling.Checker = var20 as CheckerModeling.Checker;
         val var23: MethodConfig = new MethodConfig(MethodConfig.CheckCall.PostCallInCaller);
         var21.getConfig().invoke(var23);
         val var24: CheckCallBack = new CheckCallBack(var21.getAtMethod(), var21);
         val var25: SootMethod = var21.getAtMethod();
         switch (CheckerModeling.WhenMappings.$EnumSwitchMapping$0[imp.getAt().ordinal()]) {
            case 1:
               `$this$register`.getCallBackManager()
                  .put(
                     CallerSiteCBImpl.PrevCall::class.java,
                     var25,
                     (new Function2<CallerSiteCBImpl.PrevCall, Continuation<? super Unit>, Object>(var24, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CallerSiteCBImpl.PrevCall = this.L$0 as CallerSiteCBImpl.PrevCall;
                                 val var10000: CheckCallBack = this.$cb;
                                 val var10001: AbstractHeapFactory = `$this$put`.getHf();
                                 val var10002: HookEnv = `$this$put`.getEnv();
                                 val var10003: ICallCB = `$this$put`;
                                 val var10004: InterproceduralCFG = this.$this_register.getIcfg();
                                 val var10005: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.check(var10001, var10002, var10003, var10004, var10005) === var3x) {
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
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CallerSiteCBImpl.PrevCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 2:
               `$this$register`.getCallBackManager()
                  .put(
                     CallerSiteCBImpl.EvalCall::class.java,
                     var25,
                     (new Function2<CallerSiteCBImpl.EvalCall, Continuation<? super Unit>, Object>(var24, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CallerSiteCBImpl.EvalCall = this.L$0 as CallerSiteCBImpl.EvalCall;
                                 val var10000: CheckCallBack = this.$cb;
                                 val var10001: AbstractHeapFactory = `$this$put`.getHf();
                                 val var10002: HookEnv = `$this$put`.getEnv();
                                 val var10003: ICallCB = `$this$put`;
                                 val var10004: InterproceduralCFG = this.$this_register.getIcfg();
                                 val var10005: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.check(var10001, var10002, var10003, var10004, var10005) === var3x) {
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
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CallerSiteCBImpl.EvalCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 3:
               `$this$register`.getCallBackManager()
                  .put(
                     CallerSiteCBImpl.PostCall::class.java,
                     var25,
                     (new Function2<CallerSiteCBImpl.PostCall, Continuation<? super Unit>, Object>(var24, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CallerSiteCBImpl.PostCall = this.L$0 as CallerSiteCBImpl.PostCall;
                                 val var10000: CheckCallBack = this.$cb;
                                 val var10001: AbstractHeapFactory = `$this$put`.getHf();
                                 val var10002: HookEnv = `$this$put`.getEnv();
                                 val var10003: ICallCB = `$this$put`;
                                 val var10004: InterproceduralCFG = this.$this_register.getIcfg();
                                 val var10005: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.check(var10001, var10002, var10003, var10004, var10005) === var3x) {
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
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CallerSiteCBImpl.PostCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 4:
               `$this$register`.getCallBackManager()
                  .put(
                     CalleeCBImpl.PrevCall::class.java,
                     var25,
                     (new Function2<CalleeCBImpl.PrevCall, Continuation<? super Unit>, Object>(var24, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CalleeCBImpl.PrevCall = this.L$0 as CalleeCBImpl.PrevCall;
                                 val var10000: CheckCallBack = this.$cb;
                                 val var10001: AbstractHeapFactory = `$this$put`.getHf();
                                 val var10002: HookEnv = `$this$put`.getEnv();
                                 val var10003: ICallCB = `$this$put`;
                                 val var10004: InterproceduralCFG = this.$this_register.getIcfg();
                                 val var10005: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.check(var10001, var10002, var10003, var10004, var10005) === var3x) {
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
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CalleeCBImpl.PrevCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 5:
               `$this$register`.getCallBackManager()
                  .put(
                     CalleeCBImpl.EvalCall::class.java,
                     var25,
                     (new Function2<CalleeCBImpl.EvalCall, Continuation<? super Unit>, Object>(var24, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CalleeCBImpl.EvalCall = this.L$0 as CalleeCBImpl.EvalCall;
                                 val var10000: CheckCallBack = this.$cb;
                                 val var10001: AbstractHeapFactory = `$this$put`.getHf();
                                 val var10002: HookEnv = `$this$put`.getEnv();
                                 val var10003: ICallCB = `$this$put`;
                                 val var10004: InterproceduralCFG = this.$this_register.getIcfg();
                                 val var10005: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.check(var10001, var10002, var10003, var10004, var10005) === var3x) {
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
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CalleeCBImpl.EvalCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            case 6:
               `$this$register`.getCallBackManager()
                  .put(
                     CalleeCBImpl.PostCall::class.java,
                     var25,
                     (new Function2<CalleeCBImpl.PostCall, Continuation<? super Unit>, Object>(var24, `$this$register`, null) {
                        int label;

                        {
                           super(2, `$completionx`);
                           this.$cb = `$cb`;
                           this.$this_register = `$receiver`;
                        }

                        public final Object invokeSuspend(Object $result) {
                           val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                           switch (this.label) {
                              case 0:
                                 ResultKt.throwOnFailure(`$result`);
                                 val `$this$put`: CalleeCBImpl.PostCall = this.L$0 as CalleeCBImpl.PostCall;
                                 val var10000: CheckCallBack = this.$cb;
                                 val var10001: AbstractHeapFactory = `$this$put`.getHf();
                                 val var10002: HookEnv = `$this$put`.getEnv();
                                 val var10003: ICallCB = `$this$put`;
                                 val var10004: InterproceduralCFG = this.$this_register.getIcfg();
                                 val var10005: Continuation = this as Continuation;
                                 this.label = 1;
                                 if (var10000.check(var10001, var10002, var10003, var10004, var10005) === var3x) {
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
                           val var3: Function2 = new <anonymous constructor>(this.$cb, this.$this_register, `$completion`);
                           var3.L$0 = value;
                           return var3 as Continuation<Unit>;
                        }

                        public final Object invoke(CalleeCBImpl.PostCall p1, Continuation<? super Unit> p2) {
                           return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                        }
                     }) as Function2
                  );
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   public override fun addStmt(decl: IJDecl, config: (MethodConfig) -> Unit, stmt: IStmt) {
      if (decl is IMethodDecl) {
         (decl as IMethodDecl).getMatch();

         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            val it: SootMethod = `element$iv` as SootMethod;
            synchronized (this.summaries) {
               this.summaries.add(new Triple(it, config, stmt));
            }
         }
      } else {
         this.getLogger().debug(CheckerModeling::addStmt$lambda$6);
      }
   }

   public override fun check(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IBoolExpr, checkType: CheckType, env: (Env) -> Unit) {
      GLB.INSTANCE.plusAssign(checkType);
      if (this.mainConfig.isEnable(checkType)) {
         if (decl is IMethodDecl) {
            (decl as IMethodDecl).getMatch();

            val `$this$forEach$iv`: java.lang.Iterable;
            for (Object element$iv : $this$forEach$iv) {
               val it: SootMethod = `element$iv` as SootMethod;
               synchronized (this.checkPoints) {
                  this.checkPoints.add(new CheckerModeling.Checker(it, config, expr, checkType, env));
               }
            }
         } else {
            this.getLogger().error(CheckerModeling::check$lambda$9);
         }
      }
   }

   public override fun eval(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IExpr, accept: (Any) -> Unit) {
      throw new NotImplementedError("An operation is not implemented: Not yet implemented");
   }

   public override fun validate() {
   }

   public override fun toDecl(target: Any): XDecl {
      return this.$$delegate_0.toDecl(target);
   }

   public override infix fun XDecl.dependsOn(dep: XDecl) {
      this.$$delegate_0.dependsOn(`$this$dependsOn`, dep);
   }

   public override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
      this.$$delegate_0.dependsOn(`$this$dependsOn`, deps);
   }

   @JvmStatic
   fun `register$lambda$0`(`this$0`: CheckerModeling): Any {
      return "summaries model size: ${`this$0`.summaries.size()}";
   }

   @JvmStatic
   fun `register$lambda$2`(`this$0`: CheckerModeling): Any {
      return "check-points size: ${`this$0`.checkPoints.size()}";
   }

   @JvmStatic
   fun `addStmt$lambda$6`(`$decl`: IJDecl): Any {
      return "TODO: decl: $`$decl` not support";
   }

   @JvmStatic
   fun `check$lambda$9`(`$decl`: IJDecl): Any {
      return "TODO: decl: $`$decl` not support";
   }

   @JvmStatic
   fun `logger$lambda$10`(): Unit {
      return Unit.INSTANCE;
   }

   public data class Checker(atMethod: SootMethod, config: (MethodConfig) -> Unit, guard: IBoolExpr, report: CheckType, env: (Env) -> Unit) {
      public final val atMethod: SootMethod
      public final val config: (MethodConfig) -> Unit
      public final val guard: IBoolExpr
      public final val report: CheckType
      public final val env: (Env) -> Unit

      init {
         this.atMethod = atMethod;
         this.config = config;
         this.guard = guard;
         this.report = report;
         this.env = env;
      }

      public operator fun component1(): SootMethod {
         return this.atMethod;
      }

      public operator fun component2(): (MethodConfig) -> Unit {
         return this.config;
      }

      public operator fun component3(): IBoolExpr {
         return this.guard;
      }

      public operator fun component4(): CheckType {
         return this.report;
      }

      public operator fun component5(): (Env) -> Unit {
         return this.env;
      }

      public fun copy(
         atMethod: SootMethod = this.atMethod,
         config: (MethodConfig) -> Unit = this.config,
         guard: IBoolExpr = this.guard,
         report: CheckType = this.report,
         env: (Env) -> Unit = this.env
      ): cn.sast.dataflow.interprocedural.check.checker.CheckerModeling.Checker {
         return new CheckerModeling.Checker(atMethod, config, guard, report, env);
      }

      public override fun toString(): String {
         return "Checker(atMethod=${this.atMethod}, config=${this.config}, guard=${this.guard}, report=${this.report}, env=${this.env})";
      }

      public override fun hashCode(): Int {
         return (((this.atMethod.hashCode() * 31 + this.config.hashCode()) * 31 + this.guard.hashCode()) * 31 + this.report.hashCode()) * 31
            + this.env.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is CheckerModeling.Checker) {
            return false;
         } else {
            val var2: CheckerModeling.Checker = other as CheckerModeling.Checker;
            if (!(this.atMethod == (other as CheckerModeling.Checker).atMethod)) {
               return false;
            } else if (!(this.config == var2.config)) {
               return false;
            } else if (!(this.guard == var2.guard)) {
               return false;
            } else if (!(this.report == var2.report)) {
               return false;
            } else {
               return this.env == var2.env;
            }
         }
      }
   }

   public companion object {
      private final val logger: KLogger
   }
}
