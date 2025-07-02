package cn.sast.common

import java.util.Timer
import java.util.TimerTask

public open class CustomRepeatingTimer(interval: Long, action: () -> Unit) {
   private final val interval: Long
   private final val action: () -> Unit
   private final var timer: Timer?

   public final var isRepeats: Boolean
      internal set

   init {
      this.interval = interval;
      this.action = action;
      this.isRepeats = true;
   }

   public fun start() {
      val var1: Timer = new Timer();
      var1.scheduleAtFixedRate(new TimerTask(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public void run() {
            CustomRepeatingTimer.access$getAction$p(this.this$0).invoke();
            if (!this.this$0.isRepeats()) {
               this.cancel();
            }
         }
      }, this.interval, this.interval);
      this.timer = var1;
   }

   public open fun stop() {
      if (this.timer != null) {
         val it: Timer = this.timer;
         this.timer.cancel();
         it.purge();
         this.timer = null;
      }
   }
}
