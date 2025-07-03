package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import mu.KotlinLogging
import kotlin.math.*

/**
 * `java.lang.Math` Hook 示例（暂为空实现，预留扩展点）。
 */
class WMath : SummaryHandlePackage<IValue> {

   private val logger = KotlinLogging.logger {}

   override fun ACheckCallAnalysis.register() {
      // 👉 这里可以按需添加 Math.pow / abs / sin 等 Hook
      logger.debug { "WMath registered (no-op for now)" }
   }

   companion object { fun v() = WMath() }
}
