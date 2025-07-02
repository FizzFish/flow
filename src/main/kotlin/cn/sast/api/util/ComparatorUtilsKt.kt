@file:SourceDebugExtension(["SMAP\nComparatorUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ComparatorUtils.kt\ncn/sast/api/util/ComparatorUtilsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,37:1\n1#2:38\n*E\n"])

package cn.sast.api.util

import java.util.Comparator
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

public fun <T> Comparator<T>.compareTo(a: Collection<T>, b: Collection<T>): Int {
   val bI: java.util.Iterator = b.iterator();

   for (Object e : a) {
      if (!bI.hasNext()) {
         return 1;
      }

      val c: Int = `$this$compareTo`.compare(e, bI.next());
      if (c != 0) {
         return c;
      }
   }

   return if (bI.hasNext()) -1 else 0;
}

public fun <K : Comparable<K>, V : Comparable<V>> Comparator<Pair<K, V>>.compareToMap(a: Map<K, V>, b: Map<K, V>): Int {
   val bSorted: Int = Intrinsics.compare(a.size(), b.size());
   val aSorted: Int = if (bSorted.intValue() != 0) bSorted else null;
   return if (aSorted != null)
      aSorted.intValue()
      else
      compareTo(
         `$this$compareToMap`,
         CollectionsKt.sortedWith(MapsKt.toList(a), `$this$compareToMap`),
         CollectionsKt.sortedWith(MapsKt.toList(b), `$this$compareToMap`)
      );
}

public fun <K : Comparable<K>, V : Comparable<V>> Map<K, V>.compareToMap(other: Map<K, V>): Int {
   return compareToMap(
      ComparisonsKt.then(new ComparatorUtilsKt$compareToMap$$inlined$compareBy$1(), new ComparatorUtilsKt$compareToMap$$inlined$compareBy$2()),
      `$this$compareToMap`,
      other
   );
}

public fun <E : Comparable<E>> Collection<E>.compareToCollection(other: Collection<E>): Int {
   return compareTo(new ComparatorUtilsKt$compareToCollection$$inlined$compareBy$1(), `$this$compareToCollection`, other);
}

public fun <T : Comparable<T>> T?.compareToNullable(other: T?): Int {
   if (`$this$compareToNullable` == null && other == null) {
      return 0;
   } else if (`$this$compareToNullable` == null) {
      return 1;
   } else {
      label26:
      if (other == null) {
         return -1;
      } else {
         val var3: Int = `$this$compareToNullable`.compareTo(other);
         val var2: Int = if (var3.intValue() != 0) var3 else null;
         return if (var2 != null) var2.intValue() else 0;
      }
   }
}
