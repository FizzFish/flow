package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.AnyNewExprEnv
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.util.SootUtilsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import soot.ByteType
import soot.DoubleType
import soot.FloatType
import soot.G
import soot.IntType
import soot.IntegerType
import soot.LongType
import soot.PrimType
import soot.RefType
import soot.ShortType
import soot.SootField
import soot.Type
import soot.jimple.AnyNewExpr
import soot.jimple.Constant
import soot.jimple.Jimple
import soot.jimple.NewExpr
import soot.jimple.NumericConstant
import soot.jimple.StringConstant

@SourceDebugExtension(["SMAP\nWNumber.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WNumber.kt\ncn/sast/dataflow/interprocedural/override/lang/WNumber\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n*L\n1#1,263:1\n44#2:264\n44#2:265\n44#2:266\n*S KotlinDebug\n*F\n+ 1 WNumber.kt\ncn/sast/dataflow/interprocedural/override/lang/WNumber\n*L\n62#1:264\n146#1:265\n178#1:266\n*E\n"])
class WNumber : SummaryHandlePackage<IValue>() {
    private fun ACheckCallAnalysis.registerToValue(clzName: String, valueFieldType: PrimType, valueField: SootField, toType: PrimType) {
        val handle: Function1<EvalCall, Unit> = WNumber::registerToValue$lambda$1
        this.evalCall("<$clzName: $toType $toTypeValue()>", handle)
        val var10003: String = toType.toString()
        this.evalCall("<$clzName: $toType to${StringsKt.capitalize(var10003)}()>", handle)
    }

    fun EvalCall.parseString(hint: String, sIdx: Int, radixIdx: Int, resType: PrimType): IOpCalculator<IValue> {
        val valueOp: IOpCalculator<IValue> = this.getHf()
            .resolveOp(this.getEnv(), this.arg(sIdx), this.arg(radixIdx))
        valueOp.resolve(WNumber::parseString$lambda$2)
        valueOp.putSummaryIfNotConcrete(resType as Type, "return")
        return valueOp
    }

    fun EvalCall.parseStringFloating(hint: String, sIdx: Int, resType: PrimType): IOpCalculator<IValue> {
        val valueOp: IOpCalculator<IValue> = this.getHf().resolveOp(this.getEnv(), this.arg(sIdx))
        valueOp.resolve(WNumber::parseStringFloating$lambda$3)
        valueOp.putSummaryIfNotConcrete(resType as Type, "return")
        return valueOp
    }

    fun ACheckCallAnalysis.registerValueOf(clzName: String, valueFieldType: PrimType) {
        val valueField: SootField = SootUtilsKt.getOrMakeField(clzName, "value", valueFieldType as Type)
        val newExpr: NewExpr = Jimple.v().newNewExpr(RefType.v(clzName))
        if (valueFieldType !is IntegerType && valueFieldType !is LongType && valueFieldType !is FloatType && valueFieldType !is DoubleType) {
            throw IllegalStateException("error type of $valueFieldType")
        } else {
            this.evalCall("<$clzName: $clzName valueOf($valueFieldType)>", WNumber::registerValueOf$lambda$4)
            this.registerWrapper("<$clzName: $clzName valueOf(java.lang.String)>", true)
            var var10003: String = valueFieldType.toString()
            this.registerWrapper("<$clzName: $valueFieldType parse${StringsKt.capitalize(var10003)}(java.lang.String)>", true)
            if (valueFieldType !is FloatType && valueFieldType !is DoubleType) {
                var10003 = valueFieldType.toString()
                this.evalCall(
                    "<$clzName: $valueFieldType parse${StringsKt.capitalize(var10003)}(java.lang.String,int)>", WNumber::registerValueOf$lambda$6
                )
                this.evalCall("<$clzName: $clzName valueOf(java.lang.String,int)>", WNumber::registerValueOf$lambda$7)
            } else {
                var10003 = valueFieldType.toString()
                this.evalCall(
                    "<$clzName: $valueFieldType parse${StringsKt.capitalize(var10003)}(java.lang.String)>", WNumber::registerValueOf$lambda$5
                )
            }
        }
    }

    fun ACheckCallAnalysis.registerEquals(clzName: String, valueField: SootField) {
        val classType: RefType = RefType.v(clzName)
        this.registerWrapper("<$clzName: boolean equals(java.lang.Object)>", false)
    }

    override fun ACheckCallAnalysis.register() {
        listOf(
            "java.lang.Integer" to G.v().soot_IntType(),
            "java.lang.Long" to G.v().soot_LongType(),
            "java.lang.Short" to G.v().soot_ShortType(),
            "java.lang.Byte" to G.v().soot_ByteType(),
            "java.lang.Float" to G.v().soot_FloatType(),
            "java.lang.Double" to G.v().soot_DoubleType()
        ).forEach { (c, valueOfType) ->
            this.registerClassAllWrapper(c)
            val valueField: SootField = SootUtilsKt.getOrMakeField(c, "value", valueOfType as Type)
            this.registerToValue(c, valueOfType, valueField, G.v().soot_ByteType())
            this.registerToValue(c, valueOfType, valueField, G.v().soot_ShortType())
            this.registerToValue(c, valueOfType, valueField, G.v().soot_IntType())
            this.registerToValue(c, valueOfType, valueField, G.v().soot_LongType())
            this.registerToValue(c, valueOfType, valueField, G.v().soot_FloatType())
            this.registerToValue(c, valueOfType, valueField, G.v().soot_DoubleType())
            this.registerValueOf(c, valueOfType)
            this.registerEquals(c, valueField)
        }
    }

