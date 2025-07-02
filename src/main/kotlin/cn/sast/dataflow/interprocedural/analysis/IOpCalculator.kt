package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder

public interface IOpCalculator<V> : ICalculator<V> {
   public abstract fun resolve(fx: (IOpCalculator<Any>, Builder<Any>, Array<CompanionV<Any>>) -> Boolean): IOpCalculator<Any> {
   }
}
