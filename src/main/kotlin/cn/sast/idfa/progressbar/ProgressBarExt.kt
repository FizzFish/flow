package cn.sast.idfa.progressbar

import cn.sast.idfa.analysis.ProcessInfoView
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicLong
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarRenderer
import me.tongfei.progressbar.ProgressBarStyle
import me.tongfei.progressbar.ProgressState

public class ProgressBarExt(updateIntervalMillis: Int = 500, maxProgressLength: Int = 120) {
   public final var updateIntervalMillis: Int
      internal set

   public final var maxProgressLength: Int
      internal set

   init {
      this.updateIntervalMillis = updateIntervalMillis;
      this.maxProgressLength = maxProgressLength;
   }

   public fun getProgressBar(
      unitName: String,
      initialMax: Long,
      style: ProgressBarStyle = ProgressBarStyle.COLORFUL_UNICODE_BLOCK,
      builder: (ProgressBarBuilder) -> Unit = ProgressBarExt::getProgressBar$lambda$0
   ): ProgressBar {
      val var7: ProgressBarBuilder = new ProgressBarBuilder()
         .setTaskName(">")
         .showSpeed(new DecimalFormat("#.##"))
         .setStyle(style)
         .showSpeed()
         .setInitialMax(initialMax)
         .setUnit(unitName, 1L)
         .setSpeedUnit(ChronoUnit.SECONDS)
         .setMaxRenderedLength(this.maxProgressLength)
         .continuousUpdate()
         .setUpdateIntervalMillis(this.updateIntervalMillis);
      builder.invoke(var7);
      val var10000: ProgressBar = var7.build();
      return var10000;
   }

   @JvmStatic
   fun `getProgressBar$lambda$0`(var0: ProgressBarBuilder): Unit {
      return Unit.INSTANCE;
   }

   fun ProgressBarExt() {
      this(0, 0, 3, null);
   }

   public open class DefaultProcessInfoRenderer(progressBar: ProgressBar, render: ProgressBarRenderer) : ProcessInfoView(null, 1), ProgressBarRenderer {
      public final val progressBar: ProgressBar
      private final val render: ProgressBarRenderer

      public final var splitLines: Long
         internal set

      public final val counter: AtomicLong

      public open val extraMessage: String
         public open get() {
            return " ${this.getJvmMemoryUsageText()} ${this.getFreeMemoryText()} ${this.getCpuLoadText()}";
         }


      init {
         this.progressBar = progressBar;
         this.render = render;
         this.counter = new AtomicLong(-1L);
      }

      public open fun render(progressState: ProgressState, maxLength: Int): String {
         val var10000: java.lang.String;
         synchronized (this.progressBar) {
            this.progressBar.setExtraMessage("${this.getExtraMessage()}${this.progressBar.getExtraMessage()}");
            val var5: java.lang.String = this.render.render(progressState, maxLength);
            this.progressBar.setExtraMessage("");
            var10000 = var5;
         }

         return var10000;
      }

      public fun step() {
         if (this.splitLines <= 0L) {
            this.progressBar.step();
         } else {
            val progressCount: Long = this.progressBar.getMax() / this.splitLines;
            val curCount: Long = this.counter.incrementAndGet();
            var needRefresh: Boolean = false;
            synchronized (this.progressBar) {
               this.progressBar.step();
               if (progressCount != 0L && curCount % progressCount == 0L || curCount == this.progressBar.getMax()) {
                  this.progressBar.setExtraMessage("\n");
                  needRefresh = true;
               }
            }

            if (needRefresh) {
               this.progressBar.refresh();
            }
         }
      }

      public fun refresh() {
         this.progressBar.refresh();
      }

      public fun close() {
         this.progressBar.pause();
         this.progressBar.close();
      }
   }
}
