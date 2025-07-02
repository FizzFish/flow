package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.api.util.OthersKt
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.ForwardInterProceduralAnalysis
import cn.sast.idfa.analysis.InterproceduralCFG
import cn.sast.idfa.analysis.ProcessInfoView
import cn.sast.idfa.analysis.ForwardInterProceduralAnalysis.InvokeResult
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import soot.ArrayType
import soot.BooleanType
import soot.G
import soot.Local
import soot.RefLikeType
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.VoidType
import soot.jimple.BinopExpr
import soot.jimple.CastExpr
import soot.jimple.Constant
import soot.jimple.DefinitionStmt
import soot.jimple.Expr
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InstanceOfExpr
import soot.jimple.InvokeExpr
import soot.jimple.ReturnStmt
import soot.jimple.ReturnVoidStmt
import soot.jimple.StaticFieldRef
import soot.jimple.Stmt
import soot.jimple.UnopExpr
import soot.jimple.infoflow.cfg.FlowDroidEssentialMethodTag
import soot.jimple.internal.JEqExpr
import soot.jimple.internal.JimpleLocal
import soot.tagkit.Tag
import soot.toolkits.graph.DirectedGraph

@SourceDebugExtension(["SMAP\nAJimpleInterProceduralAnalysis.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AJimpleInterProceduralAnalysis.kt\ncn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 3 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,859:1\n333#2,6:860\n333#2,6:870\n44#3:866\n44#3:867\n44#3:868\n44#3:869\n44#3:876\n1755#4,3:877\n*S KotlinDebug\n*F\n+ 1 AJimpleInterProceduralAnalysis.kt\ncn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis\n*L\n267#1:860,6\n586#1:870,6\n432#1:866\n442#1:867\n492#1:868\n499#1:869\n608#1:876\n768#1:877,3\n*E\n"])
public abstract class AJimpleInterProceduralAnalysis<V, CTX extends Context<SootMethod, Unit, IFact<V>>>
   : ForwardInterProceduralAnalysis<SootMethod, Unit, IFact<V>, IHeapValues<V>, CTX> {
   public final val hf: AbstractHeapFactory<Any>
   public final val icfg: InterproceduralCFG

   public final var analyzeLibraryClasses: Boolean
      internal set

   public final var needAnalyze: ((SootMethod) -> Boolean)?
      internal set

   open fun AJimpleInterProceduralAnalysis(hf: AbstractHeapFactory<V>, icfg: InterproceduralCFG) {
      super(null, 1, null);
      this.hf = hf;
      this.icfg = icfg;
      this.analyzeLibraryClasses = true;
   }

   public open fun programRepresentation(): InterproceduralCFG {
      return this.icfg;
   }

   public abstract fun newExprEnv(context: Any, node: Unit, inValue: IFact<Any>): AnyNewExprEnv {
   }

   public abstract fun resolveTargets(callerMethod: SootMethod, ie: InvokeExpr, node: Unit, inValue: IFact<Any>): Set<SootMethod> {
   }

   private fun resolveClinit(callerMethod: SootMethod, node: Unit, inValue: IFact<Any>): SootMethod? {
      if (inValue.isBottom()) {
         return null;
      } else {
         val stmt: Stmt = node as Stmt;
         if ((node as Stmt) !is DefinitionStmt) {
            return null;
         } else {
            var ret: SootMethod = null;
            var var10000: Value = (stmt as DefinitionStmt).getLeftOp();
            var10000 = (stmt as DefinitionStmt).getRightOp();
            var staticReference: StaticFieldRef = null;
            if (var10000 is StaticFieldRef) {
               staticReference = var10000 as StaticFieldRef;
            } else if (var10000 is StaticFieldRef) {
               staticReference = var10000 as StaticFieldRef;
            }

            if (staticReference != null) {
               val var15: SootClass = staticReference.getField().getDeclaringClass();
               if (var15.isLibraryClass()) {
                  val var16: java.util.Iterator = var15.getFields().iterator();
                  val clinit: java.util.Iterator = var16;

                  while (clinit.hasNext()) {
                     val clinitCalled: SootField = clinit.next() as SootField;
                     if (clinitCalled.isStatic() && clinitCalled.getType() is RefLikeType) {
                     }
                  }
               }

               if (var15.declaresMethodByName("<clinit>")) {
                  val var17: SootMethod = var15.getMethodByName("<clinit>");
                  if (!inValue.getCalledMethods().contains(var17)) {
                     ret = var17;
                  }
               }
            }

            if (ret == callerMethod) {
               ret = null;
            }

            return ret;
         }
      }
   }

   public open fun normalFlowUnAccessibleFunction(context: Any, node: Unit, succ: Unit, inValue: IFact<Any>): IFact<Any> {
      if (node !is ReturnStmt && node !is ReturnVoidStmt) {
         return inValue;
      } else {
         val out: IFact.Builder = inValue.builder();
         val method: SootMethod = context.getMethod() as SootMethod;
         if (!method.isStatic()) {
            out.summarizeTargetFields(-1);
         }

         var env: Int = 0;

         for (int var8 = method.getParameterCount(); i < var8; i++) {
            out.summarizeTargetFields(env);
         }

         if (method.getReturnType() !is VoidType) {
            val var9: HeapValuesEnv = this.hf.env(node);
            val var10000: AbstractHeapFactory = this.hf;
            val var10002: AbstractHeapFactory = this.hf;
            val var10004: Type = method.getReturnType();
            var var10: IHeapValues = var10000.push(var9, var10002.newSummaryVal(var9, var10004, "return")).markSummaryReturnValueInCalleeSite().popHV();
            if (method.getReturnType() is RefType) {
               var10 = var10.plus(this.hf.push(var9, this.hf.getNullConst()).markSummaryReturnValueInCalleeSite().popHV());
            }

            IFact.Builder.DefaultImpls.assignNewExpr$default(out, var9, this.hf.getVg().getRETURN_LOCAL(), var10, false, 8, null);
         }

         return out.build();
      }
   }

   public open fun shutDownAnalyze(isNegativeBranch: AtomicBoolean) {
   }

   public open fun newContext(cfg: DirectedGraph<Unit>, method: SootMethod, entryValue: IFact<Any>, isAnalyzable: Boolean): Any {
      val ctx: Context = super.newContext(cfg, method, entryValue, isAnalyzable);
      if (logger.isTraceEnabled()) {
         logger.trace(AJimpleInterProceduralAnalysis::newContext$lambda$0);
         logger.trace(AJimpleInterProceduralAnalysis::newContext$lambda$1);
      }

      if (ctx.getPathSensitiveEnable() && OthersKt.getSkipPathSensitive(method)) {
         ctx.setPathSensitiveEnable(false);
      }

      return (CTX)ctx;
   }

   public open suspend fun normalFlowFunction(context: Any, node: Unit, succ: Unit, inValue: IFact<Any>, isNegativeBranch: AtomicBoolean): IFact<Any> {
      return normalFlowFunction$suspendImpl(this, (CTX)context, node, succ, inValue, isNegativeBranch, `$completion`);
   }

   public open fun isDummyComponentInvoke(container: SootMethod, node: Unit, callee: SootMethod): Boolean {
      return false;
   }

   public open fun callEntryFlowFunction(context: Any, callee: SootMethod, node: Unit, succ: Unit, inValue: IFact<Any>): IFact<Any> {
      if (inValue.isBottom()) {
         return inValue;
      } else {
         val callStmt: Stmt = node as Stmt;
         val isDummyComponentInvoke: Boolean = this.isDummyComponentInvoke(context.getMethod() as SootMethod, node, callee);
         val caller: SootMethod = context.getMethod() as SootMethod;
         val callEdge: IFact.Builder = inValue.builder();
         if (!caller.isStatic()) {
            callEdge.kill(-1);
         }

         var env: Int = 0;

         for (int invokeExpr = caller.getParameterCount(); callerArgs < invokeExpr; callerArgs++) {
            callEdge.kill(env);
         }

         val var21: HeapValuesEnv = this.hf.env(node);
         if (callStmt.containsInvokeExpr()) {
            val var22: InvokeExpr = callStmt.getInvokeExpr();
            if (var22 is InstanceInvokeExpr && caller.hasActiveBody()) {
               val var10002: Int = -1;
               val var10003: Value = (var22 as InstanceInvokeExpr).getBase();
               callEdge.assignLocal(var21, var10002, var10003 as Local);
            }

            var callerLocal: Int = 0;

            for (int var13 = invokeExpr.getArgCount(); i < var13; i++) {
               val var14: Value = var22.getArg(callerLocal);
               if (var14 is Local) {
                  if (isDummyComponentInvoke && (var14 as Local).getType() is ArrayType) {
                     val var10000: AbstractHeapFactory = this.hf;
                     val var41: Type = (var14 as Local).getType();
                     IFact.Builder.DefaultImpls.assignNewExpr$default(
                        callEdge,
                        var21,
                        callerLocal,
                        this.hf.push(var21, (V)var10000.newSummaryVal(var21, var41, "dummyMainMethodArgArrayElement")).markOfEntryMethodParam(callee).popHV(),
                        false,
                        8,
                        null
                     );
                  } else {
                     callEdge.assignLocal(var21, callerLocal, var14);
                  }
               } else {
                  if (var14 !is Constant) {
                     throw new RuntimeException(var14.toString());
                  }

                  val `$i$f$of`: Type = (var14 as Constant).getType();
                  val var36: Type;
                  if (`$i$f$of` is RefLikeType) {
                     var36 = `$i$f$of`;
                  } else {
                     var36 = var22.getMethodRef().getParameterType(callerLocal);
                  }

                  IFact.Builder.DefaultImpls.assignNewExpr$default(
                     callEdge,
                     var21,
                     callerLocal,
                     if (!isDummyComponentInvoke)
                        JOperatorV.DefaultImpls.markOfConstant$default(
                              this.hf.push(var21, this.hf.newConstVal(var14 as Constant, var36)), var14 as Constant, null, 2, null
                           )
                           .popHV()
                        else
                        this.hf.push(var21, this.hf.newSummaryVal(var21, var36, "dummyMainMethodArg")).markOfEntryMethodParam(callee).popHV(),
                     false,
                     8,
                     null
                  );
               }
            }
         } else {
            if (!callee.isStaticInitializer()) {
               throw new IllegalArgumentException("Failed requirement.".toString());
            }

            val var38: java.util.Iterator = callee.getDeclaringClass().getFields().iterator();
            val var23: java.util.Iterator = var38;

            while (var23.hasNext()) {
               val var25: SootField = var23.next() as SootField;
               val var39: IVGlobal = this.hf.getVg();
               val var10001: Type = var25.getType();
               val var28: Pair = var39.defaultValue(var10001);
               val var29: Constant = var28.component1() as Constant;
               val var31: Type = var28.component2() as Type;
               if (var25.isStatic()) {
                  val var42: java.lang.String = this.hf.getVg().getGLOBAL_LOCAL();
                  val var33: FieldUtil = FieldUtil.INSTANCE;
                  callEdge.setFieldNew(
                     var21,
                     var42,
                     new JSootFieldType(var25),
                     JOperatorV.DefaultImpls.markOfConstant$default(this.hf.push(var21, this.hf.newConstVal(var29, var31)), var29, null, 2, null).popHV()
                  );
               }
            }
         }

         if (caller.hasActiveBody()) {
            val var40: java.util.Iterator = caller.getActiveBody().getLocals().iterator();
            val var24: java.util.Iterator = var40;

            while (var24.hasNext()) {
               val var26: Local = var24.next() as Local;
               callEdge.kill(var26);
            }
         }

         callEdge.kill(this.hf.getVg().getRETURN_LOCAL());
         callEdge.callEntryFlowFunction(context, callee, node, succ);
         if (this.hf.getVg().getStaticFieldTrackingMode() === StaticFieldTrackingMode.ContextFlowInsensitive
            || callee.isJavaLibraryMethod()
            || callee.getDeclaringClass().isLibraryClass()) {
            callEdge.kill(this.hf.getVg().getGLOBAL_LOCAL());
         }

         callEdge.gc();
         if (this.hf.getVg().getStaticFieldTrackingMode() === StaticFieldTrackingMode.ContextFlowInsensitive) {
            IFact.Builder.DefaultImpls.assignNewExpr$default(
               callEdge, var21, this.hf.getVg().getGLOBAL_LOCAL(), this.hf.push(var21, (V)this.hf.getVg().getGLOBAL_SITE()).popHV(), false, 8, null
            );
         }

         return callEdge.build();
      }
   }

   public open fun recursiveCallFlowFunction(
      context: Any,
      callee: SootMethod,
      node: Unit,
      succ: Unit,
      inValue: IFact<Any>,
      siteValue: IFact<Any>,
      isAnalyzable: Boolean
   ): InvokeResult<SootMethod, IFact<Any>, IHeapValues<Any>?> {
      val returnType: Type = callee.getReturnType();
      val hasReturnValue: Boolean = !(returnType == G.v().soot_VoidType());
      val intraEdge: IFact.Builder = siteValue.builder();
      val var13: IHeapValues;
      if (hasReturnValue) {
         val env: HeapValuesEnv = this.hf.env(node);
         val var10000: AbstractHeapFactory = this.hf;
         val var10002: AbstractHeapFactory = this.hf;
         var13 = var10000.push(env, var10002.newSummaryVal(env, returnType, "recursiveReturn")).markSummaryReturnValueInCalleeSite().popHV();
      } else {
         var13 = null;
      }

      intraEdge.addCalledMethod(callee);
      return new ForwardInterProceduralAnalysis.InvokeResult<>(callee, intraEdge.build(), var13);
   }

   public open fun failedInvokeResult(
      context: Any,
      callee: SootMethod,
      node: Unit,
      succ: Unit,
      inValue: IFact<Any>,
      siteValue: IFact<Any>,
      isAnalyzable: Boolean
   ): InvokeResult<SootMethod, IFact<Any>, IHeapValues<Any>?> {
      val returnType: Type = callee.getReturnType();
      val hasReturnValue: Boolean = !(returnType == G.v().soot_VoidType());
      val intraEdge: IFact.Builder = siteValue.builder();
      val var13: IHeapValues;
      if (hasReturnValue) {
         val env: HeapValuesEnv = this.hf.env(node);
         val var10000: AbstractHeapFactory = this.hf;
         val var10002: AbstractHeapFactory = this.hf;
         var13 = var10000.push(env, var10002.newSummaryVal(env, returnType, "failedReturn")).markSummaryReturnValueInCalleeSite().popHV();
      } else {
         var13 = null;
      }

      intraEdge.addCalledMethod(callee);
      return new ForwardInterProceduralAnalysis.InvokeResult<>(callee, intraEdge.build(), var13);
   }

   public open fun callExitFlowFunction(
      context: Any,
      siteValue: IFact<Any>,
      callee: SootMethod,
      callEdgeValue: IFact<Any>,
      calleeCtx: Any,
      node: Unit,
      succ: Unit,
      isAnalyzable: Boolean
   ): InvokeResult<SootMethod, IFact<Any>, IHeapValues<Any>?> {
      val intraEdge: IFact.Builder = siteValue.builder();
      var var10000: Any = calleeCtx.getExitValue();
      val calleeExitAbs: IFact = var10000 as IFact;
      val returnType: Type = callee.getReturnType();
      val hasReturnValue: Boolean = !(returnType == G.v().soot_VoidType());
      val env: HeapValuesEnv = this.hf.env(node);
      var returnValue: IHeapValues = intraEdge.updateIntraEdge(env, context, calleeCtx, callEdgeValue, hasReturnValue);
      if (hasReturnValue && (returnValue == null || returnValue.isEmpty())) {
         logger.trace(AJimpleInterProceduralAnalysis::callExitFlowFunction$lambda$6);
         var10000 = this.hf;
         val var10002: AbstractHeapFactory = this.hf;
         returnValue = ((AbstractHeapFactory<Object>)var10000).push(
               env, var10002.newSummaryVal(env, returnType, "ret_${callee.getDeclaringClass().getShortName()}::${callee.getName()}")
            )
            .markSummaryReturnValueInCalleeSite()
            .popHV();
      }

      intraEdge.addCalledMethod(callee);
      return new ForwardInterProceduralAnalysis.InvokeResult<>(callee, intraEdge.build(), returnValue);
   }

   public open fun getCfg(method: SootMethod, isAnalyzable: Boolean): DirectedGraph<Unit> {
      val var4: DirectedGraph;
      if (method.hasActiveBody()) {
         val cfg: DirectedGraph = this.programRepresentation().getControlFlowGraph(method);
         val var10000: java.util.List = cfg.getHeads();
         var4 = if (!var10000.isEmpty()) cfg else this.programRepresentation().getSummaryControlFlowGraph(method);
      } else {
         var4 = this.programRepresentation().getSummaryControlFlowGraph(method);
      }

      return var4;
   }

   public open fun isAnalyzable(callee: SootMethod, in1: IFact<Any>): Boolean {
      if (!this.icfg.isAnalyzable(callee)) {
         return false;
      } else if (this.needAnalyze != null && this.needAnalyze.invoke(callee) as java.lang.Boolean) {
         return true;
      } else {
         if (this.analyzeLibraryClasses) {
            val `$this$any$iv`: Int = ExtSettings.INSTANCE.getCalleeDepChainMaxNumForLibClassesInInterProceduraldataFlow();
            if (`$this$any$iv` >= 0) {
               var `$i$f$any`: CallStackContext = in1.getCallStack();
               val libraryMethods: java.util.List = new ArrayList();
               if (!callee.getDeclaringClass().isApplicationClass()) {
                  libraryMethods.add(callee);
               }

               while (cur != null) {
                  if (!`$i$f$any`.getMethod().getDeclaringClass().isApplicationClass()) {
                     libraryMethods.add(`$i$f$any`.getMethod());
                  }

                  if (libraryMethods.size() > `$this$any$iv`) {
                     return false;
                  }

                  `$i$f$any` = `$i$f$any`.getCaller();
               }
            }
         } else if (!callee.getDeclaringClass().isApplicationClass()) {
            return false;
         }

         val var10000: java.util.List = callee.getTags();
         val var9: java.lang.Iterable = var10000;
         var var12: Boolean;
         if (var10000 is java.util.Collection && (var10000 as java.util.Collection).isEmpty()) {
            var12 = false;
         } else {
            val var11: java.util.Iterator = var9.iterator();

            while (true) {
               if (!var11.hasNext()) {
                  var12 = false;
                  break;
               }

               if (var11.next() as Tag is FlowDroidEssentialMethodTag) {
                  var12 = true;
                  break;
               }
            }
         }

         if (var12) {
            return true;
         } else {
            return !Scene.v().isExcluded(callee.getDeclaringClass());
         }
      }
   }

   public open fun doAnalysis(entries: Collection<SootMethod>, methodsMustAnalyze: Collection<SootMethod>) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:276)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc_w "entries"
      // 004: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 007: aload 2
      // 008: ldc_w "methodsMustAnalyze"
      // 00b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 00e: new java/util/LinkedHashSet
      // 011: dup
      // 012: invokespecial java/util/LinkedHashSet.<init> ()V
      // 015: checkcast java/util/Set
      // 018: astore 3
      // 019: aload 2
      // 01a: checkcast java/lang/Iterable
      // 01d: invokestatic kotlin/collections/CollectionsKt.toSet (Ljava/lang/Iterable;)Ljava/util/Set;
      // 020: aload 1
      // 021: checkcast java/lang/Iterable
      // 024: invokestatic kotlin/collections/SetsKt.plus (Ljava/util/Set;Ljava/lang/Iterable;)Ljava/util/Set;
      // 027: astore 4
      // 029: getstatic cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.logger Lmu/KLogger;
      // 02c: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.doAnalysis$lambda$8 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 031: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 036: new kotlin/jvm/internal/Ref$ObjectRef
      // 039: dup
      // 03a: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
      // 03d: astore 5
      // 03f: aload 5
      // 041: aload 1
      // 042: checkcast java/lang/Iterable
      // 045: invokestatic kotlin/collections/CollectionsKt.toSet (Ljava/lang/Iterable;)Ljava/util/Set;
      // 048: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 04b: aload 4
      // 04d: astore 6
      // 04f: new kotlin/jvm/internal/Ref$IntRef
      // 052: dup
      // 053: invokespecial kotlin/jvm/internal/Ref$IntRef.<init> ()V
      // 056: astore 7
      // 058: aload 6
      // 05a: checkcast java/util/Collection
      // 05d: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 062: ifne 069
      // 065: bipush 1
      // 066: goto 06a
      // 069: bipush 0
      // 06a: ifne 085
      // 06d: aload 5
      // 06f: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 072: checkcast java/util/Collection
      // 075: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 07a: ifne 081
      // 07d: bipush 1
      // 07e: goto 082
      // 081: bipush 0
      // 082: ifeq 13a
      // 085: aload 5
      // 087: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 08a: checkcast java/util/Set
      // 08d: invokeinterface java/util/Set.isEmpty ()Z 1
      // 092: ifeq 09c
      // 095: aload 5
      // 097: aload 6
      // 099: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 09c: getstatic cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.logger Lmu/KLogger;
      // 09f: aload 7
      // 0a1: aload 5
      // 0a3: aload 2
      // 0a4: invokedynamic invoke (Lkotlin/jvm/internal/Ref$IntRef;Lkotlin/jvm/internal/Ref$ObjectRef;Ljava/util/Collection;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.doAnalysis$lambda$9 (Lkotlin/jvm/internal/Ref$IntRef;Lkotlin/jvm/internal/Ref$ObjectRef;Ljava/util/Collection;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 0a9: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 0ae: new cn/sast/common/CustomRepeatingTimer
      // 0b1: dup
      // 0b2: ldc2_w 60000
      // 0b5: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.doAnalysis$lambda$10 ()Lkotlin/Unit;, ()Lkotlin/Unit; ]
      // 0ba: invokespecial cn/sast/common/CustomRepeatingTimer.<init> (JLkotlin/jvm/functions/Function0;)V
      // 0bd: astore 9
      // 0bf: aload 9
      // 0c1: astore 10
      // 0c3: bipush 0
      // 0c4: istore 11
      // 0c6: aload 10
      // 0c8: bipush 1
      // 0c9: invokevirtual cn/sast/common/CustomRepeatingTimer.setRepeats (Z)V
      // 0cc: aload 10
      // 0ce: invokevirtual cn/sast/common/CustomRepeatingTimer.start ()V
      // 0d1: nop
      // 0d2: aload 9
      // 0d4: astore 8
      // 0d6: nop
      // 0d7: aload 0
      // 0d8: aload 5
      // 0da: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 0dd: checkcast java/util/Collection
      // 0e0: invokevirtual cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.doAnalysis (Ljava/util/Collection;)V
      // 0e3: aload 8
      // 0e5: invokevirtual cn/sast/common/CustomRepeatingTimer.stop ()V
      // 0e8: goto 0f5
      // 0eb: astore 9
      // 0ed: aload 8
      // 0ef: invokevirtual cn/sast/common/CustomRepeatingTimer.stop ()V
      // 0f2: aload 9
      // 0f4: athrow
      // 0f5: aload 7
      // 0f7: getfield kotlin/jvm/internal/Ref$IntRef.element I
      // 0fa: istore 9
      // 0fc: aload 7
      // 0fe: iload 9
      // 100: bipush 1
      // 101: iadd
      // 102: putfield kotlin/jvm/internal/Ref$IntRef.element I
      // 105: aload 3
      // 106: checkcast java/util/Collection
      // 109: aload 0
      // 10a: invokevirtual cn/sast/dataflow/interprocedural/analysis/AJimpleInterProceduralAnalysis.getReachableMethods ()Lsoot/jimple/infoflow/collect/ConcurrentHashSet;
      // 10d: checkcast java/lang/Iterable
      // 110: invokestatic kotlin/collections/CollectionsKt.addAll (Ljava/util/Collection;Ljava/lang/Iterable;)Z
      // 113: pop
      // 114: aload 4
      // 116: aload 3
      // 117: checkcast java/lang/Iterable
      // 11a: invokestatic kotlin/collections/SetsKt.minus (Ljava/util/Set;Ljava/lang/Iterable;)Ljava/util/Set;
      // 11d: astore 9
      // 11f: aload 6
      // 121: aload 9
      // 123: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 126: ifeq 12c
      // 129: goto 13a
      // 12c: aload 9
      // 12e: astore 6
      // 130: aload 5
      // 132: aload 9
      // 134: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 137: goto 058
      // 13a: return
   }

   public fun Builder<Any>.resolveExpr(env: HeapValuesEnv, expr: Expr, resType: Type): IHeapValues<Any> {
      var var25: IHeapValues;
      if (expr is CastExpr) {
         val op1Values: Type = (expr as CastExpr).getCastType();
         val var10002: Value = (expr as CastExpr).getOp();
         val var10003: Type = (expr as CastExpr).getOp().getType();
         val op2Values: IHeapValues = `$this$resolveExpr`.getOfSootValue(env, var10002, var10003);
         val var10000: AbstractHeapFactory = `$this$resolveExpr`.getHf();
         val var13: IOpCalculator = var10000.resolveCast(env, `$this$resolveExpr`, op1Values, op2Values);
         if (var13 != null) {
            val var14: IHeapValues.Builder = var13.getRes();
            if (var14 != null) {
               var25 = var14.build();
               if (var25 != null) {
                  return var25;
               }
            }
         }

         var25 = op2Values;
      } else if (expr is InstanceOfExpr) {
         val var8: Type = (expr as InstanceOfExpr).getCheckType();
         val var17: Value = (expr as InstanceOfExpr).getOp();
         val var21: Type = (expr as InstanceOfExpr).getOp().getType();
         val var11: IHeapValues = `$this$resolveExpr`.getOfSootValue(env, var17, var21);
         val var16: AbstractHeapFactory = `$this$resolveExpr`.getHf();
         var25 = var16.resolveInstanceOf(env, var11, var8).getRes().build();
      } else if (expr is UnopExpr) {
         val var18: Value = (expr as UnopExpr).getOp();
         val var22: Type = (expr as UnopExpr).getOp().getType();
         var25 = `$this$resolveExpr`.getHf()
            .resolveUnop(env, `$this$resolveExpr`, `$this$resolveExpr`.getOfSootValue(env, var18, var22), expr as UnopExpr, resType)
            .getRes()
            .build();
      } else {
         if (expr !is BinopExpr) {
            `$this$resolveExpr`.getHf().getLogger().error(AJimpleInterProceduralAnalysis::resolveExpr$lambda$12);
            return `$this$resolveExpr`.getHf().empty();
         }

         var var19: Value = (expr as BinopExpr).getOp1();
         var var23: Type = (expr as BinopExpr).getOp1().getType();
         val var10: IHeapValues = `$this$resolveExpr`.getOfSootValue(env, var19, var23);
         var19 = (expr as BinopExpr).getOp2();
         var23 = (expr as BinopExpr).getOp2().getType();
         var25 = `$this$resolveExpr`.getHf()
            .resolveBinop(env, `$this$resolveExpr`, var10, `$this$resolveExpr`.getOfSootValue(env, var19, var23), expr as BinopExpr, resType)
            .getRes()
            .build();
      }

      return var25;
   }

   @JvmStatic
   fun `newContext$lambda$0`(`$method`: SootMethod): Any {
      return "new context for $`$method`";
   }

   @JvmStatic
   fun `newContext$lambda$1`(`$method`: SootMethod): Any {
      return `$method`.getActiveBody();
   }

   @JvmStatic
   fun <V, CTX extends Context<SootMethod, Unit, IFact<V>>> `normalFlowFunction$lambda$3$check`(
      `this$0`: AJimpleInterProceduralAnalysis<V, CTX>,
      env: HeapValuesEnv,
      out: ObjectRef<IFactBuilder<V>>,
      keyValue: IHeapValues<V>,
      key: Value,
      caseValue: IHeapValues<V>
   ): java.lang.Boolean {
      val resType: BooleanType = G.v().soot_BooleanType();
      val var10000: AbstractHeapFactory = `this$0`.hf;
      val var10002: IFact.Builder = out.element as IFact.Builder;
      val var10005: BinopExpr = (new JEqExpr((new JimpleLocal("a", key.getType())) as Value, (new JimpleLocal("b", key.getType())) as Value)) as BinopExpr;
      val r: IOpCalculator = var10000.resolveBinop(env, var10002, keyValue, caseValue, var10005, resType as Type);
      val eq: IHeapValues = r.getRes().build();
      if (r.isFullySimplified() && eq.isSingle()) {
         val var9: Any = eq.getSingle().getValue();
         return FactValuesKt.getBooleanValue(var9 as IValue, true);
      } else {
         return null;
      }
   }

   @JvmStatic
   fun `callExitFlowFunction$lambda$6`(`$env`: HeapValuesEnv, `$calleeExitAbs`: IFact): Any {
      return "returnValue is empty. at $`$env` exitValue: $`$calleeExitAbs`";
   }

   @JvmStatic
   fun `doAnalysis$lambda$8`(): Any {
      return "Before Analyze: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `doAnalysis$lambda$9`(`$count`: IntRef, `$works`: ObjectRef, `$methodsMustAnalyze`: java.util.Collection): Any {
      return "iteration ${`$count`.element} : [${(`$works`.element as java.util.Set).size()}/${`$methodsMustAnalyze`.size()}]";
   }

   @JvmStatic
   fun `doAnalysis$lambda$10`(): kotlin.Unit {
      System.gc();
      return kotlin.Unit.INSTANCE;
   }

   @JvmStatic
   fun `resolveExpr$lambda$12`(`$expr`: Expr): Any {
      return "resolveExpr: not yet impl type of expr $`$expr`";
   }

   @JvmStatic
   fun `logger$lambda$13`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      private final var logger: KLogger
   }
}
