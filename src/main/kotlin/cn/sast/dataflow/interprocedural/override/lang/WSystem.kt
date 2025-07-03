package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapBuilder
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.ArraySpace
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.reflect.KCallable
import kotlinx.collections.immutable.ExtensionsKt
import soot.ArrayType
import soot.G
import soot.IntType
import soot.NullType
import soot.PrimType
import soot.RefType
import soot.Type

public class WSystem : SummaryHandlePackage<IValue> {
    public val arrayType: ArrayType = ArrayType.v(G.v().soot_ByteType() as Type, 1)

    public override fun ACheckCallAnalysis.register() {
        this.evalCall(UtilsKt.getSootSignature(TODO("FIXME — unrepresentable INSTANCE") as KCallable<*>), WSystem::register$lambda$1)
        this.evalCall(UtilsKt.getSootSignature(TODO("FIXME — unrepresentable INSTANCE") as KCallable<*>), WSystem::register$lambda$4)
    }

    @JvmStatic
    fun register$lambda$1$lambda$0(
        evalCall: CalleeCBImpl.EvalCall,
        resolve: IOpCalculator,
        res: IHeapValues.Builder,
        var3: Array<CompanionV>
    ): Boolean {
        res.add(
            evalCall.hf
                .push(evalCall.env, evalCall.hf.toConstVal(var3[0].hashCode()))
                .markOfReturnValueOfMethod(evalCall)
                .pop()
        )
        return true
    }

    @JvmStatic
    fun CalleeCBImpl.EvalCall.register$lambda$1() {
        val unop: IOpCalculator = this.hf.resolveOp(this.env, this.arg(0))
        unop.resolve(WSystem::register$lambda$1$lambda$0)
        val var4: IntType = G.v().soot_IntType()
        unop.putSummaryIfNotConcrete(var4 as Type, "return")
        this.setReturn(unop.res.build())
    }

