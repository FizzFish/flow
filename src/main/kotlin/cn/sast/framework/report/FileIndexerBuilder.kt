package cn.sast.framework.report

import cn.sast.common.IResFile
import java.util.Collections
import java.util.LinkedHashMap
import java.util.NavigableSet
import java.util.Map.Entry
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentNavigableMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.util.ArraySet

@SourceDebugExtension(["SMAP\nJavaSourceLocator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/FileIndexerBuilder\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n267#1,5:507\n267#1,5:517\n72#2,2:491\n72#2,2:494\n72#2,2:497\n72#2,2:500\n1#3:493\n1#3:496\n1#3:499\n1#3:502\n462#4:503\n412#4:504\n462#4:513\n412#4:514\n1246#5,2:505\n1249#5:512\n1246#5,2:515\n1249#5:522\n*S KotlinDebug\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/FileIndexerBuilder\n*L\n277#1:507,5\n278#1:517,5\n248#1:491,2\n249#1:494,2\n254#1:497,2\n257#1:500,2\n248#1:493\n249#1:496\n254#1:499\n257#1:502\n277#1:503\n277#1:504\n278#1:513\n278#1:514\n277#1:505,2\n277#1:512\n278#1:515,2\n278#1:522\n*E\n"])
public open class FileIndexerBuilder {
   private final val fileNameToPathMap: ConcurrentNavigableMap<String, NavigableSet<IResFile>> = (new ConcurrentSkipListMap()) as ConcurrentNavigableMap
   private final val extensionToPathMap: ConcurrentNavigableMap<String, NavigableSet<IResFile>> = (new ConcurrentSkipListMap()) as ConcurrentNavigableMap

   private final val compressToSet: Set<E>
      private final inline get() {
         val var3: Int = `$this$compressToSet`.size();
         val var10000: java.util.Set;
         if (var3 == 0) {
            var10000 = SetsKt.emptySet();
         } else if (var3 == 1) {
            var10000 = Collections.singleton(`$this$compressToSet`.first());
         } else {
            var10000 = if (0 <= var3 && var3 < 17) (new ArraySet(`$this$compressToSet`)) as java.util.Set else `$this$compressToSet`;
         }

         return var10000;
      }


   public fun addIndexMap(resFile: IResFile) {
      if (resFile.isFile()) {
         var `$this$getOrPut$iv`: ConcurrentMap = this.fileNameToPathMap;
         var `key$iv`: Any = resFile.getName();
         var var10000: Any = `$this$getOrPut$iv`.get(`key$iv`);
         if (var10000 == null) {
            val `default$iv`: Any = new ConcurrentSkipListSet();
            var10000 = `$this$getOrPut$iv`.putIfAbsent(`key$iv`, `default$iv`);
            if (var10000 == null) {
               var10000 = `default$iv`;
            }
         }

         (var10000 as NavigableSet).add(resFile);
         `$this$getOrPut$iv` = this.extensionToPathMap;
         `key$iv` = resFile.getExtension();
         var10000 = `$this$getOrPut$iv`.get(`key$iv`);
         if (var10000 == null) {
            val var12: Any = new ConcurrentSkipListSet();
            var10000 = `$this$getOrPut$iv`.putIfAbsent(`key$iv`, var12);
            if (var10000 == null) {
               var10000 = var12;
            }
         }

         (var10000 as NavigableSet).add(resFile);
      }
   }

   public fun union(indexer: FileIndexer) {
      for (Entry var3 : indexer.getFileNameToPathMap$corax_framework().entrySet()) {
         val k: java.lang.String = var3.getKey() as java.lang.String;
         val v: java.util.Set = var3.getValue() as java.util.Set;
         val `$this$getOrPut$iv`: ConcurrentMap = this.fileNameToPathMap;
         var var10000: Any = this.fileNameToPathMap.get(k);
         if (var10000 == null) {
            val `default$iv`: Any = new ConcurrentSkipListSet();
            var10000 = `$this$getOrPut$iv`.putIfAbsent(k, `default$iv`);
            if (var10000 == null) {
               var10000 = `default$iv`;
            }
         }

         (var10000 as NavigableSet).addAll(v);
      }

      for (Entry var12 : indexer.getExtensionToPathMap$corax_framework().entrySet()) {
         val var13: java.lang.String = var12.getKey() as java.lang.String;
         val var14: java.util.Set = var12.getValue() as java.util.Set;
         val var15: ConcurrentMap = this.extensionToPathMap;
         var var20: Any = this.extensionToPathMap.get(var13);
         if (var20 == null) {
            val var18: Any = new ConcurrentSkipListSet();
            var20 = var15.putIfAbsent(var13, var18);
            if (var20 == null) {
               var20 = var18;
            }
         }

         (var20 as NavigableSet).addAll(var14);
      }
   }

   public fun build(): FileIndexer {
      return new FileIndexer(this.fileNameToPathMap, this.extensionToPathMap);
   }

   public fun sortAndOptimizeMem(): FileIndexer {
      var `$this$mapValues$iv`: java.util.Map = this.fileNameToPathMap;
      var `destination$iv$iv`: java.util.Map = new LinkedHashMap(MapsKt.mapCapacity(this.fileNameToPathMap.size()));

      val `$this$associateByTo$iv$iv$iv`: java.lang.Iterable;
      for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
         val var10001: Any = (`element$iv$iv$iv` as Entry).getKey();
         var var10000: Any = (`element$iv$iv$iv` as Entry).getValue();
         val `$this$compressToSet$iv`: NavigableSet = var10000 as NavigableSet;
         val var18: Int = (var10000 as NavigableSet).size();
         if (var18 == 0) {
            var10000 = SetsKt.emptySet();
         } else if (var18 == 1) {
            var10000 = Collections.singleton(`$this$compressToSet$iv`.first());
         } else {
            var10000 = if (0 <= var18 && var18 < 17) (new ArraySet(`$this$compressToSet$iv`)) as java.util.Set else `$this$compressToSet$iv`;
         }

         `destination$iv$iv`.put(var10001, var10000);
      }

      `$this$mapValues$iv` = this.extensionToPathMap;
      `destination$iv$iv` = new LinkedHashMap(MapsKt.mapCapacity(this.extensionToPathMap.size()));

      for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
         val var44: Any = (var32 as Entry).getKey();
         var var42: Any = (var32 as Entry).getValue();
         val var37: NavigableSet = var42 as NavigableSet;
         val var39: Int = (var42 as NavigableSet).size();
         if (var39 == 0) {
            var42 = SetsKt.emptySet();
         } else if (var39 == 1) {
            var42 = Collections.singleton(var37.first());
         } else {
            var42 = if (0 <= var39 && var39 < 17) (new ArraySet(var37)) as java.util.Set else var37;
         }

         `destination$iv$iv`.put(var44, var42);
      }

      return new FileIndexer(`destination$iv$iv`, `destination$iv$iv`);
   }

   public companion object
}
