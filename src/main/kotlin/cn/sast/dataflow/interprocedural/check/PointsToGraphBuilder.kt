package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AIContext
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.AnyNewValue
import cn.sast.dataflow.interprocedural.analysis.CallStackContext
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapDataBuilder
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IFieldManager
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphAbstract
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphBuilderAbstract
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapKV
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapKV
import cn.sast.dataflow.interprocedural.override.lang.WString
import cn.sast.idfa.analysis.Context
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.IntType
import soot.RefType
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.jimple.AnyNewExpr
import soot.jimple.Constant
import soot.jimple.IntConstant
import soot.jimple.NewArrayExpr
import soot.jimple.StringConstant

@SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 extensions.kt\nkotlinx/collections/immutable/ExtensionsKt\n*L\n1#1,612:1\n49#2:613\n44#2:614\n44#2:615\n44#2:616\n1#3:617\n362#4:618\n362#4:619\n362#4:620\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder\n*L\n308#1:613\n371#1:614\n372#1:615\n373#1:616\n556#1:618\n593#1:619\n596#1:620\n*E\n"])
public open class PointsToGraphBuilder(orig: PointsToGraphAbstract<IValue>,
   hf: AbstractHeapFactory<IValue>,
   vg: IVGlobal,
   callStack: CallStackContext,
   slots: Builder<Any, IHeapValues<IValue>>,
   heap: Builder<IValue, PersistentMap<Any, IData<IValue>>>,
   calledMethods: kotlinx.collections.immutable.PersistentSet.Builder<SootMethod>
) : PointsToGraphBuilderAbstract(orig, hf, vg, callStack, slots, heap, calledMethods) {
   public fun mayChange(): Boolean {
      return this.getSlots().build() != this.getOrig().getSlots()
         || this.getHeap().build() != this.getOrig().getHeap()
         || this.getCalledMethods().build() != this.getOrig().getCalledMethods();
   }

   public override fun build(): IFact<IValue> {
      return (IFact<IValue>)(if (!this.mayChange())
         this.getOrig()
         else
         new PointsToGraph(this.getHf(), this.getVg(), this.getCallStack(), this.getSlots().build(), this.getHeap().build(), this.getCalledMethods().build()));
   }

   public override fun newSummary(env: HeapValuesEnv, src: CompanionV<IValue>, mt: Any, key: Any?): IHeapValues<IValue>? {
      if (mt != BuiltInModelT.Field) {
         if (mt === BuiltInModelT.Array) {
            val var11: Type = this.getType(src);
            val var9: ArrayType = var11 as? ArrayType;
            if ((var11 as? ArrayType) != null) {
               val var15: AbstractHeapFactory = this.getHf();
               val var16: AbstractHeapFactory = this.getHf();
               val var10004: Type = var9.getElementType();
               return var15.push(env, var16.newSummaryVal(env, var10004, "${src.getValue().hashCode()}.$key"))
                  .markSummaryReturnValueFailedGetKeyFromKey(src, mt, key)
                  .popHV();
            }
         }

         return null;
      } else {
         val var13: IHeapValues;
         if (src.getValue() is IFieldManager) {
            val var10000: AbstractHeapFactory = this.getHf();
            val var10002: IFieldManager = src.getValue() as IFieldManager;
            var13 = var10000.push(env, var10002.getPhantomField(key as JFieldType)).markSummaryReturnValueFailedGetKeyFromKey(src, mt, key).popHV();
         } else if (src.getValue() is AnyNewValue && key is JFieldType) {
            val var14: IVGlobal = this.getHf().getVg();
            val var6: FieldUtil = FieldUtil.INSTANCE;
            val arrayTy: Pair = var14.defaultValue((key as JFieldType).getType());
            val var10: Constant = arrayTy.component1() as Constant;
            var13 = this.getHf().push(env, this.getHf().newConstVal(var10, arrayTy.component2() as Type)).markOfConstant(var10, "unset null field").popHV();
         } else {
            var13 = null;
         }

         return var13;
      }
   }

   public fun newSummaryArraySize(env: HeapValuesEnv, allocSite: IHeapValues<IValue>): IHeapValues<IValue> {
      val var10000: AbstractHeapFactory = this.getHf();
      val var10002: AbstractHeapFactory = this.getHf();
      val var10004: IntType = G.v().soot_IntType();
      return var10000.push(env, var10002.newSummaryVal(env, var10004 as Type, "arraySize")).markSummaryArraySize(allocSite).popHV();
   }

   public override fun getEmptyFieldSpace(type: RefType): FieldHeapKV<IValue> {
      return new FieldSpace<>(type, ExtensionsKt.persistentHashMapOf(), this.getHf().empty());
   }

   public override fun getEmptyArraySpace(env: HeapValuesEnv, allocSite: IHeapValues<IValue>, type: ArrayType, arrayLength: IHeapValues<IValue>?): ArrayHeapKV<
         IValue
      > {
      return ArraySpace.Companion
         .v(
            this.getHf(),
            env,
            ExtensionsKt.persistentHashMapOf(),
            this.getHf().empty(),
            type,
            if (arrayLength != null && !arrayLength.isEmpty()) arrayLength else this.newSummaryArraySize(env, allocSite)
         );
   }

   public override fun getType(value: CompanionV<IValue>): Type? {
      return (value.getValue() as IValue).getType();
   }

   public override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<IValue> {
      var var10000: IHeapValues = this.getTargets(slot);
      if (var10000 == null) {
         var10000 = this.getHf().empty();
      }

      return var10000;
   }

   public override fun getConstantPoolObjectData(env: HeapValuesEnv, cv: CompanionV<IValue>, mt: Any): IData<IValue>? {
      val value: IValue = cv.getValue() as IValue;
      if (value is ConstVal && (value as ConstVal).getV() is StringConstant) {
         if (!this.getHeap().containsKey(cv.getValue())) {
            val var10002: Unit = env.getNode();
            var var10003: AbstractHeapFactory = this.getHf();
            val var10004: java.lang.String = ((value as ConstVal).getV() as StringConstant).value;
            val var36: ByteArray = var10004.getBytes(Charsets.UTF_8);
            val arrayValue: JStrArrValue = new JStrArrValue(var10002, var10003, var36);
            val var16: WString = WString.Companion.v();
            var var10000: AbstractHeapFactory = (AbstractHeapFactory)this.emptyFieldFx().invoke(cv);
            val members: IHeapKVData.Builder = (var10000 as FieldHeapKV).builder();
            var10000 = this.getHf();
            val var10001: AnyNewExprEnv = this.getHf().getVg().getNEW_Env();
            val var32: NewArrayExpr = var16.getNewValueExpr();
            val newArrayValue: IValue = var10000.anyNewVal(var10001, var32 as AnyNewExpr) as IValue;
            val newValue: IHeapValues = this.getHf().empty().plus(this.getHf().push(env, newArrayValue).dataSequenceToSeq(cv).pop());
            val var24: IHeapValues = this.getHf().empty();
            val var27: AbstractHeapFactory = this.getHf();
            var10003 = this.getHf();
            val var37: Constant = WString.Companion.getLATIN1() as Constant;
            val var10005: ByteType = G.v().soot_ByteType();
            val newCoder: IHeapValues = var24.plus(
               JOperatorV.DefaultImpls.markOfConstant$default(
                     var27.push(env, var10003.newConstVal(var37, var10005 as Type)), WString.Companion.getLATIN1() as Constant, null, 2, null
                  )
                  .pop()
            );
            val var25: IHeapValues = this.getHf().empty();
            val var28: AbstractHeapFactory = this.getHf();
            var10003 = this.getHf();
            val var38: IntConstant = IntConstant.v(((value as ConstVal).getV() as StringConstant).value.hashCode());
            val var39: Constant = var38 as Constant;
            val var40: IntType = G.v().soot_IntType();
            val newHash: IHeapValues = var25.plus(var28.push(env, var10003.newConstVal(var39, var40 as Type)).markOfOp("string.hash", cv).pop());
            val var29: IHeapValuesFactory = this.getHf();
            var `this_$iv`: FieldUtil = FieldUtil.INSTANCE;
            members.set(var29, env, new JSootFieldType(var16.getValueField()), newValue, false);
            val var30: IHeapValuesFactory = this.getHf();
            `this_$iv` = FieldUtil.INSTANCE;
            members.set(var30, env, new JSootFieldType(var16.getCoderField()), newCoder, false);
            val var31: IHeapValuesFactory = this.getHf();
            `this_$iv` = FieldUtil.INSTANCE;
            members.set(var31, env, new JSootFieldType(var16.getHashField()), newHash, false);
            this.setValueData(env, newArrayValue, BuiltInModelT.Array, arrayValue);
            this.setValueData(env, value, BuiltInModelT.Field, members.build());
            val var26: IFact.Builder = this;
            val var33: java.lang.String = ((value as ConstVal).getV() as StringConstant).value;
            IFact.Builder.DefaultImpls.assignNewExpr$default(var26, env, var33, this.getHf().empty().plus(cv), false, 8, null);
         }

         return this.getValueData(value, mt);
      } else {
         return null;
      }
   }

   public override fun callEntryFlowFunction(context: Context<SootMethod, Unit, IFact<IValue>>, callee: SootMethod, node: Unit, succ: Unit) {
      this.setCallStack(new CallStackContext(this.getCallStack(), node, callee, this.getCallStack().getDeep() + 1));
      val receivers: IHeapValues = this.getSlots().get(-1) as IHeapValues;
      if (receivers != null) {
         val var10000: AbstractHeapFactory = this.getHf();
         val var10002: RefType = callee.getDeclaringClass().getType();
         (this.getSlots() as java.util.Map).put(-1, var10000.canStore(receivers, var10002 as Type));
      }
   }

   private fun activeCalleeReports(container: SootMethod, env: HeapValuesEnv, ctx: AIContext, callEdgeValue: IFact<IValue>, calleeCtx: AIContext): cn.sast.dataflow.interprocedural.check.PointsToGraphBuilder.PathTransfer {
      val calleePathToCallerPath: java.util.Map = new LinkedHashMap();
      val var10001: IDiff = new IDiff<IValue>(calleePathToCallerPath) {
         {
            this.$calleePathToCallerPath = `$calleePathToCallerPath`;
         }

         @Override
         public void diff(CompanionV<IValue> left, CompanionV<? extends Object> right) {
            val calleePath: IPath = (right as PathCompanionV).getPath();
            if (calleePath is EntryPath) {
               this.$calleePathToCallerPath.put(calleePath, (left as PathCompanionV).getPath());
            }
         }
      };
      val var10002: Any = calleeCtx.getEntryValue();
      callEdgeValue.diff(var10001, var10002 as IFact);
      return new PointsToGraphBuilder.PathTransfer(env, calleePathToCallerPath, ctx, calleeCtx);
   }

   public override fun updateIntraEdge(env: HeapValuesEnv, ctx: soot.Context, calleeCtx: soot.Context, callEdgeValue: IFact<IValue>, hasReturnValue: Boolean): IHeapValues<
         IValue
      >? {
      if (callEdgeValue !is PointsToGraphAbstract) {
         throw new IllegalArgumentException("updateIntraEdge error of fact type: ${callEdgeValue.getClass()} \n$callEdgeValue");
      } else {
         val pathTransfer: PointsToGraphBuilder.PathTransfer = this.activeCalleeReports(
            (ctx as AIContext).getMethod(), env, ctx as AIContext, callEdgeValue, calleeCtx as AIContext
         );
         var var10000: Any = (calleeCtx as AIContext).getExitValue();
         val exitValue: IFact = var10000 as IFact;
         if ((var10000 as IFact) !is PointsToGraphAbstract) {
            if (exitValue.isBottom()) {
               return null;
            } else {
               throw new IllegalArgumentException("updateIntraEdge error of fact type: ${exitValue.getClass()} \n$exitValue");
            }
         } else {
            this.getCalledMethods().addAll((exitValue as PointsToGraphAbstract).getCalledMethods() as java.util.Collection);
            if (FactValuesKt.getLeastExpr()) {
               val orig: <unrepresentable> = new IReNew<IValue>(pathTransfer) {
                  {
                     this.$pathTransfer = `$pathTransfer`;
                  }

                  @Override
                  public CompanionV<IValue> checkNeedReplace(CompanionV<IValue> c) {
                     val var10000: InvokeEdgePath = this.$pathTransfer.transform((c as PathCompanionV).getPath());
                     if (var10000 == null) {
                        return null;
                     } else if (c is CompanionValueOfConst) {
                        return new CompanionValueOfConst((c as CompanionValueOfConst).getValue(), var10000, (c as CompanionValueOfConst).getAttr());
                     } else if (c is CompanionValueImpl1) {
                        return new CompanionValueImpl1((c as CompanionValueImpl1).getValue(), var10000);
                     } else {
                        throw new NotImplementedError(null, 1, null);
                     }
                  }

                  public IValue checkNeedReplace(IValue old) {
                     return IReNew.DefaultImpls.checkNeedReplace(this, old);
                  }

                  @Override
                  public IReNew<IValue> context(Object value) {
                     return IReNew.DefaultImpls.context(this, value);
                  }
               };

               for (Entry returnValue : ((java.util.Map)((PointsToGraphAbstract)exitValue).getHeap()).entrySet()) {
                  val k: IValue = returnValue.getKey() as IValue;
                  val v: PersistentMap = returnValue.getValue() as PersistentMap;
                  val dataMap: PersistentMap = this.getHeap().get(k) as PersistentMap;
                  val dataMapBuilder: Builder = v.builder();

                  for (Entry rpVal : ((java.util.Map)dataMap).entrySet()) {
                     val mt: Any = rpVal.getKey();
                     val `$this$plus$iv`: IData = rpVal.getValue() as IData;
                     val `map$iv`: IData = `$this$plus$iv`.cloneAndReNewObjects(orig);
                     if (`map$iv` != `$this$plus$iv`) {
                        (dataMapBuilder as java.util.Map).put(mt, `map$iv`);
                     }
                  }

                  if (k == this.getHf().getVg().getGLOBAL_SITE()) {
                     val var41: BuiltInModelT = BuiltInModelT.Field;
                     val var46: IData = if (dataMap != null) dataMap.get(BuiltInModelT.Field) as IData else null;
                     val var52: IData = dataMapBuilder.get(BuiltInModelT.Field) as IData;
                     if (var46 != null && var52 != null) {
                        val var57: java.util.Map = dataMapBuilder as java.util.Map;
                        val var62: IData.Builder = var52.builder();
                        (var62 as HeapDataBuilder).updateFrom(this.getHf(), var46);
                        var57.put(var41, var62.build());
                     }
                  }

                  val var42: PersistentMap = dataMapBuilder.build();
                  if (dataMap as java.util.Map == null || (dataMap as java.util.Map).isEmpty()) {
                     (this.getHeap() as java.util.Map).put(k, var42);
                  } else {
                     (this.getHeap() as java.util.Map).put(k, ExtensionsKt.putAll(dataMap, var42 as java.util.Map));
                  }
               }

               (ctx as AIContext).activeReport(calleeCtx as AIContext, pathTransfer);
               val var23: IHeapValues = (exitValue as PointsToGraphAbstract).getTargetsUnsafe(this.getVg().getRETURN_LOCAL());
               if (hasReturnValue && var23 != null) {
                  return var23.cloneAndReNewObjects(orig);
               }
            } else {
               val var22: java.util.Set = new LinkedHashSet();

               for (Entry var27 : ((java.util.Map)((PointsToGraphAbstract)callEdgeValue).getSlots()).entrySet()) {
                  val var31: Any = var27.getKey();
                  var22.addAll((var27.getValue() as IHeapValues).getValues() as java.util.Collection);
               }

               for (Entry var28 : ((java.util.Map)((PointsToGraphAbstract)callEdgeValue).getHeap()).entrySet()) {
                  val var32: IValue = var28.getKey() as IValue;

                  for (Entry var39 : ((java.util.Map)((PersistentMap)var28.getValue())).entrySet()) {
                     val var43: Any = var39.getKey();
                     (var39.getValue() as IData).reference(var22);
                  }
               }

               val var26: IReNew = this.getHf().newReNewInterface(var22);
               if (!((exitValue as PointsToGraphAbstract).getHeap() as java.util.Map).isEmpty()) {
                  for (Entry var33 : ((java.util.Map)((PointsToGraphAbstract)exitValue).getHeap()).entrySet()) {
                     val var36: IValue = var33.getKey() as IValue;
                     val var38: PersistentMap = var33.getValue() as PersistentMap;
                     val var40: Builder = var38.builder();

                     for (Entry var50 : ((java.util.Map)dataMap).entrySet()) {
                        val var54: Any = var50.getKey();
                        val var59: IData = var50.getValue() as IData;
                        val var64: IData = var59.cloneAndReNewObjects(var26);
                        if (var64 != var59) {
                           (var40 as java.util.Map).put(var54, var64);
                        }
                     }

                     var10000 = this.getHeap().get(var36) as PersistentMap;
                     if (var10000 == null) {
                        var10000 = ExtensionsKt.persistentHashMapOf();
                     }

                     val var51: IValue = var26.checkNeedReplace(var36);
                     if (var51 == null) {
                        (this.getHeap() as java.util.Map).put(var36, ExtensionsKt.putAll((PersistentMap)var10000, var40.build() as java.util.Map));
                     } else if (!(var36 == var51)) {
                        (this.getHeap() as java.util.Map).put(var51, ExtensionsKt.putAll((PersistentMap)var10000, var40.build() as java.util.Map));
                        this.getHeap().remove(var36);
                     }
                  }
               }

               if (hasReturnValue) {
                  var10000 = (exitValue as PointsToGraphAbstract).getTargetsUnsafe(this.getVg().getRETURN_LOCAL());
                  if (var10000 == null) {
                     return null;
                  }

                  return ((IHeapValues)var10000).cloneAndReNewObjects(var26);
               }
            }

            return null;
         }
      }
   }

   @SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder$PathTransfer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n1230#2,4:613\n808#2,11:617\n865#2,2:628\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/PointsToGraphBuilder$PathTransfer\n*L\n446#1:613,4\n459#1:617,11\n460#1:628,2\n*E\n"])
   public class PathTransfer(env: HeapValuesEnv, calleePathToCallerPath: Map<EntryPath, IPath>, ctx: AIContext, calleeCtx: AIContext) {
      public final val env: HeapValuesEnv
      private final val calleePathToCallerPath: Map<EntryPath, IPath>
      public final val ctx: AIContext
      public final val calleeCtx: AIContext

      init {
         this.env = env;
         this.calleePathToCallerPath = calleePathToCallerPath;
         this.ctx = ctx;
         this.calleeCtx = calleeCtx;
      }

      public fun transform(calleePath: IPath, entryHeads: Set<EntryPath>): InvokeEdgePath? {
         if (entryHeads.isEmpty()) {
            return null;
         } else {
            val `$this$associateByTo$iv`: java.lang.Iterable = entryHeads;
            val `destination$iv`: java.util.Map = new HashMap();

            for (Object element$iv : $this$associateByTo$iv) {
               var var10000: IPath = this.calleePathToCallerPath.get(`element$iv` as EntryPath);
               if (var10000 == null) {
                  var10000 = UnknownPath.Companion.v(this.env);
               }

               `destination$iv`.put(var10000, `element$iv`);
            }

            return InvokeEdgePath.Companion.v(this.env, `destination$iv`, calleePath, this.ctx.getMethod(), this.calleeCtx.getMethod());
         }
      }

      public fun transform(calleePath: IPath): InvokeEdgePath? {
         val entryHeads2: java.lang.Iterable = PathGenerator.getHeads$default(PathGeneratorImpl.Companion.getPathGenerator(), calleePath, null, 2, null);
         val `$i$f$filterTo`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filterIsInstance$iv) {
            if (it is EntryPath) {
               `$i$f$filterTo`.add(it);
            }
         }

         val var13: java.lang.Iterable = `$i$f$filterTo` as java.util.List;
         val `destination$iv`: java.util.Collection = new LinkedHashSet();

         for (Object element$iv : var13) {
            if (this.calleeCtx.getEntries().contains(var16 as EntryPath)) {
               `destination$iv`.add(var16);
            }
         }

         return if ((`destination$iv` as java.util.Set).isEmpty()) null else this.transform(calleePath, `destination$iv` as MutableSet<EntryPath>);
      }
   }
}
