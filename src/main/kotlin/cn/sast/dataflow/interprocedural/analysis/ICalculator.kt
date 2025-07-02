package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import soot.Type

public interface ICalculator<V> {
   public val res: Builder<Any>

   public abstract fun isFullySimplified(): Boolean {
   }

   public abstract fun putSummaryValue(type: Type, special: Any) {
   }

   public abstract fun putSummaryIfNotConcrete(type: Type, special: Any) {
   }
}
