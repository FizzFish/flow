@file:Suppress(
   "UNCHECKED_CAST",
   "ReplacePutWithAssignment",
   "MoveVariableDeclarationIntoWhen",
   "CascadeIf",
   "NAME_SHADOWING"
)

package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapKV
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.heapimpl.FieldHeapKV
import cn.sast.dataflow.util.Printer
import kotlinx.collections.immutable.*
import kotlinx.collections.immutable.extensions.*
import mu.KotlinLogging
import soot.*
import soot.jimple.Constant
import soot.jimple.IntConstant
import java.lang.StringBuilder
import java.util.*

/**
 * 可变 **Points-to Graph** 构建器基类。
 *
 * @param orig          原始不可变 PTG
 * @param hf            抽象堆工厂
 * @param vg            全局常量表
 * @param callStack     调用栈上下文
 * @param slots         slot → pointees 可变映射
 * @param heap          对象 → modelType → IData 可变映射
 * @param calledMethods 已调用方法集合
 */
abstract class PointsToGraphBuilderAbstract<V>(
   val orig: PointsToGraphAbstract<Any>,
   override val hf: AbstractHeapFactory<Any>,
   protected val vg: IVGlobal,
   override var callStack: CallStackContext,
   protected val slots: PersistentMap.Builder<Any, IHeapValues<Any>>,
   protected val heap: PersistentMap.Builder<Any, PersistentMap<Any, IData<Any>>>,
   val calledMethods: PersistentSet.Builder<SootMethod>
) : IFact.Builder<V> {

   /* -------------------------------------------------- */
   /*  log                                               */
   /* -------------------------------------------------- */
   private val logger = KotlinLogging.logger {}

   /* -------------------------------------------------- */
   /*  #region IFact.Builder 实现                        */
   /* -------------------------------------------------- */

   /** 把 `rhs` 的值直接赋给 `lhs` */
   override fun assignLocal(env: HeapValuesEnv, lhs: Any, rhs: Any) {
      val rhsValue = slots[rhs]
      if (rhsValue != null && !rhsValue.isEmpty()) {
         assignNewExpr(env, lhs, rhsValue, append = false)
      } else {
         logger.debug { "$env assignLocal rhs: $rhs is $rhsValue" }
         assignNewExpr(env, lhs, hf.empty(), append = false)
      }
   }

   /** new / merge-new */
   override fun assignNewExpr(
      env: HeapValuesEnv,
      lhs: Any,
      allocSite: IHeapValues<Any>,
      append: Boolean
   ) {
      val finalValue =
         if (append) allocSite + (slots[lhs] ?: hf.empty())
         else allocSite

      slots[lhs] = hf.push(env, finalValue)
         .assignLocal(lhs, finalValue)
         .pop()

      if (finalValue.isEmpty())
         logger.debug { "$env allocSite is empty" }
   }

   /* -------------------------------------------------- */
   /*  ↓↓↓ 各类 Field / Array 操作 ↓↓↓                    */
   /* -------------------------------------------------- */

   /** 创建新的摘要对象（由子类实现） */
   abstract fun newSummary(
      env: HeapValuesEnv,
      src: CompanionV<Any>,
      mt: Any,
      key: Any?
   ): IHeapValues<Any>?

   override fun kill(slot: Any) {
      slots.remove(slot)
   }

   /** 获取常量池补偿对象（由子类实现） */
   abstract fun getConstantPoolObjectData(
      env: HeapValuesEnv,
      cv: CompanionV<Any>,
      mt: Any
   ): IData<Any>?

   /* -------------------------------------------------- */
   /*  get / set Heap-KV 通用模板                        */
   /* -------------------------------------------------- */

   /** helper: 取出 KV-model 数据，并按需补全 Summary */
   fun <K : Any> getHeapKVData(
      env: HeapValuesEnv,
      mt: Any,
      oldHeap: PersistentMap<Any, PersistentMap<Any, IData<Any>>>,
      rhs: Any,
      key: K?,
      newSummary: Boolean,
      emptyIData: (CompanionV<Any>) -> IHeapKVData<K, Any>?
   ): IHeapValues<Any>? {

      val rhsPointees = getTargets(rhs) ?: return null
      val rhsBuilder = hf.empty().builder()

      for (cp in rhsPointees) {
         val heapData = heap[cp.value] ?: oldHeap[cp.value]
         val iData = heapData?.get(mt) ?: getConstantPoolObjectData(env, cp, mt)

         val edges = iData as? IHeapKVData<K, Any>
         if (edges == null) {
            logger.debug { "get modelT: $mt map: $cp [$key] is not exist" }
         }

         val targets = edges?.get(hf, key)
         if (targets != null && targets.isNotEmpty()) {
            rhsBuilder.add(hf.push(env, targets).getKVValue(mt, cp, key).pop())
         } else if (newSummary) {
            val summary = newSummary(env, cp, mt, key)
            if (summary != null &&
               setHeapKVData(env, mt, oldHeap, cp, key, summary, append = false, emptyIData)
            ) {
               rhsBuilder.add(hf.push(env, summary).getKVValue(mt, cp, key).pop())
            }
         }
      }
      return rhsBuilder.build()
   }

   /** helper: 写 KV-model */
   fun <K : Any> setHeapKVData(
      env: HeapValuesEnv,
      mt: Any,
      oldHeap: PersistentMap<Any, PersistentMap<Any, IData<Any>>>,
      lhs: CompanionV<Any>,
      key: K?,
      update: IHeapValues<Any>?,
      append: Boolean,
      emptyIData: (CompanionV<Any>) -> IHeapKVData<K, Any>?
   ): Boolean {

      var modelMap = heap[lhs.value] ?: oldHeap[lhs.value] ?: persistentHashMapOf()
      var kvData = modelMap[mt] as? IHeapKVData<K, Any>
      if (kvData == null) {
         kvData = emptyIData(lhs) ?: return false
      }

      val builder = kvData.builder()
      val writeValue =
         if (update == null) update
         else hf.push(env, update).setKVValue(mt, lhs, key).pop()

      builder.set(hf, env, key, writeValue, append)

      val newData = builder.build()
      if (newData != kvData) {
         modelMap = modelMap.put(mt, newData)
         heap[lhs.value] = modelMap
      }
      return true
   }

   /* -------------------------------------------------- */
   /*  Field helpers (empty stubs供子类实现)             */
   /* -------------------------------------------------- */

   abstract fun getEmptyFieldSpace(type: RefType): FieldHeapKV<Any>

   abstract fun getEmptyArraySpace(
      env: HeapValuesEnv,
      allocSite: IHeapValues<Any>,
      type: ArrayType,
      arrayLength: IHeapValues<Any>? = null
   ): ArrayHeapKV<Any>

   /* lambda for empty Field / Array model */
   fun emptyArrayFx(env: HeapValuesEnv):
              (CompanionV<Any>) -> ArrayHeapKV<Any>? = { lhsV ->
      val tp = getType(lhsV)
      (tp as? ArrayType)?.let {
         getEmptyArraySpace(env, hf.single(lhsV), it, null)
      }
   }

   fun emptyFieldFx(): (CompanionV<Any>) -> FieldHeapKV<Any>? = { lhsV ->
      val tp = getType(lhsV)
      (tp as? RefType)?.let(::getEmptyFieldSpace)
   }

   /* -------------------------------------------------- */
   /*  assign / get / set field                         */
   /* -------------------------------------------------- */

   fun assignField(
      env: HeapValuesEnv,
      lhsPointees: IHeapValues<Any>,
      field: JFieldType,
      update: IHeapValues<Any>?,
      append: Boolean
   ) {
      val oldHeap = heap.build()
      val finalAppend = append || !lhsPointees.isSingle()
      for (cp in lhsPointees) {
         setHeapKVData(
            env, BuiltInModelT.Field, oldHeap, cp, field,
            update, finalAppend, emptyFieldFx()
         )
      }
   }

   fun assignField(
      env: HeapValuesEnv,
      lhs: Any,
      field: JFieldType,
      update: IHeapValues<Any>?,
      append: Boolean
   ) {
      slots[lhs]?.let { assignField(env, it, field, update, append) }
   }

   override fun setField(
      env: HeapValuesEnv,
      lhs: Any,
      field: JFieldType,
      rhs: Any,
      append: Boolean
   ) = assignField(env, lhs, field, slots[rhs], append)

   override fun setFieldNew(
      env: HeapValuesEnv,
      lhs: Any,
      field: JFieldType,
      allocSite: IHeapValues<Any>
   ) = assignField(env, lhs, field, allocSite, append = false)

   override fun summarizeTargetFields(lhs: Any) {
      /* no-op: 实现留给子类，如有需要 */
   }

   override fun getField(
      env: HeapValuesEnv,
      lhs: Any,
      rhs: Any,
      field: JFieldType,
      newSummaryField: Boolean
   ) {
      val res = getHeapKVData(
         env, BuiltInModelT.Field, heap.build(), rhs, field,
         newSummaryField, emptyFieldFx()
      )
      assignNewExpr(env, lhs, res ?: hf.empty(), append = false)
   }

   /* -------------------------------------------------- */
   /*  Array helpers                                    */
   /* -------------------------------------------------- */

   private fun getAllIndex(index: Value?): MutableSet<Int>? = when (index) {
      null -> null
      is IntConstant -> mutableSetOf(index.value)
      is Local -> slots[index]?.getAllIntValue(true)
      else -> null
   }

   fun setArray(
      env: HeapValuesEnv,
      lhs: Any,
      index: Value?,
      update: IHeapValues<Any>?,
      append: Boolean
   ) {
      val lhsValues = slots[lhs] ?: return
      val oldHeap = heap.build()
      val allIdx = getAllIndex(index)
      val finalAppend = append || !lhsValues.isSingle()

      fun write(cp: CompanionV<Any>, key: Int?) =
         setHeapKVData(
            env, BuiltInModelT.Array, oldHeap, cp, key, update,
            finalAppend, emptyArrayFx(env)
         )

      for (cp in lhsValues) {
         when {
            allIdx == null || allIdx.isEmpty() -> write(cp, null)
            allIdx.size == 1 -> write(cp, allIdx.first())
            else -> allIdx.forEach { write(cp, it) }
         }
      }
   }

   override fun setArraySootValue(
      env: HeapValuesEnv,
      lhs: Any,
      index: Value,
      rhs: Value,
      valueType: Type
   ) = setArray(env, lhs, index, getOfSootValue(env, rhs, valueType), append = false)

   override fun setArrayValueNew(
      env: HeapValuesEnv,
      lhs: Any,
      index: Value?,
      allocSite: IHeapValues<Any>
   ) = setArray(env, lhs, index, allocSite, append = false)

   override fun getArrayValue(
      env: HeapValuesEnv,
      lhs: Any,
      rhs: Any,
      index: Value?
   ): Boolean {

      val rhsBuilder = hf.empty().builder()
      val oldHeap = heap.build()
      val allIdx = getAllIndex(index)

      fun collect(key: Int?) {
         val res = getHeapKVData(
            env, BuiltInModelT.Array, oldHeap, rhs, key,
            newSummary = false, emptyArrayFx(env)
         )
         res?.let { rhsBuilder.add(it) }
      }

      when {
         allIdx == null || allIdx.isEmpty() -> collect(null)
         allIdx.size == 1 -> collect(allIdx.first())
         else -> allIdx.forEach(::collect)
      }

      assignNewExpr(env, lhs, rhsBuilder.build(), append = false)
      return rhsBuilder.isNotEmpty()
   }

   override fun getArrayValue(
      env: HeapValuesEnv,
      lhs: Any,
      rhs: Value,
      index: Value?
   ): Boolean {
      if (rhs is Constant) return false
      require(rhs is Local) { "unsupported soot.Value: $rhs" }
      return getArrayValue(env, lhs, rhs, index)
   }

   /* -------------------------------------------------- */
   /*  Other helper                                     */
   /* -------------------------------------------------- */

   /** 返回 lhs 的静态类型（子类决定如何解析） */
   abstract fun getType(value: CompanionV<Any>): Type?

   /* builder: assign array */
   override fun assignNewArray(
      env: HeapValuesEnv,
      lhs: Any,
      allocSite: IHeapValues<Any>,
      type: ArrayType,
      size: Value
   ) {
      val arrModel = getEmptyArraySpace(
         env, allocSite, type,
         getOfSootValue(env, size, G.v().soot_IntType())
      )
      for (cp in allocSite) {
         heap[cp.value] = persistentHashMapOf<Any, IData<Any>>()
            .put(BuiltInModelT.Array, arrModel)
      }
      assignNewExpr(env, lhs, allocSite, append = false)
   }

   /* -------------------------------------------------- */
   /*  数据存取                                         */
   /* -------------------------------------------------- */

   override fun getValueData(v: Any, mt: Any): IData<Any>? =
      heap[v]?.get(mt)

   override fun setValueData(env: HeapValuesEnv, v: Any, mt: Any, data: IData<Any>?) {
      val map = heap[v]
      if (data == null) {
         map?.remove(mt)?.let { heap[v] = map }
      } else {
         val newData = hf.pathFactory.setModelData(env, v as V, mt, data)
         heap[v] = (map ?: persistentHashMapOf()).put(mt, newData)
      }
   }

   override fun copyValueData(from: Any, to: Any) {
      heap[from]?.let { heap[to] = it }
   }

   /* -------------------------------------------------- */
   /*  GC                                              */
   /* -------------------------------------------------- */

   override fun gc() {
      val workList: Queue<Any> = LinkedList()
      val visited = HashSet<Any>()

      // seed from slots
      slots.values.forEach { it.reference(workList) }

      val heapSnapshot = heap.build()

      while (workList.isNotEmpty()) {
         val v = workList.remove()
         if (visited.add(v)) {
            heapSnapshot[v]?.values?.forEach { it.reference(workList) }
         }
      }

      // remove unreachable
      heap.keys.toList().filterNot { it in visited }.forEach { heap.remove(it) }
   }

   /* -------------------------------------------------- */
   /*  union / targets / status                         */
   /* -------------------------------------------------- */

   override fun union(that: IFact<Any>) {
      require(that is PointsToGraphAbstract<*>)
      // merge slots
      for ((k, vR) in that.slots) {
         val vL = slots[k] ?: hf.empty()
         slots[k] = vL + (vR as IHeapValues<Any>)
      }

      // merge heap
      for ((obj, mapR) in that.heap) {
         var mapL = heap[obj] ?: persistentHashMapOf()
         for ((mt, dataR) in mapR) {
            val dataL = mapL[mt]
            mapL = if (dataL == null) {
               mapL.put(mt, dataR)
            } else if (dataL != dataR) {
               val builder = dataL.builder()
               builder.union(hf, dataR)
               mapL.put(mt, builder.build())
            } else mapL
         }
         heap[obj] = mapL
      }
   }

   override fun getSlots(): Set<Any> = HashSet(slots.keys)

   override fun addCalledMethod(sm: SootMethod) {
      calledMethods.add(sm)
   }

   override fun getCalledMethods(): ImmutableSet<SootMethod> =
      calledMethods.build()

   override fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? {
      if (slot !is String && slot !is Local && slot !is Number) {
         logger.error { "error slot value: $slot" }
      }
      return slots[slot]
   }

   override fun isBottom() = false
   override fun isTop() = false

   /* -------------------------------------------------- */
   /*  builders helpers                                 */
   /* -------------------------------------------------- */

   override fun toString(): String = buildString {
      append("call stack: ").append(callStack.deep).append('\n')
      var ctx: CallStackContext? = callStack
      while (ctx != null) {
         append(ctx)
         ctx = ctx.caller
      }

      append("\nslot:\n")
      slots.forEach { (k, v) ->
         append(k).append(" -> ").append(v).append('\n')
      }

      append("\nheap:\n")
      heap.forEach { (obj, mp) ->
         append(Printer.node2String(obj)).append(":\n")
         mp.forEach { (mt, data) ->
            append('\t').append(mt).append(": ").append(data).append('\n')
         }
      }
   }
}
