package cn.sast.dataflow.interprocedural.analysis

/**
 * 把一组 Method-Summary 句柄注册到 [ACheckCallAnalysis]。
 */
fun interface SummaryHandlePackage<V> {

   /**
    * 将自身注册进 [analysis]；由调用方遍历并调用。
    */
   fun register(analysis: ACheckCallAnalysis<V, *>)
}
