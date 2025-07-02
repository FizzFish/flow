package cn.sast.dataflow.interprocedural.analysis

import soot.Type

/** 表达式求值器 —— 将多个 [IHeapValues] 合并到 [res] 里 */
interface ICalculator<V : Any> {

   /** 结果累积的可变集合 */
   val res: IHeapValues.Builder<V>

   /** 是否已化简到不可再继续 */
   fun isFullySimplified(): Boolean

   /** 写入摘要值 */
   fun putSummaryValue(type: Type, special: Any)

   /** 若 [type] 不是具体类型时才写入摘要值 */
   fun putSummaryIfNotConcrete(type: Type, special: Any)
}
