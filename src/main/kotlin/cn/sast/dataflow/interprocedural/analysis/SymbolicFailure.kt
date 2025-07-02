package cn.sast.dataflow.interprocedural.analysis

/**
 * 方法符号执行失败结果。
 *
 * @param symbolic        失败时的符号值
 * @param concrete        捕获到的异常（可能为空）
 * @param explicit        是否显式抛出
 * @param inNestedMethod  是否发生在嵌套方法
 */
data class SymbolicFailure<V>(
   val symbolic: V,
   val concrete: Throwable?,
   val explicit: Boolean,
   val inNestedMethod: Boolean
) : MethodResult<V>()
