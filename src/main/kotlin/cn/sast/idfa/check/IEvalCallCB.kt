package cn.sast.idfa.check

public interface IEvalCallCB<V, R> : IEvalCB, ICallCB<V, R> {
   public var isEvalAble: Boolean
      internal final set
}
