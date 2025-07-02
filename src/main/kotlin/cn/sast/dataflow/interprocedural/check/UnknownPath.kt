package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import soot.Unit

public class UnknownPath private constructor(node: Unit) : IPath() {
   public open val node: Unit

   public final var hash: Int?
      internal set

   init {
      this.node = node;
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is UnknownPath) {
         return false;
      } else if (this.equivHashCode() != (other as UnknownPath).equivHashCode()) {
         return false;
      } else {
         return this.getNode() === (other as UnknownPath).getNode();
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = System.identityHashCode(this.getNode());
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv): UnknownPath {
         return IPath.Companion.getInterner(new UnknownPath(env.getNode(), null));
      }
   }
}
