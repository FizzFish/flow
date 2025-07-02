@file:SourceDebugExtension(["SMAP\nPathGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,213:1\n774#2:214\n865#2,2:215\n1557#2:217\n1628#2,3:218\n*S KotlinDebug\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorKt\n*L\n22#1:214\n22#1:215,2\n23#1:217\n23#1:218,3\n*E\n"])

package cn.sast.dataflow.interprocedural.check

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

public final val removeAdjacentDuplicates: List<E>
   public final get() {
      val var10000: java.util.List;
      if (`$this$removeAdjacentDuplicates`.isEmpty()) {
         var10000 = `$this$removeAdjacentDuplicates`;
      } else {
         var `$this$map$iv`: java.lang.Iterable = CollectionsKt.zipWithNext(`$this$removeAdjacentDuplicates`);
         var `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filter$iv) {
            if (!((`item$iv$iv` as Pair).getFirst() == (`item$iv$iv` as Pair).getSecond())) {
               `destination$iv$iv`.add(`item$iv$iv`);
            }
         }

         `$this$map$iv` = `destination$iv$iv` as java.util.List;
         `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

         for (Object item$iv$iv : $this$filter$iv) {
            `destination$iv$iv`.add((var16 as Pair).getFirst());
         }

         var10000 = CollectionsKt.plus(`destination$iv$iv` as java.util.List, CollectionsKt.last(`$this$removeAdjacentDuplicates`));
      }

      return var10000;
   }

