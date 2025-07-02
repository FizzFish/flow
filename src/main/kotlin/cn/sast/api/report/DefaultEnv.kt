package cn.sast.api.report

import com.feysh.corax.config.api.report.Region.Mutable
import java.util.LinkedHashMap
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Unit
import soot.jimple.InvokeExpr

public open class DefaultEnv(region: Mutable,
      fileName: String? = null,
      callSite: Unit? = null,
      callee: SootMethod? = null,
      container: SootMethod? = null,
      invokeExpr: InvokeExpr? = null,
      clazz: SootClass? = null,
      field: SootField? = null,
      method: SootMethod? = null
   )
   : AbstractBugEnv {
   public open var region: Mutable
      internal final set

   public open var fileName: String?
      internal final set

   public open var callSite: Unit?
      internal final set

   public open var callee: SootMethod?
      internal final set

   public open var container: SootMethod?
      internal final set

   public open var invokeExpr: InvokeExpr?
      internal final set

   public open var clazz: SootClass?
      internal final set

   public open var field: SootField?
      internal final set

   public open var method: SootMethod?
      internal final set

   public open val args: MutableMap<Any, Any>

   init {
      this.region = region;
      this.fileName = fileName;
      this.callSite = callSite;
      this.callee = callee;
      this.container = container;
      this.invokeExpr = invokeExpr;
      this.clazz = clazz;
      this.field = field;
      this.method = method;
      this.args = new LinkedHashMap<>();
   }
}
