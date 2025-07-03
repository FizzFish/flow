package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import com.feysh.corax.config.api.utils.UtilsKt
import mu.KotlinLogging
import kotlin.reflect.KCallable
import soot.*

/**
 * `java.lang.Object` 相关 hook：`getClass`/`equals`/`hashCode`
 */
class WObject : SummaryHandlePackage<IValue> {

   private val logger = KotlinLogging.logger {}
   private val jClassType = RefType.v("java.lang.Class")

   override fun ACheckCallAnalysis.register() {
      val getClassSig  = UtilsKt.getSootSignature((Any::class.java::getClass as KCallable<*>))
      val equalsSig    = UtilsKt.getSootSignature((Any::equals as KCallable<*>))
      val hashCodeSig  = UtilsKt.getSootSignature((Any::hashCode as KCallable<*>))

      // getClass()
      evalCall(getClassSig) { eval -> handleGetClass(eval) }

      // equals(Object)
      evalCall(equalsSig) { eval -> handleEquals(eval) }

      // hashCode()
      evalCall(hashCodeSig) { eval -> handleHashCode(eval) }
   }

   /* ---------- getClass ---------- */

   private fun handleGetClass(eval: EvalCall) {
      val op = eval.hf.resolveOp(eval.env, eval.arg(-1))
      op.resolve { _, res, (self) ->
         if (!self.value.typeIsConcrete()) return@resolve false
         val cc = ClassConstant.fromType(self.value.type)
         res.add(
            eval.hf.push(eval.env, eval.hf.newConstVal(cc, jClassType))
               .markOfGetClass(self).pop()
         )
         true
      }
      op.putSummaryIfNotConcrete(jClassType, "return")
      eval.setReturn(op.res.build())
   }

   /* ---------- equals ---------- */

   private fun handleEquals(eval: EvalCall) {
      val calc = eval.hf.resolveOp(eval.env, eval.arg(-1), eval.arg(0))
      calc.resolve { _, res, (thiz, that) ->
         val result = if ((that.value as IValue).isNullConstant())
            false else (thiz.value == that.value)
         res.add(
            eval.hf.push(eval.env, eval.hf.toConstVal(result))
               .markOfObjectEqualsResult(thiz, that)
               .pop()
         )
         true
      }
      eval.setReturn(calc.res.build())
   }

   /* ---------- hashCode ---------- */

   private fun handleHashCode(eval: EvalCall) {
      val op = eval.hf.resolveOp(eval.env, eval.arg(0))
      op.resolve { _, res, (self) ->
         res.add(
            eval.hf.push(eval.env, eval.hf.toConstVal(self.hashCode()))
               .markOfReturnValueOfMethod(eval)
               .pop()
         )
         true
      }
      op.putSummaryIfNotConcrete(G.v().soot_IntType(), "return")
      eval.setReturn(op.res.build())
   }

   companion object { fun v() = WObject() }
}
