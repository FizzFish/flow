package cn.sast.dataflow.infoflow.svfa

import java.util.Objects
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import soot.Value

internal class FlowFact {
   public final var data: PersistentMap<Value, PersistentSet<VFNode>> = ExtensionsKt.persistentHashMapOf()
      internal set

   public override fun toString(): String {
      return "\n${CollectionsKt.joinToString$default(CollectionsKt.flatten(this.data.values()), "\n", null, null, 0, null, null, 62, null)}";
   }

   public override fun hashCode(): Int {
      return Objects.hash(this.data);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else {
         return other is FlowFact && (other as FlowFact).data == this.data;
      }
   }
}
