package cn.sast.api.config

import cn.sast.api.util.ComparatorUtilsKt
import cn.sast.common.IResFile
import com.charleskorn.kaml.Yaml
import java.io.Closeable
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SourceDebugExtension(["SMAP\nCheckerPriorityConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerPriorityConfig.kt\ncn/sast/api/config/CheckerPriorityConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,52:1\n1187#2,2:53\n1261#2,4:55\n1187#2,2:59\n1261#2,4:61\n1485#2:65\n1510#2,3:66\n1513#2,3:76\n1246#2,2:81\n1485#2:83\n1510#2,3:84\n1513#2,3:94\n1246#2,2:99\n1557#2:101\n1628#2,3:102\n1249#2:105\n1249#2:106\n381#3,7:69\n462#3:79\n412#3:80\n381#3,7:87\n462#3:97\n412#3:98\n*S KotlinDebug\n*F\n+ 1 CheckerPriorityConfig.kt\ncn/sast/api/config/CheckerPriorityConfig\n*L\n16#1:53,2\n16#1:55,4\n17#1:59,2\n17#1:61,4\n31#1:65\n31#1:66,3\n31#1:76,3\n31#1:81,2\n32#1:83\n32#1:84,3\n32#1:94,3\n32#1:99,2\n33#1:101\n33#1:102,3\n32#1:105\n31#1:106\n31#1:69,7\n31#1:79\n31#1:80\n32#1:87,7\n32#1:97\n32#1:98\n*E\n"])
public data class CheckerPriorityConfig(categoryList: List<String>, severityList: List<String>) {
   @SerialName("category")
   public final val categoryList: List<String>

   @SerialName("severity")
   public final val severityList: List<String>

   init {
      this.categoryList = categoryList;
      this.severityList = severityList;
   }

