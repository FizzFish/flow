package cn.sast.dataflow.interprocedural.check

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapKV
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import soot.ArrayType
import soot.Type
import soot.jimple.Constant

@SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n1#2:613\n1863#3,2:614\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace\n*L\n225#1:614,2\n*E\n"])
public open class ArraySpace internal constructor(
    element: PersistentMap<Int, IHeapValues<IValue>>,
    unreferenced: IHeapValues<IValue>?,
    type: ArrayType,
    allSize: IHeapValues<IValue>,
    size: Int?,
    initializedValue: CompanionV<IValue>?
) : ArrayHeapKV(element, unreferenced, allSize, type, size, initializedValue) {
    public open fun builder(): ArraySpaceBuilder {
        val mapBuilder = this.getMap().builder()
        val unreferenced = this.getUnreferenced()
        return ArraySpaceBuilder(this, mapBuilder, unreferenced?.builder())
    }

    public override fun getElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
        val b = hf.emptyBuilder()
        val unreferenced = this.getUnreferenced()
        if (unreferenced != null) {
            b.add(unreferenced)
        }

        for (element in this.getMap().values) {
            b.add(element)
        }

        return b.build()
    }

    public override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
        val b = this.builder()
        b.cloneAndReNewObjects(re)
        return b.build()
    }

    public open fun getArray(hf: IHeapValuesFactory<IValue>): Array<IValue>? {
        val size = this.getSize() ?: return null
        
        val result = arrayOfNulls<IValue>(size)
        for (i in 0 until size) {
            val element = this.get(hf, i) ?: return null
            if (!element.isSingle()) return null
            result[i] = element.getSingle().getValue()
        }
        @Suppress("UNCHECKED_CAST")
        return result as Array<IValue>
    }

    public override fun getByteArray(hf: IHeapValuesFactory<IValue>): ByteArray? {
        val arr = getArray(hf) ?: return null
        
        val result = ByteArray(arr.size)
        for (i in arr.indices) {
            val value = arr[i] as? ConstVal ?: return null
            val byteValue = FactValuesKt.getByteValue(value, true) ?: return null
            result[i] = byteValue
        }
        return result
    }

    @SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,612:1\n13474#2,3:613\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace$Companion\n*L\n211#1:613,3\n*E\n"])
    public companion object {
        public fun v(
            hf: AbstractHeapFactory<IValue>,
            env: HeapValuesEnv,
            element: PersistentMap<Int, IHeapValues<IValue>>,
            unreferenced: IHeapValues<IValue>,
            type: ArrayType,
            allSize: IHeapValues<IValue>
        ): ArraySpace {
            val size = allSize.getMaxInt(true)
            val (v, t) = hf.getVg().zeroValue(type)
            val constant = v as Constant
            return ArraySpace(
                element, unreferenced, type, allSize, size, 
                hf.push(env, hf.newConstVal(constant, t as Type))
                    .markOfConstant(constant, "array init value")
                    .pop()
            )
        }

        public fun <T : Number> v(
            hf: AbstractHeapFactory<IValue>,
            env: HeapValuesEnv,
            value: CompanionV<IValue>,
            array: Array<T>,
            type: ArrayType,
            allSize: IHeapValues<IValue>
        ): ArraySpace {
            val element = ExtensionsKt.persistentHashMapOf<Int, IHeapValues<IValue>>().builder()
            
            array.forEachIndexed { index, item ->
                val (constant) = SootUtilsKt.constOf(item)
                element.put(
                    index,
                    hf.push(env, hf.newConstVal(constant as Constant, type.getElementType()))
                        .dataGetElementFromSequence(value)
                        .popHV()
                )
            }

            return ArraySpace(element.build(), hf.empty(), type, allSize, array.size, null)
        }
    }
}