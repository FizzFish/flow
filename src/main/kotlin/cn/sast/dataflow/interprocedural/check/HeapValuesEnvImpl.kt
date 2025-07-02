package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import soot.Unit
import soot.jimple.internal.JNopStmt

internal class HeapValuesEnvImpl(node: Unit) : HeapValuesEnv(node) {
   public constructor(p: IPath) : this(p.getNode())
   public companion object {
      public final val phantomUnit: JNopStmt
   }
}
