package cn.sast.dataflow.interprocedural.analysis

import soot.SootMethod
import soot.Unit

public class CallStackContext(caller: CallStackContext?, callSite: Unit, method: SootMethod, deep: Int) {
   public final val caller: CallStackContext?
   public final val callSite: Unit
   public final val method: SootMethod
   public final val deep: Int

   init {
      this.caller = caller;
      this.callSite = callSite;
      this.method = method;
      this.deep = deep;
   }

   public override fun toString(): String {
      return "at ${if (this.caller != null) this.caller.method else null} line: ${this.callSite.getJavaSourceStartLineNumber()}: ${this.callSite} invoke -> ${this.method}\n";
   }
}
