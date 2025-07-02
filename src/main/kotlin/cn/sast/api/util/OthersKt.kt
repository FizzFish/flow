@file:SourceDebugExtension(["SMAP\nOthers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Others.kt\ncn/sast/api/util/OthersKt\n+ 2 Timing.kt\nkotlin/system/TimingKt\n*L\n1#1,48:1\n17#2,6:49\n*S KotlinDebug\n*F\n+ 1 Others.kt\ncn/sast/api/util/OthersKt\n*L\n36#1:49,6\n*E\n"])

package cn.sast.api.util

import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.baseimpl.MatchUtilsKt
import java.io.InputStream
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass
import soot.SootClass

public final val isDummy: Boolean
   public final get() {
      if (!(`$this$isDummy`.getName() == "dummyMainMethod")) {
         val var10000: SootClass = `$this$isDummy`.getDeclaringClass();
         if (!isDummy(var10000)) {
            return false;
         }
      }

      return true;
   }


public final val isDummy: Boolean
   public final get() {
      val var10000: java.lang.String = `$this$isDummy`.getName();
      return StringsKt.contains(var10000, "dummy", true);
   }


public final val isSyntheticComponent: Boolean
   public final get() {
      val var10000: java.lang.String = `$this$isSyntheticComponent`.getName();
      return StringsKt.contains(var10000, "synthetic", true) || isDummy(`$this$isSyntheticComponent`);
   }


public final val isSyntheticComponent: Boolean
   public final get() {
      val var10000: java.lang.String = `$this$isSyntheticComponent`.getName();
      return StringsKt.contains(var10000, "synthetic", true) || isDummy(`$this$isSyntheticComponent`);
   }


public final val skipPathSensitive: Boolean
   public final get() {
      return isDummy(`$this$skipPathSensitive`) || isSyntheticComponent(`$this$skipPathSensitive`);
   }


public fun KClass<*>.asInputStream(): InputStream {
   val var10000: InputStream = JvmClassMappingKt.getJavaClass(`$this$asInputStream`)
      .getResourceAsStream("/${StringsKt.replace$default(SootUtilsKt.getClassName(`$this$asInputStream`), '.', '/', false, 4, null)}.class");
   return var10000;
}

public fun printMilliseconds(message: String, body: () -> Unit) {
   val `start$iv`: Long = System.currentTimeMillis();
   body.invoke();
   System.out.println("$message: ${System.currentTimeMillis() - `start$iv`} ms");
}

public fun methodSignatureToMatcher(signature: String): IMethodMatch? {
   return if (StringsKt.startsWith$default(signature, "<", false, 2, null) && StringsKt.endsWith$default(signature, ">", false, 2, null))
      MatchUtilsKt.matchSoot(signature)
      else
      (if (StringsKt.contains$default(signature, ":", false, 2, null)) MatchUtilsKt.matchSimpleSig(signature) else null);
}
