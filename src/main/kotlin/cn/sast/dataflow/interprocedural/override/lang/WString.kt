package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.ArraySpace
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import cn.sast.dataflow.util.SootUtilsKt
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.IntType
import soot.Local
import soot.RefType
import soot.SootField
import soot.Type
import soot.jimple.AnyNewExpr
import soot.jimple.Constant
import soot.jimple.IntConstant
import soot.jimple.NewArrayExpr

public class WString : SummaryHandlePackage<IValue> {
    public val classType: RefType = RefType.v("java.lang.String")
    public val byteType: ByteType = G.v().soot_ByteType()
    public val arrayType: ArrayType = ArrayType.v(this.byteType as Type, 1)
    public val valueField: SootField
    public val coderField: SootField
    public val hashField: SootField
    public val sizeLocal: Local
    public val newValueExpr: NewArrayExpr
    public val newStringExpr: NewArrayExpr

    public override fun ACheckCallAnalysis.register() {
        `$this$register`.evalCall(UtilsKt.getSootSignature(TODO("FIXME — unrepresentable instance") as KCallable<*>, WString::register$lambda$1)
        val var10003: ArrayType = this.arrayType
        `$this$register`.postCallAtCaller("<java.lang.String: byte[] getBytes()>", register$getValueElement(this, var10003))
        val var10003_2: ArrayType = this.arrayType
        `$this$register`.postCallAtCaller("<java.lang.String: byte[] getBytes(java.lang.String)>", register$getValueElement(this, var10003_2))
        `$this$register`.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.lang.String: boolean equals(java.lang.Object)>", false))
        `$this$register`.registerWrapper(SootUtilsKt.sootSignatureToRef("<java.lang.String: char charAt(int)>", false))
        `$this$register`.evalCall("<java.lang.String: boolean isLatin1()>", WString::register$lambda$4)
        `$this$register`.evalCall("<java.lang.String: byte coder()>", WString::register$lambda$5)
    }

    @JvmStatic
    private fun `register$lambda$1$lambda$0`(
        `$this_ret`: CalleeCBImpl.EvalCall,
        `$this$resolve`: IOpCalculator,
        res: IHeapValues.Builder,
        var3: Array<CompanionV>
    ): Boolean {
        val var7: Any = var3[0].getValue()
        val var10000: ConstVal? = var7 as? ConstVal
        if (var10000 != null) {
            val var8: String? = FactValuesKt.getStringValue(var10000, true)
            if (var8 != null) {
                val var9: AbstractHeapFactory = `$this_ret`.getHf()
                val var10001: IntConstant = IntConstant.v(var8.hashCode())
                val var10: Constant = var10001 as Constant
                val var10002: IntType = G.v().soot_IntType()
                res.add(
                    `$this_ret`.getHf().push(`$this_ret`.getEnv(), var9.newConstVal(var10, var10002 as Type) as IValue).markOfReturnValueOfMethod(`$this_ret`).pop()
                )
                return true
            }
        }
        return false
    }

    @JvmStatic
    private fun CalleeCBImpl.EvalCall.`register$lambda$1`() {
        val c: IOpCalculator = WStringKt.getStringFromObject(`$this$ret`, `$this$ret`.arg(-1))
        c.putSummaryIfNotConcrete(`$this$ret`.getHf().getVg().getSTRING_TYPE() as Type, "return")
        val strOp: IOpCalculator = `$this$ret`.getHf().resolveOp(`$this$ret`.getEnv(), c.getRes().build())
        strOp.resolve(WString::register$lambda$1$lambda$0)
        IFact.Builder.DefaultImpls.assignNewExpr$default(
            `$this$ret`.getOut(), `$this$ret`.getEnv(), `$this$ret`.getHf().getVg().getRETURN_LOCAL(), strOp.getRes().build(), false, 8, null
        )
    }

    @JvmStatic
    private fun `register$getValueElement$lambda$3$lambda$2`(
        `$this_ret`: CallerSiteCBImpl.PostCall,
        `this$0`: WString,
        `$returnType`: ArrayType,
        `$this$resolve`: IOpCalculator,
        res: IHeapValues.Builder,
        var5: Array<CompanionV>
    ): Boolean {
        val th1s: CompanionV = var5[0]
        val var10000: AbstractHeapFactory = `$this_ret`.getHf()
        val var10001: AnyNewExprEnv = `$this_ret`.getNewEnv()
        val var10002: NewArrayExpr = `this$0`.newValueExpr
        val newValue: IValue = var10000.anyNewVal(var10001, var10002 as AnyNewExpr) as IValue
        res.add(`$this_ret`.getHf().push(`$this_ret`.getEnv(), newValue).dataSequenceToSeq(th1s).pop())
        if (th1s.getValue() is ConstVal) {
            val var12: String? = FactValuesKt.getStringValue$default(th1s.getValue() as IValue, false, 1, null)
            if (var12 != null) {
                val var13: ByteArray = var12.getBytes(Charsets.UTF_8)
                if (var13 != null) {
                    `$this_ret`.getOut()
                        .setValueData(
                            `$this_ret`.getEnv(),
                            newValue,
                            BuiltInModelT.Array,
                            ArraySpace.Companion
                                .v(
                                    `$this_ret`.getHf(),
                                    `$this_ret`.getEnv(),
                                    th1s,
                                    var13.toTypedArray(),
                                    `$returnType`,
                                    `$this_ret`.getHf().push(`$this_ret`.getEnv(), `$this_ret`.getHf().toConstVal(var13.size)).popHV()
                                )
                        )
                    return true
                }
            }
            return false
        } else {
            `$this_ret`.getOut()
                .setValueData(`$this_ret`.getEnv(), newValue, BuiltInModelT.Array, `$this_ret`.getOut().getValueData(th1s.getValue() as IValue, BuiltInModelT.Array))
            return true
        }
    }

    @JvmStatic
    private fun `register$getValueElement$lambda$3`(`this$0`: WString, `$returnType`: ArrayType, `$this$ret`: CallerSiteCBImpl.PostCall) {
        val strObjectOp: IOpCalculator = `$this$ret`.getHf().resolveOp(`$this$ret`.getEnv(), `$this$ret`.getThis())
        strObjectOp.resolve(WString::register$getValueElement$lambda$3$lambda$2)
        `$this$ret`.setReturn(`$this$ret`.getReturn().plus(strObjectOp.getRes().build()))
    }

    @JvmStatic
    private fun `register$getValueElement`(`this$0`: WString, returnType: ArrayType): (CallerSiteCBImpl.PostCall) -> Unit {
        return { `$this$ret` -> `register$getValueElement$lambda$3`(`this$0`, returnType, `$this$ret`) }
    }

    @JvmStatic
    private fun CalleeCBImpl.EvalCall.`register$lambda$4`() {
        IFact.Builder.DefaultImpls.assignNewExpr$default(
            `$this$ret`.getOut(),
            `$this$ret`.getEnv(),
            `$this$ret`.getHf().getVg().getRETURN_LOCAL(),
            `$this$ret`.getHf().push(`$this$ret`.getEnv(), `$this$ret`.getHf().toConstVal(true)).markOfReturnValueOfMethod(`$this$ret`).popHV(),
            false,
            8,
            null
        )
        `$this$ret`.getOut().build()
    }

    @JvmStatic
    private fun CalleeCBImpl.EvalCall.`register$lambda$5`() {
        val var10000: IFact.Builder = `$this$ret`.getOut()
        val var10001: HeapValuesEnv = `$this$ret`.getEnv()
        val var10002: String = `$this$ret`.getHf().getVg().getRETURN_LOCAL()
        val var10003: AbstractHeapFactory = `$this$ret`.getHf()
        val var10004: HeapValuesEnv = `$this$ret`.getEnv()
        val var10005: AbstractHeapFactory = `$this$ret`.getHf()
        val var10006: Constant = LATIN1 as Constant
        val var10007: ByteType = G.v().soot_ByteType()
        IFact.Builder.DefaultImpls.assignNewExpr$default(
            var10000,
            var10001,
            var10002,
            var10003.push(var10004, var10005.newConstVal(var10006, var10007 as Type)).markOfReturnValueOfMethod(`$this$ret`).popHV(),
            false,
            8,
            null
        )
    }

    public companion object {
        public val LATIN1_BYTE: Byte
        public val UTF16_BYTE: Byte
        public val LATIN1: IntConstant
        public val UTF16: IntConstant

        init {
            LATIN1 = IntConstant.v(0)
            UTF16 = IntConstant.v(1)
        }

        public fun v(): WString {
            return WString()
        }
    }
}