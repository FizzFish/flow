package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import soot.G
import soot.jimple.IntConstant
import kotlin.math.min

/**
 * StringLatin1 部分 API 建模（精简版）。
 */
class WStringLatin1 : SummaryHandlePackage<IValue> {

   override fun ACheckCallAnalysis.register() {
      // indexOf(byte[], int, int)
      evalCall("<java.lang.StringLatin1: int indexOf(byte[],int,int)>") { eval ->
         TODO("FIXME — 填充 indexOf 建模逻辑")
      }

      // equals(byte[], byte[])
      evalCall("<java.lang.StringLatin1: boolean equals(byte[],byte[])>") { eval ->
         TODO("FIXME — equals 建模")
      }

      // hashCode(byte[])
      evalCall("<java.lang.StringLatin1: int hashCode(byte[])>") { eval ->
         TODO("FIXME — hashCode 建模")
      }

      // newString(byte[], int, int) → 直接走 wrapper
      registerWrapper(
         cn.sast.dataflow.util.SootUtilsKt
            .sootSignatureToRef("<java.lang.StringLatin1: java.lang.String newString(byte[],int,int)>", true)
      )
   }

   companion object {
      fun v() = WStringLatin1()
   }
}
