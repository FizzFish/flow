package cn.sast.common.interner

import com.google.common.base.Equivalence
import com.google.common.collect.Interner
import kotlin.properties.ReadWriteProperty

public class WeakInternerX {
   private final val interner: Interner<InternerEquiv>

   public fun <E : InternerEquiv> intern(value: E): E {
      val representative: InternerEquiv = this.interner.intern(value) as InternerEquiv;
      return (E)representative;
   }

   @JvmStatic
   fun `_init_$lambda$0`(`$map$delegate`: ReadWriteProperty<Object, Object>): Any {
      return `$map$delegate`.getValue(null, $$delegatedProperties[0]);
   }

   @JvmStatic
   fun `_init_$lambda$1`(`$keyEquivalence$delegate`: ReadWriteProperty<Object, Equivalence<InternerEquiv>>): Equivalence<InternerEquiv> {
      return `$keyEquivalence$delegate`.getValue(null, $$delegatedProperties[1]) as Equivalence<InternerEquiv>;
   }

   @JvmStatic
   fun `_init_$lambda$2`(`$keyEquivalence$delegate`: ReadWriteProperty<Object, Equivalence<InternerEquiv>>, var1: Equivalence<InternerEquiv>) {
      `$keyEquivalence$delegate`.setValue(null, $$delegatedProperties[1], var1);
   }
}
