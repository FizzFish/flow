package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.util.SootUtilsKt
import java.util.Arrays
import soot.BooleanType
import soot.G
import soot.Type

public class WArrays : SummaryHandlePackage<IValue> {
    public override fun ACheckCallAnalysis.register() {
        this.evalCall("<java.util.Arrays: boolean equals(byte[],byte[])>", WArrays::register$lambda$1)
        this.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.util.Arrays: byte[] copyOfRange(byte[],int,int)>", true))
    }

    @JvmStatic
    fun register$lambda$1$contentEqual(
        evalCall: CalleeCBImpl.EvalCall,
        arrayA: IArrayHeapKV<Int, IValue>,
        arrayB: IArrayHeapKV<Int, IValue>
    ): Boolean? {
        val var10000 = arrayA.getByteArray(evalCall.hf)
        if (var10000 == null) {
            return null
        } else {
            val var5 = arrayB.getByteArray(evalCall.hf)
            return if (var5 == null) null else Arrays.equals(var10000, var5)
        }
    }

    @JvmStatic
    fun register$lambda$1$lambda$0(
        evalCall: CalleeCBImpl.EvalCall,
        resolve: IOpCalculator,
        res: IHeapValues.Builder,
        var3: Array<CompanionV>
    ): Boolean {
        val ca1 = var3[0]
        val ca2 = var3[1]
        val a1 = ca1.value as IValue
        val a2 = ca2.value as IValue
        if (!a1.isNullConstant() && !a2.isNullConstant()) {
            val arrayA1 = evalCall.out.getArray(a1)
            val arrayA2 = evalCall.out.getArray(a2)
            if (arrayA1 != null && arrayA2 != null) {
                val var10000 = register$lambda$1$contentEqual(evalCall, arrayA1, arrayA2)
                if (var10000 != null) {
                    res.add(
                        evalCall.hf
                            .push(evalCall.env, evalCall.hf.toConstVal(var10000))
                            .markOfArrayContentEqualsBoolResult()
                            .pop()
                    )
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } else {
            res.add(
                evalCall.hf.push(evalCall.env, evalCall.hf.toConstVal(false)).markOfArrayContentEqualsBoolResult().pop()
            )
            return true
        }
    }

    @JvmStatic
    fun CalleeCBImpl.EvalCall.register$lambda$1() {
        val binop = this.hf.resolveOp(this.env, this.arg(0), this.arg(1))
        binop.resolve(WArrays::register$lambda$1$lambda$0)
        val var5 = G.v().soot_BooleanType
        binop.putSummaryIfNotConcrete(var5 as Type, "return")
        IFact.Builder.DefaultImpls.assignNewExpr$default(
            this.out, this.env, this.hf.vg.RETURN_LOCAL, binop.res.build(), false, 8, null
        )
    }

    public companion object {
        public fun v(): WArrays {
            return WArrays()
        }
    }
}