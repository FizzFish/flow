package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.config.ExtSettings
import cn.sast.api.report.Counter
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl.EvalCall
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl.PostCall
import cn.sast.dataflow.util.SootUtilsKt
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.analysis.ForwardInterProceduralAnalysis.InvokeResult
import cn.sast.idfa.check.CallBackManager
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Intrinsics.Kotlin
import mu.KLogger
import soot.Body
import soot.Local
import soot.PrimType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.Type
import soot.Value
import soot.jimple.DefinitionStmt
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.toolkits.graph.BriefUnitGraph
import soot.toolkits.graph.DirectedGraph
import soot.toolkits.graph.UnitGraph

@SourceDebugExtension(["SMAP\nACheckCallAnalysis.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ACheckCallAnalysis.kt\ncn/sast/dataflow/interprocedural/analysis/ACheckCallAnalysis\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CheckerManager.kt\ncn/sast/idfa/check/CallBackManager\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,426:1\n1863#2,2:427\n1863#2:438\n1864#2:440\n1863#2,2:462\n1863#2,2:469\n1863#2,2:487\n1863#2,2:494\n1863#2,2:512\n83#3,3:429\n83#3,3:432\n83#3,3:435\n146#3,2:441\n137#3,14:443\n137#3,5:457\n137#3,5:464\n146#3,2:471\n137#3,14:473\n137#3,5:489\n146#3,2:496\n137#3,14:498\n1#4:439\n*S KotlinDebug\n*F\n+ 1 ACheckCallAnalysis.kt\ncn/sast/dataflow/interprocedural/analysis/ACheckCallAnalysis\n*L\n65#1:427,2\n108#1:438\n108#1:440\n199#1:462,2\n214#1:469,2\n254#1:487,2\n293#1:494,2\n329#1:512,2\n80#1:429,3\n86#1:432,3\n92#1:435,3\n157#1:441,2\n157#1:443,14\n199#1:457,5\n214#1:464,5\n254#1:471,2\n254#1:473,14\n293#1:489,5\n329#1:496,2\n329#1:498,14\n*E\n"])
public abstract class ACheckCallAnalysis : AJimpleInterProceduralAnalysis<IValue, AIContext> {
   public final val callBackManager: CallBackManager = new CallBackManager()
   public final val summaries: MutableList<SummaryHandlePackage<IValue>> = (new ArrayList()) as java.util.List
   public final val excludeMethods: Counter<SootMethod> = new Counter()

   open fun ACheckCallAnalysis(hf: AbstractHeapFactory<IValue>, icfg: InterproceduralCFG) {
      super(hf, icfg);
   }

   public fun registerWrapper(smr: String, isStatic: Boolean) {
      this.registerWrapper(SootUtilsKt.sootSignatureToRef(smr, isStatic));
   }

   public fun registerWrapper(smr: SootMethodRef) {
      val sm: SootMethod = smr.resolve();

      var var4: BriefUnitGraph;
      try {
         val var10000: BriefUnitGraph = new BriefUnitGraph;
         val var10002: Body = sm.retrieveActiveBody();
         if (var10002 == null) {
            return;
         }

         var10000./* $VF: Unable to resugar constructor */<init>(var10002);
         var4 = var10000;
      } catch (var6: Exception) {
         return;
      }

      val var10001: java.lang.String = sm.getSignature();
      this.registerJimpleWrapper(var10001, var4 as UnitGraph);
   }

   public fun registerClassAllWrapper(sc: String) {
      val var10001: SootClass = Scene.v().getSootClassUnsafe(sc, true);
      this.registerClassAllWrapper(var10001);
   }

