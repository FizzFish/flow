package cn.sast.dataflow.interprocedural.analysis

/** 支持“差分比较”的数据结构 */
interface IDiffAble<V> {

   /**
    * 将当前对象与 [that] 进行差异比较，
    * 回调 [cmp] 收集差异。
    */
   fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>)
}