    @JvmStatic
    fun register$lambda$4$lambda$2(
        evalCall: CalleeCBImpl.EvalCall,
        append: Boolean,
        resolve: IOpCalculator,
        res: IHeapValues.Builder,
        var4: Array<CompanionV>
    ): Boolean {
        val src: CompanionV = var4[0]
        val srcPos: CompanionV = var4[1]
        val dest: CompanionV = var4[2]
        val destPos: CompanionV = var4[3]
        val length: CompanionV = var4[4]
        var var10000: IArrayHeapKV = evalCall.out.getArray(src.value as IValue)
        if (var10000 == null) {
            return false
        } else {
            val arrSrc: IArrayHeapKV = var10000
            var10000 = evalCall.out.getArray(dest.value as IValue)
            if (var10000 == null) {
                return false
            } else {
                val var20: Int = FactValuesKt.getIntValue(srcPos.value as IValue, true)
                if (var20 != null) {
                    val intSrcPos: Int = var20
                    val var21: Int = FactValuesKt.getIntValue(destPos.value as IValue, true)
                    if (var21 != null) {
                        val intDestPos: Int = var21
                        val var22: Int = FactValuesKt.getIntValue(length.value as IValue, true)
                        if (var22 != null) {
                            val intLength: Int = var22
                            if (intLength >= 20) {
                                return false
                            } else {
                                val b: IHeapKVData.Builder = var10000.builder()

                                for (i in 0 until intLength) {
                                    val var23: IHeapValues = arrSrc.get(evalCall.hf, i + intSrcPos)
                                    if (var23 == null) {
                                        return false
                                    }

                                    b.set(
                                        evalCall.hf,
                                        evalCall.env,
                                        i + intDestPos,
                                        evalCall.hf.push(evalCall.env, var23).dataElementCopyToSequenceElement(var23).pop(),
                                        append
                                    )
                                }

                                evalCall.out.setValueData(evalCall.env, dest.value as IValue, BuiltInModelT.Array, b.build())
                                return true
                            }
                        } else {
                            return false
                        }
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            }
        }
    }

    @JvmStatic
    fun CalleeCBImpl.EvalCall.register$lambda$4() {
        val srcP: IHeapValues = this.arg(0)
        val srcPosP: IHeapValues = this.arg(1)
        val destP: IHeapValues = this.arg(2)
        val destPosP: IHeapValues = this.arg(3)
        val lengthP: IHeapValues = this.arg(4)
        val op: IOpCalculator = this.hf.resolveOp(this.env, srcP, srcPosP, destP, destPosP, lengthP)
        val var22: Boolean = !srcP.isSingle() || !srcPosP.isSingle() || !destP.isSingle() || !destPosP.isSingle() || !lengthP.isSingle()
        val orig: IFact = this.out.build()
        op.resolve(WSystem::register$lambda$4$lambda$2)
        if (!op.isFullySimplified()) {
            val builder: IFact.Builder = orig.builder()
            val multiDst: Boolean = !destP.isSingle()

            for (dst in destP) {
                val cb: Type = (dst.value as IValue).type
                val var28: ArrayType? = cb as? ArrayType
                if (var28 != null) {
                    var var29: IArrayHeapKV
                    if (multiDst) {
                        var29 = builder.getArray(dst.value)
                        if (var29 == null) {
                            val var30: AbstractHeapFactory = this.hf
                            val var35: HeapValuesEnv = this.env
                            val var10002: AbstractHeapFactory = this.hf
                            val var10003: HeapValuesEnv = this.env
                            val var10004: IntType = G.v().soot_IntType()
                            var29 = ArraySpace.Companion.v(
                                this.hf,
                                this.env,
                                ExtensionsKt.persistentHashMapOf(),
                                this.hf.empty(),
                                var28,
                                var30.push(var35, var10002.newSummaryVal(var10003, var10004 as Type, "arraySize")).popHV()
                            )
                        }
                    } else {
                        val var31: AbstractHeapFactory = this.hf
                        val var36: HeapValuesEnv = this.env
                        val var39: AbstractHeapFactory = this.hf
                        val var42: HeapValuesEnv = this.env
                        val var45: IntType = G.v().soot_IntType()
                        var29 = ArraySpace.Companion.v(
                            this.hf,
                            this.env,
                            ExtensionsKt.persistentHashMapOf(),
                            this.hf.empty(),
                            var28,
                            var31.push(var36, var39.newSummaryVal(var42, var45 as Type, "arraySize")).popHV()
                        )
                    }

                    val var26: IHeapKVData.Builder = var29.builder()
                    val var32: ArrayHeapBuilder? = var26 as? ArrayHeapBuilder
                    if (var32 != null) {
                        val var24: ArrayHeapBuilder = var32
                        var32.clearAllIndex()

                        for (src in srcP) {
                            var arrSrc: IArrayHeapKV = builder.getArray(src.value)
                            if (arrSrc == null) {
                                val baseType: Type = (src.value as IValue).type
                                if (baseType !is PrimType && baseType !is RefType && baseType !is NullType) {
                                    continue
                                }

                                val var33: AbstractHeapFactory = this.hf
                                var var37: HeapValuesEnv = this.env
                                var var40: AbstractHeapFactory = this.hf
                                var var43: HeapValuesEnv = this.env
                                val var46: ArrayType = (src.value as IValue).type.makeArrayType()
                                val summary: IHeapValues = var33.push(var37, var40.newSummaryVal(var43, var46 as Type, "arraySize")).popHV()
                                val var34: AbstractHeapFactory = this.hf
                                var37 = this.env
                                var40 = this.hf
                                var43 = this.env
                                val var47: IntType = G.v().soot_IntType()
                                arrSrc = ArraySpace.Companion.v(
                                    this.hf,
                                    this.env,
                                    ExtensionsKt.persistentHashMapOf(),
                                    summary,
                                    this.hf.vg.OBJ_ARRAY_TYPE,
                                    var34.push(var37, var40.newSummaryVal(var43, var47 as Type, "summary")).popHV()
                                )
                                builder.setValueData(this.env, src.value as IValue, BuiltInModelT.Array, arrSrc)
                            }

                            var24.set(this.hf, this.env, null, arrSrc.getElement(this.hf), true)
                        }

                        builder.setValueData(this.env, dst.value as IValue, BuiltInModelT.Array, var24.build())
                    }
                }
            }

            this.setOut(builder)
        }
    }

    public companion object {
        public fun v(): WSystem {
            return WSystem()
        }
    }
}