package cn.sast.dataflow.interprocedural.analysis

/**
 * 方法符号执行成功结果。
 */
data class SymbolicSuccess<V>(val value: V) : MethodResult<V>()
