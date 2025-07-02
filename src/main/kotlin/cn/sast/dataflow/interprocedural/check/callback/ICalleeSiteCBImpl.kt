package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.idfa.check.ICalleeCB

public interface ICalleeSiteCBImpl : ICalleeCB<IHeapValues<IValue>, IFact.Builder<IValue>>, ICallCBImpl<IHeapValues<IValue>, IFact.Builder<IValue>>
