package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.ImmutableSet
import soot.*

/**
 * “常量” BOTTOM；所有查询皆视为无效。
 */
abstract class AbstractBOTTOM<V>(
   /** 堆工厂（通常为单例，可安全共享） */
   override val hf: AbstractHeapFactory<V>
) : InValidFact<V>() {

   /* ------------------------------------------------------------------ */
   /*  IFact 基本性质                                                     */
   /* ------------------------------------------------------------------ */
   override fun isBottom() = true
   override fun isTop()    = false
   override fun isValid()  = false

   override fun toString() = "IFact: BOTTOM"

   override fun equals(other: Any?): Boolean =
      other is IFact<*> && other.isBottom()

   override fun hashCode(): Int = 2

   /* ------------------------------------------------------------------ */
   /*  IFact 查询接口 —— 一律抛异常                                       */
   /* ------------------------------------------------------------------ */
   override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any> =
      throw UnsupportedOperationException("BOTTOM has no slot values")

   override fun diff(cmp: IDiff<Any>, that: IFact<Any>) = Unit

   /* ------------------------------------------------------------------ */
   /*  Builder: 返回自身（不可变）                                        */
   /* ------------------------------------------------------------------ */
   override fun builder(): IFact.Builder<V> = object : IFact.Builder<V>(this@AbstractBOTTOM) {

      /* ------- 只要保证编译期签名完整即可，全部保持 Bottom 语义 -------- */
      override val callStack: CallStackContext
         get() = error("BOTTOM has no call-stack")

      override fun assignLocal(env: HeapValuesEnv, lhs: Any, rhs: Any) = Unit
      override fun assignNewExpr(
         env: HeapValuesEnv, lhs: Any, allocSite: IHeapValues<V>, append: Boolean
      ) = Unit

      override fun setField(
         env: HeapValuesEnv, lhs: Any, field: JFieldType, rhs: Any, append: Boolean
      ) = Unit

      override fun setFieldNew(
         env: HeapValuesEnv, lhs: Any, field: JFieldType, allocSite: IHeapValues<V>
      ) = Unit

      override fun getField(
         env: HeapValuesEnv, lhs: Any, rhs: Any, field: JFieldType, newSummaryField: Boolean
      ) = Unit

      override fun summarizeTargetFields(lhs: Any) = Unit
      override fun union(that: IFact<V>)         = Unit

      override fun updateIntraEdge(
         env: HeapValuesEnv,
         ctx: Context,
         calleeCtx: Context,
         callEdgeValue: IFact<V>,
         hasReturnValue: Boolean
      ): IHeapValues<V>? = null

      override fun kill(slot: Any) = Unit
      override fun getSlots(): Set<Local> = emptySet()

      /* 直接返回 BOTTOM 本身 */
      override fun build(): IFact<V> = this@AbstractBOTTOM

      override fun addCalledMethod(sm: SootMethod)            = Unit
      override fun getCalledMethods(): ImmutableSet<SootMethod> = emptySet()

      override fun getTargets(slot: Any): IHeapValues<V> =
         hf.empty()

      /* three-state helpers */
      override fun isBottom() = true
      override fun isTop()    = false
      override fun isValid()  = false

      /* 数组相关操作 —— 均忽略 */
      override fun setArraySootValue(
         env: HeapValuesEnv, lhs: Any, index: Value, rhs: Value, valueType: Type
      ) = Unit

      override fun setArrayValueNew(
         env: HeapValuesEnv, lhs: Any, index: Value, allocSite: IHeapValues<V>
      ) = Unit

      override fun getArrayValue(
         env: HeapValuesEnv, lhs: Any, rhs: Any, index: Value
      ): Boolean = false

      override fun getArrayValue(
         env: HeapValuesEnv, lhs: Any, rhs: Value, index: Value
      ): Boolean = false

      override fun gc() = Unit

      override fun callEntryFlowFunction(
         context: cn.sast.idfa.analysis.Context<SootMethod, Unit, IFact<V>>,
         callee: SootMethod,
         node: Unit,
         succ: Unit
      ) = Unit

      override fun assignNewArray(
         env: HeapValuesEnv, lhs: Any, allocSite: IHeapValues<V>,
         type: ArrayType, size: Value
      ) = Unit

      override fun getValueData(v: V, mt: Any): IData<V>? = null

      override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<V> =
         hf.empty()

      override fun setValueData(env: HeapValuesEnv, v: V, mt: Any, data: IData<V>) = Unit
      override fun getArrayLength(array: V): IHeapValues<V>? = null

      override fun assignLocalSootValue(
         env: HeapValuesEnv, lhs: Any, rhs: Value, valueType: Type
      ) = Unit

      override fun assignFieldSootValue(
         env: HeapValuesEnv, lhs: Any, field: JFieldType,
         rhs: Value, valueType: Type, append: Boolean
      ) = Unit

      override fun getOfSootValue(
         env: HeapValuesEnv, value: Value, valueType: Type
      ): IHeapValues<V> = hf.empty()

      override fun getHf(): AbstractHeapFactory<V> = hf
      override fun copyValueData(from: V, to: V)   = Unit
      override fun getTargetsUnsafe(slot: Any): IHeapValues<V> = hf.empty()
      override fun getArray(array: V): IArrayHeapKV<Int, V>? = null
   }
}
