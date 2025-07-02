package cn.sast.dataflow.interprocedural.check

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.AIContext
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.AnyNewValue
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValues
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IFieldManager
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IIFact
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JFieldType
import cn.sast.dataflow.interprocedural.analysis.JOperatorC
import cn.sast.dataflow.interprocedural.analysis.JOperatorHV
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphBuilderAbstract
import cn.sast.dataflow.interprocedural.analysis.SummaryValue
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.checker.CheckerModelingKt
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSetBuilder
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValues
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.IClassField
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IModelExpressionVisitor
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.utils.UtilsKt
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import soot.AnySubType
import soot.ArrayType
import soot.BooleanType
import soot.FastHierarchy
import soot.G
import soot.NullType
import soot.PrimType
import soot.RefLikeType
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.Type
import soot.Unit
import soot.UnknownType
import soot.jimple.AnyNewExpr
import soot.jimple.BinopExpr
import soot.jimple.ConditionExpr
import soot.jimple.Constant
import soot.jimple.EqExpr
import soot.jimple.Expr
import soot.jimple.IntConstant
import soot.jimple.LengthExpr
import soot.jimple.NeExpr
import soot.jimple.NegExpr
import soot.jimple.NullConstant
import soot.jimple.NumericConstant
import soot.jimple.UnopExpr
import soot.jimple.internal.JIfStmt

@SourceDebugExtension(["SMAP\nHeapFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactory\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1430:1\n1755#2,3:1431\n1#3:1434\n*S KotlinDebug\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactory\n*L\n1150#1:1431,3\n*E\n"])
internal class HeapFactory(vg: IVGlobal) : AbstractHeapFactory<IValue> {
   public open val vg: IVGlobal
   public final val empty: HeapValues
   public open val nullConst: IValue

   init {
      this.vg = vg;
      this.empty = HeapValues.Companion.empty$corax_data_flow();
      val var10001: ConstVal.Companion = ConstVal.Companion;
      val var10002: NullConstant = NullConstant.v();
      val var2: Constant = var10002 as Constant;
      val var10003: NullType = G.v().soot_NullType();
      this.nullConst = var10001.v(var2, var10003 as Type);
   }

   public override fun empty(): IHeapValues<IValue> {
      return this.empty;
   }

   public fun getField(env: HeapValuesEnv, fact: Builder<IValue>, base: IHeapValues<IValue>, field: JFieldType): IHeapValues<IValue> {
      IFact.Builder.DefaultImpls.assignNewExpr$default(fact, env, "@base", base, false, 8, null);
      fact.getField(env, "@res", "@base", field, true);
      val value: IHeapValues = fact.getTargetsUnsafe("@res");
      fact.kill("@res");
      fact.kill("@base");
      var var10000: IHeapValues = value;
      if (value == null) {
         var10000 = this.empty;
      }

      return var10000;
   }

   public override fun canStore(receivers: IHeapValues<IValue>, receiverType: Type): IHeapValues<IValue> {
      val receiversValues: IHeapValues.Builder = this.emptyBuilder();
      val hierarchy: FastHierarchy = Scene.v().getOrMakeFastHierarchy();

      for (CompanionV receiver : receivers) {
         val v: IValue = receiver.getValue() as IValue;
         val var10000: Boolean;
         if (v.typeIsConcrete()) {
            var10000 = hierarchy.canStoreType(v.getType(), receiverType);
         } else if (receiverType !is RefLikeType) {
            var10000 = true;
         } else if (receiverType is RefType && (receiverType as RefType).getSootClass().isPhantom()) {
            var10000 = true;
         } else {
            val var11: Type;
            if (v.getType() is RefType) {
               val var10: Type = v.getType();
               var11 = AnySubType.v(var10 as RefType) as Type;
            } else {
               var11 = v.getType();
            }

            var10000 = v.getType() == receiverType || hierarchy.canStoreType(var11, receiverType);
         }

         if (var10000) {
            receiversValues.add(receiver);
         }
      }

      return receiversValues.build();
   }

   public override fun resolve(env: HeapValuesEnv, atCall: ICallCB<IHeapValues<IValue>, Builder<IValue>>, iExpr: IExpr): Sequence<Any> {
      return iExpr.accept(
         new IModelExpressionVisitor<Sequence<? extends Object>>(this, env, atCall)// $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
   //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
   //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
   //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
   //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
   //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
   //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
   //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
   //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
   //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
   
      );
   }

