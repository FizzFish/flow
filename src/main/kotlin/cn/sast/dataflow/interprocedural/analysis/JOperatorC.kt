package cn.sast.dataflow.interprocedural.analysis

/** 单值栈顶返回型 Operator */
interface JOperatorC<V> : JOperator<V> {

   fun markEntry(): JOperatorC<V>

   fun pop(): CompanionV<V>

   fun popHV(): IHeapValues<V>
}
