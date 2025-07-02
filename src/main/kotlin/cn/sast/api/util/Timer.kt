package cn.sast.api.util

import java.util.Arrays

public class Timer(name: String) : PhaseIntervalTimer {
   public final val name: String

   public open val prefix: String
      public open get() {
         return "timer: ${this.name} ";
      }


   init {
      this.name = name;
   }

   public override fun toString(): String {
      val var2: Array<Any> = new Object[]{this.name, PhaseIntervalTimerKt.nanoTimeInSeconds$default(this.getElapsedTime(), 0, 1, null)};
      val var10000: java.lang.String = java.lang.String.format("[%s] elapsed time: %.2fs", Arrays.copyOf(var2, var2.length));
      return var10000;
   }
}
