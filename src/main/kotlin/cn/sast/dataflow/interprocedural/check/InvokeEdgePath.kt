package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import soot.SootMethod
import soot.Unit

public class InvokeEdgePath private constructor(node: Unit,
   interproceduralPathMap: Map<IPath, EntryPath>,
   path: IPath,
   container: SootMethod,
   callee: SootMethod
) : IPath() {
   public open val node: Unit
   public final val path: IPath
   public final val container: SootMethod
   public final val callee: SootMethod
   public final val interproceduralPathMap: Map<IPath, EntryPath>

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.path = path;
      this.container = container;
      this.callee = callee;
      this.interproceduralPathMap = PathCompanionKt.access$getShort(interproceduralPathMap);
   }

   public override fun toString(): String {
      return "${this.container} ${this.getNode()}";
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is InvokeEdgePath) {
         return false;
      } else if (this.equivHashCode() != (other as InvokeEdgePath).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as InvokeEdgePath).getNode()) {
         return false;
      } else if (this.path != (other as InvokeEdgePath).path) {
         return false;
      } else if (!(this.container == (other as InvokeEdgePath).container)) {
         return false;
      } else {
         return this.callee == (other as InvokeEdgePath).callee;
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31
               * Integer.valueOf(
                  31 * Integer.valueOf(31 * Integer.valueOf(System.identityHashCode(this.getNode())) + this.path.hashCode()) + this.container.hashCode()
               )
            + this.callee.hashCode();
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv, interproceduralPathMap: Map<IPath, EntryPath>, path: IPath, container: SootMethod, callee: SootMethod): InvokeEdgePath {
         return IPath.Companion.getInterner(new InvokeEdgePath(env.getNode(), interproceduralPathMap, path, container, callee, null));
      }
   }
}
