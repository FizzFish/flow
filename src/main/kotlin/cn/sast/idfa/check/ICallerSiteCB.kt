package cn.sast.idfa.check

import soot.SootMethod

public interface ICallerSiteCB<V, R> : IStmtCB, ICallCB<V, R> {
   public val caller: SootMethod

   public interface IEvalCall<V, R> : ICallerSiteCB<V, R>, IEvalCallCB<V, R>

   public interface IPostCall<V, R> : ICallerSiteCB<V, R>, IPostCallCB<V, R>

   public interface IPrevCall<V, R> : ICallerSiteCB<V, R>, IPrevCB
}