   public open fun anyNewVal(newExprEnv: AnyNewExprEnv, newExr: AnyNewExpr): IValue {
      val var10002: Unit = newExprEnv.getNode();
      val var10003: java.lang.String = newExprEnv.getMethod().getSignature();
      return new AnyNewValue(var10002, var10003, newExr);
   }

   public open fun newSummaryVal(env: HeapValuesEnv, type: Type, special: Any): IValue {
      return SummaryValue.Companion.v(type, env.getNode(), special);
   }

   public open fun newConstVal(constant: Constant, type: Type): ConstVal {
      return ConstVal.Companion.v(constant, type);
   }

   public override fun env(node: Unit): HeapValuesEnv {
      return new HeapValuesEnvImpl(node);
   }

   public override fun env(ctx: AIContext, node: Unit): HookEnv {
      return new HookEnv(ctx, node);
   }

   public override fun getPathFactory(): PathFactory<IValue> {
      return new PathFactoryImpl();
   }

   public override fun resolveOp(env: HeapValuesEnv, vararg ops: IHeapValues<IValue>?): IOpCalculator<IValue> {
      return new OpCalculator(env, this as AbstractHeapFactory<IValue>, Arrays.copyOf(ops, ops.length));
   }

   public override fun newReNewInterface(orig: MutableSet<IValue>): IReNew<IValue> {
      return new IReNew<IValue>(orig) {
         private final java.util.Map<IValue, IValue> newValueMap;

         {
            this.$orig = `$orig`;
            this.newValueMap = new LinkedHashMap<>();
         }

         public final java.util.Map<IValue, IValue> getNewValueMap() {
            return this.newValueMap;
         }

         public IValue checkNeedReplace(IValue old) {
            if (FactValuesKt.getLeastExpr()) {
               return null;
            } else if (old !is SummaryValue && old !is AnyNewValue) {
               return null;
            } else if (this.$orig.contains(old)) {
               return null;
            } else {
               label62: {
                  synchronized (this){} // $VF: monitorenter 

                  label31: {
                     try {
                        if (this.newValueMap.get(old) != null) {
                           break label31;
                        }

                        this.newValueMap.put(old, old.clone());
                     } catch (var7: java.lang.Throwable) {
                        // $VF: monitorexit
                     }

                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }
            }
         }

         @Override
         public CompanionV<IValue> checkNeedReplace(CompanionV<IValue> c) {
            return IReNew.DefaultImpls.checkNeedReplace(this, c);
         }

         @Override
         public IReNew<IValue> context(Object value) {
            return IReNew.DefaultImpls.context(this, value);
         }
      };
   }

   public open fun getBooleanValue(v: IValue, checkType: Boolean): Boolean? {
      return FactValuesKt.getBooleanValue(v, checkType);
   }

   public open fun getIntValue(v: IValue, checkType: Boolean): Int? {
      return FactValuesKt.getIntValue(v, checkType);
   }

   public override fun single(v: CompanionV<IValue>): IHeapValues<IValue> {
      return this.empty().plus(v);
   }

   public open fun push(env: HeapValuesEnv, alloc: IValue): JOperatorV<IValue> {
      return new OperatorPathFactory(this, env, alloc, null, 8, null);
   }

   public override fun push(env: HeapValuesEnv, value: CompanionV<IValue>): JOperatorC<IValue> {
      val res: ObjectRef = new ObjectRef();
      res.element = value;
      return new JOperatorC<IValue>(res, this) {
         {
            this.$res = `$res`;
            this.this$0 = `$receiver`;
         }

         @Override
         public JOperatorC<IValue> markEntry() {
            return this as JOperatorC<IValue>;
         }

         @Override
         public CompanionV<IValue> pop() {
            return this.$res.element as CompanionV<IValue>;
         }

         @Override
         public IHeapValues<IValue> popHV() {
            return this.this$0.getEmpty().plus(this.$res.element as CompanionV<IValue>);
         }
      };
   }

   public override fun push(env: HeapValuesEnv, value: IHeapValues<IValue>): JOperatorHV<IValue> {
      return new JOperatorHVImpl(this as AbstractHeapFactory<IValue>, env, value);
   }

   private fun checkTypeHierarchyIsPhantom(type: Type): Boolean {
      if (type is ArrayType) {
         val var10001: Type = (type as ArrayType).getElementType();
         return this.checkTypeHierarchyIsPhantom(var10001);
      } else if (type is RefType) {
         if (!(type as RefType).hasSootClass()) {
            return false;
         } else {
            val var10000: SootClass = (type as RefType).getSootClass();
            val `$this$any$iv`: java.lang.Iterable = UtilsKt.findAncestors(var10000);
            var var8: Boolean;
            if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
               var8 = false;
            } else {
               val var4: java.util.Iterator = `$this$any$iv`.iterator();

               while (true) {
                  if (!var4.hasNext()) {
                     var8 = false;
                     break;
                  }

                  if ((var4.next() as SootClass).isPhantom()) {
                     var8 = true;
                     break;
                  }
               }
            }

            return var8;
         }
      } else {
         return false;
      }
   }

