package cn.sast.dataflow.infoflow.svfa

import java.util.LinkedList
import java.util.Queue
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSparseInfoflowSolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/RefCntUnit\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,232:1\n1863#2,2:233\n*S KotlinDebug\n*F\n+ 1 SparseInfoflowSolver.kt\ncn/sast/dataflow/infoflow/svfa/RefCntUnit\n*L\n38#1:233,2\n*E\n"])
public class RefCntUnit<N>(u: Any, cnt: Int) {
   public final val u: Any

   public final var cnt: Int
      internal set

   private final val ref: Queue<RefCntUnit<Any>>

   init {
      this.u = (N)u;
      this.cnt = cnt;
      this.ref = new LinkedList<>();
   }

   public fun add(prev: RefCntUnit<Any>) {
      this.ref.add(prev);
      val var2: Int = prev.cnt++;
   }

   public fun dec() {
      this.cnt += -1;
      if (this.cnt == 0) {
         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            (`element$iv` as RefCntUnit).dec();
         }
      }

      if (this.cnt < 0) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      }
   }

   public override fun hashCode(): Int {
      return if (this.u != null) this.u.hashCode() else 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else {
         return other is RefCntUnit && (other as RefCntUnit).u == this.u;
      }
   }

   public override fun toString(): String {
      return "${this.u} ${this.cnt}";
   }
}
