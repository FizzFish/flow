@file:SourceDebugExtension(["SMAP\nWNumber.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WNumber.kt\ncn/sast/dataflow/interprocedural/override/lang/WNumberKt\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n*L\n1#1,263:1\n44#2:264\n*S KotlinDebug\n*F\n+ 1 WNumber.kt\ncn/sast/dataflow/interprocedural/override/lang/WNumberKt\n*L\n35#1:264\n*E\n"])

package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.callback.ICallCBImpl
import kotlin.jvm.internal.SourceDebugExtension
import soot.G
import soot.RefType
import soot.SootField
import soot.Type

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getValueFromObject(obj: IHeapValues<IValue>, type: Type): IOpCalculator<IValue> {
    val c: IOpCalculator<IValue> = this.getHf().resolveOp(this.getEnv(), obj)
    c.resolve(::getValueFromObject$lambda$0)
    return c
}

private fun getValueFromObject$lambda$0(
    type: Type,
    this_getValueFromObject: ICallCBImpl<*, *>,
    this_getValue: IOpCalculator<*>,
    res: IHeapValues.Builder,
    var4: Array<CompanionV>
): Boolean {
    val numObj: CompanionV = var4[0]
    var var10000: Any?
    when (type) {
        G.v().soot_ByteType(),
        G.v().soot_CharType(),
        G.v().soot_BooleanType(),
        G.v().soot_ShortType(),
        G.v().soot_IntType() -> var10000 = FactValuesKt.getIntValue(numObj.getValue() as IValue, false)
        G.v().soot_LongType() -> var10000 = FactValuesKt.getLongValue(numObj.getValue() as IValue, false)
        G.v().soot_FloatType() -> var10000 = FactValuesKt.getFloatValue(numObj.getValue() as IValue, false)
        G.v().soot_DoubleType() -> var10000 = FactValuesKt.getDoubleValue(numObj.getValue() as IValue, false)
        else -> return false
    }

    if (var10000 != null) {
        res.add(numObj)
        return true
    } else {
        IFact.Builder.DefaultImpls.assignNewExpr$default(
            this_getValueFromObject.getOut() as IFact.Builder,
            this_getValueFromObject.getEnv(),
            "@num",
            this_getValueFromObject.getHf().empty().plus(numObj),
            false,
            8,
            null
        )
        val value: Type = (numObj.getValue() as IValue).getType()
        var10000 = value as? RefType
        if ((value as? RefType) == null) {
            return false
        } else {
            var10000 = (var10000 as RefType).sootClass.getFieldByNameUnsafe("value")
            if (var10000 == null) {
                return false
            } else {
                val var15: IFact.Builder = this_getValueFromObject.getOut() as IFact.Builder
                val var10001: HeapValuesEnv = this_getValueFromObject.getEnv()
                val var11: FieldUtil = FieldUtil.INSTANCE
                IFact.Builder.DefaultImpls.getField$default(
                    var15,
                    var10001,
                    "@value",
                    "@num",
                    JSootFieldType(var10000 as SootField),
                    false,
                    16,
                    null
                )
                val var12: IHeapValues<*> = (this_getValueFromObject.getOut() as IFact.Builder).getTargets("@value")
                (this_getValueFromObject.getOut() as IFact.Builder).kill("@value")
                (this_getValueFromObject.getOut() as IFact.Builder).kill("@num")
                res.add(var12)
                return true
            }
        }
    }
}