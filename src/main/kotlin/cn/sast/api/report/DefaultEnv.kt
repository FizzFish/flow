package cn.sast.api.report

import com.feysh.corax.config.api.report.Region.Mutable
import soot.*
import soot.jimple.InvokeExpr
import java.util.LinkedHashMap

/**
 * 报告环境默认实现，持有各种可变定位信息。
 *
 * 所有属性对框架 **internal set**，防止插件随意更改。
 */
open class DefaultEnv(
    override var region: Mutable,
    override var fileName: String?    = null,
    override var callSite: soot.Unit? = null,
    override var callee: SootMethod?  = null,
    override var container: SootMethod? = null,
    override var invokeExpr: InvokeExpr? = null,
    override var clazz: SootClass?    = null,
    override var field: SootField?    = null,
    override var method: SootMethod?  = null,
    override val args: MutableMap<Any, Any> = LinkedHashMap()
) : AbstractBugEnv()
