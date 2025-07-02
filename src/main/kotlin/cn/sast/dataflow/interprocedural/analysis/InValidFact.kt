package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.FixPointStatus
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.ImmutableSet
import soot.*
import soot.jimple.Constant

/**
 * “无效” 事实基类：代表 TOP 或 BOTTOM 的派生实现。
 */
abstract class InValidFact<V> : IFact<V> {

   /* ---------- 抽象属性留给子类 ---------- */
   override val callStack: CallStackContext
      get() = error("callStack not available for invalid fact")

   /* ---------- IFact 默认实现 ---------- */

   override fun hasChange(
      context: Context<SootMethod, Unit, IFact<Any>>,
      new: IProblemIteratorTerminal<Any>
   ): FixPointStatus =
      if (this === new) FixPointStatus.Fixpoint else FixPointStatus.HasChange

   override fun getSlots(): Set<Any> = emptySet()
   override fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? = null
   override fun getCalledMethods(): ImmutableSet<SootMethod> = persistentHashSetOf()

   override fun getValueData(v: Any, mt: Any): IData<Any>? = null

   override fun getArrayLength(array: V): IHeapValues<V>? =
      IIFact.DefaultImpls.getArrayLength(this, array)

   override fun getArray(array: V): IArrayHeapKV<Int, V>? =
      IIFact.DefaultImpls.getArray(this, array)

   override fun getOfSootValue(
      env: HeapValuesEnv,
      value: Value,
      valueType: Type
   ): IHeapValues<V> =
      IIFact.DefaultImpls.getOfSootValue(this, env, value, valueType) as IHeapValues<V>
}
