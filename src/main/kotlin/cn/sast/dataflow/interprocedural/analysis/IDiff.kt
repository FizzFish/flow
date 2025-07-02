package cn.sast.dataflow.interprocedural.analysis

/** 两个节点差异比较接口 */
fun interface IDiff<V> {
   fun diff(left: CompanionV<V>, right: CompanionV<out V?>)
}
