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
import cn.sast.dataflow.interprocedural.check.ArraySpace
import cn.sast.dataflow.interprocedural.check.ArraySpaceBuilder
import cn.sast.dataflow.interprocedural.check.OverrideModel
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import soot.G
import soot.IntType
import soot.Type

@SourceDebugExtension(["SMAP\nWHashMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WHashMap.kt\ncn/sast/dataflow/interprocedural/override/lang/util/WHashMap\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,182:1\n1863#2,2:183\n*S KotlinDebug\n*F\n+ 1 WHashMap.kt\ncn/sast/dataflow/interprocedural/override/lang/util/WHashMap\n*L\n49#1:183,2\n*E\n"])
public class WHashMap : SummaryHandlePackage<IValue> {
    public override fun ACheckCallAnalysis.register() {
        val var20: Iterable<Any>
        for (element in var20) {
            this.evalCallAtCaller(element as String, WHashMap::register$lambda$2$lambda$1)
        }

        this.evalCallAtCaller("<java.util.HashMap: void clear()>", WHashMap::register$lambda$4)
        this.evalCallAtCaller("<java.util.HashMap: java.lang.Object get(java.lang.Object)>", WHashMap::register$lambda$6)
        this.evalCallAtCaller("<java.util.HashMap: java.lang.Object getOrDefault(java.lang.Object,java.lang.Object)>", WHashMap::register$lambda$8)
        this.evalCall("<java.util.HashMap: java.lang.Object put(java.lang.Object,java.lang.Object)>", WHashMap::register$lambda$10)
    }

    @JvmStatic
    fun `register$mapGetModel`(`$this_register`: ACheckCallAnalysis, mapData: IData<IValue>, key: IValue): IHeapValues<IValue>? {
        if (FactValuesKt.isNull(key) == true) {
            // Empty if block preserved
        }

        return if (key.getType() == `$this_register`.hf.vg.STRING_TYPE) {
            val var10000: String? = FactValuesKt.getStringValue(key, false)
            if (var10000 == null) {
                null
            } else {
                (mapData as ArraySpace).get(`$this_register`.hf, kotlin.math.abs(var10000.hashCode()))
            }
        } else {
            null
        }
    }

    @JvmStatic
    fun `register$lambda$2$lambda$1$lambda$0`(
        `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
        `$this$resolve`: IOpCalculator,
        var2: IHeapValues.Builder,
        var3: Array<CompanionV>
    ): Boolean {
        val self: CompanionV = var3[0]
        val var10000: AbstractHeapFactory = `$this_evalCallAtCaller`.hf
        val var10001: HeapValuesEnv = `$this_evalCallAtCaller`.env
        val var10002: AbstractHeapFactory = `$this_evalCallAtCaller`.hf
        val var10003: HeapValuesEnv = `$this_evalCallAtCaller`.env
        val var10004: IntType = G.v().soot_IntType()
        `$this_evalCallAtCaller`.out.setValueData(
            `$this_evalCallAtCaller`.env,
            self.value as IValue,
            OverrideModel.HashMap,
            ArraySpace.Companion.v(
                `$this_evalCallAtCaller`.hf,
                `$this_evalCallAtCaller`.env,
                ExtensionsKt.persistentHashMapOf(),
                `$this_evalCallAtCaller`.hf.empty(),
                `$this_evalCallAtCaller`.hf.vg.OBJ_ARRAY_TYPE,
                var10000.push(var10001, var10002.newSummaryVal(var10003, var10004 as Type, "mapSize")).popHV()
            )
        )
        return true
    }

    @JvmStatic
    fun CallerSiteCBImpl.EvalCall.`register$lambda$2$lambda$1`() {
        this.hf.resolveOp(this.env, this.arg(-1))
            .resolve(WHashMap::register$lambda$2$lambda$1$lambda$0)
    }

    @JvmStatic
    fun `register$lambda$4$lambda$3`(
        `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
        `$this$resolve`: IOpCalculator,
        var2: IHeapValues.Builder,
        var3: Array<CompanionV>
    ): Boolean {
        val self: CompanionV = var3[0]
        val var10000: AbstractHeapFactory = `$this_evalCallAtCaller`.hf
        val var10001: HeapValuesEnv = `$this_evalCallAtCaller`.env
        val var10002: AbstractHeapFactory = `$this_evalCallAtCaller`.hf
        val var10003: HeapValuesEnv = `$this_evalCallAtCaller`.env
        val var10004: IntType = G.v().soot_IntType()
        `$this_evalCallAtCaller`.out.setValueData(
            `$this_evalCallAtCaller`.env,
            self.value as IValue,
            OverrideModel.HashMap,
            ArraySpace.Companion.v(
                `$this_evalCallAtCaller`.hf,
                `$this_evalCallAtCaller`.env,
                ExtensionsKt.persistentHashMapOf(),
                `$this_evalCallAtCaller`.hf.empty(),
                `$this_evalCallAtCaller`.hf.vg.OBJ_ARRAY_TYPE,
                var10000.push(var10001, var10002.newSummaryVal(var10003, var10004 as Type, "mapSize")).popHV()
            )
        )
        return true
    }

    @JvmStatic
    fun CallerSiteCBImpl.EvalCall.`register$lambda$4`() {
        val self: IHeapValues = this.this
        if (!self.isSingle()) {
            this.evalAble = false
        } else {
            this.hf.resolveOp(this.env, self).resolve(WHashMap::register$lambda$4$lambda$3)
        }
    }

