package cn.sast.idfa.analysis

import io.smallrye.metrics.MetricRegistries
import mu.KotlinLogging
import org.eclipse.microprofile.metrics.Gauge
import org.eclipse.microprofile.metrics.MetricRegistry
import org.eclipse.microprofile.metrics.MetricRegistry.Type
import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max
import kotlin.math.roundToLong

/**
 * 轻量封装系统 / JVM 监控数据，并提供格式化工具。
 *
 * **依赖**：`org.eclipse.microprofile.metrics:microprofile-metrics-api`
 * （如项目未使用 MicroProfile，可将 Gauge 换成自定义接口）
 */
class UsefulMetrics {

   /* ------------------------------------------------------------------ */
   /* 基础 Gauge                                                          */
   /* ------------------------------------------------------------------ */

   private val osBean = ManagementFactory.getOperatingSystemMXBean()
           as com.sun.management.OperatingSystemMXBean
   private val runtime = Runtime.getRuntime()

   val registryBASE:   MetricRegistry? = runCatching { MetricRegistries.get(Type.BASE)   }.getOrNull()
   val registryVENDOR: MetricRegistry? = runCatching { MetricRegistries.get(Type.VENDOR) }.getOrNull()

   val jvmMemoryUsed:      Gauge<Long>  = Gauge { runtime.totalMemory() - runtime.freeMemory() }
   val jvmMemoryCommitted: Gauge<Long>  = Gauge { runtime.totalMemory() }
   val jvmMemoryMax:       Gauge<Long>  = Gauge { runtime.maxMemory() }
   val freePhysicalSize:   Gauge<Long>  = Gauge { osBean.freePhysicalMemorySize }
   val cpuSystemCpuLoad:   Gauge<Double> = Gauge { osBean.systemCpuLoad }  // 0.0‒1.0

   /* ------------------------------------------------------------------ */
   /* 阈值 / 长时监控                                                     */
   /* ------------------------------------------------------------------ */

   var warningThreshold: Long  = -1 ; private set
   var memoryThresholdExceededPercentage: Float = -1f ; private set
   var memoryUsed: Long = -1 ;     private set
   var isMemoryThresholdTriggered  = false ; private set
   var isLongTermThresholdTriggered = false ; private set

   private var monitorThread: Thread? = null

   fun reset() {
      monitorThread?.interrupt()
      monitorThread = null
      warningThreshold = -1
      memoryThresholdExceededPercentage = -1f
      memoryUsed = -1
      isMemoryThresholdTriggered = false
      isLongTermThresholdTriggered = false
   }

   /**
    * 设置“低内存”预警阈值（占 maxMemory 百分比）。
    * 连续超过 5 s 视为长时触发。
    */
   fun setWarningThreshold(percentage: Double) {
      if (percentage < 0.01) {
         reset(); return
      }

      val maxMem = jvmMemoryMax.value
      warningThreshold = (maxMem * percentage).roundToLong()
      val reachedTime = AtomicLong(-1)

      monitorThread = Thread({
         while (!Thread.currentThread().isInterrupted) {
            memoryUsed = jvmMemoryUsed.value
            isMemoryThresholdTriggered = memoryUsed >= warningThreshold
            memoryThresholdExceededPercentage =
               if (isMemoryThresholdTriggered)
                  ((memoryUsed - warningThreshold).toFloat() / max(1, (maxMem - warningThreshold))).coerceAtLeast(0f)
               else -1f

            if (isMemoryThresholdTriggered) {
               if (reachedTime.compareAndExchange(-1, System.nanoTime()) != -1L) {
                  isLongTermThresholdTriggered =
                     System.nanoTime() - reachedTime.get() > 5_000_000_000L
               }
            } else {
               reachedTime.set(-1)
               isLongTermThresholdTriggered = false
            }

            val sleepMs = if (isMemoryThresholdTriggered) 300L
            else ((warningThreshold - memoryUsed).toDouble() / warningThreshold * 1000).roundToLong().coerceAtLeast(300)

            try {
               Thread.sleep(sleepMs)
            } catch (_: InterruptedException) {
               break
            }
         }
      }, "Low-memory-monitor").apply {
         isDaemon = true
         priority = Thread.MIN_PRIORITY
         start()
      }
   }

   /* ------------------------------------------------------------------ */
   /* 格式化工具                                                          */
   /* ------------------------------------------------------------------ */

   /** 将 Gauge (bytes) → Long?，若值无效则返回 null。 */
   fun getMemSize(gauge: Gauge<Long>?): Long? = gauge?.value?.takeIf { it >= 0 }

   /** bytes → “123.4” (GB) */
   fun getMemFmt(bytes: Long): String =
      String.format("%.1f", bytes.toDouble() / (1024.0 * 1024 * 1024))

   /** Gauge(bytes) → “123.4” or “?” */
   fun getMemFmt(gauge: Gauge<Long>?): String =
      getMemSize(gauge)?.let { getMemFmt(it) } ?: "?"

   /** Gauge(load 0‒1) → “ 42%” or “?” */
   fun getLoadFmt(gauge: Gauge<Double>?): String =
      gauge?.value?.takeIf { it >= 0 }?.let { String.format("%3.0f%%", it * 100) } ?: "?"

   /* ------------------------------------------------------------------ */
   /* 单例                                                                */
   /* ------------------------------------------------------------------ */

   companion object {
      private val logger = KotlinLogging.logger {}
      val metrics: UsefulMetrics by lazy { UsefulMetrics() }
   }
}
