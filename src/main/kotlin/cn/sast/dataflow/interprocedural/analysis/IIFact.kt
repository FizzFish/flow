package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import kotlinx.collections.immutable.ImmutableSet
import mu.KotlinLogging
import soot.*
import soot.jimple.Constant

/**
 * 在 *数据流分析* 中跨方法传播的“事实”最小抽象接口。
 */
interface IIFact<V> {

   val hf: AbstractHeapFactory<Any>
   val callStack: CallStackContext

   /* ---------- 业务数据 ---------- */

   fun getValueData(v: Any, mt: Any): IData<Any>?

   /** 安全(空)包装版本 */
   fun getTargets(slot: Any): IHeapValues<Any> =
      getTargetsUnsafe(slot) ?: hf.empty()

   fun getTargetsUnsafe(slot: Any): IHeapValues<Any>?
   fun getSlots(): Set<Any>
   fun getCalledMethods(): ImmutableSet<SootMethod>

   /* ---------- 状态 ---------- */

   fun isBottom(): Boolean
   fun isTop(): Boolean
   fun isValid(): Boolean = !isTop() && !isBottom()

   /* ---------- 查询 ---------- */

   fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any>?
   fun getArrayLength(array: Any): IHeapValues<Any>? =
      getArray(array)?.getArrayLength()

   fun getArray(array: Any): IArrayHeapKV<Int, Any>? =
      (getValueData(array, BuiltInModelT.Array) as? IArrayHeapKV<*, *>)
              as? IArrayHeapKV<Int, Any>

   /**
    * 根据 [Value] 与其 [valueType] 推导出目标集合
    */
   fun getOfSootValue(
      env: HeapValuesEnv, value: Value, valueType: Type
   ): IHeapValues<Any> = when (value) {
      is Constant -> {
         val ty = if (value.type is RefLikeType) value.type else valueType
         hf.push(env, hf.newConstVal(value, ty))
            .markOfConstant(value)
            .popHV()
      }
      is Local   -> getTargets(value)
      else -> error("Unsupported soot.Value: $value")
   }

   /* ---------- 默认静态代理 ---------- */
   companion object {
      @JvmField val logger = KotlinLogging.logger {}
   }
}
