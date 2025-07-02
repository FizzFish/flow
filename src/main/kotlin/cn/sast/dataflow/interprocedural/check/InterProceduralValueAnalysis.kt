package cn.sast.dataflow.interprocedural.check

import cn.sast.api.config.ExtSettings
import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AIContext
import cn.sast.dataflow.interprocedural.analysis.AJimpleInterProceduralAnalysisKt
import cn.sast.dataflow.interprocedural.analysis.AbstractBOTTOM
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AbstractTOP
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.EntryParam
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphAbstract
import cn.sast.idfa.analysis.FixPointStatus
import cn.sast.idfa.analysis.InterproceduralCFG
import java.util.Collections
import java.util.HashSet
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import mu.KLogger
import soot.Body
import soot.G
import soot.Local
import soot.RefLikeType
import soot.RefType
import soot.Scene
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.DynamicInvokeExpr
import soot.jimple.InstanceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Jimple
import soot.jimple.NopStmt
import soot.jimple.StaticInvokeExpr

@SourceDebugExtension(["SMAP\nInterProceduralValueAnalysis.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InterProceduralValueAnalysis.kt\ncn/sast/dataflow/interprocedural/check/InterProceduralValueAnalysis\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,315:1\n1#2:316\n1863#3,2:317\n*S KotlinDebug\n*F\n+ 1 InterProceduralValueAnalysis.kt\ncn/sast/dataflow/interprocedural/check/InterProceduralValueAnalysis\n*L\n258#1:317,2\n*E\n"])
public abstract class InterProceduralValueAnalysis : ACheckCallAnalysis {
   private final val bottom: <unrepresentable>

   open fun InterProceduralValueAnalysis(vg: IVGlobal, hf: AbstractHeapFactory<IValue>, icfg: InterproceduralCFG) {
      super(hf, icfg);
      val unit: NopStmt = Jimple.v().newNopStmt();
      val method: SootMethod = Scene.v().makeSootMethod("initConstantPoolObjectData", CollectionsKt.emptyList(), G.v().soot_VoidType() as Type);
      val var10003: Unit = unit as Unit;
      hf.setConstantPoolObjectData(this.getTopState(new CallStackContext(null, var10003, method, 0)).builder());
      this.bottom = new AbstractBOTTOM<IValue>() {};
   }

   private fun getTopState(callStackContext: CallStackContext): IFact<IValue> {
      return new AbstractTOP<IValue>(callStackContext, this.getHf()) {
         {
            super(`$super_call_param$1`);
            this.$callStackContext = `$callStackContext`;
         }

         @Override
         public IFact.Builder<IValue> builder() {
            return new PointsToGraph(
                  this.getHf(),
                  this.getHf().getVg(),
                  this.$callStackContext,
                  ExtensionsKt.persistentHashMapOf(),
                  ExtensionsKt.persistentHashMapOf(),
                  ExtensionsKt.persistentHashSetOf()
               )
               .builder();
         }
      };
   }

   public open fun boundaryValue(entryPoint: SootMethod): IFact<IValue> {
      if (!entryPoint.isConcrete()) {
         return this.bottom;
      } else if (!entryPoint.hasActiveBody()) {
         return this.bottom;
      } else {
         val entryUnit: Unit = entryPoint.getActiveBody().getUnits().getFirst();
         val entryValue: IFact.Builder = this.getTopState(new CallStackContext(null, entryUnit, entryPoint, 0)).builder();
         var env: Int = 0;

         for (int var7 = entryPoint.getParameterCount(); argIndex < var7; argIndex++) {
            val var10000: AbstractHeapFactory = this.getHf();
            val var10001: Body = entryPoint.getActiveBody();
            var var12: Unit = AJimpleInterProceduralAnalysisKt.getParameterUnit(var10001, env);
            if (var12 == null) {
               var12 = entryUnit;
            }

            val envx: HeapValuesEnv = var10000.env(var12);
            IFact.Builder.DefaultImpls.assignNewExpr$default(
               entryValue, envx, env, this.getHf().push(envx, new EntryParam(entryPoint, env)).markOfEntryMethodParam(entryPoint).popHV(), false, 8, null
            );
         }

         if (!entryPoint.isStatic()) {
            val var11: AbstractHeapFactory = this.getHf();
            val var13: Body = entryPoint.getActiveBody();
            var var14: Unit = AJimpleInterProceduralAnalysisKt.getParameterUnit(var13, -1);
            if (var14 == null) {
               var14 = entryUnit;
            }

            val var9: HeapValuesEnv = var11.env(var14);
            IFact.Builder.DefaultImpls.assignNewExpr$default(
               entryValue, var9, -1, this.getHf().push(var9, new EntryParam(entryPoint, -1)).markOfEntryMethodParam(entryPoint).popHV(), false, 8, null
            );
         }

         this.getHf().getVg().setStaticFieldTrackingMode(this.getStaticFieldTrackingMode());
         if (this.getStaticFieldTrackingMode() != StaticFieldTrackingMode.None) {
            val var10: HeapValuesEnv = this.getHf().env(entryUnit);
            IFact.Builder.DefaultImpls.assignNewExpr$default(
               entryValue,
               var10,
               this.getHf().getVg().getGLOBAL_LOCAL(),
               this.getHf().push(var10, this.getHf().getVg().getGLOBAL_SITE()).markOfEntryMethodParam(entryPoint).popHV(),
               false,
               8,
               null
            );
         }

         return entryValue.build();
      }
   }

   public open fun copy(src: IFact<IValue>): IFact<IValue> {
      return src;
   }

