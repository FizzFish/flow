package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IVGlobal
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.ArraySpace
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.PointsToGraphBuilder
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.util.SootUtilsKt
import kotlin.jvm.internal.SourceDebugExtension
import soot.ByteType
import soot.G
import soot.Local
import soot.RefType
import soot.SootField
import soot.Type
import soot.Value
import soot.jimple.AnyNewExpr
import soot.jimple.Constant
import soot.jimple.Jimple
import soot.jimple.NewArrayExpr
import soot.jimple.NewExpr

@SourceDebugExtension(["SMAP\nWStringCoding.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WStringCoding.kt\ncn/sast/dataflow/interprocedural/override/lang/WStringCoding\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n*L\n1#1,112:1\n44#2:113\n44#2:114\n*S KotlinDebug\n*F\n+ 1 WStringCoding.kt\ncn/sast/dataflow/interprocedural/override/lang/WStringCoding\n*L\n93#1:113\n100#1:114\n*E\n"])
class WStringCoding(vg: IVGlobal) : SummaryHandlePackage<IValue>() {
    val sizeLocal: Local = Jimple.v().newLocal("size", G.v().soot_IntType() as Type)
    val newValueExpr: NewArrayExpr
    val clzStringCodingResult: String
    val StringCodingResultType: RefType
    val newExprStringCodingResult: NewExpr
    val stringCodingResultValueField: SootField
    val stringCodingResultCoderField: SootField

    init {
        newValueExpr = Jimple.v().newNewArrayExpr(vg.getBYTE_ARRAY_TYPE() as Type, sizeLocal as Value)
        clzStringCodingResult = "java.lang.StringCoding$Result"
        StringCodingResultType = RefType.v(clzStringCodingResult)
        newExprStringCodingResult = Jimple.v().newNewExpr(StringCodingResultType)
        stringCodingResultValueField = SootUtilsKt.getOrMakeField(clzStringCodingResult, "value", vg.getBYTE_ARRAY_TYPE() as Type)
        stringCodingResultCoderField = SootUtilsKt.getOrMakeField(clzStringCodingResult, "coder", G.v().soot_ByteType() as Type)
    }

    override fun ACheckCallAnalysis.register() {
        `$this$register`.evalCall("<java.lang.StringCoding: byte[] encode(byte,byte[])>", WStringCoding::register$lambda$1)
        `$this$register`.evalCall("<java.lang.StringCoding: java.lang.StringCoding$Result decode(byte[],int,int)>", WStringCoding::register$lambda$3)
    }

    companion object {
        @JvmStatic
        fun `register$lambda$1$lambda$0`(
            `$this_evalCall`: CalleeCBImpl.EvalCall,
            `this$0`: WStringCoding,
            `$this$encode`: IOpCalculator,
            ret: IHeapValues.Builder,
            var4: Array<CompanionV>
        ): Boolean {
            val coder: CompanionV = var4[0]
            val var6: CompanionV = var4[1]
            val var10000: Byte? = FactValuesKt.getByteValue(coder.getValue() as IValue, true)
            if (var10000 != null) {
                val coderInt: Byte = var10000
                if (WStringKt.getByteArray(`$this_evalCall`, var6.getValue() as IValue) == null) {
                    return false
                } else {
                    val array: IData = `$this_evalCall`.getOut().getValueData(var6.getValue() as IValue, BuiltInModelT.Array)
                    val arrayData: IArrayHeapKV? = array as? IArrayHeapKV
                    if (arrayData != null) {
                        val var17: ByteArray? = arrayData.getByteArray(`$this_evalCall`.getHf())
                        if (var17 != null) {
                            val var18: ByteArray = (if (coderInt == WString.Companion.getLATIN1_BYTE())
                                String(var17, Charsets.UTF_8)
                            else
                                String(var17, Charsets.UTF_16))
                                .toByteArray(Charsets.UTF_8)
                            val var19: AbstractHeapFactory = `$this_evalCall`.getHf()
                            val var10001: HeapValuesEnv = `$this_evalCall`.getEnv()
                            val var10002: AbstractHeapFactory = `$this_evalCall`.getHf()
                            val var10003: AnyNewExprEnv = `$this_evalCall`.getNewEnv()
                            val var10004: NewArrayExpr = `this$0`.newValueExpr
                            val var20: JOperatorV = var19.push(var10001, var10002.anyNewVal(var10003, var10004 as AnyNewExpr))
                            val var21: NewArrayExpr = `this$0`.newValueExpr
                            val newValue: CompanionV = var20.markOfNewExpr(var21 as AnyNewExpr).pop()
                            ret.add(newValue)
                            `$this_evalCall`.getOut()
                                .setValueData(
                                    `$this_evalCall`.getEnv(),
                                    newValue.getValue() as IValue,
                                    BuiltInModelT.Array,
                                    ArraySpace.Companion
                                        .v(
                                            `$this_evalCall`.getHf(),
                                            `$this_evalCall`.getEnv(),
                                            var6,
                                            var18.toTypedArray(),
                                            `$this_evalCall`.getHf().getVg().getBYTE_ARRAY_TYPE(),
                                            `$this_evalCall`.getHf()
                                                .push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(var18.size))
                                                .markArraySizeOf(var6)
                                                .popHV()
                                        )
                                )
                            return true
                        }
                    }

                    return false
                }
            } else {
                return false
            }
        }

