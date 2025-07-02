package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapKV
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapKV
import cn.sast.dataflow.util.Printer
import java.util.HashSet
import java.util.LinkedList
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentMap.Builder
import soot.ArrayType
import soot.G
import soot.IntType
import soot.Local
import soot.RefType
import soot.SootMethod
import soot.Type
import soot.Value
import soot.jimple.Constant
import soot.jimple.IntConstant

@SourceDebugExtension(["SMAP\nPointsToGraphAbstract.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraphAbstract.kt\ncn/sast/dataflow/interprocedural/analysis/PointsToGraphBuilderAbstract\n+ 2 PointsToGraphAbstract.kt\ncn/sast/dataflow/interprocedural/analysis/PointsToGraphAbstractKt\n*L\n1#1,1045:1\n119#2,3:1046\n119#2,3:1049\n119#2,3:1052\n119#2,3:1055\n119#2,3:1058\n*S KotlinDebug\n*F\n+ 1 PointsToGraphAbstract.kt\ncn/sast/dataflow/interprocedural/analysis/PointsToGraphBuilderAbstract\n*L\n580#1:1046,3\n610#1:1049,3\n650#1:1052,3\n865#1:1055,3\n979#1:1058,3\n*E\n"])
public abstract class PointsToGraphBuilderAbstract<V> : IFact.Builder<V> {
   public final val orig: PointsToGraphAbstract<Any>
   public open val hf: AbstractHeapFactory<Any>
   protected final val vg: IVGlobal

   public open var callStack: CallStackContext
      internal final set

   protected final val slots: Builder<Any, IHeapValues<Any>>
   protected final val heap: Builder<Any, PersistentMap<Any, IData<Any>>>
   public final val calledMethods: kotlinx.collections.immutable.PersistentSet.Builder<SootMethod>

   open fun PointsToGraphBuilderAbstract(
      orig: PointsToGraphAbstract<V>,
      hf: AbstractHeapFactory<V>,
      vg: IVGlobal,
      callStack: CallStackContext,
      slots: Builder<Object, IHeapValues<V>>,
      heap: Builder<V, PersistentMap<Object, IData<V>>>,
      calledMethods: kotlinx.collections.immutable.PersistentSetBuilder<SootMethod>
   ) {
      this.orig = orig;
      this.hf = hf;
      this.vg = vg;
      this.callStack = callStack;
      this.slots = slots;
      this.heap = heap;
      this.calledMethods = calledMethods;
   }

