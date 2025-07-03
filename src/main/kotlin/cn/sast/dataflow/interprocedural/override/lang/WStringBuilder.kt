package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.util.SootUtilsKt
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import soot.*

/**
 * Hook for java.lang.AbstractStringBuilder & StringBuilder
 * 只保留注册流程，内部逻辑需按 TODO 填充。
 */
class WStringBuilder : SummaryHandlePackage<IValue> {

   override fun ACheckCallAnalysis.register() {
      /* TODO(FIXME):
       *  1. 使用 UtilsKt.getSootSignature(::appendInt) 等方式拿到签名
       *  2. 按业务调用 evalCall / evalCallAtCaller
       *  3. 在回调里补充真正的建模逻辑
       */

      // 示例：包装类字段 value: byte[]
      val byteArrayTy: Type = ArrayType.v(G.v().soot_ByteType(), 1)
      val valueField: SootField = SootUtilsKt.getOrMakeField(
         "java.lang.AbstractStringBuilder", "value", byteArrayTy
      )
      valueField // keep reference, avoid unused warning
   }

   companion object {
      fun v() = WStringBuilder()
   }
}
