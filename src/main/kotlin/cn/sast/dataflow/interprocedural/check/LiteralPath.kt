package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import com.feysh.corax.config.api.IIexConst
import soot.Unit
import soot.jimple.Constant

internal class LiteralPath private constructor(node: Unit, const: Any) : IPath() {
   public open val node: Unit
   public final val const: Any

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.const = var2;
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is LiteralPath) {
         return false;
      } else if (this.equivHashCode() != (other as LiteralPath).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as LiteralPath).getNode()) {
         return false;
      } else {
         return this.const == (other as LiteralPath).const;
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31 * Integer.valueOf(System.identityHashCode(this.getNode())) + this.const.hashCode();
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv, const: Constant, info: Any?): LiteralPath {
         return IPath.Companion.getInterner(new LiteralPath(env.getNode(), var2, null));
      }

      public fun v(env: HeapValuesEnv, constIex: IIexConst): LiteralPath {
         return IPath.Companion.getInterner(new LiteralPath(env.getNode(), constIex, null));
      }
   }
}