        @JvmStatic
        fun `register$lambda$1`(`this$0`: WStringCoding, `$this$evalCall`: CalleeCBImpl.EvalCall) {
            val encodeOp: IOpCalculator = `$this$evalCall`.getHf().resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(0), `$this$evalCall`.arg(1))
            encodeOp.resolve(Companion::`register$lambda$1$lambda$0`)
            encodeOp.putSummaryIfNotConcrete(`$this$evalCall`.getHf().getVg().getBYTE_ARRAY_TYPE() as Type, "return")
            `$this$evalCall`.setReturn(encodeOp.getRes().build())
        }

        @JvmStatic
        fun `register$lambda$3$lambda$2`(
            `$this_evalCall`: CalleeCBImpl.EvalCall,
            `this$0`: WStringCoding,
            `$this$encode`: IOpCalculator,
            res: IHeapValues.Builder,
            var4: Array<CompanionV>
        ): Boolean {
            val var5: CompanionV = var4[0]
            val off: CompanionV = var4[1]
            val len: CompanionV = var4[2]
            var var10000: Int? = FactValuesKt.getIntValue(off.getValue() as IValue, true)
            if (var10000 != null) {
                val offInt: Int = var10000
                var10000 = FactValuesKt.getIntValue(len.getValue() as IValue, true)
                if (var10000 != null) {
                    val lenInt: Int = var10000
                    val var26: ByteArray? = WStringKt.getByteArray(`$this_evalCall`, var5.getValue() as IValue)
                    if (var26 == null) {
                        return false
                    } else {
                        val byteArray: ByteArray = var26

                        val newResult: ByteArray = try {
                            String(byteArray, offInt, lenInt, Charsets.UTF_8).toByteArray(Charsets.UTF_8)
                        } catch (var20: StringIndexOutOfBoundsException) {
                            return false
                        }

                        val var28: AbstractHeapFactory = `$this_evalCall`.getHf()
                        var var10001: HeapValuesEnv = `$this_evalCall`.getEnv()
                        var var10002: AbstractHeapFactory = `$this_evalCall`.getHf()
                        var var10003: AnyNewExprEnv = `$this_evalCall`.getNewEnv()
                        val var10004: NewExpr = `this$0`.newExprStringCodingResult
                        val var29: JOperatorV = var28.push(var10001, var10002.anyNewVal(var10003, var10004 as AnyNewExpr))
                        val var37: NewExpr = `this$0`.newExprStringCodingResult
                        val var21: CompanionV = var29.markOfNewExpr(var37 as AnyNewExpr).pop()
                        val var30: AbstractHeapFactory = `$this_evalCall`.getHf()
                        var10001 = `$this_evalCall`.getEnv()
                        var10002 = `$this_evalCall`.getHf()
                        var10003 = `$this_evalCall`.getNewEnv()
                        val var49: NewArrayExpr = `this$0`.newValueExpr
                        val var31: JOperatorV = var30.push(var10001, var10002.anyNewVal(var10003, var49 as AnyNewExpr))
                        val var39: NewArrayExpr = `this$0`.newValueExpr
                        val newValue: CompanionV = var31.markOfNewExpr(var39 as AnyNewExpr).pop()
                        val var32: AbstractHeapFactory = `$this_evalCall`.getHf()
                        var10001 = `$this_evalCall`.getEnv()
                        var10002 = `$this_evalCall`.getHf()
                        val var48: Constant = WString.Companion.getLATIN1() as Constant
                        val var50: ByteType = G.v().soot_ByteType()
                        val newCoder: CompanionV = JOperatorV.DefaultImpls.markOfConstant$default(
                            var32.push(var10001, var10002.newConstVal(var48, var50 as Type)),
                            WString.Companion.getLATIN1() as Constant,
                            null,
                            2,
                            null
                        ).pop()
                        `$this_evalCall`.getOut()
                            .setValueData(
                                `$this_evalCall`.getEnv(),
                                newValue.getValue() as IValue,
                                BuiltInModelT.Array,
                                ArraySpace.Companion
                                    .v(
                                        `$this_evalCall`.getHf(),
                                        `$this_evalCall`.getEnv(),
                                        var5,
                                        newResult.toTypedArray(),
                                        `$this_evalCall`.getHf().getVg().getBYTE_ARRAY_TYPE(),
                                        `$this_evalCall`.getHf()
                                            .push(`$this_evalCall`.getEnv(), `$this_evalCall`.getHf().toConstVal(newResult.size))
                                            .markArraySizeOf(var5)
                                            .popHV()
                                    )
                            )
                        val var33: IFact.Builder = `$this_evalCall`.getOut()
                        val var34: PointsToGraphBuilder = var33 as PointsToGraphBuilder
                        var10001 = `$this_evalCall`.getEnv()
                        val var45: IHeapValues = `$this_evalCall`.getHf().empty().plus(var21)
                        var `this_$iv`: FieldUtil = FieldUtil.INSTANCE
                        var34.assignField(
                            var10001, var45, JSootFieldType(`this$0`.stringCodingResultValueField), `$this_evalCall`.getHf().empty().plus(newValue), false
                        )
                        val var35: IFact.Builder = `$this_evalCall`.getOut()
                        val var36: PointsToGraphBuilder = var35 as PointsToGraphBuilder
                        var10001 = `$this_evalCall`.getEnv()
                        val var46: IHeapValues = `$this_evalCall`.getHf().empty().plus(var21)
                        `this_$iv` = FieldUtil.INSTANCE
                        var36.assignField(
                            var10001, var46, JSootFieldType(`this$0`.stringCodingResultCoderField), `$this_evalCall`.getHf().empty().plus(newCoder), false
                        )
                        res.add(var21)
                        return true
                    }
                } else {
                    return false
                }
            } else {
                return false
            }
        }

        @JvmStatic
        fun `register$lambda$3`(`this$0`: WStringCoding, `$this$evalCall`: CalleeCBImpl.EvalCall) {
            val decodeOp: IOpCalculator = `$this$evalCall`.getHf()
                .resolveOp(`$this$evalCall`.getEnv(), `$this$evalCall`.arg(0), `$this$evalCall`.arg(1), `$this$evalCall`.arg(2))
            decodeOp.resolve(Companion::`register$lambda$3$lambda$2`)
            val var7: RefType = `this$0`.StringCodingResultType
            decodeOp.putSummaryIfNotConcrete(var7 as Type, "return")
            `$this$evalCall`.setReturn(decodeOp.getRes().build())
        }

        fun v(vg: IVGlobal): WStringCoding {
            return WStringCoding(vg)
        }
    }
}