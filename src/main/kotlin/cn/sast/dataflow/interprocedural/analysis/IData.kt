package cn.sast.dataflow.interprocedural.analysis

public interface IData<V> : IDiffAble<V> {
   public abstract fun reference(res: MutableCollection<Any>) {
   }

   public abstract fun builder(): cn.sast.dataflow.interprocedural.analysis.IData.Builder<Any> {
   }

   public abstract fun computeHash(): Int {
   }

   public abstract fun cloneAndReNewObjects(re: IReNew<Any>): IData<Any> {
   }

   public interface Builder<V> {
      public abstract fun union(hf: AbstractHeapFactory<Any>, that: IData<Any>) {
      }

      public abstract fun cloneAndReNewObjects(re: IReNew<Any>) {
      }

      public abstract fun build(): IData<Any> {
      }
   }
}
