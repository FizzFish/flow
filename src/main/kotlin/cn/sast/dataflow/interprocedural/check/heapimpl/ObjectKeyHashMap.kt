package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.HeapValues
import cn.sast.dataflow.interprocedural.analysis.HeapValuesBuilder
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.PersistentMap

public class ObjectKeyHashMap(
    keys: IHeapValues<IValue>,
    fields: PersistentMap<IValue, IHeapValues<IValue>>,
    unreferenced: IHeapValues<IValue>?
) : ImmutableElementHashMap(fields, unreferenced) {
    public val keys: IHeapValues<IValue>

    public val values: IHeapValues<IValue>
        get() {
            val builder = HeapValues(null, 1, null).builder()
            for (v in this.getMap().values as ImmutableCollection<IHeapValues<IValue>>) {
                builder.add(v)
            }
            return builder.build()
        }

    init {
        this.keys = keys
    }

    public override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
        if (that is ObjectKeyHashMap) {
            this.keys.diff(cmp, that.keys)
            this.getValues().diff(cmp, that.getValues())
        }
        super.diff(cmp, that)
    }

    public override fun computeHash(): Int {
        return 31 * 1 + super.computeHash()
    }

    public override fun hashCode(): Int {
        return super.hashCode()
    }

    public override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) {
            return false
        }
        return other is ObjectKeyHashMap && this.keys == other.keys
    }

    public override fun builder(): ImmutableElementHashMapBuilder<IValue, IValue> {
        return ObjectKeyHashMapBuilder(
            this.keys.builder(),
            this.getMap().builder(),
            this.getUnreferenced()?.builder()
        )
    }
}