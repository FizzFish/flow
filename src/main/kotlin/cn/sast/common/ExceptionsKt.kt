package cn.sast.common

import java.io.IOException

public fun Throwable.checkCritical() {
   if (`$this$checkCritical` is IOException) {
      val var10000: java.lang.String = `$this$checkCritical`.getMessage();
      if (var10000 != null && StringsKt.contains(var10000, "no space left", true)) {
         `$this$checkCritical`.printStackTrace(System.err);
         System.exit(2);
         throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
      }
   } else if (`$this$checkCritical` is OutOfMemoryError) {
      `$this$checkCritical`.printStackTrace(System.err);
      System.exit(10);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }
}
