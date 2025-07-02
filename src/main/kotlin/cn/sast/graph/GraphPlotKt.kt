package cn.sast.graph

import cn.sast.common.IResFile
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import soot.util.dot.DotGraph

public fun DotGraph.dump(output: IResFile) {
   label19: {
      val var10000: Path = output.getPath();
      val var10001: Array<OpenOption> = new OpenOption[0];
      val var11: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var10001, var10001.length));
      val var2: Closeable = var11;
      var var3: java.lang.Throwable = null;

      try {
         try {
            `$this$dump`.render(var2 as OutputStream, 0);
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