   public open fun meet(op1: IFact<IValue>, op2: IFact<IValue>): IFact<IValue> {
      if (op1.isBottom()) {
         return op2;
      } else if (op2.isBottom()) {
         return op1;
      } else if (op1.isTop()) {
         return op2;
      } else if (op2.isTop()) {
         return op1;
      } else if (op1 === op2) {
         return op1;
      } else {
         val var3: IFact.Builder = op1.builder();
         var3.union(op2);
         return var3.build();
      }
   }

   public open fun shallowMeet(op1: IFact<IValue>, op2: IFact<IValue>): IFact<IValue> {
      throw new NotImplementedError("An operation is not implemented: Not yet implemented");
   }

   public open fun merge(local: IFact<IValue>, ret: IFact<IValue>): IFact<IValue> {
      return ret;
   }

   public open fun bottomValue(): IFact<IValue> {
      return this.bottom;
   }

   public open fun newExprEnv(context: AIContext, node: Unit, inValue: IFact<IValue>): AnyNewExprEnv {
      return new AnyNewExprEnv(context.getMethod(), node);
   }

   public fun isRecursive(callee: SootMethod, inValue: IFact<IValue>): Boolean {
      var cur: CallStackContext = (inValue as PointsToGraphAbstract).getCallStack();

      for (java.util.Set set = new LinkedHashSet(); cur != null; cur = cur.getCaller()) {
         if (!set.add(cur.getMethod())) {
            return true;
         }
      }

      return false;
   }

   public override fun isAnalyzable(callee: SootMethod, in1: IFact<IValue>): Boolean {
      if (!super.isAnalyzable(callee, in1)) {
         return false;
      } else {
         return !this.isRecursive(callee, in1);
      }
   }

   public override fun resolveTargets(callerMethod: SootMethod, ie: InvokeExpr, node: Unit, inValue: IFact<IValue>): Set<SootMethod> {
      if (ie !is StaticInvokeExpr && ie !is DynamicInvokeExpr) {
         val targets: java.util.Set = new HashSet();
         val var24: Value = (ie as InstanceInvokeExpr).getBase();
         val receiver: Local = var24 as Local;
         val heapNodes: IHeapValues = inValue.getTargetsUnsafe(var24 as Local);
         if (heapNodes != null && heapNodes.isNotEmpty()) {
            for (IValue v : heapNodes.getValues()) {
               val target: Type = `$i$f$forEach`.getType();
               val var25: RefLikeType = target as? RefLikeType;
               if ((target as? RefLikeType) != null) {
                  val var26: RefLikeType;
                  if (`$i$f$forEach`.typeIsConcrete()) {
                     var26 = var25;
                  } else if (var25 is RefType) {
                     val var14: Type = receiver.getType();
                     var var27: RefType = var14 as? RefType;
                     if ((var14 as? RefType) == null) {
                        var27 = Scene.v().getObjectType();
                     }

                     var26 = var27 as RefLikeType;
                  } else {
                     var26 = var25;
                  }

                  if (var26 != null) {
                     val var21: java.util.Iterator = SootUtilsKt.getCallTargets(var26 as Type, callerMethod, ie, false);

                     while (iter.hasNext()) {
                        targets.add(var21.next() as SootMethod);
                     }
                  }
               }
            }
         }

         if (targets.isEmpty()) {
            val var15: java.lang.Iterable;
            for (Object element$iv : var15) {
               targets.add(var19 as SootMethod);
            }
         }

         if (targets.isEmpty()) {
            return SetsKt.setOf((ie as InstanceInvokeExpr).getMethod());
         } else {
            val var16: Long = ExtSettings.INSTANCE.getDataFlowResolveTargetsMaxNum();
            if (var16 >= 0L && targets.size() >= var16) {
               logger.debug(InterProceduralValueAnalysis::resolveTargets$lambda$2);
               return SetsKt.setOf((ie as InstanceInvokeExpr).getMethod());
            } else {
               return targets;
            }
         }
      } else {
         val var10000: java.util.Set = Collections.singleton(ie.getMethod());
         return var10000;
      }
   }

   public open fun hasChange(context: AIContext, node: Unit, succ: Unit, old: IFact<IValue>, new: IFact<IValue>): FixPointStatus {
      var var10000: java.util.Map = context.getIteratorCount();
      val var8: Int = var10000.get(node) as Int;
      val count: Int = (var8 ?: 0) + 1;
      val var7: Int = count;
      var10000 = context.getIteratorCount();
      var10000.put(node, var7);
      if (!var5.isValid()) {
         return FixPointStatus.HasChange;
      } else if ((context.getMethod().getDeclaringClass().isApplicationClass() || count >= ExtSettings.INSTANCE.getDataFlowIteratorCountForLibClasses())
         && (!context.getMethod().getDeclaringClass().isApplicationClass() || count >= ExtSettings.INSTANCE.getDataFlowIteratorCountForAppClasses())) {
         val var10: java.util.Set = context.getWidenNode();
         return if (var10.add(TuplesKt.to(node, succ))) FixPointStatus.NeedWideningOperators else FixPointStatus.Fixpoint;
      } else {
         return old.hasChange(context, var5);
      }
   }

   @JvmStatic
   fun `resolveTargets$lambda$2`(`$node`: Unit, `$targets`: java.util.Set, `$callerMethod`: SootMethod): Any {
      return "Too many callee at $`$node`. size: ${`$targets`.size()}. in $`$callerMethod` line:${`$node`.getJavaSourceStartLineNumber()}";
   }

   @JvmStatic
   fun `logger$lambda$3`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      private final var logger: KLogger
   }
}
