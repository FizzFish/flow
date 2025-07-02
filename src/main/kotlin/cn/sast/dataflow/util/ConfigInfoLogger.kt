package cn.sast.dataflow.util

import com.feysh.corax.config.api.AIAnalysisApi
import mu.KLogger
import mu.KotlinLogging

/**
 * 收集配置阶段的 error / warning 并打日志。
 */
class ConfigInfoLogger : AIAnalysisApi.Error {

   val errors: MutableList<String>   = mutableListOf()
   val warnings: MutableList<String> = mutableListOf()

   override val logger: KLogger = KotlinLogging.logger {}

   /* ---------- AIAnalysisApi.Error ---------- */

   override fun error(msg: String) {
      logger.error { msg }
      errors += msg
   }

   override fun warning(msg: String) {
      logger.warn  { msg }
      warnings += msg
   }
}