    companion object {
        @JvmStatic
        fun registerToValue$lambda$1$lambda$0(
            `$toType`: PrimType,
            `$this`: CalleeCBImpl.EvalCall,
            `$this$resolve`: IOpCalculator<IValue>,
            res: IHeapValues.Builder,
            var4: Array<CompanionV>
        ): Boolean {
            val op: IValue = var4[0].getValue() as IValue
            val var9: Constant? = (op as? ConstVal)?.getV()
            val var10000: NumericConstant? = var9 as? NumericConstant
            if (var9 as? NumericConstant == null) {
                return false
            } else {
                val var10: Constant? = cn.sast.api.util.SootUtilsKt.castTo(var10000, `$toType` as Type)
                if (var10 == null) {
                    return false
                } else {
                    res.add(`$this`.getHf().push(`$this`.getEnv(), `$this`.getHf().newConstVal(var10, `$toType` as Type)).markOfCastTo(`$toType`).pop())
                    return true
                }
            }
        }

        @JvmStatic
        fun registerToValue$lambda$1(`$valueField`: SootField, `$toType`: PrimType, var2: CalleeCBImpl.EvalCall) {
            val var10000: IFact.Builder = var2.getOut()
            val var10001: HeapValuesEnv = var2.getEnv()
            val var10003: Int = -1
            val value: FieldUtil = FieldUtil.INSTANCE
            IFact.Builder.DefaultImpls.getField$default(var10000, var10001, "value", var10003, JSootFieldType(`$valueField`), false, 16, null)
            val var7: IOpCalculator<IValue> = var2.getHf().resolveOp(var2.getEnv(), var2.getOut().getTargets("value"))
            var7.resolve(WNumber::registerToValue$lambda$1$lambda$0)
            var7.putSummaryIfNotConcrete(`$toType` as Type, var2.getHf().getVg().getRETURN_LOCAL())
            IFact.Builder.DefaultImpls.assignNewExpr$default(
                var2.getOut(), var2.getEnv(), var2.getHf().getVg().getRETURN_LOCAL(), var7.getRes().build(), false, 8, null
            )
            var2.getOut().kill("value")
        }

        @JvmStatic
        fun parseString$lambda$2(
            `$this_parseString`: CalleeCBImpl.EvalCall,
            `$resType`: PrimType,
            `$hint`: String,
            `$this$resolve`: IOpCalculator<IValue>,
            res: IHeapValues.Builder,
            var5: Array<CompanionV>
        ): Boolean {
            val cop: CompanionV = var5[0]
            val cradix: CompanionV = var5[1]
            val str: IValue = cop.getValue() as IValue
            val var10000: Int? = FactValuesKt.getIntValue(cradix.getValue() as IValue, true)
            if (var10000 == null) {
                return false
            } else {
                val radixNm: Int = var10000
                val var18: String
                if (str is ConstVal) {
                    val var13: Constant = (str as ConstVal).getV()
                    val var17: StringConstant? = var13 as? StringConstant
                    if (var13 as? StringConstant == null) {
                        return false
                    }

                    var18 = var17.value
                    if (var17.value == null) {
                        return false
                    }
                } else {
                    val var19: ByteArray? = WStringKt.getByteArray(`$this_parseString`, str)
                    if (var19 == null) {
                        return false
                    }

                    var18 = StringsKt.decodeToString(var19)
                }

                val sc: String = var18

                val var14: NumericConstant?
                try {
                    var14 = cn.sast.api.util.SootUtilsKt.cvtNumericConstant(sc, radixNm, `$resType` as Type)
                } catch (var16: NumberFormatException) {
                    return false
                }

                if (var14 == null) {
                    return false
                } else {
                    res.add(
                        `$this_parseString`.getHf()
                            .push(`$this_parseString`.getEnv(), `$this_parseString`.getHf().newConstVal(var14, `$resType` as Type))
                            .markOfParseString(`$hint`, cop)
                            .pop()
                    )
                    return true
                }
            }
        }

        @JvmStatic
        fun parseStringFloating$lambda$3(
            `$this_parseStringFloating`: CalleeCBImpl.EvalCall,
            `$resType`: PrimType,
            `$hint`: String,
            `$this$resolve`: IOpCalculator<IValue>,
            res: IHeapValues.Builder,
            var5: Array<CompanionV>
        ): Boolean {
            val cop: CompanionV = var5[0]
            val str: IValue = var5[0].getValue() as IValue
            val var14: String
            if (str is ConstVal) {
                val var10: Constant = (str as ConstVal).getV()
                val var10000: StringConstant? = var10 as? StringConstant
                if (var10 as? StringConstant == null) {
                    return false
                }

                var14 = var10000.value
                if (var10000.value == null) {
                    return false
                }
            } else {
                val var15: ByteArray? = WStringKt.getByteArray(`$this_parseStringFloating`, str)
                if (var15 == null) {
                    return false
                }

                var14 = StringsKt.decodeToString(var15)
            }

            val sc: String = var14

            val var11: NumericConstant?
            try {
                var11 = cn.sast.api.util.SootUtilsKt.cvtNumericConstant(sc, -1, `$resType` as Type)
            } catch (var13: NumberFormatException) {
                return false
            }

            if (var11 == null) {
                return false
            } else {
                res.add(
                    `$this_parseStringFloating`.getHf()
                        .push(`$this_parseStringFloating`.getEnv(), `$this_parseStringFloating`.getHf().newConstVal(var11, `$resType` as Type))
                        .markOfParseString(`$hint`, cop)
                        .pop()
                )
                return true
            }
        }

        @JvmStatic
        fun registerValueOf$lambda$4(`$newExpr`: NewExpr, `$valueField`: SootField, `$this$evalCall`: CalleeCBImpl.EvalCall) {
            val var10000: AbstractHeapFactory = `$this$evalCall`.getHf()
            var var10001: HeapValuesEnv = `$this$evalCall`.getEnv()
            val var10002: AbstractHeapFactory = `$this$evalCall`.getHf()
            val var10003: AnyNewExprEnv = `$this$evalCall`.getNewEnv()
            IFact.Builder.DefaultImpls.assignNewExpr$default(
                `$this$evalCall`.getOut(),
                `$this$evalCall`.getEnv(),
                `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
                var10000.push(var10001, var10002.anyNewVal(var10003, `$newExpr`)).markOfNewExpr(`$newExpr`).popHV(),
                false,
                8,
                null
            )
            val var6: IFact.Builder = `$this$evalCall`.getOut()
            var10001 = `$this$evalCall`.getEnv()
            val var8: String = `$this$evalCall`.getHf().getVg().getRETURN_LOCAL()
            val `this_$iv`: FieldUtil = FieldUtil.INSTANCE
            IFact.Builder.DefaultImpls.setField$default(var6, var10001, var8, JSootFieldType(`$valueField`), 0, false, 16, null)
        }

        @JvmStatic
        fun registerValueOf$lambda$5(`this$0`: WNumber, `$clzName`: String, `$valueFieldType`: PrimType, `$this$evalCall`: CalleeCBImpl.EvalCall) {
            IFact.Builder.DefaultImpls.assignNewExpr$default(
                `$this$evalCall`.getOut(),
                `$this$evalCall`.getEnv(),
                `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
                `this$0`.parseStringFloating(`$this$evalCall`, `$clzName`, 0, `$valueFieldType`).getRes().build(),
                false,
                8,
                null
            )
        }

        @JvmStatic
        fun registerValueOf$lambda$6(`this$0`: WNumber, `$clzName`: String, `$valueFieldType`: PrimType, `$this$evalCall`: CalleeCBImpl.EvalCall) {
            IFact.Builder.DefaultImpls.assignNewExpr$default(
                `$this$evalCall`.getOut(),
                `$this$evalCall`.getEnv(),
                `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
                `this$0`.parseString(`$this$evalCall`, `$clzName`, 0, 1, `$valueFieldType`).getRes().build(),
                false,
                8,
                null
            )
        }

        @JvmStatic
        fun registerValueOf$lambda$7(
            `$newExpr`: NewExpr,
            `this$0`: WNumber,
            `$clzName`: String,
            `$valueFieldType`: PrimType,
            `$valueField`: SootField,
            `$this$evalCall`: CalleeCBImpl.EvalCall
        ) {
            val var10000: AbstractHeapFactory = `$this$evalCall`.getHf()
            var var10001: HeapValuesEnv = `$this$evalCall`.getEnv()
            val var10002: AbstractHeapFactory = `$this$evalCall`.getHf()
            val var10003: AnyNewExprEnv = `$this$evalCall`.getNewEnv()
            IFact.Builder.DefaultImpls.assignNewExpr$default(
                `$this$evalCall`.getOut(),
                `$this$evalCall`.getEnv(),
                `$this$evalCall`.getHf().getVg().getRETURN_LOCAL(),
                var10000.push(var10001, var10002.anyNewVal(var10003, `$newExpr`)).markOfNewExpr(`$newExpr`).popHV(),
                false,
                8,
                null
            )
            val eval: IOpCalculator<IValue> = `this$0`.parseString(`$this$evalCall`, `$clzName`, 0, 1, `$valueFieldType`)
            val var10: IFact.Builder = `$this$evalCall`.getOut()
            var10001 = `$this$evalCall`.getEnv()
            val var12: String = `$this$evalCall`.getHf().getVg().getRETURN_LOCAL()
            val `this_$iv`: FieldUtil = FieldUtil.INSTANCE
            var10.setFieldNew(var10001, var12, JSootFieldType(`$valueField`), eval.getRes().build())
        }

        fun v(): WNumber {
            return WNumber()
        }
    }
}