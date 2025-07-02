package cn.sast.dataflow.interprocedural.analysis

/** K → V 集合型堆对象接口 */
interface IHeapKVData<K, V> : IData<V> {

   /** 若 [key] 为 `null`，返回全量合并视图 */
   fun get(hf: IHeapValuesFactory<V>, key: K?): IHeapValues<V>?

   fun builder(): Builder<K, V>

   /* ---------- Builder ---------- */

   interface Builder<K, V> : IData.Builder<V> {

      /**
       * 向键 [key] 写入或追加 [update]。
       * 若 [key] 为 `null` 表示未知索引，写入到 *unreferenced*。
       */
      fun set(
         hf: IHeapValuesFactory<V>,
         env: HeapValuesEnv,
         key: K?,
         update: IHeapValues<V>?,
         append: Boolean
      )
   }
}
