package cn.sast.api.report

import cn.sast.common.IResFile
import java.io.Closeable
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.Comparator
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCounter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Counter.kt\ncn/sast/api/report/Counter\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,53:1\n462#2:54\n412#2:55\n1246#3,4:56\n1062#3:60\n1216#3,2:61\n1246#3,4:63\n1053#3:67\n*S KotlinDebug\n*F\n+ 1 Counter.kt\ncn/sast/api/report/Counter\n*L\n26#1:54\n26#1:55\n26#1:56,4\n26#1:60\n27#1:61,2\n27#1:63,4\n36#1:67\n*E\n"])
public class Counter<T> {
   private final var statistics: ConcurrentHashMap<Any, AtomicInteger> = new ConcurrentHashMap()

   private object SortMapDescendingComparator : Comparator<Entry<Any, Int>> {
      override fun compare(a: Entry<Any, Int>, b: Entry<Any, Int>): Int {
         return b.value.compareTo(a.value)
      }
   }

   private object KeyStringComparator : Comparator<Any> {
      override fun compare(a: Any, b: Any): Int {
         return a.toString().compareTo(b.toString())
      }
   }

   public fun count(item: Any, map: MutableMap<Any, AtomicInteger>) {
      var var7: AtomicInteger = new AtomicInteger();
      val `$this$count_u24lambda_u240`: Counter = this;
      val old: AtomicInteger = map.putIfAbsent(item, var7);
      if (old != null) {
         var7 = old;
      }

      var7.incrementAndGet();
   }

   public fun count(item: Any) {
      this.count((T)item, this.statistics);
   }

   public fun get(item: Any): Int {
      val var10000: AtomicInteger = this.statistics.get(item);
      return if (var10000 != null) var10000.get() else 0;
   }

   private fun sortMap(input: Map<Any, AtomicInteger>): Map<Any, Int> {
      val `$this$associateByTo$iv$iv`: java.util.Map = new LinkedHashMap(MapsKt.mapCapacity(input.size()));

      val `$i$f$associateByTo`: java.lang.Iterable;
      for (Object element$iv$iv$iv : $i$f$associateByTo) {
         `$this$associateByTo$iv$iv`.put((it as Entry).getKey(), ((it as Entry).getValue() as AtomicInteger).get());
      }

      val var19: java.lang.Iterable = CollectionsKt.sortedWith(`$this$associateByTo$iv$iv`.entrySet(), SortMapDescendingComparator);
      val `destination$iv$ivx`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(var19, 10)), 16)
      );

      for (Object element$iv$iv : var19) {
         `destination$iv$ivx`.put((var25 as Entry).getKey(), ((var25 as Entry).getValue() as java.lang.Number).intValue());
      }

      return `destination$iv$ivx`;
   }

   public fun writeResults(file: IResFile) {
      val statistics: java.util.Map = this.sortMap(this.statistics);
      if (!statistics.isEmpty()) {
         label66: {
            file.mkdirs();
            val var3: Path = file.getPath();
            val writer: Array<OpenOption> = new OpenOption[0];
            val var4: Charset = Charsets.UTF_8;
            val var15: Closeable = new OutputStreamWriter(Files.newOutputStream(var3, Arrays.copyOf(writer, writer.length)), var4);
            var var16: java.lang.Throwable = null;

            try {
               try {
                  val var17: OutputStreamWriter = var15 as OutputStreamWriter;
                  (var15 as OutputStreamWriter).write("--------sort--------\n");

                  for (Object item : CollectionsKt.sortedWith(statistics.keySet(), KeyStringComparator)) {
                     var17.write("$var19\n");
                  }

                  var17.write("\n--------frequency--------\n");

                  for (Entry var20 : statistics.entrySet()) {
                     var17.write("${(var20.getValue() as java.lang.Number).intValue()} ${var20.getKey()}\n");
                  }

                  var17.flush();
               } catch (var11: java.lang.Throwable) {
                  var16 = var11;
                  throw var11;
               }
            } catch (var12: java.lang.Throwable) {
               CloseableKt.closeFinally(var15, var16);
            }

            CloseableKt.closeFinally(var15, null);
         }
      }
   }

   public fun isNotEmpty(): Boolean {
      return !this.statistics.isEmpty();
   }

   public fun clear() {
      this.statistics.clear();
   }

   public fun size(): Int {
      return this.statistics.size();
   }
}
