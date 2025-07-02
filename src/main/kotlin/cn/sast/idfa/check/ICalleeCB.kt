package cn.sast.idfa.check

import soot.SootMethod

public interface ICalleeCB<V, R> : IStmtCB, ICallCB<V, R> {
   public val callee: SootMethod

   public interface IEvalCall<V, R> : ICalleeCB<V, R>, IEvalCallCB<V, R>

   public interface IPostCall<V, R> : ICalleeCB<V, R>, IPostCallCB<V, R>

   public interface IPrevCall<V, R> : ICalleeCB<V, R>, IPrevCB
}
