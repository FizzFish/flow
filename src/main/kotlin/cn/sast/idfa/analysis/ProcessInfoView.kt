package cn.sast.idfa.analysis

import org.eclipse.microprofile.metrics.Gauge
import java.util.concurrent.atomic.AtomicLong
import java.util.function.LongUnaryOperator

/**
 * 将 [UsefulMetrics] 的监控数据封装为易读文本。
 */
open class ProcessInfoView(
   val metrics: UsefulMetrics = UsefulMetrics.metrics,
) {

   /** 运行过程中观测到的 JVM 堆使用峰值（字节）。 */
   private val maxUsedMemory = AtomicLong(0)

   /* ------------------------------------------------------------------ */
   /* 内部工具                                                            */
   /* ------------------------------------------------------------------ */

   private fun memFmt(bytes: Long): String = metrics.getMemFmt(bytes)

   /** 从 metrics 里取出最新的 JVM  used 值，并更新 [maxUsedMemory]。 */
   fun updateStat() {
      val used = metrics.jvmMemoryUsed?.value ?: return
      maxUsedMemory.updateAndGet(LongUnaryOperator { prev -> if (prev < used) used else prev })
   }

   /* ------------------------------------------------------------------ */
   /* 各种文本片段                                                         */
   /* ------------------------------------------------------------------ */

   val jvmMemoryUsedText: String
      get() {
         updateStat()
         val base     = metrics.getMemFmt(metrics.jvmMemoryUsed)
         val longTerm = if (metrics.isLongTermThresholdTriggered) "(JVM mem)" else ""
         val mark     = if (metrics.isMemoryThresholdTriggered) "!" else ""
         return "$base$longTerm$mark"
      }

   val maxUsedMemoryText: String
      get() = memFmt(maxUsedMemory.get())

   val jvmMemoryCommittedText: String
      get() = metrics.getMemFmt(metrics.jvmMemoryCommitted)

   val jvmMemoryMaxText: String
      get() = "${metrics.getMemFmt(metrics.jvmMemoryMax)}G"

   /** e.g. `123.4/456.7/789.0/2048.0` */
   val jvmMemoryUsageText: String
      get() = "$jvmMemoryUsedText/$maxUsedMemoryText/$jvmMemoryCommittedText/$jvmMemoryMaxText"

   val freeMemoryText: String
      get() {
         val phy  = metrics.getMemSize(metrics.freePhysicalSize)
         val txt  = phy?.let { memFmt(it) } ?: "?"
         val warn = if (phy != null && phy < 800L * 1024 * 1024) "(low memory warning!)" else ""
         return "free:${txt}G$warn"
      }

   val cpuLoadText: String
      get() = "cpu:${metrics.getLoadFmt(metrics.cpuSystemCpuLoad)}"

   /** 汇总信息，供进度条额外字段使用。 */
   val processInfoText: String
      get() = "$jvmMemoryUsageText $freeMemoryText"

   companion object {
      /** 全局共享实例（可按需直接引用避免重复创建）。 */
      val globalProcessInfo: ProcessInfoView = ProcessInfoView()
   }
}
