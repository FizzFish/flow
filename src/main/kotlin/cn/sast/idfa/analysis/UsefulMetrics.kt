package cn.sast.idfa.analysis

import cn.sast.api.util.PhaseIntervalTimerKt
import io.quarkus.runtime.ShutdownContext
import io.quarkus.runtime.StartupContext
import io.quarkus.smallrye.metrics.runtime.SmallRyeMetricsRecorder
import io.smallrye.metrics.MetricRegistries
import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
import java.lang.management.MemoryType
import java.lang.management.MemoryUsage
import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.atomic.AtomicLong
import java.util.function.LongUnaryOperator
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.eclipse.microprofile.metrics.Gauge
import org.eclipse.microprofile.metrics.Metric
import org.eclipse.microprofile.metrics.MetricID
import org.eclipse.microprofile.metrics.MetricRegistry
import org.eclipse.microprofile.metrics.MetricRegistry.Type
import soot.jimple.infoflow.util.ThreadUtils

@SourceDebugExtension(["SMAP\nUsefulMetrics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UsefulMetrics.kt\ncn/sast/idfa/analysis/UsefulMetrics\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,207:1\n1#2:208\n*E\n"])
public class UsefulMetrics {
   public final val registryBASE: MetricRegistry? = MetricRegistries.get(Type.BASE)
   public final val registryVENDOR: MetricRegistry? = MetricRegistries.get(Type.VENDOR)
   public final val tenuredGenPool: MemoryPoolMXBean? = this.findTenuredGenPool()
   public final val jvmMemoryUsed: Gauge<Long>?
   public final val jvmMemoryCommitted: Gauge<Long>?
   public final val jvmMemoryMax: Gauge<Long>?
   public final val freePhysicalSize: Gauge<Long>?
   public final val cpuSystemCpuLoad: Gauge<Double>?

   public open val memSize: Long?
      public open get() {
         if (`$this$memSize` == null) {
            return null;
         } else {
            val var2: Any = `$this$memSize`.getValue();
            return (if (var2 as java.lang.Long >= 0L) var2 else null) as java.lang.Long;
         }
      }


