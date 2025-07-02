package cn.sast.dataflow.util

import cn.sast.dataflow.interprocedural.analysis.CompanionV

public class Printer {
   public companion object {
      public fun <V> node2String(node: V): String {
         return java.lang.String.valueOf(node);
      }

      public fun <V> nodes2String(node: Collection<CompanionV<V>>?): String {
         if (node == null) {
            return "!null!";
         } else {
            return if (node.isEmpty())
               "empty"
               else
               "{ ${CollectionsKt.joinToString$default(node, null, null, null, 0, null, Printer.Companion::nodes2String$lambda$0, 31, null)} }";
         }
      }

      @JvmStatic
      fun `nodes2String$lambda$0`(it: CompanionV): java.lang.CharSequence {
         return Printer.Companion.node2String(it);
      }
   }
}
