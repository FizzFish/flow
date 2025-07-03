package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentList

@SourceDebugExtension(["SMAP\nWArrayList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpace\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,344:1\n1#2:345\n1863#3,2:346\n*S KotlinDebug\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpace\n*L\n219#1:346,2\n*E\n"])
public data class ListSpace(
    public val list: PersistentList<IHeapValues<IValue>> = ExtensionsKt.persistentListOf(),
    public val unreferenced: IHeapValues<IValue>? = null
) : IData<IValue> {
    private var hashCode: Int? = null

    init {
        if (this.unreferenced != null && !this.unreferenced.isNotEmpty()) {
            throw IllegalArgumentException("Failed requirement.")
        }
    }

    public override fun reference(res: MutableCollection<IValue>) {
        for (e in this.list) {
            e.reference(res)
        }
    }

    public open fun builder(): ListSpaceBuilder {
        return ListSpaceBuilder(this.list.builder(), this.unreferenced?.builder())
    }

    public override fun computeHash(): Int {
        return 31 * (31 * 1 + this.list.hashCode()) + (this.unreferenced?.hashCode() ?: 0) + 1231
    }

    public override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
        if (this != that) {
            if (that is ListSpace) {
                var index = 0
                for (element in this.list) {
                    if (index >= that.list.size) {
                        break
                    }
                    element.diff(cmp, that.list[index])
                    index++
                }

                if (that.unreferenced != null && this.unreferenced != null) {
                    this.unreferenced.diff(cmp, that.unreferenced)
                }
            }
        }
    }

    public override fun hashCode(): Int {
        var hash = this.hashCode
        if (hash == null) {
            hash = computeHash()
            this.hashCode = hash
        }
        return hash
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListSpace) return false
        if (hashCode() != other.hashCode()) return false
        return list == other.list && unreferenced == other.unreferenced
    }

    public fun getAllElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
        val res = hf.emptyBuilder()
        for (element in list) {
            res.add(element)
        }
        unreferenced?.let { res.add(it) }
        return res.build()
    }

    public operator fun get(hf: AbstractHeapFactory<IValue>, index: Int?): IHeapValues<IValue>? {
        return if (index != null && unreferenced == null) {
            if (index in 0 until list.size) list[index] else null
        } else {
            getAllElement(hf)
        }
    }

    public override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
        val b = builder()
        b.cloneAndReNewObjects(re)
        return b.build()
    }

    public operator fun component1(): PersistentList<IHeapValues<IValue>> = list
    public operator fun component2(): IHeapValues<IValue>? = unreferenced

    public fun copy(
        list: PersistentList<IHeapValues<IValue>> = this.list,
        unreferenced: IHeapValues<IValue>? = this.unreferenced
    ): ListSpace = ListSpace(list, unreferenced)

    public override fun toString(): String = "ListSpace(list=$list, unreferenced=$unreferenced)"
}