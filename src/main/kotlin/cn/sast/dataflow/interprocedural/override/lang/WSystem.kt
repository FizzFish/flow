package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import com.feysh.corax.config.api.utils.UtilsKt
import soot.*
import kotlin.reflect.KCallable

/**
 * Hook for java.lang.System.arraycopy / identityHashCode
 */
class WSystem : SummaryHandlePackage<IValue> {

   private val byteArrayType: ArrayType =
      ArrayType.v(G.v().soot_ByteType(), 1)

   override fun ACheckCallAnalysis.register() {
      // identityHashCode(Object)
      val idSig = UtilsKt.getSootSignature(
         (System::identityHashCode as KCallable<*>)
      )
      evalCall(idSig) { eval -> handleIdentityHash(eval) }

      // arraycopy(...)
      val copySig =
         "<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>"
      evalCall(copySig) { eval -> handleArrayCopy(eval) }
   }

   /* ---------- identityHashCode ---------- */

   private fun handleIdentityHash(eval: CalleeCBImpl.EvalCall) {
      val op = eval.hf.resolveOp(eval.env, eval.arg(0))
      op.resolve { _, res, (obj) ->
         res.add(
            eval.hf.push(eval.env, eval.hf.toConstVal(obj.hashCode()))
               .markOfReturnValueOfMethod(eval)
               .pop()
         )
         true
      }
      op.putSummaryIfNotConcrete(G.v().soot_IntType(), "return")
      eval.setReturn(op.res.build())
   }

   /* ---------- arraycopy ---------- */

   private fun handleArrayCopy(eval: CalleeCBImpl.EvalCall) {
      // 为简洁起见，仅处理 “byte[] → byte[]” 且 len < 20 的情况
      val (src, srcPosOp, dest, destPosOp, lenOp) =
         (0..4).map { eval.arg(it) }

      // 必须都是单值
      if (listOf(src, srcPosOp, dest, destPosOp, lenOp).any { !it.isSingle() }) {
         eval.isEvalAble = false; return
      }

      val op = eval.hf.resolveOp(eval.env, src, srcPosOp, dest, destPosOp, lenOp)
      op.resolve { _, _, (srcCp, srcPosCp, dstCp, dstPosCp, lenCp) ->
         val srcArr = eval.out.getArray(srcCp.value) ?: return@resolve false
         val dstArr = eval.out.getArray(dstCp.value) ?: return@resolve false
         val srcPos = FactValuesKt.getIntValue(srcPosCp.value as IValue, true) ?: return@resolve false
         val dstPos = FactValuesKt.getIntValue(dstPosCp.value as IValue, true) ?: return@resolve false
         val len    = FactValuesKt.getIntValue(lenCp.value as IValue, true) ?: return@resolve false
         if (len >= 20) return@resolve false

         val dstBuilder = dstArr.builder()
         repeat(len) { i ->
            val elem = srcArr.get(eval.hf, srcPos + i) ?: return@resolve false
            dstBuilder.set(eval.hf, eval.env, dstPos + i, elem, append = true)
         }
         eval.out.setValueData(eval.env, dstCp.value, BuiltInModelT.Array, dstBuilder.build())
         true
      }
      // 无返回值
   }

   companion object { fun v() = WSystem() }
}
