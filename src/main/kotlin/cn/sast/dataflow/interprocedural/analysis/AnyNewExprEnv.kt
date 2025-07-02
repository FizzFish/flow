package cn.sast.dataflow.interprocedural.analysis

import soot.SootMethod
import soot.Unit

public class AnyNewExprEnv(method: SootMethod, u: Unit) : HeapValuesEnv(u) {
   public final val method: SootMethod

   init {
      this.method = method;
   }
}
