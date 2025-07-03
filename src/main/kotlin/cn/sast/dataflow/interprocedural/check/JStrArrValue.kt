package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.Type
import soot.Unit
import soot.jimple.Constant
import soot.jimple.IntConstant

@SourceDebugExtension(["SMAP\nHeapFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/JStrArrValue\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1430:1\n13409#2,2:1431\n13409#2,2:1433\n*S KotlinDebug\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/JStrArrValue\n*L\n92#1:1431,2\n164#1:1433,2\n*E\n"])
class JStrArrValue(
    val node: Unit,
    hf: AbstractHeapFactory<IValue>,
    val byteArray: ByteArray
) : IArrayHeapKV<Int, IValue> {
    val type: ArrayType
    private val arrayLength: IHeapValues<IValue>
    private val byteArrayConstVal: Array<CompanionV<IValue>>

    init {
        type = hf.getVg().getBYTE_ARRAY_TYPE()
        val it: IntConstant = IntConstant.v(byteArray.size)
        val var10001: HeapValuesEnv = hf.env(node)
        arrayLength = JOperatorV.DefaultImpls.markOfConstant$default(
            hf.push(var10001, hf.newConstVal(it as Constant, type as Type)), it as Constant, null, 2, null
        )
            .popHV()
        var var11 = 0
        val var12 = byteArray.size

        val var13 = arrayOfNulls<CompanionV<IValue>>(byteArray.size)
        for (i in byteArray.indices) {
            val v = IntConstant.v(byteArray[i])
            val var10003: Constant = v as Constant
            val var10004: ByteType = G.v().soot_ByteType()
            var13[i] = JOperatorV.DefaultImpls.markOfConstant$default(
                hf.push(hf.env(node), hf.newConstVal(var10003, var10004 as Type) as IValue), v as Constant, null, 2, null
            )
                .pop()
        }

        @Suppress("UNCHECKED_CAST")
        byteArrayConstVal = var13 as Array<CompanionV<IValue>>
    }

    fun get(hf: IHeapValuesFactory<IValue>, key: Int?): IHeapValues<IValue>? {
        if (key != null) {
            return if (key >= 0 && key < byteArray.size) hf.single(byteArrayConstVal[key]) else null
        } else {
            val b = hf.emptyBuilder()
            for (element in byteArrayConstVal) {
                b.add(element)
            }
            return b.build()
        }
    }

    override fun toString(): String {
        return "ImByteArray_${String(byteArray, Charsets.UTF_8)}"
    }

    override fun builder(): IHeapKVData.Builder<Int, IValue> {
        return object : IHeapKVData.Builder<Int, IValue>(this) {
            init {
                this@builder.this$0 = this@JStrArrValue
            }

            override fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: Int, update: IHeapValues<IValue>, append: Boolean) {
                Companion.getLogger().error { "$env ${this@JStrArrValue.javaClass.simpleName} is immutable!!!" }
            }

            override fun union(hf: AbstractHeapFactory<IValue>, that: IData<IValue>) {
                Companion.getLogger().error { "${this@JStrArrValue.javaClass.simpleName} is immutable!!! has no union" }
            }

            override fun cloneAndReNewObjects(re: IReNew<IValue>) {
            }

            override fun build(): IData<IValue> {
                return this@JStrArrValue
            }
        }
    }

    override fun reference(res: MutableCollection<IValue>) {
    }

    override fun computeHash(): Int {
        return 1138 + Arrays.hashCode(byteArray)
    }

    override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
    }

    override fun hashCode(): Int {
        return computeHash()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is JStrArrValue) {
            return false
        } else {
            return hashCode() == other.hashCode() && Arrays.equals(byteArray, other.byteArray)
        }
    }

    override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
        return this
    }

    override fun getArrayLength(): IHeapValues<IValue> {
        return arrayLength
    }

    override fun getElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
        val b = hf.emptyBuilder()
        for (element in byteArrayConstVal) {
            b.add(element)
        }
        return b.build()
    }

    fun getArray(hf: IHeapValuesFactory<IValue>): Array<IValue> {
        val result = arrayOfNulls<IValue>(byteArrayConstVal.size)
        for (i in byteArrayConstVal.indices) {
            result[i] = byteArrayConstVal[i].getValue()
        }
        @Suppress("UNCHECKED_CAST")
        return result as Array<IValue>
    }

    override fun getByteArray(hf: IHeapValuesFactory<IValue>): ByteArray {
        return byteArray
    }

    companion object {
        lateinit var logger: KLogger
    }
}