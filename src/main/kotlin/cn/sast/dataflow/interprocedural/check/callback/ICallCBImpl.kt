package cn.sast.dataflow.interprocedural.check.callback

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.HookEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.idfa.check.ICallCB

interface ICallCBImpl<V, R> : ICallCB<V, R> {
    val env: HookEnv
    val newEnv: AnyNewExprEnv
    val hf: AbstractHeapFactory<IValue>
}