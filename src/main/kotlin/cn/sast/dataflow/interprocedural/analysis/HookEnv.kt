package cn.sast.dataflow.interprocedural.analysis

import soot.Unit

public class HookEnv(ctx: AIContext, u: Unit) : HeapValuesEnv(u) {
   public final val ctx: AIContext

   init {
      this.ctx = ctx;
   }
}
