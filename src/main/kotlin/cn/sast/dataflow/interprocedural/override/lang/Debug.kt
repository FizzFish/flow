package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl.EvalCall
import com.feysh.corax.config.api.utils.UtilsKt
import mu.KotlinLogging
import kotlin.reflect.KCallable

/**
 * 简单 Debug hook：打印 `break()` 与 `print(any)`。
 */
class Debug : SummaryHandlePackage<IValue> {

   private val logger = KotlinLogging.logger {}

   override fun ACheckCallAnalysis.register() {
      // 函数引用
      val breakSig = UtilsKt.getSootSignature((::breakPoint as KCallable<*>))
      val printSig = UtilsKt.getSootSignature((::printValue as KCallable<*>))

      // break()
      evalCallAtCaller(breakSig) { eval ->
         logger.debug { "debug break" }
      }

      // print(Object)
      evalCallAtCaller(printSig) { eval ->
         logger.debug { "debug print(${eval.arg(0)})" }
      }
   }

   /* ---------- 实际业务方法（仅签名占位） ---------- */

   @Suppress("UNUSED_PARAMETER")
   fun breakPoint() = Unit

   @Suppress("UNUSED_PARAMETER")
   fun printValue(any: Any?) = Unit

   companion object { fun v() = Debug() }
}
