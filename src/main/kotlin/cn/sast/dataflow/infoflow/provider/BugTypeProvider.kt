package cn.sast.dataflow.infoflow.provider

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.PreAnalysisCoroutineScopeKt
import cn.sast.common.GLB
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IBoolExpr
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.IClassDecl
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IFieldDecl
import com.feysh.corax.config.api.IJDecl
import com.feysh.corax.config.api.ILocalVarDecl
import com.feysh.corax.config.api.IMethodDecl
import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MethodConfig
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import mu.KLogger
import soot.Scene
import soot.SootMethod

@SourceDebugExtension(["SMAP\nBugTypeProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BugTypeProvider.kt\ncn/sast/dataflow/infoflow/provider/BugTypeProvider\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,84:1\n1797#2,3:85\n1#3:88\n*S KotlinDebug\n*F\n+ 1 BugTypeProvider.kt\ncn/sast/dataflow/infoflow/provider/BugTypeProvider\n*L\n34#1:85,3\n*E\n"])
public class BugTypeProvider(config: MainConfig, preAnalysisImpl: PreAnalysisCoroutineScope) {
   public final val config: MainConfig
   public final val preAnalysisImpl: PreAnalysisCoroutineScope
   private final var methodToCheckType: MutableMap<SootMethod, MutableSet<CheckType>>
   private final val aiCheckerImpl: AIAnalysisBaseImpl

   init {
      this.config = config;
      this.preAnalysisImpl = preAnalysisImpl;
      this.methodToCheckType = new LinkedHashMap<>();
      this.aiCheckerImpl = new AIAnalysisBaseImpl(this) {
         private final ConfigInfoLogger error;
         private final PreAnalysisApi preAnalysis;

         {
            this.this$0 = `$receiver`;
            this.error = new ConfigInfoLogger();
            this.preAnalysis = `$receiver`.getPreAnalysisImpl();
         }

         @Override
         public FastCache getFastCache() {
            return FastCacheImpl.INSTANCE;
         }

         public ConfigInfoLogger getError() {
            return this.error;
         }

         @Override
         public PreAnalysisApi getPreAnalysis() {
            return this.preAnalysis;
         }

         @Override
         public CoroutineScope getScope() {
            return GlobalScope.INSTANCE as CoroutineScope;
         }

         @Override
         public void addStmt(IJDecl decl, Function1<? super MethodConfig, Unit> config, IStmt stmt) {
         }

         // $VF: Extended synchronized range to monitorexit
         @Override
         public void check(
            IJDecl decl, Function1<? super MethodConfig, Unit> config, IBoolExpr expr, CheckType checkType, Function1<? super BugMessage.Env, Unit> env
         ) {
            GLB.INSTANCE.plusAssign(checkType);
            if (decl !is IMethodDecl) {
               if (decl is IClassDecl) {
                  throw new NotImplementedError(null, 1, null);
               } else if (decl is IFieldDecl) {
                  throw new NotImplementedError(null, 1, null);
               } else if (decl is ILocalVarDecl) {
                  throw new NotImplementedError(null, 1, null);
               } else {
                  throw new NoWhenBranchMatchedException();
               }
            } else {
               var var10000: IMethodMatch = (decl as IMethodDecl).getMatch();
               val var10001: Scene = Scene.v();

               for (SootMethod sink : var10000.matched(var10001)) {
                  val var10: java.util.Map = BugTypeProvider.access$getMethodToCheckType$p(this.this$0);
                  val var11: BugTypeProvider = this.this$0;
                  synchronized (var10) {
                     val `$this$getOrPut$iv`: java.util.Map = BugTypeProvider.access$getMethodToCheckType$p(var11);
                     val `value$iv`: Any = `$this$getOrPut$iv`.get(sink);
                     if (`value$iv` == null) {
                        val var20: Any = new LinkedHashSet();
                        `$this$getOrPut$iv`.put(sink, var20);
                        var10000 = (IMethodMatch)var20;
                     } else {
                        var10000 = (IMethodMatch)`value$iv`;
                     }

                     val var19: Boolean = (var10000 as java.util.Set).add(checkType);
                  }
               }
            }
         }

         @Override
         public void eval(IJDecl decl, Function1<? super MethodConfig, Unit> config, IExpr expr, Function1<Object, Unit> accept) {
         }

         @Override
         public void validate() {
         }
      };
   }

   public fun init() {
      BuildersKt.runBlocking$default(null, (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
         int label;

         {
            super(2, `$completionx`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val var10000: AIAnalysisBaseImpl = BugTypeProvider.access$getAiCheckerImpl$p(this.this$0);
                  val var10001: PreAnalysisCoroutineScope = this.this$0.getPreAnalysisImpl();
                  val var10002: Continuation = this as Continuation;
                  this.label = 1;
                  if (PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(var10000, var10001, var10002) === var2) {
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
            return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
         }

         public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2, 1, null);
   }

   public fun lookUpChecker(method: SootMethod): Set<IChecker> {
      val `$this$fold$iv`: java.lang.Iterable = this.lookUpCheckType(method);
      var `accumulator$iv`: Any = SetsKt.emptySet();

      for (Object element$iv : $this$fold$iv) {
         `accumulator$iv` = SetsKt.plus((java.util.Set)`accumulator$iv`, (`element$iv` as CheckType).getChecker());
      }

      return (if ((`accumulator$iv` as java.util.Collection).isEmpty()) SetsKt.emptySet() else `accumulator$iv` as java.util.Collection) as MutableSet<IChecker>;
   }

   public fun lookUpCheckType(method: SootMethod): Set<CheckType> {
      var var10000: java.util.Set = this.methodToCheckType.get(method);
      if (var10000 == null) {
         var10000 = SetsKt.emptySet();
      }

      return var10000;
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
