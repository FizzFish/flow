package cn.sast.api.util

import java.util.Arrays
import java.util.function.Supplier
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger

public class SimpleTimer {
   public final var elapsedTime: Long
      private set

   public final var start: Long
      private set

   private final var startTime: Long

   public final var end: Long
      private set

   public fun start() {
      this.startTime = PhaseIntervalTimerKt.currentNanoTime();
      if (this.start == 0L) {
         this.start = this.startTime;
      }
   }

   public fun stop() {
      val cur: Long = PhaseIntervalTimerKt.currentNanoTime();
      this.elapsedTime = this.elapsedTime + (cur - this.startTime);
      this.startTime = 0L;
      this.end = cur;
   }

   public fun currentElapsedTime(): Long {
      return this.elapsedTime + (PhaseIntervalTimerKt.currentNanoTime() - this.startTime);
   }

   public fun inSecond(): Float {
      return (float)this.elapsedTime / 1000.0F;
   }

   public fun clear() {
      this.elapsedTime = 0L;
   }

   public override fun toString(): String {
      val var2: Array<Any> = new Object[]{this.inSecond()};
      val var10000: java.lang.String = java.lang.String.format("elapsed time: %.2fs", Arrays.copyOf(var2, var2.length));
      return var10000;
   }

   public companion object {
      private final val logger: Logger

      public fun <T> runAndCount(task: Supplier<T>, taskName: String, level: Level?): T {
         SimpleTimer.access$getLogger$cp().info("{} starts ...", taskName);
         val timer: SimpleTimer = new SimpleTimer();
         timer.start();
         val result: Any = task.get();
         timer.stop();
         val var10000: Logger = SimpleTimer.access$getLogger$cp();
         val var7: Array<Any> = new Object[]{timer.inSecond()};
         val var10004: java.lang.String = java.lang.String.format("%.2fs", Arrays.copyOf(var7, var7.length));
         var10000.log(level, "{} finishes, elapsed time: {}", taskName, var10004);
         return (T)result;
      }

      @JvmOverloads
      public fun runAndCount(task: Runnable, taskName: String, level: Level? = Level.INFO) {
         this.runAndCount(new Supplier(task) {
            {
               this.$task = `$task`;
            }

            @Override
            public final Object get() {
               this.$task.run();
               return null;
            }
         }, taskName, level);
      }

      @JvmOverloads
      fun runAndCount(task: Runnable, taskName: java.lang.String) {
         runAndCount$default(this, task, taskName, null, 4, null);
      }
   }
}
