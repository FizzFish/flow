package cn.sast.framework

import java.lang.reflect.Field
import java.util.LinkedList
import soot.Scene

private final val excludedPackages: LinkedList<String>
   private final get() {
      val field: Field = `$this$excludedPackages`.getClass().getDeclaredField("excludedPackages");
      field.setAccessible(true);
      val var10000: Any = field.get(`$this$excludedPackages`);
      return var10000 as LinkedList<java.lang.String>;
   }


public final val sootCtx: SootCtx
   public final get() {
      return new SootCtx(`$this$sootCtx`);
   }


@JvmSynthetic
fun `access$getExcludedPackages`(`$receiver`: Scene): LinkedList {
   return getExcludedPackages(`$receiver`);
}
