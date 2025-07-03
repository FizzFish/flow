package cn.sast.api.report

import com.feysh.corax.config.api.report.Region.Mutable
import java.util.LinkedHashMap
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Unit
import soot.jimple.InvokeExpr

public open class DefaultEnv(
    region: Mutable,
    fileName: String? = null,
    callSite: Unit? = null,
    callee: SootMethod? = null,
    container: SootMethod? = null,
    invokeExpr: InvokeExpr? = null,
    clazz: SootClass? = null,
    field: SootField? = null,
    method: SootMethod? = null
) : AbstractBugEnv {
    public open var region: Mutable = region
        internal set

    public open var fileName: String? = fileName
        internal set

    public open var callSite: Unit? = callSite
        internal set

    public open var callee: SootMethod? = callee
        internal set

    public open var container: SootMethod? = container
        internal set

    public open var invokeExpr: InvokeExpr? = invokeExpr
        internal set

    public open var clazz: SootClass? = clazz
        internal set

    public open var field: SootField? = field
        internal set

    public open var method: SootMethod? = method
        internal set

    public open val args: MutableMap<Any, Any> = LinkedHashMap()
}