   private fun getComparator(): Comparator<ChapterFlat> {
      val severityMap: java.lang.Iterable = CollectionsKt.withIndex(this.categoryList);
      val `$this$associateTo$iv$iv`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(severityMap, 10)), 16)
      );

      for (Object element$iv$iv : $this$associate$iv) {
         val var20: Pair = TuplesKt.to((`element$iv$iv` as IndexedValue).getValue(), (`element$iv$iv` as IndexedValue).getIndex());
         `$this$associateTo$iv$iv`.put(var20.getFirst(), var20.getSecond());
      }

      val `$this$associate$ivx`: java.lang.Iterable = CollectionsKt.withIndex(this.severityList);
      val `destination$iv$ivx`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associate$ivx`, 10)), 16)
      );

      for (Object element$iv$iv : $this$associate$ivx) {
         val var22: Pair = TuplesKt.to((`element$iv$iv` as IndexedValue).getValue(), (`element$iv$iv` as IndexedValue).getIndex());
         `destination$iv$ivx`.put(var22.getFirst(), var22.getSecond());
      }

      val var14: java.util.Map = `destination$iv$ivx`;
      return new Comparator(`$this$associateTo$iv$iv`, var14) {
         {
            this.$categoryMap = `$categoryMap`;
            this.$severityMap = `$severityMap`;
         }

         public final int compare(ChapterFlat a, ChapterFlat b) {
            var var4: Int = ComparatorUtilsKt.compareToNullable(this.$categoryMap.get(a.getCategory()), this.$categoryMap.get(b.getCategory()));
            var var3: Int = if (var4.intValue() != 0) var4 else null;
            if (var3 != null) {
               return var3.intValue();
            } else {
               var4 = ComparatorUtilsKt.compareToNullable(this.$severityMap.get(a.getSeverity()), this.$severityMap.get(b.getSeverity()));
               var3 = if (var4.intValue() != 0) var4 else null;
               label35:
               if (var3 != null) {
                  return var3.intValue();
               } else {
                  var4 = ComparatorUtilsKt.compareToNullable(a.getRuleId(), b.getRuleId());
                  var3 = if (var4.intValue() != 0) var4 else null;
                  return if (var3 != null) var3.intValue() else 0;
               }
            }
         }
      };
   }

   private fun sort(chapters: List<ChapterFlat>): List<ChapterFlat> {
      return CollectionsKt.toList(CollectionsKt.toSortedSet(chapters, this.getComparator()));
   }

   public fun getSortTree(chapters: List<ChapterFlat>): Map<String, Map<String, List<String>>> {
      val `$this$mapValues$iv`: java.lang.Iterable = this.sort(chapters);
      var `destination$iv$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv$iv : $this$groupBy$iv) {
         val `it$iv$iv`: Any = (`$i$f$associateByTo` as ChapterFlat).getCategory();
         val it1: Any = `destination$iv$iv`.get(`it$iv$iv`);
         val var10000: Any;
         if (it1 == null) {
            val var57: Any = new ArrayList();
            `destination$iv$iv`.put(`it$iv$iv`, var57);
            var10000 = var57;
         } else {
            var10000 = it1;
         }

         (var10000 as java.util.List).add(`$i$f$associateByTo`);
      }

      `destination$iv$iv` = new LinkedHashMap(MapsKt.mapCapacity(`destination$iv$iv`.size()));

      val var49: java.lang.Iterable;
      for (Object element$iv$iv$iv : var49) {
         var var10001: Any = (var53 as Entry).getKey();
         val `$this$mapValues$ivx`: java.lang.Iterable = (var53 as Entry).getValue() as java.lang.Iterable;
         var `destination$iv$ivx`: java.util.Map = new LinkedHashMap();

         for (Object element$iv$iv : $this$mapValues$ivx) {
            val `it$iv$iv`: Any = (`$i$f$associateByTo` as ChapterFlat).getSeverity();
            val it2: Any = `destination$iv$ivx`.get(`it$iv$iv`);
            val var72: Any;
            if (it2 == null) {
               val var70: Any = new ArrayList();
               `destination$iv$ivx`.put(`it$iv$iv`, var70);
               var72 = var70;
            } else {
               var72 = it2;
            }

            (var72 as java.util.List).add(`$i$f$associateByTo`);
         }

         `destination$iv$ivx` = new LinkedHashMap(MapsKt.mapCapacity(`destination$iv$ivx`.size()));

         val var62: java.lang.Iterable;
         for (Object element$iv$iv$ivx : var62) {
            var10001 = (`element$iv$iv$ivx` as Entry).getKey();
            val `$this$map$iv`: java.lang.Iterable = (`element$iv$iv$ivx` as Entry).getValue() as java.lang.Iterable;
            val `destination$iv$ivxx`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$ivxx`.add((`item$iv$iv` as ChapterFlat).getRuleId());
            }

            `destination$iv$ivx`.put(var10001, `destination$iv$ivxx` as java.util.List);
         }

         `destination$iv$iv`.put(var10001, `destination$iv$ivx`);
      }

      return `destination$iv$iv`;
   }

   public fun getRuleWithSortNumber(chapters: List<ChapterFlat>): Iterable<IndexedValue<ChapterFlat>> {
      return CollectionsKt.withIndex(this.sort(chapters));
   }

   public operator fun component1(): List<String> {
      return this.categoryList;
   }

   public operator fun component2(): List<String> {
      return this.severityList;
   }

   public fun copy(categoryList: List<String> = this.categoryList, severityList: List<String> = this.severityList): CheckerPriorityConfig {
      return new CheckerPriorityConfig(categoryList, severityList);
   }

   public override fun toString(): String {
      return "CheckerPriorityConfig(categoryList=${this.categoryList}, severityList=${this.severityList})";
   }

   public override fun hashCode(): Int {
      return this.categoryList.hashCode() * 31 + this.severityList.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CheckerPriorityConfig) {
         return false;
      } else {
         val var2: CheckerPriorityConfig = other as CheckerPriorityConfig;
         if (!(this.categoryList == (other as CheckerPriorityConfig).categoryList)) {
            return false;
         } else {
            return this.severityList == var2.severityList;
         }
      }
   }

   public companion object {
      private final val yamlFormat: Yaml

      public fun deserialize(checkerPriorityYamlFile: IResFile): CheckerPriorityConfig {
         label19: {
            val var10000: Path = checkerPriorityYamlFile.getPath();
            val var10001: Array<OpenOption> = new OpenOption[0];
            val var11: InputStream = Files.newInputStream(var10000, Arrays.copyOf(var10001, var10001.length));
            val var2: Closeable = var11;
            var var3: java.lang.Throwable = null;

            try {
               try {
                  val var10: CheckerPriorityConfig = Yaml.decodeFromStream$default(
                     CheckerPriorityConfig.access$getYamlFormat$cp(),
                     CheckerPriorityConfig.Companion.serializer() as DeserializationStrategy,
                     var2 as InputStream,
                     null,
                     4,
                     null
                  ) as CheckerPriorityConfig;
               } catch (var6: java.lang.Throwable) {
                  var3 = var6;
                  throw var6;
               }
            } catch (var7: java.lang.Throwable) {
               CloseableKt.closeFinally(var2, var3);
            }

            CloseableKt.closeFinally(var2, null);
         }
      }

      public fun serializer(): KSerializer<CheckerPriorityConfig> {
         return CheckerPriorityConfig.$serializer.INSTANCE as KSerializer<CheckerPriorityConfig>;
      }
   }
}