   public override fun assignLocal(env: HeapValuesEnv, lhs: Any, rhs: Any) {
      val rhsValue: IHeapValues = this.slots.get(rhs) as IHeapValues;
      if (rhsValue != null && !rhsValue.isEmpty()) {
         IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, rhsValue, false, 8, null);
      } else {
         IIFact.Companion.getLogger().debug(PointsToGraphBuilderAbstract::assignLocal$lambda$0);
         IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, this.getHf().empty(), false, 8, null);
      }
   }

   public override fun assignNewExpr(env: HeapValuesEnv, lhs: Any, allocSite: IHeapValues<Any>, append: Boolean) {
      val var10000: IHeapValues;
      if (append) {
         var var10001: IHeapValues = this.slots.get(lhs) as IHeapValues;
         if (var10001 == null) {
            var10001 = this.getHf().empty();
         }

         var10000 = allocSite.plus(var10001);
      } else {
         var10000 = allocSite;
      }

      (this.slots as java.util.Map).put(lhs, this.getHf().push(env, var10000).assignLocal(lhs, var10000).pop());
      if (var10000.isEmpty()) {
         IIFact.Companion.getLogger().debug(PointsToGraphBuilderAbstract::assignNewExpr$lambda$1);
      }
   }

   public abstract fun newSummary(env: HeapValuesEnv, src: CompanionV<Any>, mt: Any, key: Any?): IHeapValues<Any>? {
   }

   public override fun kill(slot: Any) {
      this.slots.remove(slot);
   }

   public abstract fun getConstantPoolObjectData(env: HeapValuesEnv, cv: CompanionV<Any>, mt: Any): IData<Any>? {
   }

   public fun <K : Any> getHeapKVData(
      env: HeapValuesEnv,
      mt: Any,
      oldHeap: PersistentMap<Any, PersistentMap<Any, IData<Any>>>,
      rhs: Any,
      key: K?,
      newSummary: Boolean,
      emptyIData: (CompanionV<Any>) -> IHeapKVData<K, Any>?
   ): IHeapValues<Any>? {
      if (this.getTargetsUnsafe(rhs) == null) {
         return null;
      } else {
         val rhsValue: IHeapValues.Builder = this.getHf().empty().builder();

         val rhsPointees: IHeapValues;
         for (CompanionV e$iv : rhsPointees) {
            var var21: IData;
            label52: {
               val heapData: PersistentMap = this.heap.get(`e$iv`.getValue()) as PersistentMap;
               if (heapData != null) {
                  var21 = heapData.get(mt) as IData;
                  if (var21 != null) {
                     break label52;
                  }
               }

               var21 = this.getConstantPoolObjectData(env, `e$iv`, mt);
            }

            val edges: IHeapKVData = var21 as IHeapKVData;
            if (var21 as IHeapKVData == null) {
               IIFact.Companion.getLogger().debug(PointsToGraphBuilderAbstract::getHeapKVData$lambda$4$lambda$2);
            }

            val targets: IHeapValues = if (edges != null) edges.get(this.getHf(), key) else null;
            if (targets != null && targets.isNotEmpty()) {
               rhsValue.add(this.getHf().push(env, targets).getKVValue(mt, `e$iv`, key).pop());
            } else if (newSummary) {
               val var22: IHeapValues = this.newSummary(env, `e$iv`, mt, key);
               if (var22 != null) {
                  if (this.setHeapKVData(env, mt, oldHeap, `e$iv`, key, var22, false, emptyIData)) {
                     rhsValue.add(this.getHf().push(env, var22).getKVValue(mt, `e$iv`, key).pop());
                  }
               }
            }
         }

         return rhsValue.build();
      }
   }

   public fun <K : Any> setHeapKVData(
      env: HeapValuesEnv,
      mt: Any,
      oldHeap: PersistentMap<Any, PersistentMap<Any, IData<Any>>>,
      lhs: CompanionV<Any>,
      key: K?,
      update: IHeapValues<Any>?,
      append: Boolean,
      emptyIData: (CompanionV<Any>) -> IHeapKVData<K, Any>?
   ): Boolean {
      var var10000: PersistentMap = oldHeap.get(lhs.getValue()) as PersistentMap;
      if (var10000 == null) {
         var10000 = ExtensionsKt.persistentHashMapOf();
      }

      var var14: IData = var10000.get(mt) as IData;
      if (var14 == null) {
         val var15: IHeapKVData = emptyIData.invoke(lhs) as IHeapKVData;
         if (var15 == null) {
            return false;
         }

         var14 = var15;
      }

      val oldKV: IHeapKVData = var14 as IHeapKVData;
      val bdr: IHeapKVData.Builder = (var14 as IHeapKVData).builder();
      if (update == null) {
         bdr.set(this.getHf(), env, key, update, append);
      } else {
         bdr.set(this.getHf(), env, key, this.getHf().push(env, update).setKVValue(mt, lhs, key).pop(), append);
      }

      val var13: IData = bdr.build();
      if (var13 != oldKV) {
         (this.heap as java.util.Map).put(lhs.getValue(), var10000.put(mt, var13));
      }

      return true;
   }

   public abstract fun getEmptyFieldSpace(type: RefType): FieldHeapKV<Any> {
   }

   public abstract fun getEmptyArraySpace(env: HeapValuesEnv, allocSite: IHeapValues<Any>, type: ArrayType, arrayLength: IHeapValues<Any>?): ArrayHeapKV<Any> {
   }

   public fun assignField(env: HeapValuesEnv, lhsPointees: IHeapValues<Any>, field: JFieldType, update: IHeapValues<Any>?, append: Boolean) {
      val oldHeap: PersistentMap = this.heap.build();
      val finalAppend: Boolean = append || !lhsPointees.isSingle();

      for (CompanionV e$iv : lhsPointees) {
         this.setHeapKVData(env, BuiltInModelT.Field, oldHeap, `e$iv`, field, update, finalAppend, this.emptyFieldFx());
      }
   }

   public fun assignField(env: HeapValuesEnv, lhs: Any, field: JFieldType, update: IHeapValues<Any>?, append: Boolean) {
      val var10000: IHeapValues = this.slots.get(lhs) as IHeapValues;
      if (var10000 != null) {
         this.assignField(env, var10000, field, update, append);
      }
   }

   public override fun setField(env: HeapValuesEnv, lhs: Any, field: JFieldType, rhs: Any, append: Boolean) {
      this.assignField(env, lhs, field, this.slots.get(rhs) as IHeapValues<V>, append);
   }

   public override fun setFieldNew(env: HeapValuesEnv, lhs: Any, field: JFieldType, allocSite: IHeapValues<Any>) {
      this.assignField(env, lhs, field, allocSite, false);
   }

   public override fun summarizeTargetFields(lhs: Any) {
   }

   public override fun getField(env: HeapValuesEnv, lhs: Any, rhs: Any, field: JFieldType, newSummaryField: Boolean) {
      val res: IHeapValues = this.getHeapKVData(env, BuiltInModelT.Field, this.heap.build(), rhs, field, newSummaryField, this.emptyFieldFx());
      if (res != null) {
         IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, res, false, 8, null);
      } else {
         IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, this.getHf().empty(), false, 8, null);
      }
   }

   public override fun toString(): String {
      val sb: StringBuffer = new StringBuffer();
      var currCtx: CallStackContext = this.getCallStack();
      sb.append("call stack: ${if (currCtx != null) currCtx.getDeep() else null}\n");

      while (currCtx != null) {
         sb.append(currCtx);
         currCtx = currCtx.getCaller();
      }

      sb.append("\nslot:\n");

      for (Object slot : this.slots.keySet()) {
         sb.append(slot).append(" -> ").append(java.lang.String.valueOf(this.slots.get(slot)));
         sb.append("\n");
      }

      sb.append("\nheap:\n");

      for (Entry var12 : ((java.util.Map)this.heap).entrySet()) {
         val data: PersistentMap = var12.getValue() as PersistentMap;
         sb.append(Printer.Companion.node2String(var12.getKey())).append(":\n");

         for (Entry var8 : ((java.util.Map)data).entrySet()) {
            sb.append("\t").append(var8.getKey()).append(": ").append(var8.getValue() as IData).append("\n");
         }
      }

      val var10000: java.lang.String = sb.toString();
      return var10000;
   }

   public override fun union(that: IFact<Any>) {
      if (that !is PointsToGraphAbstract) {
         throw new IllegalArgumentException("union error of fact type: ${that.getClass()} \n$that");
      } else {
         for (Entry var3 : ((java.util.Map)((PointsToGraphAbstract)that).getSlots()).entrySet()) {
            val thatSource: Any = var3.getKey();
            val thatData: IHeapValues = var3.getValue() as IHeapValues;
            var var10000: IHeapValues = this.slots.get(thatSource) as IHeapValues;
            if (var10000 == null) {
               var10000 = this.getHf().empty();
            }

            (this.slots as java.util.Map).put(thatSource, var10000.plus(thatData));
         }

         for (Entry var15 : ((java.util.Map)((PointsToGraphAbstract)that).getHeap()).entrySet()) {
            val var16: Any = var15.getKey();

            for (Entry var7 : ((java.util.Map)((PersistentMap)var15.getValue())).entrySet()) {
               val kind: Any = var7.getKey();
               val valuesR: IData = var7.getValue() as IData;
               var var19: PersistentMap = this.heap.get(var16) as PersistentMap;
               if (var19 == null) {
                  var19 = ExtensionsKt.persistentHashMapOf();
               }

               val valuesL: IData = var19.get(kind) as IData;
               if (valuesL == null) {
                  (this.heap as java.util.Map).put(var16, var19.put(kind, valuesR));
               } else if (valuesL != valuesR) {
                  val bdr: IData.Builder = valuesL.builder();
                  bdr.union(this.getHf(), valuesR);
                  (this.heap as java.util.Map).put(var16, var19.put(kind, bdr.build()));
               }
            }
         }
      }
   }

   public override fun gc() {
      val workList: LinkedList = new LinkedList();

      for (IHeapValues nodes : this.slots.values()) {
         heapOld.reference(workList);
      }

      val var7: HashSet = new HashSet();
      val var8: PersistentMap = this.heap.build();

      while (!workList.isEmpty()) {
         val v: Any = workList.remove();
         if (!var7.contains(v)) {
            var7.add(v);
            val var10000: PersistentMap = var8.get(v) as PersistentMap;
            if (var10000 != null) {
               val k: java.util.Iterator = (var10000 as java.util.Map).entrySet().iterator();

               while (k.hasNext()) {
                  ((k.next() as Entry).getValue() as IData).reference(workList);
               }
            }
         }
      }

      for (Object k : (ImmutableSet)heapOld.keySet()) {
         if (!var7.contains(var10)) {
            this.heap.remove(var10);
         }
      }
   }

   public override fun getSlots(): Set<Any> {
      return new HashSet<>(this.slots.keySet());
   }

   public override fun addCalledMethod(sm: SootMethod) {
      this.calledMethods.add(sm);
   }

   public override fun getCalledMethods(): ImmutableSet<SootMethod> {
      return this.calledMethods.build() as ImmutableSet<SootMethod>;
   }

   public override fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? {
      if (slot !is java.lang.String && slot !is Local && slot !is java.lang.Number) {
         IIFact.Companion.getLogger().error(PointsToGraphBuilderAbstract::getTargetsUnsafe$lambda$7);
      }

      return this.slots.get(slot) as IHeapValues<V>;
   }

   public override fun isBottom(): Boolean {
      return false;
   }

   public override fun isTop(): Boolean {
      return false;
   }

   public abstract fun getType(value: CompanionV<Any>): Type? {
   }

   public fun emptyArrayFx(env: HeapValuesEnv): (CompanionV<Any>) -> ArrayHeapKV<Any>? {
      return PointsToGraphBuilderAbstract::emptyArrayFx$lambda$8;
   }

   public fun emptyFieldFx(): (CompanionV<Any>) -> FieldHeapKV<Any>? {
      return PointsToGraphBuilderAbstract::emptyFieldFx$lambda$9;
   }

   public fun setArray(env: HeapValuesEnv, lhs: Any, index: Value?, update: IHeapValues<Any>?, append: Boolean) {
      val var10000: IHeapValues = this.slots.get(lhs) as IHeapValues;
      if (var10000 != null) {
         val oldHeap: PersistentMap = this.heap.build();
         val allIndex: java.util.Set = this.getAllIndex(index);
         val finalAppend: Boolean = append || !var10000.isSingle();

         val lhsPointees: IHeapValues;
         for (CompanionV e$iv : lhsPointees) {
            val lhsV: CompanionV = `e$iv`;
            if (allIndex != null && !allIndex.isEmpty()) {
               if (allIndex.size() == 1) {
                  this.setHeapKVData(env, BuiltInModelT.Array, oldHeap, `e$iv`, CollectionsKt.first(allIndex), update, finalAppend, this.emptyArrayFx(env));
               } else {
                  val var17: java.util.Iterator = allIndex.iterator();

                  while (var17.hasNext()) {
                     this.setHeapKVData(
                        env, BuiltInModelT.Array, oldHeap, lhsV, (var17.next() as java.lang.Number).intValue(), update, true, this.emptyArrayFx(env)
                     );
                  }
               }
            } else {
               this.setHeapKVData(env, BuiltInModelT.Array, oldHeap, `e$iv`, null, update, true, this.emptyArrayFx(env));
            }
         }
      }
   }

   public override fun setArraySootValue(env: HeapValuesEnv, lhs: Any, index: Value, rhs: Value, valueType: Type) {
      this.setArray(env, lhs, index, this.getOfSootValue(env, rhs, valueType), false);
   }

   public override fun setArrayValueNew(env: HeapValuesEnv, lhs: Any, index: Value?, allocSite: IHeapValues<Any>) {
      this.setArray(env, lhs, index, allocSite, false);
   }

   private fun getAllIndex(index: Value?): MutableSet<Int>? {
      if (index == null) {
         return null;
      } else if (index is IntConstant) {
         return SetsKt.mutableSetOf(new Integer[]{(index as IntConstant).value});
      } else {
         label15:
         if (index is Local) {
            val var10000: IHeapValues = this.slots.get(index) as IHeapValues;
            return if (var10000 == null) null else var10000.getAllIntValue(true);
         } else {
            return null;
         }
      }
   }

   public override fun getArrayValue(env: HeapValuesEnv, lhs: Any, rhs: Any, index: Value?): Boolean {
      val rhsValues: IHeapValues.Builder = this.getHf().empty().builder();
      val allIndex: java.util.Set = if (index == null) null else this.getAllIndex(index);
      val oldHeap: PersistentMap = this.heap.build();
      if (allIndex != null && !allIndex.isEmpty()) {
         if (allIndex.size() == 1) {
            val var18: IHeapValues = this.getHeapKVData(env, BuiltInModelT.Array, oldHeap, rhs, CollectionsKt.first(allIndex), false, this.emptyArrayFx(env));
            if (var18 != null) {
               rhsValues.add(var18);
            }
         } else {
            val var8: java.util.Iterator = allIndex.iterator();

            while (var8.hasNext()) {
               val var19: IHeapValues = this.getHeapKVData(
                  env, BuiltInModelT.Array, oldHeap, rhs, (var8.next() as java.lang.Number).intValue(), false, this.emptyArrayFx(env)
               );
               if (var19 != null) {
                  rhsValues.add(var19);
               }
            }
         }
      } else {
         val var10000: IHeapValues = this.getHeapKVData(env, BuiltInModelT.Array, oldHeap, rhs, null, false, this.emptyArrayFx(env));
         if (var10000 != null) {
            rhsValues.add(var10000);
         }
      }

      IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, rhsValues.build(), false, 8, null);
      return rhsValues.isNotEmpty();
   }

   public override fun getArrayValue(env: HeapValuesEnv, lhs: Any, rhs: Value, index: Value?): Boolean {
      val var10000: Boolean;
      if (rhs is Constant) {
         var10000 = false;
      } else {
         if (rhs !is Local) {
            throw new IllegalStateException((PointsToGraphBuilderAbstract::getArrayValue$lambda$14).toString());
         }

         var10000 = this.getArrayValue(env, lhs, rhs, index);
      }

      return var10000;
   }

   private fun setIData(value: CompanionV<Any>, bindData: IData<Any>) {
      var var10000: PersistentMap = this.heap.get(value.getValue()) as PersistentMap;
      if (var10000 == null) {
         var10000 = ExtensionsKt.persistentHashMapOf();
      }

      (this.heap as java.util.Map).put(value.getValue(), var10000.put(BuiltInModelT.Field, bindData));
   }

   public override fun assignFieldSootValue(env: HeapValuesEnv, lhs: Any, field: JFieldType, rhs: Value, valueType: Type, append: Boolean) {
      this.assignField(env, lhs, field, this.getOfSootValue(env, rhs, valueType), append);
   }

   public override fun assignLocalSootValue(env: HeapValuesEnv, lhs: Any, rhs: Value, valueType: Type) {
      IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, this.getOfSootValue(env, rhs, valueType), false, 8, null);
   }

   public override fun assignNewArray(env: HeapValuesEnv, lhs: Any, allocSite: IHeapValues<Any>, type: ArrayType, size: Value) {
      val var10003: IntType = G.v().soot_IntType();
      val res: ArrayHeapKV = this.getEmptyArraySpace(env, allocSite, type, this.getOfSootValue(env, size, var10003 as Type));

      for (CompanionV e$iv : allocSite) {
         (this.heap as java.util.Map).put(`e$iv`.getValue(), ExtensionsKt.persistentHashMapOf().put(BuiltInModelT.Array, res));
      }

      IFact.Builder.DefaultImpls.assignNewExpr$default(this, env, lhs, allocSite, false, 8, null);
   }

   public override fun getValueData(v: Any, mt: Any): IData<Any>? {
      val var10000: PersistentMap = this.heap.get(v) as PersistentMap;
      return if (var10000 != null) var10000.get(mt) as IData else null;
   }

   public override fun setValueData(env: HeapValuesEnv, v: Any, mt: Any, data: IData<Any>?) {
      if (data == null) {
         val var10000: PersistentMap = this.heap.get(v) as PersistentMap;
         val map: PersistentMap = if (var10000 != null) var10000.remove(mt) else null;
         if (map != null) {
            (this.heap as java.util.Map).put(v, map);
         }
      } else {
         var var9: PersistentMap = this.heap.get(v) as PersistentMap;
         if (var9 == null) {
            var9 = ExtensionsKt.persistentHashMapOf();
         }

         (this.heap as java.util.Map).put(v, var9.put(mt, this.getHf().getPathFactory().setModelData(env, (V)v, mt, data)));
      }
   }

   public override fun copyValueData(from: Any, to: Any) {
      val var3: java.util.Map = this.heap as java.util.Map;
      val var10000: PersistentMap = this.heap.get(from) as PersistentMap;
      if (var10000 != null) {
         var3.put(to, var10000);
      }
   }

   public fun apply(re: IReNew<Any>) {
      for (Entry var4 : ((java.util.Map)this.heap.build()).entrySet()) {
         val v: Any = var4.getKey();
         val l: PersistentMap = var4.getValue() as PersistentMap;
         val vx: Builder = l.builder();

         for (Entry rpVal : ((java.util.Map)dataMap).entrySet()) {
            val mt: Any = rpVal.getKey();
            val data: IData = rpVal.getValue() as IData;
            val var12: IData = data.cloneAndReNewObjects(re.context(new ReferenceContext.PTG(v, mt)));
            if (var12 != data) {
               (vx as java.util.Map).put(mt, var12);
            }
         }

         val var18: Any = re.checkNeedReplace(v);
         if (var18 == null) {
            (this.heap as java.util.Map).put(v, vx.build());
         } else if (!(v == var18)) {
            (this.heap as java.util.Map).put(var18, vx.build());
            this.heap.remove(v);
         }
      }

      for (Entry var15 : ((java.util.Map)this.slots.build()).entrySet()) {
         val var16: Any = var15.getKey();
         val var17: IHeapValues = var15.getValue() as IHeapValues;
         val var19: IHeapValues.Builder = var17.builder();
         var19.cloneAndReNewObjects(re.context(new ReferenceContext.Slot(var16)));
         val var20: IHeapValues = var19.build();
         if (var20 != var17) {
            (this.slots as java.util.Map).put(var16, var20);
         }
      }
   }

   override fun getTargets(slot: Any): IHeapValues<V> {
      return IFact.Builder.DefaultImpls.getTargets(this, slot);
   }

   override fun isValid(): Boolean {
      return IFact.Builder.DefaultImpls.isValid(this);
   }

   override fun getArrayLength(array: V): IHeapValues<V>? {
      return IFact.Builder.DefaultImpls.getArrayLength(this, (V)array);
   }

   override fun getArray(array: V): IArrayHeapKV<Integer, V>? {
      return IFact.Builder.DefaultImpls.getArray(this, (V)array);
   }

   override fun getOfSootValue(env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<V> {
      return IFact.Builder.DefaultImpls.getOfSootValue(this, env, value, valueType);
   }

   @JvmStatic
   fun `assignLocal$lambda$0`(`$env`: HeapValuesEnv, `$rhs`: Any, `$rhsValue`: IHeapValues): Any {
      return "$`$env` assignLocal rhs: $`$rhs` is $`$rhsValue`";
   }

   @JvmStatic
   fun `assignNewExpr$lambda$1`(`$env`: HeapValuesEnv): Any {
      return "$`$env` allocSite is empty";
   }

   @JvmStatic
   fun `getHeapKVData$lambda$4$lambda$2`(`$mt`: Any, `$o`: CompanionV, `$key`: Any): Any {
      return "get modelT: $`$mt` map: $`$o` [$`$key`] is not exist";
   }

   @JvmStatic
   fun `getTargetsUnsafe$lambda$7`(`$slot`: Any): Any {
      return "error slot value: $`$slot`";
   }

   @JvmStatic
   fun `emptyArrayFx$lambda$8`(`this$0`: PointsToGraphBuilderAbstract, `$env`: HeapValuesEnv, lhsV: CompanionV): ArrayHeapKV {
      val allocSite: Type = `this$0`.getType(lhsV);
      return if ((allocSite as? ArrayType) != null) `this$0`.getEmptyArraySpace(`$env`, `this$0`.getHf().single(lhsV), allocSite as? ArrayType, null) else null;
   }

   @JvmStatic
   fun `emptyFieldFx$lambda$9`(`this$0`: PointsToGraphBuilderAbstract, lhsV: CompanionV): FieldHeapKV {
      val var3: Type = `this$0`.getType(lhsV);
      return if ((var3 as? RefType) != null) `this$0`.getEmptyFieldSpace(var3 as? RefType) else null;
   }

   @JvmStatic
   fun `getArrayValue$lambda$14`(`$rhs`: Value): java.lang.String {
      return "error soot.Value: $`$rhs`";
   }
}
