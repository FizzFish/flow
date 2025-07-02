package cn.sast.common

import com.feysh.corax.config.api.CheckType
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nResourceImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceImpl.kt\ncn/sast/common/GLB\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,730:1\n1#2:731\n*E\n"])
public object GLB {
   private final val allTypes: MutableSet<CheckType> = (new LinkedHashSet()) as java.util.Set

   public operator fun plusAssign(t: CheckType) {
      synchronized (allTypes) {
         val var5: Boolean = allTypes.add(t);
      }
   }

   public operator fun plusAssign(t: Collection<CheckType>) {
      synchronized (allTypes) {
         val var5: Boolean = allTypes.addAll(t);
      }
   }

   public fun get(): Set<CheckType> {
      return allTypes;
   }
}
