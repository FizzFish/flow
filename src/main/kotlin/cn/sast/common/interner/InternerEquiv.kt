package cn.sast.common.interner

public interface InternerEquiv {
   public abstract fun equivTo(other: Any?): Boolean {
   }

   public abstract fun equivHashCode(): Int {
   }
}
