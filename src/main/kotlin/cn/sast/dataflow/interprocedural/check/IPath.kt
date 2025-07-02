package cn.sast.dataflow.interprocedural.check

import cn.sast.common.interner.InternerEquiv
import cn.sast.common.interner.WeakInternerX
import com.github.benmanes.caffeine.cache.Interner
import soot.Unit

public sealed class IPath protected constructor() : InternerEquiv {
   public abstract val node: Unit

   @JvmStatic
   fun {
      val var10000: Interner = Interner.newWeakInterner();
      specialWeakInterner = var10000;
   }

   public companion object {
      private final val specialWeakInterner: Interner<Any>
      private final val weakInterner: WeakInternerX

      public final val interner: T
         public final get() {
            return (T)IPath.access$getWeakInterner$cp().intern(`$this$interner`);
         }


      public fun <T> specialInterner(v: T): T {
         return (T)IPath.access$getSpecialWeakInterner$cp().intern(v);
      }
   }
}
