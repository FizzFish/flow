package cn.sast.dataflow.infoflow.provider

import soot.SootMethod
import soot.jimple.infoflow.methodSummary.data.provider.IMethodSummaryProvider
import soot.jimple.infoflow.methodSummary.taintWrappers.ReportMissingSummaryWrapper

public class MissingSummaryWrapper(flows: IMethodSummaryProvider, reportMissing: (SootMethod) -> Unit) : ReportMissingSummaryWrapper(flows) {
   public final val reportMissing: (SootMethod) -> Unit

   init {
      this.reportMissing = reportMissing;
   }

   protected open fun reportMissingMethod(method: SootMethod) {
      this.reportMissing.invoke(method);
      super.reportMissingMethod(method);
   }
}
