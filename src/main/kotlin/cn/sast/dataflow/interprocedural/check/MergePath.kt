package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.PersistentSet.Builder
import soot.Unit

public class MergePath private constructor(node: Unit, all: PersistentSet<IPath>) : IPath() {
   public open val node: Unit
   public final val all: PersistentSet<IPath>

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.all = all;
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is MergePath) {
         return false;
      } else if (this.equivHashCode() != (other as MergePath).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as MergePath).getNode()) {
         return false;
      } else {
         return this.all === (other as MergePath).all;
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31 * Integer.valueOf(System.identityHashCode(this.getNode())) + System.identityHashCode(this.all);
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv, a: IPath, b: IPath): IPath {
         if (a === b) {
            return a;
         } else {
            val all: Builder = ExtensionsKt.persistentHashSetOf().builder();
            if (a is MergePath) {
               all.addAll((a as MergePath).getAll() as java.util.Collection);
            } else {
               all.add(a);
            }

            if (b is MergePath) {
               all.addAll((b as MergePath).getAll() as java.util.Collection);
            } else {
               all.add(b);
            }

            return this.v(env.getNode(), all.build());
         }
      }

      public fun v(env: HeapValuesEnv, paths: Set<IPath>): IPath {
         if (paths.isEmpty()) {
            return UnknownPath.Companion.v(env);
         } else if (paths.size() == 1) {
            return CollectionsKt.first(paths) as IPath;
         } else {
            val all: Builder = ExtensionsKt.persistentHashSetOf().builder();

            for (IPath path : paths) {
               if (path is MergePath) {
                  all.addAll((path as MergePath).getAll() as java.util.Collection);
               } else {
                  all.add(path);
               }
            }

            return this.v(env.getNode(), all.build());
         }
      }

      private fun v(node: Unit, set: PersistentSet<IPath>): MergePath {
         return IPath.Companion.getInterner(new MergePath(node, IPath.Companion.specialInterner(set), null));
      }
   }
}
