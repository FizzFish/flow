package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.check.OverrideModel
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import kotlin.jvm.internal.SourceDebugExtension
import soot.BooleanType
import soot.G
import soot.IntType
import soot.Type
import soot.jimple.Constant
import soot.jimple.IntConstant

@SourceDebugExtension(["SMAP\nWArrayList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/WArrayList\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,344:1\n1863#2,2:345\n*S KotlinDebug\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/WArrayList\n*L\n47#1:345,2\n*E\n"])
public class WArrayList : SummaryHandlePackage<IValue> {
    public val intType: IntType = G.v().soot_IntType()

    public override fun ACheckCallAnalysis.register() {
        val var15: Iterable<Any>
        for (element in var15) {
            this.evalCallAtCaller(element as String, Companion::register$lambda$2$lambda$1)
        }

        this.evalCallAtCaller("<java.util.ArrayList: void clear()>", Companion::register$lambda$4)
        this.evalCallAtCaller("<java.util.ArrayList: java.lang.Object get(int)>", Companion::register$lambda$6)
        this.evalCall("<java.util.ArrayList: boolean add(java.lang.Object)>", Companion::register$lambda$8)
        this.evalCallAtCaller("<java.util.ArrayList: java.lang.Object remove(int)>", Companion::register$lambda$10)
    }