    @JvmStatic
    fun `register$lambda$6$lambda$5`(
        `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
        `$this_register`: ACheckCallAnalysis,
        `$this$get`: IOpCalculator,
        res: IHeapValues.Builder,
        var4: Array<CompanionV>
    ): Boolean {
        val map: CompanionV = var4[0]
        val key: CompanionV = var4[1]
        val var10000: IData<IValue>? = `$this_evalCallAtCaller`.out.getValueData(map.value as IValue, OverrideModel.HashMap)
        return if (var10000 == null) {
            false
        } else {
            val var10001: IHeapValues<IValue>? = `register$mapGetModel`(`$this_register`, var10000, key.value as IValue)
            if (var10001 == null) {
                false
            } else {
                res.add(var10001)
                true
            }
        }
    }

    @JvmStatic
    fun `register$lambda$6`(`$this_register`: ACheckCallAnalysis, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall) {
        val map: IHeapValues = `$this$evalCallAtCaller`.this
        val calculator: IOpCalculator = `$this$evalCallAtCaller`.hf.resolveOp(`$this$evalCallAtCaller`.env, map, `$this$evalCallAtCaller`.arg(0))
        if (!map.isSingle()) {
            `$this$evalCallAtCaller`.evalAble = false
        } else {
            calculator.resolve(WHashMap::register$lambda$6$lambda$5)
            if (calculator.isFullySimplified() && !calculator.res.isEmpty()) {
                `$this$evalCallAtCaller`.returnValue = calculator.res.build()
            } else {
                `$this$evalCallAtCaller`.evalAble = false
            }
        }
    }

    @JvmStatic
    fun `register$lambda$8$lambda$7`(
        `$this_evalCallAtCaller`: CallerSiteCBImpl.EvalCall,
        `$this_register`: ACheckCallAnalysis,
        `$this$get`: IOpCalculator,
        res: IHeapValues.Builder,
        var4: Array<CompanionV>
    ): Boolean {
        val map: CompanionV = var4[0]
        val key: CompanionV = var4[1]
        val defaultValue: CompanionV = var4[2]
        val var10000: IData<IValue>? = `$this_evalCallAtCaller`.out.getValueData(map.value as IValue, OverrideModel.HashMap)
        if (var10000 == null) {
            return false
        } else {
            var var10001: IHeapValues<IValue>? = `register$mapGetModel`(`$this_register`, var10000, key.value as IValue)
            if (var10001 == null) {
                var10001 = `$this_evalCallAtCaller`.hf.single(defaultValue)
            }

            res.add(var10001)
            return true
        }
    }

    @JvmStatic
    fun `register$lambda$8`(`$this_register`: ACheckCallAnalysis, `$this$evalCallAtCaller`: CallerSiteCBImpl.EvalCall) {
        val map: IHeapValues = `$this$evalCallAtCaller`.this
        if (!map.isSingle()) {
            `$this$evalCallAtCaller`.evalAble = false
        } else {
            val calculator: IOpCalculator = `$this$evalCallAtCaller`.hf
                .resolveOp(`$this$evalCallAtCaller`.env, map, `$this$evalCallAtCaller`.arg(0), `$this$evalCallAtCaller`.arg(1))
            calculator.resolve(WHashMap::register$lambda$8$lambda$7)
            if (calculator.isFullySimplified() && !calculator.res.isEmpty()) {
                `$this$evalCallAtCaller`.returnValue = calculator.res.build()
            } else {
                `$this$evalCallAtCaller`.evalAble = false
            }
        }
    }

    @JvmStatic
    fun `register$lambda$10$lambda$9`(
        `$this_evalCall`: CalleeCBImpl.EvalCall,
        `$value`: IHeapValues<IValue>,
        `$this$put`: IOpCalculator,
        res: IHeapValues.Builder,
        var4: Array<CompanionV>
    ): Boolean {
        val map: CompanionV = var4[0]
        val key: CompanionV = var4[1]
        val var10000: IData<IValue>? = `$this_evalCall`.out.getValueData(map.value as IValue, OverrideModel.HashMap)
        if (var10000 == null) {
            return false
        } else {
            val builder: ArraySpaceBuilder = (var10000 as ArraySpace).builder()
            if ((key.value as IValue).type == `$this_evalCall`.hf.vg.STRING_TYPE) {
                val keyStr: String? = FactValuesKt.getStringValue(key.value as IValue, false)
                if (keyStr == null) {
                    builder.set(`$this_evalCall`.hf, `$this_evalCall`.env, null, `$value`, true)
                } else {
                    builder.set(`$this_evalCall`.hf, `$this_evalCall`.env, kotlin.math.abs(keyStr.hashCode()), `$value`, true)
                }

                `$this_evalCall`.out.setValueData(`$this_evalCall`.env, map.value as IValue, OverrideModel.HashMap, builder.build())
                res.add((var10000 as ArraySpace).getElement(`$this_evalCall`.hf))
                return true
            } else {
                builder.set(`$this_evalCall`.hf, `$this_evalCall`.env, null, `$value`, true)
                return true
            }
        }
    }

    @JvmStatic
    fun CalleeCBImpl.EvalCall.`register$lambda$10`() {
        val map: IHeapValues = this.this
        if (!map.isSingle()) {
            this.evalAble = false
        } else {
            val key: IHeapValues = this.arg(0)
            val value: IHeapValues = this.arg(1)
            val calculator: IOpCalculator = this.hf.resolveOp(this.env, map, key)
            calculator.resolve(WHashMap::register$lambda$10$lambda$9)
            if (!calculator.isFullySimplified()) {
                this.evalAble = false
            } else {
                this.returnValue = calculator.res.build()
            }
        }
    }

    public companion object {
        public fun v(): WHashMap {
            return WHashMap()
        }
    }
}