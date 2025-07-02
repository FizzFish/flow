package cn.sast.idfa.check

import soot.jimple.InvokeExpr

public interface IInvokeStmtCB<V, R> : ICallerSiteCB<V, R> {
   public open val invokeExpr: InvokeExpr
      public open get() {
      }


   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V, R> getInvokeExpr(`$this`: IInvokeStmtCB<V, R>): InvokeExpr {
         val var10000: InvokeExpr = `$this`.getStmt().getInvokeExpr();
         return var10000;
      }
   }
}
