package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage

public class WMath : SummaryHandlePackage<IValue> {
   public override fun ACheckCallAnalysis.register() {
   }

   public companion object {
      public fun v(): WMath {
         return new WMath();
      }
   }
}
