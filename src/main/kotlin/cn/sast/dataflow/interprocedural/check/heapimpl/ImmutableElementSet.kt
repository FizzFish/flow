package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap

@SourceDebugExtension(["SMAP\nImmutableCollections.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ImmutableCollections.kt\ncn/sast/dataflow/interprocedural/check/heapimpl/ImmutableElementSet\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,314:1\n1#2:315\n*E\n"])
public class ImmutableElementSet<E>(fields: PersistentMap<Any, IHeapValues<IValue>> = ExtensionsKt.persistentHashMapOf(),
   unreferenced: IHeapValues<IValue>? = null
) : ImmutableElementHashMap(fields, unreferenced) {
   public final val element: Set<Any>
      public final get() {
         return this.getMap().keySet();
      }


   public override fun getName(): String {
      return "ImmHashSet";
   }

   public fun isEmpty(): Boolean {
      if (this.getMap().isEmpty()) {
         val var10000: IHeapValues = this.getUnreferenced();
         if (var10000 == null || var10000.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public fun containsAll(rhs: ImmutableElementSet<*>): Boolean {
      return this.getMap().keySet().containsAll(rhs.getMap().keySet());
   }

   public fun intersect(hf: AbstractHeapFactory<IValue>, env: HeapValuesEnv, rhs: ImmutableElementSet<Any>): ImmutableElementSet<Any> {
      if (rhs.isEmpty()) {
         return rhs;
      } else if (this.isEmpty()) {
         return this;
      } else {
         val set: java.util.Set = CollectionsKt.intersect(this.getMap().keySet(), rhs.getMap().keySet());
         val r: ImmutableElementSetBuilder = new ImmutableElementSet(null, null, 3, null).builder();

         for (Object e : set) {
            var var10000: IHeapValues = this.get(hf, (E)(if (e is Any) e else null));
            if (var10000 != null) {
               r.set(hf, env, (E)e, var10000, true);
            }

            var10000 = rhs.get(hf, (E)e);
            if (var10000 != null) {
               r.set(hf, env, (E)e, var10000, true);
            }
         }

         return r.build();
      }
   }

   public fun plus(hf: AbstractHeapFactory<IValue>, env: HeapValuesEnv, rhs: ImmutableElementSet<Any>): ImmutableElementSet<Any> {
      if (this.isEmpty()) {
         return rhs;
      } else if (rhs.isEmpty()) {
         return this;
      } else {
         val set: java.util.Set = SetsKt.plus(this.getMap().keySet(), rhs.getMap().keySet());
         val r: ImmutableElementSetBuilder = new ImmutableElementSet(null, null, 3, null).builder();

         for (Object e : set) {
            var var10000: IHeapValues = this.get(hf, (E)(if (e is Any) e else null));
            if (var10000 != null) {
               r.set(hf, env, (E)e, var10000, true);
            }

            var10000 = rhs.get(hf, (E)e);
            if (var10000 != null) {
               r.set(hf, env, (E)e, var10000, true);
            }
         }

         return r.build();
      }
   }

   public fun minus(hf: AbstractHeapFactory<IValue>, env: HeapValuesEnv, rhs: ImmutableElementSet<Any>): ImmutableElementSet<Any> {
      if (this.isEmpty()) {
         return this;
      } else if (rhs.isEmpty()) {
         return this;
      } else {
         val r: ImmutableElementSetBuilder = this.builder();

         for (Object e : rhs.getElement()) {
            val var10000: IHeapValues = this.get(hf, (E)e);
            if (var10000 != null) {
               if (var10000.isSingle()) {
                  r.getMap().remove(e);
               }
            }
         }

         return r.build();
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!super.equals(other)) {
         return false;
      } else {
         return other is ImmutableElementSet;
      }
   }

   public override fun hashCode(): Int {
      return super.hashCode();
   }

   public open fun builder(): ImmutableElementSetBuilder<Any> {
      val var10002: kotlinx.collections.immutable.PersistentMap.Builder = this.getMap().builder();
      val var10003: IHeapValues = this.getUnreferenced();
      return new ImmutableElementSetBuilder<>(var10002, if (var10003 != null) var10003.builder() else null);
   }

   fun ImmutableElementSet() {
      this(null, null, 3, null);
   }
}
