package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.report.Region
import soot.SootMethod
import soot.Unit
import soot.jimple.Stmt

public abstract class ProgramStateContext : DefaultEnv {
   public final val resInfo: IBugResInfo
   public final val callSiteStmt: Stmt
   public final val container1: SootMethod
   public final val callee1: SootMethod
   public final val guard: IExpr

   public open var callSite: Unit?
      internal final set

   public open var container: SootMethod?
      internal final set

   open fun ProgramStateContext(region: Region.Mutable, resInfo: IBugResInfo, callSiteStmt: Stmt, container1: SootMethod, callee1: SootMethod, guard: IExpr) {
      super(region, null, null, null, null, null, null, null, null, 510, null);
      this.resInfo = resInfo;
      this.callSiteStmt = callSiteStmt;
      this.container1 = container1;
      this.callee1 = callee1;
      this.guard = guard;
      this.callSite = this.callSiteStmt as Unit;
      this.container = this.container1;
   }
}
