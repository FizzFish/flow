package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import kotlinx.collections.immutable.*
import soot.*
import soot.Value
import java.util.*

/**
 * 不可变 **Points-to Graph** 基类，实现 IFact。
 */
abstract class PointsToGraphAbstract<V>(
   final override val hf: AbstractHeapFactory<Any>,
   val vg: IVGlobal,
   final override val callStack: CallStackContext,
   val slots: PersistentMap<Any, IHeapValues<V>>,
   val heap: PersistentMap<V, PersistentMap<Any, IData<V>>>,
   val calledMethods: PersistentSet<SootMethod>
) : IFact<V> {

   private var _hashCode: Int? = null

   /* ---------- Hash / Equals ---------- */

   fun computeHash(): Int = 31 * slots.hashCode() + heap.hashCode()

   override fun hashCode(): Int =
      _hashCode ?: computeHash().also { _hashCode = it }

   override fun equals(other: Any?): Boolean =
      other is PointsToGraphAbstract<*> &&
              hashCode() == other.hashCode() &&
              slots == other.slots &&
              heap == other.heap

   /* ---------- IFact ---------- */

   override fun getValueData(v: Any, mt: Any): IData<Any>? =
      (heap[v] as? PersistentMap<*, *>)?.get(mt) as? IData<Any>

   override fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? =
      slots[slot] as? IHeapValues<Any>

   override fun getSlots(): Set<Any> = slots.keys
   override fun getCalledMethods(): ImmutableSet<SootMethod> = calledMethods

   override fun isBottom() = false
   override fun isTop()    = false
   override fun isValid()  = true

   override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any>? =
      getTargets(slot)

   override fun diff(cmp: IDiff<Any>, that: IFact<Any>) {
      require(that is PointsToGraphAbstract<*>)
      // slots
      that.slots.forEach { (k, r) ->
         slots[k]?.diff(cmp, r as IHeapValues<Any>)
      }
      // heap
      that.heap.forEach { (k, mapR) ->
         val mapL = heap[k] ?: persistentHashMapOf()
         mapR.forEach { (mt, dataR) ->
            mapL[mt]?.diff(cmp, dataR)
         }
      }
   }

   /* ---------- default delegate ---------- */

   override fun toString(): String = builder().toString()

   /* ---------- 获得数组辅助 ---------- */

   override fun getArrayLength(array: V): IHeapValues<V>? =
      IFact.DefaultImpls.getArrayLength(this, array)

   override fun getArray(array: V): IArrayHeapKV<Int, V>? =
      IFact.DefaultImpls.getArray(this, array)

   override fun getOfSootValue(
      env: HeapValuesEnv,
      value: Value,
      valueType: Type
   ): IHeapValues<V> =
      IFact.DefaultImpls.getOfSootValue(this, env, value, valueType)
}