   public open val memFmt: String
      public open get() {
         val var4: Array<Any> = new Object[]{(double)`$this$memFmt` / 1024.0 / (double)1024 / (double)1024};
         val var10000: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var4, var4.length));
         return var10000;
      }


   public open val memFmt: String
      public open get() {
         val var10000: java.lang.Long = this.getMemSize(`$this$memFmt`);
         if (var10000 != null) {
            val var2: java.lang.String = this.getMemFmt(var10000);
            if (var2 != null) {
               return var2;
            }
         }

         return "?";
      }


   public open val loadFmt: String
      public open get() {
         if (`$this$loadFmt` == null) {
            return "?";
         } else {
            val var4: Any = `$this$loadFmt`.getValue();
            val var10000: java.lang.Double = (if (var4 as java.lang.Double >= 0.0) var4 else null) as java.lang.Double;
            if (var10000 != null) {
               val var8: Array<Any> = new Object[]{var10000 * (double)100};
               val var9: java.lang.String = java.lang.String.format("%3.0f%%", Arrays.copyOf(var8, var8.length));
               return var9;
            } else {
               return "?";
            }
         }
      }


   public final var warningThreshold: Long
      private set

   public final var memoryThresholdExceededPercentage: Float
      private set

   public final var memoryUsed: Long
      private set

   public final var isMemoryThresholdTriggered: Boolean
      private set

   public final var isLongTermThresholdTriggered: Boolean
      private set

   private final var thread: Thread?

   public fun MetricRegistry.getMetricAndTestGauge(metricID: MetricID): Metric? {
      val metric: Metric = `$this$getMetricAndTestGauge`.getMetric(metricID);
      if (metric != null && metric is Gauge) {
         try {
            (metric as Gauge).getValue();
            return metric;
         } catch (var5: Error) {
            logger.error("Accessing Metric $metricID throw an Error:", var5);
            return null;
         } catch (var6: Exception) {
            logger.error("Accessing Metric $metricID throw an Exception:", var6);
            return null;
         }
      } else {
         return null;
      }
   }

   public fun findTenuredGenPool(): MemoryPoolMXBean? {
      val usablePools: java.util.List = new ArrayList();

      for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
         if (pool.getType() === MemoryType.HEAP && pool.isUsageThresholdSupported()) {
            if (pool.getName() == "Tenured Gen") {
               usablePools.add(0, pool);
            } else {
               usablePools.add(pool);
            }
         }
      }

      if (!usablePools.isEmpty()) {
         for (MemoryPoolMXBean poolx : usablePools) {
            try {
               val it: MemoryUsage = poolx.getUsage();
               it.getInit();
               it.getUsed();
               it.getCommitted();
               it.getMax();
               return poolx;
            } catch (var7: Error) {
               logger.error("Getting MemoryUsage from ${poolx.getName()} throw an Error", var7);
            } catch (var8: Exception) {
               logger.error("Getting MemoryUsage from ${poolx.getName()} throw an Exception", var8);
            }
         }
      }

      logger.error("Could not find tenured space");
      return null;
   }

   public fun reset() {
      if (this.thread != null) {
         val it: Thread = this.thread;
         this.thread = null;
         if (!it.isInterrupted()) {
            it.interrupt();
         }
      }

      this.warningThreshold = -1L;
      this.memoryThresholdExceededPercentage = -1.0F;
      this.memoryUsed = -1L;
      this.isMemoryThresholdTriggered = false;
      this.isLongTermThresholdTriggered = false;
   }

   public fun setWarningThreshold(percentage: Double) {
      if (this.thread != null || percentage < 0.01) {
         this.reset();
      } else if (this.jvmMemoryUsed != null) {
         val jvmMemoryUsed: Gauge = this.jvmMemoryUsed;
         if (this.jvmMemoryMax != null) {
            val jvmMemoryMax: Gauge = this.jvmMemoryMax;
            val maxMemory: java.lang.Long = jvmMemoryMax.getValue() as java.lang.Long;
            this.warningThreshold = (long)(maxMemory.longValue() * percentage);
            val memoryThresholdReachedPrevNanoTime: AtomicLong = new AtomicLong(-1L);
            val var7: Thread = ThreadUtils.createGenericThread(
               new Runnable(jvmMemoryUsed, this, maxMemory, memoryThresholdReachedPrevNanoTime) {
                  {
                     this.$jvmMemoryUsed = `$jvmMemoryUsed`;
                     this.this$0 = `$receiver`;
                     this.$maxMemory = `$maxMemory`;
                     this.$memoryThresholdReachedPrevNanoTime = `$memoryThresholdReachedPrevNanoTime`;
                  }

                  @Override
                  public final void run() {
                     while (true) {
                        val usedMemory: java.lang.Long = this.$jvmMemoryUsed.getValue() as java.lang.Long;
                        val warningThreshold: Long = this.this$0.getWarningThreshold();
                        UsefulMetrics.access$setMemoryUsed$p(this.this$0, usedMemory);
                        if (warningThreshold > 0L) {
                           UsefulMetrics.access$setMemoryThresholdTriggered$p(this.this$0, usedMemory >= warningThreshold);
                           UsefulMetrics.access$setMemoryThresholdExceededPercentage$p(
                              this.this$0, (float)((double)(usedMemory - warningThreshold) / (double)(this.$maxMemory - warningThreshold))
                           );
                           val missing: Long = this.$memoryThresholdReachedPrevNanoTime.updateAndGet(new LongUnaryOperator(this.this$0) {
                              {
                                 this.this$0 = `$receiver`;
                              }

                              @Override
                              public final long applyAsLong(long it) {
                                 return if (this.this$0.isMemoryThresholdTriggered()) (if (it > 0L) it else PhaseIntervalTimerKt.currentNanoTime()) else -1L;
                              }
                           });
                           UsefulMetrics.access$setLongTermThresholdTriggered$p(
                              this.this$0, missing > 0L && PhaseIntervalTimerKt.currentNanoTime() - missing > 5000L
                           );
                        }

                        val var9: Long = warningThreshold - usedMemory;
                        if (var9 <= 0L) {
                           Thread.sleep(300L);
                        } else {
                           try {
                              Thread.sleep((long)((double)var9 / (double)warningThreshold * (double)1000));
                           } catch (var8: InterruptedException) {
                           }
                        }
                     }
                  }
               },
               "Low memory monitor",
               true
            );
            var7.setPriority(1);
            var7.start();
            this.thread = var7;
         }
      }
   }

   @JvmStatic
   fun `logger$lambda$8`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun {
      val recorder: SmallRyeMetricsRecorder = new SmallRyeMetricsRecorder();
      val var10000: Any = new StartupContext().getValue("io.quarkus.runtime.ShutdownContext");
      recorder.registerMicrometerJvmMetrics(var10000 as ShutdownContext);
      recorder.registerBaseMetrics();
      recorder.registerVendorMetrics();
   }

   public companion object {
      private final val logger: KLogger
      public final val metrics: UsefulMetrics
   }
}
