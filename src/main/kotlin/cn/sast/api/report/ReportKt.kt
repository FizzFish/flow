@file:SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/ReportKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n12834#2,3:452\n9326#2,2:456\n9476#2,4:458\n1#3:455\n*S KotlinDebug\n*F\n+ 1 Report.kt\ncn/sast/api/report/ReportKt\n*L\n236#1:452,3\n410#1:456,2\n410#1:458,4\n*E\n"])

package cn.sast.api.report

import cn.sast.api.config.MainConfig
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.BugMessage.Env
import java.security.MessageDigest
import java.util.Arrays
import java.util.LinkedHashMap
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension

public final val perfectName: String
   public final get() {
      return "${`$this$perfectName`.getChecker().getClass().getSimpleName()}.${`$this$perfectName`.getClass().getSimpleName()}";
   }


public fun md5(str: String): ByteArray {
   val var10000: MessageDigest = MessageDigest.getInstance("MD5");
   val var10001: ByteArray = str.getBytes(Charsets.UTF_8);
   val var1: ByteArray = var10000.digest(var10001);
   return var1;
}

public fun ByteArray.toHex(): String {
   return ArraysKt.joinToString$default(`$this$toHex`, "", null, null, 0, null, ReportKt::toHex$lambda$0, 30, null);
}

public fun ByteArray.xor2Int(): Int {
   var `accumulator$iv`: Int = 123;

   for (byte element$iv : $this$xor2Int) {
      `accumulator$iv` = `accumulator$iv` shl 8 xor `element$iv`;
   }

   return `accumulator$iv`;
}

public fun CheckType.bugMessage(lang: Language, env: Env): String {
   val var10000: BugMessage = `$this$bugMessage`.getBugMessage().get(lang);
   if (var10000 != null) {
      val var5: Function1 = var10000.getMsg();
      if (var5 != null) {
         val var6: java.lang.String = var5.invoke(env) as java.lang.String;
         if (var6 != null) {
            return var6;
         }
      }
   }

   return "$lang not exists of checkType: $`$this$bugMessage`";
}

public fun CheckType.bugMessage(env: Env): Map<Language, String> {
   val `$this$associateWith$iv`: Array<Any> = Language.values();
   val `result$iv`: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(`$this$associateWith$iv`.length), 16));

   for (Object element$iv$iv : $this$associateWith$iv) {
      `result$iv`.put(`element$iv$iv`, bugMessage(`$this$bugMessage`, (Language)`element$iv$iv`, env));
   }

   return `result$iv`;
}

public fun <T> Map<Language, T>.preferredMessage(defaultValue: () -> T): T {
   val var2: java.util.Iterator = MainConfig.Companion.getPreferredLanguages().iterator();

   var var10000: Any;
   while (true) {
      if (var2.hasNext()) {
         val var5: Any = `$this$preferredMessage`.get(var2.next() as Language);
         if (var5 == null) {
            continue;
         }

         var10000 = var5;
         break;
      }

      var10000 = null;
      break;
   }

   if (var10000 == null) {
      var10000 = defaultValue.invoke();
   }

   return (T)var10000;
}

fun `toHex$lambda$0`(var0: Byte): java.lang.CharSequence {
   val var2: Array<Any> = new Object[]{var0};
   val var10000: java.lang.String = java.lang.String.format("%02x", Arrays.copyOf(var2, var2.length));
   return var10000;
}
