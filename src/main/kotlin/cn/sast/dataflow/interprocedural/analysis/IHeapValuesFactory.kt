package cn.sast.dataflow.interprocedural.analysis

/** 创建 [IHeapValues] 及其 Builder 的抽象工厂 */
interface IHeapValuesFactory<V> {

   /** 返回一个 *空* 不可变集合 */
   fun empty(): IHeapValues<V>

   /** 返回 “空集合” 的可变 Builder */
   fun emptyBuilder(): IHeapValues.Builder<V> = empty().builder()

   /** 构造只含单元素的不可变集合 */
   fun single(v: CompanionV<V>): IHeapValues<V>
}
