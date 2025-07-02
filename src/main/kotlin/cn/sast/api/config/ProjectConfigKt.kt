@file:SourceDebugExtension(["SMAP\nProjectConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ProjectConfig.kt\ncn/sast/api/config/ProjectConfigKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,45:1\n1863#2,2:46\n*S KotlinDebug\n*F\n+ 1 ProjectConfig.kt\ncn/sast/api/config/ProjectConfigKt\n*L\n9#1:46,2\n*E\n"])

package cn.sast.api.config

import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import kotlin.jvm.internal.SourceDebugExtension

public fun List<IMatchItem>.validate() {
   val `$this$forEach$iv`: java.lang.Iterable;
   for (Object element$iv : $this$forEach$iv) {
      val e: ProcessRule.IMatchItem = `element$iv` as ProcessRule.IMatchItem;
      val var10000: ProcessRule.ErrorCommit = `element$iv` as ProcessRule.IMatchItem as? ProcessRule.ErrorCommit;
      if ((e as? ProcessRule.ErrorCommit) != null) {
         val var9: java.lang.String = var10000.getError();
         if (var9 != null) {
            throw new IllegalStateException(("Invalid process-regex: `$e`, error: $var9").toString());
         }
      }
   }
}
