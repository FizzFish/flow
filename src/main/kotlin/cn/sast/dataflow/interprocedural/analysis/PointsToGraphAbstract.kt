package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import java.util.Map.Entry
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import soot.SootMethod
import soot.Type
import soot.Value

public abstract class PointsToGraphAbstract<V> : IFact<V> {
   public open val hf: AbstractHeapFactory<Any>
   public final val vg: IVGlobal
   public open val callStack: CallStackContext
   public final val slots: PersistentMap<Any, IHeapValues<Any>>
   public final val heap: PersistentMap<Any, PersistentMap<Any, IData<Any>>>
   public final val calledMethods: PersistentSet<SootMethod>

   public final var hashCode: Int?
      internal set

   open fun PointsToGraphAbstract(
      hf: AbstractHeapFactory<V>,
      vg: IVGlobal,
      callStack: CallStackContext,
      slots: PersistentMap<Object, ? extends IHeapValues<V>>,
      heap: PersistentMap<V, ? extends PersistentMap<Object, ? extends IData<V>>>,
      calledMethods: PersistentSet<? extends SootMethod>
   ) {
      this.hf = hf;
      this.vg = vg;
      this.callStack = callStack;
      this.slots = slots;
      this.heap = heap;
      this.calledMethods = calledMethods;
   }

   public fun computeHash(): Int {
      return 31 * (31 * 1 + this.slots.hashCode()) + this.heap.hashCode();
   }

   public override fun hashCode(): Int {
      var h: Int = this.hashCode;
      if (this.hashCode == null) {
         h = this.computeHash();
         this.hashCode = h;
      }

      return h;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is PointsToGraphAbstract) {
         return false;
      } else if (this.hashCode() != (other as PointsToGraphAbstract).hashCode()) {
         return false;
      } else {
         return this.slots == (other as PointsToGraphAbstract).slots && this.heap == (other as PointsToGraphAbstract).heap;
      }
   }

   public override fun toString(): String {
      return this.builder().toString();
   }

   public override fun getValueData(v: Any, mt: Any): IData<Any>? {
      val var10000: PersistentMap = this.heap.get(v) as PersistentMap;
      return if (var10000 != null) var10000.get(mt) as IData else null;
   }

   public override fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? {
      return this.slots.get(slot) as IHeapValues<V>;
   }

   public override fun getSlots(): Set<Any> {
      return this.slots.keySet();
   }

   public override fun getCalledMethods(): ImmutableSet<SootMethod> {
      return this.calledMethods as ImmutableSet<SootMethod>;
   }

   public override fun isBottom(): Boolean {
      return false;
   }

   public override fun isTop(): Boolean {
      return false;
   }

   public override fun isValid(): Boolean {
      return true;
   }

   public override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any>? {
      return this.getTargets(slot);
   }

   public override fun diff(cmp: IDiff<Any>, that: IFact<Any>) {
      if (that !is PointsToGraphAbstract) {
         throw new IllegalArgumentException("union error of fact type: ${that.getClass()} \n$that");
      } else {
         for (Entry var4 : ((java.util.Map)((PointsToGraphAbstract)that).slots).entrySet()) {
            val thatData: IHeapValues = var4.getValue() as IHeapValues;
            val var10000: IHeapValues = this.slots.get(var4.getKey()) as IHeapValues;
            if (var10000 != null) {
               var10000.diff(cmp, thatData);
            }
         }

         for (Entry var14 : ((java.util.Map)((PointsToGraphAbstract)that).heap).entrySet()) {
            val var16: PersistentMap = var14.getValue() as PersistentMap;
            var var17: PersistentMap = this.heap.get(var14.getKey()) as PersistentMap;
            if (var17 == null) {
               var17 = ExtensionsKt.persistentHashMapOf();
            }

            val pack: PersistentMap = var17;

            for (Entry var9 : ((java.util.Map)thatData).entrySet()) {
               val valuesR: IData = var9.getValue() as IData;
               val valuesL: IData = pack.get(var9.getKey()) as IData;
               if (valuesL != null && valuesL != valuesR) {
                  valuesL.diff(cmp, valuesR);
               }
            }
         }
      }
   }

   override fun getTargets(slot: Any): IHeapValues<V> {
      return IFact.DefaultImpls.getTargets(this, slot);
   }

   override fun getArrayLength(array: V): IHeapValues<V>? {
      return IFact.DefaultImpls.getArrayLength(this, (V)array);
   }

   override fun getArray(array: V): IArrayHeapKV<Integer, V>? {
      return IFact.DefaultImpls.getArray(this, (V)array);
   }

   override fun getOfSootValue(env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<V> {
      return IFact.DefaultImpls.getOfSootValue(this, env, value, valueType);
   }
}
