@file:SourceDebugExtension(["SMAP\nkotlin-ext.kt\nKotlin\n*S Kotlin\n*F\n+ 1 kotlin-ext.kt\ncn/sast/api/util/Kotlin_extKt\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,13:1\n136#2,9:14\n216#2:23\n217#2:25\n145#2:26\n1#3:24\n9326#4,2:27\n9476#4,4:29\n*S KotlinDebug\n*F\n+ 1 kotlin-ext.kt\ncn/sast/api/util/Kotlin_extKt\n*L\n7#1:14,9\n7#1:23\n7#1:25\n7#1:26\n7#1:24\n10#1:27,2\n10#1:29,4\n*E\n"])

package cn.sast.api.util

import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.internal.SourceDebugExtension

public fun <K, V> Map<K, V?>.nonNullValue(): Map<K, V> {
   val `destination$iv$iv`: java.util.Collection = new ArrayList();

   for (Entry element$iv$iv$iv : $this$nonNullValue.entrySet()) {
      val var10001: Any = `element$iv$iv$iv`.getValue();
      val var17: Pair = if (var10001 == null) null else TuplesKt.to(`element$iv$iv$iv`.getKey(), var10001);
      if (var17 != null) {
         `destination$iv$iv`.add(var17);
      }
   }

   return MapsKt.toMap(`destination$iv$iv` as java.util.List);
}

public fun <E> concurrentHashSetOf(vararg pairs: E): MutableSet<E> {
   val `result$iv`: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(pairs.length), 16));

   for (Object element$iv$iv : pairs) {
      `result$iv`.put(`element$iv$iv`, true);
   }

   val var10000: java.util.Set = Collections.newSetFromMap(new ConcurrentHashMap(`result$iv`));
   return var10000;
}

public fun <E> concurrentHashSetOf(): MutableSet<E> {
   val var10000: java.util.Set = Collections.newSetFromMap(new ConcurrentHashMap());
   return var10000;
}
