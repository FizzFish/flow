package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentList.Builder

@SourceDebugExtension(["SMAP\nWArrayList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpaceBuilder\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,344:1\n1863#2,2:345\n1#3:347\n*S KotlinDebug\n*F\n+ 1 WArrayList.kt\ncn/sast/dataflow/interprocedural/override/lang/util/ListSpaceBuilder\n*L\n281#1:345,2\n*E\n"])
data class ListSpaceBuilder(
    val list: Builder<IHeapValues<IValue>>,
    private var unreferenced: IHeapValues.Builder<IValue>?
) : IData.Builder<IValue> {

    override fun union(hf: AbstractHeapFactory<IValue>, that: IData<IValue>) {
        if (that !is ListSpace) {
            throw IllegalArgumentException("Failed requirement.")
        } else if (this.unreferenced != null) {
            if (that.getUnreferenced() != null) {
                this.unreferenced?.add(that.getUnreferenced())
            } else {
                this.unreferenced?.add(that.getAllElement(hf))
            }
        } else if (that.getUnreferenced() != null) {
            this.unreferenced = that.getUnreferenced().builder()
            this.unreferenced?.add(this.getAllElement(hf))
            this.list.clear()
        } else if (this.list.size != that.getList().size) {
            val builder = this.getAllElement(hf).builder()
            builder.add(that.getAllElement(hf))
            this.unreferenced = builder
            this.list.clear()
        } else {
            var i = 0
            for (element in that.getList()) {
                this.list[i] = this.list[i].plus(element)
                i++
            }
        }
    }

    private fun getAllElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
        val res = hf.emptyBuilder()

        for (element in this.list.build()) {
            res.add(element)
        }

        this.unreferenced?.let {
            res.add(it.build())
        }

        return res.build()
    }

    override fun cloneAndReNewObjects(re: IReNew<IValue>) {
        var k = 0
        for (element in this.list.build()) {
            this.list[k] = element.cloneAndReNewObjects(re.context(ReferenceContext.KVPosition(k)))
            k++
        }

        this.unreferenced?.cloneAndReNewObjects(re.context(ReferenceContext.KVUnreferenced.INSTANCE))
    }

    override fun build(): IData<IValue> {
        val unreferenced = this.unreferenced?.takeUnless { it.isEmpty() }?.build()
        return ListSpace(this.list.build(), unreferenced)
    }

    fun add(value: IHeapValues<IValue>) {
        if (this.unreferenced != null) {
            this.unreferenced?.add(value)
        } else {
            this.list.add(value)
        }
    }

    fun clear(value: IHeapValues<IValue>) {
        this.unreferenced = null
        this.list.clear()
    }

    fun addAll(hf: AbstractHeapFactory<IValue>, b: ListSpace) {
        if (this.unreferenced == null && b.getUnreferenced() == null) {
            this.list.addAll(b.getList())
        } else {
            this.union(hf, b)
        }
    }

    fun remove(hf: AbstractHeapFactory<IValue>, index: Int?): IHeapValues<IValue> {
        return if (index == null) {
            if (this.unreferenced == null) {
                this.unreferenced = hf.emptyBuilder()
            }
            this.unreferenced?.add(this.getAllElement(hf))
            this.list.clear()
            this.unreferenced!!.build()
        } else if (this.unreferenced == null) {
            if (index in 0 until this.list.size) {
                this.list.removeAt(index)
            } else {
                this.getAllElement(hf)
            }
        } else {
            this.getAllElement(hf)
        }
    }

    fun component1(): Builder<IHeapValues<IValue>> = this.list

    private fun component2(): IHeapValues.Builder<IValue>? = this.unreferenced

    fun copy(
        list: Builder<IHeapValues<IValue>> = this.list,
        unreferenced: IHeapValues.Builder<IValue>? = this.unreferenced
    ): ListSpaceBuilder {
        return ListSpaceBuilder(list, unreferenced)
    }

    override fun toString(): String {
        return "ListSpaceBuilder(list=${this.list}, unreferenced=${this.unreferenced})"
    }

    override fun hashCode(): Int {
        return this.list.hashCode() * 31 + (this.unreferenced?.hashCode() ?: 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListSpaceBuilder) return false
        return this.list == other.list && this.unreferenced == other.unreferenced
    }
}