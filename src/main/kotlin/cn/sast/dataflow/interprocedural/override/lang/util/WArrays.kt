package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import java.util.Arrays
import soot.BooleanType
import soot.G

/**
 * Hook `java.util.Arrays.*`
 */
class WArrays : SummaryHandlePackage<IValue> {

   override fun ACheckCallAnalysis.register() {
      evalCall("<java.util.Arrays: boolean equals(byte[],byte[])>") { eval ->
         val a = eval.arg(0)
         val b = eval.arg(1)
         val calc = eval.hf.resolveOp(eval.env, a, b)
         calc.resolve { _, res, (arrA, arrB) ->
            val kvA = eval.out.getArray(arrA.value) ?: return@resolve false
            val kvB = eval.out.getArray(arrB.value) ?: return@resolve false
            kvA.getByteArray(eval.hf)?.let { ba ->
               res.add(eval.hf.single(eval.hf.toConstVal(Arrays.equals(ba, kvB.getByteArray(eval.hf)))))
               true
            } ?: false
         }
         calc.putSummaryIfNotConcrete(G.v().soot_BooleanType() as BooleanType, "return")
         eval.out.assignNewExpr(eval.env, eval.vg.RETURN_LOCAL, calc.res.build(), false)
      }

      // copyOfRange 直接走 wrapper 注册
      registerWrapper("<java.util.Arrays: byte[] copyOfRange(byte[],int,int)>", isStatic = true)
   }

   companion object { fun v() = WArrays() }
}
