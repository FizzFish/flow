package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import soot.*
import cn.sast.idfa.analysis.Context as Ctx

/**
 * **单帧数据流事实** 接口。
 */
interface IFact<V> : IProblemIteratorTerminal<V>, IIFact<V> {

   /* ---------- 差分 ---------- */

   fun diff(cmp: IDiff<Any>, that: IFact<Any>)

   /* ---------- 可变 Builder ---------- */

   fun builder(): Builder<V>

   /* ---------- Builder 定义 ---------- */

   interface Builder<V> : IIFact<V> {

      fun copyValueData(from: V, to: V)
      fun setValueData(env: HeapValuesEnv, v: V, mt: Any, data: IData<V>?)

      /* ----------- 基本赋值 ----------- */

      fun assignLocal(env: HeapValuesEnv, lhs: V, rhs: V)
      fun assignLocalSootValue(env: HeapValuesEnv, lhs: V, rhs: Value, valueType: Type)

      /* ----------- new / newArray ----------- */

      fun assignNewExpr(
         env: HeapValuesEnv,
         lhs: V,
         allocSite: IHeapValues<V>,
         append: Boolean = false
      )

      fun assignNewArray(
         env: HeapValuesEnv,
         lhs: V,
         allocSite: IHeapValues<V>,
         type: ArrayType,
         size: Value
      )

      /* ----------- field ----------- */

      fun assignFieldSootValue(
         env: HeapValuesEnv,
         lhs: V,
         field: JFieldType,
         rhs: Value,
         valueType: Type,
         append: Boolean = false
      )

      fun setField(env: HeapValuesEnv, lhs: V, field: JFieldType, rhs: V, append: Boolean = false)
      fun setFieldNew(env: HeapValuesEnv, lhs: V, field: JFieldType, allocSite: IHeapValues<V>)

      fun getField(
         env: HeapValuesEnv, lhs: V, rhs: V, field: JFieldType,
         newSummaryField: Boolean = false
      )

      fun summarizeTargetFields(lhs: V)

      /* ----------- union / kill ----------- */

      fun union(that: IFact<V>)
      fun kill(slot: V)

      /* ----------- intra-procedural更新 ----------- */

      fun updateIntraEdge(
         env: HeapValuesEnv,
         ctx: Ctx<SootMethod, Unit, IFact<V>>,
         calleeCtx: Ctx<SootMethod, Unit, IFact<V>>,
         callEdgeValue: IFact<V>,
         hasReturnValue: Boolean
      ): IHeapValues<V>?

      /* ----------- 数组 ----------- */

      fun setArraySootValue(
         env: HeapValuesEnv,
         lhs: V, index: Value, rhs: Value, valueType: Type
      )

      fun setArrayValueNew(
         env: HeapValuesEnv,
         lhs: V, index: Value?, allocSite: IHeapValues<V>
      )

      fun getArrayValue(env: HeapValuesEnv, lhs: V, rhs: V, index: Value?): Boolean
      fun getArrayValue(env: HeapValuesEnv, lhs: V, rhs: Value, index: Value?): Boolean

      fun getArrayLength(array: V): IHeapValues<V>? =
         IIFact.DefaultImpls.getArrayLength(this, array)

      fun getArray(array: V): IArrayHeapKV<Int, V>? =
         IIFact.DefaultImpls.getArray(this, array)

      /* ----------- 调用 ----------- */

      fun addCalledMethod(sm: SootMethod)
      fun callEntryFlowFunction(
         context: Ctx<SootMethod, Unit, IFact<V>>,
         callee: SootMethod,
         node: Unit,
         succ: Unit
      )

      /* ----------- GC / Build ----------- */

      fun gc()
      fun build(): IFact<V>

      /* ----------- 默认代理 ----------- */

      companion object {
         fun <V> IHeapValues<V>.targets(slot: V): IHeapValues<V> =
            IIFact.DefaultImpls.getTargets(this, slot)
      }
   }

   /* ---------- 默认代理 ---------- */

   companion object {
      fun <V> IFact<V>.targets(slot: V): IHeapValues<V> =
         IIFact.DefaultImpls.getTargets(this, slot)
   }
}
