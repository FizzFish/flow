package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.idfa.check.ICallerSiteCB
import soot.SootMethod

public interface ICallerSiteCBImpl : ICallerSiteCB<IHeapValues<IValue>, IFact.Builder<IValue>>, ICallCBImpl<IHeapValues<IValue>, IFact.Builder<IValue>> {
   public val caller: SootMethod
}
