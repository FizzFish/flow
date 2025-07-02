package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.idfa.check.ICallCB

public interface ICallCBImpl<V, R> : ICallCB<V, R> {
   public val env: HookEnv
   public val newEnv: AnyNewExprEnv
   public val hf: AbstractHeapFactory<IValue>
}
