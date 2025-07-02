package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import kotlinx.collections.immutable.PersistentList
import soot.SootMethod
import soot.Unit

public class EntryPath private constructor(special: PersistentList<ReferenceContext>, node: Unit) : IPath() {
   public open val node: Unit
   public final val special: PersistentList<ReferenceContext>

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.special = IPath.Companion.specialInterner(special);
   }

   public override fun toString(): String {
      return "${CollectionsKt.joinToString$default(this.special as java.lang.Iterable, ":", null, null, 0, null, null, 62, null)} ${this.getNode()}";
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is EntryPath) {
         return false;
      } else if (this.equivHashCode() != (other as EntryPath).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as EntryPath).getNode()) {
         return false;
      } else {
         return this.special === (other as EntryPath).special;
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31 * Integer.valueOf(System.identityHashCode(this.getNode())) + System.identityHashCode(this.special);
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(special: PersistentList<ReferenceContext>, method: SootMethod, env: HeapValuesEnv): EntryPath {
         return IPath.Companion.getInterner(new EntryPath(special, env.getNode(), null));
      }
   }
}
