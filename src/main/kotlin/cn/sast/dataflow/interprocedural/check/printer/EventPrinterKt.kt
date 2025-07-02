package cn.sast.dataflow.interprocedural.check.printer

import com.feysh.corax.config.api.utils.UtilsKt

internal final val pname: String
   internal final get() {
      var var10000: java.lang.String = UtilsKt.getTypename(`$this$pname`);
      if (var10000 == null) {
         var10000 = `$this$pname`.toString();
         return var10000;
      } else {
         return StringsKt.removePrefix(var10000, "java.lang.");
      }
   }

