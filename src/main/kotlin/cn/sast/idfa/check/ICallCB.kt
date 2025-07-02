package cn.sast.idfa.check

public interface ICallCB<V, R> {
   public val global: Any

   public var `return`: Any
      internal final set

   public val `this`: Any

   public var out: Any
      internal final set

   public abstract fun arg(argIndex: Int): Any {
   }

   public abstract fun argToValue(argIndex: Int): Any {
   }
}
