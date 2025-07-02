package cn.sast.dataflow.interprocedural.analysis

import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.FixPointStatus
import soot.SootMethod
import soot.Unit

public interface IProblemIteratorTerminal<V> {
   public abstract fun hasChange(context: Context<SootMethod, Unit, IFact<Any>>, new: IProblemIteratorTerminal<Any>): FixPointStatus {
   }
}
