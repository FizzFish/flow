package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.check.ModelingStmtInfo
import soot.Unit

public class StmtModelingEnv(u: Unit, info: ModelingStmtInfo) : HeapValuesEnv(u) {
   public final val info: ModelingStmtInfo

   init {
      this.info = info;
   }
}
