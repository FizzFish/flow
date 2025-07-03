package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.IBugResInfo
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.report.Region
import soot.SootMethod
import soot.Unit
import soot.jimple.Stmt

public abstract class ProgramStateContext(
    region: Region.Mutable,
    public final val resInfo: IBugResInfo,
    public final val callSiteStmt: Stmt,
    public final val container1: SootMethod,
    public final val callee1: SootMethod,
    public final val guard: IExpr
) : DefaultEnv(region, null, null, null, null, null, null, null, null, 510, null) {

    public open var callSite: Unit? = callSiteStmt as Unit
        internal set

    public open var container: SootMethod? = container1
        internal set
}