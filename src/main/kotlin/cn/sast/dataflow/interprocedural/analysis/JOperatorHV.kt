package cn.sast.dataflow.interprocedural.analysis

public interface JOperatorHV<V> : JOperator<V> {
   public abstract fun pop(): IHeapValues<Any> {
   }

   public abstract fun <K> setKVValue(mt: Any, lhs: CompanionV<Any>, key: K?): JOperatorHV<Any> {
   }

   public abstract fun <K> getKVValue(mt: Any, rhs: CompanionV<Any>, key: K?): JOperatorHV<Any> {
   }

   public abstract fun assignLocal(lhs: Any, rhsValue: IHeapValues<Any>): JOperatorHV<Any> {
   }

   public abstract fun markOfArrayLength(rhs: CompanionV<Any>): JOperatorHV<Any> {
   }

   public abstract fun dataElementCopyToSequenceElement(sourceElement: IHeapValues<IValue>): JOperatorHV<Any> {
   }
}
