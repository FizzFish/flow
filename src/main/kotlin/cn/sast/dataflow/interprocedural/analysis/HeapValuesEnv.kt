package cn.sast.dataflow.interprocedural.analysis

import soot.Unit

public abstract class HeapValuesEnv {
   public final val node: Unit

   open fun HeapValuesEnv(node: Unit) {
      this.node = node;
   }

   public override fun toString(): String {
      return "Env *${this.node.getJavaSourceStartLineNumber()} ${this.node}";
   }
}
