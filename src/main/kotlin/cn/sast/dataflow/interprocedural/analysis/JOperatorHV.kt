package cn.sast.dataflow.interprocedural.analysis

/** Heap-values 栈顶返回型 Operator */
interface JOperatorHV<V> : JOperator<V> {

   fun pop(): IHeapValues<V>

   fun <K> setKVValue(mt: Any, lhs: CompanionV<V>, key: K?): JOperatorHV<V>

   fun <K> getKVValue(mt: Any, rhs: CompanionV<V>, key: K?): JOperatorHV<V>

   fun assignLocal(lhs: Any, rhsValue: IHeapValues<V>): JOperatorHV<V>

   fun markOfArrayLength(rhs: CompanionV<V>): JOperatorHV<V>

   fun dataElementCopyToSequenceElement(sourceElement: IHeapValues<IValue>): JOperatorHV<V>
}
