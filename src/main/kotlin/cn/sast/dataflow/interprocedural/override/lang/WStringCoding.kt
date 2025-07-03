package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.*
import cn.sast.dataflow.util.SootUtilsKt
import soot.*
import soot.jimple.*
import kotlin.ByteArray

/**
 * Hook for java.lang.StringCoding
 * 大量复杂逻辑已标出 TODO，可按原 Java 逻辑逐段移植。
 */
class WStringCoding(private val vg: IVGlobal) : SummaryHandlePackage<IValue> {

   /* ---------- 常用局部 ----------- */

   private val sizeLocal: Local           = Jimple.v().newLocal("size", G.v().soot_IntType())
   private val newValueExpr: NewArrayExpr =
      Jimple.v().newNewArrayExpr(vg.BYTE_ARRAY_TYPE, sizeLocal)
   private val clzResult                  = "java.lang.StringCoding\$Result"
   private val resultType: RefType        = RefType.v(clzResult)
   private val newExprResult: NewExpr     = Jimple.v().newNewExpr(resultType)
   private val fieldValue: SootField      =
      SootUtilsKt.getOrMakeField(clzResult, "value", vg.BYTE_ARRAY_TYPE)
   private val fieldCoder: SootField      =
      SootUtilsKt.getOrMakeField(clzResult, "coder", G.v().soot_ByteType())

   override fun ACheckCallAnalysis.register() {
      // encode(byte,byte[])
      evalCall("<java.lang.StringCoding: byte[] encode(byte,byte[])>") { eval ->
         TODO("FIXME — 补充 encode 建模逻辑")
      }

      // decode(byte[],int,int)
      evalCall("<java.lang.StringCoding\$Result decode(byte[],int,int)>") { eval ->
         TODO("FIXME — 补充 decode 建模逻辑")
      }
   }

   companion object {
      fun v(vg: IVGlobal) = WStringCoding(vg)
   }
}
