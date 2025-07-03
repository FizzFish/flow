package cn.sast.dataflow.interprocedural.check.heapimpl

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap

@SourceDebugExtension(["SMAP\nImmutableCollections.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ImmutableCollections.kt\ncn/sast/dataflow/interprocedural/check/heapimpl/ImmutableElementSet\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,314:1\n1#2:315\n*E\n"])
public class ImmutableElementSet<E>(
    fields: PersistentMap<Any, IHeapValues<IValue>> = ExtensionsKt.persistentHashMapOf(),
    unreferenced: IHeapValues<IValue>? = null
) : ImmutableElementHashMap(fields, unreferenced) {
    public val element: Set<Any>
        get() = this.getMap().keySet()

    public override fun getName(): String {
        return "ImmHashSet"
    }

    public fun isEmpty(): Boolean {
        if (this.getMap().isEmpty()) {
            val unreferenced = this.getUnreferenced()
            if (unreferenced == null || unreferenced.isEmpty()) {
                return true
            }
        }
        return false
    }

    public fun containsAll(rhs: ImmutableElementSet<*>): Boolean {
        return this.getMap().keySet().containsAll(rhs.getMap().keySet())
    }

    public fun intersect(
        hf: AbstractHeapFactory<IValue>,
        env: HeapValuesEnv,
        rhs: ImmutableElementSet<Any>
    ): ImmutableElementSet<Any> {
        if (rhs.isEmpty()) {
            return rhs
        } else if (this.isEmpty()) {
            return this
        } else {
            val set = this.getMap().keySet().intersect(rhs.getMap().keySet())
            val r = ImmutableElementSetBuilder<Any>(null, null).builder()

            for (e in set) {
                val value = this.get(hf, e as? E)
                if (value != null) {
                    r.set(hf, env, e, value, true)
                }

                val rhsValue = rhs.get(hf, e)
                if (rhsValue != null) {
                    r.set(hf, env, e, rhsValue, true)
                }
            }

            return r.build()
        }
    }

    public fun plus(
        hf: AbstractHeapFactory<IValue>,
        env: HeapValuesEnv,
        rhs: ImmutableElementSet<Any>
    ): ImmutableElementSet<Any> {
        if (this.isEmpty()) {
            return rhs
        } else if (rhs.isEmpty()) {
            return this
        } else {
            val set = this.getMap().keySet() + rhs.getMap().keySet()
            val r = ImmutableElementSetBuilder<Any>(null, null).builder()

            for (e in set) {
                val value = this.get(hf, e as? E)
                if (value != null) {
                    r.set(hf, env, e, value, true)
                }

                val rhsValue = rhs.get(hf, e)
                if (rhsValue != null) {
                    r.set(hf, env, e, rhsValue, true)
                }
            }

            return r.build()
        }
    }

    public fun minus(
        hf: AbstractHeapFactory<IValue>,
        env: HeapValuesEnv,
        rhs: ImmutableElementSet<Any>
    ): ImmutableElementSet<Any> {
        if (this.isEmpty()) {
            return this
        } else if (rhs.isEmpty()) {
            return this
        } else {
            val r = this.builder()

            for (e in rhs.element) {
                val value = this.get(hf, e as? E)
                if (value != null && value.isSingle()) {
                    r.getMap().remove(e)
                }
            }

            return r.build()
        }
    }

    public override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) {
            return false
        }
        return other is ImmutableElementSet<*>
    }

    public override fun hashCode(): Int {
        return super.hashCode()
    }

    public fun builder(): ImmutableElementSetBuilder<Any> {
        val mapBuilder = this.getMap().builder()
        val unreferencedBuilder = this.getUnreferenced()?.builder()
        return ImmutableElementSetBuilder(mapBuilder, unreferencedBuilder)
    }

    constructor() : this(ExtensionsKt.persistentHashMapOf(), null)
}