   public fun registerClassAllWrapper(sc: SootClass) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val sm: SootMethod = `element$iv` as SootMethod;
         if ((`element$iv` as SootMethod).getSource() == null && !(`element$iv` as SootMethod).hasActiveBody()) {
            logger.warn(ACheckCallAnalysis::registerClassAllWrapper$lambda$1$lambda$0);
         } else {
            Scene.v().forceResolve(sm.getDeclaringClass().getName(), 3);
            val var10: Body = sm.retrieveActiveBody();
            if (var10 != null) {
               val ug: BriefUnitGraph = new BriefUnitGraph(var10);
               val var10001: java.lang.String = sm.getSignature();
               this.registerJimpleWrapper(var10001, ug as UnitGraph);
            }
         }
      }
   }

   public fun evalCallAtCaller(methodSignature: String, prevCall: (EvalCall) -> Unit) {
      val var10000: SootMethod = Scene.v().grabMethod(methodSignature);
      if (var10000 != null) {
         this.callBackManager
            .put(
               CallerSiteCBImpl.EvalCall::class.java,
               var10000,
               (
                  new Function2<CallerSiteCBImpl.EvalCall, Continuation<? super Unit>, Object>(prevCall) {
                     {
                        super(
                           2,
                           receiver,
                           Kotlin::class.java,
                           "suspendConversion0",
                           "evalCallAtCaller$lambda$2$suspendConversion0(Lkotlin/jvm/functions/Function1;Lcn/sast/dataflow/interprocedural/check/callback/CallerSiteCBImpl$EvalCall;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                           0
                        );
                     }

                     public final Object invoke(CallerSiteCBImpl.EvalCall p0, Continuation<? super Unit> $completion) {
                        return ACheckCallAnalysis.access$evalCallAtCaller$lambda$2$suspendConversion0(this.receiver as Function1, p0, `$completion`);
                     }
                  }
               ) as Function2
            );
      }
   }

   public fun evalCall(methodSignature: String, evalCall: (cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall) -> Unit) {
      val var10000: SootMethod = Scene.v().grabMethod(methodSignature);
      if (var10000 != null) {
         this.callBackManager
            .put(
               CalleeCBImpl.EvalCall::class.java,
               var10000,
               (
                  new Function2<CalleeCBImpl.EvalCall, Continuation<? super Unit>, Object>(evalCall) {
                     {
                        super(
                           2,
                           receiver,
                           Kotlin::class.java,
                           "suspendConversion0",
                           "evalCall$lambda$4$suspendConversion0$3(Lkotlin/jvm/functions/Function1;Lcn/sast/dataflow/interprocedural/check/callback/CalleeCBImpl$EvalCall;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                           0
                        );
                     }

                     public final Object invoke(CalleeCBImpl.EvalCall p0, Continuation<? super Unit> $completion) {
                        return ACheckCallAnalysis.access$evalCall$lambda$4$suspendConversion0$3(this.receiver as Function1, p0, `$completion`);
                     }
                  }
               ) as Function2
            );
      }
   }

   public fun postCallAtCaller(methodSignature: String, postCall: (PostCall) -> Unit) {
      val var10000: SootMethod = Scene.v().grabMethod(methodSignature);
      if (var10000 != null) {
         this.callBackManager
            .put(
               CallerSiteCBImpl.PostCall::class.java,
               var10000,
               (
                  new Function2<CallerSiteCBImpl.PostCall, Continuation<? super Unit>, Object>(postCall) {
                     {
                        super(
                           2,
                           receiver,
                           Kotlin::class.java,
                           "suspendConversion0",
                           "postCallAtCaller$lambda$6$suspendConversion0$5(Lkotlin/jvm/functions/Function1;Lcn/sast/dataflow/interprocedural/check/callback/CallerSiteCBImpl$PostCall;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
                           0
                        );
                     }

                     public final Object invoke(CallerSiteCBImpl.PostCall p0, Continuation<? super Unit> $completion) {
                        return ACheckCallAnalysis.access$postCallAtCaller$lambda$6$suspendConversion0$5(this.receiver as Function1, p0, `$completion`);
                     }
                  }
               ) as Function2
            );
      }
   }

   public fun registerJimpleWrapper(methodSignature: String, jimple: UnitGraph) {
      val var10000: SootMethod = Scene.v().grabMethod(methodSignature);
      if (var10000 != null) {
         this.callBackManager.putUnitGraphOverride(var10000, jimple);
      }
   }

   public override fun doAnalysis(entries: Collection<SootMethod>) {
      super.doAnalysis(entries);
      this.excludeMethods.clear();
   }

   public override fun doAnalysis(entries: Collection<SootMethod>, methodsMustAnalyze: Collection<SootMethod>) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         (`element$iv` as SummaryHandlePackage).register(this);
      }

      super.doAnalysis(entries, methodsMustAnalyze);
   }

   public override fun getCfg(method: SootMethod, isAnalyzable: Boolean): DirectedGraph<soot.Unit> {
      val var10000: UnitGraph = this.callBackManager.getUnitGraphOverride(method);
      return if (var10000 != null) var10000 as DirectedGraph else super.getCfg(method, isAnalyzable);
   }

   public fun returnPhantom(env: HeapValuesEnv, node: Stmt, builder: Builder<IValue>) {
      if (node.containsInvokeExpr()) {
         val lhsOp: InvokeExpr = node.getInvokeExpr();
         if (lhsOp is InstanceInvokeExpr) {
            val var10001: Value = (lhsOp as InstanceInvokeExpr).getBase();
            builder.summarizeTargetFields(var10001 as Local);
         }

         var i: Int = 0;

         for (int var6 = invokeExpr.getArgCount(); i < var6; i++) {
            val argValue: Value = lhsOp.getArg(i);
            if (argValue is Local) {
               builder.summarizeTargetFields(argValue);
            }
         }
      }

      if (node is DefinitionStmt && (node as DefinitionStmt).getLeftOp() != null) {
         val var10000: Value = (node as DefinitionStmt).getLeftOp();
         if (var10000 !is Local) {
            throw new RuntimeException(var10000.toString());
         }

         val var10003: AbstractHeapFactory = this.getHf();
         val var10005: AbstractHeapFactory = this.getHf();
         val var10007: Type = (node as DefinitionStmt).getRightOp().getType();
         IFact.Builder.DefaultImpls.assignNewExpr$default(
            builder,
            env,
            var10000,
            var10003.push(env, var10005.newSummaryVal(env, var10007, var10000)).markSummaryReturnValueFailedInHook().popHV(),
            false,
            8,
            null
         );
      }
   }

   public open suspend fun evalCall(context: AIContext, callee: SootMethod, node: soot.Unit, succ: soot.Unit, inValue: IFact<IValue>): InvokeResult<
         SootMethod,
         IFact<IValue>,
         IHeapValues<IValue>?
      >? {
      return evalCall$suspendImpl(this, context, callee, node, succ, inValue, `$completion`);
   }

   public open suspend fun computeEntryValue(context: AIContext): IFact<IValue> {
      return computeEntryValue$suspendImpl(this, context, `$completion`);
   }

   public open suspend fun prevCallFunction(context: AIContext, callee: SootMethod, node: soot.Unit, succ: soot.Unit, callSiteValue: IFact<IValue>): IFact<
         IValue
      > {
      return prevCallFunction$suspendImpl(this, context, callee, node, succ, callSiteValue, `$completion`);
   }

   public open fun computeExitValue(context: AIContext): IFact<IValue> {
      val exit: IFact = super.computeExitValue(context) as IFact;
      if (exit.isBottom()) {
         return exit;
      } else {
         val callee: SootMethod = context.getMethod();
         var var10000: java.util.List = context.getControlFlowGraph().getTails();
         var10000 = (java.util.List)CollectionsKt.first(var10000);
         val env: HeapValuesEnv = this.getHf().env((var10000 as Stmt) as soot.Unit);
         val exitBuilder: IFact.Builder = exit.builder();
         exitBuilder.gc();
         return exitBuilder.build();
      }
   }

   public open suspend fun returnFlowFunction(context: AIContext, node: soot.Unit, returnValue: IFact<IValue>): IFact<IValue> {
      return returnFlowFunction$suspendImpl(this, context, node, returnValue, `$completion`);
   }

   public open fun skip(callee: SootMethod) {
      this.excludeMethods.count(callee);
   }

   public open suspend fun postCallAtCallSite(
      context: AIContext,
      node: soot.Unit,
      succ: soot.Unit,
      in1: InvokeResult<SootMethod, IFact<IValue>, IHeapValues<IValue>?>
   ): InvokeResult<SootMethod, IFact<IValue>, IHeapValues<IValue>?> {
      return postCallAtCallSite$suspendImpl(this, context, node, succ, in1, `$completion`);
   }

   public open fun wideningFunction(context: AIContext, node: soot.Unit, succ: soot.Unit, `in`: IFact<IValue>): IFact<IValue>? {
      val out: IFact.Builder = `in`.builder();
      val oldSlots: java.util.Set = `in`.getSlots();
      val env: HeapValuesEnv = this.getHf().env(node);

      for (Object local : oldSlots) {
         val var10000: IValue = CollectionsKt.firstOrNull(`in`.getTargets(local).getValues() as java.lang.Iterable) as IValue;
         val anyType: Type = if (var10000 != null) var10000.getType() else null;
         if (anyType is PrimType) {
            val summary: CompanionV = this.getHf()
               .push(env, this.getHf().newSummaryVal(env, anyType, WideningPrimitive.INSTANCE))
               .markOfWideningSummary()
               .pop();
            val builder: IHeapValues.Builder = out.getTargets(local).builder();
            builder.add(summary);
            IFact.Builder.DefaultImpls.assignNewExpr$default(out, env, local, builder.build(), false, 8, null);
         }
      }

      return out.build();
   }

   public override fun isAnalyzable(callee: SootMethod, in1: IFact<IValue>): Boolean {
      if (this.callBackManager.getUnitGraphOverride(callee) != null) {
         return true;
      } else if (!super.isAnalyzable(callee, in1)) {
         return false;
      } else if (ACheckCallAnalysisKt.getExcludeSubSignature().contains(callee.getSubSignature())) {
         return false;
      } else {
         val hit: Int = this.excludeMethods.get(callee);
         if (hit > 0) {
            if (!callee.getDeclaringClass().isApplicationClass()) {
               return false;
            }

            if (hit > 2) {
               return false;
            }
         }

         val cfg: DirectedGraph = this.getCfg(callee, true);
         val dataFlowMethodUnitsSizeLimit: Int = ExtSettings.INSTANCE.getDataFlowMethodUnitsSizeLimit();
         return dataFlowMethodUnitsSizeLimit <= 0 || cfg.size() <= dataFlowMethodUnitsSizeLimit;
      }
   }

   @JvmStatic
   fun `registerClassAllWrapper$lambda$1$lambda$0`(`$sm`: SootMethod): Any {
      return "method source of $`$sm` is null";
   }

   @JvmStatic
   fun `evalCall$lambda$10`(): Any {
      return "IR intercept exception";
   }

   @JvmStatic
   fun `computeEntryValue$lambda$12$lambda$11`(): Any {
      return "IR intercept exception";
   }

   @JvmStatic
   fun `computeEntryValue$lambda$14$lambda$13`(): Any {
      return "IR intercept exception";
   }

   @JvmStatic
   fun `prevCallFunction$lambda$16$lambda$15`(): Any {
      return "IR intercept exception";
   }

   @JvmStatic
   fun `returnFlowFunction$lambda$18$lambda$17`(): Any {
      return "IR intercept exception";
   }

   @JvmStatic
   fun `postCallAtCallSite$lambda$20$lambda$19`(): Any {
      return "IR intercept exception";
   }

   @JvmStatic
   fun `logger$lambda$21`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final var logger: KLogger
   }
}
