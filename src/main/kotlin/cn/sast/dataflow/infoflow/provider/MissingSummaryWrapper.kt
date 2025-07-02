package cn.sast.dataflow.infoflow.provider

import soot.SootMethod
import soot.jimple.infoflow.methodSummary.data.provider.IMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.taintWrappers.ReportMissingSummaryWrapper

/**
 * 当缺失方法摘要时，先回调 `reportMissing`，再交由父类处理。
 */
class MissingSummaryWrapper(
   flows: IMethodSummaryProvider,
   private val reportMissing: (SootMethod) -> Unit
) : ReportMissingSummaryWrapper(flows) {

   override fun reportMissingMethod(method: SootMethod) {
      reportMissing(method)
      super.reportMissingMethod(method)
   }
}