   public fun tryCastRef(toType: RefLikeType, it: IValue, h: FastHierarchy, must: Boolean): IValue? {
      val fromType: Type = it.getType();
      if (fromType is UnknownType) {
         return it;
      } else {
         val fromIsPhantom: Boolean = this.checkTypeHierarchyIsPhantom(fromType);
         val toIsPhantom: Boolean = this.checkTypeHierarchyIsPhantom(toType as Type);
         val canStore: Boolean = h.canStoreType(fromType, toType as Type);
         return if (!it.typeIsConcrete() || !canStore && !fromIsPhantom && !toIsPhantom)
            (
               if (!must && !it.typeIsConcrete())
                  (
                     if (!fromIsPhantom && !toIsPhantom)
                        (
                           if (!canStore && h.canStoreType(toType as Type, fromType))
                              it.copy(toType as Type)
                              else
                              (if (canStore && h.canStoreType(toType as Type, fromType)) it else null)
                        )
                        else
                        it.copy(toType as Type)
                  )
                  else
                  null
            )
            else
            it;
      }
   }

   public override fun resolveCast(env: HeapValuesEnv, fact: Builder<IValue>, toType: Type, fromValues: IHeapValues<IValue>): IOpCalculator<IValue>? {
      val hf: HeapFactory = this;
      val unop: IOpCalculator = this.resolveOp(env, fromValues);
      val var10000: IOpCalculator;
      if (toType is PrimType) {
         unop.resolve(HeapFactory::resolveCast$lambda$1);
         var10000 = unop;
      } else if (toType is RefLikeType) {
         val var16: FastHierarchy = Scene.v().getOrMakeFastHierarchy();
         val replaceMap: java.util.Map = new LinkedHashMap();
         unop.resolve(HeapFactory::resolveCast$lambda$2);
         val rpFactory: <unrepresentable> = new IReNew<IValue>(replaceMap) {
            {
               this.$replaceMap = `$replaceMap`;
            }

            public IValue checkNeedReplace(IValue old) {
               return this.$replaceMap.get(old);
            }

            @Override
            public CompanionV<IValue> checkNeedReplace(CompanionV<IValue> c) {
               return IReNew.DefaultImpls.checkNeedReplace(this, c);
            }

            @Override
            public IReNew<IValue> context(Object value) {
               return IReNew.DefaultImpls.context(this, value);
            }
         };
         if (fact !is PointsToGraphBuilderAbstract) {
            throw new IllegalStateException("Check failed.".toString());
         }

         (fact as PointsToGraphBuilderAbstract).apply(rpFactory);

         for (Entry var11 : replaceMap.entrySet()) {
            val from: IValue = var11.getKey() as IValue;
            val to: IValue = var11.getValue() as IValue;
            if (toType is ArrayType && from.getType() !is ArrayType && (fact as PointsToGraphBuilderAbstract).getArray(to) == null) {
               val array: IData = (fact as PointsToGraphBuilderAbstract).getValueData(to, BuiltInModelT.Element);
               val collection: ObjectValues = array as? ObjectValues;
               if ((array as? ObjectValues) != null) {
                  IFact.Builder.DefaultImpls.assignNewExpr$default(
                     fact, env, "@arr", this.empty.plus(hf.push(env, to).markOfCastTo(toType as RefLikeType).pop()), false, 8, null
                  );
                  (fact as PointsToGraphBuilderAbstract).setArrayValueNew(env, "@arr", null, collection.getValues());
                  (fact as PointsToGraphBuilderAbstract).kill("@arr");
               }
            }
         }

         var10000 = unop;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public override fun resolveInstanceOf(env: HeapValuesEnv, fromValues: IHeapValues<IValue>, checkType: Type): IOpCalculator<IValue> {
      val unop: IOpCalculator = this.resolveOp(env, fromValues);
      unop.resolve(HeapFactory::resolveInstanceOf$lambda$3);
      val var10001: BooleanType = G.v().soot_BooleanType();
      unop.putSummaryIfNotConcrete(var10001 as Type, "instanceOfValue");
      return unop;
   }

   public override fun resolveUnop(env: HeapValuesEnv, fact: IIFact<IValue>, opValues: IHeapValues<IValue>, expr: UnopExpr, resType: Type): IOpCalculator<
         IValue
      > {
      val unop: IOpCalculator = this.resolveOp(env, opValues);
      if (expr is NegExpr) {
         unop.resolve(HeapFactory::resolveUnop$lambda$4);
         val var10001: BooleanType = G.v().soot_BooleanType();
         unop.putSummaryIfNotConcrete(var10001 as Type, expr);
      } else if (expr is LengthExpr) {
         unop.resolve(HeapFactory::resolveUnop$lambda$5);
         unop.putSummaryIfNotConcrete(resType, expr);
      } else {
         unop.putSummaryValue(resType, expr);
      }

      return unop;
   }

   public fun resolveBinopOrNull(env: HeapValuesEnv, expr: BinopExpr, clop: CompanionV<IValue>, crop: CompanionV<IValue>, resType: Type): CompanionV<IValue>? {
      val hf: HeapFactory = this;
      val lop: IValue = clop.getValue() as IValue;
      val rop: IValue = crop.getValue() as IValue;
      val c2: Constant = if ((lop as? ConstVal) != null) (lop as? ConstVal).getV() else null;
      val c1: NumericConstant = c2 as? NumericConstant;
      val c: Constant = if ((rop as? ConstVal) != null) (rop as? ConstVal).getV() else null;
      val var15: NumericConstant = c as? NumericConstant;
      if (c1 != null && (c as? NumericConstant) != null) {
         label63:
         try {
            val var10000: NumericConstant = SootUtilsKt.evalConstantBinop(expr as Expr, c1 as Constant, var15 as Constant);
            return if (var10000 == null) null else hf.push(env, hf.newConstVal(var10000 as Constant, resType)).markSootBinOp(expr, clop, crop).pop();
         } catch (var14: Exception) {
            return null;
         }
      } else if (expr !is EqExpr && expr !is NeExpr) {
         return null;
      } else if ((expr as ConditionExpr).getOp1().getType() is RefLikeType && (expr as ConditionExpr).getOp1().getType() is RefLikeType) {
         val equality: java.lang.Boolean = if (lop is IFieldManager && rop is IFieldManager)
            lop == rop
            else
            (
               if ((lop !is AnyNewValue || rop !is AnyNewValue) && lop !is SummaryValue && rop !is SummaryValue)
                  (if (lop !is ConstVal && rop !is ConstVal) lop == rop else null)
                  else
                  (if (FactValuesKt.getLeastExpr()) null else lop == rop)
            );
         if (equality == null) {
            return null;
         } else {
            val var10003: IntConstant = IntConstant.v(if ((if (expr is EqExpr) equality else !equality)) 1 else 0);
            return this.push(env, this.newConstVal(var10003 as Constant, resType)).markSootBinOp(expr, clop, crop).pop();
         }
      } else {
         return null;
      }
   }

   public fun resolveBinop(env: HeapValuesEnv, expr: BinopExpr, clop: CompanionV<IValue>, crop: CompanionV<IValue>, resType: Type): CompanionV<IValue> {
      var var10000: CompanionV = this.resolveBinopOrNull(env, expr, clop, crop, resType);
      if (var10000 == null) {
         var10000 = this.push(env, this.newSummaryVal(env, resType, expr)).markSootBinOp(expr, clop, crop).pop();
      }

      return var10000;
   }

   public override fun resolveBinop(
      env: HeapValuesEnv,
      fact: Builder<IValue>,
      op1Values: IHeapValues<IValue>,
      op2Values: IHeapValues<IValue>,
      expr: BinopExpr,
      resType: Type
   ): IOpCalculator<IValue> {
      val binop: IOpCalculator = this.resolveOp(env, op1Values, op2Values);
      val var11: java.util.Set = new LinkedHashSet();
      binop.resolve(HeapFactory::resolveBinop$lambda$8);
      binop.putSummaryIfNotConcrete(resType, expr);
      if (env.getNode() is JIfStmt) {
         return binop;
      } else {
         resolveBinop$taint(this, env, fact, binop.getRes().build(), var11, TaintProperty.INSTANCE, false);
         return binop;
      }
   }

   @JvmStatic
   fun `resolveCast$lambda$1`(
      `$hf`: HeapFactory,
      `$toType`: Type,
      `$env`: HeapValuesEnv,
      `$fact`: IFact.Builder,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var6: Array<CompanionV>
   ): Boolean {
      val op: IValue = var6[0].getValue() as IValue;
      val casted: Constant = if ((op as? ConstVal) != null) (op as? ConstVal).getV() else null;
      val nc: NumericConstant = casted as? NumericConstant;
      val var10000: IValue;
      if ((casted as? NumericConstant) != null) {
         val var10001: Constant = SootUtilsKt.castTo(nc, `$toType`);
         if (var10001 == null) {
            return false;
         }

         var10000 = `$hf`.newConstVal(var10001, `$toType`);
      } else {
         var10000 = `$hf`.newSummaryVal(`$env`, `$toType`, "castValue");
      }

      `$fact`.copyValueData(op, var10000);
      res.add(`$hf`.push(`$env`, var10000).markOfCastTo(`$toType` as PrimType).pop());
      return true;
   }

   @JvmStatic
   fun `resolveCast$lambda$2`(
      `this$0`: HeapFactory,
      `$toType`: Type,
      `$h`: FastHierarchy,
      `$hf`: HeapFactory,
      `$env`: HeapValuesEnv,
      `$replaceMap`: java.util.Map,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var8: Array<CompanionV>
   ): Boolean {
      val op: IValue = var8[0].getValue() as IValue;
      val var10001: RefLikeType = `$toType` as RefLikeType;
      val var10000: IValue = `this$0`.tryCastRef(var10001, op, `$h`, false);
      if (var10000 == null) {
         return true;
      } else {
         val to: CompanionV = `$hf`.push(`$env`, var10000).markOfCastTo(`$toType` as RefLikeType).pop();
         if (!(var10000 == op)) {
            `$replaceMap`.put(op, var10000);
         }

         res.add(to);
         return true;
      }
   }

   @JvmStatic
   fun `resolveInstanceOf$lambda$3`(
      `$h`: FastHierarchy,
      `$checkType`: Type,
      `$hf`: HeapFactory,
      `$env`: HeapValuesEnv,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var6: Array<CompanionV>
   ): Boolean {
      val op: IValue = var6[0].getValue() as IValue;
      val var10000: Type = op.getType();
      if (var10000 == null) {
         return false;
      } else if (var10000 is UnknownType) {
         return false;
      } else {
         val canStore: Boolean = `$h`.canStoreType(var10000, `$checkType`);
         val var11: Boolean;
         if (op.typeIsConcrete() || canStore) {
            res.add(`$hf`.push(`$env`, `$hf`.toConstVal(canStore)).markOfInstanceOf().pop());
            var11 = true;
         } else if (`$h`.canStoreType(`$checkType`, var10000)) {
            var11 = false;
         } else {
            res.add(`$hf`.push(`$env`, `$hf`.toConstVal(false)).markOfInstanceOf().pop());
            var11 = true;
         }

         return var11;
      }
   }

   @JvmStatic
   fun `resolveUnop$lambda$4`(
      `$hf`: HeapFactory,
      `$env`: HeapValuesEnv,
      `$resType`: Type,
      `$expr`: UnopExpr,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var6: Array<CompanionV>
   ): Boolean {
      val cop: CompanionV = var6[0];
      val op: IValue = var6[0].getValue() as IValue;
      val var10: Constant = if ((op as? ConstVal) != null) (op as? ConstVal).getV() else null;
      val var10000: NumericConstant = var10 as? NumericConstant;
      if ((var10 as? NumericConstant) == null) {
         return false;
      } else {
         val var10004: NumericConstant = var10000.negate();
         res.add(`$hf`.push(`$env`, `$hf`.newConstVal(var10004 as Constant, `$resType`)).markOfNegExpr(`$expr` as NegExpr, cop).pop());
         return true;
      }
   }

   @JvmStatic
   fun `resolveUnop$lambda$5`(
      `$fact`: IIFact, `$hf`: HeapFactory, `$env`: HeapValuesEnv, `$this$resolve`: IOpCalculator, res: IHeapValues.Builder, var5: Array<CompanionV>
   ): Boolean {
      val cop: CompanionV = var5[0];
      val var10000: IHeapValues = `$fact`.getArrayLength(var5[0].getValue());
      if (var10000 == null) {
         return false;
      } else {
         res.add(`$hf`.push(`$env`, var10000).markOfArrayLength(cop).pop());
         return true;
      }
   }

   @JvmStatic
   fun `resolveBinop$lambda$8`(
      `this$0`: HeapFactory,
      `$env`: HeapValuesEnv,
      `$expr`: BinopExpr,
      `$resType`: Type,
      `$fact`: IFact.Builder,
      `$types`: java.util.Set,
      `$this$resolve`: IOpCalculator,
      res: IHeapValues.Builder,
      var8: Array<CompanionV>
   ): Boolean {
      val clop: CompanionV = var8[0];
      val crop: CompanionV = var8[1];
      val r: CompanionV = `this$0`.resolveBinop(`$env`, `$expr`, clop, var8[1], `$resType`);
      var var12: IData = `$fact`.getValueData(clop.getValue(), CheckerModelingKt.getKeyTaintProperty());
      var var10000: ImmutableElementSet = var12 as? ImmutableElementSet;
      if ((var12 as? ImmutableElementSet) != null) {
         `$types`.add(var10000);
      }

      var12 = `$fact`.getValueData(crop.getValue(), CheckerModelingKt.getKeyTaintProperty());
      var10000 = var12 as? ImmutableElementSet;
      if ((var12 as? ImmutableElementSet) != null) {
         `$types`.add(var10000);
      }

      val var20: Boolean;
      if (r == null) {
         var20 = false;
      } else {
         res.add(r);
         var20 = true;
      }

      return var20;
   }

   @JvmStatic
   fun `resolveBinop$taint$lambda$10`(
      `$append1`: Boolean,
      `$fact`: IFact.Builder,
      `$field`: IClassField,
      `$values`: java.util.Set,
      `$env`: HeapValuesEnv,
      `$hf`: HeapFactory,
      `$this$solve`: IOpCalculator,
      var7: IHeapValues.Builder,
      var8: Array<CompanionV>
   ): Boolean {
      val base: CompanionV = var8[0];
      var var10000: ImmutableElementSet;
      if (`$append1`) {
         val var11: IData = `$fact`.getValueData(base.getValue(), `$field`);
         var10000 = var11 as? ImmutableElementSet;
      } else {
         var10000 = null;
      }

      if (var10000 != null && !var10000.isEmpty()) {
         var10000 = null;
      } else if (`$values`.size() == 1) {
         var10000 = (ImmutableElementSet)CollectionsKt.first(`$values`);
         var10000 = var10000;
      } else {
         var10000 = null;
      }

      if (var10000 == null) {
         var10000 = var10000;
         if (var10000 == null) {
            var10000 = new ImmutableElementSet(null, null, 3, null);
         }

         val setBuilder: ImmutableElementSetBuilder = var10000.builder();

         for (ImmutableElementSet typeValues : $values) {
            var10000 = if (typeValues is ImmutableElementSet) typeValues else null;
            if ((if (typeValues is ImmutableElementSet) typeValues else null) != null) {
               val set: ImmutableElementSet = var10000;

               for (Object e : var10000.getElement()) {
                  setBuilder.set(`$hf`, `$env`, e, set.get(`$hf`, e), `$append1`);
               }
            }
         }

         var10000 = setBuilder.build();
      }

      if (base.getValue() as IValue is ConstVal) {
         return false;
      } else {
         `$fact`.setValueData(`$env`, (IValue)base.getValue(), `$field`, var10000);
         return true;
      }
   }

   @JvmStatic
   fun `resolveBinop$taint`(
      hf: HeapFactory,
      `$env`: HeapValuesEnv,
      `$fact`: IFactBuilder<IValue>,
      bases: IHeapValues<IValue>,
      values: MutableSet<ImmutableElementSet<Object>>,
      field: IClassField,
      append: Boolean
   ) {
      hf.resolveOp(`$env`, bases).resolve(HeapFactory::resolveBinop$taint$lambda$10);
   }
}
