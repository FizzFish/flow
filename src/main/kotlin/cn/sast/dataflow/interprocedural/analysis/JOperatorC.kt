package cn.sast.dataflow.interprocedural.analysis

public interface JOperatorC<V> : JOperator<V> {
   public abstract fun markEntry(): JOperatorC<Any> {
   }

   public abstract fun pop(): CompanionV<Any> {
   }

   public abstract fun popHV(): IHeapValues<Any> {
   }
}
