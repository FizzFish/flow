package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import soot.Unit

public class AssignLocalPath private constructor(node: Unit, lhs: Any, rhsValue: String, rhsValePath: IPath) : IPath() {
   public open val node: Unit
   public final val lhs: Any
   public final val rhsValue: String
   public final val rhsValePath: IPath

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.lhs = lhs;
      this.rhsValue = rhsValue;
      this.rhsValePath = rhsValePath;
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is AssignLocalPath) {
         return false;
      } else if (this.equivHashCode() != (other as AssignLocalPath).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as AssignLocalPath).getNode()) {
         return false;
      } else if (!(this.lhs == (other as AssignLocalPath).lhs)) {
         return false;
      } else if (!(this.rhsValue == (other as AssignLocalPath).rhsValue)) {
         return false;
      } else {
         return this.rhsValePath === (other as AssignLocalPath).rhsValePath;
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31
               * Integer.valueOf(
                  31 * Integer.valueOf(31 * Integer.valueOf(System.identityHashCode(this.getNode())) + this.lhs.hashCode()) + this.rhsValue.hashCode()
               )
            + this.rhsValePath.hashCode();
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv, lhs: Any, rhsValue: IValue, rhsValePath: IPath): AssignLocalPath {
         return IPath.Companion.getInterner(new AssignLocalPath(env.getNode(), lhs, rhsValue.toString(), rhsValePath, null));
      }
   }
}