    companion object {
        @JvmStatic
        fun register$mapGetModel(
            `$this_register`: ACheckCallAnalysis,
            mapData: IData<IValue>,
            key: IValue
        ): IHeapValues<IValue>? {
            if (FactValuesKt.isNull(key) == true) {
            }

            return if (key.getType() == G.v().soot_IntType()) {
                val keyIndex: Int = FactValuesKt.getIntValue(key, false)
                (mapData as ListSpace).get(`$this_register`.getHf(), keyIndex)
            } else {
                null
            }
        }

        @JvmStatic
        fun register$lambda$2$lambda$1$lambda$0(
            `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
            `$this$resolve`: IOpCalculator,
            var2: IHeapValues.Builder,
            var3: Array<CompanionV>
        ): Boolean {
            `$this_evalCallAtCaller`.getOut()
                .setValueData(`$this_evalCallAtCaller`.getEnv(), var3[0].getValue() as IValue, OverrideModel.ArrayList, ListSpace(null, null, 3, null))
            return true
        }

        @JvmStatic
        fun CallerSiteCBImpl.EvalCall.register$lambda$2$lambda$1() {
            this.getHf()
                .resolveOp(this.getEnv(), this.getThis())
                .resolve(Companion::register$lambda$2$lambda$1$lambda$0)
        }

        @JvmStatic
        fun register$lambda$4$lambda$3(
            `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
            `$this$resolve`: IOpCalculator,
            var2: IHeapValues.Builder,
            var3: Array<CompanionV>
        ): Boolean {
            `$this_evalCallAtCaller`.getOut()
                .setValueData(`$this_evalCallAtCaller`.getEnv(), var3[0].getValue() as IValue, OverrideModel.ArrayList, ListSpace(null, null, 3, null))
            return true
        }

        @JvmStatic
        fun CallerSiteCBImpl.EvalCall.register$lambda$4() {
            val self: IHeapValues = this.getThis()
            if (!self.isSingle()) {
                this.setEvalAble(false)
            } else {
                this.getHf().resolveOp(this.getEnv(), self).resolve(Companion::register$lambda$4$lambda$3)
            }
        }

        @JvmStatic
        fun register$lambda$6$lambda$5(
            `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
            `$this_register`: ACheckCallAnalysis,
            `$this$get`: IOpCalculator,
            res: IHeapValues.Builder,
            var4: Array<CompanionV>
        ): Boolean {
            val self: CompanionV = var4[0]
            val key: CompanionV = var4[1]
            val var10000: IData<IValue>? = `$this_evalCallAtCaller`.getOut().getValueData(self.getValue() as IValue, OverrideModel.ArrayList)
            if (var10000 == null) {
                return false
            } else {
                val var10001: IHeapValues<IValue>? = register$mapGetModel(`$this_register`, var10000, key.getValue() as IValue)
                if (var10001 == null) {
                    return false
                } else {
                    res.add(var10001)
                    return true
                }
            }
        }

        @JvmStatic
        fun register$lambda$6(`$this_register`: ACheckCallAnalysis, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall) {
            val calculator: IOpCalculator = `$this$evalCallAtCaller`.getHf()
                .resolveOp(`$this$evalCallAtCaller`.getEnv(), `$this$evalCallAtCaller`.getThis(), `$this$evalCallAtCaller`.arg(0))
            calculator.resolve(Companion::register$lambda$6$lambda$5)
            if (!calculator.isFullySimplified()) {
                `$this$evalCallAtCaller`.setEvalAble(false)
            } else {
                `$this$evalCallAtCaller`.setReturn(calculator.getRes().build())
            }
        }

        @JvmStatic
        fun register$lambda$8$lambda$7(
            `$this_evalCall`: CalleeCBImpl.EvalCall,
            `$value`: IHeapValues,
            `$this$add`: IOpCalculator,
            res: IHeapValues.Builder,
            var4: Array<CompanionV>
        ): Boolean {
            val self: CompanionV = var4[0]
            val var8: IData<IValue>? = `$this_evalCall`.getOut().getValueData(var4[0].getValue() as IValue, OverrideModel.ArrayList)
            val var10000: ListSpace? = var8 as? ListSpace
            if (var10000 == null) {
                return false
            } else {
                val listBuilder: ListSpaceBuilder = var10000.builder()
                listBuilder.add(`$value`)
                `$this_evalCall`.getOut().setValueData(`$this_evalCall`.getEnv(), self.getValue() as IValue, OverrideModel.ArrayList, listBuilder.build())
                val var10001: AbstractHeapFactory = `$this_evalCall`.getHf()
                val var10002: HeapValuesEnv = `$this_evalCall`.getEnv()
                val var10003: AbstractHeapFactory = `$this_evalCall`.getHf()
                val var10004: IntConstant = IntConstant.v(1)
                val var9: Constant = var10004
                val var10005: BooleanType = G.v().soot_BooleanType()
                res.add(var10001.push(var10002, var10003.newConstVal(var9, var10005 as Type)).popHV())
                return true
            }
        }

        @JvmStatic
        fun CalleeCBImpl.EvalCall.register$lambda$8() {
            val self: IHeapValues = this.getThis()
            val value: IHeapValues = this.arg(0)
            val calculator: IOpCalculator = this.getHf().resolveOp(this.getEnv(), self)
            calculator.resolve(Companion::register$lambda$8$lambda$7)
            if (!calculator.isFullySimplified()) {
                this.setEvalAble(false)
            } else {
                val var5: BooleanType = G.v().soot_BooleanType()
                calculator.putSummaryIfNotConcrete(var5 as Type, "return")
                this.setReturn(calculator.getRes().build())
            }
        }

        @JvmStatic
        fun register$lambda$10$lambda$9(
            `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
            `$this$add`: IOpCalculator,
            res: IHeapValues.Builder,
            var3: Array<CompanionV>
        ): Boolean {
            val self: CompanionV = var3[0]
            val index: CompanionV = var3[1]
            val listBuilder: IData<IValue>? = `$this_evalCallAtCaller`.getOut().getValueData(self.getValue() as IValue, OverrideModel.ArrayList)
            val var10000: ListSpace? = listBuilder as? ListSpace
            if (var10000 == null) {
                return false
            } else {
                val indexConstant: Int = FactValuesKt.getIntValue(index.getValue() as IValue, true)
                val var10: ListSpaceBuilder = var10000.builder()
                val resValue: IHeapValues<IValue> = var10.remove(`$this_evalCallAtCaller`.getHf(), indexConstant)
                `$this_evalCallAtCaller`.getOut().setValueData(`$this_evalCallAtCaller`.getEnv(), self.getValue() as IValue, OverrideModel.ArrayList, var10.build())
                res.add(resValue)
                return true
            }
        }

        @JvmStatic
        fun CallerSiteCBImpl.EvalCall.register$lambda$10() {
            val self: IHeapValues = this.getThis()
            val index: IHeapValues = this.arg(0)
            if (!self.isSingle()) {
                this.setEvalAble(false)
            } else {
                val calculator: IOpCalculator = this.getHf().resolveOp(this.getEnv(), self, index)
                calculator.resolve(Companion::register$lambda$10$lambda$9)
                if (!calculator.isFullySimplified()) {
                    this.setEvalAble(false)
                } else {
                    calculator.putSummaryIfNotConcrete(this.getHf().getVg().getOBJECT_TYPE() as Type, "return")
                    this.setReturn(calculator.getRes().build())
                }
            }
        }

        @JvmStatic
        fun v(): WArrayList {
            return WArrayList()
        }
    }
}