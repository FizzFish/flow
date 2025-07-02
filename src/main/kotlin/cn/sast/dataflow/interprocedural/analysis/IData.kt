package cn.sast.dataflow.interprocedural.analysis

/** 所有可差分、可引用、可克隆的数据对象顶层接口 */
interface IData<V> : IDiffAble<V> {

   /** 收集内部引用 */
   fun reference(res: MutableCollection<Any>)

   /** 返回 *可变* Builder */
   fun builder(): Builder<V>

   /** 用于依赖缓存的散列 */
   fun computeHash(): Int

   /** 克隆并在克隆过程中对对象做重映射 */
   fun cloneAndReNewObjects(re: IReNew<Any>): IData<V>

   /* ---------- Builder ---------- */

   interface Builder<V> {

      /** 与另一个数据对象并集 */
      fun union(hf: AbstractHeapFactory<Any>, that: IData<Any>)

      /** 克隆并重映射内部对象 */
      fun cloneAndReNewObjects(re: IReNew<Any>)

      /** 构建为不可变实例 */
      fun build(): IData<V>
   }
}
