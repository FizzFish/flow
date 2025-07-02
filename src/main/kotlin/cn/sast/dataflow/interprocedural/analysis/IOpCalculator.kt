package cn.sast.dataflow.interprocedural.analysis

/**
 * 表达式求值器，支持将自身再交由外部函数 [resolve] 处理。
 */
interface IOpCalculator<V> : ICalculator<V> {

   /**
    * 把自身交给 [fx]；若 [fx] 返回 `true` 表示处理完毕，
    * 则停止链式调用并返回当前实例；否则可返回新的 IOpCalculator。
    */
   fun resolve(
      fx: (self: IOpCalculator<Any>,
           res: IHeapValues.Builder<Any>,
           args: Array<CompanionV<Any>>) -> Boolean
   ): IOpCalculator<Any>
}
