package cn.sast.dataflow.interprocedural.analysis

public interface IHeapKVData<K, V> : IData<V> {
   public abstract fun get(hf: IHeapValuesFactory<Any>, key: Any?): IHeapValues<Any>? {
   }

   public abstract fun builder(): IHeapKVData.Builder<Any, Any> {
   }

   public interface Builder<K, V> : IData.Builder<V> {
      public abstract fun set(hf: IHeapValuesFactory<Any>, env: HeapValuesEnv, key: Any?, update: IHeapValues<Any>?, append: Boolean) {
      }
   }
}
