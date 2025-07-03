package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.IHeapValues.Builder
import kotlinx.collections.immutable.PersistentMap

public open class ObjectKeyHashMapBuilder(
    public final val keys: Builder<IValue>,
    fields: kotlinx.collections.immutable.PersistentMap.Builder<IValue, IHeapValues<IValue>>,
    unreferenced: Builder<IValue>?
) : ImmutableElementHashMapBuilder(fields, unreferenced) {

    public open fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: IValue?, update: IHeapValues<IValue>?, append: Boolean) {
        throw IllegalStateException("key must be CompanionV".toString())
    }

    public fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: CompanionV<IValue>, update: IHeapValues<IValue>?, append: Boolean) {
        super.set(hf, env, key.getValue(), update, append)
        this.keys.add(key)
    }

    public fun set(hf: IHeapValuesFactory<IValue>, env: HeapValuesEnv, key: IHeapValues<IValue>, update: IHeapValues<IValue>?, append: Boolean) {
        for (k in key) {
            this.set(hf, env, k as CompanionV<IValue>, update, append)
        }
    }

    public override fun build(): ImmutableElementHashMap<IValue, IValue> {
        val keysBuilt = this.keys.build()
        val mapBuilt = this.getMap().build()
        val unreferencedBuilt = this.getUnreferenced()?.build()
        return ObjectKeyHashMap(keysBuilt, mapBuilt, unreferencedBuilt)
    }
}